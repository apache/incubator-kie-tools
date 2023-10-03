import { ExpressionDefinition, ExpressionDefinitionLogicType } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DMN15__tDefinitions,
  DMN15__tFunctionDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { beeToDmn } from "../boxedExpressions/beeToDmn";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import { renameDrgElement } from "./renameNode";

export function updateExpression({
  definitions,
  expression,
  drgElementIndex,
}: {
  definitions: DMN15__tDefinitions;
  expression: ExpressionDefinition;
  drgElementIndex: number;
}) {
  const updatedWidthsMap = new Map<string, number[]>();
  const updatedExpression = beeToDmn(expression, updatedWidthsMap);

  const drgElement = definitions.drgElement?.[drgElementIndex];

  if (!drgElement) {
    throw new Error("Can't update expression for drgElement that doesn't exist.");
  }

  renameDrgElement({
    definitions,
    newName: updatedExpression?.["@_label"] ?? drgElement!["@_name"]!,
    index: drgElementIndex,
  });

  if (drgElement?.__$$element === "decision") {
    drgElement.expression = updatedExpression;
    drgElement.variable!["@_typeRef"] = updatedExpression?.["@_typeRef"] ?? drgElement.variable!["@_typeRef"];
  } else if (drgElement?.__$$element === "businessKnowledgeModel") {
    if (expression.logicType !== ExpressionDefinitionLogicType.Function) {
      throw new Error("Can't have an expression on a BKM that is not a Function.");
    }

    drgElement.encapsulatedLogic = updatedExpression as DMN15__tFunctionDefinition;
    drgElement.variable!["@_typeRef"] = updatedExpression?.["@_typeRef"] ?? drgElement.variable!["@_typeRef"];
  } else {
    throw new Error("Can't update expression for drgElement that is not a Decision or a BKM.");
  }

  const { widthsExtension, widths } = addOrGetDefaultDiagram({ definitions });
  const componentWidthsMap = widths.reduce(
    (acc, e) => (e["@_dmnElementRef"] ? acc.set(e["@_dmnElementRef"], e["kie:width"] ?? []) : acc),
    new Map<string, number[]>()
  );

  updatedWidthsMap.forEach((v, k) => componentWidthsMap.set(k, v));
  widthsExtension["kie:ComponentWidths"] = [...componentWidthsMap.entries()].map(([k, v]) => ({
    "@_dmnElementRef": k,
    "kie:width": v,
  }));
}
