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

import { enableMapSet, immerable } from "immer";
import * as RF from "reactflow";
import { create } from "zustand";
import { immer } from "zustand/middleware/immer";
import { SwfDiagramNodeData } from "../diagram/nodes/SwfNodes";
import { ComputedStateCache } from "./ComputedStateCache";
import { computeDiagramData } from "./computed/computeDiagramData";
import { computeIndexedSwf } from "./computed/computeIndexes";
import { computeIsDropTargetNodeValidForSelection } from "./computed/computeIsDropTargetNodeValidForSelection";
import { DEFAULT_VIEWPORT } from "../diagram/Diagram";

import { Specification } from "@serverlessworkflow/sdk-typescript";

enableMapSet(); // Necessary because `Computed` has a lot of Maps and Sets.

export interface SwfEditorDiagramNodeStatus {
  selected: boolean;
  dragging: boolean;
}

export interface SwfEditorDiagramEdgeStatus {
  selected: boolean;
  draggingWaypoint: boolean;
}

export interface SnapGrid {
  isEnabled: boolean;
  x: number;
  y: number;
}

export type DropTargetNode = undefined | RF.Node<SwfDiagramNodeData>;

export interface State {
  dispatch: (s: State) => Dispatch;
  computed: (s: State) => Computed;
  layout: (s: State) => Layout;
  swf: { model: Specification.IWorkflow };
  focus: {
    consumableId: string | undefined;
  };
  diagram: {
    autoLayout: {
      canAutoGenerate: boolean;
    };
    __unsafeSwfIndex: number;
    edgeIdBeingUpdated: string | undefined;
    dropTargetNode: DropTargetNode;
    ongoingConnection: RF.OnConnectStartParams | undefined;
    overlaysPanel: {
      isOpen: boolean;
    };
    overlays: {
      enableNodeHierarchyHighlight: boolean;
    };
    snapGrid: SnapGrid;
    nodeIds: Array<string>;
    nodesPosition: Array<RF.XYPosition>;
    edgeIds: Array<string>;
    edgeWaypoints: Array<RF.XYPosition[]>;
    _selectedNodes: Array<string>;
    _selectedEdges: Array<string>;
    draggingNodes: Array<string>;
    draggingWaypoints: Array<string>;
    isEditingStyle: boolean;
    viewport: {
      x: number;
      y: number;
      zoom: number;
    };
  };
}

// Read this to understand why we need computed as part of the store.
// https://github.com/pmndrs/zustand/issues/132#issuecomment-1120467721
export type Computed = {
  isDiagramEditingInProgress(): boolean;

  indexedSwf(): ReturnType<typeof computeIndexedSwf>;

  getDiagramData(requiresLayout?: boolean): ReturnType<typeof computeDiagramData>;

  isDropTargetNodeValidForSelection(): boolean;
};

//Handle layout data out of the model
export type Layout = {
  setNodePosition: (nodeId: string, newPosition: RF.XYPosition | undefined) => void;
  setEdgeWaypoints: (edgeId: string, newWaypoints: RF.XYPosition[] | undefined) => void;
  updateWaypointPosition: (edgeId: string, index: number, newPosition: RF.XYPosition | undefined) => void;
};

export type Dispatch = {
  swf: {
    reset: (model: State["swf"]["model"], keepSelection?: boolean) => void;
  };
  diagram: {
    setNodeStatus: (nodeId: string, status: Partial<SwfEditorDiagramNodeStatus>) => void;
    setEdgeStatus: (edgeId: string, status: Partial<SwfEditorDiagramEdgeStatus>) => void;
  };
};

export const defaultStaticState = (): Omit<State, "swf" | "dispatch" | "computed" | "layout"> => ({
  focus: {
    consumableId: undefined,
  },
  diagram: {
    autoLayout: {
      canAutoGenerate: true,
    },
    __unsafeSwfIndex: 0,
    edgeIdBeingUpdated: undefined,
    dropTargetNode: undefined,
    ongoingConnection: undefined,
    overlaysPanel: {
      isOpen: false,
    },
    overlays: {
      enableNodeHierarchyHighlight: false,
    },
    snapGrid: {
      isEnabled: true,
      x: 10,
      y: 10,
    },
    nodeIds: [],
    nodesPosition: [],
    edgeIds: [],
    edgeWaypoints: [],
    _selectedNodes: [],
    _selectedEdges: [],
    draggingNodes: [],
    draggingWaypoints: [],
    isEditingStyle: false,
    viewport: DEFAULT_VIEWPORT,
  },
});

