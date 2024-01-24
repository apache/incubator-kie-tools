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

interface ConditionalExpressionPath extends PathType {
  type: "conditional";
  row?: "if" | "else" | "then";
}

interface ContextExpressionPath extends PathType {
  type: "context";
  row?: number;
  column?: "variable" | "expression";
}

interface DecisionTablePath extends PathType {
  type: "decisionTable";
  header?: "input" | "output";
  row?: number;
  column?: number;
}

interface EveryExpressionPath extends PathType {
  type: "every";
  row?: "in" | "statisfies";
}

interface FilterExpressionPath extends PathType {
  type: "filter";
  row?: "in" | "match";
}

interface ForExpressionPath extends PathType {
  type: "for";
  row?: "in" | "return";
}

interface FunctionDefinitionExpressionPath extends PathType {
  type: "functionDefinition";
  parameterIndex?: number;
}

interface InvocationExpressionPath extends PathType {
  type: "invocation";
  row?: number;
  column?: "parameter" | "expression";
}

interface ListExpressionPath extends PathType {
  type: "list";
  row?: number;
}

interface LiteralExpressionPath extends PathType {
  type: "literalExpression";
}

interface RelationExpressionPath extends PathType {
  type: "relation";
  row?: number;
  column?: number;
}

interface SomeExpressionPath extends PathType {
  type: "some";
  row?: "in" | "statisfies";
}

export type ExpressionPath =
  | ConditionalExpressionPath
  | ContextExpressionPath
  | DecisionTablePath
  | EveryExpressionPath
  | FilterExpressionPath
  | ForExpressionPath
  | FunctionDefinitionExpressionPath
  | InvocationExpressionPath
  | ListExpressionPath
  | LiteralExpressionPath
  | RelationExpressionPath
  | SomeExpressionPath;

/**
 * A map of "@_id" to cell (expression) and its path in the expression hierarchy
 */
export type BeeMap = Map<
  string,
  { expressionPath: ExpressionPath[]; cell: AllExpressionsWithoutTypes | AllExpressionsWithoutTypes[] }
>;

export function generateBeeMap(
  expression: AllExpressions,
  parentMap: BeeMap,
  parentExpressionPath: ExpressionPath[]
): BeeMap {
  const map: BeeMap = parentMap ? parentMap : new Map();
  switch (expression?.__$$element) {
    case "conditional":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "conditional" }],
          cell: expression,
        });
      generateBeeMap(expression.if.expression, map, [...parentExpressionPath, { type: "conditional", row: "if" }]);
      generateBeeMap(expression.else.expression, map, [...parentExpressionPath, { type: "conditional", row: "else" }]);
      generateBeeMap(expression.then.expression, map, [...parentExpressionPath, { type: "conditional", row: "then" }]);
      return map;
    case "context":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "context" }],
          cell: expression,
        });
      expression.contextEntry?.forEach((ce, row) => {
        const id = ce.variable?.["@_id"] ?? ce?.["@_id"];
        id &&
          ce.variable &&
          map.set(id, {
            expressionPath: [...parentExpressionPath, { type: "context", row, column: "variable" }],
            cell: ce.variable,
          });
        generateBeeMap(ce.expression, map, [...parentExpressionPath, { type: "context", row, column: "expression" }]);
      });
      return map;
    case "decisionTable":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "decisionTable" }],
          cell: expression,
        });
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
    case "every":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "every" }],
          cell: expression,
        });
      generateBeeMap(expression.in.expression, map, [...parentExpressionPath, { type: "every", row: "in" }]);
      generateBeeMap(expression.satisfies.expression, map, [
        ...parentExpressionPath,
        { type: "every", row: "statisfies" },
      ]);
      return map;
    case "filter":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "filter" }],
          cell: expression,
        });
      generateBeeMap(expression.in.expression, map, [...parentExpressionPath, { type: "filter", row: "in" }]);
      generateBeeMap(expression.match.expression, map, [...parentExpressionPath, { type: "filter", row: "match" }]);
      return map;
    case "for":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "for" }],
          cell: expression,
        });
      generateBeeMap(expression.in.expression, map, [...parentExpressionPath, { type: "for", row: "in" }]);
      generateBeeMap(expression.return.expression, map, [...parentExpressionPath, { type: "for", row: "return" }]);
      return map;
    case "functionDefinition":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "functionDefinition" }],
          cell: expression,
        });

      if (expression.expression?.["@_id"]) {
        generateBeeMap(expression.expression, map, [
          ...parentExpressionPath,
          { type: "functionDefinition", parameterIndex: -1 },
        ]);
      }

      map.set(`${expression["@_id"]}-parameters`, {
        expressionPath: [...parentExpressionPath, { type: "functionDefinition", parameterIndex: 0 }],
        cell: expression.formalParameter ?? [],
      });
      // expression.formalParameter?.forEach((fp, parameterIndex) => {
      //   fp["@_id"] &&
      //     map.set(fp["@_id"], {
      //       expressionPath: [...parentExpressionPath, { type: "functionDefinition", parameterIndex: parameterIndex }],
      //       cell: fp,
      //     });
      // });
      return map;
    case "invocation":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "invocation" }],
          cell: expression,
        });
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
      // function call
      expression.expression &&
        expression.expression["@_id"] &&
        map.set(expression.expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "invocation", row: -1, column: "expression" }],
          cell: expression.expression,
        });
      return map;
    case "list":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "list" }],
          cell: expression,
        });
      expression.expression?.forEach((e, row) =>
        generateBeeMap(e, map, [...parentExpressionPath, { type: "list", row }])
      );
      return map;
    case "literalExpression":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "literalExpression" }],
          cell: expression,
        });
      return map;
    case "relation":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "relation" }],
          cell: expression,
        });
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
    case "some":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "some" }],
          cell: expression,
        });
      generateBeeMap(expression.in.expression, map, [...parentExpressionPath, { type: "some", row: "in" }]);
      generateBeeMap(expression.satisfies.expression, map, [
        ...parentExpressionPath,
        { type: "some", row: "statisfies" },
      ]);
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
  INVOCATION_FUNCTION_CALL,
  LITERAL_EXPRESSION_CONTENT,
  UNARY_TEST,
}

