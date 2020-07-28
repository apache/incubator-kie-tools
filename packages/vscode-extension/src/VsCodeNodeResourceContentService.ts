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
} from "@kogito-tooling/editor-envelope-protocol";

import * as fs from "fs";
import * as minimatch from "minimatch";

/**
 * Implementation of a ResourceContentService using the Node filesystem APIs. This should only be used when the edited
 * asset is not part the opened workspace.
 */
export class VsCodeNodeResourceContentService implements ResourceContentService {

  private readonly rootFolder: string;

  constructor(rootFolder: string) {
    this.rootFolder = rootFolder;
  }

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    return new Promise<ResourcesList>((resolve, reject) => {
      fs.readdir(this.rootFolder, { withFileTypes: true }, (err, files) => {
        if (err) {
          resolve(new ResourcesList(pattern, []));
        } else {
          const paths = files.filter(file => {
            let fileName;
            if (opts?.type === SearchType.TRAVERSAL) {
              fileName = this.rootFolder + file.name;
            } else {
              fileName = file.name;
            }
            return file.isFile() && minimatch(fileName, pattern);
          }).map(file => this.rootFolder + file.name);
          resolve(new ResourcesList(pattern, paths));
        }
      });
    });
  }

  public async get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined> {
    let assetPath = path;

    if (!assetPath.startsWith(this.rootFolder)) {
      assetPath = this.rootFolder + path;
    }

    if (opts?.type === ContentType.BINARY) {
      return new Promise<ResourceContent | undefined>((resolve, reject) => {
        fs.readFile(assetPath, (err, data) => {
          if (err) {
            resolve(new ResourceContent(path, undefined, ContentType.BINARY));
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