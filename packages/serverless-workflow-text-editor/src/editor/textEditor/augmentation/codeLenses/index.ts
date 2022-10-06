import * as monaco from "monaco-editor";
import {
  SwfLanguageServiceCommandIds,
  SwfLanguageServiceCommandTypes,
} from "@kie-tools/serverless-workflow-language-service/dist/api";
import { ServerlessWorkflowTextEditorChannelApi } from "../../../../api";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

export function initJsonCodeLenses(
  commandIds: SwfLanguageServiceCommandIds,
  channelApi: MessageBusClientApi<ServerlessWorkflowTextEditorChannelApi>
): monaco.IDisposable {
  return monaco.languages.registerCodeLensProvider(["json", "yaml"], {
    provideCodeLenses: async (model, cancellationToken) => {
      const lsCodeLenses = await channelApi.requests.kogitoSwfLanguageService__getCodeLenses({
        uri: model.uri.toString(),
        content: model.getValue(),
      });

      if (cancellationToken.isCancellationRequested) {
        return;
      }

      const monacoCodeLenses: monaco.languages.CodeLens[] = lsCodeLenses.map((c) => ({
        command: c.command
          ? {
              id: commandIds[c.command.command as SwfLanguageServiceCommandTypes],
              arguments: c.command.arguments,
              title: c.command.title,
            }
          : undefined,
        range: {
          startLineNumber: c.range.start.line + 1,
          endLineNumber: c.range.end.line + 1,
          startColumn: c.range.start.character + 1,
          endColumn: c.range.end.character + 1,
        },
      }));

      return {
        lenses: monacoCodeLenses,
        dispose: () => {},
      };
    },
  });
}
