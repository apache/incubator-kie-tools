import {
  DMN15__tDefinitions,
  DMN15__tFunctionDefinition,
  DMN15__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import {
  traverseExpressionsInExpressionHolders,
  traverseItemDefinitions,
  traverseTypeRefedInExpressionHolders,
} from "../dataTypes/DataTypeSpec";
import { buildFeelQName, parseFeelQName } from "../feel/parseFeelQName";
import { DataTypeIndex } from "../dataTypes/DataTypes";
import { DMN15__tContext } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DMN15_SPEC } from "../Dmn15Spec";

export function renameImport({
  definitions,
  newName,
  allTopLevelDataTypesByFeelName,
  index,
}: {
  definitions: DMN15__tDefinitions;
  allTopLevelDataTypesByFeelName: DataTypeIndex;
  newName: string;
  index: number;
}) {
  const trimmedNewName = newName.trim();

  const _import = definitions.import![index];

  traverseItemDefinitions(definitions.itemDefinition ?? [], (item) => {
    if (item.typeRef) {
      const feelQName = parseFeelQName(item.typeRef);
      if (allTopLevelDataTypesByFeelName.get(item.typeRef)?.namespace === _import["@_namespace"]) {
        item.typeRef = buildFeelQName({
          type: "feel-qname",
          importName: trimmedNewName,
          localPart: feelQName.localPart,
        });
      }
    }
  });

  definitions.drgElement ??= [];
  for (let i = 0; i < definitions.drgElement.length; i++) {
    const element = definitions.drgElement[i];
    if (
      element.__$$element === "inputData" ||
      element.__$$element === "decision" ||
      element.__$$element === "businessKnowledgeModel" ||
      element.__$$element === "decisionService"
    ) {
      if (element.variable?.["@_typeRef"]) {
        if (allTopLevelDataTypesByFeelName.get(element.variable?.["@_typeRef"])?.namespace === _import["@_namespace"]) {
          const feelQName = parseFeelQName(element.variable["@_typeRef"]);
          element.variable["@_typeRef"] = buildFeelQName({
            type: "feel-qname",
            importName: trimmedNewName,
            localPart: feelQName.localPart,
          });
        }
      }

      if (element.__$$element === "decision" || element.__$$element === "businessKnowledgeModel") {
        traverseExpressionsInExpressionHolders(element, (expression, __$$element) => {
          if (__$$element === "functionDefinition") {
            const e = expression as DMN15__tFunctionDefinition;
            if (e["@_kind"] === "PMML") {
              const pmmlDocument = (e.expression as DMN15__tContext).contextEntry?.find(
                ({ variable }) => variable?.["@_name"] === DMN15_SPEC.BOXED.FUNCTION.PMML.documentFieldName
              );

              const pmmlDocumentLiteralExpression = pmmlDocument?.expression as DMN15__tLiteralExpression | undefined;
              if (pmmlDocumentLiteralExpression?.text === _import["@_name"]) {
                pmmlDocumentLiteralExpression.text = trimmedNewName;
              }
            }
          }
        });

        traverseTypeRefedInExpressionHolders(element, (typeRefed) => {
          if (typeRefed["@_typeRef"]) {
            if (allTopLevelDataTypesByFeelName.get(typeRefed["@_typeRef"])?.namespace === _import["@_namespace"]) {
              const feelQName = parseFeelQName(typeRefed["@_typeRef"]);
              typeRefed["@_typeRef"] = buildFeelQName({
                type: "feel-qname",
                importName: trimmedNewName,
                localPart: feelQName.localPart,
              });
            }
          }
        });
      }
    }
  }

  // TODO: Tiago --> Update the "document" entry of PMML functions that were pointing to the renamed included PMML model.

  // FIXME: Daniel --> Update FEEL expressions that contain references to this import.

  _import["@_name"] = trimmedNewName;
}
