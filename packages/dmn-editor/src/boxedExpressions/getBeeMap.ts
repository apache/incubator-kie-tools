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
  DMN15__tBinding,
  DMN15__tConditional,
  DMN15__tContext,
  DMN15__tDecision,
  DMN15__tDecisionTable,
  DMN15__tFilter,
  DMN15__tFor,
  DMN15__tFunctionDefinition,
  DMN15__tInvocation,
  DMN15__tList,
  DMN15__tLiteralExpression,
  DMN15__tQuantified,
  DMN15__tRelation,
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

// TODO: LUIZ
interface FunctionExpressionPath extends PathType {
  type: "functionDefinition";
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

type Path =
  | LiteralExpressionPath
  | ContextExpressionPath
  | DecisionTableHeaderPath
  | RelationExpressionPath
  | InvocationExpressionPath
  | ListExpressionPath
  | FunctionExpressionPath
  | ForExpressionPath
  | EveryExpressionPath
  | SomeExpressionPath
  | ConditionalExpressionPath
  | FilterExpressionPath;

type BeeMap = Map<string, { path: Path[]; cell: AllExpressionsWithoutTypes }>;

export function generateBeeMap(expression: AllExpressions, parentMap: BeeMap, parentPath: Path[]): BeeMap {
  const map: BeeMap = parentMap ? parentMap : new Map();

  switch (expression?.__$$element) {
    case "literalExpression":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          path: [...parentPath, { type: "literalExpression" }],
          cell: expression,
        });
      return map;
    case "invocation":
      // TODO: LUIZ - make it possible to edit parameters? expression.expression
      expression.binding?.forEach((b, row) => {
        b.parameter["@_id"] &&
          map.set(b.parameter["@_id"], {
            path: [...parentPath, { type: "invocation", row, column: "parameter" }],
            cell: b.parameter,
          });
        b.expression &&
          generateBeeMap(b.expression, map, [...parentPath, { type: "invocation", row, column: "expression" }]);
      });
      return map;
    case "decisionTable":
      expression.output.forEach(
        (o, column) =>
          o["@_id"] &&
          map.set(o["@_id"], {
            path: [...parentPath, { type: "decisionTable", header: "output", row: -1, column }],
            cell: o,
          })
      );
      expression.input?.forEach((i, column) => {
        i["@_id"] &&
          map.set(i["@_id"], {
            path: [...parentPath, { type: "decisionTable", header: "input", row: -1, column }],
            cell: i,
          });
      });
      expression.rule?.forEach((r, row) => {
        r.outputEntry?.forEach(
          (ro, column) =>
            ro["@_id"] &&
            map.set(ro["@_id"], {
              path: [...parentPath, { type: "decisionTable", header: "output", row, column }],
              cell: ro,
            })
        );
        r.inputEntry?.forEach(
          (ri, column) =>
            ri["@_id"] &&
            map.set(ri["@_id"], {
              path: [...parentPath, { type: "decisionTable", header: "input", row, column }],
              cell: ri,
            })
        );
      });
      return map;
    case "context":
      expression.contextEntry?.forEach((ce, row) => {
        ce.variable?.["@_id"] &&
          map.set(ce.variable["@_id"], {
            path: [...parentPath, { type: "context", row, column: "variable" }],
            cell: ce.variable,
          });
        generateBeeMap(ce.expression, map, [...parentPath, { type: "context", row, column: "expression" }]);
      });
      return map;
    case "functionDefinition":
      // TODO: LUIZ - make it possible to edit function parameters? expression.formalParameters
      if (expression.expression?.["@_id"]) {
        generateBeeMap(expression.expression, map, [...parentPath, { type: "functionDefinition" }]);
      }
      return map;
    case "relation":
      expression.column?.forEach(
        (c, column) =>
          c["@_id"] && map.set(c["@_id"], { path: [...parentPath, { type: "relation", row: -1, column }], cell: c })
      );
      expression.row?.forEach((r, row) =>
        r.expression?.forEach((re, column) => {
          re["@_id"] && map.set(re["@_id"], { path: [...parentPath, { type: "relation", row, column }], cell: r });
        })
      );
      return map;
    case "list":
      expression.expression?.forEach((e, row) => generateBeeMap(e, map, [...parentPath, { type: "list", row }]));
      return map;
    case "for":
      generateBeeMap(expression.in.expression, map, [...parentPath, { type: "for", row: "in" }]);
      generateBeeMap(expression.return.expression, map, [...parentPath, { type: "for", row: "return" }]);
      return map;
    case "every":
      generateBeeMap(expression.in.expression, map, [...parentPath, { type: "every", row: "in" }]);
      generateBeeMap(expression.satisfies.expression, map, [...parentPath, { type: "every", row: "statisfies" }]);
      return map;
    case "some":
      generateBeeMap(expression.in.expression, map, [...parentPath, { type: "some", row: "in" }]);
      generateBeeMap(expression.satisfies.expression, map, [...parentPath, { type: "some", row: "statisfies" }]);
      return map;
    case "conditional":
      generateBeeMap(expression.if.expression, map, [...parentPath, { type: "conditional", row: "if" }]);
      generateBeeMap(expression.else.expression, map, [...parentPath, { type: "conditional", row: "else" }]);
      generateBeeMap(expression.then.expression, map, [...parentPath, { type: "conditional", row: "then" }]);
      return map;
    case "filter":
      generateBeeMap(expression.in.expression, map, [...parentPath, { type: "filter", row: "in" }]);
      generateBeeMap(expression.match.expression, map, [...parentPath, { type: "filter", row: "match" }]);
      return map;
  }
}

