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

import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";

declare global {
  // Set of Functions used to interact with the GWT Layer. Must be synchronized with ExpressionEditorService.java
  interface BeeApi {
    // It requests to the GWT layer the default GwtExpressionDefinition given a selected logic type and a data type.
    getDefaultExpressionDefinition: (
      logicType: GwtExpressionDefinitionLogicType,
      dataType: string
    ) => GwtExpressionDefinition;

    // It Navigates to "Data Type" tab page
    openDataTypePage: () => void;

    // Notifies that an object was selected.
    selectObject: (uuid?: string) => void;

    // It propagates to the GWT layer the modified expression. It MUTATES the GWT layer status.
    updateExpression: (expression: GwtExpressionDefinition) => void;
  }

  // API that the containing component of BoxedExpressionEditor (BEE) expects to be defined in the Window namespace
  interface Window {
    beeApiWrapper: BeeApi;
  }
}

export enum GwtExpressionDefinitionLogicType {
  Undefined = "<Undefined>",
  Literal = "Literal",
  Context = "Context",
  DecisionTable = "Decision table",
  Relation = "Relation",
  Function = "Function",
  Invocation = "Invocation",
  List = "List",
}

export interface ExpressionDefinitionBase {
  /** Unique identifier used to identify the expression */
  id: string;
  /** Expression name (which, in DMN world, is equal to the Decision node's name) */
  name?: string;
  /** Expression data type */
  dataType: DmnBuiltInDataType;
}

// LITERAL EXPRESSION

export interface LiteralExpressionDefinition extends ExpressionDefinitionBase {
  /** Logic type must be LiteralExpression */
  logicType: GwtExpressionDefinitionLogicType.Literal;
  /** Optional content to display for this literal expression */
  content?: string;
  /** Optional width for this literal expression */
  width?: number;
}

// RELATION EXPRESSION

export interface RelationExpressionDefinition extends ExpressionDefinitionBase {
  /** Logic type must be Relation */
  logicType: GwtExpressionDefinitionLogicType.Relation;
  /** Each column has a name and a data type. Their order is from left to right */
  columns?: RelationExpressionDefinitionColumn[];
  /** Rows order is from top to bottom. Each row has a collection of cells, one for each column */
  rows?: RelationExpressionDefinitionRow[];
}

export interface RelationExpressionDefinitionRow {
  /** Row identifier */
  id: string;
  /** Cells */
  cells: RelationExpressionDefinitionCell[];
}

export interface RelationExpressionDefinitionCell {
  /** Relation identifier */
  id: string;
  /** The content of the relation */
  content: string;
}

export interface RelationExpressionDefinitionColumn {
  /** Column identifier */
  id: string;
  /** Column name */
  name: string;
  /** Column data type */
  dataType: DmnBuiltInDataType;
  /** Column width */
  width: number | undefined;
}

// CONTEXT EXPRESSION

export interface ContextExpressionDefinition extends ExpressionDefinitionBase {
  /** Logic type must be Context */
  logicType: GwtExpressionDefinitionLogicType.Context;
  /** Collection of context entries */
  contextEntries: ContextExpressionDefinitionEntry[];
  /** Context result */
  result: GwtExpressionDefinition;
  /** Entry info width */
  entryInfoWidth?: number;
}

export interface ContextExpressionDefinitionEntryInfo {
  /** Entry id */
  id: string;
  /** Entry name */
  name: string;
  /** Entry data type */
  dataType: DmnBuiltInDataType;
}

export interface ContextExpressionDefinitionEntry<T extends GwtExpressionDefinition = GwtExpressionDefinition> {
  entryInfo: ContextExpressionDefinitionEntryInfo;
  /** Entry expression */
  entryExpression: T;
}

// DECISION TABLE EXPRESSION

export interface DecisionTableExpressionDefinition extends ExpressionDefinitionBase {
  /** Logic type must be Decision table */
  logicType: GwtExpressionDefinitionLogicType.DecisionTable;
  /** Hit policy for this particular Decision table */
  hitPolicy: DecisionTableExpressionDefinitionHitPolicy;
  /** Aggregation policy, when the hit policy supports it */
  aggregation: DecisionTableExpressionDefinitionBuiltInAggregation;
  /** Annotation columns names */
  annotations?: DecisionTableExpressionDefinitionAnnotation[];
  /** Input columns definition */
  input?: DecisionTableExpressionDefinitionInputClause[];
  /** Output columns definition */
  output?: DecisionTableExpressionDefinitionOutputClause[];
  /** Rules represent rows values */
  rules?: DecisionTableExpressionDefinitionRule[];
}

export enum DecisionTableExpressionDefinitionBuiltInAggregation {
  "<None>" = "?",
  SUM = "+",
  COUNT = "#",
  MIN = "<",
  MAX = ">",
}

export enum DecisionTableExpressionDefinitionHitPolicy {
  Unique = "UNIQUE",
  First = "FIRST",
  Priority = "PRIORITY",
  Collect = "COLLECT",
  Any = "ANY",
  RuleOrder = "RULE ORDER",
  OutputOrder = "OUTPUT ORDER",
}