export type DeepPartial<T> = T extends object
  ? {
      [P in keyof T]?: Partial<T[P]>;
    }
  : T;

export function getBeePropertiesPanel(selectedObjectPath: ExpressionPath): {
  component: BeePropertiesPanelComponent;
  title: string;
} {
  if (selectedObjectPath.type === "conditional") {
    if (selectedObjectPath.row === undefined) {
      return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Conditional" };
    }
  }

  if (selectedObjectPath.type === "context") {
    if (selectedObjectPath.column === undefined) {
      return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Context" };
    }
    if (selectedObjectPath.column === "variable") {
      return { component: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL, title: "Boxed Context Variable" };
    }
    // selectedObjectPath.column === "expression" is handled by the nested expression
  }

  if (selectedObjectPath.type === "decisionTable") {
    if (selectedObjectPath.row === undefined) {
      return { component: BeePropertiesPanelComponent.DECISION_TABLE_ROOT, title: "Decision Table" };
    }
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
  }

  if (selectedObjectPath.type === "every") {
    if (selectedObjectPath.row === undefined) {
      return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Every" };
    }
  }

  if (selectedObjectPath.type === "filter") {
    if (selectedObjectPath.row === undefined) {
      return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Filter" };
    }
  }

  if (selectedObjectPath.type === "for") {
    if (selectedObjectPath.row === undefined) {
      return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed For" };
    }
  }

  if (selectedObjectPath.type === "functionDefinition") {
    if (selectedObjectPath.parameterIndex === undefined) {
      return { component: BeePropertiesPanelComponent.FUNCTION_DEFINITION_ROOT, title: "Function Definition" };
    }
    return { component: BeePropertiesPanelComponent.FUNCTION_DEFINITION_PARAMETERS, title: "Function Parameters" };
  }

  if (selectedObjectPath.type === "invocation") {
    if (selectedObjectPath.row === undefined || selectedObjectPath.column === undefined) {
      return {
        component: BeePropertiesPanelComponent.EXPRESSION_ROOT,
        title: "Boxed Invocation",
      };
    }
    if (selectedObjectPath.row < 0) {
      return {
        component: BeePropertiesPanelComponent.INVOCATION_FUNCTION_CALL,
        title: "Boxed Invocation Called Function",
      };
    }
    if (selectedObjectPath.column === "parameter") {
      return {
        component: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
        title: "Boxed Invocation Parameter",
      };
    }
    if (selectedObjectPath.column === "expression") {
      return { component: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT, title: "Boxed Invocation" };
    }
  }

  if (selectedObjectPath.type === "list") {
    if (selectedObjectPath.row === undefined) {
      return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed List" };
    }
  }

  if (selectedObjectPath.type === "literalExpression") {
    return { component: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT, title: "Literal Expression" };
  }

  if (selectedObjectPath.type === "relation") {
    if (selectedObjectPath.row === undefined) {
      return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Relation" };
    }
    if (selectedObjectPath.row < 0) {
      return { component: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL, title: "Boxed Relation Header" };
    }
    return { component: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT, title: "Boxed Relation Cell" };
  }

  if (selectedObjectPath.type === "some") {
    if (selectedObjectPath.row === undefined) {
      return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "Boxed Some" };
    }
  }
  return { component: BeePropertiesPanelComponent.EXPRESSION_ROOT, title: "" };
}