export type TextCell = {
  type: AllExpressionTypes;
  text: { __$$text: string };
};

export type TypeRefCell = {
  type: AllExpressionTypes;
  "@_typeRef": string;
};

export type NameCell = {
  type: AllExpressionTypes;
  "@_name": string;
};

export type LiteralExpressionCell = TextCell & { type: "literalExpression" };
export type ContextExpressionVariableCell = (NameCell & TypeRefCell) & { type: "context" };

export type DecisionTableCell = DecisionTableOutputHeaderCell | DecisionTableInputHeaderCell | DecisionTableRuleCell;
export type DecisionTableOutputHeaderCell = (NameCell & TypeRefCell) & { type: "decisionTable" } & {
  cell: "outputHeader";
};
export type DecisionTableInputHeaderCell =
  | {
      inputExpression: TypeRefCell & TextCell;
    } & { type: "decisionTable" } & { cell: "inputHeader" };
export type DecisionTableRuleCell = TextCell & { type: "decisionTable" } & { cell: "rule" };

export type RelationCell = RelationHeaderCell | RelationContentCell;
export type RelationHeaderCell = (NameCell & TypeRefCell) & { type: "relation" } & { cell: "header" };
export type RelationContentCell = TextCell & { type: "relation" } & { cell: "content" };

export type InvocationParameterCell = (NameCell & TypeRefCell) & { type: "invocation" };

// Create to complete all types
type ListCell = { type: "list" };
type FunctionDefinitionCell = { type: "functionDefinition" };
type ForCell = { type: "for" };
type EveryCell = (NameCell & TypeRefCell) & { type: "every" };
type SomeCell = (NameCell & TypeRefCell) & { type: "some" };
type ConditionalCell = (NameCell & TypeRefCell) & { type: "conditional" };
type FilterCell = (NameCell & TypeRefCell) & { type: "filter" };

export type CellContent =
  | LiteralExpressionCell
  | ContextExpressionVariableCell
  | DecisionTableCell
  | RelationCell
  | InvocationParameterCell
  | ListCell
  | FunctionDefinitionCell
  | ForCell
  | EveryCell
  | SomeCell
  | ConditionalCell
  | FilterCell;

export enum BeePanelType {
  TEXT,
  NAME_TYPE,
  DECISION__TABLE_INPUT_HEADER,
  NONE,
}

