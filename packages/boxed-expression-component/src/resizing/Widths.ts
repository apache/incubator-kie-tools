import { ExpressionDefinitionLogicType } from "../api";
import { ExpressionDefinition, FunctionExpressionDefinitionKind } from "../api/ExpressionDefinition";
import { ResizingWidth } from "../resizing/ResizingWidthsContext";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_EXTRA_WIDTH,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  DECISION_TABLE_ANNOTATION_MIN_WIDTH,
  DECISION_TABLE_INPUT_MIN_WIDTH,
  DECISION_TABLE_OUTPUT_MIN_WIDTH,
  DEFAULT_MIN_WIDTH,
  LITERAL_EXPRESSION_MIN_WIDTH,
  NESTED_EXPRESSION_RESET_MARGIN,
  RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
} from "./WidthValues";

export function getExpressionMinWidth(expression?: ExpressionDefinition): number {
  if (!expression) {
    return DEFAULT_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Context) {
    return (
      Math.max(
        CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
        ...expression.contextEntries.map(({ entryExpression }) => getExpressionMinWidth(entryExpression)),
        getExpressionMinWidth(expression.result)
      ) +
      CONTEXT_ENTRY_INFO_MIN_WIDTH +
      CONTEXT_ENTRY_EXTRA_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.Literal) {
    return LITERAL_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Function) {
    if (expression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH - 1; // 1px for the missing entry info border
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH * 2 - 1;
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH * 2 - 1;
    } else {
      throw new Error("Should never get here");
    }
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return (
      (expression.columns?.length ?? 0) * (RELATION_EXPRESSION_COLUMN_MIN_WIDTH + 1) +
      1 + // last-child border-right
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
      NESTED_EXPRESSION_RESET_MARGIN
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return (
      Math.max(
        CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
        ...(expression.items ?? []).map((expression) => getExpressionMinWidth(expression))
      ) +
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
      NESTED_EXPRESSION_RESET_MARGIN
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    return (
      CONTEXT_ENTRY_INFO_MIN_WIDTH +
      Math.max(
        CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
        ...(expression.bindingEntries ?? []).map(({ entryExpression }) => getExpressionMinWidth(entryExpression))
      ) +
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
      NESTED_EXPRESSION_RESET_MARGIN
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    return (
      (expression.input?.length ?? 0) * (DECISION_TABLE_INPUT_MIN_WIDTH + 1) +
      1 + // last-child border-right
      (expression.output?.length ?? 0) * (DECISION_TABLE_OUTPUT_MIN_WIDTH + 1) +
      1 + // last-child border-right
      (expression.annotations?.length ?? 0) * (DECISION_TABLE_ANNOTATION_MIN_WIDTH + 1) +
      1 + // last-child border-right
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
      NESTED_EXPRESSION_RESET_MARGIN
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.PmmlLiteral) {
    return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
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

  const resizingWidth = resizingWidths.get(expression.id!)?.value;

  if (expression.logicType === ExpressionDefinitionLogicType.Context) {
    return (
      resizingWidth ??
      (expression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) +
        Math.max(
          CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
          ...[...expression.contextEntries.map((e) => e.entryExpression), expression.result].map((e) =>
            getExpressionResizingWidth(e, resizingWidths)
          )
        ) +
        CONTEXT_ENTRY_EXTRA_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.Literal) {
    return (resizingWidth ?? expression.width ?? LITERAL_EXPRESSION_MIN_WIDTH) + NESTED_EXPRESSION_RESET_MARGIN;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Function) {
    if (expression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      return getExpressionResizingWidth(expression.expression, resizingWidths) + CONTEXT_ENTRY_EXTRA_WIDTH - 1 + 1; // 1px for the missing entry info border, 1px for last-child border-right
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH * 2 - 1;
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH * 2 - 1;
    } else {
      throw new Error("Should never get here");
    }
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return (
      resizingWidth ??
      (expression.columns ?? []).reduce(
        (acc, { width }) => acc + (width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH),
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + NESTED_EXPRESSION_RESET_MARGIN + (expression.columns?.length ?? 0) + 1 // last-child border-right
      )
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    const columns = [...(expression.input ?? []), ...(expression.output ?? []), ...(expression.annotations ?? [])];
    return (
      resizingWidth ??
      columns.reduce(
        (acc, c) => acc + (c.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH),
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + NESTED_EXPRESSION_RESET_MARGIN + columns.length + 1
      )
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return (
      resizingWidth ??
      Math.max(
        CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
        ...(expression.items ?? []).map((expression) => getExpressionResizingWidth(expression, resizingWidths))
      ) +
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
        NESTED_EXPRESSION_RESET_MARGIN
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    return (
      resizingWidth ??
      Math.max(
        CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
        ...(expression.bindingEntries ?? []).map((e) => getExpressionResizingWidth(e.entryExpression, resizingWidths))
      ) +
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
        NESTED_EXPRESSION_RESET_MARGIN
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.PmmlLiteral) {
    return resizingWidth ?? CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH;
  } else {
    throw new Error("Shouldn't ever reach this point");
  }
}
