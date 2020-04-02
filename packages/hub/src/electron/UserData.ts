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

export class UserData {
  private readonly path: string;
  private readonly defaults: any;

  constructor(options: { configName: string; defaults: any }) {
    const userDataPath = (electron.app || electron.remote.app).getPath("userData");

    this.path = path.join(userDataPath, options.configName + ".json");
    this.defaults = options.defaults;
  }

  public get(key: string): unknown {
    return this.parseDataFile(this.path, this.defaults)[key];
  }

  public set(key: string, value: any): void {
    const data = this.parseDataFile(this.path, this.defaults);
    data[key] = value;
    fs.writeFileSync(this.path, JSON.stringify(data));
  }

  public delete(key: string) {
    const data = this.parseDataFile(this.path, this.defaults);
    delete data[key];
    fs.writeFileSync(this.path, JSON.stringify(data));
  }

  public clearAll() {
    const data = JSON.parse(JSON.stringify(this.defaults));
    fs.writeFileSync(this.path, JSON.stringify(data));
  }

  private parseDataFile(filePath: string, defaults: any) {
    try {
      return JSON.parse(fs.readFileSync(filePath).toString());
    } catch (error) {
      return defaults;
    }
  }
}
