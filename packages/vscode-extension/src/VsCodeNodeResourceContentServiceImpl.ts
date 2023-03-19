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
  SearchType,
} from "@kie-tools-core/workspace/dist/api";

import * as minimatch from "minimatch";
import * as vscode from "vscode";

/**
 * Implementation of a ResourceContentService using the Node filesystem APIs. This should only be used when the edited
 * asset is not part the opened workspace.
 */
export class VsCodeNodeResourceContentServiceImpl implements ResourceContentService {
  private readonly rootFolder: string;

  constructor(rootFolder: string) {
    this.rootFolder = rootFolder;
  }

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    return new Promise<ResourcesList>((resolve, reject) => {
      vscode.workspace.fs.readDirectory(vscode.Uri.parse(this.rootFolder)).then(
        (files) => {
          const paths = files
            .filter(([name, depth]) => {
              let fileName;
              if (opts?.type === SearchType.TRAVERSAL) {
                fileName = this.rootFolder + name;
              } else {
                fileName = name;
              }
              return depth === 1 && minimatch(name, pattern);
            })
            .map(([name]) => this.rootFolder + name);
          resolve(new ResourcesList(pattern, paths));
        },
        (_err) => {
          resolve(new ResourcesList(pattern, []));
        }
      );
    });
  }

  public async get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined> {
    let assetPath = path;

    if (!assetPath.startsWith(this.rootFolder)) {
      assetPath = this.rootFolder + path;
    }

    if (opts?.type === ContentType.BINARY) {
      return new Promise<ResourceContent | undefined>((resolve, reject) => {
        vscode.workspace.fs.readFile(vscode.Uri.parse(assetPath)).then(
          (data) => resolve(new ResourceContent(path, Buffer.from(data).toString("base64"), ContentType.BINARY)),
          (_err) => resolve(new ResourceContent(path, undefined, ContentType.BINARY))
        );
      });
    }
    return new Promise<ResourceContent | undefined>((resolve, reject) => {
      vscode.workspace.fs.readFile(vscode.Uri.parse(assetPath)).then(
        (data) => resolve(new ResourceContent(path, Buffer.from(data).toString(), ContentType.TEXT)),
        (_err) => resolve(new ResourceContent(path, undefined))
      );
    });
  }
}
