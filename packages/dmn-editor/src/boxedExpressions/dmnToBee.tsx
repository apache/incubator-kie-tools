import {
  ContextExpressionDefinitionEntry,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import {
  DMN15__tContext,
  DMN15__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DMN15_SPEC } from "../Dmn15Spec";
import { AllExpressions } from "../dataTypes/DataTypeSpec";

/** Converts a DMN JSON to an ExpressionDefinition. This convertion is
 *  necessary for historical reasons, as the Boxed Expression Editor was
 *  created prior to the DMN Editor, needing to declare its own model. */
export function dmnToBee(
  widthsById: Map<string, number[]>,
  expressionHolder: { expression?: AllExpressions } | undefined,
  typeRef?: string
): ExpressionDefinition {
  if (!expressionHolder?.expression) {
    return { ...getUndefinedExpressionDefinition(), ...(typeRef ? { dataType: typeRef as DmnBuiltInDataType } : {}) };
  }

  const expr = expressionHolder.expression;

  if (expr.__$$element === "literalExpression") {
    return {
      id: expr["@_id"]!,
      name: expr["@_label"],
      logicType: ExpressionDefinitionLogicType.Literal,
      dataType: (expr["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      content: expr.text?.__$$text,
      width: widthsById.get(expr["@_id"]!)?.[0],
    };
  } else if (expr.__$$element === "decisionTable") {
    return {
      id: expr["@_id"]!,
      name: expr["@_label"],
      logicType: ExpressionDefinitionLogicType.DecisionTable,
      dataType: (expr["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      aggregation: expr["@_aggregation"]
        ? DecisionTableExpressionDefinitionBuiltInAggregation[expr["@_aggregation"]]
        : DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
      hitPolicy:
        (expr["@_hitPolicy"] as DecisionTableExpressionDefinitionHitPolicy) ??
        DMN15_SPEC.BOXED.DECISION_TABLE.HitPolicy.default,
      input: (expr.input ?? []).map((input, i) => ({
        idLiteralExpression: input.inputExpression["@_id"]!,
        id: input["@_id"]!,
        name: input["@_label"] ?? input.inputExpression["@_label"] ?? input.inputExpression.text?.__$$text ?? "",
        dataType: (input.inputExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
        width: widthsById.get(expr["@_id"]!)?.[1 + i],
      })),
      output: (expr.output ?? []).map((output, i) => ({
        id: output["@_id"]!,
        name: output["@_label"] ?? output["@_name"] ?? "",
        dataType: (output["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
        width: widthsById.get(expr["@_id"]!)?.[1 + (expr.input ?? []).length + i],
      })),
      annotations: (expr.annotation ?? []).map((a, i) => ({
        name: a["@_name"] ?? "",
        width: widthsById.get(expr["@_id"]!)?.[1 + (expr.input ?? []).length + (expr.output ?? []).length + i],
      })),
      rules: (expr.rule ?? []).map((r) => ({
        id: r["@_id"]!,
        inputEntries: (r.inputEntry ?? []).map((i) => ({ id: i["@_id"]!, content: i.text?.__$$text ?? "" })),
        outputEntries: (r.outputEntry ?? []).map((o) => ({ id: o["@_id"]!, content: o.text?.__$$text ?? "" })),
        annotationEntries: (r.annotationEntry ?? []).map((a) => a.text?.__$$text ?? ""),
      })),
    };
  } else if (expr.__$$element === "relation") {
    return {
      id: expr["@_id"]!,
      name: expr["@_label"],
      logicType: ExpressionDefinitionLogicType.Relation,
      dataType: (expr["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      rows: (expr.row ?? []).map((row) => ({
        id: row["@_id"]!,
        // Assuming only literalExpressions are supported. Any other type of expression won't work for Relations.
        cells: ((row.expression as DMN15__tLiteralExpression[]) ?? []).map((s) => ({
          id: s["@_id"]!,
          content: s.text?.__$$text ?? "",
        })),
      })),
      columns: (expr.column ?? []).map((c, i) => ({
        id: c["@_id"]!,
        name: c["@_label"] ?? c["@_name"] ?? "",
        dataType: (c["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
        width: widthsById.get(expr["@_id"]!)?.[1 + i],
      })),
    };
  } else if (expr.__$$element === "context") {
    const { contextEntries, result } = (expr.contextEntry ?? []).reduce(
      (acc, e) => {
        if (!e.variable) {
          acc.result = dmnToBee(widthsById, e, expr["@_typeRef"]);
        } else {
          acc.contextEntries.push({
            entryInfo: {
              id: e.variable?.["@_id"] ?? e["@_id"]!,
              name: e.variable?.["@_label"] ?? e.variable?.["@_name"] ?? e["@_label"]!,
              dataType: (e.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
            },
            entryExpression: dmnToBee(
              widthsById,
              e,
              (e.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType
            ),
          });
        }

        return acc;
      },
      { contextEntries: [], result: getUndefinedExpressionDefinition() } as {
        contextEntries: ContextExpressionDefinitionEntry[];
        result: ExpressionDefinition;
      }
    );

    return {
      id: expr["@_id"]!,
      dataType: expr["@_typeRef"] as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Context as const,
      name: expr["@_label"],
      entryInfoWidth: widthsById.get(expr["@_id"] ?? "")?.[0],
      result,
      contextEntries,
    };
  } else if (expr.__$$element === "invocation") {
    // From the spec:
    //
    // An Invocation contains a calledFunction, an Expression, which must evaluate to a function. Most
    // commonly, it is a LiteralExpression naming a BusinessKnowledgeModel.
    //
    // Source: https://www.omg.org/spec/DMN/1.4/PDF. PDF page 71, document page 57. Section "7.3.6 Invocation metamodel".
    const calledFunction = expr.expression! as DMN15__tLiteralExpression;

    return {
      id: expr["@_id"]!,
      dataType: (expr["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Invocation as const,
      name: expr["@_label"],
      entryInfoWidth: widthsById.get(expr["@_id"] ?? "")?.[0],
      invokedFunction: {
        id: calledFunction["@_id"]!,
        name: calledFunction.text?.__$$text ?? "",
      },
      bindingEntries: (expr.binding ?? []).map((b) => ({
        entryInfo: {
          id: b.parameter["@_id"]!,
          name: b.parameter["@_name"],
          dataType: (b.parameter["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
        },
        entryExpression: dmnToBee(
          widthsById,
          b,
          (b.parameter["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType
        ),
      })),
    };
  } else if (expr.__$$element === "functionDefinition") {
    const basic = {
      id: expr["@_id"]!,
      name: expr["@_label"],
      dataType: expr["@_typeRef"] as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Function as const,
      formalParameters: (expr.formalParameter ?? []).map((p) => ({
        id: p["@_id"]!,
        name: p["@_name"]!,
        dataType: (p["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      })),
    };

    const kind = expr["@_kind"] ?? DMN15_SPEC.BOXED.FUNCTION.kind.default;
    switch (kind) {
      case "FEEL": {
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Feel,
          expression: dmnToBee(widthsById, expr),
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
        const c = expr.expression! as DMN15__tContext;
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
          classAndMethodNamesWidth: widthsById.get(expr["@_id"] ?? "")?.[1],
        };
      }
      case "PMML": {
        // Special case, defined by the spec, where the implementation is a context expression with two fields.
        const c = expr.expression as DMN15__tContext;
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
        throw new Error(`Unknown function expression kind '${expr["@_kind"]}'`);
    }
  } else if (expr.__$$element === "list") {
    return {
      id: expr["@_id"]!,
      name: expr["@_label"],
      dataType: (expr["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.List as const,
      items: (expr.expression ?? []).map((e) => dmnToBee(widthsById, { expression: e }, expr["@_typeRef"])),
    };
  } else {
    return getUndefinedExpressionDefinition();
  }
}

export function getUndefinedExpressionDefinition(): ExpressionDefinition {
  return {
    id: generateUuid(),
    logicType: ExpressionDefinitionLogicType.Undefined,
    dataType: DmnBuiltInDataType.Undefined,
  };
}
