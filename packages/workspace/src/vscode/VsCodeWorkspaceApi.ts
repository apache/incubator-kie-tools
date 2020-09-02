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

import {
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList
} from "@kogito-tooling/channel-common-api";
import * as fs from "fs";
import * as vscode from "vscode";
import { WorkspaceApi } from "../api";

export class VsCodeWorkspaceApi implements WorkspaceApi {
  public receive_openFile(path: string) {
    if (!fs.existsSync(path)) {
      throw new Error(`Cannot open file at: ${path}.`);
    }
    vscode.commands.executeCommand("vscode.open", vscode.Uri.parse(path));
  }

  public async receive_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent> {
    throw new Error("This is not implemented yet.");
  }

  public async receive_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList> {
    throw new Error("This is not implemented yet.");
  }
}
