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
  root: string;
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
  row?: "variable" | "in" | "statisfies";
}

interface FilterExpressionPath extends PathType {
  type: "filter";
  row?: "in" | "match";
}

interface ForExpressionPath extends PathType {
  type: "for";
  row?: "variable" | "in" | "return";
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
  row?: "variable" | "in" | "statisfies";
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
export type BoxedExpressionIndex = Map<
  string,
  { expressionPath: ExpressionPath[]; cell: AllExpressionsWithoutTypes | AllExpressionsWithoutTypes[] }
>;

export function generateBoxedExpressionIndex(
  expression: AllExpressions,
  parentMap: BoxedExpressionIndex,
  parentExpressionPath: ExpressionPath[]
): BoxedExpressionIndex {
  const map: BoxedExpressionIndex = parentMap ? parentMap : new Map();
  switch (expression?.__$$element) {
    case "conditional":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "conditional", root: expression["@_id"] }],
          cell: expression,
        });
      generateBoxedExpressionIndex(expression.if.expression, map, [
        ...parentExpressionPath,
        { type: "conditional", row: "if", root: expression["@_id"]! },
      ]);
      generateBoxedExpressionIndex(expression.else.expression, map, [
        ...parentExpressionPath,
        { type: "conditional", row: "else", root: expression["@_id"]! },
      ]);
      generateBoxedExpressionIndex(expression.then.expression, map, [
        ...parentExpressionPath,
        { type: "conditional", row: "then", root: expression["@_id"]! },
      ]);
      return map;
    case "context":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "context", root: expression["@_id"] }],
          cell: expression,
        });
      expression.contextEntry?.forEach((ce, row) => {
        const id = ce.variable?.["@_id"] ?? ce?.["@_id"];
        id &&
          ce.variable &&
          map.set(id, {
            expressionPath: [
              ...parentExpressionPath,
              { type: "context", row, column: "variable", root: expression["@_id"]! },
            ],
            cell: ce.variable,
          });
        generateBoxedExpressionIndex(ce.expression, map, [
          ...parentExpressionPath,
          { type: "context", row, column: "expression", root: expression["@_id"]! },
        ]);
      });
      return map;
    case "decisionTable":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "decisionTable", root: expression["@_id"] }],
          cell: expression,
        });
      expression.output.forEach(
        (o, column) =>
          o["@_id"] &&
          map.set(o["@_id"], {
            expressionPath: [
              ...parentExpressionPath,
              { type: "decisionTable", header: "output", row: -1, column, root: expression["@_id"]! },
            ],
            cell: o,
          })
      );
      expression.input?.forEach((i, column) => {
        i["@_id"] &&
          map.set(i["@_id"], {
            expressionPath: [
              ...parentExpressionPath,
              { type: "decisionTable", header: "input", row: -1, column, root: expression["@_id"]! },
            ],
            cell: i,
          });
      });
      expression.rule?.forEach((r, row) => {
        r.outputEntry?.forEach(
          (ro, column) =>
            ro["@_id"] &&
            map.set(ro["@_id"], {
              expressionPath: [
                ...parentExpressionPath,
                { type: "decisionTable", header: "output", row, column, root: expression["@_id"]! },
              ],
              cell: ro,
            })
        );
        r.inputEntry?.forEach(
          (ri, column) =>
            ri["@_id"] &&
            map.set(ri["@_id"], {
              expressionPath: [
                ...parentExpressionPath,
                { type: "decisionTable", header: "input", row, column, root: expression["@_id"]! },
              ],
              cell: ri,
            })
        );
      });
      return map;
    case "every":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "every", root: expression["@_id"] }],
          cell: expression,
        });
      expression["@_id"] &&
        map.set(`${expression["@_id"]}-iteratorVariable`, {
          expressionPath: [...parentExpressionPath, { type: "every", row: "variable", root: expression["@_id"] }],
          cell: expression,
        });
      generateBoxedExpressionIndex(expression.in.expression, map, [
        ...parentExpressionPath,
        { type: "every", row: "in", root: expression["@_id"]! },
      ]);
      generateBoxedExpressionIndex(expression.satisfies.expression, map, [
        ...parentExpressionPath,
        { type: "every", row: "statisfies", root: expression["@_id"]! },
      ]);
      return map;
    case "filter":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "filter", root: expression["@_id"] }],
          cell: expression,
        });
      generateBoxedExpressionIndex(expression.in.expression, map, [
        ...parentExpressionPath,
        { type: "filter", row: "in", root: expression["@_id"]! },
      ]);
      generateBoxedExpressionIndex(expression.match.expression, map, [
        ...parentExpressionPath,
        { type: "filter", row: "match", root: expression["@_id"]! },
      ]);
      return map;
    case "for":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "for", root: expression["@_id"] }],
          cell: expression,
        });
      expression["@_id"] &&
        map.set(`${expression["@_id"]}-iteratorVariable`, {
          expressionPath: [...parentExpressionPath, { type: "for", row: "variable", root: expression["@_id"] }],
          cell: expression,
        });
      generateBoxedExpressionIndex(expression.in.expression, map, [
        ...parentExpressionPath,
        { type: "for", row: "in", root: expression["@_id"]! },
      ]);
      generateBoxedExpressionIndex(expression.return.expression, map, [
        ...parentExpressionPath,
        { type: "for", row: "return", root: expression["@_id"]! },
      ]);
      return map;
    case "functionDefinition":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "functionDefinition", root: expression["@_id"] }],
          cell: expression,
        });

      if (expression.expression?.["@_id"]) {
        generateBoxedExpressionIndex(expression.expression, map, [
          ...parentExpressionPath,
          { type: "functionDefinition", parameterIndex: -1, root: expression["@_id"]! },
        ]);
      }

      map.set(`${expression["@_id"]}-parameters`, {
        expressionPath: [
          ...parentExpressionPath,
          { type: "functionDefinition", parameterIndex: 0, root: expression["@_id"]! },
        ],
        cell: expression.formalParameter ?? [],
      });
      return map;
    case "invocation":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "invocation", root: expression["@_id"] }],
          cell: expression,
        });
      expression.binding?.forEach((b, row) => {
        b.parameter["@_id"] &&
          map.set(b.parameter["@_id"], {
            expressionPath: [
              ...parentExpressionPath,
              { type: "invocation", row, column: "parameter", root: expression["@_id"]! },
            ],
            cell: b.parameter,
          });
        b.expression &&
          generateBoxedExpressionIndex(b.expression, map, [
            ...parentExpressionPath,
            { type: "invocation", row, column: "expression", root: expression["@_id"]! },
          ]);
      });
      // function call
      expression.expression &&
        expression.expression["@_id"] &&
        map.set(expression.expression["@_id"], {
          expressionPath: [
            ...parentExpressionPath,
            { type: "invocation", row: -1, column: "expression", root: expression["@_id"]! },
          ],
          cell: expression.expression,
        });
      return map;
    case "list":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "list", root: expression["@_id"] }],
          cell: expression,
        });
      expression.expression?.forEach((e, row) =>
        generateBoxedExpressionIndex(e, map, [
          ...parentExpressionPath,
          { type: "list", row, root: expression["@_id"]! },
        ])
      );
      return map;
    case "literalExpression":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "literalExpression", root: expression["@_id"] }],
          cell: expression,
        });
      return map;
    case "relation":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "relation", root: expression["@_id"] }],
          cell: expression,
        });
      expression.column?.forEach(
        (c, column) =>
          c["@_id"] &&
          map.set(c["@_id"], {
            expressionPath: [...parentExpressionPath, { type: "relation", row: -1, column, root: expression["@_id"]! }],
            cell: c,
          })
      );
      expression.row?.forEach((r, row) =>
        r.expression?.forEach((re, column) => {
          re["@_id"] &&
            map.set(re["@_id"], {
              expressionPath: [...parentExpressionPath, { type: "relation", row, column, root: expression["@_id"]! }],
              cell: re,
            });
        })
      );
      return map;
    case "some":
      expression["@_id"] &&
        map.set(expression["@_id"], {
          expressionPath: [...parentExpressionPath, { type: "some", root: expression["@_id"] }],
          cell: expression,
        });
      expression["@_id"] &&
        map.set(`${expression["@_id"]}-iteratorVariable`, {
          expressionPath: [...parentExpressionPath, { type: "some", row: "variable", root: expression["@_id"] }],
          cell: expression,
        });
      generateBoxedExpressionIndex(expression.in.expression, map, [
        ...parentExpressionPath,
        { type: "some", row: "in", root: expression["@_id"]! },
      ]);
      generateBoxedExpressionIndex(expression.satisfies.expression, map, [
        ...parentExpressionPath,
        { type: "some", row: "statisfies", root: expression["@_id"]! },
      ]);
      return map;
  }
}

export function getDmnObjectByPath(
  paths: ExpressionPath[],
  expressionRoot?: AllExpressionsWithoutTypes
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
      if (path.row === "variable") {
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
      if (path.row === "variable") {
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
      if (path.row === "variable") {
        return expressionToEdit as DMN15__tQuantified;
      }
      if (path.row === "in") {
        return (expressionToEdit as DMN15__tQuantified).in.expression;
      }
      return (expressionToEdit as DMN15__tQuantified).satisfies.expression;
    }
  }, expressionRoot);
}
