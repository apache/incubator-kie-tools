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

import * as RF from "reactflow";
import { SnapGrid } from "../snapgrid/SnapGrid";
import { DC__Point, DC__Shape } from "../maths/model";
import { GraphStructureAdjacencyList, GraphStructureEdge } from "../graph/graph";
import { ContainmentMode } from "../graph/graphStructure";

export type XyFlowReactKieDiagramNodeData<N extends string, NData extends XyFlowReactKieDiagramNodeData<N, NData>> = {
  shape: DC__Shape;
  /**
   * We don't use Reactflow's parenting mechanism because it is
   * too opinionated on how it deletes nodes/edges that are
   * inside/connected to nodes with parents
   * */
  parentXyFlowNode: RF.Node<NData, N> | undefined;
};

export type XyFlowReactKieDiagramEdgeData = {
  edgeInfo: GraphStructureEdge;
  ["di:waypoint"]: DC__Point[];
  ["@_id"]: string;
  shapeSource: DC__Shape;
  shapeTarget: DC__Shape;
};

export type XyFlowDiagramData<
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = {
  graphStructureEdges: GraphStructureEdge[];
  graphStructureAdjacencyList: GraphStructureAdjacencyList;
  nodes: RF.Node<NData, N>[];
  edges: RF.Edge<EData>[];
  edgesById: Map<string, RF.Edge<EData>>;
  nodesById: Map<string, RF.Node<NData, N>>;
  selectedNodeTypes: Set<N>;
  selectedNodesById: Map<string, RF.Node<NData, N>>;
  selectedEdgesById: Map<string, RF.Edge<EData>>;
};

export interface XyFlowReactKieDiagramNodeStatus {
  selected: boolean;
  dragging: boolean;
  resizing: boolean;
}
export interface XyFlowReactKieDiagramEdgeStatus {
  selected: boolean;
  draggingWaypoint: boolean;
}

export interface XyFlowDiagramState<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> {
  computed: (s: ThisType<this>) => {
    getDiagramData(): XyFlowDiagramData<N, NData, EData>;
    isDiagramEditingInProgress(): boolean;
  };
  dispatch: (s: ThisType<this>) => {
    setNodeStatus: (nodeId: string, status: Partial<XyFlowReactKieDiagramNodeStatus>) => any;
    setEdgeStatus: (edgeId: string, status: Partial<XyFlowReactKieDiagramEdgeStatus>) => any;
  };
  xyFlowReactKieDiagram: {
    snapGrid: SnapGrid;
    _selectedNodes: Array<string>;
    _selectedEdges: Array<string>;
    draggingNodes: Array<string>;
    resizingNodes: Array<string>;
    draggingWaypoints: Array<string>;
    edgeIdBeingUpdated: string | undefined;
    dropTarget: undefined | { node: RF.Node<NData, N>; containmentMode: ContainmentMode };
    ongoingConnection: RF.OnConnectStartParams | undefined;
    newNodeProjection: undefined | RF.Node<NData, N>;
  };
}