export interface DecisionTableExpressionDefinitionClause {
  /** Clause identifier */
  id: string;
  /** Clause name */
  name: string;
  /** Clause data type */
  dataType: DmnBuiltInDataType;
  /** Clause Unary Tests */
  clauseUnaryTests?: DecisionTableExpressionDefinitionClauseUnaryTests;
  /** Clause width */
  width: number | undefined;
}

export interface DecisionTableExpressionDefinitionInputClause extends DecisionTableExpressionDefinitionClause {
  /** Clause Literal Expression identifier */
  idLiteralExpression: string;
}

export interface DecisionTableExpressionDefinitionOutputClause extends DecisionTableExpressionDefinitionClause {
  /**
   * defaultOutputEntry: Expression [0..1]
   * In an Incomplete table, this attribute lists an instance of Expression that is selected when no rules match
   * for the decision table, which is also an instance of LiteralExpression
   */
  defaultOutputEntry?: GwtExpressionDefinition;
}

export interface DecisionTableExpressionDefinitionClauseUnaryTests {
  /** ClauseUnaryTests identifier */
  id: string;
  /** ClauseUnaryTests name */
  text: string;
  /** ClauseUnaryTests constraint type */
  constraintType: string;
}

export interface DecisionTableExpressionDefinitionAnnotation {
  /** Annotation name */
  name: string;
  /** Annotation width */
  width: number | undefined;
}

export interface DecisionTableExpressionDefinitionRuleEntry {
  /** Clause identifier */
  id: string;
  /** The content of the clause */
  content: string;
}

export interface DecisionTableExpressionDefinitionRule {
  /** Rule identifier */
  id: string;
  /** Values for the input columns */
  inputEntries: DecisionTableExpressionDefinitionRuleEntry[];
  /** Values for the output columns */
  outputEntries: DecisionTableExpressionDefinitionRuleEntry[];
  /** Values for the annotation columns */
  annotationEntries: string[];
}

export interface ListExpressionDefinition extends ExpressionDefinitionBase {
  /** Logic type must be List */
  logicType: GwtExpressionDefinitionLogicType.List;
  /** List items */
  items: GwtExpressionDefinition[];
}

export interface InvocationExpressionDefinition<T extends GwtExpressionDefinition = GwtExpressionDefinition>
  extends ExpressionDefinitionBase {
  /** Logic type must be Invocation */
  logicType: GwtExpressionDefinitionLogicType.Invocation;
  /** Function to be invoked */
  invokedFunction: InvocationFunction;
  /** Collection of arguments used to invoke the function */
  bindingEntries: ContextExpressionDefinitionEntry<T>[]; // Please rename to `argumentEntries` as part of https://github.com/apache/incubator-kie-issues/issues/169. Make sure to update other places that untypedly reference it too!
  /** Entry info width */
  entryInfoWidth?: number; // Please rename to `parametersInfoColumnWidth` as part of https://github.com/apache/incubator-kie-issues/issues/169. Make sure to update other places that untypedly reference it too!
}

export interface InvocationFunction {
  id: string;
  name: string;
}

// UNDEFINED EXPRESSION

export interface UndefinedExpressionDefinition extends ExpressionDefinitionBase {
  logicType: GwtExpressionDefinitionLogicType.Undefined;
}

// FUNCTION EXPRESSION

export enum FunctionExpressionDefinitionKind {
  Feel = "FEEL",
  Java = "Java",
  Pmml = "PMML",
}

export interface FunctionExpressionDefinitionBase extends ExpressionDefinitionBase {
  /** Logic type must be Function */
  logicType: GwtExpressionDefinitionLogicType.Function;
  /** List of parameters passed to the function */
  formalParameters: ContextExpressionDefinitionEntryInfo[];
}

export type FeelFunctionExpressionDefinition = FunctionExpressionDefinitionBase & {
  /** Feel Function */
  functionKind: FunctionExpressionDefinitionKind.Feel;
  /** The Expression related to the function */
  expression: GwtExpressionDefinition;
};

export type PmmlFunctionExpressionDefinition = FunctionExpressionDefinitionBase & {
  /** Pmml Function */
  functionKind: FunctionExpressionDefinitionKind.Pmml;
  /** Selected PMML document */
  document?: string;
  /** Selected PMML model */
  model?: string;
  /** Document dropdown field identifier */
  documentFieldId?: string;
  /** Model dropdown field identifier */
  modelFieldId?: string;
};

export type JavaFunctionExpressionDefinition = FunctionExpressionDefinitionBase & {
  /** Java Function */
  functionKind: FunctionExpressionDefinitionKind.Java;
  /** Java class */
  className?: string;
  /** Method signature */
  methodName?: string;
  /** Class text field identifier */
  classFieldId?: string;
  /** Method text field identifier */
  methodFieldId?: string;
  /** Width for the column with Java className and methodName strings */
  classAndMethodNamesWidth?: number;
};

export type FunctionExpressionDefinition =
  | FeelFunctionExpressionDefinition
  | PmmlFunctionExpressionDefinition
  | JavaFunctionExpressionDefinition;

// ALL

export type GwtExpressionDefinition =
  | LiteralExpressionDefinition
  | RelationExpressionDefinition
  | ContextExpressionDefinition
  | DecisionTableExpressionDefinition
  | ListExpressionDefinition
  | InvocationExpressionDefinition
  | UndefinedExpressionDefinition
  | FunctionExpressionDefinition;
