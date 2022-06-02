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
import { COMMAND_IDS } from "./commandIds";
import * as ls from "vscode-languageserver-types";
import {
  SwfLanguageServiceCommandHandlers,
  SwfLanguageServiceCommandTypes,
} from "@kie-tools/serverless-workflow-language-service/dist/api";
import { SwfVsCodeExtensionConfiguration } from "./configuration";
import { SwfServiceCatalogStore } from "./serviceCatalog/SwfServiceCatalogStore";
import { SwfServiceCatalogSupportActions } from "./serviceCatalog/SwfServiceCatalogSupportActions";
import { SwfJsonLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { debounce } from "../utils";

export function setupBuiltInVsCodeEditorSwfContributions(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
  swfLanguageService: SwfJsonLanguageService;
  swfServiceCatalogGlobalStore: SwfServiceCatalogStore;
  swfServiceCatalogSupportActions: SwfServiceCatalogSupportActions;
}) {
  const swfLsCommandHandlers: SwfLanguageServiceCommandHandlers = {
    "swf.ls.commands.ImportFunctionFromCompletionItem": (cmdArgs) => {
      args.swfServiceCatalogSupportActions.importFunctionFromCompletionItem(cmdArgs);
    },
    "swf.ls.commands.RefreshServiceCatalogFromRhhcc": (cmdArgs) => {
      args.swfServiceCatalogSupportActions.refresh();
    },
    "swf.ls.commands.SetupServiceRegistryUrl": (cmdArgs) => {
      vscode.commands.executeCommand(COMMAND_IDS.setupServiceRegistryUrl, cmdArgs);
    },
    "swf.ls.commands.LogInToRhhcc": (cmdArgs) => {
      vscode.commands.executeCommand(COMMAND_IDS.loginToRhhcc, cmdArgs);
    },
    "swf.ls.commands.OpenFunctionsCompletionItems": (cmdArgs) => {
      if (!vscode.window.activeTextEditor) {
        return;
      }

      vscode.window.activeTextEditor.selection = new vscode.Selection(
        new vscode.Position(cmdArgs.newCursorPosition.line, cmdArgs.newCursorPosition.character),
        new vscode.Position(cmdArgs.newCursorPosition.line, cmdArgs.newCursorPosition.character)
      );

      vscode.commands.executeCommand("editor.action.triggerSuggest");
    },
    "swf.ls.commands.OpenFunctionsWidget": (cmdArgs) => {
      console.info("No op");
    },
    "swf.ls.commands.OpenStatesWidget": (cmdArgs) => {
      console.info("No op");
    },
  };

  const swfJsonValidationDiganosticsCollection: vscode.DiagnosticCollection =
    vscode.languages.createDiagnosticCollection("swfValidation");

  args.context.subscriptions.push(
    vscode.workspace.onDidOpenTextDocument(async (doc: vscode.TextDocument) => {
      const doValidationOnOpen = debounce(
        (args: ls.Command, uri: vscode.Uri, swfJsonValidationDiganosticsCollection: vscode.DiagnosticCollection) => {
          setSwfJsobDiagnostics(args, uri, swfJsonValidationDiganosticsCollection);
        },
        0
      );
      doValidationOnOpen(args, doc.uri, swfJsonValidationDiganosticsCollection);
    })
  );

  args.context.subscriptions.push(
    vscode.workspace.onDidChangeTextDocument(async (event: vscode.TextDocumentChangeEvent) => {
      const doValidationOnChange = debounce(
        (args: ls.Command, uri: vscode.Uri, swfJsonValidationDiganosticsCollection: vscode.DiagnosticCollection) => {
          setSwfJsobDiagnostics(args, uri, swfJsonValidationDiganosticsCollection);
        },
        1000
      );
      doValidationOnChange(args, event.document.uri, swfJsonValidationDiganosticsCollection);
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.swfLsCommand, (args: ls.Command) => {
      const commandArgs = args.arguments ?? [];
      const commandHandler = swfLsCommandHandlers[args.command as SwfLanguageServiceCommandTypes];
      (commandHandler as (...args: any[]) => any)?.(...commandArgs);
    })
  );

  args.context.subscriptions.push(
    vscode.languages.registerCodeLensProvider(
      { scheme: "file", pattern: "**/*.sw.json" },
      {
        provideCodeLenses: async (document: vscode.TextDocument, token: vscode.CancellationToken) => {
          const lsCodeLenses = await args.swfLanguageService.getCodeLenses({
            uri: document.uri.toString(),
            content: document.getText(),
          });

          const vscodeCodeLenses: vscode.CodeLens[] = lsCodeLenses.map((lsCodeLens) => {
            return new vscode.CodeLens(
              new vscode.Range(
                new vscode.Position(lsCodeLens.range.start.line, lsCodeLens.range.start.character),
                new vscode.Position(lsCodeLens.range.end.line, lsCodeLens.range.end.character)
              ),
              lsCodeLens.command
                ? {
                    command: COMMAND_IDS.swfLsCommand,
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
      { scheme: "file", pattern: "**/*.sw.json" },
      {
        provideCompletionItems: async (
          document: vscode.TextDocument,
          position: vscode.Position,
          token: vscode.CancellationToken,
          context: vscode.CompletionContext
        ) => {
          const cursorWordRange = document.getWordRangeAtPosition(position);

          const lsCompletionItems = await args.swfLanguageService.getCompletionItems({
            uri: document.uri.toString(),
            content: document.getText(),
            cursorPosition: position,
            cursorWordRange: {
              start: cursorWordRange?.start ?? position,
              end: cursorWordRange?.end ?? position,
            },
          });

          const vscodeCompletionItems: vscode.CompletionItem[] = lsCompletionItems.map((lsCompletionItem) => {
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
                    command: COMMAND_IDS.swfLsCommand,
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

async function setSwfJsobDiagnostics(
  args: any,
  uri: vscode.Uri,
  swfJsonValidationDiganosticsCollection: vscode.DiagnosticCollection
) {
  const editor = vscode.window.activeTextEditor;

  const jsonContent: string | undefined = editor?.document.getText();
  const jsonContentUri: string = uri.path;
  const jsonSchemaUri: string = "https://serverlessworkflow.io/schemas/0.8/workflow.json";

  let validationResults: ls.Diagnostic[];

  if (jsonContent !== undefined) {
    validationResults = await args.swfLanguageService.swfJsonValidation(jsonContent, jsonContentUri, jsonSchemaUri);

    const diagnostics: vscode.Diagnostic[] = [];

    validationResults.map((result: any) => {
      diagnostics.push(new vscode.Diagnostic(result.range, result.message, vscode.DiagnosticSeverity.Warning));
    });

    swfJsonValidationDiganosticsCollection.clear();
    swfJsonValidationDiganosticsCollection.set(uri, diagnostics);
  }
}
