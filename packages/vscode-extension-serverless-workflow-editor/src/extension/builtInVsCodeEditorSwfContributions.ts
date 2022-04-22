import * as vscode from "vscode";
import { COMMAND_IDS } from "./commands";
import * as ls from "vscode-languageserver-types";
import {
  SwfLanguageServiceCommandHandlers,
  SwfLanguageServiceCommandTypes,
} from "@kie-tools/serverless-workflow-language-service";
import { SwfLanguageServiceChannelApiImpl } from "./languageService/SwfLanguageServiceChannelApiImpl";

export function setupBuiltInVsCodeEditorSwfContributions(args: {
  context: vscode.ExtensionContext;
  swfLanguageService: SwfLanguageServiceChannelApiImpl;
}) {
  const swfLsCommandHandlers: SwfLanguageServiceCommandHandlers = {
    "swf.ls.commands.ImportFunctionFromCompletionItem": (args) => {
      // FIXME: tiago
      // Copy from kogitoSwfServiceCatalog_importFunctionFromCompletionItem
      console.error(args);
    },
    "swf.ls.commands.RefreshServiceCatalogFromRhhcc": (args) => {
      // FIXME: tiago
      // Copy from kogitoSwfServiceCatalog_refresh
      console.error(args);
    },
    "swf.ls.commands.SetupServiceRegistryUrl": (args) => {
      vscode.commands.executeCommand(COMMAND_IDS.setupServiceRegistryUrl, args);
    },
    "swf.ls.commands.LogInToRhhcc": (args) => {
      vscode.commands.executeCommand(COMMAND_IDS.loginToRhhcc, args);
    },
    "swf.ls.commands.OpenFunctionsCompletionItems": (args) => {
      if (!vscode.window.activeTextEditor) {
        return;
      }

      vscode.window.activeTextEditor.selection = new vscode.Selection(
        new vscode.Position(args.newCursorPosition.line, args.newCursorPosition.character),
        new vscode.Position(args.newCursorPosition.line, args.newCursorPosition.character)
      );

      vscode.commands.executeCommand("editor.action.triggerSuggest");
    },
    "swf.ls.commands.OpenFunctionsWidget": (args) => {
      console.info("No op");
    },
    "swf.ls.commands.OpenStatesWidget": (args) => {
      console.info("No op");
    },
  };

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.swfLsCommand, (args: ls.Command) => {
      const commandArgs = args.arguments ?? [];
      const commandHandler = swfLsCommandHandlers[args.command as SwfLanguageServiceCommandTypes];
      (commandHandler as (...args: any[]) => any)?.(...commandArgs);
    })
  );

  args.context.subscriptions.push(
    vscode.languages.registerCodeLensProvider(
      { scheme: "file", language: "serverless-workflow-json" },
      {
        provideCodeLenses: async (document: vscode.TextDocument, token: vscode.CancellationToken) => {
          const lsCodeLenses = await args.swfLanguageService.kogitoSwfLanguageService__getCodeLenses({
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
      { scheme: "file", language: "serverless-workflow-json" },
      {
        provideCompletionItems: async (
          document: vscode.TextDocument,
          position: vscode.Position,
          token: vscode.CancellationToken,
          context: vscode.CompletionContext
        ) => {
          const cursorWordRange = document.getWordRangeAtPosition(position);

          const lsCompletionItems = await args.swfLanguageService.kogitoSwfLanguageService__getCompletionItems({
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
