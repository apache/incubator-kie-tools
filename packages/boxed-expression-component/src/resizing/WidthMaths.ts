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
  FUNCTION_EXPRESSION_COMMON_EXTRA_WIDTH,
  LIST_EXPRESSION_EXTRA_WIDTH,
  LITERAL_EXPRESSION_EXTRA_WIDTH,
  LITERAL_EXPRESSION_MIN_WIDTH,
  RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
  FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
  LIST_ITEM_EXPRESSION_MIN_WIDTH,
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
      CONTEXT_ENTRY_INFO_MIN_WIDTH +
      Math.max(CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH, ...nestedExpressions.map((e) => getExpressionMinWidth(e))) +
      CONTEXT_EXPRESSION_EXTRA_WIDTH
    );

    // Function
  } else if (expression.logicType === ExpressionDefinitionLogicType.Function) {
    if (expression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      return (
        Math.max(
          FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
          ...[expression.expression].map((expression) => getExpressionMinWidth(expression))
        ) + FUNCTION_EXPRESSION_COMMON_EXTRA_WIDTH
      );
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return (
        JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
        JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH +
        2 + // column borders
        2 + // whole table borders
        FUNCTION_EXPRESSION_COMMON_EXTRA_WIDTH
      );
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return (
        PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
        PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH +
        2 + // column borders
        2 + // whole table borders
        FUNCTION_EXPRESSION_COMMON_EXTRA_WIDTH
      );
    } else {
      throw new Error("Should never get here");
    }

    // Relation + DecisionTable
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return (
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
      (expression.columns?.length ?? 0) * (RELATION_EXPRESSION_COLUMN_MIN_WIDTH + 1) +
      1 // 1px for last-child border-right
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    return (
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
      (expression.input?.length ?? 0) * (DECISION_TABLE_INPUT_MIN_WIDTH + 1) +
      (expression.output?.length ?? 0) * (DECISION_TABLE_OUTPUT_MIN_WIDTH + 1) +
      (expression.annotations?.length ?? 0) * (DECISION_TABLE_ANNOTATION_MIN_WIDTH + 1) +
      1 // 1px for last-child border-right
    );
  }

  // List
  else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return (
      Math.max(
        LIST_ITEM_EXPRESSION_MIN_WIDTH,
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

    return (
      variableWidth +
      columns.length + // 1px for each column
      1 // 1px for last-child border-right
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    const columns = [...(expression.input ?? []), ...(expression.output ?? []), ...(expression.annotations ?? [])];

    const variableWidth =
      resizingWidth ??
      columns.reduce((acc, { width }) => {
        return acc + (width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH);
      }, BEE_TABLE_ROW_INDEX_COLUMN_WIDTH);

    return (
      variableWidth +
      columns.length + // 1px for each column
      1 // 1px for last-child border-right
    );
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
      (expression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) +
        Math.max(
          CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
          ...nestedExpressions.map((e) => getExpressionResizingWidth(e, resizingWidths))
        ) +
        CONTEXT_EXPRESSION_EXTRA_WIDTH
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
        ) + FUNCTION_EXPRESSION_COMMON_EXTRA_WIDTH
      );
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return (
        JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
        (expression.classAndMethodNamesWidth ?? JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH) +
        2 + // column borders
        2 + // value column borders
        FUNCTION_EXPRESSION_COMMON_EXTRA_WIDTH
      );
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return (
        PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH +
        PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH +
        2 + // column borders
        2 + // whole table borders
        FUNCTION_EXPRESSION_COMMON_EXTRA_WIDTH
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
        LIST_ITEM_EXPRESSION_MIN_WIDTH,
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
