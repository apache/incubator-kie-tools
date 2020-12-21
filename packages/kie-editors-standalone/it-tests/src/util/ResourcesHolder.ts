/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ContentType } from "@kogito-tooling/channel-common-api/dist";

export interface ResourcesHolderItem {
  name: string;
  value: Resource;
}

export interface Resource {
  contentType: ContentType;
  content: Promise<string>;
}

export class ResourcesHolder {
  public resources: Map<string, Resource>;
  constructor(res?: Map<string, Resource>) {
    if (res) {
      this.resources = res;
    } else {
      this.resources = new Map();
    }
  }
  private readUploadedFileAsText = (inputFile: File): Promise<string> => {
    const temporaryFileReader = new FileReader();

    return new Promise((resolve, reject) => {
      temporaryFileReader.onerror = () => {
        temporaryFileReader.abort();
        reject(new DOMException("Problem parsing input file."));
      };
      temporaryFileReader.onload = () => {
        resolve(temporaryFileReader!.result as string);
      };
      temporaryFileReader.readAsText(inputFile);
    });
  };
  public loadFile(file: File, onResourceChanged?: () => void): ResourcesHolder {
    const name: string = file.name;
    this.addFile(
      { name, value: { contentType: ContentType.TEXT, content: this.readUploadedFileAsText(file) } },
      onResourceChanged
    );
    return this;
  }
  public addFile(resource: ResourcesHolderItem, onResourceChanged?: () => void): ResourcesHolder {
    this.resources.set(resource.name, resource.value);
    onResourceChanged && onResourceChanged();
    return this;
  }

  public removeFile(file: ResourcesHolderItem): void {
    this.resources.delete(file.name);
  }
}
