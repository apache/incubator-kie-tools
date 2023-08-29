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
import * as ls from "vscode-languageserver-types";
import { debounce } from "../debounce";
import { DashbuilderVsCodeExtensionConfiguration } from "./configuration";
import { VsCodeKieEditorStore } from "@kie-tools-core/vscode-extension";
import { VsCodeDashbuilderLanguageService } from "./languageService/VsCodeDashbuilderLanguageService";
import { DashbuilderYamlLanguageService } from "@kie-tools/dashbuilder-language-service/dist/channel";
import {
  DashbuilderLanguageServiceCommandHandlers,
  DashbuilderLanguageServiceCommandTypes,
} from "@kie-tools/dashbuilder-language-service/dist/api";
import { COMMAND_IDS } from "./commandIds";

function isDashbuilder(textDocument: vscode.TextDocument) {
  return /^.*\.dash\.(json|yml|yaml)$/.test(textDocument.fileName);
}

export function setupBuiltInVsCodeEditorDashbuilderContributions(args: {
  context: vscode.ExtensionContext;
  configuration: DashbuilderVsCodeExtensionConfiguration;
  vsCodeDashbuilderLanguageService: VsCodeDashbuilderLanguageService;
  kieEditorsStore: VsCodeKieEditorStore;
}) {
  const dashbuilderLsCommandHandlers: DashbuilderLanguageServiceCommandHandlers = {
    "editor.ls.commands.OpenCompletionItems": (cmdArgs: any) => {
      if (!vscode.window.activeTextEditor) {
        return;
      }

      vscode.window.activeTextEditor.selection = new vscode.Selection(
        new vscode.Position(cmdArgs.newCursorPosition.line, cmdArgs.newCursorPosition.character),
        new vscode.Position(cmdArgs.newCursorPosition.line, cmdArgs.newCursorPosition.character)
      );

      vscode.commands.executeCommand("editor.action.triggerSuggest");
    },
  };
  const dashbuilderDiagnosticsCollection = vscode.languages.createDiagnosticCollection(
    "DASHBUILDER-DIAGNOSTICS-COLLECTION"
  );

  args.context.subscriptions.push(
    vscode.workspace.onDidOpenTextDocument(async (document) => {
      if (!isDashbuilder(document)) {
        // Ignore non Dashbuilder files
        return;
      }
      return setDashbuilderDiagnostics(
        args.vsCodeDashbuilderLanguageService.getLs(document),
        document,
        dashbuilderDiagnosticsCollection
      );
    })
  );

  args.context.subscriptions.push(
    vscode.window.onDidChangeActiveTextEditor(async (editor) => {
      if (!(editor?.document && isDashbuilder(editor?.document))) {
        // We only want to show diagnostics when the Dashbuilder file is selected.
        dashbuilderDiagnosticsCollection.clear();
        return;
      }
      return setDashbuilderDiagnostics(
        args.vsCodeDashbuilderLanguageService.getLs(editor.document),
        editor.document,
        dashbuilderDiagnosticsCollection
      );
    })
  );

  const setDashbuilderDiagnosticsDebounced = debounce(setDashbuilderDiagnostics, 1000);

  args.context.subscriptions.push(
    vscode.workspace.onDidChangeTextDocument(async (event: vscode.TextDocumentChangeEvent) => {
      if (!isDashbuilder(event.document)) {
        // Ignore non Dashbuilder files
        return;
      }
      setDashbuilderDiagnosticsDebounced(
        args.vsCodeDashbuilderLanguageService.getLs(event.document),
        event.document,
        dashbuilderDiagnosticsCollection
      );
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.dashbuilderLsCommand, (args: ls.Command) => {
      const commandArgs = args.arguments ?? [];
      const commandHandler = dashbuilderLsCommandHandlers[args.command as DashbuilderLanguageServiceCommandTypes];
      (commandHandler as (...args: any[]) => any)?.(...commandArgs);
    })
  );

  args.context.subscriptions.push(
    vscode.languages.registerCodeLensProvider(
      { pattern: "**/*.dash.{yaml,yml,json}" },
      {
        provideCodeLenses: async (document: vscode.TextDocument, token: vscode.CancellationToken) => {
          const lsCodeLenses = await args.vsCodeDashbuilderLanguageService.getLs(document).getCodeLenses({
            uri: document.uri.toString(),
            content: document.getText(),
          });

          const vscodeCodeLenses: vscode.CodeLens[] = lsCodeLenses.map((lsCodeLens: any) => {
            return new vscode.CodeLens(
              new vscode.Range(
                new vscode.Position(lsCodeLens.range.start.line, lsCodeLens.range.start.character),
                new vscode.Position(lsCodeLens.range.end.line, lsCodeLens.range.end.character)
              ),
              lsCodeLens.command
                ? {
                    command: COMMAND_IDS.dashbuilderLsCommand,
                    title: lsCodeLens.command.title,
                    arguments: [lsCodeLens.command],
                  }
                : undefined
            );
          });

          return vscodeCodeLenses;
        },
      }
    )
  );

  args.context.subscriptions.push(
    vscode.languages.registerCompletionItemProvider(
      { pattern: "**/*.dash.{yaml,yml,json}" },
      {
        provideCompletionItems: async (
          document: vscode.TextDocument,
          position: vscode.Position,
          token: vscode.CancellationToken,
          context: vscode.CompletionContext
        ) => {
          const cursorWordRange = new vscode.Range(position, position);
          const lsCompletionItems = await args.vsCodeDashbuilderLanguageService.getLs(document).getCompletionItems({
            uri: document.uri.toString(),
            content: document.getText(),
            cursorPosition: position,
            cursorWordRange: {
              start: cursorWordRange?.start ?? position,
              end: cursorWordRange?.end ?? position,
            },
          });

          const vscodeCompletionItems: vscode.CompletionItem[] = lsCompletionItems.map((lsCompletionItem: any) => {
            const rangeStart = (lsCompletionItem.textEdit as ls.TextEdit).range.start;
            const rangeEnd = (lsCompletionItem.textEdit as ls.TextEdit).range.end;
            return {
              kind: lsCompletionItem.kind,
              label: lsCompletionItem.label,
              sortText: lsCompletionItem.sortText,
              detail: lsCompletionItem.detail,
              filterText: lsCompletionItem.filterText,
              insertText:
                lsCompletionItem.insertTextFormat === ls.InsertTextFormat.Snippet
                  ? new vscode.SnippetString(lsCompletionItem.insertText ?? lsCompletionItem.textEdit?.newText ?? "")
                  : lsCompletionItem.insertText ?? lsCompletionItem.textEdit?.newText ?? "",
              command: lsCompletionItem.command
                ? {
                    command: COMMAND_IDS.dashbuilderLsCommand,
                    title: lsCompletionItem.command.title ?? "",
                    arguments: [lsCompletionItem.command],
                  }
                : undefined,
              range: new vscode.Range(
                new vscode.Position(rangeStart.line, rangeStart.character),
                new vscode.Position(rangeEnd.line, rangeEnd.character)
              ),
            };
          });

          return { items: vscodeCompletionItems };
        },
      },
      ` `,
      `:`,
      `"`
    )
  );
}

async function setDashbuilderDiagnostics(
  dashbuilderLanguageService: DashbuilderYamlLanguageService,
  document: vscode.TextDocument,
  diagnosticsCollection: vscode.DiagnosticCollection
) {
  const lsDiagnostics = await dashbuilderLanguageService.getDiagnostics({
    content: document.getText(),
    uriPath: document.uri.path,
  });

  const vscodeDiagnostics = lsDiagnostics.map(
    (lsDiagnostic: any) =>
      new vscode.Diagnostic(
        new vscode.Range(
          new vscode.Position(lsDiagnostic.range.start.line, lsDiagnostic.range.start.character),
          new vscode.Position(lsDiagnostic.range.end.line, lsDiagnostic.range.end.character)
        ),
        lsDiagnostic.message,
        vscode.DiagnosticSeverity.Warning
      )
  );

  diagnosticsCollection.clear();
  diagnosticsCollection.set(document.uri, vscodeDiagnostics);
}
