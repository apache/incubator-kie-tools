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
import { buildHierarchy } from "../graph/graph";
import { getDeepChildNodes } from "../graph/childNodes";
import { ContainmentMap, ContainmentMode, getDefaultEdgeTypeBetween, GraphStructure } from "../graph/graphStructure";
import { checkIsValidConnection } from "../graph/isValidConnection";
import { getContainmentRelationship, getDiBoundsCenterPoint } from "../maths/DcMaths";
import { DC__Point } from "../maths/model";
import { snapShapeDimensions } from "../snapgrid/SnapGrid";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useXyFlowReactKieDiagramStore, useXyFlowReactKieDiagramStoreApi } from "../store/Store";
import { NodeSizes } from "../nodes/NodeSizes";
import { SelectionStatusLabel } from "./SelectionStatusLabel";
import { XyFlowDiagramState, XyFlowReactKieDiagramEdgeData, XyFlowReactKieDiagramNodeData } from "../store/State";
import { Draft } from "immer";
import { PositionalNodeHandleId } from "../nodes/PositionalNodeHandles";
import { WaypointActionsContextProvider, WaypointActionsContextType } from "../waypoints/WaypointActionsContext";
import { DEFAULT_BORDER_ALLOWANCE_IN_PX, snapToDropTargetsBorder } from "../snapgrid/BorderSnapping";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { KieDiagramI18nContext, kieDiagramI18nDefaults, kieDiagramI18nDictionaries } from "../i18n";

// nodes

export type OnNodeAdded<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: { state: Draft<S>; type: N; element: string; dropPoint: { x: number; y: number }; data: NData }) => {
  newNodeId: string;
};

export type OnConnectedNodeAdded<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  E extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: {
  state: Draft<S>;
  sourceNode: RF.Node<NData, N>;
  newNodeType: N;
  edgeType: E;
  dropPoint: { x: number; y: number };
}) => { newNodeId: string };

export type OnNodeUnparented<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: {
  state: Draft<S>;
  exParentNode: RF.Node<NData, N>;
  activeNode: RF.Node<NData, N>;
  selectedNodes: RF.Node<NData, N>[];
}) => void;

export type OnNodeParented<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: {
  state: Draft<S>;
  parentNode: RF.Node<NData, N>;
  activeNode: RF.Node<NData, N>;
  containmentMode: ContainmentMode;
  selectedNodes: RF.Node<NData, N>[];
}) => void;

export type OnNodeRepositioned<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: {
  state: Draft<S>;
  /** Set of waypoint indexes by Edge index */
  controlWaypointsByEdge: Map<number, Set<number>>;
  node: RF.Node<NData, N>;
  newPosition: RF.XYPosition;
  childNodeIds: string[];
}) => void;

export type OnNodeDeleted<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: { state: Draft<S>; node: RF.Node<NData, N> }) => void;

export type OnNodeResized<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: { state: Draft<S>; node: RF.Node<NData, N>; newDimensions: { width: number; height: number } }) => void;

// edges

export type OnEdgeAdded<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  E extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: {
  state: Draft<S>;
  sourceNode: RF.Node<NData, N>;
  targetNode: RF.Node<NData, N>;
  edgeType: E;
  sourceHandle: PositionalNodeHandleId;
  targetHandle: PositionalNodeHandleId;
}) => void;

export type OnEdgeUpdated<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  E extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: {
  state: Draft<S>;
  sourceNode: RF.Node<NData, N>;
  targetNode: RF.Node<NData, N>;
  edge: RF.Edge<EData>;
  targetHandle: PositionalNodeHandleId;
  sourceHandle: PositionalNodeHandleId;
  firstWaypoint: DC__Point;
  lastWaypoint: DC__Point;
}) => { id: string };

export type OnEdgeDeleted<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  E extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (args: { state: Draft<S>; edge: RF.Edge<EData> }) => void;

// waypoints

export type OnWaypointAdded = (args: { beforeIndex: number; waypoint: DC__Point; edgeIndex: number }) => void;
export type OnWaypointDeleted = (args: { edgeIndex: number; waypointIndex: number }) => void;
export type OnWaypointRepositioned = (args: { edgeIndex: number; waypointIndex: number; waypoint: DC__Point }) => void;

// misc

export type OnEscPressed = () => void;

// model
export type OnResetToBeforeEditingBegan<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  E extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = (stateDraft: Draft<S>) => void;

//

export type Props<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  E extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = {
  // model
  model: unknown;
  modelBeforeEditingRef: React.MutableRefObject<unknown>;
  onResetToBeforeEditingBegan: OnResetToBeforeEditingBegan<S, N, E, NData, EData>;
  // components
  connectionLineComponent: RF.ConnectionLineComponent;
  nodeComponents: RF.NodeTypes;
  edgeComponents: RF.EdgeTypes;
  // infra
  diagramRef: React.RefObject<DiagramRef<N, NData, EData>>;
  children: React.ReactElement[];
  container: React.RefObject<HTMLElement>;
  // domain
  newNodeMimeType: string;
  containmentMap: ContainmentMap<N>;
  nodeTypes: Record<string, string>;
  minNodeSizes: NodeSizes<N>;
  graphStructure: GraphStructure<N, E>;
  allowCycles: boolean;
  // actions
  onNodeRepositioned: OnNodeRepositioned<S, N, NData, EData>;
  onNodeDeleted: OnNodeDeleted<S, N, NData, EData>;
  onNodeAdded: OnNodeAdded<S, N, NData, EData>;
  onNodeUnparented: OnNodeUnparented<S, N, NData, EData>;
  onNodeParented: OnNodeParented<S, N, NData, EData>;
  onConnectedNodeAdded: OnConnectedNodeAdded<S, N, E, NData, EData>;
  onNodeResized: OnNodeResized<S, N, NData, EData>;
  onEdgeAdded: OnEdgeAdded<S, N, E, NData, EData>;
  onEdgeUpdated: OnEdgeUpdated<S, N, E, NData, EData>;
  onEdgeDeleted: OnEdgeDeleted<S, N, E, NData, EData>;
  onEscPressed: OnEscPressed;
  onWaypointAdded: OnWaypointAdded;
  onWaypointRepositioned: OnWaypointRepositioned;
  onWaypointDeleted: OnWaypointDeleted;
};

