/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ExpressionDefinitionLogicType } from "./ExpressionDefinitionLogicType";
import { DmnBuiltInDataType } from "./DmnBuiltInDataType";
import {
  ContextExpressionDefinitionEntry,
  ContextExpressionDefinitionEntryInfo,
} from "./ContextExpressionDefinitionEntry";
import { DecisionTableExpressionDefinitionHitPolicy } from "./DecisionTableExpressionDefinitionHitPolicy";
import { DecisionTableExpressionDefinitionBuiltInAggregation } from "./DecisionTableExpressionDefinitionBuiltInAggregation";
import {
  DecisionTableExpressionDefinitionAnnotation,
  DecisionTableExpressionDefinitionCaluse,
  DecisionTableExpressionDefinitionRule,
} from "./DecisionTableExpressionDefinitionRule";
import {
  FeelFunctionExpressionDefinition,
  JavaFunctionExpressionDefinition,
  PmmlFunctionExpressionDefinition,
} from "./FunctionExpressionDefinitionKind";
import { BeeTableColumn, BeeTableRow } from "./BeeTable";

export interface ExpressionDefinition {
  /** Unique identifier used to identify the expression */
  id?: string;
  /** Expression name (which, in DMN world, is equal to the Decision node's name) */
  name?: string;
  /** Expression data type */
  dataType?: DmnBuiltInDataType;
  /** Optional callback executed to update expression's name and data type */
  onUpdatingNameAndDataType?: (updatedName: string, updatedDataType: DmnBuiltInDataType) => void;
  /** Logic type should not be defined at this stage */
  logicType?: ExpressionDefinitionLogicType;
  /** True, to have no header for this specific expression component, used in a recursive expression */
  isHeadless?: boolean;
  /** When a component is headless, it will call this function to pass its most updated expression definition */
  onUpdatingRecursiveExpression?: (expression: ExpressionDefinition) => void;
  /** True, to have no clear action rendered for this specific expression */
  noClearAction?: boolean;
}

export interface LiteralExpressionDefinition extends ExpressionDefinition {
  /** Logic type must be LiteralExpression */
  logicType: ExpressionDefinitionLogicType.LiteralExpression;
  /** Optional content to display for this literal expression */
  content?: string;
  /** Optional width for this literal expression */
  width?: number;
}

export interface PmmlLiteralExpressionDefinition extends ExpressionDefinition {
  /** Logic type must be PmmlLiteralExpression */
  logicType: ExpressionDefinitionLogicType.PmmlLiteralExpression;
  /** Callback for retrieving the options to provide in the dropdown */
  getOptions: () => string[];
  /** Dropdown's selected option */
  selected?: string;
  /** Label displayed (in italic style) when no options are available */
  noOptionsLabel: string;
  /** Property used for test purposes only */
  testId?: string;
}

export interface RelationExpressionDefinition extends ExpressionDefinition {
  /** Logic type must be Relation */
  logicType: ExpressionDefinitionLogicType.Relation;
  /** Each column has a name and a data type. Their order is from left to right */
  columns?: BeeTableColumn[];
  /** Rows order is from top to bottom. Each row has a collection of cells, one for each column */
  rows?: BeeTableRow[];
}

export interface ContextExpressionDefinition extends ExpressionDefinition {
  /** Logic type must be Context */
  logicType: ExpressionDefinitionLogicType.Context;
  /** Collection of context entries */
  contextEntries?: ContextExpressionDefinitionEntry[];
  /** Context result */
  result?: ExpressionDefinition;
  /** False, to avoid the rendering of the result section */
  renderResult?: boolean;
  /** Entry info width */
  entryInfoWidth?: number;
  /** Entry expression width */
  entryExpressionWidth?: number;
  /** True, to avoid the presence of table menu handler */
  noHandlerMenu?: boolean;
}

export interface DecisionTableExpressionDefinition extends ExpressionDefinition {
  /** Logic type must be Decision Table */
  logicType: ExpressionDefinitionLogicType.DecisionTable;
  /** Hit policy for this particular decision table */
  hitPolicy?: DecisionTableExpressionDefinitionHitPolicy;
  /** Aggregation policy, when the hit policy supports it */
  aggregation?: DecisionTableExpressionDefinitionBuiltInAggregation;
  /** Annotation columns names */
  annotations?: DecisionTableExpressionDefinitionAnnotation[];
  /** Input columns definition */
  input?: DecisionTableExpressionDefinitionCaluse[];
  /** Output columns definition */
  output?: DecisionTableExpressionDefinitionCaluse[];
  /** Rules represent rows values */
  rules?: DecisionTableExpressionDefinitionRule[];
}

export interface ListExpressionDefinition extends ExpressionDefinition {
  /** Logic type must be List */
  logicType: ExpressionDefinitionLogicType.List;
  /** List items */
  items?: ExpressionDefinition[];
  /** Optional width for this list expression */
  width?: number;
}

export interface InvocationExpressionDefinition<T = ExpressionDefinition> extends ExpressionDefinition {
  /** Logic type must be Invocation */
  logicType: ExpressionDefinitionLogicType.Invocation;
  /** Function to be invoked */
  invokedFunction?: string;
  /** Collection of parameters used to invoke the function */
  bindingEntries?: ContextExpressionDefinitionEntry<T>[];
  /** Entry info width */
  entryInfoWidth?: number;
  /** Entry expression width */
  entryExpressionWidth?: number;
}

export type FunctionExpressionDefinition = ExpressionDefinition & {
  /** Logic type must be Function */
  logicType: ExpressionDefinitionLogicType.Function;
  /** List of parameters passed to the function */
  formalParameters?: ContextExpressionDefinitionEntryInfo[];
  /** Parameters column width */
  parametersWidth?: number;
} & (FeelFunctionExpressionDefinition | JavaFunctionExpressionDefinition | PmmlFunctionExpressionDefinition);
