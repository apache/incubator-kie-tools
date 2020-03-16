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

import { UserData } from "../storage/core/UserData";

const NUMBER_OF_FILES_TO_KEEP = 50;

export class DesktopUserData {
  private userData: UserData;

  constructor() {
    this.userData = new UserData({
      configName: "config",
      defaults: {
        lastOpenedFiles: []
      }
    });
  }

  public registerFile(fullPath: string) {
    const lastOpenedFiles = this.userData.get("lastOpenedFiles");
    lastOpenedFiles.unshift(fullPath);
    this.userData.set(
      "lastOpenedFiles",
      lastOpenedFiles
        .filter((item: string, i: number, ar: string[]) => ar.indexOf(item) === i)
        .slice(0, NUMBER_OF_FILES_TO_KEEP)
    );
  }

  public getLastOpenedFiles(): string[] {
    return this.userData.get("lastOpenedFiles");
  }

  public clear() {
    this.userData.clear();
  }
}
