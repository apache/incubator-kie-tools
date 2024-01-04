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
    const workspaceFolderPath = vscode.workspace.workspaceFolders![0].uri.fsPath + __path.sep;
    const basePath =
      opts?.type === SearchType.ASSET_FOLDER ? workspaceFolderPath + this.currentAssetFolder : workspaceFolderPath;

    // The vscode API will read all files, including the .gitignore ones,
    // making this action is less performatic than the git ls-files which will
    // automatically exclude the .gitignore files.
    try {
      console.debug("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Trying to use isomorphic-git to read dir.");
      const filePathsRelativeToTheBasePath = await listFiles({ fs: this.vscodeEquivalentFs as any, dir: basePath });
      console.debug("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Success on using isomorphic-git!");

      const minimatch = new Minimatch(pattern);
      const regexp = minimatch.makeRe(); // The regexp is ~50x faster than the direct match using glob.
      const matchingPathsRelativeToTheBasePath = filePathsRelativeToTheBasePath.filter(
        (p) =>
          regexp.test(
            "/" + p // Adding a leading slash here to make the regex have the same behavior as the glob with **/* pattern.
          ) || regexp.test(__path.relative(this.currentAssetFolder, p)) // check on the asset folder for *.{ext} pattern
      );
      return new ResourcesList(pattern, matchingPathsRelativeToTheBasePath);
    } catch (error) {
      console.debug(
        "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Failed to use isomorphic-git to read dir. Falling back to vscode's API.",
        error
      );
      const relativePattern = new RelativePattern(basePath, pattern);
      const files = await vscode.workspace.findFiles(relativePattern);
      const pathsRelativeToTheWorkspaceRoot = files.map((uri: vscode.Uri) =>
        vscode.workspace.asRelativePath(uri, false)
      );
      return new ResourcesList(pattern, pathsRelativeToTheWorkspaceRoot);
    }
  }

  public async get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined> {
    const contentPath = this.resolvePath(path);

    if (!contentPath) {
      return new ResourceContent(path, undefined);
    }

    try {
      await vscode.workspace.fs.stat(vscode.Uri.parse(contentPath));
    } catch (e) {
      console.warn(`Error checking file ${path}: ${e}`);
      return new ResourceContent(path, undefined);
    }

    return this.retrieveContent(opts?.type, path, contentPath);
  }

  private resolvePath(uri: string) {
    if (__path.isAbsolute(uri)) {
      return uri;
    }
    const folders: ReadonlyArray<WorkspaceFolder> = vscode.workspace.workspaceFolders!;
    if (folders) {
      const rootPath = folders[0].uri.path;
      if (!uri.startsWith(__path.sep)) {
        uri = __path.sep + uri;
      }
      return rootPath + uri;
    }
    return null;
  }

  private retrieveContent(type: ContentType | undefined, path: string, contentPath: string): Thenable<ResourceContent> {
    if (type === ContentType.BINARY) {
      return vscode.workspace.fs
        .readFile(vscode.Uri.parse(contentPath))
        .then((content) => new ResourceContent(path, Buffer.from(content).toString("base64"), ContentType.BINARY));
    } else {
      return vscode.workspace.fs
        .readFile(vscode.Uri.parse(contentPath))
        .then((content) => new ResourceContent(path, Buffer.from(content).toString(), ContentType.TEXT));
    }
  }
}
