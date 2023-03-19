/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { FileLanguage, getFileLanguageOrThrow } from "@kie-tools/serverless-workflow-language-service/dist/api";
import { SwfJsonOffsets, SwfYamlOffsets } from "@kie-tools/serverless-workflow-language-service/dist/editor";
import * as vscode from "vscode";

const swfJsonOffsets = new SwfJsonOffsets();
const swfYamlOffsets = new SwfYamlOffsets();

export function initSwfOffsetsApi(textDocument: vscode.TextDocument): SwfJsonOffsets | SwfYamlOffsets {
  const fileLanguage = getFileLanguageOrThrow(textDocument.fileName);

  const editorContent = textDocument.getText();

  const swfOffsetsApi = fileLanguage === FileLanguage.JSON ? swfJsonOffsets : swfYamlOffsets;

  swfOffsetsApi.parseContent(editorContent);

  return swfOffsetsApi;
}