//

const isFirefox = typeof (window as any).InstallTrigger !== "undefined"; // See https://stackoverflow.com/questions/9847580/how-to-detect-safari-chrome-ie-firefox-and-opera-browsers

const PAN_ON_DRAG = [1, 2];

const FIT_VIEW_OPTIONS: RF.FitViewOptions = { maxZoom: 1, minZoom: 0.1, duration: 400 };

export const DEFAULT_VIEWPORT = { x: 100, y: 100, zoom: 1 };

const DELETE_NODE_KEY_CODES = ["Backspace", "Delete"];

export type DiagramRef<
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
> = {
  getReactFlowInstance: () => RF.ReactFlowInstance<NData, EData> | undefined;
};

export function XyFlowReactKieDiagram<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  E extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
>({
  // model
  model,
  modelBeforeEditingRef,
  onResetToBeforeEditingBegan,
  // infra
  diagramRef,
  children,
  container,
  // components
  connectionLineComponent,
  nodeComponents,
  edgeComponents,
  // domain
  newNodeMimeType,
  containmentMap,
  nodeTypes,
  minNodeSizes,
  graphStructure,
  allowCycles,
  // actions
  onNodeAdded,
  onConnectedNodeAdded,
  onNodeUnparented,
  onNodeParented,
  onNodeRepositioned,
  onNodeResized,
  onNodeDeleted,
  onEdgeAdded,
  onEdgeUpdated,
  onEdgeDeleted,
  onWaypointAdded,
  onWaypointDeleted,
  onWaypointRepositioned,
  onEscPressed,
}: Props<S, N, E, NData, EData>) {
  // Contexts
  const xyFlowReactKieDiagramStoreApi = useXyFlowReactKieDiagramStoreApi<S, N, NData, EData>();
  const snapGrid = useXyFlowReactKieDiagramStore((s) => s.xyFlowReactKieDiagram.snapGrid);

  // State
  const [reactFlowInstance, setReactFlowInstance] = useState<RF.ReactFlowInstance<NData, EData> | undefined>(undefined);

  // Refs
  React.useImperativeHandle(diagramRef, () => ({ getReactFlowInstance: () => reactFlowInstance }), [reactFlowInstance]);

  const nodeIdBeingDraggedRef = useRef<string | null>(null);

  // Memos

  const xyFlowSnapGrid = useMemo<[number, number]>(
    () => (snapGrid.isEnabled ? [snapGrid.x, snapGrid.y] : [1, 1]),
    [snapGrid.isEnabled, snapGrid.x, snapGrid.y]
  );

  // Callbacks

  const onConnect = useCallback<RF.OnConnect>(
    ({ source, target, sourceHandle, targetHandle }) => {
      console.debug("XYFLOW KIE DIAGRAM: `onConnect`: ", { source, target, sourceHandle, targetHandle });
      const state = xyFlowReactKieDiagramStoreApi.getState();
      const sourceNode = state.computed(state).getDiagramData().nodesById.get(source!);
      const targetNode = state.computed(state).getDiagramData().nodesById.get(target!);
      if (!sourceNode || !targetNode) {
        throw new Error("Cannot create connection without target and source nodes!");
      }

      const sourceBounds = sourceNode.data.shape["dc:Bounds"];
      const targetBounds = targetNode.data.shape["dc:Bounds"];
      if (!sourceBounds || !targetBounds) {
        throw new Error("Cannot create connection without target bounds!");
      }

      console.log("XYFLOW KIE DIAGRAM: Edge added");
      xyFlowReactKieDiagramStoreApi.setState((state) => {
        onEdgeAdded({
          state,
          sourceNode,
          targetNode,
          edgeType: sourceHandle as E,
          targetHandle: targetHandle as PositionalNodeHandleId,
          sourceHandle: sourceHandle as PositionalNodeHandleId,
        });
      });
    },
    [onEdgeAdded, xyFlowReactKieDiagramStoreApi]
  );

  const ongoingConnection = useXyFlowReactKieDiagramStore((s) => s.xyFlowReactKieDiagram.ongoingConnection);
  useEffect(() => {
    const edgeUpdaterSource = document.querySelectorAll(
      ".react-flow__edgeupdater-source, .react-flow__edgeupdater-target"
    );
    if (ongoingConnection) {
      edgeUpdaterSource.forEach((e) => e.classList.add("hidden"));
    } else {
      edgeUpdaterSource.forEach((e) => e.classList.remove("hidden"));
    }
  }, [ongoingConnection]);

  const onConnectStart = useCallback<RF.OnConnectStart>(
    (e, newConnection) => {
      console.debug("XYFLOW KIE DIAGRAM: `onConnectStart`");
      xyFlowReactKieDiagramStoreApi.setState((state) => {
        state.xyFlowReactKieDiagram.ongoingConnection = newConnection;
      });
    },
    [xyFlowReactKieDiagramStoreApi]
  );

  const onConnectEnd = useCallback<RF.OnConnectEnd>(
    (e) => {
      if (!(e instanceof MouseEvent)) {
        console.debug("XYFLOW KIE DIAGRAM: Ignoring `onConnectEnd`. Not MouseEvent.");
        return;
      }

      console.debug("XYFLOW KIE DIAGRAM: `onConnectEnd`");

      xyFlowReactKieDiagramStoreApi.setState((state) => {
        const targetIsPane = (e.target as Element | null)?.classList?.contains("react-flow__pane");
        if (
          !targetIsPane ||
          !container.current ||
          !state.xyFlowReactKieDiagram.ongoingConnection ||
          !reactFlowInstance
        ) {
          return;
        }

        const dropPoint = reactFlowInstance.screenToFlowPosition({
          x: e.clientX,
          y: e.clientY,
        });

        // only try to create node if source handle is compatible
        if (!Object.values(nodeTypes).find((n) => n === state.xyFlowReactKieDiagram.ongoingConnection!.handleId)) {
          return;
        }

        if (!state.xyFlowReactKieDiagram.ongoingConnection.nodeId) {
          return;
        }

        const sourceNode = state
          .computed(state)
          .getDiagramData()
          .nodesById.get(state.xyFlowReactKieDiagram.ongoingConnection.nodeId);
        if (!sourceNode) {
          return;
        }

        const sourceNodeBounds = state.computed(state).getDiagramData().nodesById.get(sourceNode.id)?.data.shape[
          "dc:Bounds"
        ];
        if (!sourceNodeBounds) {
          return;
        }

        const newNodeType = state.xyFlowReactKieDiagram.ongoingConnection.handleId as N;
        const sourceNodeType = sourceNode.type as N;

        const edgeType = getDefaultEdgeTypeBetween(graphStructure, sourceNodeType as N, newNodeType);
        if (!edgeType) {
          throw new Error(`XYFLOW KIE DIAGRAM: Invalid structure: ${sourceNodeType} --(any)--> ${newNodeType}`);
        }

        console.log("XYFLOW KIE DIAGRAM: Node added (connected)");
        const { newNodeId } = onConnectedNodeAdded({
          state,
          sourceNode,
          newNodeType,
          edgeType,
          dropPoint,
        });

        state.xyFlowReactKieDiagram._selectedNodes = [newNodeId];
      });

      // Indepdent of what happens in the state mutation above, we always need to reset the `ongoingConnection` at the end here.
      xyFlowReactKieDiagramStoreApi.setState((state) => {
        state.xyFlowReactKieDiagram.ongoingConnection = undefined;
      });
    },
    [xyFlowReactKieDiagramStoreApi, container, reactFlowInstance, nodeTypes, graphStructure, onConnectedNodeAdded]
  );

  const isValidConnection = useCallback<RF.IsValidConnection>(
    (edgeOrConnection) => {
      const state = xyFlowReactKieDiagramStoreApi.getState();
      const edgeId = state.xyFlowReactKieDiagram.edgeIdBeingUpdated;
      const edgeType = edgeId ? (reactFlowInstance?.getEdge(edgeId)?.type as E) : undefined;

      const ongoingConnectionHierarchy = buildHierarchy({
        nodeId: state.xyFlowReactKieDiagram.ongoingConnection?.nodeId,
        edges: state.computed(state).getDiagramData().graphStructureEdges,
      });

      return (
        // Reflexive edges are not allowed
        edgeOrConnection.source !== edgeOrConnection.target &&
          // Matches graph structure.
          checkIsValidConnection(
            graphStructure,
            state.computed(state).getDiagramData().nodesById,
            edgeOrConnection,
            edgeType
          ) &&
          // Does not form cycles.
          allowCycles
          ? true
          : !!edgeOrConnection.target &&
              !ongoingConnectionHierarchy.dependencies.has(edgeOrConnection.target) &&
              !!edgeOrConnection.source &&
              !ongoingConnectionHierarchy.dependents.has(edgeOrConnection.source)
      );
    },
    [xyFlowReactKieDiagramStoreApi, reactFlowInstance, graphStructure, allowCycles]
  );

  const onNodesChange = useCallback<RF.OnNodesChange>(
    (changes) => {
      if (!reactFlowInstance) {
        return;
      }

      const controlWaypointsByEdge = new Map<number, Set<number>>();

      xyFlowReactKieDiagramStoreApi.setState((state) => {
        for (const change of changes) {
          switch (change.type) {
            case "add":
              console.debug(`XYFLOW KIE DIAGRAM: 'onNodesChange' --> add '${change.item.id}'`);
              state.dispatch(state).setNodeStatus(change.item.id, { selected: true });
              break;
            case "dimensions":
              if (state.xyFlowReactKieDiagram.newNodeProjection?.id === change.id) {
                break;
              }

              console.debug(`XYFLOW KIE DIAGRAM: 'onNodesChange' --> dimensions '${change.id}'`);
              state.dispatch(state).setNodeStatus(change.id, { resizing: change.resizing });
              if (change.dimensions) {
                const node = state.computed(state).getDiagramData().nodesById.get(change.id)!;
                // We only need to resize the node if its snapped dimensions change, as snapping is non-destructive.
                const snappedShape = snapShapeDimensions(
                  state.xyFlowReactKieDiagram.snapGrid,
                  node.data.shape,
                  minNodeSizes[node.type as N]({
                    snapGrid: state.xyFlowReactKieDiagram.snapGrid,
                  })
                );
                if (
                  snappedShape.width !== change.dimensions.width ||
                  snappedShape.height !== change.dimensions.height
                ) {
                  console.log("XYFLOW KIE DIAGRAM: Node resized");
                  onNodeResized({
                    state,
                    node,
                    newDimensions: { ...change.dimensions },
                  });
                }
              }
              break;
            case "position":
              console.debug(`XYFLOW KIE DIAGRAM: 'onNodesChange' --> position '${change.id}'`);
              state.dispatch(state).setNodeStatus(change.id, { dragging: change.dragging });

              if (change.positionAbsolute) {
                const allNodes = state.computed(state).getDiagramData().nodes;

                if (nodeIdBeingDraggedRef.current === change.id) {
                  const nodeBeingDragged =
                    state.computed(state).getDiagramData().nodesById.get(change.id) ??
                    state.xyFlowReactKieDiagram.newNodeProjection!;

                  let foundContainer = false;
                  for (const potentialContainer of reactFlowInstance?.getNodes().reverse() ??
                    [] /* Respect the nodes z-index */) {
                    if (potentialContainer.id === nodeBeingDragged.id) {
                      // ignore `nodeBeingDragged`
                      continue;
                    }

                    const containmentRelationship = getContainmentRelationship({
                      snapGrid,
                      container: potentialContainer.data.shape["dc:Bounds"]!,
                      containerMinSizes: minNodeSizes[potentialContainer.type as N],
                      bounds: {
                        ...nodeBeingDragged.data.shape["dc:Bounds"],
                        "@_x": change.positionAbsolute.x,
                        "@_y": change.positionAbsolute.y,
                      },
                      boundsMinSizes: minNodeSizes[nodeBeingDragged.type as N],
                      borderAllowanceInPx: DEFAULT_BORDER_ALLOWANCE_IN_PX,
                    });

                    if (!(containmentRelationship.isAtBorder || containmentRelationship.isCompletelyInside)) {
                      // `nodeBeingDragged` is not inside `potentialContainer`
                      continue;
                    }

                    let containmentMode: ContainmentMode;
                    if (containmentRelationship.isAtBorder) {
                      containmentMode = ContainmentMode.BORDER;
                    } else if (containmentRelationship.isCompletelyInside) {
                      containmentMode = ContainmentMode.INSIDE;
                    } else {
                      throw new Error(
                        "Can't determine ContainmentMode for a node that is not visually inside the other."
                      );
                    }

                    const diagramData = state.computed(state).getDiagramData();
                    if (diagramData.selectedNodesById.size > 1 && containmentMode === ContainmentMode.BORDER) {
                      // Containment at border should only be done 1 by 1. Can't drag a selection to the border of another node.
                      continue;
                    }

                    const allowedContainmentModes =
                      containmentMap.get(potentialContainer.type as N) ?? new Map<ContainmentMode, Set<N>>();

                    const typesOfNodesBeingDragged = state.xyFlowReactKieDiagram.newNodeProjection?.type
                      ? [state.xyFlowReactKieDiagram.newNodeProjection.type as N]
                      : [...diagramData.selectedNodeTypes];

                    const allSelectedNodesRespectContainmentMode = typesOfNodesBeingDragged.every((nodeType) =>
                      allowedContainmentModes.get(containmentMode)?.has(nodeType)
                    );

                    const newDropTarget = {
                      node: potentialContainer as Draft<RF.Node<NData, N>>,
                      containmentMode: allSelectedNodesRespectContainmentMode
                        ? containmentMode
                        : containmentMode === ContainmentMode.INSIDE &&
                            allowedContainmentModes.has(ContainmentMode.INSIDE)
                          ? ContainmentMode.INVALID_INSIDE
                          : containmentMode === ContainmentMode.BORDER &&
                              allowedContainmentModes.has(ContainmentMode.BORDER)
                            ? ContainmentMode.INVALID_BORDER
                            : containmentMode === ContainmentMode.INSIDE
                              ? ContainmentMode.INVALID_NON_INSIDE_CONTAINER
                              : containmentMode === ContainmentMode.BORDER
                                ? ContainmentMode.INVALID_IGNORE
                                : ContainmentMode.INVALID_IGNORE,
                    };

                    state.xyFlowReactKieDiagram.dropTarget ??= newDropTarget;
                    state.xyFlowReactKieDiagram.dropTarget.node = newDropTarget.node;
                    state.xyFlowReactKieDiagram.dropTarget.containmentMode = newDropTarget.containmentMode;

                    // If one one those states is reached, we stop searching for a valid container.
                    // As there's no reason to keep looking if the node is already visually completely inside another node.
                    // That's not true for borders, though, as we don't want to let an invalid "border" containment to
                    // stop us from finding an "inside" container, for example.
                    if (
                      newDropTarget.containmentMode === ContainmentMode.BORDER ||
                      newDropTarget.containmentMode === ContainmentMode.INSIDE ||
                      newDropTarget.containmentMode === ContainmentMode.INVALID_INSIDE ||
                      newDropTarget.containmentMode === ContainmentMode.INVALID_NON_INSIDE_CONTAINER
                    ) {
                      foundContainer = true;
                      break;
                    }
                  }

                  // cleanup from last dragging event if none was found this time.
                  if (!foundContainer) {
                    state.xyFlowReactKieDiagram.dropTarget = undefined;
                  }
                }

                const node = state.computed(state).getDiagramData().nodesById.get(change.id)!;

                const dropTarget = state.xyFlowReactKieDiagram.dropTarget as S["xyFlowReactKieDiagram"]["dropTarget"];

                const newPosition =
                  dropTarget?.containmentMode === ContainmentMode.BORDER
                    ? snapToDropTargetsBorder(
                        dropTarget,
                        {
                          ...(node ?? state.xyFlowReactKieDiagram.newNodeProjection).data.shape["dc:Bounds"],
                          "@_x": change.positionAbsolute.x,
                          "@_y": change.positionAbsolute.y,
                        },
                        (node ?? state.xyFlowReactKieDiagram.newNodeProjection).type!,
                        state.xyFlowReactKieDiagram.snapGrid,
                        minNodeSizes,
                        DEFAULT_BORDER_ALLOWANCE_IN_PX
                      )
                    : change.positionAbsolute;

                if (!node && state.xyFlowReactKieDiagram.newNodeProjection) {
                  state.xyFlowReactKieDiagram.newNodeProjection.position = newPosition;
                  state.xyFlowReactKieDiagram.newNodeProjection.data.shape["dc:Bounds"]["@_x"] = newPosition.x;
                  state.xyFlowReactKieDiagram.newNodeProjection.data.shape["dc:Bounds"]["@_y"] = newPosition.y;
                } else {
                  if (isAnyParentSelected(node?.data.parentXyFlowNode)) {
                    // Do nothing.
                    // Nodes that have a virtual parent will be automatically dragged with them, so there's no need to reposition them here.
                  } else {
                    onNodeRepositioned({
                      state,
                      controlWaypointsByEdge,
                      node,
                      newPosition,
                      childNodeIds: getDeepChildNodes([change.id], allNodes).get(change.id) ?? [],
                    });
                  }
                }
              }
              break;
            case "remove":
              console.debug(`XYFLOW KIE DIAGRAM: 'onNodesChange' --> remove '${change.id}'`);
              const node = state.computed(state).getDiagramData().nodesById.get(change.id)!;
              console.log("XYFLOW KIE DIAGRAM: Node deleted");
              onNodeDeleted({ state, node });

              state.dispatch(state).setNodeStatus(node.id, { selected: false, dragging: false, resizing: false });
              break;
            case "reset":
              state.dispatch(state).setNodeStatus(change.item.id, {
                selected: false,
                dragging: false,
                resizing: false,
              });
              break;
            case "select":
              state.dispatch(state).setNodeStatus(change.id, { selected: change.selected });
              break;
          }
        }
      });
    },
    [
      containmentMap,
      minNodeSizes,
      onNodeDeleted,
      onNodeRepositioned,
      onNodeResized,
      reactFlowInstance,
      snapGrid,
      xyFlowReactKieDiagramStoreApi,
    ]
  );

  const onNodeDrag = useCallback<RF.NodeDragHandler>((e, nodeBeingDragged: RF.Node<NData, N>) => {
    nodeIdBeingDraggedRef.current = nodeBeingDragged.id;
  }, []);

  const onNodeDragStart = useCallback<RF.NodeDragHandler>(
    (e, node: RF.Node<NData, N>, nodes) => {
      modelBeforeEditingRef.current = model;
      onNodeDrag(e, node, nodes);
    },
    [modelBeforeEditingRef, onNodeDrag, model]
  );

  const onNodeDragStop = useCallback<RF.NodeDragHandler>(
    (e, node: RF.Node<NData, N>) => {
      try {
        xyFlowReactKieDiagramStoreApi.setState((state) => {
          console.debug("XYFLOW KIE DIAGRAM: `onNodeDragStop`");
          const nodeBeingDragged = state.computed(state).getDiagramData().nodesById.get(nodeIdBeingDraggedRef.current!);
          nodeIdBeingDraggedRef.current = null;
          if (!nodeBeingDragged) {
            return;
          }

          // Validate
          const dropTarget = state.xyFlowReactKieDiagram.dropTarget;
          state.xyFlowReactKieDiagram.dropTarget = undefined;
          if (
            containmentMap.has(dropTarget?.node.type as N) &&
            (dropTarget?.containmentMode === ContainmentMode.INVALID_INSIDE ||
              /* dropTarget?.containmentMode === ContainmentMode.INVALID_IGNORE // Commented on purpose: We don't want to disallow positioning nodes on top of another. Especially without visual feedback. */
              /* dropTarget?.containmentMode === ContainmentMode.INVALID_BORDER // Commented on purpose: We don't want to disallow positioning nodes slightly on top of another. Especially without visual feedback. */
              dropTarget?.containmentMode === ContainmentMode.INVALID_NON_INSIDE_CONTAINER)
          ) {
            console.debug(
              `XYFLOW KIE DIAGRAM: Invalid containment: '${[
                ...state.computed(state).getDiagramData().selectedNodeTypes,
              ].join("', '")}' inside '${dropTarget?.node.type}'. Ignoring nodes dropped.`
            );
            state.xyFlowReactKieDiagram.newNodeProjection = undefined;
            state.xyFlowReactKieDiagram._selectedNodes = [];
            onResetToBeforeEditingBegan(state);
            return;
          }

          const selectedNodes = [...state.computed(state).getDiagramData().selectedNodesById.values()];

          if (!node.dragging) {
            return;
          }

          console.log("XYFLOW KIE DIAGRAM: Node parented");
          // Un-parent
          if (nodeBeingDragged.data.parentXyFlowNode) {
            const p = state.computed(state).getDiagramData().nodesById.get(nodeBeingDragged.data.parentXyFlowNode.id);
            if (p?.type && containmentMap.get(p.type)) {
              onNodeUnparented({ state, exParentNode: p, activeNode: nodeBeingDragged, selectedNodes });
            } else {
              console.debug(
                `XYFLOW KIE DIAGRAM: Ignoring '${nodeBeingDragged.type}' with parent '${dropTarget?.node.type}' dropping somewhere..`
              );
            }
          }

          // // Parent
          if (dropTarget?.node.type && containmentMap.get(dropTarget.node.type as N)) {
            onNodeParented({
              state,
              parentNode: dropTarget.node as RF.Node<NData, N>,
              activeNode: nodeBeingDragged,
              selectedNodes,
              containmentMode: dropTarget.containmentMode,
            });
          } else {
            console.debug(
              `XYFLOW KIE DIAGRAM: Ignoring '${nodeBeingDragged.type}' dropped on top of '${dropTarget?.node.type}'`
            );
          }
        });
      } catch (e) {
        console.error(e);
        xyFlowReactKieDiagramStoreApi.setState((state) => {
          onResetToBeforeEditingBegan(state);
        });
      }
    },
    [containmentMap, onNodeParented, onNodeUnparented, xyFlowReactKieDiagramStoreApi, onResetToBeforeEditingBegan]
  );

  const onEdgesChange = useCallback<RF.OnEdgesChange>(
    (changes) => {
      xyFlowReactKieDiagramStoreApi.setState((state) => {
        for (const change of changes) {
          switch (change.type) {
            case "select":
              console.debug(`XYFLOW KIE DIAGRAM: 'onEdgesChange' --> select '${change.id}'`);
              state.dispatch(state).setEdgeStatus(change.id, { selected: change.selected });
              break;
            case "remove":
              console.debug(`XYFLOW KIE DIAGRAM: 'onEdgesChange' --> remove '${change.id}'`);
              const edge = state.computed(state).getDiagramData().edgesById.get(change.id);
              if (edge?.data) {
                console.log("XYFLOW KIE DIAGRAM: Edge deleted");
                onEdgeDeleted({ state, edge });

                state.dispatch(state).setEdgeStatus(change.id, {
                  selected: false,
                  draggingWaypoint: false,
                });
              }
              break;
            case "add":
            case "reset":
              console.debug(`XYFLOW KIE DIAGRAM: 'onEdgesChange' --> add/reset '${change.item.id}'. Ignoring`);
          }
        }
      });
    },
    [onEdgeDeleted, xyFlowReactKieDiagramStoreApi]
  );

  const onEdgeUpdate = useCallback<RF.OnEdgeUpdateFunc<EData>>(
    (oldEdge, newConnection) => {
      console.debug("XYFLOW KIE DIAGRAM: `onEdgeUpdate`", oldEdge, newConnection);

      xyFlowReactKieDiagramStoreApi.setState((state) => {
        const sourceNode = state.computed(state).getDiagramData().nodesById.get(newConnection.source!);
        const targetNode = state.computed(state).getDiagramData().nodesById.get(newConnection.target!);
        if (!sourceNode || !targetNode) {
          throw new Error("Cannot create connection without target and source nodes!");
        }

        const sourceBounds = sourceNode.data.shape["dc:Bounds"];
        const targetBounds = targetNode.data.shape["dc:Bounds"];
        if (!sourceBounds || !targetBounds) {
          throw new Error("Cannot create connection without target bounds!");
        }

        const lastWaypoint = oldEdge.data?.edgeInfo
          ? oldEdge.data!["di:waypoint"]![oldEdge.data!["di:waypoint"]!.length - 1]!
          : getDiBoundsCenterPoint(targetBounds);
        const firstWaypoint = oldEdge.data?.edgeInfo
          ? oldEdge.data!["di:waypoint"]![0]!
          : getDiBoundsCenterPoint(sourceBounds);

        console.log("XYFLOW KIE DIAGRAM: Edge updated");
        const { id } = onEdgeUpdated({
          state,
          sourceNode,
          targetNode,
          sourceHandle: newConnection.sourceHandle as PositionalNodeHandleId,
          targetHandle: newConnection.targetHandle as PositionalNodeHandleId,
          lastWaypoint,
          firstWaypoint,
          edge: oldEdge,
        });

        state.xyFlowReactKieDiagram._selectedEdges = [id]; // Keep the updated edge selected

        // Finish edge update atomically.
        state.xyFlowReactKieDiagram.ongoingConnection = undefined;
        state.xyFlowReactKieDiagram.edgeIdBeingUpdated = undefined;
      });
    },
    [onEdgeUpdated, xyFlowReactKieDiagramStoreApi]
  );

  const onEdgeUpdateStart = useCallback(
    (e: React.MouseEvent | React.TouchEvent, edge: RF.Edge, handleType: RF.HandleType) => {
      console.debug("XYFLOW KIE DIAGRAM: `onEdgeUpdateStart`");
      xyFlowReactKieDiagramStoreApi.setState((state) => {
        state.xyFlowReactKieDiagram.edgeIdBeingUpdated = edge.id;
      });
    },
    [xyFlowReactKieDiagramStoreApi]
  );

  const onEdgeUpdateEnd = useCallback(
    (e: MouseEvent | TouchEvent, edge: RF.Edge, handleType: RF.HandleType) => {
      console.debug("XYFLOW KIE DIAGRAM: `onEdgeUpdateEnd`");

      // Needed for when the edge update operation doesn't change anything.
      xyFlowReactKieDiagramStoreApi.setState((state) => {
        state.xyFlowReactKieDiagram.ongoingConnection = undefined;
        state.xyFlowReactKieDiagram.edgeIdBeingUpdated = undefined;
      });
    },
    [xyFlowReactKieDiagramStoreApi]
  );

  const onDrop = useCallback(
    async (e: React.DragEvent) => {
      console.log("XYFLOW KIE DIAGRAM: Node added (standalone)");
      e.preventDefault();
      if (!container.current || !reactFlowInstance) {
        return;
      }

      if (e.dataTransfer.getData(newNodeMimeType)) {
        const { nodeType, element } = JSON.parse(e.dataTransfer.getData(newNodeMimeType)) as {
          nodeType: N;
          element: string;
        };

        e.stopPropagation();

        xyFlowReactKieDiagramStoreApi.setState((state) => {
          const { newNodeId } = onNodeAdded({
            state,
            dropPoint: state.xyFlowReactKieDiagram.newNodeProjection!.position,
            type: nodeType,
            element,
            data: state.xyFlowReactKieDiagram.newNodeProjection!.data as NData,
          });
          state.xyFlowReactKieDiagram._selectedNodes = [newNodeId];
          nodeIdBeingDraggedRef.current = newNodeId;
        });

        onNodeDragStop(undefined as any, { dragging: true } as any, []);

        xyFlowReactKieDiagramStoreApi.setState((state) => {
          state.xyFlowReactKieDiagram.newNodeProjection = undefined;
        });
      }
    },
    [container, newNodeMimeType, onNodeAdded, onNodeDragStop, reactFlowInstance, xyFlowReactKieDiagramStoreApi]
  );

  const onDragOver = useCallback(
    (e: React.DragEvent) => {
      if (!e.dataTransfer.types.find((t) => t === newNodeMimeType)) {
        return;
      }
      e.preventDefault();
      e.dataTransfer.dropEffect = "move";

      if (reactFlowInstance) {
        const position = reactFlowInstance.screenToFlowPosition({ x: e.clientX, y: e.clientY });

        if (
          position.x === xyFlowReactKieDiagramStoreApi.getState().xyFlowReactKieDiagram.newNodeProjection?.position.x &&
          position.y === xyFlowReactKieDiagramStoreApi.getState().xyFlowReactKieDiagram.newNodeProjection?.position.y
        ) {
          // `onDragOver` is fires continuously as long as the dragged element is inside the target
          // to prevent unnecessary state updates, we only do it when the element's position is different.
          return;
        }

        onNodeDragStart(
          undefined as any,
          xyFlowReactKieDiagramStoreApi.getState().xyFlowReactKieDiagram.newNodeProjection!,
          []
        );

        xyFlowReactKieDiagramStoreApi.setState((s) => {
          if (s.xyFlowReactKieDiagram.newNodeProjection) {
            s.xyFlowReactKieDiagram.newNodeProjection.hidden = false;
          }
        });

        onNodesChange([
          {
            type: "position",
            positionAbsolute: {
              x: position.x,
              y: position.y,
            },
            id: xyFlowReactKieDiagramStoreApi.getState().xyFlowReactKieDiagram.newNodeProjection!.id,
          },
        ]);
      }
    },
    [newNodeMimeType, onNodeDragStart, onNodesChange, reactFlowInstance, xyFlowReactKieDiagramStoreApi]
  );

  // Override Reactflow's behavior by intercepting the keydown event using its `capture` variant.
  const handleRfKeyDownCapture = useCallback(
    (e: React.KeyboardEvent) => {
      if (e.key === "Escape") {
        const s = xyFlowReactKieDiagramStoreApi.getState();
        if (s.computed(s).isDiagramEditingInProgress() && modelBeforeEditingRef.current) {
          console.debug(
            "XYFLOW KIE DIAGRAM: Intercepting Escape pressed and preventing propagation. Reverting `model` to what it was before editing began."
          );

          e.stopPropagation();
          e.preventDefault();
          xyFlowReactKieDiagramStoreApi.setState((state) => {
            onResetToBeforeEditingBegan(state);
          });
        } else if (!s.xyFlowReactKieDiagram.ongoingConnection) {
          xyFlowReactKieDiagramStoreApi.setState((state) => {
            if (
              state.computed(state).getDiagramData().selectedNodesById.size > 0 ||
              state.computed(state).getDiagramData().selectedEdgesById.size > 0
            ) {
              console.debug("XYFLOW KIE DIAGRAM: Esc pressed. Desselecting everything.");
              state.xyFlowReactKieDiagram._selectedNodes = [];
              state.xyFlowReactKieDiagram._selectedEdges = [];
              e.preventDefault();
            } else if (
              state.computed(state).getDiagramData().selectedNodesById.size <= 0 &&
              state.computed(state).getDiagramData().selectedEdgesById.size <= 0
            ) {
              console.debug("XYFLOW KIE DIAGRAM: Esc pressed. Closing all open panels.");
              console.log("XYFLOW KIE DIAGRAM: Esc pressed");
              e.preventDefault();
              onEscPressed();
            } else {
              // Let the
            }
          });
        } else {
          // Let the KeyboardShortcuts handle it.
        }
      }
    },
    [xyFlowReactKieDiagramStoreApi, modelBeforeEditingRef, onResetToBeforeEditingBegan, onEscPressed]
  );

  const nodes = useXyFlowReactKieDiagramStore((s) => s.computed(s).getDiagramData().nodes);
  const edges = useXyFlowReactKieDiagramStore((s) => s.computed(s).getDiagramData().edges);

  const waypointActionsContextValue = useMemo<WaypointActionsContextType>(
    () => ({
      onWaypointAdded,
      onWaypointDeleted,
      onWaypointRepositioned,
    }),
    [onWaypointAdded, onWaypointDeleted, onWaypointRepositioned]
  );

  return (
    <>
      <I18nDictionariesProvider
        defaults={kieDiagramI18nDefaults}
        dictionaries={kieDiagramI18nDictionaries}
        initialLocale={navigator.language}
        ctx={KieDiagramI18nContext}
      >
        <WaypointActionsContextProvider value={waypointActionsContextValue}>
          <RF.ReactFlow
            connectionMode={RF.ConnectionMode.Loose} // Allow target handles to be used as source. This is very important for allowing the positional handles to be updated for the base of an edge.
            onKeyDownCapture={handleRfKeyDownCapture} // Override Reactflow's keyboard listeners.
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onEdgeUpdateStart={onEdgeUpdateStart}
            onEdgeUpdateEnd={onEdgeUpdateEnd}
            onEdgeUpdate={onEdgeUpdate}
            onlyRenderVisibleElements={true}
            zoomOnDoubleClick={false}
            elementsSelectable={true}
            panOnScroll={true}
            zoomOnScroll={false}
            preventScrolling={true}
            selectionOnDrag={true}
            panOnDrag={PAN_ON_DRAG}
            selectionMode={RF.SelectionMode.Full} // For selections happening inside Containment nodes it's better to leave it as "Full"
            isValidConnection={isValidConnection}
            connectionLineComponent={connectionLineComponent}
            onConnect={onConnect}
            onConnectStart={onConnectStart}
            onConnectEnd={onConnectEnd}
            // (begin)
            // 'Starting to drag' and 'dragging' should have the same behavior. Otherwise,
            // clicking a node and letting it go, without moving, won't work properly, and
            // Nodes will be removed from Containment Nodes.
            onNodeDragStart={onNodeDragStart}
            onNodeDrag={onNodeDrag}
            // (end)
            onNodeDragStop={onNodeDragStop}
            nodeTypes={nodeComponents}
            edgeTypes={edgeComponents}
            snapToGrid={true}
            snapGrid={xyFlowSnapGrid}
            defaultViewport={DEFAULT_VIEWPORT}
            fitView={false}
            fitViewOptions={FIT_VIEW_OPTIONS}
            attributionPosition={"bottom-right"}
            onInit={setReactFlowInstance}
            deleteKeyCode={DELETE_NODE_KEY_CODES}
            // (begin)
            // Used to make the Palette work by dropping nodes on the Reactflow Canvas
            onDrop={onDrop}
            onDragOver={onDragOver}
            // (end)
          >
            {children}
            <SelectionStatusLabel />
            {!isFirefox && <RF.Background />}
            <RF.Controls fitViewOptions={FIT_VIEW_OPTIONS} position={"bottom-right"} />
            <SetConnectionToReactFlowStore />
          </RF.ReactFlow>
        </WaypointActionsContextProvider>
      </I18nDictionariesProvider>
    </>
  );
}

export function SetConnectionToReactFlowStore(props: {}) {
  const ongoingConnection = useXyFlowReactKieDiagramStore((s) => s.xyFlowReactKieDiagram.ongoingConnection);
  const xyFlowStoreApi = RF.useStoreApi();
  useEffect(() => {
    xyFlowStoreApi.setState({
      connectionHandleId: ongoingConnection?.handleId,
      connectionHandleType: ongoingConnection?.handleType,
      connectionNodeId: ongoingConnection?.nodeId,
    });
  }, [ongoingConnection?.handleId, ongoingConnection?.handleType, ongoingConnection?.nodeId, xyFlowStoreApi]);

  return <></>;
}

export function isAnyParentSelected<N extends string, NData extends XyFlowReactKieDiagramNodeData<N, NData>>(
  parentNode: RF.Node<NData, N> | undefined
): boolean {
  return (
    parentNode?.selected ||
    (parentNode?.data.parentXyFlowNode ? isAnyParentSelected(parentNode?.data.parentXyFlowNode) : false)
  );
}
