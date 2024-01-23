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

import {
  DMN15__tConditional,
  DMN15__tContext,
  DMN15__tDecisionTable,
  DMN15__tFilter,
  DMN15__tFor,
  DMN15__tFunctionDefinition,
  DMN15__tInformationItem,
  DMN15__tInputClause,
  DMN15__tInvocation,
  DMN15__tList,
  DMN15__tLiteralExpression,
  DMN15__tOutputClause,
  DMN15__tQuantified,
  DMN15__tRelation,
  DMN15__tUnaryTests,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { AllExpressionTypes, AllExpressions, AllExpressionsWithoutTypes } from "../dataTypes/DataTypeSpec";

interface PathType {
  type: AllExpressionTypes;
}

interface LiteralExpressionPath extends PathType {
  type: "literalExpression";
}

interface ContextExpressionPath extends PathType {
  type: "context";
  row: number;
  column: "variable" | "expression";
}

interface DecisionTableHeaderPath extends PathType {
  type: "decisionTable";
  header: "input" | "output";
  row: number;
  column: number;
}

interface RelationExpressionPath extends PathType {
  type: "relation";
  row: number;
  column: number;
}

interface InvocationExpressionPath extends PathType {
  type: "invocation";
  row: number;
  column: "parameter" | "expression";
}

interface ListExpressionPath extends PathType {
  type: "list";
  row: number;
}

interface FunctionDefinitionExpressionPath extends PathType {
  type: "functionDefinition";
  parameterIndex: number;
}

interface ForExpressionPath extends PathType {
  type: "for";
  row: "in" | "return";
}

interface EveryExpressionPath extends PathType {
  type: "every";
  row: "in" | "statisfies";
}

interface SomeExpressionPath extends PathType {
  type: "some";
  row: "in" | "statisfies";
}

interface ConditionalExpressionPath extends PathType {
  type: "conditional";
  row: "if" | "else" | "then";
}

interface FilterExpressionPath extends PathType {
  type: "filter";
  row: "in" | "match";
}

export type ExpressionPath =
  | LiteralExpressionPath
  | ContextExpressionPath
  | DecisionTableHeaderPath
  | RelationExpressionPath
  | InvocationExpressionPath
  | ListExpressionPath
  | FunctionDefinitionExpressionPath
  | ForExpressionPath
  | EveryExpressionPath
  | SomeExpressionPath
  | ConditionalExpressionPath
  | FilterExpressionPath;

/**
 * A map of "@_id" to cell (expression) and its path in the expression hierarchy
 */
type BeeMap = Map<string, { expressionPath: ExpressionPath[]; cell: AllExpressionsWithoutTypes }>;

export function generateBeeMap(
  expression: AllExpressions,
  parentMap: BeeMap,
  parentExpressionPath: ExpressionPath[]
): BeeMap {
  const map: BeeMap = parentMap ? parentMap : new Map();
  switch (expression?.__$$element) {
    case "literalExpression":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "literalExpression" }],
          cell: expression,
        });
      return map;
    case "invocation":
      expression.binding?.forEach((b, row) => {
        b.parameter["@_id"] &&
          map.set(b.parameter["@_id"], {
            expressionPath: [...parentExpressionPath, { type: "invocation", row, column: "parameter" }],
            cell: b.parameter,
          });
        b.expression &&
          generateBeeMap(b.expression, map, [
            ...parentExpressionPath,
            { type: "invocation", row, column: "expression" },
          ]);
      });
      expression.expression;
      expression.expression &&
        expression.expression["@_id"] &&
        map.set(expression.expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "invocation", row: -1, column: "expression" }],
          cell: expression.expression,
        });
      return map;
    case "decisionTable":
      expression.output.forEach(
        (o, column) =>
          o["@_id"] &&
          map.set(o["@_id"], {
            expressionPath: [...parentExpressionPath, { type: "decisionTable", header: "output", row: -1, column }],
            cell: o,
          })
      );
      expression.input?.forEach((i, column) => {
        i["@_id"] &&
          map.set(i["@_id"], {
            expressionPath: [...parentExpressionPath, { type: "decisionTable", header: "input", row: -1, column }],
            cell: i,
          });
      });
      expression.rule?.forEach((r, row) => {
        r.outputEntry?.forEach(
          (ro, column) =>
            ro["@_id"] &&
            map.set(ro["@_id"], {
              expressionPath: [...parentExpressionPath, { type: "decisionTable", header: "output", row, column }],
              cell: ro,
            })
        );
        r.inputEntry?.forEach(
          (ri, column) =>
            ri["@_id"] &&
            map.set(ri["@_id"], {
              expressionPath: [...parentExpressionPath, { type: "decisionTable", header: "input", row, column }],
              cell: ri,
            })
        );
      });
      return map;
    case "context":
      expression.contextEntry?.forEach((ce, row) => {
        ce.variable?.["@_id"] &&
          map.set(ce.variable["@_id"], {
            expressionPath: [...parentExpressionPath, { type: "context", row, column: "variable" }],
            cell: ce.variable,
          });
        generateBeeMap(ce.expression, map, [...parentExpressionPath, { type: "context", row, column: "expression" }]);
      });
      return map;
    case "functionDefinition":
      if (expression.expression?.["@_id"]) {
        generateBeeMap(expression.expression, map, [
          ...parentExpressionPath,
          { type: "functionDefinition", parameterIndex: -1 },
        ]);
      }
      expression.formalParameter?.forEach((fp, parameterIndex) => {
        fp["@_id"] &&
          map.set(fp["@_id"], {
            expressionPath: [...parentExpressionPath, { type: "functionDefinition", parameterIndex: parameterIndex }],
            cell: fp,
          });
      });
      return map;
    case "relation":
      expression.column?.forEach(
        (c, column) =>
          c["@_id"] &&
          map.set(c["@_id"], {
            expressionPath: [...parentExpressionPath, { type: "relation", row: -1, column }],
            cell: c,
          })
      );
      expression.row?.forEach((r, row) =>
        r.expression?.forEach((re, column) => {
          re["@_id"] &&
            map.set(re["@_id"], {
              expressionPath: [...parentExpressionPath, { type: "relation", row, column }],
              cell: r,
            });
        })
      );
      return map;
    case "list":
      expression.expression?.forEach((e, row) =>
        generateBeeMap(e, map, [...parentExpressionPath, { type: "list", row }])
      );
      return map;
    case "for":
      generateBeeMap(expression.in.expression, map, [...parentExpressionPath, { type: "for", row: "in" }]);
      generateBeeMap(expression.return.expression, map, [...parentExpressionPath, { type: "for", row: "return" }]);
      return map;
    case "every":
      generateBeeMap(expression.in.expression, map, [...parentExpressionPath, { type: "every", row: "in" }]);
      generateBeeMap(expression.satisfies.expression, map, [
        ...parentExpressionPath,
        { type: "every", row: "statisfies" },
      ]);
      return map;
    case "some":
      generateBeeMap(expression.in.expression, map, [...parentExpressionPath, { type: "some", row: "in" }]);
      generateBeeMap(expression.satisfies.expression, map, [
        ...parentExpressionPath,
        { type: "some", row: "statisfies" },
      ]);
      return map;
    case "conditional":
      generateBeeMap(expression.if.expression, map, [...parentExpressionPath, { type: "conditional", row: "if" }]);
      generateBeeMap(expression.else.expression, map, [...parentExpressionPath, { type: "conditional", row: "else" }]);
      generateBeeMap(expression.then.expression, map, [...parentExpressionPath, { type: "conditional", row: "then" }]);
      return map;
    case "filter":
      generateBeeMap(expression.in.expression, map, [...parentExpressionPath, { type: "filter", row: "in" }]);
      generateBeeMap(expression.match.expression, map, [...parentExpressionPath, { type: "filter", row: "match" }]);
      return map;
  }
}

