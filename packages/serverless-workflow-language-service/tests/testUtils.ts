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

import {
  ELsJsonPath,
  findNodeAtLocation,
  parseJsonContent,
  parseYamlContent,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { FileLanguage } from "@kie-tools/serverless-workflow-language-service/dist/api";
import {
  CodeCompletionStrategy,
  SwfJsonLanguageService,
  SwfYamlLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CompletionItem, DocumentUri, Position } from "vscode-languageserver-types";

/**
 * Gets the corresponding line from an offset.
 *
 * @param fullText -
 * @param offset -
 * @param lineFrom if you want to get the next line set to 1
 * @returns only the line
 */
export function getLineFromOffset(fullText: string, offset: number | undefined, lineFrom = 0): string {
  const partialText = fullText.substring(offset!);
  return partialText.substring(0).split("\n")[lineFrom];
}

export type ContentWithCursor = `${string}🎯${string}`;

export function treat(content: ContentWithCursor, trimContent = true) {
  const trimmedContent = trimContent ? content.trim() : content;
  const treatedContent = trimmedContent.replace("🎯", "");
  const doc = TextDocument.create("", "json", 0, trimmedContent);
  const cursorOffset = trimmedContent.indexOf("🎯");
  return { content: treatedContent, cursorPosition: doc.positionAt(cursorOffset), cursorOffset };
}

export function trim(content: string) {
  return { content: content.trim() };
}

/**
 * Gets the CompletionItem and the cursorPosition for a content with cursor
 *
 * @param ls -
 * @param documentUri -
 * @param contentToParse -
 * @returns
 */
export async function codeCompletionTester(
  ls: SwfJsonLanguageService | SwfYamlLanguageService,
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

/**
 * Get the returned position from getStartNodeValuePosition().
 *
 * @param args -
 */
export function getStartNodeValuePositionTester(args: {
  content: string;
  path: ELsJsonPath;
  codeCompletionStrategy: CodeCompletionStrategy;
  documentUri: string;
  ls: SwfJsonLanguageService | SwfYamlLanguageService;
}): Position | undefined {
  const rootNode =
    args.ls instanceof SwfJsonLanguageService ? parseJsonContent(args.content) : parseYamlContent(args.content);
  const doc = TextDocument.create(args.documentUri, FileLanguage.YAML, 0, args.content);
  const node = findNodeAtLocation(rootNode!, args.path);
  return args.codeCompletionStrategy.getStartNodeValuePosition(doc, node!);
}
