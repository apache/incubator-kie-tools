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

import { ExpressionDefinitionLogicType } from "../api";
import { ExpressionDefinition, FunctionExpressionDefinitionKind } from "../api/ExpressionDefinition";
import { ResizingWidth } from "./ResizingWidthsContext";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  CONTEXT_EXPRESSION_EXTRA_WIDTH,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  DECISION_TABLE_ANNOTATION_MIN_WIDTH,
  DECISION_TABLE_INPUT_MIN_WIDTH,
  DECISION_TABLE_OUTPUT_MIN_WIDTH,
  DEFAULT_MIN_WIDTH,
  LIST_EXPRESSION_EXTRA_WIDTH,
  LITERAL_EXPRESSION_EXTRA_WIDTH,
  LITERAL_EXPRESSION_MIN_WIDTH,
  RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
  FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
  LIST_EXPRESSION_ITEM_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  PMML_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  INVOCATION_EXTRA_WIDTH,
  INVOCATION_PARAMETER_MIN_WIDTH,
  INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH,
  FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH,
} from "./WidthConstants";

export function getExpressionMinWidth(expression?: ExpressionDefinition): number {
  if (!expression) {
    return DEFAULT_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Literal) {
    return LITERAL_EXPRESSION_MIN_WIDTH + LITERAL_EXPRESSION_EXTRA_WIDTH;

    // Context + Invocation
  } else if (expression.logicType === ExpressionDefinitionLogicType.Context) {
    const nestedExpressions = [...expression.contextEntries.map((e) => e.entryExpression), expression.result];
    return (
      CONTEXT_ENTRY_INFO_MIN_WIDTH +
      Math.max(CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH, ...nestedExpressions.map((e) => getExpressionMinWidth(e))) +
      CONTEXT_EXPRESSION_EXTRA_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    const nestedExpressions = (expression.bindingEntries ?? []).map((e) => e.entryExpression);
    return (
      INVOCATION_PARAMETER_MIN_WIDTH +
      Math.max(INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH, ...nestedExpressions.map((e) => getExpressionMinWidth(e))) +
      INVOCATION_EXTRA_WIDTH
    );

    // Function
  } else if (expression.logicType === ExpressionDefinitionLogicType.Function) {
    if (expression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      return (
        Math.max(
          FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
          ...[expression.expression].map((expression) => getExpressionMinWidth(expression))
        ) + FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return (
        JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
        JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH +
        JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return (
        PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
        PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH +
        PMML_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else {
      throw new Error("Should never get here");
    }

    // Relation + DecisionTable
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return (
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + //
      (expression.columns?.length ?? 0) * RELATION_EXPRESSION_COLUMN_MIN_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    return (
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
      (expression.input?.length ?? 0) * DECISION_TABLE_INPUT_MIN_WIDTH +
      (expression.output?.length ?? 0) * DECISION_TABLE_OUTPUT_MIN_WIDTH +
      (expression.annotations?.length ?? 0) * DECISION_TABLE_ANNOTATION_MIN_WIDTH
    );
  }

  // List
  else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return (
      Math.max(
        LIST_EXPRESSION_ITEM_MIN_WIDTH,
        ...(expression.items ?? []).map((expression) => getExpressionMinWidth(expression))
      ) + LIST_EXPRESSION_EXTRA_WIDTH
    );
  }

  // Others
  else if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
    return DEFAULT_MIN_WIDTH;
  } else {
    throw new Error("Shouldn't ever reach this point");
  }
}

/**
 * This function goes recursively through all `expression`'s nested expressions and sums either `entryInfoWidth` or default minimal width, returned by `getExpressionMinWidth`, if it is the last nested expression in the chain.
 *
 * This function returns maximal sum found in all `expression`'s nested expressions.
 */
export function getExpressionTotalMinWidth(currentWidth: number, expression: ExpressionDefinition): number {
  if (expression.logicType === ExpressionDefinitionLogicType.Context) {
    const width = currentWidth + (expression.entryInfoWidth ?? 0);
    const contextEntriesMaxWidth = expression.contextEntries.reduce((maxWidth, currentExpression) => {
      return Math.max(maxWidth, getExpressionTotalMinWidth(width, currentExpression.entryExpression));
    }, width);
    const resultWidth = getExpressionTotalMinWidth(width, expression.result);
    return Math.max(contextEntriesMaxWidth, resultWidth);
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    const width = currentWidth + (expression.entryInfoWidth ?? 0);
    return expression.bindingEntries.reduce((maxWidth, currentExpression) => {
      return Math.max(maxWidth, getExpressionTotalMinWidth(width, currentExpression.entryExpression));
    }, width);
  } else {
    // it is an expression without entryInfoWidth
    return currentWidth + getExpressionMinWidth(expression);
  }
}

export function getExpressionResizingWidth(
  expression: ExpressionDefinition | undefined,
  resizingWidths: Map<string, ResizingWidth>
): number {
  if (!expression) {
    return getExpressionMinWidth(expression);
  }

  const resizingWidth = resizingWidths.get(expression.id)?.value;

  // Literal
  if (expression.logicType === ExpressionDefinitionLogicType.Literal) {
    return (resizingWidth ?? expression.width ?? LITERAL_EXPRESSION_MIN_WIDTH) + LITERAL_EXPRESSION_EXTRA_WIDTH;
  }

  // Relation + DecisionTable
  else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    const columns = expression.columns ?? [];

    const variableWidth =
      resizingWidth ??
      columns.reduce((acc, { width }) => {
        return acc + (width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH);
      }, BEE_TABLE_ROW_INDEX_COLUMN_WIDTH);

    return variableWidth;
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    const columns = [...(expression.input ?? []), ...(expression.output ?? []), ...(expression.annotations ?? [])];

    const variableWidth =
      resizingWidth ??
      columns.reduce((acc, { width }) => {
        return acc + (width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH);
      }, BEE_TABLE_ROW_INDEX_COLUMN_WIDTH);

    return variableWidth;
  }

  // Context + Invocation
  else if (expression.logicType === ExpressionDefinitionLogicType.Context) {
    const nestedExpressions = [...expression.contextEntries.map((e) => e.entryExpression), expression.result];
    return (
      resizingWidth ??
      (expression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) +
        Math.max(
          CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
          ...nestedExpressions.map((e) => getExpressionResizingWidth(e, resizingWidths))
        ) +
        CONTEXT_EXPRESSION_EXTRA_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    const nestedExpressions = (expression.bindingEntries ?? []).map((e) => e.entryExpression);
    return (
      resizingWidth ??
      (expression.entryInfoWidth ?? INVOCATION_PARAMETER_MIN_WIDTH) +
        Math.max(
          INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH,
          ...nestedExpressions.map((e) => getExpressionResizingWidth(e, resizingWidths))
        ) +
        INVOCATION_EXTRA_WIDTH
    );
  }

  // Function
  else if (expression.logicType === ExpressionDefinitionLogicType.Function) {
    if (expression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      return (
        resizingWidth ??
        Math.max(
          FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
          ...[expression.expression].map((expression) => getExpressionResizingWidth(expression, resizingWidths))
        ) + FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return (
        resizingWidth ??
        JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
          (expression.classAndMethodNamesWidth ?? JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH) +
          JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return (
        resizingWidth ??
        PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
          PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH +
          PMML_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else {
      throw new Error("Should never get here");
    }
  }

  // List
  else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return (
      resizingWidth ??
      Math.max(
        LIST_EXPRESSION_ITEM_MIN_WIDTH,
        ...(expression.items ?? []).map((expression) => getExpressionResizingWidth(expression, resizingWidths))
      ) + LIST_EXPRESSION_EXTRA_WIDTH
    );
  }

  // Others
  else if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH;
  } else {
    throw new Error("Shouldn't ever reach this point");
  }
}