export enum BeePropertiesPanelComponent {
  DECISION_TABLE_INPUT_HEADER,
  DECISION_TABLE_OUTPUT_HEADER,
  DECISION_TABLE_ROOT,
  EXPRESSION_ROOT,
  FUNCTION_DEFINITION_PARAMETERS,
  FUNCTION_DEFINITION_ROOT,
  INFORMATION_ITEM_CELL,
  LITERAL_EXPRESSION_CONTENT,
  UNARY_TEST,
}

interface CellIdentification {
  type: AllExpressionTypes;
  cell: BeePropertiesPanelComponent;
  content: any;
}

// CONTEXT - START
type ContextExpressionCells = ContextExpressionRootCell | ContextExpressionVariableCell;
interface ContextExpressionRootCell extends CellIdentification {
  type: "context";
  cell: BeePropertiesPanelComponent.EXPRESSION_ROOT;
  content: Pick<DMN15__tContext, "@_label" | "description">;
}
interface ContextExpressionVariableCell extends CellIdentification {
  type: "context";
  cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL;
  content: Pick<DMN15__tInformationItem, "@_label" | "@_name" | "@_typeRef" | "description">;
}
// CONTEXT - END

// DECISION TABLE - START
type DecisionTableCells =
  | DecisionTableInputHeaderCell
  | DecisionTableInputRuleCell
  | DecisionTableOutputHeaderCell
  | DecisionTableOutputRuleCell
  | DecisionTableRootCell;

