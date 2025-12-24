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

import {
  DiagramRef,
  OnConnectedNodeAdded,
  OnEdgeAdded,
  OnEdgeDeleted,
  OnEdgeUpdated,
  OnEscPressed,
  OnNodeAdded,
  OnNodeDeleted,
  OnNodeParented,
  OnNodeRepositioned,
  OnNodeResized,
  OnNodeUnparented,
  OnResetToBeforeEditingBegan,
  OnWaypointAdded,
  OnWaypointDeleted,
  OnWaypointRepositioned,
  XyFlowReactKieDiagram,
} from "@kie-tools/xyflow-react-kie-diagram/dist/diagram/XyFlowReactKieDiagram";
import { ConnectionLine as ReactFlowDiagramConnectionLine } from "@kie-tools/xyflow-react-kie-diagram/dist/edges/ConnectionLine";
import { EdgeMarkers } from "@kie-tools/xyflow-react-kie-diagram/dist/edges/EdgeMarkers";
import { ContainmentMode } from "@kie-tools/xyflow-react-kie-diagram/dist/graph/graphStructure";
import { getHandlePosition } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/DcMaths";
import { PositionalNodeHandleId } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/PositionalNodeHandles";
import { Draft } from "immer";
import * as React from "react";
import { useCallback, useState } from "react";
import * as RF from "reactflow";
import { useBpmnEditor } from "../BpmnEditorContext";
import { addConnectedNode } from "../mutations/addConnectedNode";
import { addEdge } from "../mutations/addEdge";
import { addEdgeWaypoint } from "../mutations/addEdgeWaypoint";
import { moveNodesInsideLane } from "../mutations/moveNodesInsideLane";
import { moveNodesInsideSubProcess } from "../mutations/moveNodesInsideSubProcess";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { deleteEdge } from "../mutations/deleteEdge";
import { deleteEdgeWaypoint } from "../mutations/deleteEdgeWaypoint";
import { deleteNode } from "../mutations/deleteNode";
import { moveNodesOutOfLane } from "../mutations/moveNodesOutOfLane";
import { moveNodesOutOfSubProcess } from "../mutations/moveNodesOutOfSubProcess";
import { makeBoundaryEvent } from "../mutations/makeBoundaryEvent";
import { detachBoundaryEvent } from "../mutations/detachBoundaryEvent";
import { repositionEdgeWaypoint } from "../mutations/repositionEdgeWaypoint";
import { repositionNode } from "../mutations/repositionNode";
import { resizeNode } from "../mutations/resizeNode";
import { normalize } from "../normalization/normalize";
import { BpmnDiagramLhsPanel, State } from "../store/Store";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../store/StoreContext";
import { BpmnDiagramCommands } from "./BpmnDiagramCommands";
import {
  BPMN_CONTAINMENT_MAP,
  BPMN_GRAPH_STRUCTURE,
  BpmnDiagramEdgeData,
  BpmnDiagramNodeData,
  BpmnEdgeType,
  BpmnNodeType,
  CONNECTION_LINE_EDGE_COMPONENTS_MAPPING,
  CONNECTION_LINE_NODE_COMPONENT_MAPPING,
  DEFAULT_NODE_SIZES,
  elementToNodeType,
  MIN_NODE_SIZES,
  NODE_TYPES,
  XY_FLOW_EDGE_TYPES,
  XY_FLOW_NODE_TYPES,
} from "./BpmnDiagramDomain";
import { BpmnDiagramEmptyState } from "./BpmnDiagramEmptyState";
import { TopRightCornerPanels } from "./BpmnDiagramTopRightPanels";
import { BpmnPalette, MIME_TYPE_FOR_BPMN_EDITOR_NEW_NODE_FROM_PALETTE } from "./BpmnPalette";
import { DiagramContainerContextProvider } from "./DiagramContainerContext";
import { useCustomTasks } from "../customTasks/BpmnEditorCustomTasksContextProvider";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { bpmnEditorI18nDefaults, bpmnEditorDictionaries, BpmnEditorI18nContext } from "../i18n";

