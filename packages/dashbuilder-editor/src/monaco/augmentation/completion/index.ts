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
import { editor, languages } from "monaco-editor";
import { DashbuilderEditorChannelApi } from "../../../api";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import * as ls from "vscode-languageserver-types";
import {
  DashbuilderLanguageServiceCommandIds,
  DashbuilderLanguageServiceCommandTypes,
} from "@kie-tools/dashbuilder-language-service/dist/api";
import CompletionItemInsertTextRule = languages.CompletionItemInsertTextRule;

export interface DashbuilderTextEditorInstance {
  commands: DashbuilderLanguageServiceCommandIds;
  instance: editor.IStandaloneCodeEditor;
}

export function initCompletion(
  commandIds: DashbuilderTextEditorInstance["commands"],
  channelApi: MessageBusClientApi<DashbuilderEditorChannelApi>
): monaco.IDisposable {
  return monaco.languages.registerCompletionItemProvider(["yaml"], {
    triggerCharacters: [" ", ":", '"'],
    provideCompletionItems: async (
      model: monaco.editor.ITextModel,
      cursorPosition: monaco.Position,
      context: monaco.languages.CompletionContext,
      cancellationToken: monaco.CancellationToken
    ) => {
      const currentWordPosition = model.getWordAtPosition(cursorPosition);

      const lsCompletionItems = await channelApi.requests.kogitoDashbuilderLanguageService__getCompletionItems({
        content: model.getValue(),
        uri: model.uri.toString(),
        cursorPosition: {
          line: cursorPosition.lineNumber - 1,
          character: cursorPosition.column - 1,
        },
        cursorWordRange: {
          start: {
            line: cursorPosition.lineNumber - 1,
            character: (currentWordPosition?.startColumn ?? cursorPosition.column) - 1,
          },
          end: {
            line: cursorPosition.lineNumber - 1,
            character: (currentWordPosition?.endColumn ?? cursorPosition.column) - 1,
          },
        },
      });

      if (cancellationToken.isCancellationRequested) {
        return undefined;
      }

      const monacoCompletionItems: monaco.languages.CompletionItem[] = lsCompletionItems.map((c: any) => ({
        kind: toMonacoCompletionItemKind(c.kind),
        label: c.label,
        sortText: c.sortText,
        detail: c.detail,
        filterText: c.filterText,
        insertTextRules:
          c.insertTextFormat === ls.InsertTextFormat.Snippet ? CompletionItemInsertTextRule.InsertAsSnippet : undefined,
        insertText: c.insertText ?? c.textEdit?.newText ?? "",
        command: c.command
          ? {
              id: commandIds[c.command.command as DashbuilderLanguageServiceCommandTypes],
              arguments: c.command.arguments,
              title: c.command.title,
            }
          : undefined,
        range: {
          startLineNumber: (c.textEdit as ls.TextEdit).range.start.line + 1,
          startColumn: (c.textEdit as ls.TextEdit).range.start.character + 1,
          endLineNumber: (c.textEdit as ls.TextEdit).range.end.line + 1,
          endColumn: (c.textEdit as ls.TextEdit).range.end.character + 1,
        },
      }));

      return {
        suggestions: monacoCompletionItems,
      };
    },
  });
}

function toMonacoCompletionItemKind(lsCompletionItemKind: ls.CompletionItemKind | undefined) {
  switch (lsCompletionItemKind) {
    case ls.CompletionItemKind.Text:
      return monaco.languages.CompletionItemKind.Text;
    case ls.CompletionItemKind.Method:
      return monaco.languages.CompletionItemKind.Method;
    case ls.CompletionItemKind.Function:
      return monaco.languages.CompletionItemKind.Function;
    case ls.CompletionItemKind.Constructor:
      return monaco.languages.CompletionItemKind.Constructor;
    case ls.CompletionItemKind.Field:
      return monaco.languages.CompletionItemKind.Field;
    case ls.CompletionItemKind.Variable:
      return monaco.languages.CompletionItemKind.Variable;
    case ls.CompletionItemKind.Class:
      return monaco.languages.CompletionItemKind.Class;
    case ls.CompletionItemKind.Interface:
      return monaco.languages.CompletionItemKind.Interface;
    case ls.CompletionItemKind.Module:
      return monaco.languages.CompletionItemKind.Module;
    case ls.CompletionItemKind.Property:
      return monaco.languages.CompletionItemKind.Property;
    case ls.CompletionItemKind.Unit:
      return monaco.languages.CompletionItemKind.Unit;
    case ls.CompletionItemKind.Value:
      return monaco.languages.CompletionItemKind.Value;
    case ls.CompletionItemKind.Enum:
      return monaco.languages.CompletionItemKind.Enum;
    case ls.CompletionItemKind.Keyword:
      return monaco.languages.CompletionItemKind.Keyword;
    case ls.CompletionItemKind.Snippet:
      return monaco.languages.CompletionItemKind.Snippet;
    case ls.CompletionItemKind.Color:
      return monaco.languages.CompletionItemKind.Color;
    case ls.CompletionItemKind.File:
      return monaco.languages.CompletionItemKind.File;
    case ls.CompletionItemKind.Reference:
      return monaco.languages.CompletionItemKind.Reference;
    case ls.CompletionItemKind.Folder:
      return monaco.languages.CompletionItemKind.Folder;
    case ls.CompletionItemKind.EnumMember:
      return monaco.languages.CompletionItemKind.EnumMember;
    case ls.CompletionItemKind.Constant:
      return monaco.languages.CompletionItemKind.Constant;
    case ls.CompletionItemKind.Struct:
      return monaco.languages.CompletionItemKind.Struct;
    case ls.CompletionItemKind.Event:
      return monaco.languages.CompletionItemKind.Event;
    case ls.CompletionItemKind.Operator:
      return monaco.languages.CompletionItemKind.Operator;
    case ls.CompletionItemKind.TypeParameter:
      return monaco.languages.CompletionItemKind.TypeParameter;
    default:
      throw new Error("Can't convert from LS Completion Kind to Monaco Completion Kind");
  }
}
