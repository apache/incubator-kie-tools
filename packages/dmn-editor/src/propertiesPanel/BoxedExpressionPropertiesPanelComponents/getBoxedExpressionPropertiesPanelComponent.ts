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

import { ExpressionPath } from "../../boxedExpressions/boxedExpressionIndex";

export enum BoxedExpressionPropertiesPanelComponent {
  CONTEXT_INFORMATION_ITEM_CELL = "context-information-item-cell",
  DECISION_TABLE_INPUT_HEADER = "decision-table-input-header",
  DECISION_TABLE_INPUT_RULE = "decision-table-input-rule",
  DECISION_TABLE_OUTPUT_HEADER = "decision-table-output-header",
  DECISION_TABLE_OUTPUT_RULE = "decision-table-output-rule",
  DECISION_TABLE_ROOT = "decision-table-root",
  EXPRESSION_ROOT = "expression-root",
  FUNCTION_DEFINITION_PARAMETERS = "function-definition-parameters",
  FUNCTION_DEFINITION_ROOT = "function-definition-root",
  INVOCATION_FUNCTION_CALL = "invocation-function-call",
  INVOCATION_INFORMATION_ITEM_CELL = "invocation-information-item-cell",
  ITERATOR_VARIABLE_CELL = "iterator-variable-cell",
  LITERAL_EXPRESSION_CONTENT = "literal-expression-content",
  RELATION_INFORMATION_ITEM_CELL = "relation-information-item-cell",
  WITHOUT_PROPERTIES_CELL = "without-properties-cell",
}

export function getBoxedExpressionPropertiesPanelComponent(selectedObjectPath: ExpressionPath): {
  component: BoxedExpressionPropertiesPanelComponent;
  title: string;
} {
  if (selectedObjectPath.type === "conditional") {
    if (selectedObjectPath.row === undefined) {
      return { component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Conditional" };
    }
  }

  if (selectedObjectPath.type === "context") {
    if (selectedObjectPath.column === undefined) {
      return { component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Context" };
    }
    if (selectedObjectPath.column === "variable") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.CONTEXT_INFORMATION_ITEM_CELL,
        title: "Boxed Context Variable",
      };
    }
  }

  if (selectedObjectPath.type === "decisionTable") {
    if (selectedObjectPath.row === undefined) {
      return { component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_ROOT, title: "Decision Table" };
    }
    if (selectedObjectPath.header === "input") {
      if (selectedObjectPath.row < 0) {
        return {
          component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_INPUT_HEADER,
          title: "Decision Table Input Header",
        };
      }
      return {
        component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_INPUT_RULE,
        title: "Decision Table Input Cell",
      };
    }
    if (selectedObjectPath.header === "output") {
      if (selectedObjectPath.row < 0) {
        return {
          component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_OUTPUT_HEADER,
          title: "Decision Table Output Header",
        };
      }
      return {
        component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_OUTPUT_RULE,
        title: "Decision Table Output Cell",
      };
    }
  }

  if (selectedObjectPath.type === "every") {
    if (selectedObjectPath.row === undefined) {
      return { component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Every" };
    }
    if (selectedObjectPath.row === "variable") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.ITERATOR_VARIABLE_CELL,
        title: "Boxed Every Variable",
      };
    }
  }

  if (selectedObjectPath.type === "filter") {
    if (selectedObjectPath.row === undefined) {
      return { component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Filter" };
    }
  }

  if (selectedObjectPath.type === "for") {
    if (selectedObjectPath.row === undefined) {
      return { component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed For" };
    }
    if (selectedObjectPath.row === "variable") {
      return { component: BoxedExpressionPropertiesPanelComponent.ITERATOR_VARIABLE_CELL, title: "Boxed For Variable" };
    }
  }

  if (selectedObjectPath.type === "functionDefinition") {
    if (selectedObjectPath.parameterIndex === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.FUNCTION_DEFINITION_ROOT,
        title: "Function Definition",
      };
    }
    return {
      component: BoxedExpressionPropertiesPanelComponent.FUNCTION_DEFINITION_PARAMETERS,
      title: "Function Parameters",
    };
  }

  if (selectedObjectPath.type === "invocation") {
    if (selectedObjectPath.row === undefined || selectedObjectPath.column === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: "Boxed Invocation",
      };
    }
    if (selectedObjectPath.row < 0) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.INVOCATION_FUNCTION_CALL,
        title: "Boxed Invocation Called Function",
      };
    }
    if (selectedObjectPath.column === "parameter") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.INVOCATION_INFORMATION_ITEM_CELL,
        title: "Boxed Invocation Parameter",
      };
    }
    if (selectedObjectPath.column === "expression") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
        title: "Boxed Invocation",
      };
    }
  }

  if (selectedObjectPath.type === "list") {
    if (selectedObjectPath.row === undefined) {
      return { component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed List" };
    }
  }

  if (selectedObjectPath.type === "literalExpression") {
    return {
      component: BoxedExpressionPropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
      title: "Literal Expression",
    };
  }

  if (selectedObjectPath.type === "relation") {
    if (selectedObjectPath.row === undefined) {
      return { component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Relation" };
    }
    if (selectedObjectPath.row < 0) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.RELATION_INFORMATION_ITEM_CELL,
        title: "Boxed Relation Header",
      };
    }
    return {
      component: BoxedExpressionPropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
      title: "Boxed Relation Cell",
    };
  }

  if (selectedObjectPath.type === "some") {
    if (selectedObjectPath.row === undefined) {
      return { component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Some" };
    }
    if (selectedObjectPath.row === "variable") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.ITERATOR_VARIABLE_CELL,
        title: "Boxed Some Variable",
      };
    }
  }
  return { component: BoxedExpressionPropertiesPanelComponent.WITHOUT_PROPERTIES_CELL, title: "" };
}
