/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  ContentType,
  ResourceContent,
  ResourceContentOptions,
  ResourceContentService,
  ResourceListOptions,
  ResourcesList,
  SearchType
} from "@kogito-tooling/channel-common-api";

import * as vscode from "vscode";
import * as nodePath from "path";
import { RelativePattern, WorkspaceFolder } from "vscode";

/**
 * Implementation of a ResourceContentService using the vscode apis to list/get assets.
 */
export class VsCodeResourceContentService implements ResourceContentService {
  private readonly currentAssetFolder: string;

  constructor(currentAssetFolder: string) {
    this.currentAssetFolder = currentAssetFolder;
  }

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    const workspaceFolderPath = vscode.workspace!.workspaceFolders![0].uri.fsPath + nodePath.sep;
    const basePath = opts?.type === SearchType.ASSET_FOLDER ? workspaceFolderPath + this.currentAssetFolder : workspaceFolderPath;
    const relativePattern = new RelativePattern(basePath, pattern);
    const files = await vscode.workspace.findFiles(relativePattern);
    const paths = files.map(f => vscode.workspace.asRelativePath(f.path));
    return new ResourcesList(pattern, paths);
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
    const folders: ReadonlyArray<WorkspaceFolder> = vscode.workspace!.workspaceFolders!;
    if (folders) {
      const rootPath = folders[0].uri.path;
      if (!uri.startsWith(nodePath.sep)) {
        uri = nodePath.sep + uri;
      }
      return rootPath + uri;
    }
    return null;
  }

  private retrieveContent(type: ContentType | undefined, path: string, contentPath: string): Thenable<ResourceContent> {
    if (type === ContentType.BINARY) {
      return vscode.workspace.fs
        .readFile(vscode.Uri.parse(contentPath))
        .then(content => new ResourceContent(path, Buffer.from(content).toString("base64"), ContentType.BINARY));
    } else {
      return vscode.workspace
        .openTextDocument(contentPath)
        .then(textDoc => new ResourceContent(path, textDoc.getText(), ContentType.TEXT));
    }
  }
}
