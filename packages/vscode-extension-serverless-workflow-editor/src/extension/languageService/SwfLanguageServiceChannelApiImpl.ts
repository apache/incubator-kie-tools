import { CodeLens, CompletionItem, Position, TextDocumentIdentifier } from "vscode-languageserver-types";
import { SwfLanguageServiceChannelApi } from "@kie-tools/serverless-workflow-language-service";

export class SwfLanguageServiceChannelApiImpl implements SwfLanguageServiceChannelApi {
  public async kogitoSwfLanguageService__doCompletion(
    textDocumentIdentifier: TextDocumentIdentifier,
    position: Position
  ): Promise<CompletionItem[]> {
    return [];
  }
  public async kogitoSwfLanguageService__doCodeLenses(
    textDocumentIdentifier: TextDocumentIdentifier
  ): Promise<CodeLens[]> {
    return [];
  }
}
