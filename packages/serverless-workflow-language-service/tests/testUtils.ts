/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { TextDocument } from "vscode-languageserver-textdocument";

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

export function treat(content: ContentWithCursor) {
  const trimmedContent = content.trim();
  const treatedContent = trimmedContent.replace("🎯", "");
  const doc = TextDocument.create("", "json", 0, trimmedContent);
  const cursorOffset = trimmedContent.indexOf("🎯");
  return { content: treatedContent, cursorPosition: doc.positionAt(cursorOffset), cursorOffset };
}

export function trim(content: string) {
  return { content: content.trim() };
}