export function BpmnDiagram({
  container,
  diagramRef,
  locale,
}: {
  diagramRef: React.RefObject<DiagramRef<BpmnNodeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>>;
  container: React.RefObject<HTMLElement>;
  locale: string;
}) {
  const [showEmptyState, setShowEmptyState] = useState(true);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const model = useBpmnEditorStore((s) => s.bpmn.model);
  const openLhsPanel = useBpmnEditorStore((s) => s.diagram.openLhsPanel);

  const { bpmnModelBeforeEditingRef } = useBpmnEditor();
  const { customTasks } = useCustomTasks();

  const onResetToBeforeEditingBegan: OnResetToBeforeEditingBegan<
    State,
    BpmnNodeType,
    BpmnEdgeType,
    BpmnDiagramNodeData,
    BpmnDiagramEdgeData
  > = useCallback(
    (stateDraft: Draft<State>) => {
      stateDraft.bpmn.model = normalize(bpmnModelBeforeEditingRef.current);
      stateDraft.xyFlowReactKieDiagram.draggingNodes = [];
      stateDraft.xyFlowReactKieDiagram.draggingWaypoints = [];
      stateDraft.xyFlowReactKieDiagram.resizingNodes = [];
      stateDraft.xyFlowReactKieDiagram.dropTarget = undefined;
      stateDraft.xyFlowReactKieDiagram.edgeIdBeingUpdated = undefined;
    },
    [bpmnModelBeforeEditingRef]
  );

  const nodes = useBpmnEditorStore((s) => s.computed(s).getDiagramData().nodes);

  const isEmptyStateShowing = showEmptyState && nodes.length === 0 && openLhsPanel === BpmnDiagramLhsPanel.NONE;

  // nodes

  const onNodeAdded = useCallback<OnNodeAdded<State, BpmnNodeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>>(
    ({ state, type, element, dropPoint, data }) => {
      console.log("BPMN EDITOR DIAGRAM: onNodeAdded");

      if (data.bpmnElement.__$$element === "task") {
        for (const ct of customTasks ?? []) {
          if (ct.matches(data.bpmnElement)) {
            ct.onAdded?.(state, data.bpmnElement);
          }
        }
      }

      const { id } = addStandaloneNode({
        definitions: state.bpmn.model.definitions,
        __readonly_element: element as keyof typeof elementToNodeType,
        __readonly_newNode: {
          type,
          bounds: {
            "@_x": dropPoint.x,
            "@_y": dropPoint.y,
            "@_width": DEFAULT_NODE_SIZES[type]({ snapGrid: state.xyFlowReactKieDiagram.snapGrid })["@_width"],
            "@_height": DEFAULT_NODE_SIZES[type]({ snapGrid: state.xyFlowReactKieDiagram.snapGrid })["@_height"],
          },
          data,
        },
      });

      return { newNodeId: id };
    },
    [customTasks]
  );

  const onConnectedNodeAdded = useCallback<
    OnConnectedNodeAdded<State, BpmnNodeType, BpmnEdgeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>
  >(({ state, sourceNode, newNodeType, edgeType, dropPoint }) => {
    console.log("BPMN EDITOR DIAGRAM: onConnectedNodeAdded");
    const { id } = addConnectedNode({
      definitions: state.bpmn.model.definitions,
      __readonly_sourceNode: {
        bounds: sourceNode.data.shape["dc:Bounds"],
        id: sourceNode.id,
        shapeId: sourceNode.data.shape["@_id"],
        type: sourceNode.type as BpmnNodeType,
      },
      __readonly_newNode: {
        type: newNodeType,
        bounds: {
          "@_x": dropPoint.x,
          "@_y": dropPoint.y,
          "@_width": DEFAULT_NODE_SIZES[newNodeType]({ snapGrid: state.xyFlowReactKieDiagram.snapGrid })["@_width"],
          "@_height": DEFAULT_NODE_SIZES[newNodeType]({ snapGrid: state.xyFlowReactKieDiagram.snapGrid })["@_height"],
        },
      },
    });
    return { newNodeId: id };
  }, []);

  const onNodeRepositioned = useCallback<
    OnNodeRepositioned<State, BpmnNodeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>
  >(({ state, node, controlWaypointsByEdge, newPosition, childNodeIds }) => {
    console.log("BPMN EDITOR DIAGRAM: onNodeRepositioned");
    const { delta } = repositionNode({
      definitions: state.bpmn.model.definitions,
      controlWaypointsByEdge,
      __readonly_change: {
        type: "absolute",
        nodeType: node.type as BpmnNodeType,
        selectedEdges: [...state.computed(state).getDiagramData().selectedEdgesById.keys()],
        shapeIndex: node.data.shapeIndex,
        sourceEdgeIndexes: state
          .computed(state)
          .getDiagramData()
          .edges.flatMap((e) => (e.source === node.id && e.data?.bpmnEdge ? [e.data.bpmnEdgeIndex] : [])),
        targetEdgeIndexes: state
          .computed(state)
          .getDiagramData()
          .edges.flatMap((e) => (e.target === node.id && e.data?.bpmnEdge ? [e.data.bpmnEdgeIndex] : [])),
        position: newPosition,
      },
    });

    const allEdgeIds = state
      .computed(state)
      .getDiagramData()
      .edges.map((e) => e.id); // Simulate all edges being selected

    for (const nestedId of childNodeIds) {
      const nestedNode = state.computed(state).getDiagramData().nodesById.get(nestedId);
      if (!nestedNode) {
        throw new Error("Can't reposition nested node with id " + nestedId);
      }

      repositionNode({
        definitions: state.bpmn.model.definitions,
        controlWaypointsByEdge,
        __readonly_change: {
          type: "offset",
          nodeType: nestedNode.type as BpmnNodeType,
          selectedEdges: allEdgeIds, // Makes sure all internal waypoints move too.
          shapeIndex: nestedNode.data.shapeIndex,
          sourceEdgeIndexes: state
            .computed(state)
            .getDiagramData()
            .edges.flatMap((e) => (e.source === nestedId && e.data?.bpmnEdge ? [e.data.bpmnEdgeIndex] : [])),
          targetEdgeIndexes: state
            .computed(state)
            .getDiagramData()
            .edges.flatMap((e) => (e.target === nestedId && e.data?.bpmnEdge ? [e.data.bpmnEdgeIndex] : [])),
          offset: {
            deltaX: delta.x,
            deltaY: delta.y,
          },
        },
      });
    }
  }, []);

  const onNodeDeleted = useCallback<OnNodeDeleted<State, BpmnNodeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>>(
    ({ state, node }) => {
      console.log("BPMN EDITOR DIAGRAM: onNodeDeleted");
      deleteNode({
        definitions: state.bpmn.model.definitions,
        __readonly_bpmnElementId: node.data.bpmnElement?.["@_id"],
        __readonly_bpmnEdgeData: state
          .computed(state)
          .getDiagramData()
          .edges.map((e) => e.data!),
      });
    },
    []
  );

  const onNodeUnparented = useCallback<OnNodeUnparented<State, BpmnNodeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>>(
    ({ state, activeNode, exParentNode, selectedNodes }) => {
      console.log("BPMN EDITOR DIAGRAM: onNodeUnparented");
      if (exParentNode.type === NODE_TYPES.subProcess) {
        // ContainmentMode was INSIDE
        moveNodesOutOfSubProcess({
          definitions: state.bpmn.model.definitions,
          __readonly_subProcessId: exParentNode.data.bpmnElement?.["@_id"],
          __readonly_nodeIds: selectedNodes.flatMap((s) => s.data.bpmnElement?.["@_id"] ?? []),
        });
      }

      if (exParentNode.type === NODE_TYPES.lane) {
        // ContainmentMode was INSIDE
        moveNodesOutOfLane({
          definitions: state.bpmn.model.definitions,
          __readonly_laneId: exParentNode.data.bpmnElement?.["@_id"],
          __readonly_nodeIds: selectedNodes.flatMap((s) => s.data.bpmnElement?.["@_id"] ?? []),
        });
      }

      if (
        (exParentNode.type === NODE_TYPES.subProcess || exParentNode.type === NODE_TYPES.task) &&
        activeNode.type === NODE_TYPES.intermediateCatchEvent &&
        activeNode.data.bpmnElement?.__$$element === "boundaryEvent"
      ) {
        if (selectedNodes.length > 1) {
          throw new Error("Can't unparent more than one node when boundary events are selected.");
        }

        // ContainmentMode was BORDER
        detachBoundaryEvent({
          definitions: state.bpmn.model.definitions,
          __readonly_eventId: activeNode.data.bpmnElement?.["@_id"],
        });
      }
    },
    []
  );

  const onNodeParented = useCallback<OnNodeParented<State, BpmnNodeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>>(
    ({ state, containmentMode, activeNode, parentNode, selectedNodes }) => {
      console.log(`BPMN EDITOR DIAGRAM: onNodeParented (${containmentMode})`);
      if (containmentMode === ContainmentMode.INSIDE && parentNode.type === NODE_TYPES.subProcess) {
        moveNodesInsideSubProcess({
          definitions: state.bpmn.model.definitions,
          __readonly_subProcessId: parentNode.data.bpmnElement?.["@_id"],
          __readonly_nodeIds: selectedNodes.flatMap((s) => s.data.bpmnElement?.["@_id"] ?? []),
        });
      } else if (containmentMode === ContainmentMode.INSIDE && parentNode.type === NODE_TYPES.lane) {
        moveNodesInsideLane({
          definitions: state.bpmn.model.definitions,
          __readonly_laneId: parentNode.data.bpmnElement?.["@_id"],
          __readonly_nodeIds: selectedNodes.flatMap((s) => s.data.bpmnElement?.["@_id"] ?? []),
        });
      } else if (
        containmentMode === ContainmentMode.BORDER &&
        (parentNode.type === NODE_TYPES.subProcess || parentNode.type === NODE_TYPES.task)
      ) {
        makeBoundaryEvent({
          definitions: state.bpmn.model.definitions,
          __readonly_targetActivityId: parentNode.data.bpmnElement?.["@_id"],
          __readonly_eventId: activeNode.data.bpmnElement?.["@_id"],
        });
      }
    },
    []
  );

  const onNodeResized = useCallback<OnNodeResized<State, BpmnNodeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>>(
    ({ state, node, newDimensions }) => {
      console.log("BPMN EDITOR DIAGRAM: onNodeResized");
      resizeNode({
        definitions: state.bpmn.model.definitions,
        __readonly_snapGrid: state.xyFlowReactKieDiagram.snapGrid,
        __readonly_change: {
          bpmnElement: node.data.bpmnElement,
          nodeType: node.type!,
          shapeIndex: node.data.shapeIndex,
          sourceEdgeIndexes: state
            .computed(state)
            .getDiagramData()
            .edges.flatMap((e) => (e.source === node.id && e.data?.bpmnEdge ? [e.data.bpmnEdgeIndex] : [])),
          targetEdgeIndexes: state
            .computed(state)
            .getDiagramData()
            .edges.flatMap((e) => (e.target === node.id && e.data?.bpmnEdge ? [e.data.bpmnEdgeIndex] : [])),
          dimension: {
            "@_width": newDimensions.width,
            "@_height": newDimensions.height,
          },
        },
      });
    },
    []
  );

  // edges

  const onEdgeAdded = useCallback<
    OnEdgeAdded<State, BpmnNodeType, BpmnEdgeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>
  >(({ state, edgeType, sourceNode, targetNode, targetHandle }) => {
    console.log("BPMN EDITOR DIAGRAM: onEdgeAdded");
    addEdge({
      definitions: state.bpmn.model.definitions,
      __readonly_edge: {
        type: edgeType as BpmnEdgeType,
        targetHandle: targetHandle,
        sourceHandle: PositionalNodeHandleId.Center,
        autoPositionedEdgeMarker: undefined,
        name: undefined,
        documentation: undefined,
      },
      __readonly_sourceNode: {
        type: sourceNode.type as BpmnNodeType,
        href: sourceNode.id,
        bounds: sourceNode.data.shape["dc:Bounds"],
        shapeId: sourceNode.data.shape["@_id"],
      },
      __readonly_targetNode: {
        type: targetNode.type as BpmnNodeType,
        href: targetNode.id,
        bounds: targetNode.data.shape["dc:Bounds"],
        shapeId: targetNode.data.shape["@_id"],
      },
      __readonly_keepWaypoints: false,
    });
  }, []);

  const onEdgeUpdated = useCallback<
    OnEdgeUpdated<State, BpmnNodeType, BpmnEdgeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>
  >(({ state, edge, targetNode, sourceNode, targetHandle, sourceHandle, firstWaypoint, lastWaypoint }) => {
    console.log("BPMN EDITOR DIAGRAM: onEdgeUpdated");
    const { newBpmnEdge } = addEdge({
      definitions: state.bpmn.model.definitions,
      __readonly_edge: {
        autoPositionedEdgeMarker: undefined,
        type: edge.type as BpmnEdgeType,
        targetHandle: ((targetHandle as PositionalNodeHandleId) ??
          getHandlePosition({ shapeBounds: targetNode.data.shape["dc:Bounds"], waypoint: lastWaypoint })
            .handlePosition) as PositionalNodeHandleId,
        sourceHandle: ((sourceHandle as PositionalNodeHandleId) ??
          getHandlePosition({ shapeBounds: sourceNode.data.shape["dc:Bounds"], waypoint: firstWaypoint })
            .handlePosition) as PositionalNodeHandleId,
        name: edge.data?.bpmnElement.__$$element === "sequenceFlow" ? edge.data.bpmnElement["@_name"] : undefined,
        documentation:
          edge.data?.bpmnElement.__$$element === "sequenceFlow" ? edge.data.bpmnElement.documentation : undefined,
      },
      __readonly_sourceNode: {
        type: sourceNode.type!,
        href: sourceNode.id,
        bounds: sourceNode.data.shape["dc:Bounds"],
        shapeId: sourceNode.data.shape["@_id"],
      },
      __readonly_targetNode: {
        type: targetNode.type!,
        href: targetNode.id,
        bounds: targetNode.data.shape["dc:Bounds"],
        shapeId: targetNode.data.shape["@_id"],
      },
      __readonly_keepWaypoints: true,
    });

    // The BPMN Edge changed nodes, so we need to delete the old one, but keep the waypoints.
    if (newBpmnEdge["@_bpmnElement"] !== edge.id) {
      const { deletedBpmnEdge } = deleteEdge({
        definitions: state.bpmn.model.definitions,
        __readonly_edgeId: edge.id,
      });
      const deletedWaypoints = deletedBpmnEdge?.["di:waypoint"];

      if (edge.source !== sourceNode.id && deletedWaypoints) {
        newBpmnEdge["di:waypoint"] = [newBpmnEdge["di:waypoint"]![0], ...deletedWaypoints.slice(1)];
      }

      if (edge.target !== targetNode.id && deletedWaypoints) {
        newBpmnEdge["di:waypoint"] = [
          ...deletedWaypoints.slice(0, deletedWaypoints.length - 1),
          newBpmnEdge["di:waypoint"]![newBpmnEdge["di:waypoint"]!.length - 1],
        ];
      }
    }

    return { id: newBpmnEdge["@_bpmnElement"]! };
  }, []);

  const onEdgeDeleted = useCallback<
    OnEdgeDeleted<State, BpmnNodeType, BpmnEdgeType, BpmnDiagramNodeData, BpmnDiagramEdgeData>
  >(({ state, edge }) => {
    console.log("BPMN EDITOR DIAGRAM: onEdgeDeleted");
    deleteEdge({ definitions: state.bpmn.model.definitions, __readonly_edgeId: edge.id });
  }, []);

  // waypoints

  const onWaypointAdded = useCallback<OnWaypointAdded>(
    ({ beforeIndex, edgeIndex, waypoint }) => {
      console.log("BPMN EDITOR DIAGRAM: onWaypointAdded");
      bpmnEditorStoreApi.setState((s) => {
        addEdgeWaypoint({
          definitions: s.bpmn.model.definitions,
          __readonly_edgeIndex: edgeIndex,
          __readonly_beforeIndex: beforeIndex,
          __readonly_waypoint: waypoint,
        });
      });
    },
    [bpmnEditorStoreApi]
  );

  const onWaypointRepositioned = useCallback<OnWaypointRepositioned>(
    ({ waypointIndex, edgeIndex, waypoint }) => {
      console.log("BPMN EDITOR DIAGRAM: onWaypointRepositioned");
      bpmnEditorStoreApi.setState((s) => {
        repositionEdgeWaypoint({
          definitions: s.bpmn.model.definitions,
          __readonly_edgeIndex: edgeIndex,
          __readonly_waypoint: waypoint,
          __readonly_waypointIndex: waypointIndex,
        });
      });
    },
    [bpmnEditorStoreApi]
  );

  const onWaypointDeleted = useCallback<OnWaypointDeleted>(
    ({ waypointIndex, edgeIndex }) => {
      console.log("BPMN EDITOR DIAGRAM: onWaypointDeleted");
      bpmnEditorStoreApi.setState((s) => {
        deleteEdgeWaypoint({
          definitions: s.bpmn.model.definitions,
          __readonly_edgeIndex: edgeIndex,
          __readonly_waypointIndex: waypointIndex,
        });
      });
    },
    [bpmnEditorStoreApi]
  );

  // misc

  const onEscPressed = useCallback<OnEscPressed>(() => {
    bpmnEditorStoreApi.setState((state) => {
      state.propertiesPanel.isOpen = false;
      state.diagram.overlaysPanel.isOpen = false;
      state.diagram.openLhsPanel = BpmnDiagramLhsPanel.NONE;
    });
  }, [bpmnEditorStoreApi]);

  return (
    <>
      {isEmptyStateShowing && <BpmnDiagramEmptyState setShowEmptyState={setShowEmptyState} />}
      <I18nDictionariesProvider
        defaults={bpmnEditorI18nDefaults}
        dictionaries={bpmnEditorDictionaries}
        initialLocale={locale}
        ctx={BpmnEditorI18nContext}
      >
        <DiagramContainerContextProvider container={container}>
          <svg style={{ position: "absolute", top: 0, left: 0 }}>
            <EdgeMarkers />
          </svg>

          <XyFlowReactKieDiagram
            // infra
            diagramRef={diagramRef}
            container={container}
            // model
            modelBeforeEditingRef={bpmnModelBeforeEditingRef}
            model={model}
            onResetToBeforeEditingBegan={onResetToBeforeEditingBegan}
            // components
            connectionLineComponent={ConnectionLine}
            nodeComponents={XY_FLOW_NODE_TYPES}
            edgeComponents={XY_FLOW_EDGE_TYPES}
            // domain
            newNodeMimeType={MIME_TYPE_FOR_BPMN_EDITOR_NEW_NODE_FROM_PALETTE}
            containmentMap={BPMN_CONTAINMENT_MAP}
            nodeTypes={NODE_TYPES}
            minNodeSizes={MIN_NODE_SIZES}
            graphStructure={BPMN_GRAPH_STRUCTURE}
            allowCycles={true}
            // actions
            onNodeAdded={onNodeAdded}
            onConnectedNodeAdded={onConnectedNodeAdded}
            onNodeRepositioned={onNodeRepositioned}
            onNodeDeleted={onNodeDeleted}
            onEdgeAdded={onEdgeAdded}
            onEdgeUpdated={onEdgeUpdated}
            onEdgeDeleted={onEdgeDeleted}
            onNodeUnparented={onNodeUnparented}
            onNodeParented={onNodeParented}
            onNodeResized={onNodeResized}
            onEscPressed={onEscPressed}
            onWaypointAdded={onWaypointAdded}
            onWaypointRepositioned={onWaypointRepositioned}
            onWaypointDeleted={onWaypointDeleted}
          >
            <BpmnPalette pulse={isEmptyStateShowing} />
            <TopRightCornerPanels availableHeight={container.current?.offsetHeight} />
            <BpmnDiagramCommands />
          </XyFlowReactKieDiagram>
        </DiagramContainerContextProvider>
      </I18nDictionariesProvider>
    </>
  );
}

export function ConnectionLine<N extends string, E extends string>(props: RF.ConnectionLineComponentProps) {
  return (
    <ReactFlowDiagramConnectionLine
      {...props}
      defaultNodeSizes={DEFAULT_NODE_SIZES}
      minNodeSizes={MIN_NODE_SIZES}
      graphStructure={BPMN_GRAPH_STRUCTURE}
      nodeComponentsMapping={CONNECTION_LINE_NODE_COMPONENT_MAPPING}
      edgeComponentsMapping={CONNECTION_LINE_EDGE_COMPONENTS_MAPPING}
    />
  );
}