export function getBeePropertiesPanel(selectedObjectPath: Path): {
  panelType: BeePanelType;
  title?: string;
} {
  switch (selectedObjectPath.type) {
    case "literalExpression":
      return { panelType: BeePanelType.TEXT, title: "Boxed Literal" };
    case "invocation":
      if (selectedObjectPath.column === "parameter") {
        return {
          panelType: BeePanelType.NAME_TYPE,
          title: "Boxed Invocation Parameter",
        };
      }
      return { panelType: BeePanelType.NONE };
    case "decisionTable":
      if (selectedObjectPath.header === "input") {
        if (selectedObjectPath.row < 0) {
          return {
            panelType: BeePanelType.DECISION__TABLE_INPUT_HEADER,
            title: "Decision Table Input Header",
          };
        }
        return { panelType: BeePanelType.TEXT, title: "Decision Table Input Cell" };
      }
      if (selectedObjectPath.row < 0) {
        return {
          panelType: BeePanelType.NAME_TYPE,
          title: "Decision Table Output Header",
        };
      }
      return { panelType: BeePanelType.TEXT, title: "Decision Table Output Cell" };
    case "context":
      if (selectedObjectPath.column === "variable") {
        return { panelType: BeePanelType.NAME_TYPE, title: "Boxed Context Variable" };
      }
      return { panelType: BeePanelType.NONE };
    case "functionDefinition":
      return { panelType: BeePanelType.NONE };
    case "relation":
      if (selectedObjectPath.row < 0) {
        return { panelType: BeePanelType.NAME_TYPE, title: "Boxed Relation Header" };
      }
      return { panelType: BeePanelType.TEXT, title: "Boxed Relation Cell" };
    case "list":
      return { panelType: BeePanelType.NONE };
    case "for":
      return { panelType: BeePanelType.NONE };
    case "every":
      return { panelType: BeePanelType.NONE };
    case "some":
      return { panelType: BeePanelType.NONE };
    case "conditional":
      return { panelType: BeePanelType.NONE };
    case "filter":
      return { panelType: BeePanelType.NONE };
  }
}

export function getDmnObject(paths: Path[], decesionObject: DMN15__tDecision): AllExpressionsWithoutTypes | undefined {
  return paths.reduce((decesionObject: AllExpressionsWithoutTypes, path) => {
    switch (path.type) {
      case "filter":
        if (path.row === "in") {
          return (decesionObject as DMN15__tFilter).in.expression;
        }
        return (decesionObject as DMN15__tFilter).match.expression;
      case "literalExpression":
        return decesionObject as DMN15__tLiteralExpression;
      case "invocation":
        if (path.column === "parameter") {
          return (decesionObject as DMN15__tInvocation).binding?.[path.row].parameter;
        }
        return (decesionObject as DMN15__tInvocation).binding?.[path.row].expression;
      case "decisionTable":
        if (path.header === "input") {
          if (path.row < 0) {
            return (decesionObject as DMN15__tDecisionTable).input?.[path.row].inputExpression;
          }
          return (decesionObject as DMN15__tDecisionTable).rule?.[path.row].inputEntry?.[path.column];
        }
        if (path.row < 0) {
          return (decesionObject as DMN15__tDecisionTable).output?.[path.row];
        }
        return (decesionObject as DMN15__tDecisionTable).rule?.[path.row].outputEntry?.[path.column];
      case "context":
        if (path.column === "expression") {
          return (decesionObject as DMN15__tContext).contextEntry?.[path.row].expression;
        }
        return (decesionObject as DMN15__tContext).contextEntry?.[path.row].variable;
      case "functionDefinition":
        return (decesionObject as DMN15__tFunctionDefinition).expression;
      case "relation":
        if (path.row < 0) {
          return (decesionObject as DMN15__tRelation).column?.[path.column];
        }
        return (decesionObject as DMN15__tRelation).row?.[path.row].expression?.[path.column];
      case "list":
        return (decesionObject as DMN15__tList).expression?.[path.row];
      case "for":
        if (path.row === "in") {
          return (decesionObject as DMN15__tFor).in.expression;
        }
        return (decesionObject as DMN15__tFor).return.expression;
      case "every":
        if (path.row === "in") {
          return (decesionObject as DMN15__tQuantified).in.expression;
        }
        return (decesionObject as DMN15__tQuantified).satisfies.expression;
      case "some":
        if (path.row === "in") {
          return (decesionObject as DMN15__tQuantified).in.expression;
        }
        return (decesionObject as DMN15__tQuantified).satisfies.expression;
      case "conditional":
        if (path.row === "if") {
          return (decesionObject as DMN15__tConditional).if.expression;
        }
        if (path.row === "else") {
          return (decesionObject as DMN15__tConditional).else.expression;
        }
        return (decesionObject as DMN15__tConditional).then.expression;
    }
  }, decesionObject.expression);
}
