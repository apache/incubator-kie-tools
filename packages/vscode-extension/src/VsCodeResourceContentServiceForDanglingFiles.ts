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

import {
  ContentType,
  ResourceContent,
  ResourceContentOptions,
  ResourceContentService,
  ResourceListOptions,
  ResourcesList,
  SearchType,
} from "@kie-tools-core/workspace/dist/api";

import * as minimatch from "minimatch";
import * as vscode from "vscode";
import * as __path from "path";

/**
 * Implementation of a ResourceContentService using the Node filesystem APIs. This should only be used when the edited
 * asset is not part the opened workspace.
 */
export class VsCodeResourceContentServiceForDanglingFiles implements ResourceContentService {
  private readonly rootFolder: string;

  constructor(rootFolder: string) {
    this.rootFolder = rootFolder;
  }

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    try {
      const files = await vscode.workspace.fs.readDirectory(vscode.Uri.parse(this.rootFolder));
      return new ResourcesList(
        pattern,
        files.flatMap(([relativePath, fileType]) =>
          fileType === vscode.FileType.File && minimatch(relativePath, pattern) ? relativePath : []
        )
      );
    } catch (e) {
      return new ResourcesList(pattern, []);
    }
  }

  public async get(
    pathRelativeToTheWorkspaceRoot: string,
    opts?: ResourceContentOptions
  ): Promise<ResourceContent | undefined> {
    if (__path.isAbsolute(pathRelativeToTheWorkspaceRoot)) {
      throw new Error(
        "VS CODE RESOURCE CONTENT API IMPL FOR DANGLING FILES: Can't work with absolute paths. All paths must be relative to folder of the open file."
      );
    }

    const absolutePath = __path.join(this.rootFolder, pathRelativeToTheWorkspaceRoot);

    if (__path.resolve(this.rootFolder, pathRelativeToTheWorkspaceRoot) !== absolutePath) {
      throw new Error(
        "VS CODE RESOURCE CONTENT API IMPL FOR DANGLING FILES: Path relative to the root folder trying to access files outside of it."
      );
    }

    try {
      const content = await vscode.workspace.fs.readFile(vscode.Uri.parse(absolutePath));

      if (opts?.type === ContentType.BINARY) {
        return new ResourceContent(
          pathRelativeToTheWorkspaceRoot, // Always return the relative path.
          Buffer.from(content).toString("base64"),
          ContentType.BINARY
        );
      } else {
        return new ResourceContent(
          pathRelativeToTheWorkspaceRoot, // Always return the relative path.
          Buffer.from(content).toString(),
          ContentType.TEXT
        );
      }
    } catch (e) {
      console.error(
        `VS CODE RESOURCE CONTENT API IMPL FOR DANGLING FILES: Error reading file ${pathRelativeToTheWorkspaceRoot}. Returning undefined.`,
        e
      );
      return new ResourceContent(pathRelativeToTheWorkspaceRoot, undefined, opts?.type);
    }
  }
}
