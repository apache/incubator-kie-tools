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

import { BoxedExpression, BoxedFunctionKind } from "../api/BoxedExpression";
import { ResizingWidth } from "./ResizingWidthsContext";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  CONDITIONAL_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH,
  CONDITIONAL_EXPRESSION_EXTRA_WIDTH,
  CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH,
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
  CONTEXT_EXPRESSION_EXTRA_WIDTH,
  DECISION_TABLE_ANNOTATION_MIN_WIDTH,
  DECISION_TABLE_INPUT_MIN_WIDTH,
  DECISION_TABLE_OUTPUT_MIN_WIDTH,
  DEFAULT_MIN_WIDTH,
  FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
  INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH,
  INVOCATION_EXTRA_WIDTH,
  INVOCATION_PARAMETER_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
  LIST_EXPRESSION_EXTRA_WIDTH,
  LIST_EXPRESSION_ITEM_MIN_WIDTH,
  LITERAL_EXPRESSION_EXTRA_WIDTH,
  LITERAL_EXPRESSION_MIN_WIDTH,
  PMML_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
  RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
} from "./WidthConstants";

export function getExpressionMinWidth(expression?: BoxedExpression): number {
  if (!expression || !expression.__$$element) {
    return DEFAULT_MIN_WIDTH;
  }

  // Literal
  else if (expression.__$$element === "literalExpression") {
    return LITERAL_EXPRESSION_MIN_WIDTH + LITERAL_EXPRESSION_EXTRA_WIDTH;
  }

  // Context + Invocation
  else if (expression.__$$element === "context") {
    const result = expression.contextEntry?.find((e) => !e.variable);
    const nestedExpressions = [...(expression.contextEntry ?? []).map((e) => e.expression), result?.expression];
    return (
      CONTEXT_ENTRY_VARIABLE_MIN_WIDTH +
      Math.max(CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH, ...nestedExpressions.map((e) => getExpressionMinWidth(e))) +
      CONTEXT_EXPRESSION_EXTRA_WIDTH
    );
  } else if (expression.__$$element === "invocation") {
    const nestedExpressions = (expression.binding ?? []).map((e) => e.expression);
    return (
      INVOCATION_PARAMETER_MIN_WIDTH +
      Math.max(INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH, ...nestedExpressions.map((e) => getExpressionMinWidth(e))) +
      INVOCATION_EXTRA_WIDTH
    );
  }

  // Function
  else if (expression.__$$element === "functionDefinition") {
    if (expression["@_kind"] === BoxedFunctionKind.Feel) {
      return (
        Math.max(
          FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
          ...[expression.expression].map((expression) => getExpressionMinWidth(expression))
        ) + FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else if (expression["@_kind"] === BoxedFunctionKind.Java) {
      return (
        JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
        JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH +
        JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else if (expression["@_kind"] === BoxedFunctionKind.Pmml) {
      return (
        PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
        PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH +
        PMML_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else {
      throw new Error("Should never get here");
    }
  }

  // Relation + DecisionTable
  else if (expression.__$$element === "relation") {
    return (
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + //
      (expression.column?.length ?? 0) * RELATION_EXPRESSION_COLUMN_MIN_WIDTH
    );
  } else if (expression.__$$element === "decisionTable") {
    return (
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
      (expression.input?.length ?? 0) * DECISION_TABLE_INPUT_MIN_WIDTH +
      (expression.output?.length ?? 0) * DECISION_TABLE_OUTPUT_MIN_WIDTH +
      (expression.annotation?.length ?? 0) * DECISION_TABLE_ANNOTATION_MIN_WIDTH
    );
  }

  // List
  else if (expression.__$$element === "list") {
    return (
      Math.max(
        LIST_EXPRESSION_ITEM_MIN_WIDTH,
        ...(expression.expression ?? []).map((expression) => getExpressionMinWidth(expression))
      ) + LIST_EXPRESSION_EXTRA_WIDTH
    );
  }

  // Conditional
  else if (expression.__$$element === "conditional") {
    const nestedExpressions = [expression.if.expression, expression.then.expression, expression.else.expression];
    return (
      CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH +
      Math.max(
        CONDITIONAL_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH,
        ...nestedExpressions.map((e) => getExpressionMinWidth(e))
      ) +
      CONDITIONAL_EXPRESSION_EXTRA_WIDTH
    );
  }

  // Others
  else {
    throw new Error("Shouldn't ever reach this point");
  }
}

export function getWidth(id: string | undefined, widthsById: Map<string, number[]>) {
  const widths = widthsById.get(id ?? "");
  if (widths && widths.length > 0) {
    return widths[0];
  } else {
    return undefined;
  }
}

/**
 * This function goes recursively through all `expression`'s nested expressions and sums either `entryInfoWidth` or
 * default minimal width, returned by `getExpressionMinWidth`, if it is the last nested expression in the chain.
 *
 * This function returns maximal sum found in all `expression`'s nested expressions.
 */
export function getExpressionTotalMinWidth(
  currentWidth: number,
  expression: BoxedExpression | undefined,
  widthsById: Map<string, number[]>
): number {
  if (!expression) {
    return 0;
  }

  if (expression.__$$element === "context") {
    const width = currentWidth + (getWidth(expression["@_id"], widthsById) ?? 0);
    const contextEntriesMaxWidth = (expression.contextEntry ?? []).reduce((maxWidth, currentExpression) => {
      return Math.max(maxWidth, getExpressionTotalMinWidth(width, currentExpression.expression, widthsById));
    }, width);
    const result = expression.contextEntry?.find((e) => !e.variable);
    const resultWidth = result ? getExpressionTotalMinWidth(width, result.expression, widthsById) : 0;
    return Math.max(contextEntriesMaxWidth, resultWidth);
  } else if (expression.__$$element === "invocation") {
    const width = currentWidth + (getWidth(expression["@_id"], widthsById) ?? 0);
    return (expression.binding ?? []).reduce((maxWidth, currentExpression) => {
      return Math.max(maxWidth, getExpressionTotalMinWidth(width, currentExpression.expression, widthsById));
    }, width);
  } else if (expression.__$$element === "conditional") {
    const width = currentWidth + (getWidth(expression["@_id"], widthsById) ?? 0);
    return [expression.if.expression, expression.then.expression, expression.else.expression].reduce(
      (maxWidth, currentExpression) => {
        return Math.max(maxWidth, getExpressionTotalMinWidth(width, currentExpression, widthsById));
      },
      width
    );
  } else {
    // it is an expression without entryInfoWidth
    return currentWidth + getExpressionMinWidth(expression);
  }
}

function getWidthAt(index: number, widths?: number[]): number | undefined {
  if (!widths || widths.length <= index) {
    return undefined;
  }
  return widths[index];
}

export function getExpressionResizingWidth(
  expression: BoxedExpression | undefined,
  resizingWidths: Map<string, ResizingWidth>,
  widthsById: Map<string, number[]>
): number {
  if (!expression) {
    return getExpressionMinWidth(expression);
  }

  const resizingWidth = resizingWidths.get(expression["@_id"]!)?.value;

  // Literal
  if (expression.__$$element === "literalExpression") {
    return (
      (resizingWidth ?? getWidth(expression["@_id"], widthsById) ?? LITERAL_EXPRESSION_MIN_WIDTH) +
      LITERAL_EXPRESSION_EXTRA_WIDTH
    );
  }

  // Relation + DecisionTable
  else if (expression.__$$element === "relation") {
    const columns = expression.column ?? [];

    const expressionWidth = widthsById.get(expression["@_id"]!);

    return (
      resizingWidth ??
      columns.reduce((acc, c, currentIndex) => {
        return acc + (getWidthAt(currentIndex + 1, expressionWidth) ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH);
      }, BEE_TABLE_ROW_INDEX_COLUMN_WIDTH)
    );
  } else if (expression.__$$element === "decisionTable") {
    const columns = [...(expression.input ?? []), ...(expression.output ?? []), ...(expression.annotation ?? [])];
    const expressionWidth = widthsById.get(expression["@_id"]!);
    return (
      resizingWidth ??
      columns.reduce((acc, c, currentIndex) => {
        return acc + (getWidthAt(currentIndex + 1, expressionWidth) ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH);
      }, BEE_TABLE_ROW_INDEX_COLUMN_WIDTH)
    );
  }

  // Context + Invocation
  else if (expression.__$$element === "context") {
    const result = expression.contextEntry?.find((e) => !e.variable);
    const nestedExpressions = [...(expression.contextEntry ?? []).map((e) => e.expression), result?.expression];
    return (
      resizingWidth ??
      (getWidth(expression["@_id"], widthsById) ?? CONTEXT_ENTRY_VARIABLE_MIN_WIDTH) +
        Math.max(
          CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
          ...nestedExpressions.map((e) => getExpressionResizingWidth(e, resizingWidths, widthsById))
        ) +
        CONTEXT_EXPRESSION_EXTRA_WIDTH
    );
  } else if (expression.__$$element === "invocation") {
    const nestedExpressions = (expression.binding ?? []).map((e) => e.expression);
    return (
      resizingWidth ??
      (getWidth(expression["@_id"], widthsById) ?? INVOCATION_PARAMETER_MIN_WIDTH) +
        Math.max(
          INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH,
          ...nestedExpressions.map((e) => getExpressionResizingWidth(e, resizingWidths, widthsById))
        ) +
        INVOCATION_EXTRA_WIDTH
    );
  }

  // Function
  else if (expression.__$$element === "functionDefinition") {
    if (expression["@_kind"] === BoxedFunctionKind.Feel) {
      return (
        resizingWidth ??
        Math.max(
          FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
          ...[expression.expression].map((expression) =>
            getExpressionResizingWidth(expression, resizingWidths, widthsById)
          )
        ) + FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else if (expression["@_kind"] === BoxedFunctionKind.Java) {
      return (
        resizingWidth ??
        JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
          (getWidthAt(2, widthsById.get(expression["@_id"]!)) ?? JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH) +
          JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH
      );
    } else if (expression["@_kind"] === BoxedFunctionKind.Pmml) {
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
  else if (expression.__$$element === "list") {
    return (
      resizingWidth ??
      Math.max(
        LIST_EXPRESSION_ITEM_MIN_WIDTH,
        ...(expression.expression ?? []).map((expression) =>
          getExpressionResizingWidth(expression, resizingWidths, widthsById)
        )
      ) + LIST_EXPRESSION_EXTRA_WIDTH
    );
  }

  // Conditional
  else if (expression.__$$element === "conditional") {
    const nestedExpressions = [expression.if.expression, expression.then.expression, expression.else.expression];
    return (
      resizingWidth ??
      CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH +
        Math.max(
          CONDITIONAL_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH,
          ...nestedExpressions.map((e) => getExpressionResizingWidth(e, resizingWidths, widthsById))
        ) +
        CONDITIONAL_EXPRESSION_EXTRA_WIDTH
    );
  }

  // Others
  else {
    throw new Error(`Resize unknown for expression of type ${expression.__$$element}`);
    //return resizingWidth ?? DEFAULT_MIN_WIDTH;
  }
}
