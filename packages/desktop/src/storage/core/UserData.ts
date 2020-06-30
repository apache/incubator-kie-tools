/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as electron from "electron";
import * as path from "path";
import * as fs from "fs";
import { Files } from "./Files";
import { FS } from "./FS";

export class UserData {
  private readonly basePath: string;
  private readonly dataPath: string;
  private readonly defaults: unknown;
  private readonly resourceTypes: string[];

  constructor(options: { configName: string; resourceTypes: string[]; defaults: any }) {
    this.basePath = (electron.app || electron.remote.app).getPath("userData");
    this.dataPath = path.join(this.basePath, options.configName + ".json");
    this.defaults = options.defaults;
    this.resourceTypes = options.resourceTypes;
  }

  public get(key: string): any {
    return this.parseDataFile(this.dataPath, this.defaults)[key];
  }

  public set(key: string, value: any) {
    const data = this.parseDataFile(this.dataPath, this.defaults);
    data[key] = value;
    fs.writeFileSync(this.dataPath, JSON.stringify(data));
  }

  public saveResource(type: string, fileName: string, fileContent: string) {
    const resourcePath = path.join(this.basePath, type, fileName);
    Files.write(FS.newFile(resourcePath), fileContent)
      .then(() => {
        console.info("User resource " + resourcePath + " saved.");
      })
      .catch(error => {
        console.info("Failed to save user resource" + resourcePath + ":" + error);
      });
  }

  public readResource(type: string, fileName: string): Promise<string> {
    this.createResourceFolderIfNecessary(type);
    const resourcePath = path.join(this.basePath, type, fileName);
    return Files.read(FS.newFile(resourcePath));
  }

  public listResources(type: string): string[] {
    this.createResourceFolderIfNecessary(type);
    return Files.list(FS.newFile(path.join(this.basePath, type))).map(file => file.fullName);
  }

  public deleteResources(files: string[]) {
    files.forEach(file => {
      Files.delete(FS.newFile(file));
    });
  }

  public clearData() {
    const data = JSON.parse(JSON.stringify(this.defaults));
    fs.writeFileSync(this.dataPath, JSON.stringify(data));
  }

  public clearResources(...resourceTypes: string[]) {
    resourceTypes.forEach(resourceType => {
      this.createResourceFolderIfNecessary(resourceType);
      const resourceTypeDir = path.join(this.basePath, resourceType);
      Files.delete(FS.newFile(resourceTypeDir));
    });
  }

  public getBasePath() {
    return this.basePath;
  }

  private createResourceFolderIfNecessary(type: string) {
    const resourceTypeDir = path.join(this.getBasePath(), type);
    if (!fs.existsSync(resourceTypeDir)) {
      fs.mkdirSync(resourceTypeDir);
    }
  }

  private createDataFileIfNecessary() {
    if (!fs.existsSync(this.dataPath)) {
      fs.writeFileSync(this.dataPath, JSON.stringify(this.defaults));
      return true;
    }

    return false;
  }

  private parseDataFile(filePath: string, defaults: any) {
    if (this.createDataFileIfNecessary()) {
      return this.defaults;
    }

    try {
      return JSON.parse(fs.readFileSync(filePath).toString());
    } catch (error) {
      return defaults;
    }
  }
}
