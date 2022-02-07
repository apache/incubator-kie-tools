/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { FsCache } from "./FsCache";
import { encoder, WorkspaceFile } from "../contexts";
import { StorageFile, StorageService } from "./StorageService";
import { WorkspaceFileEvents } from "../hooks/WorkspaceFileHooks";
import { join } from "path";
import { WorkspaceDmnRunnerEvents } from "../hooks/WorkspaceDmnRunnerInput";

export class WorkspaceDmnRunnerInputsService {
  constructor(private readonly storageService: StorageService, private readonly fsCache = new FsCache()) {}

  public getWorkspaceDmnRunnerDataFs(workspaceId: string) {
    return this.fsCache.getOrCreateFs(this.getDmnRunnerDataStoreName(workspaceId));
  }

  public async getDmnRunnerData(workspaceFile: WorkspaceFile) {
    return this.storageService.getFile(
      await this.getWorkspaceDmnRunnerDataFs(workspaceFile.workspaceId),
      `/${workspaceFile.relativePath}`
    );
  }

  public async deleteDmnRunnerData(workspaceFile: WorkspaceFile) {
    const dmnRunnerDataFile = await this.getDmnRunnerData(workspaceFile);
    if (!dmnRunnerDataFile) {
      console.debug(
        `Can't delete DMN Runner data, because it doesn't exist for file '${workspaceFile.relativePath}' on Workspace '${workspaceFile.workspaceId}'`
      );
      return;
    }

    await this.storageService.createOrOverwriteFile(
      await this.getWorkspaceDmnRunnerDataFs(workspaceFile.workspaceId),
      new StorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(JSON.stringify([{}]))),
        path: `/${workspaceFile.relativePath}`,
      })
    );
  }

  public async createOrOverwriteDmnRunnerData(workspaceFile: WorkspaceFile, dmnRunnerData: string) {
    await this.storageService.createOrOverwriteFile(
      this.getWorkspaceDmnRunnerDataFs(workspaceFile.workspaceId),
      new StorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(dmnRunnerData)),
        path: `/${workspaceFile.relativePath}`,
      })
    );
    const broadcastChannel = new BroadcastChannel(
      this.getUniqueFileIdentifier({
        workspaceId: workspaceFile.workspaceId,
        relativePath: workspaceFile.relativePath,
      })
    );
    broadcastChannel.postMessage({
      type: "ADD",
      relativePath: workspaceFile.relativePath,
    } as WorkspaceFileEvents);
  }

  public async updateDmnRunnerInputs(
    workspaceFile: WorkspaceFile,
    dmnRunnerData: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.updateFile(
      await this.getWorkspaceDmnRunnerDataFs(workspaceFile.workspaceId),
      new StorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(dmnRunnerData)),
        path: `/${workspaceFile.relativePath}`,
      })
    );

    if (broadcastArgs.broadcast) {
      const broadcastChannel = new BroadcastChannel(
        this.getUniqueFileIdentifier({
          workspaceId: workspaceFile.workspaceId,
          relativePath: workspaceFile.relativePath,
        })
      );
      broadcastChannel.postMessage({
        type: "UPDATE",
        dmnRunnerData,
      } as WorkspaceDmnRunnerEvents);
    }
  }

  public async renameDmnRunnerData(workspaceFile: WorkspaceFile, newFileNameWithoutExtension: string) {
    const dmnRunnerDataFile = await this.getDmnRunnerData(workspaceFile);
    if (!dmnRunnerDataFile) {
      console.debug(
        `Can't rename DMN Runner data, because it doesn't exist for file '${workspaceFile.relativePath}' on Workspace '${workspaceFile.workspaceId}'`
      );
      return;
    }

    return this.storageService.renameFile(
      await this.getWorkspaceDmnRunnerDataFs(workspaceFile.workspaceId),
      dmnRunnerDataFile,
      `${newFileNameWithoutExtension}`
    );
  }

  public async delete(workspaceId: string) {
    indexedDB.deleteDatabase(this.getDmnRunnerDataStoreName(workspaceId));
  }

  public getDmnRunnerInputsLabel() {
    return "__dmn_runner_inputs";
  }

  public getDmnRunnerDataStoreName(workspaceId: string) {
    return `${workspaceId}${this.getDmnRunnerInputsLabel()}`;
  }

  public getAbsolutePath(args: { workspaceId: string; relativePath?: string }) {
    return join("/", args.relativePath ?? "");
  }

  public getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }) {
    return args.workspaceId + this.getDmnRunnerInputsLabel() + this.getAbsolutePath(args);
  }
}
