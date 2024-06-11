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

import { DMN15__tImport } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { parseFeelQName, buildFeelQName } from "../feel/parseFeelQName";
import { builtInFeelTypeNames } from "./BuiltInFeelTypes";
import { DataTypeIndex } from "./DataTypes";
import { ExternalModelsIndex } from "../DmnEditor";

export function resolveTypeRef({
  typeRef,
  namespace,
  relativeToNamespace,
  thisDmnsImportsByNamespace,
  allTopLevelDataTypesByFeelName,
  externalModelsByNamespace,
}: {
  typeRef: string | undefined;
  namespace: string;
  relativeToNamespace: string;
  thisDmnsImportsByNamespace: Map<string, DMN15__tImport>;
  allTopLevelDataTypesByFeelName: DataTypeIndex;
  externalModelsByNamespace: ExternalModelsIndex | undefined;
}) {
  if (!typeRef) {
    return typeRef;
  }

  // Built-in types are not relative.
  if (builtInFeelTypeNames.has(typeRef)) {
    return typeRef;
  }

  // If it's a local data type, it's not relative.
  if (namespace === relativeToNamespace) {
    return typeRef;
  }

  const externalModel = externalModelsByNamespace?.[namespace];
  if (externalModel?.type !== "dmn") {
    throw new Error("DMN EDITOR: Can't find external DMN model for known external Data Type.");
  }

  const parsedTypeRefFeelQName = parseFeelQName(typeRef);

  const possibleNamespaces = [
    ...(externalModel.model.definitions.import ?? []).flatMap((i) =>
      i["@_name"] === (parsedTypeRefFeelQName.importName ?? "") ? i["@_namespace"] : []
    ),
    namespace, // Has to go last to override imports, as per the DMN specification.
  ];

  return possibleNamespaces.reduce(
    (acc, namespace) => {
      const thisDmnsImport = thisDmnsImportsByNamespace.get(namespace);
      if (!thisDmnsImport) {
        return acc;
      }

      const typeRefQName = buildFeelQName({
        type: "feel-qname",
        importName: thisDmnsImport["@_name"],
        localPart: parsedTypeRefFeelQName.localPart,
      });

      return allTopLevelDataTypesByFeelName.get(typeRefQName)?.feelName ?? acc;
    },
    buildFeelQName({
      type: "feel-qname",
      importName: "?",
      localPart: parsedTypeRefFeelQName.localPart,
    })
  );
}
