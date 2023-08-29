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

import { DashbuilderYamlLanguageService } from "@kie-tools/dashbuilder-language-service/dist/channel";
import { CompletionItem, DocumentUri, Position } from "vscode-languageserver-types";
import { TextDocument } from "vscode-languageserver-textdocument";

export type ContentWithCursor = `${string}🎯${string}`;

export function treat(content: ContentWithCursor, trimContent = true) {
  const trimmedContent = trimContent ? content.trim() : content;
  const treatedContent = trimmedContent.replace("🎯", "");
  const doc = TextDocument.create("", "json", 0, trimmedContent);
  const cursorOffset = trimmedContent.indexOf("🎯");
  return { content: treatedContent, cursorPosition: doc.positionAt(cursorOffset), cursorOffset };
}

export async function codeCompletionTester(
  ls: DashbuilderYamlLanguageService,
  documentUri: DocumentUri,
  contentToParse: ContentWithCursor,
  trimContent = true
): Promise<{ completionItems: CompletionItem[]; cursorPosition: Position }> {
  const { content, cursorPosition } = treat(contentToParse, trimContent);

  return {
    completionItems: await ls.getCompletionItems({
      uri: documentUri,
      content,
      cursorPosition,
      cursorWordRange: { start: cursorPosition, end: cursorPosition },
    }),
    cursorPosition,
  };
}
