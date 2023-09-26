import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import {
  findDataTypeById,
  traverseItemDefinition,
  traverseTypeRefedInExpressionHolders,
} from "../dataTypes/DataTypeSpec";
import { DataTypesById } from "../dataTypes/DataTypes";

export function renameItemDefinition({
  definitions,
  newName,
  dataTypesById,
  itemDefinitionId,
}: {
  definitions: DMN15__tDefinitions;
  newName: string;
  itemDefinitionId: string;
  dataTypesById: DataTypesById;
}) {
  const dataType = dataTypesById.get(itemDefinitionId);
  if (!dataType) {
    throw new Error(`Can't rename unnexistent item definition. ID ${itemDefinitionId}`);
  }

  if (dataType.namespace !== definitions["@_namespace"]) {
    throw new Error(
      `Can't rename an external item definition. ID ${itemDefinitionId}, Namespace: ${dataType.namespace}`
    );
  }

  newName = newName.trim();

  const { itemDefinition } = findDataTypeById({ definitions, itemDefinitionId: itemDefinitionId, dataTypesById });

  // Is top-level itemDefinition
  if (!dataType?.parentId) {
    traverseItemDefinition(definitions.itemDefinition ?? [], (item) => {
      if (item.typeRef === itemDefinition["@_name"]) {
        item.typeRef = newName;
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
        if (element.variable?.["@_typeRef"] === itemDefinition["@_name"]) {
          element.variable["@_typeRef"] = newName;
        }

        if (element.__$$element === "decision" || element.__$$element === "businessKnowledgeModel") {
          traverseTypeRefedInExpressionHolders(element, (typeRefed) => {
            if (typeRefed["@_typeRef"] === itemDefinition["@_name"]) {
              typeRefed["@_typeRef"] = newName;
            }
          });
        }
      }
    }
  }

  // Not top-level.. meaning that we need to update FEEL expressions referencing it
  else {
    // FIXME: Daniel --> Implement this...
  }

  itemDefinition["@_name"] = newName;
}
