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

import { editor } from "monaco-editor";
import * as React from "react";
import { DashbuilderEditorChannelApi } from "../../../api";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import {
  DashbuilderLanguageServiceCommandArgs,
  DashbuilderLanguageServiceCommandIds,
} from "@kie-tools/dashbuilder-language-service/dist/api";

export function initAugmentationCommands(
  editorInstance: editor.IStandaloneCodeEditor,
  channelApi: MessageBusClientApi<DashbuilderEditorChannelApi>
): DashbuilderLanguageServiceCommandIds {
  return {
    "editor.ls.commands.OpenCompletionItems": editorInstance.addCommand(
      0,
      async (ctx, args: DashbuilderLanguageServiceCommandArgs["editor.ls.commands.OpenCompletionItems"]) => {
        editorInstance.setPosition({
          lineNumber: args.newCursorPosition.line + 1,
          column: args.newCursorPosition.character + 1,
        });
        editorInstance.trigger("OpenCompletionItemsAtTheBottom", "editor.action.triggerSuggest", {});
      }
    )!,
  };
}
