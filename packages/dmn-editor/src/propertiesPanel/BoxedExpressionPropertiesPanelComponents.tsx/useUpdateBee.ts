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

import { useCallback, useMemo } from "react";
import { ExpressionPath, getDmnObjectByPath } from "../../boxedExpressions/boxedExpressionIndex";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { buildXmlHref } from "../../xml/xmlHrefs";
import { AllExpressionsWithoutTypes } from "../../dataTypes/DataTypeSpec";
import { useExternalModels } from "../../includedModels/DmnEditorDependenciesContext";

export function useBoxedExpressionUpdater<T extends AllExpressionsWithoutTypes>(
  expressionPath: ExpressionPath[] | undefined
) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const activeDrgElementId = useDmnEditorStore((s) => s.boxedExpressionEditor.activeDrgElementId);
  const { externalModelsByNamespace } = useExternalModels();

  const node = useMemo(() => {
    const nodesById = dmnEditorStoreApi
      .getState()
      .computed(dmnEditorStoreApi.getState())
      .getDiagramData(externalModelsByNamespace).nodesById;
    return activeDrgElementId ? nodesById.get(buildXmlHref({ id: activeDrgElementId })) : undefined;
  }, [activeDrgElementId, dmnEditorStoreApi, externalModelsByNamespace]);

  return useCallback(
    (consumer: (dmnObject: T) => void) => {
      dmnEditorStoreApi.setState((state) => {
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "businessKnowledgeModel") {
          const dmnObject = getDmnObjectByPath(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tBusinessKnowledgeModel)
              ?.encapsulatedLogic
          );
          dmnObject && consumer(dmnObject as T);
        }
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "decision") {
          const dmnObject = getDmnObjectByPath(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tDecision)?.expression
          );
          dmnObject && consumer(dmnObject as T);
        }
      });
    },
    [dmnEditorStoreApi, expressionPath, node?.data.index]
  );
}
