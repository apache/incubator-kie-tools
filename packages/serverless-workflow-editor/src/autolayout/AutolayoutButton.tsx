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
import { useSwfEditorStoreApi } from "../store/StoreContext";
import { getAutoLayoutedInfo } from "./autoLayoutInfo";
import { applyAutoLayoutToSwf } from "../mutations/applyAutoLayoutToSwf";

export function AutolayoutButton() {
  const swfEditorStoreApi = useSwfEditorStoreApi();

  const onClick = React.useCallback(async () => {
    const state = swfEditorStoreApi.getState();
    const snapGrid = state.diagram.snapGrid;
    const nodesById = state.computed(state).getDiagramData().nodesById;
    const edgesById = state.computed(state).getDiagramData().edgesById;
    const nodes = state.computed(state).getDiagramData().nodes;

    await getAutoLayoutedInfo({
      __readonly_snapGrid: snapGrid,
      __readonly_nodesById: nodesById,
      __readonly_edgesById: edgesById,
      __readonly_nodes: nodes,
    }).then((autoLayout) =>
      swfEditorStoreApi.setState((s) => {
        applyAutoLayoutToSwf({
          state: s,
          __readonly_autoLayoutedInfo: autoLayout,
        });
      })
    );
  }, [swfEditorStoreApi]);

  return (
    <button className={"kie-swf-editor--autolayout-panel-toggle-button"} onClick={onClick} title={"Autolayout"}>
      <OptimizeIcon />
    </button>
  );
}
