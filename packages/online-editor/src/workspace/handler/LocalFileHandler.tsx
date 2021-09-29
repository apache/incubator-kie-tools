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

import { LocalFile, WorkspaceFile } from "../WorkspacesContext";
import { join } from "path";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { FileHandler, FileHandlerCommonArgs } from "./FileHandler";

export interface LocalFileHandlerArgs extends FileHandlerCommonArgs {
  files: LocalFile[];
}

export class LocalFileHandler extends FileHandler {
  public constructor(private readonly args: LocalFileHandlerArgs) {
    super(args.workspaceService, args.storageService);
  }

  public async store(descriptor: WorkspaceDescriptor): Promise<WorkspaceFile[]> {
    const contextPath = await this.workspaceService.resolveContextPath(descriptor);
    const updatedFiles = this.args.files.map((file: LocalFile) => {
      const updatedPath = join(contextPath, file.path!.substring(file.path!.indexOf("/") + 1));
      return new WorkspaceFile({ getFileContents: file.getFileContents, path: updatedPath });
    });

    await this.storageService.createFiles(updatedFiles, false);
    return updatedFiles;
  }

  public async sync(_descriptor: WorkspaceDescriptor): Promise<void> {
    // Nothing to do here
  }
}
