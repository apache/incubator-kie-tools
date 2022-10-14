/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { Minimatch } from "minimatch";
import { join, relative } from "path";
import { encoder } from "../encoderdecoder/EncoderDecoder";
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { WorkspaceEvents } from "../worker/api/WorkspaceEvents";
import { WorkspacesEvents } from "../worker/api/WorkspacesEvents";
import { WorkspaceFile } from "../WorkspacesContext";
import { LfsStorageFile, LfsStorageService } from "./LfsStorageService";
import { LfsWorkspaceDescriptorService } from "./LfsWorkspaceDescriptorService";
import { buildUniqueWorkspaceBroadcastChannelName, buildWorkspacesBroadcastChannelName } from "./LfsWorkspaceEvents";

export class LfsWorkspaceService {
  private readonly WORKSPACES_BROADCAST_CHANNEL_NAME = buildWorkspacesBroadcastChannelName(
    this.args.broadcastChannelPrefix
  );

  public constructor(
    private readonly args: {
      storageService: LfsStorageService;
      descriptorService: LfsWorkspaceDescriptorService;
      fsMountPoint: string;
      broadcastChannelPrefix: string;
    }
  ) {}

  public async create(args: {
    storeFiles: (descriptor: WorkspaceDescriptor) => Promise<WorkspaceFile[]>;
    broadcastArgs: { broadcast: boolean };
    workspaceDescriptor: WorkspaceDescriptor;
  }) {
    const descriptor = await this.args.descriptorService.create({ workspaceDescriptor: args.workspaceDescriptor });

    try {
      const files = await args.storeFiles(descriptor);

      if (args.broadcastArgs.broadcast) {
        new BroadcastChannel(this.WORKSPACES_BROADCAST_CHANNEL_NAME).postMessage({
          type: "ADD_WORKSPACE",
          workspaceId: descriptor.workspaceId,
        } as WorkspacesEvents);

        new BroadcastChannel(
          buildUniqueWorkspaceBroadcastChannelName({
            prefix: this.args.broadcastChannelPrefix,
            workspaceId: descriptor.workspaceId,
          })
        ).postMessage({ type: "ADD", workspaceId: descriptor.workspaceId } as WorkspaceEvents);
      }

      return { descriptor, files };
    } catch (e) {
      await this.args.descriptorService.delete(descriptor.workspaceId);
      throw e;
    }
  }

  public async getFilesWithLazyContent(args: {
    fs: KieSandboxFs;
    workspaceId: string;
    globPattern?: string;
  }): Promise<WorkspaceFile[]> {
    const matcher = args.globPattern ? new Minimatch(args.globPattern, { dot: true }) : undefined;
    const gitDirPath = this.getAbsolutePath({ workspaceId: args.workspaceId, relativePath: ".git" });

    return this.args.storageService.walk({
      fs: args.fs,
      startFromDirPath: this.getAbsolutePath({ workspaceId: args.workspaceId }),
      shouldExcludeDir: (dirPath) => dirPath === gitDirPath,
      onVisit: async ({ absolutePath, relativePath }) => {
        const workspaceFile = new WorkspaceFile({
          workspaceId: args.workspaceId,
          relativePath,
          getFileContents: () =>
            this.args.storageService.getFile(args.fs, absolutePath).then((f) => f!.getFileContents()),
        });

        if (matcher && !matcher.match(workspaceFile.name)) {
          return undefined;
        }

        return workspaceFile;
      },
    });
  }

