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
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "./configuration";
import { SwfServiceCatalogSupportActions } from "./serviceCatalog/SwfServiceCatalogSupportActions";
import { SwfJsonLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { debounce } from "../debounce";

export function setupBuiltInVsCodeEditorSwfContributions(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
  swfLanguageService: SwfJsonLanguageService;
  swfServiceCatalogSupportActions: SwfServiceCatalogSupportActions;
}) {
  const swfLsCommandHandlers: SwfLanguageServiceCommandHandlers = {
    "swf.ls.commands.ImportFunctionFromCompletionItem": (cmdArgs) => {
      args.swfServiceCatalogSupportActions.importFunctionFromCompletionItem(cmdArgs);
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
    "swf.ls.commands.OpenServiceRegistriesConfig": () => {
      vscode.commands.executeCommand(COMMAND_IDS.serviceRegistriesConfig);
    },
    "swf.ls.commands.LogInServiceRegistries": () => {
      vscode.commands.executeCommand(COMMAND_IDS.serviceRegistriesLogin);
    },
    "swf.ls.commands.RefreshServiceRegistries": () => {
      vscode.commands.executeCommand(COMMAND_IDS.serviceRegistriesRefresh);
    },
  };

  const swfJsonDiganosticsCollection = vscode.languages.createDiagnosticCollection("SWF-JSON-DIAGNOSTICS-COLLECTION");

  args.context.subscriptions.push(
    vscode.workspace.onDidOpenTextDocument(async (doc: vscode.TextDocument) => {
      if (!doc.uri.path.match(/\.(sw.json)$/i) || doc.languageId !== "serverless-workflow-json") {
        swfJsonDiganosticsCollection.clear();
        return;
      }
      setSwfJsonDiagnostics(args.swfLanguageService, doc.uri, swfJsonDiganosticsCollection);
    })
  );

  const doValidationOnChange = debounce(setSwfJsonDiagnostics, 1000);

  args.context.subscriptions.push(
    vscode.workspace.onDidChangeTextDocument(async (event: vscode.TextDocumentChangeEvent) => {
      if (!event.document.uri.path.match(/\.(sw.json)$/i) || event.document.languageId !== "serverless-workflow-json") {
        swfJsonDiganosticsCollection.clear();
        return;
      }
      doValidationOnChange(args.swfLanguageService, event.document.uri, swfJsonDiganosticsCollection);
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

  args.context.subscriptions.push(
    vscode.workspace.onDidChangeConfiguration(async (event) => {
      if (event.affectsConfiguration(CONFIGURATION_SECTIONS.enableKogitoServerlessWorkflowVisualizationPreview)) {
        const isStunnerEnabled = args.configuration.isKogitoServerlessWorkflowVisualizationPreviewEnabled();
        const restartNowLabel = "Restart now";
        const selection = await vscode.window.showInformationMessage(
          `Kogito Serverless Workflow Visualization Preview will be ${
            isStunnerEnabled ? "enabled" : "disabled"
          } for JSON files after VS Code is restarted.`,
          restartNowLabel
        );
        if (selection !== restartNowLabel) {
          return;
        }
        vscode.commands.executeCommand("workbench.action.reloadWindow");
      }
    })
  );
}

async function setSwfJsonDiagnostics(
  swfLanguageService: SwfJsonLanguageService,
  uri: vscode.Uri,
  diganosticsCollection: vscode.DiagnosticCollection
) {
  const content = vscode.window.activeTextEditor?.document.getText();

  if (content === undefined) {
    return;
  }

  const lsDiagnostics = await swfLanguageService.getDiagnostics({
    content,
    uriPath: uri.path,
  });

  const diagnostics = lsDiagnostics.map((lsDiagnostic: any) => {
    return new vscode.Diagnostic(lsDiagnostic.range, lsDiagnostic.message, vscode.DiagnosticSeverity.Warning);
  });

  diganosticsCollection.clear();
  diganosticsCollection.set(uri, diagnostics);
}
