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
import { encoder, WorkspaceFile } from "../WorkspacesContext";
import { StorageFile, StorageService } from "./StorageService";
import { WorkspaceWorkerFileDescriptor } from "../../workspacesWorker/api/WorkspaceWorkerFileDescriptor";

export class WorkspaceSvgService {
  constructor(private readonly storageService: StorageService, private readonly fsCache = new FsCache()) {}

  public async getWorkspaceSvgsFs(workspaceId: string) {
    return this.fsCache.getOrCreateFs(`${workspaceId}__svgs`);
  }

  public async getSvg(wwfd: WorkspaceWorkerFileDescriptor) {
    return this.storageService.getFile(await this.getWorkspaceSvgsFs(wwfd.workspaceId), `/${wwfd.relativePath}.svg`);
  }

  public async deleteSvg(wwfd: WorkspaceWorkerFileDescriptor) {
    const svgFile = await this.getSvg(wwfd);
    if (!svgFile) {
      console.debug(
        `Can't delete SVG, because it doesn't exist for file '${wwfd.relativePath}' on Workspace '${wwfd.workspaceId}'`
      );
      return;
    }

    await this.storageService.deleteFile(await this.getWorkspaceSvgsFs(wwfd.workspaceId), `/${wwfd.relativePath}.svg`);
  }

  public async createOrOverwriteSvg(wwfd: WorkspaceWorkerFileDescriptor, svgString: string) {
    await this.storageService.createOrOverwriteFile(
      await this.getWorkspaceSvgsFs(wwfd.workspaceId),
      new StorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(svgString)),
        path: `/${wwfd.relativePath}.svg`,
      })
    );
  }

  public async renameSvg(wwfd: WorkspaceWorkerFileDescriptor, newFileNameWithoutExtension: string) {
    const svgFile = await this.getSvg(wwfd);
    if (!svgFile) {
      console.debug(
        `Can't rename SVG, because it doesn't exist for file '${wwfd.relativePath}' on Workspace '${wwfd.workspaceId}'`
      );
      return;
    }

    const wf = new WorkspaceFile({
      workspaceId: wwfd.workspaceId,
      relativePath: wwfd.relativePath,
      getFileContents: async () => new Uint8Array(), // This is just temporary to be able to use `wf.extension`
    });

    return this.storageService.renameFile(
      await this.getWorkspaceSvgsFs(wwfd.workspaceId),
      svgFile,
      `${newFileNameWithoutExtension}.${wf.extension}`
    );
  }

  public async delete(workspaceId: string) {
    indexedDB.deleteDatabase(WorkspaceSvgService.getSvgStoreName(workspaceId));
  }

  private static getSvgStoreName(workspaceId: string) {
    return `${workspaceId}__svgs`;
  }
}
