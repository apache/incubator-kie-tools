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

import * as vscode from "vscode";
import * as __path from "path";
import { RelativePattern, WorkspaceFolder } from "vscode";
import { listFiles } from "isomorphic-git";
import { Minimatch } from "minimatch";
import { VsCodeEquivalentIsomorphicGitFs } from "./VsCodeResourceContentServiceIsomorphicGitFs";
import { toFsPath } from "@kie-tools-core/operating-system/dist/paths";

/**
 * Implementation of a ResourceContentService using the vscode apis to list/get assets.
 */
export class VsCodeResourceContentServiceForWorkspaces implements ResourceContentService {
  private readonly currentAssetFolder: string;
  private readonly vscodeEquivalentFs: VsCodeEquivalentIsomorphicGitFs;

  constructor(currentAssetFolder: string) {
    this.currentAssetFolder = currentAssetFolder;
    this.vscodeEquivalentFs = new VsCodeEquivalentIsomorphicGitFs();
  }

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    if (!vscode.workspace.workspaceFolders) {
      throw new Error("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: No workspaces found.");
    }

    const workspaceRootAbsoluteFsPath = vscode.workspace.workspaceFolders[0].uri.fsPath + __path.sep;

    const baseAbsoluteFsPath =
      opts?.type === SearchType.ASSET_FOLDER
        ? __path.join(workspaceRootAbsoluteFsPath, this.currentAssetFolder)
        : workspaceRootAbsoluteFsPath;

    // The vscode API will read all files, including the .gitignore ones,
    // making this action is less performatic than the git ls-files which will
    // automatically exclude the .gitignore files.
    try {
      console.debug("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Trying to use isomorphic-git to read dir.");
      const normalizedPosixPathsRelativeToTheBasePath = await listFiles({
        fs: this.vscodeEquivalentFs as any,
        dir: baseAbsoluteFsPath,
      });
      console.debug("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Success on using isomorphic-git!");

      const minimatch = new Minimatch(pattern);
      const regexp = minimatch.makeRe(); // The regexp is ~50x faster than the direct match using glob.
      const matchingNormalizedPosixPathsRelativeToTheBasePath = normalizedPosixPathsRelativeToTheBasePath.filter(
        (p) =>
          regexp.test(
            "/" + p // Adding a leading slash here to make the regex have the same behavior as the glob with **/* pattern.
          ) || regexp.test(__path.relative(this.currentAssetFolder, p)) // check on the asset folder for *.{ext} pattern
      );
      return new ResourcesList(pattern, matchingNormalizedPosixPathsRelativeToTheBasePath);
    } catch (error) {
      console.debug(
        "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Failed to use isomorphic-git to read dir. Falling back to vscode's API.",
        error
      );
      const relativePattern = new RelativePattern(baseAbsoluteFsPath, pattern);
      const files = await vscode.workspace.findFiles(relativePattern);
      const normalizedPosixPathsRelativeToTheWorkspaceRoot = files.map((uri) =>
        vscode.workspace.asRelativePath(uri, false)
      );
      return new ResourcesList(pattern, normalizedPosixPathsRelativeToTheWorkspaceRoot);
    }
  }

  public async get(
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    opts?: ResourceContentOptions
  ): Promise<ResourceContent | undefined> {
    if (!vscode.workspace.workspaceFolders) {
      throw new Error("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: No workspaces found.");
    }

    if (__path.isAbsolute(normalizedPosixPathRelativeToTheWorkspaceRoot)) {
      throw new Error(
        "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Can't work with absolute paths. All paths must be relative to the workspace root."
      );
    }

    const workspaceRootAbsoluteFsPath = vscode.workspace.workspaceFolders[0].uri.fsPath;
    const normalizedFsPathRelativeToTheWorkspaceRoot = toFsPath(normalizedPosixPathRelativeToTheWorkspaceRoot);

    const absoluteFsPath = __path.join(workspaceRootAbsoluteFsPath, normalizedFsPathRelativeToTheWorkspaceRoot);

    if (__path.resolve(workspaceRootAbsoluteFsPath, normalizedFsPathRelativeToTheWorkspaceRoot) !== absoluteFsPath) {
      throw new Error(
        "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Path relative to the workspace root trying to access files outside the workspace."
      );
    }

    try {
      const content = await vscode.workspace.fs.readFile(vscode.Uri.parse(absoluteFsPath));

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
        `VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Error reading file ${normalizedPosixPathRelativeToTheWorkspaceRoot}. Returning undefined.`,
        e
      );
      return new ResourceContent(normalizedPosixPathRelativeToTheWorkspaceRoot, undefined, opts?.type);
    }
  }
}
