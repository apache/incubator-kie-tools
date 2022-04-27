import { SwfLanguageServiceChannelApi } from "@kie-tools/serverless-workflow-language-service/dist/api";
import { SwfLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";

export class SwfLanguageServiceChannelApiImpl implements SwfLanguageServiceChannelApi {
  constructor(
    private readonly args: {
      ls: SwfLanguageService;
    }
  ) {}

  public async kogitoSwfLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return this.args.ls.getCompletionItems(args);
  }

  public async kogitoSwfLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]> {
    return this.args.ls.getCodeLenses(args);
  }
}
