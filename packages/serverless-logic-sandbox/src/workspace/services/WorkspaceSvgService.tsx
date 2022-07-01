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

import { FsCache } from "../commonServices/FsCache";
import { WorkspaceFile } from "../WorkspacesContext";
import { StorageFile, StorageService } from "../commonServices/StorageService";
import { encoder } from "../commonServices/BaseFile";

export class WorkspaceSvgService {
  constructor(private readonly storageService: StorageService, private readonly fsCache = new FsCache()) {}

  public async getWorkspaceSvgsFs(workspaceId: string) {
    return this.fsCache.getOrCreateFs(`${workspaceId}__svgs`);
  }

  public async getSvg(workspaceFile: WorkspaceFile) {
    return this.storageService.getFile(
      await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      `/${workspaceFile.relativePath}.svg`
    );
  }

  public async deleteSvg(workspaceFile: WorkspaceFile) {
    const svgFile = await this.getSvg(workspaceFile);
    if (!svgFile) {
      console.debug(
        `Can't delete SVG, because it doesn't exist for file '${workspaceFile.relativePath}' on Workspace '${workspaceFile.workspaceId}'`
      );
      return;
    }

    await this.storageService.deleteFile(
      await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      `/${workspaceFile.relativePath}.svg`
    );
  }

  public async createOrOverwriteSvg(workspaceFile: WorkspaceFile, svgString: string) {
    await this.storageService.createOrOverwriteFile(
      await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      new StorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(svgString)),
        path: `/${workspaceFile.relativePath}.svg`,
      })
    );
  }

  public async renameSvg(workspaceFile: WorkspaceFile, newFileNameWithoutExtension: string) {
    const svgFile = await this.getSvg(workspaceFile);
    if (!svgFile) {
      console.debug(
        `Can't rename SVG, because it doesn't exist for file '${workspaceFile.relativePath}' on Workspace '${workspaceFile.workspaceId}'`
      );
      return;
    }

    return this.storageService.renameFile(
      await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      svgFile,
      `${newFileNameWithoutExtension}.${workspaceFile.extension}`
    );
  }

  public async delete(workspaceId: string) {
    indexedDB.deleteDatabase(WorkspaceSvgService.getSvgStoreName(workspaceId));
  }

  private static getSvgStoreName(workspaceId: string) {
    return `${workspaceId}__svgs`;
  }
}
