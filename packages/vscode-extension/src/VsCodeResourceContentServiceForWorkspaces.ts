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
      console.debug("Trying to use isomorphic-git to read dir.");
      const files = await listFiles({ fs: this.vscodeEquivalentFs as any, dir: basePath });
      const minimatch = new Minimatch(pattern);
      // The regexp is 50x faster than the direct match using glob.
      const regexp = minimatch.makeRe();
      const paths = files.filter((file) => regexp.test(file));
      return new ResourcesList(pattern, paths);
    } catch (error) {
      console.debug("Failed to use isomorphic-git to read dir. Falling back to vscode API. error: ", error);
      const relativePattern = new RelativePattern(basePath, pattern);
      const files = await vscode.workspace.findFiles(relativePattern);
      const paths = files.map((uri: vscode.Uri) => vscode.workspace.asRelativePath(uri));
      return new ResourcesList(pattern, paths);
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