export function createSwfEditorStore(model: Specification.IWorkflow, computedCache: ComputedStateCache<Computed>) {
  const { diagram, ...defaultState } = defaultStaticState();
  return create(
    immer<State>(() => ({
      swf: {
        model: model,
      },
      ...defaultState,
      diagram: {
        ...diagram,
        autoLayout: {
          canAutoGenerate: true,
        },
      },
      dispatch(s: State) {
        return {
          swf: {
            reset: (model, keepSelection) => {
              if (!keepSelection) {
                s.diagram._selectedNodes = [];
              }
              s.diagram.draggingNodes = [];
            },
          },
          diagram: {
            setNodeStatus: (nodeId, newStatus) => {
              //selected
              if (newStatus.selected !== undefined) {
                if (newStatus.selected) {
                  s.diagram._selectedNodes.push(nodeId);
                } else {
                  s.diagram._selectedNodes = s.diagram._selectedNodes.filter((s) => s !== nodeId);
                }
              }
              //dragging
              if (newStatus.dragging !== undefined) {
                if (newStatus.dragging) {
                  s.diagram.draggingNodes.push(nodeId);
                } else {
                  s.diagram.draggingNodes = s.diagram.draggingNodes.filter((s) => s !== nodeId);
                }
              }
            },
            setEdgeStatus: (edgeId, newStatus) => {
              //selected
              if (newStatus.selected !== undefined) {
                if (newStatus.selected) {
                  s.diagram._selectedEdges.push(edgeId);
                } else {
                  s.diagram._selectedEdges = s.diagram._selectedEdges.filter((s) => s !== edgeId);
                }
              }
              //dragging
              if (newStatus.draggingWaypoint !== undefined) {
                if (newStatus.draggingWaypoint) {
                  s.diagram.draggingWaypoints.push(edgeId);
                } else {
                  s.diagram.draggingWaypoints = s.diagram.draggingWaypoints.filter((s) => s !== edgeId);
                }
              }
            },
          },
        };
      },
      computed(s: State) {
        return {
          isDiagramEditingInProgress: () => {
            return computedCache.cached(
              "isDiagramEditingInProgress",
              (draggingNodesCount: number, draggingWaypointsCount: number, isisEditingStyle: boolean) =>
                draggingNodesCount > 0 || draggingWaypointsCount > 0 || isisEditingStyle,
              [s.diagram.draggingNodes.length, s.diagram.draggingWaypoints.length, s.diagram.isEditingStyle]
            );
          },

          indexedSwf: () => {
            return computedCache.cached("indexedSwf", computeIndexedSwf, [s.swf.model]);
          },

          isDropTargetNodeValidForSelection: () =>
            computedCache.cached("isDropTargetNodeValidForSelection", computeIsDropTargetNodeValidForSelection, [
              s.diagram.dropTargetNode,
              s.computed(s).getDiagramData(),
            ]),

          getDiagramData: (requiresLayout) =>
            computedCache.cached("getDiagramData", computeDiagramData, [
              s.diagram,
              s.swf.model,
              s.computed(s).indexedSwf(),
              s.diagram.nodeIds,
              s.diagram.nodesPosition,
              s.diagram.edgeIds,
              s.diagram.edgeWaypoints,
              requiresLayout,
            ]),
        };
      },
      layout(s: State) {
        return {
          setNodePosition: (nodeId, newPosition) => {
            if (!newPosition) {
              return;
            }
            const i = s.diagram.nodeIds.indexOf(nodeId);
            if (i !== -1) {
              s.diagram.nodesPosition[i] = newPosition;
            } else {
              s.diagram.nodeIds.push(nodeId);
              s.diagram.nodesPosition.push(newPosition);
            }
          },
          setEdgeWaypoints: (edgeId, newWaypoints) => {
            if (!newWaypoints) {
              return;
            }
            const i = s.diagram.edgeIds.indexOf(edgeId);
            if (i !== -1) {
              s.diagram.edgeWaypoints[i] = newWaypoints;
            } else {
              s.diagram.edgeIds.push(edgeId);
              s.diagram.edgeWaypoints.push(newWaypoints);
            }
          },
          updateWaypointPosition: (edgeId, index, newPosition) => {
            if (!newPosition) {
              return;
            }
            const i = s.diagram.edgeIds.indexOf(edgeId);
            if (i !== -1) {
              if (s.diagram.edgeWaypoints[i] && s.diagram.edgeWaypoints[i].length - 1 >= index) {
                s.diagram.edgeWaypoints[i][index] = newPosition;
              }
            }
          },
        };
      },
    }))
  );
}
