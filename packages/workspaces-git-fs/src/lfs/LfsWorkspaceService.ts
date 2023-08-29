/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { Minimatch } from "minimatch";
import { join, relative } from "path";
import { encoder } from "../encoderdecoder/EncoderDecoder";
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { WorkspaceFile } from "../context/WorkspacesContext";
import { LfsStorageFile, LfsStorageService } from "./LfsStorageService";
import { CreateDescriptorArgs, LfsWorkspaceDescriptorService } from "./LfsWorkspaceDescriptorService";
import { buildUniqueWorkspaceBroadcastChannelName, buildWorkspacesBroadcastChannelName } from "./LfsWorkspaceEvents";
import { WorkspaceBroadcastEvents } from "../worker/api/WorkspaceBroadcastEvents";
import { WorkspacesBroadcastEvents } from "../worker/api/WorkspacesBroadcastEvents";

interface BroadcastArgs {
  broadcast: boolean;
}

const broadcastChannel__postMessage = (
  channel: string,
  message: WorkspacesBroadcastEvents | WorkspaceBroadcastEvents
) => {
  new BroadcastChannel(channel).postMessage(message);
};

export class LfsWorkspaceService {
  private readonly WORKSPACES_BROADCAST_CHANNEL_NAME = buildWorkspacesBroadcastChannelName(this.args.eventNamePrefix);

  public constructor(
    private readonly args: {
      storageService: LfsStorageService;
      descriptorService: LfsWorkspaceDescriptorService;
      fsMountPoint: string;
      eventNamePrefix: string;
    }
  ) {}

  public async create(args: {
    storeFiles: (descriptor: WorkspaceDescriptor) => Promise<WorkspaceFile[]>;
    broadcastArgs: BroadcastArgs;
    descriptorArgs: CreateDescriptorArgs;
  }) {
    const descriptor = await this.args.descriptorService.create(args.descriptorArgs);

    try {
      const files = await args.storeFiles(descriptor);

      if (args.broadcastArgs.broadcast) {
        broadcastChannel__postMessage(this.WORKSPACES_BROADCAST_CHANNEL_NAME, {
          type: "WSS_ADD_WORKSPACE",
          workspaceId: descriptor.workspaceId,
        });

        broadcastChannel__postMessage(
          buildUniqueWorkspaceBroadcastChannelName({
            prefix: this.args.eventNamePrefix,
            workspaceId: descriptor.workspaceId,
          }),
          { type: "WS_ADD", workspaceId: descriptor.workspaceId }
        );
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

  public async delete(args: { workspaceId: string; broadcastArgs: BroadcastArgs }): Promise<void> {
    await this.args.descriptorService.delete(args.workspaceId);
    indexedDB.deleteDatabase(`${this.args.fsMountPoint}${args.workspaceId}`);

    if (args.broadcastArgs.broadcast) {
      broadcastChannel__postMessage(this.WORKSPACES_BROADCAST_CHANNEL_NAME, {
        type: "WSS_DELETE_WORKSPACE",
        workspaceId: args.workspaceId,
      });

      broadcastChannel__postMessage(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.eventNamePrefix,
          workspaceId: args.workspaceId,
        }),
        { type: "WS_DELETE", workspaceId: args.workspaceId }
      );
    }
  }

  public async rename(args: { workspaceId: string; newName: string; broadcastArgs: BroadcastArgs }): Promise<void> {
    await this.args.descriptorService.rename(args.workspaceId, args.newName);
    await this.args.descriptorService.bumpLastUpdatedDate(args.workspaceId);

    if (args.broadcastArgs.broadcast) {
      broadcastChannel__postMessage(this.WORKSPACES_BROADCAST_CHANNEL_NAME, {
        type: "WSS_RENAME_WORKSPACE",
        workspaceId: args.workspaceId,
      });

      broadcastChannel__postMessage(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.eventNamePrefix,
          workspaceId: args.workspaceId,
        }),
        { type: "WS_RENAME", workspaceId: args.workspaceId }
      );
    }
  }

  public async createOrOverwriteFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    broadcastArgs: BroadcastArgs;
  }): Promise<void> {
    await this.args.storageService.createOrOverwriteFile(args.fs, this.toStorageFile(args.file));
    await this.args.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);

    if (args.broadcastArgs.broadcast) {
      broadcastChannel__postMessage(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.eventNamePrefix,
          workspaceId: args.file.workspaceId,
        }),
        {
          type: "WS_ADD_FILE",
          relativePath: args.file.relativePath,
        }
      );
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
    broadcastArgs: BroadcastArgs;
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
      broadcastChannel__postMessage(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.eventNamePrefix,
          workspaceId: args.file.workspaceId,
        }),
        {
          type: "WS_UPDATE_FILE",
          relativePath: args.file.relativePath,
        }
      );
    }
  }

  public async deleteFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    broadcastArgs: BroadcastArgs;
  }): Promise<void> {
    await this.args.storageService.deleteFile(args.fs, this.toStorageFile(args.file).path);
    await this.args.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);

    if (args.broadcastArgs.broadcast) {
      broadcastChannel__postMessage(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.eventNamePrefix,
          workspaceId: args.file.workspaceId,
        }),
        {
          type: "WS_DELETE_FILE",
          relativePath: args.file.relativePath,
        }
      );
    }
  }

  public async renameFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    newFileNameWithoutExtension: string;
    broadcastArgs: BroadcastArgs;
  }): Promise<WorkspaceFile> {
    const renamedStorageFile = await this.args.storageService.renameFile(
      args.fs,
      this.toStorageFile(args.file),
      args.newFileNameWithoutExtension
    );
    const renamedWorkspaceFile = this.toWorkspaceFile(args.file.workspaceId, renamedStorageFile);
    await this.args.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);

    if (args.broadcastArgs.broadcast) {
      broadcastChannel__postMessage(
        buildUniqueWorkspaceBroadcastChannelName({
          prefix: this.args.eventNamePrefix,
          workspaceId: args.file.workspaceId,
        }),
        {
          type: "WS_RENAME_FILE",
          oldRelativePath: args.file.relativePath,
          newRelativePath: renamedWorkspaceFile.relativePath,
        }
      );
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