interface DecisionTableInputHeaderCell extends CellIdentification {
  type: "decisionTable";
  cell: BeePropertiesPanelComponent.DECISION_TABLE_INPUT_HEADER;
  content: Pick<DMN15__tInputClause, "@_label" | "description"> & {
    inputExpression: Pick<
      DMN15__tLiteralExpression,
      "@_expressionLanguage" | "@_label" | "description" | "text" | "@_typeRef"
    >;
    inputValues: Pick<
      DMN15__tUnaryTests,
      "@_expressionLanguage" | "@_kie:constraintType" | "@_label" | "description" | "text"
    >;
  };
}

interface DecisionTableInputRuleCell extends CellIdentification {
  type: "decisionTable";
  cell: BeePropertiesPanelComponent.UNARY_TEST;
  content: Pick<
    DMN15__tUnaryTests,
    "@_expressionLanguage" | "@_kie:constraintType" | "@_label" | "description" | "text"
  >;
}

interface DecisionTableOutputHeaderCell extends CellIdentification {
  type: "decisionTable";
  cell: BeePropertiesPanelComponent.DECISION_TABLE_OUTPUT_HEADER;
  content: Pick<DMN15__tOutputClause, "@_label" | "@_name" | "@_typeRef" | "description"> & {
    outputValues: Pick<
      DMN15__tUnaryTests,
      "@_expressionLanguage" | "@_kie:constraintType" | "@_label" | "description" | "text"
    >;
    defaultOutputEntry: Pick<DMN15__tLiteralExpression, "@_expressionLanguage" | "@_label" | "description" | "text">;
  };
}

interface DecisionTableOutputRuleCell extends CellIdentification {
  type: "decisionTable";
  cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT;
  content: Pick<DMN15__tLiteralExpression, "@_expressionLanguage" | "@_label" | "description" | "text">;
}

interface DecisionTableRootCell extends CellIdentification {
  type: "decisionTable";
  cell: BeePropertiesPanelComponent.DECISION_TABLE_ROOT;
  content: Pick<DMN15__tDecisionTable, "@_aggregation" | "@_hitPolicy" | "@_label" | "@_outputLabel" | "description">;
}

// DECISION TABLE - END

// FUNCTION - START
type FunctionDefinitionCells = FunctionDefinitionRootCell | FunctionDefinitionParametersCell;

interface FunctionDefinitionRootCell extends CellIdentification {
  type: "functionDefinition";
  cell: BeePropertiesPanelComponent.FUNCTION_DEFINITION_ROOT;
  content: Pick<DMN15__tFunctionDefinition, "@_kind" | "@_label" | "description">;
}

interface FunctionDefinitionParametersCell extends CellIdentification {
  type: "functionDefinition";
  cell: BeePropertiesPanelComponent.FUNCTION_DEFINITION_ROOT;
  content: {
    formalParameter: Array<Pick<DMN15__tInformationItem, "@_label" | "@_name" | "@_typeRef" | "description">>;
  };
}
// FUNCTION - END

// INVOCATION - START
type InvocationExpressionCells =
  | InvocationExpressionRootCell
  | InvocationExpressionCallCell
  | InvocationExpressionParametersCell;
interface InvocationExpressionRootCell extends CellIdentification {
  type: "invocation";
  cell: BeePropertiesPanelComponent.EXPRESSION_ROOT;
  content: Pick<DMN15__tInvocation, "@_label" | "description">;
}
interface InvocationExpressionCallCell extends CellIdentification {
  type: "invocation";
  cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT;
  content: Pick<DMN15__tLiteralExpression, "@_expressionLanguage" | "@_label" | "description" | "text">;
}

interface InvocationExpressionParametersCell extends CellIdentification {
  type: "invocation";
  cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL;
  content: Pick<DMN15__tInformationItem, "@_label" | "@_name" | "@_typeRef" | "description">;
}
// INVOCATION - END