  public async delete(args: { workspaceId: string; broadcastArgs: { broadcast: boolean } }): Promise<void> {
    await this.args.descriptorService.delete(args.workspaceId);
    indexedDB.deleteDatabase(`${this.args.fsMountPoint}${args.workspaceId}`);

    if (args.broadcastArgs.broadcast) {
      new BroadcastChannel(this.WORKSPACES_BROADCAST_CHANNEL_NAME).postMessage({
        type: "DELETE_WORKSPACE",
        workspaceId: args.workspaceId,
      } as WorkspacesEvents);

      new BroadcastChannel(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.broadcastChannelPrefix,
          workspaceId: args.workspaceId,
        })
      ).postMessage({ type: "DELETE", workspaceId: args.workspaceId } as WorkspaceEvents);
    }
  }

  public async rename(args: {
    workspaceId: string;
    newName: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<void> {
    await this.args.descriptorService.rename(args.workspaceId, args.newName);
    await this.args.descriptorService.bumpLastUpdatedDate(args.workspaceId);

    if (args.broadcastArgs.broadcast) {
      new BroadcastChannel(this.WORKSPACES_BROADCAST_CHANNEL_NAME).postMessage({
        type: "RENAME_WORKSPACE",
        workspaceId: args.workspaceId,
      } as WorkspacesEvents);

      new BroadcastChannel(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.broadcastChannelPrefix,
          workspaceId: args.workspaceId,
        })
      ).postMessage({ type: "RENAME", workspaceId: args.workspaceId } as WorkspaceEvents);
    }
  }

  public async createOrOverwriteFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    broadcastArgs: { broadcast: boolean };
  }): Promise<void> {
    await this.args.storageService.createOrOverwriteFile(args.fs, this.toStorageFile(args.file));
    await this.args.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);

    if (args.broadcastArgs.broadcast) {
      new BroadcastChannel(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.broadcastChannelPrefix,
          workspaceId: args.file.workspaceId,
        })
      ).postMessage({
        type: "ADD_FILE",
        relativePath: args.file.relativePath,
      } as WorkspaceEvents);
    }
  }

  public async getFile(args: {
    fs: KieSandboxFs;
    workspaceId: string;
    relativePath: string;
  }): Promise<WorkspaceFile | undefined> {
    const absolutePath = this.getAbsolutePath(args);
    const storageFile = await this.args.storageService.getFile(args.fs, absolutePath);
    if (!storageFile) {
      return;
    }
    return this.toWorkspaceFile(args.workspaceId, storageFile);
  }

  public async updateFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    getNewContents: () => Promise<string>;
    broadcastArgs: { broadcast: boolean };
  }): Promise<void> {
    await this.args.storageService.updateFile(
      args.fs,
      this.toStorageFile(
        new WorkspaceFile({
          relativePath: args.file.relativePath,
          workspaceId: args.file.workspaceId,
          getFileContents: () => args.getNewContents().then((c) => encoder.encode(c)),
        })
      )
    );
    await this.args.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);

    if (args.broadcastArgs.broadcast) {
      new BroadcastChannel(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.broadcastChannelPrefix,
          workspaceId: args.file.workspaceId,
        })
      ).postMessage({
        type: "UPDATE_FILE",
        relativePath: args.file.relativePath,
      } as WorkspaceEvents);
    }
  }

  public async deleteFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    broadcastArgs: { broadcast: boolean };
  }): Promise<void> {
    await this.args.storageService.deleteFile(args.fs, this.toStorageFile(args.file).path);
    await this.args.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);

    if (args.broadcastArgs.broadcast) {
      new BroadcastChannel(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.broadcastChannelPrefix,
          workspaceId: args.file.workspaceId,
        })
      ).postMessage({
        type: "DELETE_FILE",
        relativePath: args.file.relativePath,
      } as WorkspaceEvents);
    }
  }

  public async renameFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    newFileNameWithoutExtension: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<WorkspaceFile> {
    const renamedStorageFile = await this.args.storageService.renameFile(
      args.fs,
      this.toStorageFile(args.file),
      args.newFileNameWithoutExtension
    );
    const renamedWorkspaceFile = this.toWorkspaceFile(args.file.workspaceId, renamedStorageFile);
    await this.args.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);

    if (args.broadcastArgs.broadcast) {
      new BroadcastChannel(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.broadcastChannelPrefix,
          workspaceId: args.file.workspaceId,
        })
      ).postMessage({
        type: "RENAME_FILE",
        oldRelativePath: args.file.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceEvents);
    }

    return renamedWorkspaceFile;
  }

  public async existsFile(args: { fs: KieSandboxFs; workspaceId: string; relativePath: string }): Promise<boolean> {
    return this.args.storageService.exists(args.fs, this.getAbsolutePath(args));
  }

  private getAbsolutePath(args: { workspaceId: string; relativePath?: string }) {
    return join("/", args.relativePath ?? "");
  }

  private toWorkspaceFile(workspaceId: string, storageFile: LfsStorageFile): WorkspaceFile {
    return new WorkspaceFile({
      workspaceId,
      getFileContents: storageFile.getFileContents,
      relativePath: relative(this.getAbsolutePath({ workspaceId }), storageFile.path),
    });
  }

  private toStorageFile(file: WorkspaceFile): LfsStorageFile {
    return new LfsStorageFile({
      path: this.getAbsolutePath({ workspaceId: file.workspaceId, relativePath: file.relativePath }),
      getFileContents: file.getFileContents,
    });
  }
}