export function getDmnObjectByPath(
  paths: ExpressionPath[],
  expressionRoot: AllExpressionsWithoutTypes | undefined
): AllExpressionsWithoutTypes | undefined {
  if (!expressionRoot) {
    return;
  }
  return paths.reduce((expressionToEdit: AllExpressionsWithoutTypes, path) => {
    if (path.type === "conditional") {
      // root
      if (path.row === undefined) {
        return expressionToEdit as DMN15__tConditional;
      }
      if (path.row === "if") {
        return (expressionToEdit as DMN15__tConditional).if.expression;
      }
      if (path.row === "else") {
        return (expressionToEdit as DMN15__tConditional).else.expression;
      }
      return (expressionToEdit as DMN15__tConditional).then.expression;
    }
    if (path.type === "context") {
      // root
      if (path.row === undefined || path.column === undefined) {
        return expressionToEdit as DMN15__tContext;
      }
      if (path.column === "expression") {
        return (expressionToEdit as DMN15__tContext).contextEntry?.[path.row].expression;
      }
      return (expressionToEdit as DMN15__tContext).contextEntry?.[path.row].variable;
    }
    if (path.type === "decisionTable") {
      // root
      if (path.row === undefined || path.column === undefined) {
        return expressionToEdit as DMN15__tDecisionTable;
      }
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
    }
    if (path.type === "every") {
      // root
      if (path.row === undefined) {
        return expressionToEdit as DMN15__tQuantified;
      }
      if (path.row === "in") {
        return (expressionToEdit as DMN15__tQuantified).in.expression;
      }
      return (expressionToEdit as DMN15__tQuantified).satisfies.expression;
    }
    if (path.type === "filter") {
      // root
      if (path.row === undefined) {
        return expressionToEdit as DMN15__tFilter;
      }
      if (path.row === "in") {
        return (expressionToEdit as DMN15__tFilter).in.expression;
      }
      return (expressionToEdit as DMN15__tFilter).match.expression;
    }
    if (path.type === "for") {
      // root
      if (path.row === undefined) {
        return expressionToEdit as DMN15__tFor;
      }
      if (path.row === "in") {
        return (expressionToEdit as DMN15__tFor).in.expression;
      }
      return (expressionToEdit as DMN15__tFor).return.expression;
    }
    if (path.type === "functionDefinition") {
      // root
      if (!path.parameterIndex) {
        return expressionToEdit as DMN15__tFunctionDefinition;
      }
      if (path.parameterIndex < 0) {
        return (expressionToEdit as DMN15__tFunctionDefinition).expression;
      }
      return expressionToEdit as DMN15__tFunctionDefinition;
    }
    if (path.type === "invocation") {
      // root
      if (path.row === undefined || path.column === undefined) {
        return expressionToEdit as DMN15__tInvocation;
      }
      if (path.column === "parameter") {
        return (expressionToEdit as DMN15__tInvocation).binding?.[path.row].parameter;
      }
      if (path.column === "expression" && path.row >= 0) {
        return (expressionToEdit as DMN15__tInvocation).binding?.[path.row].expression;
      }
      // function call
      return (expressionToEdit as DMN15__tInvocation).expression;
    }
    if (path.type === "list") {
      // root
      if (path.row === undefined) {
        return expressionToEdit as DMN15__tList;
      }
      return (expressionToEdit as DMN15__tList).expression?.[path.row];
    }
    if (path.type === "literalExpression") {
      return expressionToEdit as DMN15__tLiteralExpression;
    }
    if (path.type === "relation") {
      // root
      if (path.row === undefined || path.column === undefined) {
        return expressionToEdit as DMN15__tRelation;
      }
      if (path.row < 0) {
        return (expressionToEdit as DMN15__tRelation).column?.[path.column];
      }
      return (expressionToEdit as DMN15__tRelation).row?.[path.row].expression?.[path.column];
    }
    if (path.type === "some") {
      // root
      if (path.row === undefined) {
        return expressionToEdit as DMN15__tQuantified;
      }
      if (path.row === "in") {
        return (expressionToEdit as DMN15__tQuantified).in.expression;
      }
      return (expressionToEdit as DMN15__tQuantified).satisfies.expression;
    }
  }, expressionRoot);
}
