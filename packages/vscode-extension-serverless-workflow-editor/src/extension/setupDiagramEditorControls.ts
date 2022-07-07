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

import * as vscode from "vscode";
import {
  ShouldOpenDiagramEditorAutomaticallyConfiguration,
  SwfVsCodeExtensionConfiguration,
  WEBVIEW_EDITOR_VIEW_TYPE,
} from "./configuration";
import { COMMAND_IDS } from "./commandIds";
import { KogitoEditorStore } from "@kie-tools-core/vscode-extension";
import {
  getFileLanguage,
  SwfJsonOffsets,
  SwfYamlOffsets,
} from "@kie-tools/serverless-workflow-language-service/dist/editor";
import { FileLanguage } from "@kie-tools/serverless-workflow-language-service/dist/editor";
import { SwfOffsetsApi } from "@kie-tools/serverless-workflow-language-service/dist/api";

let swfOffsetsApi: SwfOffsetsApi | undefined = undefined;

function isSwf(textDocument: vscode.TextDocument) {
  return getFileLanguage(textDocument.fileName) !== null;
}

function initSwfOffetsApi(textDocument: vscode.TextDocument) {
  if (!textDocument) {
    console.debug("TextDocument not found");
    return;
  }

  const resourceUri = textDocument.uri.path;
  const fileLanguage = getFileLanguage(textDocument.fileName);

  if (!fileLanguage) {
    return;
  }

  const editorContent = textDocument.getText();

  if (!swfOffsetsApi) {
    swfOffsetsApi =
      fileLanguage === FileLanguage.JSON ? new SwfJsonOffsets(resourceUri) : new SwfYamlOffsets(resourceUri);
  }

  swfOffsetsApi?.parseContent(editorContent);
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

export async function setupDiagramEditorControls(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
  kieToolsEditorStore: KogitoEditorStore;
}) {
  args.context.subscriptions.push(
    vscode.window.onDidChangeActiveTextEditor(async (textEditor) => {
      if (args.kieToolsEditorStore.activeEditor) {
        return;
      }

      if (
        args.configuration.shouldAutomaticallyOpenDiagramEditorAlongsideTextEditor() ===
        ShouldOpenDiagramEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY
      ) {
        args.kieToolsEditorStore.openEditors.forEach((kieToolsEditor) => {
          if (textEditor?.document.uri.toString() !== kieToolsEditor.document.document.uri.toString()) {
            kieToolsEditor.close();
          }
        });
      }

      if (!textEditor) {
        return;
      }

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
    vscode.commands.registerCommand(COMMAND_IDS.openAsSource, async (resourceUri) => {
      await vscode.commands.executeCommand("vscode.open", resourceUri, {
        viewColumn: vscode.ViewColumn.One, // Not ideal, but works well enough.
        preserveFocus: false,
        background: false,
      });
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(
      COMMAND_IDS.moveCursorToNode,
      async ({ nodeName, documentUri }: { nodeName: string; documentUri: string }) => {
        const textEditor = vscode.window.visibleTextEditors.filter(
          (textEditor: vscode.TextEditor) => textEditor.document.uri.path === documentUri
        )[0];
        const resourceUri = textEditor.document.uri.path;

        if (swfOffsetsApi && documentUri !== swfOffsetsApi.documentUri) {
          swfOffsetsApi = undefined;
        }

        initSwfOffetsApi(textEditor.document);

        if (!swfOffsetsApi) {
          return;
        }

        const targetOffset = swfOffsetsApi.getStateNameOffset(nodeName);

        if (!targetOffset) {
          return;
        }

        const targetPosition = textEditor.document.positionAt(targetOffset);

        if (targetPosition === null) {
          return;
        }

        await vscode.commands.executeCommand("vscode.open", resourceUri, {
          viewColumn: textEditor.viewColumn,
          preserveFocus: false,
        } as vscode.TextDocumentShowOptions);

        const targetRange = new vscode.Range(targetPosition, targetPosition);

        textEditor.revealRange(targetRange, vscode.TextEditorRevealType.InCenter);
        textEditor.selections = [new vscode.Selection(targetPosition, targetPosition)];
      }
    )
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

  vscode.window.onDidChangeTextEditorSelection((e) => {
    const offset = e.textEditor.document.offsetAt(e.selections[0].active);

    initSwfOffetsApi(e.textEditor.document);

    const nodeName = swfOffsetsApi?.getStateNameFromOffset(offset);

    debugger;
  });
}
