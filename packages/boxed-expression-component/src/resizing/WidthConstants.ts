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

export const DEFAULT_MIN_WIDTH = 100;

export const BEE_TABLE_ROW_INDEX_COLUMN_WIDTH = 60;

// CONTEXT
export const CONTEXT_ENTRY_VARIABLE_MIN_WIDTH = 120;
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
export const DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH = 240;

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

export const CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH = 80;
export const CONDITIONAL_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH = 210;
export const CONDITIONAL_EXPRESSION_EXTRA_WIDTH = 2; // 2px for borders of context entry expression // It's a mistery why to this cell is counting the borders.
