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
}): void {
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

    if (!updatedExpression?.__$$element) {
      throw new Error("Can't determine expression type without its __$$element property.");
    }

    // We remove the __$$element here, because otherwise the "functionDefinition" element name will be used in the final XML.
    const { __$$element, ..._updateExpression } = updatedExpression;
    drgElement.encapsulatedLogic = _updateExpression as DMN15__tFunctionDefinition;
    drgElement.variable!["@_typeRef"] = _updateExpression?.["@_typeRef"] ?? drgElement.variable!["@_typeRef"];
  } else {
    throw new Error("Can't update expression for drgElement that is not a Decision or a BKM.");
  }

  const { widthsExtension, widths } = addOrGetDefaultDiagram({ definitions });
  const componentWidthsMap = widths.reduce(
    (acc, e) =>
      e["@_dmnElementRef"]
        ? acc.set(
            e["@_dmnElementRef"],
            (e["kie:width"] ?? []).map((vv) => vv.__$$text)
          )
        : acc,
    new Map<string, number[]>()
  );

  updatedWidthsMap.forEach((v, k) => componentWidthsMap.set(k, v));
  widthsExtension["kie:ComponentWidths"] = [...componentWidthsMap.entries()].map(([k, v]) => ({
    "@_dmnElementRef": k,
    "kie:width": v.map((vv) => ({ __$$text: vv })),
  }));
}
