export const DEFAULT_MIN_WIDTH = 100;

export const BEE_TABLE_ROW_INDEX_COLUMN_WIDTH = 60;

// CONTEXT
export const CONTEXT_ENTRY_INFO_MIN_WIDTH = 120;
export const CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH = 210;
export const CONTEXT_EXPRESSION_EXTRA_WIDTH =
  1 + // 1px border-left
  2 + // 1px for each column border-left and border-right
  1; // 1px for contextExpression column border-right

// INVOCATION
export const INVOCATION_PARAMETER_MIN_WIDTH = 120;
export const INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH = 210;
export const INVOCATION_EXTRA_WIDTH =
  1 + // 1px border-left
  2 + // 1px for each contextExpression column border-left and border-right
  1; // 1px for argumentExpression column border-right

// DECISION TABLE
export const DECISION_TABLE_INPUT_MIN_WIDTH = 100;
export const DECISION_TABLE_INPUT_DEFAULT_WIDTH = 100;
export const DECISION_TABLE_OUTPUT_MIN_WIDTH = 100;
export const DECISION_TABLE_OUTPUT_DEFAULT_WIDTH = 150;
export const DECISION_TABLE_ANNOTATION_MIN_WIDTH = 100;
export const DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH = 250;

// RELATION
export const RELATION_EXPRESSION_COLUMN_MIN_WIDTH = 100;
export const RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH = 150;

// LITERAL
export const LITERAL_EXPRESSION_EXTRA_WIDTH =
  20 + // 20px for the equals sign,
  2; //2px for borders left and right.
export const LITERAL_EXPRESSION_MIN_WIDTH = CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH - LITERAL_EXPRESSION_EXTRA_WIDTH;

// LIST
export const LIST_EXPRESSION_EXTRA_WIDTH =
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
  1 + // 1px for border-left of the only column
  1; // 1px for last-child border-right

// FUNCTION
export const FUNCTION_EXPRESSION_EXTRA_WIDTH =
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
  1 + // 1px for border-left of the only column
  1; // 1px for last-child border-right
