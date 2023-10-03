import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { traverseTypeRefedInExpressionHolders } from "../dataTypes/DataTypeSpec";
import { buildFeelQName, parseFeelQName } from "../feel/parseFeelQName";
import { DataTypeIndex } from "../dataTypes/DataTypes";

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
          const feelQName = parseFeelQName(element.variable?.["@_typeRef"]);
          element.variable["@_typeRef"] = buildFeelQName({
            type: "feel-qname",
            importName: trimmedNewName,
            localPart: feelQName.localPart,
          });
        }
      }

      if (element.__$$element === "decision" || element.__$$element === "businessKnowledgeModel") {
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

  _import["@_name"] = trimmedNewName;
}
