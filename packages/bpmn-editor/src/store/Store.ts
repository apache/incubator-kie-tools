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

import { BpmnLatestModel } from "@kie-tools/bpmn-marshaller";
import { ComputedStateCache } from "@kie-tools/xyflow-react-kie-diagram/dist/store/ComputedStateCache";
import {
  XyFlowDiagramState,
  XyFlowReactKieDiagramEdgeStatus,
  XyFlowReactKieDiagramNodeStatus,
} from "@kie-tools/xyflow-react-kie-diagram/dist/store/State";
import { computeIsDiagramEditingInProgress } from "@kie-tools/xyflow-react-kie-diagram/dist/store/computed/computeIsDiagramEditingInProgress";
import { computeBoundaryEventIdsByAttachedBpmnElementId } from "./computeBoundaryEventIdsByAttachedBpmnElementId";
import { setNodeStatus } from "@kie-tools/xyflow-react-kie-diagram/dist/store/dispatch/setNodeStatus";
import { setEdgeStatus } from "@kie-tools/xyflow-react-kie-diagram/dist/store/dispatch/setEdgeStatus";
import { enableMapSet } from "immer";
import { create } from "zustand";
import { immer } from "zustand/middleware/immer";
import { BpmnDiagramEdgeData } from "../diagram/BpmnDiagramDomain";
import { BpmnNodeType } from "../diagram/BpmnDiagramDomain";
import { BpmnDiagramNodeData } from "../diagram/BpmnDiagramDomain";
import { Normalized, normalize } from "../normalization/normalize";
import { computeDiagramData } from "./computeDiagramData";

enableMapSet(); // Necessary because `Computed` has a lot of Maps and Sets.

export enum BpmnDiagramLhsPanel {
  NONE = "NONE",
  VARIABLES = "VARIABLES",
  CORRELATIONS = "CORRELATIONS",
  CUSTOM_TASKS = "CUSTOM_TASKS",
  PROPERTIES_MANAGEMENT = "PROPERTIES_MANAGEMENT",
}

export type BpmnXyFlowDiagramState = XyFlowDiagramState<State, BpmnNodeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>;

export interface State extends BpmnXyFlowDiagramState {
  // Read this to understand why we need computed as part of the store.
  // https://github.com/pmndrs/zustand/issues/132#issuecomment-1120467721
  computed: (s: State) => {
    getDiagramData(): ReturnType<typeof computeDiagramData>;
    getBoundaryEventIdsByAttachedBpmnElementId(): ReturnType<typeof computeBoundaryEventIdsByAttachedBpmnElementId>;
    isDiagramEditingInProgress(): boolean;
  };
  dispatch: (s: State) => {
    setNodeStatus: (nodeId: string, status: Partial<XyFlowReactKieDiagramNodeStatus>) => any;
    setEdgeStatus: (edgeId: string, status: Partial<XyFlowReactKieDiagramEdgeStatus>) => any;
    reset(model: Normalized<BpmnLatestModel>): void;
  };
  bpmn: {
    model: Normalized<BpmnLatestModel>;
  };
  settings: {
    isReadOnly: boolean;
  };
  focus: {
    consumableId: string | undefined;
  };
  propertiesPanel: {
    isOpen: boolean;
  };
  diagram: {
    overlaysPanel: {
      isOpen: boolean;
    };
    openLhsPanel: BpmnDiagramLhsPanel;
    overlays: {
      enableCustomNodeStyles: boolean;
    };
    isEditingStyle: boolean;
  };
}

export const getDefaultStaticState = (): Omit<State, "bpmn" | "computed" | "dispatch"> => ({
  focus: {
    consumableId: undefined,
  },
  settings: {
    isReadOnly: false,
  },
  propertiesPanel: {
    isOpen: true,
  },
  diagram: {
    overlaysPanel: {
      isOpen: false,
    },
    openLhsPanel: BpmnDiagramLhsPanel.NONE,
    overlays: {
      enableCustomNodeStyles: true,
    },
    isEditingStyle: false,
  },
  xyFlowReactKieDiagram: {
    snapGrid: { isEnabled: true, x: 20, y: 20 },
    _selectedNodes: [],
    _selectedEdges: [],
    draggingNodes: [],
    resizingNodes: [],
    draggingWaypoints: [],
    edgeIdBeingUpdated: undefined,
    dropTarget: undefined,
    ongoingConnection: undefined,
    newNodeProjection: undefined,
  },
});

export function createBpmnEditorStore(
  model: BpmnLatestModel,
  computedCache: ComputedStateCache<ReturnType<State["computed"]>>
) {
  const { diagram, ...defaultStaticState } = getDefaultStaticState();
  return create(
    immer<State>(() => ({
      ...defaultStaticState,
      bpmn: { model: normalize(model) },
      diagram,
      dispatch: (s) => ({
        reset: (model) => {
          s.bpmn.model = model;
          s.xyFlowReactKieDiagram._selectedNodes = [];
          s.xyFlowReactKieDiagram.draggingNodes = [];
          s.xyFlowReactKieDiagram.resizingNodes = [];
        },
        setNodeStatus: (nodeId, newStatus) => setNodeStatus(nodeId, newStatus, s),
        setEdgeStatus: (edgeId, newStatus) => setEdgeStatus(edgeId, newStatus, s),
      }),
      computed: (s) => ({
        isDiagramEditingInProgress: () =>
          computedCache.cached(
            "isDiagramEditingInProgress",
            (
              draggingNodesCount: number,
              resizingNodesCount: number,
              draggingWaypointsCount: number,
              isisEditingStyle: boolean
            ) =>
              computeIsDiagramEditingInProgress(draggingNodesCount, resizingNodesCount, draggingWaypointsCount) ||
              isisEditingStyle,
            [
              s.xyFlowReactKieDiagram.draggingNodes.length,
              s.xyFlowReactKieDiagram.resizingNodes.length,
              s.xyFlowReactKieDiagram.draggingWaypoints.length,
              s.diagram.isEditingStyle,
            ]
          ),

        getDiagramData: () =>
          computedCache.cached("getDiagramData", computeDiagramData, [
            s.bpmn.model.definitions,
            s.xyFlowReactKieDiagram,
            s.xyFlowReactKieDiagram.snapGrid,
            s.xyFlowReactKieDiagram.dropTarget,
            s.xyFlowReactKieDiagram.newNodeProjection,
          ]),

        getBoundaryEventIdsByAttachedBpmnElementId: () =>
          computedCache.cached(
            "getBoundaryEventIdsByAttachedBpmnElementId",
            computeBoundaryEventIdsByAttachedBpmnElementId,
            [s.bpmn.model.definitions]
          ),
      }),
    }))
  );
}
