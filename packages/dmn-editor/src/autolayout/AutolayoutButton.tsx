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
import { useAutoLayout } from "./AutoLayoutHook";
import { useDmnEditorStoreApi } from "../store/StoreContext";
import { autoLayout } from "./autoLayout";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";

export function AutolayoutButton() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();

  const applyAutoLayout = useAutoLayout();

  const onClick = React.useCallback(async () => {
    const state = dmnEditorStoreApi.getState();
    const snapGrid = state.diagram.snapGrid;
    const nodesById = state.computed(state).getDiagramData(externalModelsByNamespace).nodesById;
    const edgesById = state.computed(state).getDiagramData(externalModelsByNamespace).edgesById;
    const nodes = state.computed(state).getDiagramData(externalModelsByNamespace).nodes;
    const drgEdges = state.computed(state).getDiagramData(externalModelsByNamespace).drgEdges;
    const isAlternativeInputDataShape = state.computed(state).isAlternativeInputDataShape();

    const { autolayouted, parentNodesById } = await autoLayout({
      snapGrid,
      nodesById,
      edgesById,
      nodes,
      drgEdges,
      isAlternativeInputDataShape,
    });

    dmnEditorStoreApi.setState((s) => {
      applyAutoLayout({
        s,
        dmnShapesByHref: s.computed(s).indexedDrd().dmnShapesByHref,
        edges: s.computed(s).getDiagramData(externalModelsByNamespace).edges,
        edgesById: s.computed(s).getDiagramData(externalModelsByNamespace).edgesById,
        nodesById: s.computed(s).getDiagramData(externalModelsByNamespace).nodesById,
        autolayouted: autolayouted,
        parentNodesById: parentNodesById,
      });
    });
  }, [applyAutoLayout, dmnEditorStoreApi, externalModelsByNamespace]);

  return (
    <button className={"kie-dmn-editor--autolayout-panel-toggle-button"} onClick={onClick} title={"Autolayout (beta)"}>
      <OptimizeIcon />
    </button>
  );
}
