export const DEFAULT_MIN_WIDTH = 100;

export const BEE_TABLE_ROW_INDEX_COLUMN_WIDTH = 60;

// CONTEXT
export const CONTEXT_ENTRY_INFO_MIN_WIDTH = 120;
export const CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH = 210;
export const CONTEXT_EXPRESSION_EXTRA_WIDTH = 2; // 2px for borders of context entry expression // It's a mistery why to this cell is counting the borders.

// INVOCATION
export const INVOCATION_PARAMETER_MIN_WIDTH = 120;
export const INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH = 210;
export const INVOCATION_EXTRA_WIDTH = 2; // 2px for borders of context entry expression // It's a mistery why to this cell is counting the borders.

// DECISION TABLE
export const DECISION_TABLE_INPUT_MIN_WIDTH = 100;
export const DECISION_TABLE_INPUT_DEFAULT_WIDTH = 100;
export const DECISION_TABLE_OUTPUT_MIN_WIDTH = 100;
export const DECISION_TABLE_OUTPUT_DEFAULT_WIDTH = 100;
export const DECISION_TABLE_ANNOTATION_MIN_WIDTH = 100;
export const DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH = 100;

// RELATION
export const RELATION_EXPRESSION_COLUMN_MIN_WIDTH = 100;
export const RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH = 100;

// LITERAL
export const LITERAL_EXPRESSION_EXTRA_WIDTH = 20; // 20px for the equals sign

export const LITERAL_EXPRESSION_MIN_WIDTH = CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH - LITERAL_EXPRESSION_EXTRA_WIDTH;

// LIST
export const LIST_EXPRESSION_ITEM_MIN_WIDTH = 210;
export const LIST_EXPRESSION_EXTRA_WIDTH =
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + //
  2; // 2px for borders of context entry expression // It's a mistery why to this cell is counting the borders.

export const FEEL_FUNCTION_EXPRESSION_MIN_WIDTH = 210;
export const FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH = BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + 2; // 2px for borders of context entry expression // It's a mistery why to this cell is counting the borders.

export const PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH = 210;
export const PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH = 140;
export const PMML_FUNCTION_EXPRESSION_EXTRA_WIDTH = BEE_TABLE_ROW_INDEX_COLUMN_WIDTH;

export const JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH = 210;
export const JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH = 140;

export const JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH = BEE_TABLE_ROW_INDEX_COLUMN_WIDTH;
