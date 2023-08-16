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

import { VsCodeKieEditorStore } from "@kie-tools-core/vscode-extension";
import { getFileLanguage } from "@kie-tools/serverless-workflow-language-service/dist/api";
import * as vscode from "vscode";
import { COMMAND_IDS } from "./commandIds";
import {
  ShouldOpenDiagramEditorAutomaticallyConfiguration,
  SwfVsCodeExtensionConfiguration,
  WEBVIEW_EDITOR_VIEW_TYPE,
} from "./configuration";

function isSwf(textDocument: vscode.TextDocument) {
  return getFileLanguage(textDocument.fileName) !== null;
}

function getFilePathFromGlobalState(context: vscode.ExtensionContext) {
  const filePath = context.globalState.get("textEditorFilePath", {} as vscode.Uri);
  return vscode.Uri.file(filePath.path);
}

async function openAsDiagramIfSwf(args: { textEditor: vscode.TextEditor; active: boolean }) {
  if (!isSwf(args.textEditor.document)) {
    return;
  }

  await vscode.commands.executeCommand("vscode.openWith", args.textEditor.document.uri, WEBVIEW_EDITOR_VIEW_TYPE, {
    viewColumn: vscode.ViewColumn.Beside,
    // the combination of these two properties below is IMPERATIVE for the good functioning of the preview mechanism.
    preserveFocus: !args.active,
    background: !args.active,
  });
}

async function maybeOpenAsDiagramIfSwf(args: {
  configuration: SwfVsCodeExtensionConfiguration;
  textEditor: vscode.TextEditor;
  active: boolean;
}) {
  if (
    args.configuration.shouldAutomaticallyOpenDiagramEditorAlongsideTextEditor() ===
    ShouldOpenDiagramEditorAutomaticallyConfiguration.ASK
  ) {
    await args.configuration.configureAutomaticallyOpenDiagramEditorAlongsideTextEditor();
  }

  if (
    args.configuration.shouldAutomaticallyOpenDiagramEditorAlongsideTextEditor() ===
    ShouldOpenDiagramEditorAutomaticallyConfiguration.DO_NOT_OPEN
  ) {
    return;
  }

  await openAsDiagramIfSwf(args);
}

export async function setupDiagramEditorCompanionTab(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
  kieEditorsStore: VsCodeKieEditorStore;
}) {
  args.context.subscriptions.push(
    vscode.window.onDidChangeActiveTextEditor(async (textEditor) => {
      if (args.kieEditorsStore.activeEditor) {
        args.kieEditorsStore.openEditors.forEach((kieEditor) => {
          const activeTab = vscode.window.tabGroups.activeTabGroup.activeTab;
          if (
            activeTab &&
            activeTab.input instanceof vscode.TabInputCustom &&
            activeTab.input.uri.toString() !== kieEditor.document.document.uri.toString()
          ) {
            kieEditor.close();
          }
        });
      }

      if (!textEditor) {
        return;
      }

      if (
        [
          ShouldOpenDiagramEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY,
          ShouldOpenDiagramEditorAutomaticallyConfiguration.ASK,
        ].includes(args.configuration.shouldAutomaticallyOpenDiagramEditorAlongsideTextEditor())
      ) {
        args.kieEditorsStore.openEditors.forEach((kieEditor) => {
          if (textEditor?.document.uri.toString() !== kieEditor.document.document.uri.toString()) {
            kieEditor.close();
          }
        });
      }

      if (!isSwf(textEditor.document)) {
        return;
      }
      args.context.globalState.update("textEditorFilePath", textEditor?.document.uri);
      await maybeOpenAsDiagramIfSwf({ configuration: args.configuration, textEditor, active: false });
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.openAsDiagram, async () => {
      if (vscode.window.activeTextEditor) {
        await openAsDiagramIfSwf({ textEditor: vscode.window.activeTextEditor, active: true });
      }
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.setupAutomaticallyOpenDiagramEditorAlongsideTextEditor, async () => {
      await args.configuration.configureAutomaticallyOpenDiagramEditorAlongsideTextEditor();
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.openAsSource, async () => {
      await vscode.commands.executeCommand("vscode.open", getFilePathFromGlobalState(args.context), {
        viewColumn: vscode.ViewColumn.One, // Not ideal, but works well enough.
        preserveFocus: false,
        background: false,
      });
    })
  );

  if (vscode.window.activeTextEditor) {
    if (!isSwf(vscode.window.activeTextEditor.document)) {
      return;
    }

    await maybeOpenAsDiagramIfSwf({
      configuration: args.configuration,
      textEditor: vscode.window.activeTextEditor,
      active: false,
    });
  }
}
