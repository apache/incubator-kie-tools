/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { ASTNode } from "vscode-json-languageservice";
import { AbstractCompletionHelper, CompletionHelperContext } from "./CompletionHelper";
import { ArrayASTNode, PropertyASTNode } from "vscode-json-languageservice/lib/umd/jsonLanguageTypes";
import { languages, Position } from "monaco-editor";
import * as monaco from "monaco-editor";

function buildFunction(name: string, position: Position): languages.CompletionItem {
  return {
    label: name,
    kind: monaco.languages.CompletionItemKind.Snippet,
    insertText: `{ \n  "name": "${name}", \n  "operation": "http://myservice.com/openapi.json#${name}" \n},`,
    range: {
      startLineNumber: position.lineNumber,
      endLineNumber: position.lineNumber,
      startColumn: position.column,
      endColumn: position.column,
    },
  };
}

export class FullFunctionObjectCompletionHelper extends AbstractCompletionHelper {
  buildSuggestions = (context: CompletionHelperContext): languages.CompletionItem[] | undefined => {
    return [
      buildFunction("function1", context.monacoContext.position),
      buildFunction("function2", context.monacoContext.position),
      buildFunction("function3", context.monacoContext.position),
      buildFunction("function4", context.monacoContext.position),
    ];
  };

  public matches = (node: ASTNode): boolean => {
    const asArray = node as ArrayASTNode;
    if (!asArray.items) {
      return false;
    }

    // check if node has parent ("functions" node)
    if (!node.parent) {
      return false;
    }

    const functionsNode = node.parent;

    // checking if the functions node is at root level
    if (!functionsNode.parent || functionsNode.parent.parent) {
      return false;
    }

    // check if parent node is "functions" node
    const asProp = functionsNode as PropertyASTNode;
    return asProp.keyNode && asProp.keyNode.value.toLowerCase() === "functions";
  };
}
