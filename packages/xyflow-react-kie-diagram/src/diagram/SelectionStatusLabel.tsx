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
import * as RF from "reactflow";
import { useXyFlowReactKieDiagramStore, useXyFlowReactKieDiagramStoreApi } from "../store/Store";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { useKieDiagramI18n } from "../i18n";

export function SelectionStatusLabel() {
  const { i18n } = useKieDiagramI18n();
  const xyFlowStoreApi = RF.useStoreApi();

  const selectedNodesCount = useXyFlowReactKieDiagramStore(
    (s) => s.computed(s).getDiagramData().selectedNodesById.size
  );
  const selectedEdgesCount = useXyFlowReactKieDiagramStore(
    (s) => s.computed(s).getDiagramData().selectedEdgesById.size
  );
  const xyFlowReactKieDiagramStoreApi = useXyFlowReactKieDiagramStoreApi();

  React.useEffect(() => {
    if (selectedNodesCount >= 2) {
      xyFlowStoreApi.setState({ nodesSelectionActive: true });
    }
  }, [xyFlowStoreApi, selectedNodesCount]);

  const onClose = React.useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      xyFlowReactKieDiagramStoreApi.setState((state) => {
        state.xyFlowReactKieDiagram._selectedNodes = [];
        state.xyFlowReactKieDiagram._selectedEdges = [];
      });
    },
    [xyFlowReactKieDiagramStoreApi]
  );

  return (
    <>
      {(selectedNodesCount + selectedEdgesCount >= 2 && (
        <RF.Panel position={"top-center"}>
          <Label style={{ paddingLeft: "24px" }} onClose={onClose}>
            {(selectedEdgesCount === 0 && i18n.diagram.nodesSelected(selectedNodesCount)) ||
              (selectedNodesCount === 0 && i18n.diagram.edgesSelected(selectedEdgesCount)) ||
              `${selectedNodesCount === 1 ? i18n.diagram.nodeSelected(selectedNodesCount) : i18n.diagram.nodes(selectedNodesCount)}, 
              ${
                selectedEdgesCount === 1
                  ? i18n.diagram.edgeSelected(selectedEdgesCount)
                  : i18n.diagram.edges(selectedEdgesCount)
              } ${i18n.diagram.selected}`}
          </Label>
        </RF.Panel>
      )) || <></>}
    </>
  );
}
