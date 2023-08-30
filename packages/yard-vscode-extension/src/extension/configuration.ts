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

export const WEBVIEW_EDITOR_VIEW_TYPE = "kieKogitoWebviewEditorsYard";

export const CONFIGURATION_SECTIONS = {
  automaticallyOpenDiagramEditorAlongsideTextEditor: "kogito.yard.automaticallyOpenDiagramEditorAlongsideTextEditor",
};

type ShouldOpenDiagramEditorAutomaticallyType = "Ask next time" | "Do not open" | "Open automatically";
export enum ShouldOpenDiagramEditorAutomaticallyConfiguration {
  ASK = "Ask next time",
  DO_NOT_OPEN = "Do not open",
  OPEN_AUTOMATICALLY = "Open automatically",
}

export class YardVsCodeExtensionConfiguration {
  public shouldAutomaticallyOpenDiagramEditorAlongsideTextEditor(): ShouldOpenDiagramEditorAutomaticallyConfiguration {
    const configString = vscode.workspace
      .getConfiguration()
      .get(
        CONFIGURATION_SECTIONS.automaticallyOpenDiagramEditorAlongsideTextEditor,
        ShouldOpenDiagramEditorAutomaticallyConfiguration.ASK
      ) as ShouldOpenDiagramEditorAutomaticallyType;

    return this.stringToShouldOpenDiagramEditorAutomaticallyConfiguration(configString);
  }

  public stringToShouldOpenDiagramEditorAutomaticallyConfiguration(
    configString: ShouldOpenDiagramEditorAutomaticallyType
  ) {
    switch (configString) {
      case "Ask next time":
        return ShouldOpenDiagramEditorAutomaticallyConfiguration.ASK;
      case "Do not open":
        return ShouldOpenDiagramEditorAutomaticallyConfiguration.DO_NOT_OPEN;
      case "Open automatically":
        return ShouldOpenDiagramEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY;
      default:
        throw new Error("Unknown config " + configString);
    }
  }

  public async configureAutomaticallyOpenDiagramEditorAlongsideTextEditor() {
    const picked = await vscode.window.showQuickPick(
      [
        ShouldOpenDiagramEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY,
        ShouldOpenDiagramEditorAutomaticallyConfiguration.DO_NOT_OPEN,
      ],
      {
        canPickMany: false,
        title: "Open yard Editor automatically when opening yard files?",
      }
    );

    await vscode.workspace
      .getConfiguration()
      .update(CONFIGURATION_SECTIONS.automaticallyOpenDiagramEditorAlongsideTextEditor, picked, true);
  }
}
