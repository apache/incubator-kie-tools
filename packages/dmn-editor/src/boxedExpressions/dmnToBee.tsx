import {
  ContextExpressionDefinitionEntry,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
} from "@kie-tools/boxed-expression-component/dist/api";
import {
  DMN14__tContext,
  DMN14__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { SPEC } from "../Spec";
import { DmnExpression, getUndefinedExpressionDefinition } from "./BoxedExpression";

/** Converts a DMN JSON to an ExpressionDefinition. This convertion is
 *  necessary for historical reasons, as the Boxed Expression Editor was
 *  created prior to the DMN Editor, needing to declare its own model. */
export function dmnToBee(widthsById: Map<string, number[]>, dmnExpr: DmnExpression): ExpressionDefinition {
  if (!dmnExpr) {
    return getUndefinedExpressionDefinition();
  } else if (dmnExpr.expression?.__$$element === "literalExpression") {
    const l = dmnExpr.expression;
    return {
      id: l["@_id"]!,
      name: l["@_label"],
      logicType: ExpressionDefinitionLogicType.Literal,
      dataType: (l["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      content: l.text,
      width: widthsById.get(l["@_id"]!)?.[0],
    };
  } else if (dmnExpr.expression?.__$$element === "decisionTable") {
    const d = dmnExpr.expression;
    return {
      id: d["@_id"]!,
      name: d["@_label"],
      logicType: ExpressionDefinitionLogicType.DecisionTable,
      dataType: (d["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      aggregation: d["@_aggregation"]
        ? DecisionTableExpressionDefinitionBuiltInAggregation[d["@_aggregation"]]
        : DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
      hitPolicy:
        (d["@_hitPolicy"] as DecisionTableExpressionDefinitionHitPolicy) ?? SPEC.BOXED.DECISION_TABLE.HitPolicy.default,
      input: (d.input ?? []).map((input, i) => ({
        idLiteralExpression: input.inputExpression["@_id"]!,
        id: input["@_id"]!,
        name: input["@_label"] ?? input.inputExpression["@_label"] ?? input.inputExpression.text ?? "",
        dataType: (input.inputExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
        width: widthsById.get(d["@_id"]!)?.[1 + i],
        //FIXME: Tiago --> Add clauseUnitaryTests?
      })),
      output: (d.output ?? []).map((output, i) => ({
        id: output["@_id"]!,
        name: output["@_label"] ?? output["@_name"] ?? "",
        dataType: (output["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
        width: widthsById.get(d["@_id"]!)?.[1 + (d.input ?? []).length + i],
        //FIXME: Tiago --> Add defaultOutputEntry?
        //FIXME: Tiago --> Add clauseUnaryTests?
      })),
      annotations: (d.annotation ?? []).map((a, i) => ({
        name: a["@_name"] ?? "",
        width: widthsById.get(d["@_id"]!)?.[1 + (d.input ?? []).length + (d.output ?? []).length + i],
      })),
      rules: (d.rule ?? []).map((r) => ({
        id: r["@_id"]!,
        inputEntries: (r.inputEntry ?? []).map((s) => ({ id: s["@_id"]!, content: s.text ?? "" })),
        outputEntries: (r.outputEntry ?? []).map((s) => ({ id: s["@_id"]!, content: s.text ?? "" })),
        annotationEntries: (r.annotationEntry ?? []).map((s) => s.text ?? ""),
      })),
    };
  } else if (dmnExpr.expression?.__$$element === "relation") {
    const r = dmnExpr.expression;
    return {
      id: r["@_id"]!,
      name: r["@_label"],
      logicType: ExpressionDefinitionLogicType.Relation,
      dataType: (r["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      rows: (r.row ?? []).map((row) => ({
        id: row["@_id"]!,
        // Assuming only literalExpressions are supported. Any other type of expression won't work for Relations.
        cells: ((row.expression as DMN14__tLiteralExpression[]) ?? []).map((s) => ({
          id: s["@_id"]!,
          content: s.text ?? "",
        })),
      })),
      columns: (r.column ?? []).map((c, i) => ({
        id: c["@_id"]!,
        name: c["@_label"] ?? c["@_name"] ?? "",
        dataType: (c["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
        width: widthsById.get(r["@_id"]!)?.[1 + i],
      })),
    };
  } else if (dmnExpr.expression?.__$$element === "context") {
    const c = dmnExpr.expression;

    const { contextEntries, result } = (c.contextEntry ?? []).reduce(
      (acc, e) => {
        if (!e.variable) {
          acc.result = dmnToBee(widthsById, e);
        } else {
          acc.contextEntries.push({
            entryInfo: {
              id: e.variable?.["@_id"] ?? e["@_id"]!,
              name: e.variable?.["@_label"] ?? e.variable?.["@_name"] ?? e["@_label"]!,
              dataType: (e.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
            },
            entryExpression: dmnToBee(widthsById, e),
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
      id: c["@_id"]!,
      dataType: c["@_typeRef"] as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Context as const,
      name: c["@_label"],
      entryInfoWidth: widthsById.get(c["@_id"] ?? "")?.[0],
      result,
      contextEntries,
    };
  } else if (dmnExpr.expression?.__$$element === "invocation") {
    const i = dmnExpr.expression;

    // From the spec:
    //
    // An Invocation contains a calledFunction, an Expression, which must evaluate to a function. Most
    // commonly, it is a LiteralExpression naming a BusinessKnowledgeModel.
    //
    // Source: https://www.omg.org/spec/DMN/1.4/PDF, PDF page 71, document page 57. Section "7.3.6 Invocation metamodel".
    const calledFunction = i.expression! as DMN14__tLiteralExpression;

    return {
      id: i["@_id"]!,
      dataType: (i["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Invocation as const,
      name: i["@_label"],
      entryInfoWidth: widthsById.get(i["@_id"] ?? "")?.[0],
      invokedFunction: {
        id: calledFunction["@_id"]!,
        name: calledFunction.text!,
      },
      bindingEntries: (i.binding ?? []).map((b) => ({
        entryInfo: {
          id: b.parameter["@_id"]!,
          name: b.parameter["@_name"],
          dataType: (b.parameter["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
        },
        entryExpression: dmnToBee(widthsById, b),
      })),
    };
  } else if (dmnExpr.expression?.__$$element === "functionDefinition") {
    const f = dmnExpr.expression;
    const basic = {
      id: f["@_id"]!,
      name: f["@_label"],
      dataType: f["@_typeRef"] as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Function as const,
      formalParameters: (f.formalParameter ?? []).map((p) => ({
        id: p["@_id"]!,
        name: p["@_name"]!,
        dataType: (p["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      })),
    };

    const kind = f["@_kind"] ?? SPEC.BOXED.FUNCTION.kind.default;
    switch (kind) {
      case "FEEL": {
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Feel,
          expression: dmnToBee(widthsById, f),
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
        const c = f.expression! as DMN14__tContext;
        const clazz = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === SPEC.BOXED.FUNCTION.JAVA.classFieldName
        );
        const method = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === SPEC.BOXED.FUNCTION.JAVA.methodSignatureFieldName
        );

        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Java,
          className: (clazz?.expression as DMN14__tLiteralExpression | undefined)?.text,
          classFieldId: clazz?.expression?.["@_id"],
          methodName: (method?.expression as DMN14__tLiteralExpression | undefined)?.text,
          methodFieldId: method?.expression?.["@_id"],
          // `clazz` and `method` would have the exact same width, as they're always in sync, so it doens't matter which one we use.
          classAndMethodNamesWidth: widthsById.get(clazz?.expression?.["@_id"] ?? "")?.[0],
        };
      }
      case "PMML": {
        // Special case, defined by the spec, where the implementation is a context expression with two fields.
        const c = f.expression as DMN14__tContext;
        const document = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === SPEC.BOXED.FUNCTION.PMML.documentFieldName
        );
        const model = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === SPEC.BOXED.FUNCTION.PMML.modelFieldName
        );
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Pmml,
          document: (document?.expression as DMN14__tLiteralExpression | undefined)?.text,
          documentFieldId: document?.expression?.["@_id"],
          model: (model?.expression as DMN14__tLiteralExpression | undefined)?.text,
          modelFieldId: model?.expression?.["@_id"],
        };
      }
      default:
        throw new Error(`Unknown function expression kind '${f["@_kind"]}'`);
    }
  } else if (dmnExpr.expression?.__$$element === "list") {
    const l = dmnExpr.expression;

    return {
      id: l["@_id"]!,
      name: l["@_label"],
      dataType: (l["@_typeRef"] ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.List as const,
      items: (l.expression ?? []).map((e) => dmnToBee(widthsById, { expression: e })),
    };
  } else {
    return getUndefinedExpressionDefinition();
  }
}
