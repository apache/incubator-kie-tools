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
  WorkspaceEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  WorkspaceChannelApi,
} from "../api";
import * as vscode from "vscode";

export class VsCodeWorkspaceChannelApiImpl implements WorkspaceChannelApi {
  public kogitoWorkspace_openFile(path: string) {
    try {
      vscode.commands.executeCommand("vscode.open", vscode.Uri.parse(path));
    } catch (e) {
      throw new Error(`Cannot open file at: ${path}.`);
    }
  }

  public async kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent> {
    throw new Error("This is not implemented yet.");
  }

  public async kogitoWorkspace_newEdit(edit: WorkspaceEdit) {
    throw new Error("This is not implemented yet.");
  }

  public async kogitoWorkspace_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList> {
    throw new Error("This is not implemented yet.");
  }
}
