import {
  CodeLens,
  CompletionItem,
  CompletionItemKind,
  InsertTextFormat,
  Position,
  TextDocumentIdentifier,
} from "vscode-languageserver-types";
import { SwfLanguageServiceChannelApi } from "@kie-tools/serverless-workflow-language-service";

export class SwfLanguageServiceChannelApiImpl implements SwfLanguageServiceChannelApi {
  public async kogitoSwfLanguageService__doCompletion(
    textDocumentIdentifier: TextDocumentIdentifier,
    position: Position
  ): Promise<CompletionItem[]> {
    return [
      {
        kind: CompletionItemKind.Value,
        label: "myCompletionItem",
        sortText: "myCompletionItem",
        detail: "myCompletionItem",
        filterText: "myCompletionItem",
        insertTextFormat: InsertTextFormat.Snippet,
        textEdit: {
          newText: "myCompletionItem",
          range: {
            start: position,
            end: position,
          },
        },
      },
    ];
  }
  public async kogitoSwfLanguageService__doCodeLenses(
    textDocumentIdentifier: TextDocumentIdentifier
  ): Promise<CodeLens[]> {
    return [];
  }
}
