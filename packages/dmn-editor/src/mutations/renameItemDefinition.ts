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

import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import {
  findDataTypeById,
  traverseItemDefinitions,
  traverseTypeRefedInExpressionHolders,
} from "../dataTypes/DataTypeSpec";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { DataTypeIndex } from "../dataTypes/DataTypes";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import { IdentifiersRefactor } from "@kie-tools/dmn-language-service";

export function renameItemDefinition({
  definitions,
  newName,
  allDataTypesById,
  itemDefinitionId,
  externalDmnModelsByNamespaceMap,
  shouldRenameReferencedExpressions,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  newName: string;
  itemDefinitionId: string;
  allDataTypesById: DataTypeIndex;
  externalDmnModelsByNamespaceMap: Map<string, Normalized<DmnLatestModel>>;
  shouldRenameReferencedExpressions: boolean;
}) {
  const dataType = allDataTypesById.get(itemDefinitionId);
  if (!dataType) {
    throw new Error(`DMN MUTATION: Can't rename unnexistent item definition. ID ${itemDefinitionId}`);
  }

  if (dataType.namespace !== definitions["@_namespace"]) {
    throw new Error(
      `DMN MUTATION: Can't rename an external item definition. ID ${itemDefinitionId}, Namespace: ${dataType.namespace}`
    );
  }

  const trimmedNewName = newName.trim();

  const { itemDefinition } = findDataTypeById({ definitions, itemDefinitionId: itemDefinitionId, allDataTypesById });

  // Is top-level itemDefinition
  if (!dataType?.parentId) {
    traverseItemDefinitions(definitions.itemDefinition ?? [], (item) => {
      if (item.typeRef?.__$$text === itemDefinition["@_name"]) {
        item.typeRef = { __$$text: trimmedNewName };
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
          element.variable["@_typeRef"] = trimmedNewName;
        }

        if (element.__$$element === "decision" || element.__$$element === "businessKnowledgeModel") {
          traverseTypeRefedInExpressionHolders(element, (typeRefed) => {
            if (typeRefed["@_typeRef"] === itemDefinition["@_name"]) {
              typeRefed["@_typeRef"] = trimmedNewName;
            }
          });
        }
      }
    }
  }

  // Not top-level.. meaning that we need to update FEEL expressions referencing it
  else {
    if (shouldRenameReferencedExpressions) {
      const identifiersRefactor = new IdentifiersRefactor({
        writeableDmnDefinitions: definitions,
        _readonly_externalDmnModelsByNamespaceMap: externalDmnModelsByNamespaceMap,
      });

      identifiersRefactor.rename({ identifierUuid: itemDefinitionId, newName: trimmedNewName });
    }
  }

  itemDefinition["@_name"] = trimmedNewName;
}
