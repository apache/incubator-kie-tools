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

import * as monaco from "monaco-editor";
import {
  SwfLanguageServiceCommandIds,
  SwfLanguageServiceCommandTypes,
} from "@kie-tools/serverless-workflow-language-service/dist/api";
import { ServerlessWorkflowTextEditorChannelApi } from "../../../../api";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

export function initJsonCodeLenses(
  commandIds: SwfLanguageServiceCommandIds,
  channelApi: MessageBusClientApi<ServerlessWorkflowTextEditorChannelApi>,
  isReadOnly: boolean
): monaco.IDisposable {
  if (isReadOnly) {
    return {
      dispose: () => {},
    };
  }

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
