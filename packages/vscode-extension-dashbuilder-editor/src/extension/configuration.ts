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

export const WEBVIEW_EDITOR_VIEW_TYPE = "kieKogitoWebviewEditorsDashbuilder";

export const CONFIGURATION_SECTIONS = {
  automaticallyOpenDashboardEditorAlongsideTextEditor:
    "kogito.dashbuilder.automaticallyOpenDashboardEditorAlongsideTextEditor",
};

type ShouldOpenDashboardEditorAutomaticallyType = "Ask next time" | "Do not open" | "Open automatically";
export enum ShouldOpenDashboardEditorAutomaticallyConfiguration {
  ASK = "Ask next time",
  DO_NOT_OPEN = "Do not open",
  OPEN_AUTOMATICALLY = "Open automatically",
}

export class DashbuilderVsCodeExtensionConfiguration {
  public shouldAutomaticallyOpenDashboardEditorAlongsideTextEditor(): ShouldOpenDashboardEditorAutomaticallyConfiguration {
    const configString = vscode.workspace
      .getConfiguration()
      .get(
        CONFIGURATION_SECTIONS.automaticallyOpenDashboardEditorAlongsideTextEditor,
        ShouldOpenDashboardEditorAutomaticallyConfiguration.ASK
      ) as ShouldOpenDashboardEditorAutomaticallyType;

    return this.stringToShouldOpenDashboardEditorAutomaticallyConfiguration(configString);
  }

  public stringToShouldOpenDashboardEditorAutomaticallyConfiguration(
    configString: ShouldOpenDashboardEditorAutomaticallyType
  ) {
    switch (configString) {
      case "Ask next time":
        return ShouldOpenDashboardEditorAutomaticallyConfiguration.ASK;
      case "Do not open":
        return ShouldOpenDashboardEditorAutomaticallyConfiguration.DO_NOT_OPEN;
      case "Open automatically":
        return ShouldOpenDashboardEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY;
      default:
        throw new Error("Unknown config " + configString);
    }
  }

  public async configureAutomaticallyOpenDashboardEditorAlongsideTextEditor() {
    const picked = await vscode.window.showQuickPick(
      [
        ShouldOpenDashboardEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY,
        ShouldOpenDashboardEditorAutomaticallyConfiguration.DO_NOT_OPEN,
      ],
      {
        canPickMany: false,
        title: "Open Dashbuilder Editor automatically when opening dashbuilder files?",
      }
    );

    await vscode.workspace
      .getConfiguration()
      .update(CONFIGURATION_SECTIONS.automaticallyOpenDashboardEditorAlongsideTextEditor, picked, true);
  }
}
