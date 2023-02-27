/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as monaco from "monaco-editor";
import CompletionItemInsertTextRule = monaco.languages.CompletionItemInsertTextRule;

function isArrayLine(lineContent: string): boolean {
  const trimmedLine = lineContent.trim();
  console.info("line is " + trimmedLine);

  for (let i = 1; i < trimmedLine.length; i++) {
    const charAt = trimmedLine.charAt(i);
    console.info("char is " + charAt);
    if (charAt == "[") {
      console.info("true");
      return true;
    } else if (charAt == " " || charAt == "\t") {
      continue;
    } else {
      console.info("false");
      return false;
    }
  }
  console.info("default false");
  return false;
}

function isInsideRules(position: monaco.Position, model: string[]): boolean {
  for (let i = position.lineNumber - 2; i > 0; i--) {
    const lineContent = model[i];
    if (lineContent.trim().startsWith("rules")) {
      return true;
    } else if (lineContent.trim().length == 0) {
      continue;
    } else if (lineContent.trim().startsWith("-") && isArrayLine(lineContent)) {
      continue;
    } else {
      return false;
    }
  }
  return false;
}

function getComponents(position: monaco.Position, model: string[]): Array<String> {
  let result = new Array<String>();
  for (let i = position.lineNumber; i > 0; i--) {
    const lineContent = model[i];
    if (lineContent.trim().startsWith("inputs") && lineContent.includes("[")) {
      result = result.concat(getItems(lineContent));
    } else if (lineContent.trim().startsWith("outputComponents")) {
      result = result.concat(getItems(lineContent));
    }
  }
  return result;
}

function getItems(lineContent: String): Array<String> {
  const items = new Array<String>();

  let insideQuotes = false;
  let word = "";
  for (let j = 0; j < lineContent.length; j++) {
    const char = lineContent.charAt(j);
    if (char == "'") {
      if (insideQuotes) {
        items.push(word);
        word = "";
      }
      insideQuotes = !insideQuotes;
    } else if (insideQuotes) {
      word += char;
    }
  }
  return items;
}

export function resolveCompletionItem(
  position: monaco.Position,
  model: string[]
): monaco.languages.CompletionItem | null {
  if (isInsideRules(position, model)) {
    const components = getComponents(position, model);
    let textForInserting = " [ ";
    let textToBeShown = " [ ";
    for (let i = 0; i < components.length; i++) {
      textForInserting += "$" + (i + 1);
      textToBeShown += components[i];
      if (i < components.length - 1) {
        textForInserting += ",";
        textToBeShown += ",";
      }
    }
    textForInserting += " ]";
    textToBeShown += " ]";

    const completionItem: monaco.languages.CompletionItem = {
      insertText: textForInserting,
      kind: monaco.languages.CompletionItemKind.Text,
      label: textToBeShown,
      insertTextRules: CompletionItemInsertTextRule.InsertAsSnippet,
      range: {
        startLineNumber: position.lineNumber,
        startColumn: position.column,
        endLineNumber: 1,
        endColumn: 1,
      },
    };
    return completionItem;
  }
  return null;
}
