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

import {
  getFileLanguageOrThrow,
  SwfLanguageServiceCommandHandlers,
  SwfLanguageServiceCommandTypes,
} from "@kie-tools/serverless-workflow-language-service/dist/api";
import {
  SwfJsonLanguageService,
  SwfYamlLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import * as vscode from "vscode";
import { TextDocument } from "vscode";
import * as ls from "vscode-languageserver-types";
import { debounce } from "../debounce";
import { COMMAND_IDS } from "./commandIds";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "./configuration";
import { SwfServiceCatalogSupportActions } from "./serviceCatalog/SwfServiceCatalogSupportActions";
import { VsCodeSwfLanguageService } from "./languageService/VsCodeSwfLanguageService";

const SWF_YAML_LANGUAGE_ID = "serverless-workflow-yaml";
const SWF_JSON_LANGUAGE_ID = "serverless-workflow-json";

function isSwf(doc: TextDocument | undefined) {
  return (
    doc?.languageId === SWF_JSON_LANGUAGE_ID ||
    doc?.uri.path.match(/\.(sw.json)$/i) ||
    doc?.languageId === SWF_YAML_LANGUAGE_ID ||
    doc?.uri.path.match(/\.(sw.yaml)$/i) ||
    doc?.uri.path.match(/\.(sw.yml)$/i)
  );
}

export function setupBuiltInVsCodeEditorSwfContributions(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
  vsCodeSwfLanguageService: VsCodeSwfLanguageService;
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

  const swfDiagnosticsCollection = vscode.languages.createDiagnosticCollection("SWF-DIAGNOSTICS-COLLECTION");

  args.context.subscriptions.push(
    vscode.workspace.onDidOpenTextDocument(async (document) => {
      if (!isSwf(document)) {
        // Ignore non SWF files
        return;
      }
      return setSwfDiagnostics(
        args.vsCodeSwfLanguageService.getLs(getFileLanguageOrThrow(document.uri.path)),
        document,
        swfDiagnosticsCollection
      );
    })
  );

  args.context.subscriptions.push(
    vscode.window.onDidChangeActiveTextEditor(async (editor) => {
      if (!(editor?.document && isSwf(editor?.document))) {
        // We only want to show diagnostics when the SWF file is selected.
        swfDiagnosticsCollection.clear();
        return;
      }
      return setSwfDiagnostics(
        args.vsCodeSwfLanguageService.getLs(getFileLanguageOrThrow(editor.document.uri.path)),
        editor.document,
        swfDiagnosticsCollection
      );
    })
  );

  const setSwfDiagnosticsDebounced = debounce(setSwfDiagnostics, 1000);

  args.context.subscriptions.push(
    vscode.workspace.onDidChangeTextDocument(async (event: vscode.TextDocumentChangeEvent) => {
      if (!isSwf(event.document)) {
        // Ignore non SWF files
        return;
      }
      setSwfDiagnosticsDebounced(
        args.vsCodeSwfLanguageService.getLs(getFileLanguageOrThrow(event.document.uri.path)),
        event.document,
        swfDiagnosticsCollection
      );
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
          const lsCodeLenses = await args.vsCodeSwfLanguageService
            .getLs(getFileLanguageOrThrow(document.uri.path))
            .getCodeLenses({
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

          const lsCompletionItems = await args.vsCodeSwfLanguageService
            .getLs(getFileLanguageOrThrow(document.uri.path))
            .getCompletionItems({
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

async function setSwfDiagnostics(
  swfLanguageService: SwfJsonLanguageService | SwfYamlLanguageService,
  document: vscode.TextDocument,
  diagnosticsCollection: vscode.DiagnosticCollection
) {
  const lsDiagnostics = await swfLanguageService.getDiagnostics({
    content: document.getText(),
    uriPath: document.uri.path,
  });

  const vscodeDiagnostics = lsDiagnostics.map(
    (lsDiagnostic) =>
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
