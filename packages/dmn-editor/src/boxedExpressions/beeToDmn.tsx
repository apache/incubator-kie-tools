import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
} from "@kie-tools/boxed-expression-component/dist/api";
import { DMN14__tDecision } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { SPEC } from "../Spec";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  DECISION_TABLE_ANNOTATION_MIN_WIDTH,
  DECISION_TABLE_INPUT_MIN_WIDTH,
  DECISION_TABLE_OUTPUT_MIN_WIDTH,
} from "@kie-tools/boxed-expression-component/dist/resizing/WidthConstants";

/** Converts an ExpressionDefinition to a DMN JSON. This convertion is
 *  necessary for historical reasons, as the Boxed Expression Editor was
 *  created prior to the DMN Editor, needing to declare its own model. */
export function beeToDmn(
  expression: ExpressionDefinition,
  __widths: Map<string, number[]>
): DMN14__tDecision["expression"] {
  switch (expression.logicType) {
    case ExpressionDefinitionLogicType.Undefined:
      return undefined;
    case ExpressionDefinitionLogicType.Context:
      return {
        __$$element: "context",
        "@_id": expression.id,
        "@_typeRef": expression.dataType,
        contextEntry: [
          ...expression.contextEntries.map((e) => {
            __widths.set(expression.id, expression.entryInfoWidth ? [expression.entryInfoWidth] : []);
            return {
              "@_id": e.entryInfo.id,
              expression: beeToDmn(e.entryExpression, __widths)!,
              variable: {
                "@_name": e.entryInfo.name,
                "@_typeRef": e.entryInfo.dataType,
              },
            };
          }),
          ...(expression.result
            ? [
                {
                  "@_id": expression.result.id,
                  expression: beeToDmn(expression.result, __widths)!,
                },
              ]
            : []),
        ],
      };
    case ExpressionDefinitionLogicType.DecisionTable:
      __widths.set(expression.id, [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH]);
      return {
        __$$element: "decisionTable",
        "@_id": expression.id,
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
              text: s.name, // FIXME: Tiago --> This is really bad. Name is actually an expression!
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
            inputEntry: r.inputEntries.map((i) => ({ "@_id": i.id, text: i.content })),
            outputEntry: r.outputEntries.map((s) => ({ "@_id": s.id, text: s.content })),
            annotationEntry: r.annotationEntries.map((a) => ({ text: a })),
          };
        }),
      };
    case ExpressionDefinitionLogicType.Function:
      return {
        __$$element: "functionDefinition",
        "@_id": expression.id,
        "@_kind": expression.functionKind,
        "@_typeRef": expression.dataType,
        formalParameter: expression.formalParameters.map((p) => ({
          "@_id": p.id,
          "@_name": p.name,
          "@_typeRef": p.dataType,
        })),
        expression:
          expression.functionKind === "FEEL"
            ? beeToDmn(expression.expression, __widths)
            : expression.functionKind === "Java"
            ? (() => {
                __widths.set(
                  expression.id,
                  expression.classAndMethodNamesWidth ? [expression.classAndMethodNamesWidth] : []
                );
                return {
                  __$$element: "context",
                  contextEntry: [
                    {
                      "@_id": expression.classFieldId,
                      expression: { __$$element: "literalExpression", text: expression.className },
                      variable: { "@_name": SPEC.BOXED.FUNCTION.JAVA.classFieldName },
                    },
                    {
                      "@_id": expression.methodFieldId,
                      expression: { __$$element: "literalExpression", text: expression.methodName },
                      variable: { "@_name": SPEC.BOXED.FUNCTION.JAVA.methodSignatureFieldName },
                    },
                  ],
                };
              })()
            : expression.functionKind === "PMML"
            ? (() => {
                return {
                  __$$element: "context",
                  contextEntry: [
                    {
                      "@_id": expression.documentFieldId,
                      expression: { __$$element: "literalExpression", text: expression.document },
                      variable: { "@_name": SPEC.BOXED.FUNCTION.PMML.documentFieldName },
                    },
                    {
                      "@_id": expression.modelFieldId,
                      expression: { __$$element: "literalExpression", text: expression.model },
                      variable: { "@_name": SPEC.BOXED.FUNCTION.PMML.modelFieldName },
                    },
                  ],
                };
              })()
            : (() => {
                throw new Error(`Unknown Function kind '${(expression as any).functionKind}'.`);
              })(),
      };
    case ExpressionDefinitionLogicType.Invocation:
      return {
        __$$element: "invocation",
        "@_id": expression.id,
        "@_typeRef": expression.dataType,
        expression: {
          __$$element: "literalExpression",
          "@_id": expression.invokedFunction.id,
          text: expression.invokedFunction.name,
        },
        binding: expression.bindingEntries.map((e) => {
          __widths.set(expression.id, expression.entryInfoWidth ? [expression.entryInfoWidth] : []);
          return {
            "@_id": e.entryInfo.id,
            expression: beeToDmn(e.entryExpression, __widths)!,
            parameter: {
              "@_name": e.entryInfo.name,
              "@_typeRef": e.entryInfo.dataType,
            },
          };
        }),
      };
    case ExpressionDefinitionLogicType.List:
      return {
        __$$element: "list",
        "@_id": expression.id,
        "@_typeRef": expression.dataType,
        expression: expression.items.map((i) => beeToDmn(i, __widths)!),
      };
    case ExpressionDefinitionLogicType.Literal:
      __widths.set(expression.id, expression.width ? [expression.width] : []);
      return {
        __$$element: "literalExpression",
        "@_id": expression.id,
        "@_typeRef": expression.dataType,
        text: expression.content,
      };
    case ExpressionDefinitionLogicType.Relation:
      return {
        __$$element: "relation",
        "@_id": expression.id,
        "@_typeRef": expression.dataType,
        row: (expression.rows ?? []).map((r) => {
          return {
            "@_id": r.id,
            expression: r.cells.map((cell) => ({
              __$$element: "literalExpression",
              text: cell.content,
              id: cell.id,
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
