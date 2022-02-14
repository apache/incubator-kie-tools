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

import * as monaco from "monaco-editor";
import { languages, Position } from "monaco-editor";
import { ASTNode, ObjectASTNode, PropertyASTNode } from "../../language/parser";
import { AbstractCompletionHelper, CompletionContext } from "./CompletionHelper";

const FUNCTIONS_NODE = "functions";

function buildFunctionSuggestions(name: string, position: Position): languages.CompletionItem {
  return {
    label: name,
    kind: monaco.languages.CompletionItemKind.Snippet,
    insertText: `{ ${buildFunctionObjectProperties(name)}},`,
    range: {
      startLineNumber: position.lineNumber,
      endLineNumber: position.lineNumber,
      startColumn: position.column,
      endColumn: position.column,
    },
  };
}

function buildFunctionObjectSuggestions(name: string, position: Position): languages.CompletionItem {
  return {
    label: name,
    kind: monaco.languages.CompletionItemKind.Value,
    insertText: buildFunctionObjectProperties(name),
    range: {
      startLineNumber: position.lineNumber,
      endLineNumber: position.lineNumber,
      startColumn: position.column,
      endColumn: position.column,
    },
  };
}

function buildFunctionObjectProperties(name: string) {
  return `\n    "name": "${name}",\n    "operation": "http://myservice.com/openapi.json#${name}"\n`;
}

function checkFunctionsPropertyNode(functionsNode: ASTNode): boolean {
  // check if node is a the functions node in the root object and the type is an array.
  if (functionsNode.type !== "array") {
    return false;
  }

  if (!functionsNode.parent) {
    return false;
  }

  const asProp = functionsNode.parent as PropertyASTNode;

  if (!asProp.parent || asProp.parent.parent) {
    return false;
  }

  return asProp.keyNode && asProp.keyNode.value.toLowerCase() === FUNCTIONS_NODE;
}

export class FunctionObjectCompletionHelper extends AbstractCompletionHelper {
  buildSuggestions = (context: CompletionContext): languages.CompletionItem[] | undefined => {
    return [];
  };

  public matches = (node: ASTNode): boolean => {
    return checkFunctionsPropertyNode(node);
  };
}

export class FunctionObjectContentCompletionHelper extends AbstractCompletionHelper {
  public buildSuggestions = (context: CompletionContext): languages.CompletionItem[] | undefined => {
    return [];
  };

  public matches = (node: ASTNode): boolean => {
    // check if node has parent ("functions" node)
    if (!node.parent) {
      return false;
    }

    if (!checkFunctionsPropertyNode(node.parent)) {
      return false;
    }

    if (node.type === "null") {
      return true;
    }

    if (node.type !== "object") {
      return false;
    }

    const objetNode = node as ObjectASTNode;

    return !(objetNode.properties && objetNode.properties.length > 0);
  };
}
