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

import { UserData } from "./UserData";

export class HubUserData {
  private userData: UserData;

  constructor() {
    this.userData = new UserData({
      configName: "kogito-tooling-hub",
      defaults: {}
    });
  }

  public setVsCodeLocation(location: string) {
    this.userData.set("vscode_location", location);
  }

  public getVsCodeLocation() {
    return this.userData.get("vscode_location") as string | undefined;
  }

  public deleteVsCodeLocation() {
    this.userData.delete("vscode_location");
  }

  public clearAll() {
    this.userData.clearAll();
  }
}