// LITERAL - START
interface LiteralExpressionCells extends CellIdentification {
  type: "literalExpression";
  cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT;
  content: Pick<DMN15__tLiteralExpression, "@_expressionLanguage" | "@_label" | "description" | "text">;
}
// LITERAL - END

// RELATION - START
type RelationExpressionCells =
  | RelationExpressionRootCell
  | RelationExpressionHeaderCell
  | RelationExpressionContentCell;

interface RelationExpressionRootCell extends CellIdentification {
  type: "relation";
  cell: BeePropertiesPanelComponent.EXPRESSION_ROOT;
  content: Pick<DMN15__tRelation, "@_label" | "description">;
}
interface RelationExpressionHeaderCell extends CellIdentification {
  type: "relation";
  cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL;
  content: Pick<DMN15__tInformationItem, "@_label" | "@_name" | "@_typeRef" | "description">;
}

interface RelationExpressionContentCell extends CellIdentification {
  type: "relation";
  cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT;
  content: Pick<DMN15__tLiteralExpression, "@_expressionLanguage" | "@_label" | "description" | "text">;
}

// RELATION - END

// LIST - START
interface ListExpressionCells extends CellIdentification {
  type: "list";
  cell: BeePropertiesPanelComponent.EXPRESSION_ROOT;
  content: Pick<DMN15__tList, "@_label" | "description">;
}
// LIST - END

// For - START
interface ForExpressionCells extends CellIdentification {
  type: "for";
  cell: BeePropertiesPanelComponent.EXPRESSION_ROOT;
  content: Pick<DMN15__tFor, "@_label" | "description">;
}
// For - END

// Every - START
interface EveryExpressionCells extends CellIdentification {
  type: "every";
  cell: BeePropertiesPanelComponent.EXPRESSION_ROOT;
  content: Pick<DMN15__tQuantified, "@_label" | "description">;
}
// Every - END

// Some - START
interface SomeExpressionCells extends CellIdentification {
  type: "some";
  cell: BeePropertiesPanelComponent.EXPRESSION_ROOT;
  content: Pick<DMN15__tQuantified, "@_label" | "description">;
}
// Some - END

// Conditional - START
interface ConditionalExpressionCells extends CellIdentification {
  type: "conditional";
  cell: BeePropertiesPanelComponent.EXPRESSION_ROOT;
  content: Pick<DMN15__tConditional, "@_label" | "description">;
}
// Conditional - END

// Filter - START
interface FilterExpressionCells extends CellIdentification {
  type: "filter";
  cell: BeePropertiesPanelComponent.EXPRESSION_ROOT;
  content: Pick<DMN15__tFilter, "@_label" | "description">;
}
// Filter - END

export type AllCells =
  | ContextExpressionCells
  | DecisionTableCells
  | FunctionDefinitionCells
  | InvocationExpressionCells
  | LiteralExpressionCells
  | RelationExpressionCells
  | ListExpressionCells
  | ForExpressionCells
  | SomeExpressionCells
  | EveryExpressionCells
  | ConditionalExpressionCells
  | FilterExpressionCells;

type DeepPartial<T> = T extends object
  ? {
      [P in keyof T]?: Partial<T[P]>;
    }
  : T;

export type AllCellsWithPartialContent = DeepPartial<AllCells>;

