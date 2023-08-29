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
import {
  ShouldOpenDashboardEditorAutomaticallyConfiguration,
  DashbuilderVsCodeExtensionConfiguration,
  WEBVIEW_EDITOR_VIEW_TYPE,
} from "./configuration";
import { COMMAND_IDS } from "./commandIds";
import { VsCodeKieEditorStore } from "@kie-tools-core/vscode-extension";

function isDashbuilder(textDocument: vscode.TextDocument) {
  return /^.*\.dash\.(json|yml|yaml)$/.test(textDocument.fileName);
}

function getFilePathFromGlobalState(context: vscode.ExtensionContext) {
  const filePath = context.globalState.get("textEditorFilePath", {} as vscode.Uri);
  return vscode.Uri.file(filePath.path);
}

async function openAsDashboardIfDashbuilder(args: { textEditor: vscode.TextEditor; active: boolean }) {
  if (!isDashbuilder(args.textEditor.document)) {
    return;
  }

  await vscode.commands.executeCommand("vscode.openWith", args.textEditor.document.uri, WEBVIEW_EDITOR_VIEW_TYPE, {
    viewColumn: vscode.ViewColumn.Beside,
    // the combination of these two properties below is IMPERATIVE for the good functioning of the preview mechanism.
    preserveFocus: !args.active,
    background: !args.active,
  });
}

async function maybeOpenAsDashboardIfDashbuilder(args: {
  configuration: DashbuilderVsCodeExtensionConfiguration;
  textEditor: vscode.TextEditor;
  active: boolean;
}) {
  if (
    args.configuration.shouldAutomaticallyOpenDashboardEditorAlongsideTextEditor() ===
    ShouldOpenDashboardEditorAutomaticallyConfiguration.ASK
  ) {
    await args.configuration.configureAutomaticallyOpenDashboardEditorAlongsideTextEditor();
  }

  if (
    args.configuration.shouldAutomaticallyOpenDashboardEditorAlongsideTextEditor() ===
    ShouldOpenDashboardEditorAutomaticallyConfiguration.DO_NOT_OPEN
  ) {
    return;
  }

  await openAsDashboardIfDashbuilder(args);
}

export async function setupDashboardEditorControls(args: {
  context: vscode.ExtensionContext;
  configuration: DashbuilderVsCodeExtensionConfiguration;
  kieEditorsStore: VsCodeKieEditorStore;
}) {
  args.context.subscriptions.push(
    vscode.window.onDidChangeActiveTextEditor(async (textEditor) => {
      if (args.kieEditorsStore.activeEditor) {
        return;
      }

      if (
        [
          ShouldOpenDashboardEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY,
          ShouldOpenDashboardEditorAutomaticallyConfiguration.ASK,
        ].includes(args.configuration.shouldAutomaticallyOpenDashboardEditorAlongsideTextEditor())
      ) {
        args.kieEditorsStore.openEditors.forEach((kieEditor) => {
          if (textEditor?.document.uri.toString() !== kieEditor.document.document.uri.toString()) {
            kieEditor.close();
          }
        });
      }

      if (!textEditor) {
        return;
      }

      if (!isDashbuilder(textEditor.document)) {
        return;
      }
      args.context.globalState.update("textEditorFilePath", textEditor?.document.uri);
      await maybeOpenAsDashboardIfDashbuilder({ configuration: args.configuration, textEditor, active: false });
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.openAsDashboard, async () => {
      if (vscode.window.activeTextEditor) {
        await openAsDashboardIfDashbuilder({ textEditor: vscode.window.activeTextEditor, active: true });
      }
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.setupAutomaticallyOpenDashboardEditorAlongsideTextEditor, async () => {
      await args.configuration.configureAutomaticallyOpenDashboardEditorAlongsideTextEditor();
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.openAsSource, async () => {
      await vscode.commands.executeCommand("vscode.open", getFilePathFromGlobalState(args.context), {
        viewColumn: vscode.ViewColumn.One,
        preserveFocus: false,
        background: false,
      });
    })
  );

  if (vscode.window.activeTextEditor) {
    if (!isDashbuilder(vscode.window.activeTextEditor.document)) {
      return;
    }

    await maybeOpenAsDashboardIfDashbuilder({
      configuration: args.configuration,
      textEditor: vscode.window.activeTextEditor,
      active: false,
    });
  }
}
