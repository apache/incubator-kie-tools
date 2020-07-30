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
  KogitoEdit,
  ResourceContentRequest,
  ResourceListRequest,
  WorkspaceApi
} from "@kogito-tooling/editor-envelope-protocol";
import { KogitoEditorStore } from "./KogitoEditorStore";

export class KogitoPageChannelApiImpl {
  constructor(private readonly workspaceApi: WorkspaceApi, private readonly editorStore: KogitoEditorStore) {}

  public receive_newEdit(edit: KogitoEdit) {
    console.info("Channel knows that a new edit happened.");
  }

  public receive_ready() {
    console.info("Channel knows that a new editor opened.");
  }

  public receive_openFile(path: string) {
    this.workspaceApi.receive_openFile(path);
  }

  public receive_resourceContentRequest(request: ResourceContentRequest) {
    return this.workspaceApi.receive_resourceContentRequest(request);
  }

  public receive_resourceListRequest(request: ResourceListRequest) {
    return this.workspaceApi.receive_resourceListRequest(request);
  }

  public async getOpenDiagrams() {
    return await Promise.all(
      [...this.editorStore.openEditors].map(async editor => ({
        path: editor.document.relativePath,
        img: await editor.getPreview()
      }))
    );
  }
}
