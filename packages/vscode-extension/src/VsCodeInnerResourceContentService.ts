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
  ListType,
  ResourceContent,
  ResourceContentOptions,
  ResourceListOptions,
  ResourcesList
} from "@kogito-tooling/core-api";
import { KogitoEditor } from "./KogitoEditor";
import { KogitoEditorStore } from "./KogitoEditorStore";
import * as vscode from "vscode";
import * as nodePath from "path";
import * as fs from "fs";
import * as minimatch from "minimatch";

export class VsCodeInnerResourceContentServiceFactory {

  private readonly editorStore: KogitoEditorStore;

  constructor(editorStore: KogitoEditorStore) {
    this.editorStore = editorStore;
  }

  public lookupContentService(): VsCodeInnerResourceContentService {
    if (this.editorStore.activeEditor) {
      if (this.isAssetInWorkspace(this.editorStore.activeEditor)) {
        return new WorkSpaceResourceContentService(this.editorStore.activeEditor);
      }
      return new NodeResourceContentService(this.editorStore.activeEditor);
    }
    throw new Error("Cannot lookup VsCodeInnerResourceContentService without an active editor");
  }

  private isAssetInWorkspace(editor: KogitoEditor): boolean {
    const workspaceFolders = vscode.workspace.workspaceFolders?.map(folder => folder.uri.path);
    for (const key in workspaceFolders) {
      if (editor.path.startsWith(workspaceFolders[key])) {
        return true;
      }
    }
    return false;
  }
}

export interface VsCodeInnerResourceContentService {

  list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList>;

  get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined>
}


class WorkSpaceResourceContentService implements VsCodeInnerResourceContentService {

  private readonly editor: KogitoEditor;

  constructor(editor: KogitoEditor) {
    this.editor = editor;
  }

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    let expr: string = "";

    if (opts?.type === ListType.ASSET_FOLDER) {
      expr = getParentFolder(this.editor.relativePath);
    }
    expr += pattern;

    const files = await vscode.workspace.findFiles(expr);
    const paths = files.map(f => vscode.workspace.asRelativePath(f.path));
    return new ResourcesList(pattern, paths);
  }

  public async get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined> {
    const workspaceFolders = vscode.workspace!.workspaceFolders!;

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
    const folders: vscode.WorkspaceFolder[] = vscode.workspace!.workspaceFolders!;
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

class NodeResourceContentService implements VsCodeInnerResourceContentService {

  private readonly editor: KogitoEditor;

  constructor(editor: KogitoEditor) {
    this.editor = editor;
  }

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    const parentFolder = getParentFolder(this.editor.path);
    return new Promise<ResourcesList>((resolve, reject) => {
      fs.readdir(parentFolder, { withFileTypes: true }, (err, files) => {
        if (err) {
          reject();
        } else {
          const paths = files.filter(file => {
            let fileName;
            if (opts?.type === ListType.TRAVERSAL) {
              fileName = parentFolder + file.name;
            } else {
              fileName = file.name;
            }
            return file.isFile() && minimatch(fileName, pattern);
          }).map(file => parentFolder + file.name);
          resolve(new ResourcesList(pattern, paths));
        }
      });
    });
  }

  public async get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined> {
    const parentFolder = getParentFolder(this.editor.path);
    let assetPath = path;

    if (!assetPath.startsWith(parentFolder)) {
      assetPath = parentFolder + path;
    }

    if (opts?.type === ContentType.BINARY) {
      return new Promise<ResourceContent | undefined>((resolve, reject) => {
        fs.readFile(assetPath, (err, data) => {
          if (err) {
            resolve(new ResourceContent(path, undefined));
          } else {
            resolve(new ResourceContent(path, Buffer.from(data).toString("base64"), ContentType.BINARY));
          }
        });
      });
    }
    return new Promise<ResourceContent | undefined>((resolve, reject) => {
      fs.readFile(assetPath, (err, data) => {
        if (err) {
          resolve(new ResourceContent(path, undefined));
        } else {
          resolve(new ResourceContent(path, Buffer.from(data).toString(), ContentType.TEXT));
        }
      });
    });
  }
}

function getParentFolder(assetPath: string) {
  if (assetPath.includes(nodePath.sep)) {
    return assetPath.substring(0, assetPath.lastIndexOf(nodePath.sep) + 1);
  }
  return "";
}