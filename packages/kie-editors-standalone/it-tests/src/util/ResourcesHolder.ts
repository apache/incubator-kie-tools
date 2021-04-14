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

import { ContentType, ResourceContent } from "@kogito-tooling/channel-common-api/dist";

export interface ResourcesHolderItem {
  name: string;
  value: ResourceContent;
}
export class ResourcesHolder {
  public resources: Map<string, ResourceContent>;
  constructor(res?: Map<string, ResourceContent>) {
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
    let fileContent;
    this.readUploadedFileAsText(file).then((result) => {
      fileContent = result;
      this.addFile(
            { name, value: { path: name, type: ContentType.TEXT, content: result} },
            onResourceChanged
      );
    });
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