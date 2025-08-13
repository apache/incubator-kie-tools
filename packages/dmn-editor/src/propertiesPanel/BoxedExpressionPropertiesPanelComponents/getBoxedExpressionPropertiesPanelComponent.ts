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
import { DmnEditorI18n } from "../../i18n";

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

export function getBoxedExpressionPropertiesPanelComponent(
  selectedObjectPath: ExpressionPath,
  i18n: DmnEditorI18n
): {
  component: BoxedExpressionPropertiesPanelComponent;
  title: string;
} {
  if (selectedObjectPath.type === "conditional") {
    if (selectedObjectPath.row === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedConditional,
      };
    }
  }

  if (selectedObjectPath.type === "context") {
    if (selectedObjectPath.column === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedContext,
      };
    }
    if (selectedObjectPath.column === "variable") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.CONTEXT_INFORMATION_ITEM_CELL,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedContextVariable,
      };
    }
  }

  if (selectedObjectPath.type === "decisionTable") {
    if (selectedObjectPath.row === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.decisionTable,
      };
    }
    if (selectedObjectPath.header === "input") {
      if (selectedObjectPath.row < 0) {
        return {
          component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_INPUT_HEADER,
          title: i18n.boxedExpressionPropertiesPanelTitle.decisionTableInputHeader,
        };
      }
      return {
        component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_INPUT_RULE,
        title: i18n.boxedExpressionPropertiesPanelTitle.decisionTableInputCell,
      };
    }
    if (selectedObjectPath.header === "output") {
      if (selectedObjectPath.row < 0) {
        return {
          component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_OUTPUT_HEADER,
          title: i18n.boxedExpressionPropertiesPanelTitle.decisionTableOutputHeader,
        };
      }
      return {
        component: BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_OUTPUT_RULE,
        title: i18n.boxedExpressionPropertiesPanelTitle.decisionTableOutputCell,
      };
    }
  }

  if (selectedObjectPath.type === "every") {
    if (selectedObjectPath.row === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.every,
      };
    }
    if (selectedObjectPath.row === "variable") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.ITERATOR_VARIABLE_CELL,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedEveryVariable,
      };
    }
  }

  if (selectedObjectPath.type === "filter") {
    if (selectedObjectPath.row === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.filter,
      };
    }
  }

  if (selectedObjectPath.type === "for") {
    if (selectedObjectPath.row === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.for,
      };
    }
    if (selectedObjectPath.row === "variable") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.ITERATOR_VARIABLE_CELL,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedForvariable,
      };
    }
  }

  if (selectedObjectPath.type === "functionDefinition") {
    if (selectedObjectPath.parameterIndex === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.FUNCTION_DEFINITION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.functionDefinition,
      };
    }
    return {
      component: BoxedExpressionPropertiesPanelComponent.FUNCTION_DEFINITION_PARAMETERS,
      title: i18n.boxedExpressionPropertiesPanelTitle.functionParameters,
    };
  }

  if (selectedObjectPath.type === "invocation") {
    if (selectedObjectPath.row === undefined || selectedObjectPath.column === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedInvocation,
      };
    }
    if (selectedObjectPath.row < 0) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.INVOCATION_FUNCTION_CALL,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedInvocationFunction,
      };
    }
    if (selectedObjectPath.column === "parameter") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.INVOCATION_INFORMATION_ITEM_CELL,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedInvocationParameter,
      };
    }
    if (selectedObjectPath.column === "expression") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedInvocation,
      };
    }
  }

  if (selectedObjectPath.type === "list") {
    if (selectedObjectPath.row === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.list,
      };
    }
  }

  if (selectedObjectPath.type === "literalExpression") {
    return {
      component: BoxedExpressionPropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
      title: i18n.boxedExpressionPropertiesPanelTitle.literalExpresssion,
    };
  }

  if (selectedObjectPath.type === "relation") {
    if (selectedObjectPath.row === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedRelation,
      };
    }
    if (selectedObjectPath.row < 0) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.RELATION_INFORMATION_ITEM_CELL,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedRelationHeader,
      };
    }
    return {
      component: BoxedExpressionPropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
      title: i18n.boxedExpressionPropertiesPanelTitle.boxedRelationCell,
    };
  }

  if (selectedObjectPath.type === "some") {
    if (selectedObjectPath.row === undefined) {
      return {
        component: BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT,
        title: i18n.boxedExpressionPropertiesPanelTitle.some,
      };
    }
    if (selectedObjectPath.row === "variable") {
      return {
        component: BoxedExpressionPropertiesPanelComponent.ITERATOR_VARIABLE_CELL,
        title: i18n.boxedExpressionPropertiesPanelTitle.boxedsomeVariable,
      };
    }
  }
  return { component: BoxedExpressionPropertiesPanelComponent.WITHOUT_PROPERTIES_CELL, title: "" };
}
