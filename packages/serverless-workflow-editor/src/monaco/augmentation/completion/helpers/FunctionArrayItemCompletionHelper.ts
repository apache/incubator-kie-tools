/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { languages } from "monaco-editor";
import { ASTNode, PropertyASTNode } from "../../language/parser";
import { CompletionContext, CompletionHelper } from "./CompletionHelper";
import { Function } from "@kie-tools/service-catalog/dist/api";
import { ServerlessWorkflowFunctionDefinition } from "../../../../api";

const FUNCTIONS_NODE = "functions";

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

export class FunctionArrayItemCompletionHelper implements CompletionHelper {
  public matches = (node: ASTNode): boolean => {
    return checkFunctionsPropertyNode(node);
  };

  getSuggestions = (context: CompletionContext): languages.CompletionItem[] => {
    return context.serviceCatalogApi.getFunctions().map((def: Function) => {
      const functionDefinition: ServerlessWorkflowFunctionDefinition = {
        name: def.name,
        operation: def.operation,
        type: def.type,
      };
      return {
        label: functionDefinition.operation,
        kind: monaco.languages.CompletionItemKind.Value,
        insertText: context.language.getStringValue(functionDefinition),
        range: {
          startLineNumber: context.monacoContext.position.lineNumber,
          endLineNumber: context.monacoContext.position.lineNumber,
          startColumn: context.monacoContext.position.column,
          endColumn: context.monacoContext.position.column,
        },
      };
    });
  };
}
