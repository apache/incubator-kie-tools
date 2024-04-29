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
  DMN15__tContext,
  DMN15__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import {
  ContextExpressionDefinitionEntry,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  FunctionExpressionDefinitionKind,
  GwtExpressionDefinition,
  GwtExpressionDefinitionLogicType,
} from "./types";
import { DmnBuiltInDataType, BoxedExpression, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";

/** Converts a BoxedExpression to a GwtExpressionDefinition. This convertion is
 *  necessary for historical reasons, as the GWT-based DMN Editor implements its
 *  own model for Boxed Expressions. */
export function beeToGwt(
  widthsById: Map<string, number[]>,
  expression: BoxedExpression | undefined,
  typeRef?: string
): GwtExpressionDefinition {
  if (!expression) {
    return { ...getUndefinedExpressionDefinition(), ...(typeRef ? { dataType: typeRef as DmnBuiltInDataType } : {}) };
  }

  if (expression.__$$element === "literalExpression") {
    return {
      id: expression["@_id"]!,
      name: expression["@_label"],
      logicType: GwtExpressionDefinitionLogicType.Literal,
      dataType: (expression["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      content: expression.text?.__$$text,
      width: widthsById.get(expression["@_id"]!)?.[0],
    };
  } else if (expression.__$$element === "decisionTable") {
    return {
      id: expression["@_id"]!,
      name: expression["@_label"],
      logicType: GwtExpressionDefinitionLogicType.DecisionTable,
      dataType: (expression["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      aggregation: expression["@_aggregation"]
        ? DecisionTableExpressionDefinitionBuiltInAggregation[expression["@_aggregation"]]
        : DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
      hitPolicy:
        (expression["@_hitPolicy"] as DecisionTableExpressionDefinitionHitPolicy) ??
        DMN15_SPEC.BOXED.DECISION_TABLE.HitPolicy.default,
      input: (expression.input ?? []).map((input, i) => ({
        idLiteralExpression: input.inputExpression["@_id"]!,
        id: input["@_id"]!,
        name: input["@_label"] ?? input.inputExpression["@_label"] ?? input.inputExpression.text?.__$$text ?? "",
        dataType: (input.inputExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
        width: widthsById.get(expression["@_id"]!)?.[1 + i],
      })),
      output: (expression.output ?? []).map((output, i) => ({
        id: output["@_id"]!,
        name: output["@_label"] ?? output["@_name"] ?? "",
        dataType: (output["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
        width: widthsById.get(expression["@_id"]!)?.[1 + (expression.input ?? []).length + i],
      })),
      annotations: (expression.annotation ?? []).map((a, i) => ({
        name: a["@_name"] ?? "",
        width: widthsById.get(expression["@_id"]!)?.[
          1 + (expression.input ?? []).length + (expression.output ?? []).length + i
        ],
      })),
      rules: (expression.rule ?? []).map((r) => ({
        id: r["@_id"]!,
        inputEntries: (r.inputEntry ?? []).map((i) => ({
          id: i["@_id"]!,
          content: i.text?.__$$text ?? "",
        })),
        outputEntries: (r.outputEntry ?? []).map((o) => ({
          id: o["@_id"]!,
          content: o.text?.__$$text ?? "",
        })),
        annotationEntries: (r.annotationEntry ?? []).map((a) => a.text?.__$$text ?? ""),
      })),
    };
  } else if (expression.__$$element === "relation") {
    return {
      id: expression["@_id"]!,
      name: expression["@_label"],
      logicType: GwtExpressionDefinitionLogicType.Relation,
      dataType: (expression["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      rows: (expression.row ?? []).map((row) => ({
        id: row["@_id"]!,
        // Assuming only literalExpressions are supported. Any other type of expression won't work for Relations.
        cells: ((row.expression as DMN15__tLiteralExpression[]) ?? []).map((s) => ({
          id: s["@_id"]!,
          content: s.text?.__$$text ?? "",
        })),
      })),
      columns: (expression.column ?? []).map((c, i) => ({
        id: c["@_id"]!,
        name: c["@_label"] ?? c["@_name"] ?? "",
        dataType: (c["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
        width: widthsById.get(expression["@_id"]!)?.[1 + i],
      })),
    };
  } else if (expression.__$$element === "context") {
    const { contextEntries, result } = (expression.contextEntry ?? []).reduce(
      (acc, e) => {
        if (!e.variable) {
          acc.result = beeToGwt(widthsById, e.expression, expression["@_typeRef"]);
        } else {
          acc.contextEntries.push({
            entryInfo: {
              id: e.variable?.["@_id"] ?? e["@_id"]!,
              name: e.variable?.["@_label"] ?? e.variable?.["@_name"] ?? e["@_label"]!,
              dataType: (e.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
            },
            entryExpression: beeToGwt(
              widthsById,
              e.expression,
              (e.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType
            ),
          });
        }

        return acc;
      },
      { contextEntries: [], result: getUndefinedExpressionDefinition() } as {
        contextEntries: ContextExpressionDefinitionEntry[];
        result: GwtExpressionDefinition;
      }
    );

    return {
      id: expression["@_id"]!,
      dataType: expression["@_typeRef"] as DmnBuiltInDataType,
      logicType: GwtExpressionDefinitionLogicType.Context as const,
      name: expression["@_label"],
      entryInfoWidth: widthsById.get(expression["@_id"] ?? "")?.[0],
      result,
      contextEntries,
    };
  } else if (expression.__$$element === "invocation") {
    // From the spec:
    //
    // An Invocation contains a calledFunction, an Expression, which must evaluate to a function. Most
    // commonly, it is a LiteralExpression naming a BusinessKnowledgeModel.
    //
    // Source: https://www.omg.org/spec/DMN/1.4/PDF. PDF page 71, document page 57. Section "7.3.6 Invocation metamodel".
    const calledFunction = expression.expression! as DMN15__tLiteralExpression;

    return {
      id: expression["@_id"]!,
      dataType: (expression["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      logicType: GwtExpressionDefinitionLogicType.Invocation as const,
      name: expression["@_label"],
      entryInfoWidth: widthsById.get(expression["@_id"] ?? "")?.[0],
      invokedFunction: {
        id: calledFunction["@_id"]!,
        name: calledFunction.text?.__$$text ?? "",
      },
      bindingEntries: (expression.binding ?? []).map((b) => ({
        entryInfo: {
          id: b.parameter["@_id"]!,
          name: b.parameter["@_name"],
          dataType: (b.parameter["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
        },
        entryExpression: beeToGwt(
          widthsById,
          b.expression,
          (b.parameter["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType
        ),
      })),
    };
  } else if (expression.__$$element === "functionDefinition") {
    const basic = {
      id: expression["@_id"]!,
      name: expression["@_label"],
      dataType: expression["@_typeRef"] as DmnBuiltInDataType,
      logicType: GwtExpressionDefinitionLogicType.Function as const,
      formalParameters: (expression.formalParameter ?? []).map((p) => ({
        id: p["@_id"]!,
        name: p["@_name"]!,
        dataType: (p["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      })),
    };

    const kind = expression["@_kind"] ?? DMN15_SPEC.BOXED.FUNCTION.kind.default;
    switch (kind) {
      case "FEEL": {
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Feel,
          expression: beeToGwt(widthsById, expression.expression),
        };
      }
      case "Java": {
        // From the spec:
        //
        // For FEEL functions, denoted by Kind FEEL or by omission of Kind, the Body SHALL be a FEEL expression
        // that references the parameters. For externally defined functions denoted by Kind Java, the Body SHALL be a
        // context as described in 10.3.2.13.3 and the form of the mapping information SHALL be the java form. For
        // externally defined functions denoted by Kind PMML, the Body SHALL be a context as described in 10.3.2.13.3
        // and the form of the mapping information SHALL be the pmml form.
        //
        // Source: https://www.omg.org/spec/DMN/1.4/PDF, PDF page 106, document page 92. Section "10.2.1.7 Boxed Function".
        const c = expression.expression! as DMN15__tContext;
        const clazz = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === DMN15_SPEC.BOXED.FUNCTION.JAVA.classFieldName
        );
        const method = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === DMN15_SPEC.BOXED.FUNCTION.JAVA.methodSignatureFieldName
        );

        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Java,
          className: (clazz?.expression as DMN15__tLiteralExpression | undefined)?.text?.__$$text,
          classFieldId: clazz?.expression?.["@_id"],
          methodName: (method?.expression as DMN15__tLiteralExpression | undefined)?.text?.__$$text,
          methodFieldId: method?.expression?.["@_id"],
          classAndMethodNamesWidth: widthsById.get(expression["@_id"] ?? "")?.[1],
        };
      }
      case "PMML": {
        // Special case, defined by the spec, where the implementation is a context expression with two fields.
        const c = expression.expression as DMN15__tContext;
        const document = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === DMN15_SPEC.BOXED.FUNCTION.PMML.documentFieldName
        );
        const model = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === DMN15_SPEC.BOXED.FUNCTION.PMML.modelFieldName
        );
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Pmml,
          document: (document?.expression as DMN15__tLiteralExpression | undefined)?.text?.__$$text.replaceAll(`"`, ``), // Sometimes this is stored as a FEEL string. We don't need the quotes to show in the screen.
          documentFieldId: document?.expression?.["@_id"],
          model: (model?.expression as DMN15__tLiteralExpression | undefined)?.text?.__$$text.replaceAll(`"`, ``), // Sometimes this is stored as a FEEL string. We don't need the quotes to show in the screen.
          modelFieldId: model?.expression?.["@_id"],
        };
      }
      default:
        throw new Error(`Unknown function expression kind '${expression["@_kind"]}'`);
    }
  } else if (expression.__$$element === "list") {
    return {
      id: expression["@_id"]!,
      name: expression["@_label"],
      dataType: (expression["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      logicType: GwtExpressionDefinitionLogicType.List as const,
      items: (expression.expression ?? []).map((e) => beeToGwt(widthsById, e, expression["@_typeRef"])),
    };
  } else {
    return getUndefinedExpressionDefinition();
  }
}

export function getUndefinedExpressionDefinition(): GwtExpressionDefinition {
  return {
    id: generateUuid(),
    logicType: GwtExpressionDefinitionLogicType.Undefined,
    dataType: DmnBuiltInDataType.Undefined,
  };
}
