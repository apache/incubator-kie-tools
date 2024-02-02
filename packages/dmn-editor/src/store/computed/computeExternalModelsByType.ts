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

import { ExternalDmnsIndex, ExternalModelsIndex, ExternalPmmlsIndex } from "../../DmnEditor";
import { getNamespaceOfDmnImport } from "../../includedModels/importNamespaces";
import { State } from "../Store";

export function computeExternalModelsByType(
  imports: State["dmn"]["model"]["definitions"]["import"],
  externalModelsByNamespace: ExternalModelsIndex | undefined
) {
  return (imports ?? []).reduce<{ dmns: ExternalDmnsIndex; pmmls: ExternalPmmlsIndex }>(
    (acc, _import) => {
      const externalModel = externalModelsByNamespace?.[getNamespaceOfDmnImport({ dmnImport: _import })];
      if (!externalModel) {
        console.warn(
          `DMN DIAGRAM: Can't index external model with namespace '${_import["@_namespace"]}' because it doesn't exist on the external models list.`
        );
        return acc;
      }

      if (externalModel.type === "dmn") {
        acc.dmns.set(_import["@_namespace"], externalModel);
      } else if (externalModel.type === "pmml") {
        acc.pmmls.set(_import["@_namespace"], externalModel);
      } else {
        console.warn("DMN EDITOR: Unknown external model type", externalModel);
      }

      return acc;
    },
    { dmns: new Map(), pmmls: new Map() }
  );
}