// TODO: COMPLETE
export function getBeePropertiesPanel(selectedObjectPath: ExpressionPath): {
  component: BeePropertiesPanelComponent;
  title: string;
} {
  if (selectedObjectPath.type === "context") {
    if (selectedObjectPath.column === "variable") {
      return { component: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL, title: "Boxed Context Variable" };
    }
    return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Context" };
  }
  if (selectedObjectPath.type === "decisionTable") {
    if (selectedObjectPath.header === "input") {
      if (selectedObjectPath.row < 0) {
        return {
          component: BeePropertiesPanelComponent.DECISION_TABLE_INPUT_HEADER,
          title: "Decision Table Input Header",
        };
      }
      return { component: BeePropertiesPanelComponent.UNARY_TEST, title: "Decision Table Input Cell" };
    }
    if (selectedObjectPath.header === "output") {
      if (selectedObjectPath.row < 0) {
        return {
          component: BeePropertiesPanelComponent.DECISION_TABLE_OUTPUT_HEADER,
          title: "Decision Table Output Header",
        };
      }
      return { component: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT, title: "Decision Table Output Cell" };
    }
    return { component: BeePropertiesPanelComponent.DECISION_TABLE_ROOT, title: "Decision Table" };
  }
  if (selectedObjectPath.type === "functionDefinition") {
    if (selectedObjectPath.parameterIndex < -1) {
      return { component: BeePropertiesPanelComponent.DECISION_TABLE_ROOT, title: "Function Definition" };
    }
    return { component: BeePropertiesPanelComponent.FUNCTION_DEFINITION_PARAMETERS, title: "Function Parameters" };
  }
  if (selectedObjectPath.type === "invocation") {
    if (selectedObjectPath.column === "parameter") {
      return {
        component: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
        title: "Boxed Invocation Parameter",
      };
    }
    if (selectedObjectPath.column === "expression") {
      return { component: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT, title: "Boxed Invocation" };
    }
    return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Invocation" };
  }
  if (selectedObjectPath.type === "literalExpression") {
    return { component: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT, title: "Literal Expression" };
  }
  if (selectedObjectPath.type === "relation") {
    if (selectedObjectPath.row < 0) {
      return { component: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL, title: "Boxed Relation Header" };
    }
    return { component: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT, title: "Boxed Relation Cell" };
    // TODO: ROOT
  }
  // TODO: LIST, FOR, SOME, EVERY, CONDITIONAL, FILTER;
  return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "" };
}

export function getDmnObject(
  paths: ExpressionPath[],
  expressionRoot: AllExpressions | undefined
): AllExpressionsWithoutTypes | undefined {
  if (!expressionRoot) {
    return;
  }
  return paths.reduce((expressionToEdit: AllExpressionsWithoutTypes, path) => {
    switch (path.type) {
      case "filter":
        if (path.row === "in") {
          return (expressionToEdit as DMN15__tFilter).in.expression;
        }
        return (expressionToEdit as DMN15__tFilter).match.expression;
      case "literalExpression":
        return expressionToEdit as DMN15__tLiteralExpression;
      case "invocation":
        if (path.column === "parameter") {
          return (expressionToEdit as DMN15__tInvocation).binding?.[path.row].parameter;
        }
        return (expressionToEdit as DMN15__tInvocation).binding?.[path.row].expression;
      case "decisionTable":
        if (path.header === "input") {
          if (path.row < 0) {
            return (expressionToEdit as DMN15__tDecisionTable).input?.[path.column];
          }
          return (expressionToEdit as DMN15__tDecisionTable).rule?.[path.row].inputEntry?.[path.column];
        }
        if (path.row < 0) {
          return (expressionToEdit as DMN15__tDecisionTable).output?.[path.column];
        }
        return (expressionToEdit as DMN15__tDecisionTable).rule?.[path.row].outputEntry?.[path.column];
      case "context":
        if (path.column === "expression") {
          return (expressionToEdit as DMN15__tContext).contextEntry?.[path.row].expression;
        }
        return (expressionToEdit as DMN15__tContext).contextEntry?.[path.row].variable;
      case "functionDefinition":
        return (expressionToEdit as DMN15__tFunctionDefinition).expression;
      case "relation":
        if (path.row < 0) {
          return (expressionToEdit as DMN15__tRelation).column?.[path.column];
        }
        return (expressionToEdit as DMN15__tRelation).row?.[path.row].expression?.[path.column];
      case "list":
        return (expressionToEdit as DMN15__tList).expression?.[path.row];
      case "for":
        if (path.row === "in") {
          return (expressionToEdit as DMN15__tFor).in.expression;
        }
        return (expressionToEdit as DMN15__tFor).return.expression;
      case "every":
        if (path.row === "in") {
          return (expressionToEdit as DMN15__tQuantified).in.expression;
        }
        return (expressionToEdit as DMN15__tQuantified).satisfies.expression;
      case "some":
        if (path.row === "in") {
          return (expressionToEdit as DMN15__tQuantified).in.expression;
        }
        return (expressionToEdit as DMN15__tQuantified).satisfies.expression;
      case "conditional":
        if (path.row === "if") {
          return (expressionToEdit as DMN15__tConditional).if.expression;
        }
        if (path.row === "else") {
          return (expressionToEdit as DMN15__tConditional).else.expression;
        }
        return (expressionToEdit as DMN15__tConditional).then.expression;
    }
  }, expressionRoot);
}
