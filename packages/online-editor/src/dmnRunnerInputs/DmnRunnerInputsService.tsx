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

import { join } from "path";
import { StorageFile, StorageService } from "../workspace/services/StorageService";
import { FsCache } from "../workspace/services/FsCache";
import { encoder, WorkspaceFile } from "../workspace/WorkspacesContext";
import { DmnRunnerInputsEvents } from "./DmnRunnerInputsHook";
import { InputRow } from "@kie-tools/form-dmn";

export class DmnRunnerInputsService {
  private readonly storageService = new StorageService();
  private readonly fsCache = new FsCache();

  public getDmnRunnerInputsFs(workspaceId: string) {
    return this.fsCache.getOrCreateFs(this.getDmnRunnerInputsStoreName(workspaceId));
  }

  public async getDmnRunnerInputs(workspaceFile: WorkspaceFile) {
    return this.storageService.getFile(
      await this.getDmnRunnerInputsFs(workspaceFile.workspaceId),
      `/${workspaceFile.relativePath}`
    );
  }

  public async deleteDmnRunnerInputs(workspaceFile: WorkspaceFile) {
    const dmnRunnerInputsFile = await this.getDmnRunnerInputs(workspaceFile);
    if (!dmnRunnerInputsFile) {
      console.debug(
        `Can't delete DMN Runner inputs, because it doesn't exist for file '${workspaceFile.relativePath}' on Workspace '${workspaceFile.workspaceId}'`
      );
      return;
    }

    const emptyDmnRunnerInputs = JSON.stringify([{}]);

    await this.storageService.createOrOverwriteFile(
      await this.getDmnRunnerInputsFs(workspaceFile.workspaceId),
      new StorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(emptyDmnRunnerInputs)),
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
      type: "DELETE",
      dmnRunnerInputs: emptyDmnRunnerInputs,
    } as DmnRunnerInputsEvents);
  }

  public async createOrOverwriteDmnRunnerInputs(workspaceFile: WorkspaceFile, dmnRunnerInputs: string) {
    await this.storageService.createOrOverwriteFile(
      this.getDmnRunnerInputsFs(workspaceFile.workspaceId),
      new StorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(dmnRunnerInputs)),
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
      dmnRunnerInputs,
      relativePath: workspaceFile.relativePath,
    } as DmnRunnerInputsEvents);
  }

  public async updateDmnRunnerInputs(workspaceFile: WorkspaceFile, dmnRunnerInputs: string): Promise<void> {
    await this.storageService.updateFile(
      await this.getDmnRunnerInputsFs(workspaceFile.workspaceId),
      new StorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(dmnRunnerInputs)),
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
      type: "UPDATE",
      dmnRunnerInputs,
    } as DmnRunnerInputsEvents);
  }

  public async renameDmnRunnerInputs(workspaceFile: WorkspaceFile, newFileNameWithoutExtension: string) {
    const dmnRunnerInputsFile = await this.getDmnRunnerInputs(workspaceFile);
    if (!dmnRunnerInputsFile) {
      console.debug(
        `Can't rename DMN Runner inputs, because it doesn't exist for file '${workspaceFile.relativePath}' on Workspace '${workspaceFile.workspaceId}'`
      );
      return;
    }

    return this.storageService.renameFile(
      await this.getDmnRunnerInputsFs(workspaceFile.workspaceId),
      dmnRunnerInputsFile,
      `${newFileNameWithoutExtension}`
    );
  }

  public async delete(workspaceId: string) {
    indexedDB.deleteDatabase(this.getDmnRunnerInputsStoreName(workspaceId));
  }

  public getDmnRunnerInputsLabel() {
    return "__dmn_runner_inputs";
  }

  public getDmnRunnerInputsStoreName(workspaceId: string) {
    return `${workspaceId}${this.getDmnRunnerInputsLabel()}`;
  }

  public getAbsolutePath(args: { workspaceId: string; relativePath?: string }) {
    return join("/", args.relativePath ?? "");
  }

  public getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }) {
    return args.workspaceId + this.getDmnRunnerInputsLabel() + this.getAbsolutePath(args);
  }

  public stringifyDmnRunnerInputs(
    inputs: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>),
    previous?: Array<InputRow>
  ) {
    if (typeof inputs === "function") {
      return JSON.stringify(inputs(previous ?? [{}]));
    }
    return JSON.stringify(inputs);
  }

  public parseDmnRunnerInputs(inputs: string): Array<InputRow> {
    return JSON.parse(inputs) as Array<InputRow>;
  }
}
