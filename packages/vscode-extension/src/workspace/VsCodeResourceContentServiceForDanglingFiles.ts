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
} from "@kie-tools-core/workspace/dist/api";

import { Minimatch } from "minimatch";
import * as vscode from "vscode";
import * as __path from "path";
import { toFsPath, toPosixPath } from "../paths/paths";

/**
 * Implementation of a ResourceContentService using the Node filesystem APIs. This should only be used when the edited
 * asset is not part the opened workspace.
 */
export class VsCodeResourceContentServiceForDanglingFiles implements ResourceContentService {
  private readonly workspaceRootAbsoluteFsPath: string;
  constructor(private readonly args: { openFileAbsoluteFsPath: string }) {
    this.workspaceRootAbsoluteFsPath = __path.dirname(this.args.openFileAbsoluteFsPath);
  }

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    try {
      const files = await vscode.workspace.fs.readDirectory(vscode.Uri.file(this.workspaceRootAbsoluteFsPath));
      const minimatch = new Minimatch(pattern);
      const regexp = minimatch.makeRe(); // The regexp is ~50x faster than the direct match using glob.
      return new ResourcesList(
        pattern,
        files.flatMap(([p, fileType]) => {
          const matched =
            fileType === 1 && // 1 === vscode.FileType.File, using it directly causes an error
            (regexp.test("/" + p) || regexp.test(p)); // Adding a leading slash here to make the regex have the same behavior as the glob with **/* pattern.

          return matched ? toPosixPath(p) : [];
        })
      );
    } catch (e) {
      return new ResourcesList(pattern, []);
    }
  }

  public async get(
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    opts?: ResourceContentOptions
  ): Promise<ResourceContent | undefined> {
    if (__path.isAbsolute(normalizedPosixPathRelativeToTheWorkspaceRoot)) {
      throw new Error(
        "VS CODE RESOURCE CONTENT API IMPL FOR DANGLING FILES: Can't work with absolute paths. All paths must be relative to folder of the open file."
      );
    }

    const normalizedFsPathRelativeToTheWorkspaceRoot = toFsPath(normalizedPosixPathRelativeToTheWorkspaceRoot);
    const absoluteFsPath = __path.join(this.workspaceRootAbsoluteFsPath, normalizedFsPathRelativeToTheWorkspaceRoot);

    if (
      __path.resolve(this.workspaceRootAbsoluteFsPath, normalizedFsPathRelativeToTheWorkspaceRoot) !== absoluteFsPath
    ) {
      throw new Error(
        "VS CODE RESOURCE CONTENT API IMPL FOR DANGLING FILES: Path relative to the root folder trying to access files outside of it."
      );
    }

    try {
      const content = await vscode.workspace.fs.readFile(vscode.Uri.file(absoluteFsPath));

      if (opts?.type === ContentType.BINARY) {
        return new ResourceContent(
          normalizedPosixPathRelativeToTheWorkspaceRoot, // Always return the relative path.
          Buffer.from(content).toString("base64"),
          ContentType.BINARY
        );
      } else {
        return new ResourceContent(
          normalizedPosixPathRelativeToTheWorkspaceRoot, // Always return the relative path.
          Buffer.from(content).toString(),
          ContentType.TEXT
        );
      }
    } catch (e) {
      console.error(
        `VS CODE RESOURCE CONTENT API IMPL FOR DANGLING FILES: Error reading file ${normalizedPosixPathRelativeToTheWorkspaceRoot}. Returning undefined.`,
        e
      );
      return new ResourceContent(normalizedPosixPathRelativeToTheWorkspaceRoot, undefined, opts?.type);
    }
  }
}
