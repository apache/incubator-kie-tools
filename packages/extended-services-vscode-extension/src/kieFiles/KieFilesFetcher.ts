/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as vscode from "vscode";
import { KieFile } from "./KieFile";

interface KieDocumentFilter extends vscode.DocumentFilter {
  language: string;
  scheme: "file";
}

export const bpmnDocumentFilter: KieDocumentFilter = {
  language: "bpmn",
  scheme: "file",
};

export const dmnDocumentFilter: KieDocumentFilter = {
  language: "dmn",
  scheme: "file",
};

export async function findActiveKieFiles(kieDocumentFilters: KieDocumentFilter[]): Promise<KieFile[]> {
  const tabGroups: readonly vscode.TabGroup[] = vscode.window.tabGroups.all;

  const activeUris: vscode.Uri[] = tabGroups
    .flatMap((tabGroup) => tabGroup.tabs)
    .map((tab) => tab.input)
    .filter((input) => input instanceof vscode.TabInputCustom || input instanceof vscode.TabInputText)
    .map((input: vscode.TabInputCustom | vscode.TabInputText) => input.uri);

  const uniqueActiveUris: vscode.Uri[] = activeUris.reduce((unique, uri) => {
    if (!unique.some((u) => u.fsPath === uri.fsPath)) {
      unique.push(uri);
    }
    return unique;
  }, [] as vscode.Uri[]);

  const activeTextDocuments: vscode.TextDocument[] = await Promise.all(
    uniqueActiveUris.map((uri) => vscode.workspace.openTextDocument(uri))
  );

  const activeKieFiles: KieFile[] = activeTextDocuments
    .filter((textDocument) => vscode.languages.match(kieDocumentFilters, textDocument) > 0)
    .map((textDocument) => new KieFile(textDocument));

  return activeKieFiles;
}
