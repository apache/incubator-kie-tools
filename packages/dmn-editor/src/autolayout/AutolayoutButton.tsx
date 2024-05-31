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

import * as React from "react";
import OptimizeIcon from "@patternfly/react-icons/dist/js/icons/optimize-icon";
import { useDmnEditorStoreApi } from "../store/StoreContext";
import { getAutoLayoutedInfo, mutateDiagramWithAutoLayoutInfo } from "./autoLayout";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";

export function AutolayoutButton() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();

  const onClick = React.useCallback(async () => {
    const state = dmnEditorStoreApi.getState();
    const snapGrid = state.diagram.snapGrid;
    const nodesById = state.computed(state).getDiagramData(externalModelsByNamespace).nodesById;
    const edgesById = state.computed(state).getDiagramData(externalModelsByNamespace).edgesById;
    const nodes = state.computed(state).getDiagramData(externalModelsByNamespace).nodes;
    const drgEdges = state.computed(state).getDiagramData(externalModelsByNamespace).drgEdges;
    const isAlternativeInputDataShape = state.computed(state).isAlternativeInputDataShape();

    const { __readonly_autoLayoutedInfo, __readonly_parentNodesById } = await getAutoLayoutedInfo({
      __readonly_snapGrid: snapGrid,
      __readonly_nodesById: nodesById,
      __readonly_edgesById: edgesById,
      __readonly_nodes: nodes,
      __readonly_drgEdges: drgEdges,
      __readonly_isAlternativeInputDataShape: isAlternativeInputDataShape,
    });

    dmnEditorStoreApi.setState((s) => {
      mutateDiagramWithAutoLayoutInfo({
        state: s,
        __readonly_dmnShapesByHref: s.computed(s).indexedDrd().dmnShapesByHref,
        __readonly_edges: s.computed(s).getDiagramData(externalModelsByNamespace).edges,
        __readonly_edgesById: s.computed(s).getDiagramData(externalModelsByNamespace).edgesById,
        __readonly_nodesById: s.computed(s).getDiagramData(externalModelsByNamespace).nodesById,
        __readonly_autoLayoutedInfo,
        __readonly_parentNodesById,
        __readonly_drdIndex: s.computed(s).getDrdIndex(),
      });
    });
  }, [dmnEditorStoreApi, externalModelsByNamespace]);

  return (
    <button className={"kie-dmn-editor--autolayout-panel-toggle-button"} onClick={onClick} title={"Autolayout (beta)"}>
      <OptimizeIcon />
    </button>
  );
}
