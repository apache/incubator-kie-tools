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
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  DECISION_TABLE_ANNOTATION_MIN_WIDTH,
  DECISION_TABLE_INPUT_MIN_WIDTH,
  DECISION_TABLE_OUTPUT_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
} from "@kie-tools/boxed-expression-component/dist/resizing/WidthConstants";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  FunctionExpressionDefinitionKind,
  GwtExpressionDefinition,
  GwtExpressionDefinitionLogicType,
} from "./types";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { BoxedExpression } from "@kie-tools/boxed-expression-component/dist/api";

/** Converts a GwtExpressionDefinition to a BoxedExpression. This convertion is
 *  necessary for historical reasons, as the Boxed Expression Editor was
 *  created prior to the DMN Editor, needing to declare its own model. */
export function gwtToBee(expression: GwtExpressionDefinition, __widths: Map<string, number[]>): BoxedExpression {
  if (!expression) {
    return undefined!; // SPEC DISCREPANCY
  }
  switch (expression.logicType) {
    case GwtExpressionDefinitionLogicType.Undefined:
      return undefined!; // SPEC DISCREPANCY
    case GwtExpressionDefinitionLogicType.Context:
      return {
        __$$element: "context",
        "@_id": expression.id,
        "@_label": expression.name,
        "@_typeRef": expression.dataType,
        contextEntry: [
          ...expression.contextEntries.map((e) => {
            __widths.set(expression.id, expression.entryInfoWidth ? [expression.entryInfoWidth] : []);
            return {
              "@_id": e.entryInfo.id,
              expression: gwtToBee(e.entryExpression, __widths)!,
              variable: {
                "@_name": e.entryInfo.name,
                "@_typeRef": e.entryInfo.dataType,
              },
            };
          }),
          ...(expression.result.logicType !== GwtExpressionDefinitionLogicType.Undefined
            ? [
                {
                  "@_id": expression.result.id,
                  expression: gwtToBee(expression.result, __widths)!,
                },
              ]
            : []),
        ],
      };
    case GwtExpressionDefinitionLogicType.DecisionTable:
      __widths.set(expression.id, [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH]);
      return {
        __$$element: "decisionTable",
        "@_id": expression.id,
        "@_label": expression.name,
        "@_typeRef": expression.dataType,
        "@_hitPolicy": expression.hitPolicy,
        "@_aggregation": (() => {
          switch (expression.aggregation) {
            case DecisionTableExpressionDefinitionBuiltInAggregation["<None>"]:
              return undefined;
            case DecisionTableExpressionDefinitionBuiltInAggregation.SUM:
              return "SUM";
            case DecisionTableExpressionDefinitionBuiltInAggregation.COUNT:
              return "COUNT";
            case DecisionTableExpressionDefinitionBuiltInAggregation.MIN:
              return "MIN";
            case DecisionTableExpressionDefinitionBuiltInAggregation.MAX:
              return "MAX";
          }
        })(),
        input: (expression.input ?? []).map((s) => {
          __widths.set(expression.id, [
            ...(__widths.get(expression.id) ?? []),
            s.width ?? DECISION_TABLE_INPUT_MIN_WIDTH,
          ]);
          return {
            "@_id": s.id,
            inputExpression: {
              "@_id": s.idLiteralExpression,
              "@_typeRef": s.dataType,
              text: { __$$text: s.name }, // This is really bad... `s.name` is actually an expression. Will be addressed by https://github.com/apache/incubator-kie-issues/issues/455
            },
          };
        }),
        output: (expression.output ?? []).map((o) => {
          __widths.set(expression.id, [
            ...(__widths.get(expression.id) ?? []),
            o.width ?? DECISION_TABLE_OUTPUT_MIN_WIDTH,
          ]);
          return {
            "@_id": o.id,
            "@_name": o.name,
            "@_typeRef": o.dataType,
          };
        }),
        annotation: (expression.annotations ?? []).map((a) => {
          __widths.set(expression.id, [
            ...(__widths.get(expression.id) ?? []),
            a.width ?? DECISION_TABLE_ANNOTATION_MIN_WIDTH,
          ]);
          return {
            "@_name": a.name,
          };
        }),
        rule: (expression.rules ?? []).map((r) => {
          return {
            "@_id": r.id,
            inputEntry: r.inputEntries.map((i) => ({ "@_id": i.id, text: { __$$text: i.content } })),
            outputEntry: r.outputEntries.map((s) => ({ "@_id": s.id, text: { __$$text: s.content } })),
            annotationEntry: r.annotationEntries.map((a) => ({ text: { __$$text: a } })),
          };
        }),
      };
    case GwtExpressionDefinitionLogicType.Function:
      return {
        __$$element: "functionDefinition",
        "@_id": expression.id,
        "@_label": expression.name,
        "@_kind": expression.functionKind,
        "@_typeRef": expression.dataType,
        formalParameter: expression.formalParameters.map((p) => ({
          "@_id": p.id,
          "@_name": p.name,
          "@_typeRef": p.dataType,
        })),
        expression:
          expression.functionKind === FunctionExpressionDefinitionKind.Feel
            ? gwtToBee(expression.expression, __widths)
            : expression.functionKind === FunctionExpressionDefinitionKind.Java
            ? (() => {
                __widths.set(expression.id, [
                  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
                  expression.classAndMethodNamesWidth ?? JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
                ]);
                return {
                  __$$element: "context",
                  contextEntry: [
                    {
                      "@_id": expression.classFieldId,
                      expression: {
                        __$$element: "literalExpression",
                        text: { __$$text: expression.className ?? "" },
                      },
                      variable: { "@_name": DMN15_SPEC.BOXED.FUNCTION.JAVA.classFieldName },
                    },
                    {
                      "@_id": expression.methodFieldId,
                      expression: {
                        __$$element: "literalExpression",
                        text: { __$$text: expression.methodName ?? "" },
                      },
                      variable: { "@_name": DMN15_SPEC.BOXED.FUNCTION.JAVA.methodSignatureFieldName },
                    },
                  ],
                };
              })()
            : expression.functionKind === FunctionExpressionDefinitionKind.Pmml
            ? (() => {
                return {
                  __$$element: "context",
                  contextEntry: [
                    {
                      "@_id": expression.documentFieldId,
                      expression: {
                        __$$element: "literalExpression",
                        text: { __$$text: expression.document ?? "" },
                      },
                      variable: { "@_name": DMN15_SPEC.BOXED.FUNCTION.PMML.documentFieldName },
                    },
                    {
                      "@_id": expression.modelFieldId,
                      expression: {
                        __$$element: "literalExpression",
                        text: { __$$text: expression.model ?? "" },
                      },
                      variable: { "@_name": DMN15_SPEC.BOXED.FUNCTION.PMML.modelFieldName },
                    },
                  ],
                };
              })()
            : (() => {
                throw new Error(`Unknown Function kind '${(expression as any).functionKind}'.`);
              })(),
      };
    case GwtExpressionDefinitionLogicType.Invocation:
      return {
        __$$element: "invocation",
        "@_id": expression.id,
        "@_label": expression.name,
        "@_typeRef": expression.dataType,
        expression: {
          __$$element: "literalExpression",
          "@_id": expression.invokedFunction.id,
          text: { __$$text: expression.invokedFunction.name },
        },
        binding: expression.bindingEntries.map((e) => {
          __widths.set(expression.id, expression.entryInfoWidth ? [expression.entryInfoWidth] : []);
          return {
            "@_id": e.entryInfo.id,
            expression: gwtToBee(e.entryExpression, __widths)!,
            parameter: {
              "@_id": e.entryInfo.id,
              "@_name": e.entryInfo.name,
              "@_typeRef": e.entryInfo.dataType,
            },
          };
        }),
      };
    case GwtExpressionDefinitionLogicType.List:
      __widths.set(expression.id, [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH]);
      return {
        __$$element: "list",
        "@_id": expression.id,
        "@_label": expression.name,
        "@_typeRef": expression.dataType,
        expression: expression.items.map((i) => gwtToBee(i, __widths)!),
      };
    case GwtExpressionDefinitionLogicType.Literal:
      __widths.set(expression.id, expression.width ? [expression.width] : []);
      return {
        __$$element: "literalExpression",
        "@_id": expression.id,
        "@_label": expression.name,
        "@_typeRef": expression.dataType,
        text: { __$$text: expression.content ?? "" },
      };
    case GwtExpressionDefinitionLogicType.Relation:
      __widths.set(expression.id, [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH]);
      return {
        __$$element: "relation",
        "@_id": expression.id,
        "@_label": expression.name,
        "@_typeRef": expression.dataType,
        row: (expression.rows ?? []).map((r) => {
          return {
            "@_id": r.id,
            expression: r.cells.map((cell) => ({
              __$$element: "literalExpression",
              text: { __$$text: cell.content },
              "@_id": cell.id,
            })),
          };
        }),
        column: (expression.columns ?? []).map((c) => {
          __widths.set(expression.id, [...(__widths.get(expression.id) ?? []), ...(c.width ? [c.width] : [])]);
          return {
            "@_id": c.id,
            "@_name": c.name,
            "@_typeRef": c.dataType,
          };
        }),
      };
    default:
      throw new Error(`Unknown logicType for expression: '${(expression as any).logicType}'`);
  }
}
