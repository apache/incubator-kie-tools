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
import { useOnViewportChange, Viewport } from "reactflow";
import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateIcon,
  EmptyStateBody,
  EmptyStateHeader,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { MousePointerIcon } from "@patternfly/react-icons/dist/js/icons/mouse-pointer-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { VirtualMachineIcon } from "@patternfly/react-icons/dist/js/icons/virtual-machine-icon";
import { useSwfEditor } from "../SwfEditorContext";
import { AutolayoutButton } from "../autolayout/AutolayoutButton";
import { addEdge } from "../mutations/addEdge";
import { deleteEdge } from "../mutations/deleteEdge";
import { OverlaysPanel } from "../overlaysPanel/OverlaysPanel";
import { SnapGrid, State } from "../store/Store";
import { useSwfEditorStore, useSwfEditorStoreApi } from "../store/StoreContext";
import { DiagramContainerContextProvider } from "./DiagramContainerContext";
import { ConnectionLine } from "./connections/ConnectionLine";
import { PositionalNodeHandleId } from "./connections/PositionalNodeHandles";
import { containment, EdgeType, getDefaultEdgeTypeBetween, NodeType } from "./connections/graphStructure";
import { checkIsValidConnection } from "./connections/isValidConnection";
import { EdgeMarkers } from "./edges/EdgeMarkers";
import { EDGE_TYPES } from "./edges/SwfEdgeTypes";
import {
  TransitionEdge,
  SwfDiagramEdgeData,
  ErrorTransitionEdge,
  EventConditionTransitionEdge,
  DefaultConditionTransitionEdge,
  CompensationTransitionEdge,
  DataConditionTransitionEdge,
} from "./edges/SwfEdges";
import { buildHierarchy } from "./graph/graph";
import { getContainmentRelationship, getSwfBoundsCenterPoint, getHandlePosition } from "./maths/SwfMaths";
import { DEFAULT_NODE_SIZES, MIN_NODE_SIZES } from "./nodes/SwfDefaultSizes";
import { NODE_TYPES } from "./nodes/SwfNodeTypes";
import {
  OperationState,
  SwitchState,
  SleepState,
  SwfDiagramNodeData,
  ParallelState,
  InjectState,
  ForEachState,
  CallbackState,
  EventState,
  UnknownNode,
} from "./nodes/SwfNodes";
import { DiagramCommands } from "./DiagramCommands";
import { getAutoLayoutedInfo } from "../autolayout/autoLayoutInfo";
import { useSettings } from "../settings/SwfEditorSettingsContext";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { applyAutoLayoutToSwf } from "../mutations/applyAutoLayoutToSwf";
import { useSwfEditorI18n } from "../i18n";

const isFirefox = typeof (window as any).InstallTrigger !== "undefined"; // See https://stackoverflow.com/questions/9847580/how-to-detect-safari-chrome-ie-firefox-and-opera-browsers

const PAN_ON_DRAG = [1, 2];

const FIT_VIEW_OPTIONS: RF.FitViewOptions = { maxZoom: 1, minZoom: 0.1, duration: 400 };

export const DEFAULT_VIEWPORT = { x: 100, y: 100, zoom: 1 };

const DELETE_NODE_KEY_CODES = ["Backspace", "Delete"];

const AREA_ABOVE_OVERLAYS_PANEL = 120;

const nodeTypes: Record<NodeType, any> = {
  [NODE_TYPES.callbackState]: CallbackState,
  [NODE_TYPES.eventState]: EventState,
  [NODE_TYPES.foreachState]: ForEachState,
  [NODE_TYPES.injectState]: InjectState,
  [NODE_TYPES.operationState]: OperationState,
  [NODE_TYPES.parallelState]: ParallelState,
  [NODE_TYPES.sleepState]: SleepState,
  [NODE_TYPES.switchState]: SwitchState,
  [NODE_TYPES.unknown]: UnknownNode,
};

const edgeTypes: Record<EdgeType, any> = {
  [EDGE_TYPES.compensationTransition]: CompensationTransitionEdge,
  [EDGE_TYPES.dataConditionTransition]: DataConditionTransitionEdge,
  [EDGE_TYPES.defaultConditionTransition]: DefaultConditionTransitionEdge,
  [EDGE_TYPES.errorTransition]: ErrorTransitionEdge,
  [EDGE_TYPES.eventConditionTransition]: EventConditionTransitionEdge,
  [EDGE_TYPES.transition]: TransitionEdge,
};

const MIME_TYPE_FOR_SWF_EDITOR_NODE = "kie-swf-editor--node";

export type DiagramRef = {
  getReactFlowInstance: () => RF.ReactFlowInstance | undefined;
};

export const Diagram = React.forwardRef<DiagramRef, { container: React.RefObject<HTMLElement> }>(
  ({ container }, ref) => {
    // Contexts

    const swfEditorStoreApi = useSwfEditorStoreApi();
    const snapGrid = useSwfEditorStore((s) => s.diagram.snapGrid);
    const thisSwf = useSwfEditorStore((s) => s.swf);
    const settings = useSettings();
    const { swfModelBeforeEditingRef } = useSwfEditor();

    // State

    const [reactFlowInstance, setReactFlowInstance] = useState<
      RF.ReactFlowInstance<SwfDiagramNodeData, SwfDiagramEdgeData> | undefined
    >(undefined);

    const viewport = useSwfEditorStore((s) => s.diagram.viewport);

    // Refs

    React.useImperativeHandle(
      ref,
      () => ({
        getReactFlowInstance: () => {
          return reactFlowInstance;
        },
      }),
      [reactFlowInstance]
    );

    const nodeIdBeingDraggedRef = useRef<string | null>(null);

    // Memos

    const rfSnapGrid = useMemo<[number, number]>(
      () => (snapGrid.isEnabled ? [snapGrid.x, snapGrid.y] : [1, 1]),
      [snapGrid.isEnabled, snapGrid.x, snapGrid.y]
    );

    // Callbacks

    const getFirstNodeFittingBounds = useCallback(
      (
        nodeIdToIgnore: string,
        bounds: RF.Rect,
        minSizes: (args: { snapGrid: SnapGrid }) => RF.Dimensions,
        snapGrid: SnapGrid
      ) =>
        reactFlowInstance
          ?.getNodes()
          .reverse() // Respect the nodes z-index.
          .find(
            (node) =>
              node.id !== nodeIdToIgnore && // don't ever use the node being dragged
              getContainmentRelationship({
                bounds: bounds!,
                container: { ...DEFAULT_NODE_SIZES[node.type as NodeType]({ snapGrid: snapGrid }), ...{ x: 0, y: 0 } },
                snapGrid,
                containerMinSizes: DEFAULT_NODE_SIZES[node.type as NodeType],
                boundsMinSizes: minSizes,
              }).isInside
          ),
      [reactFlowInstance]
    );

    const ongoingConnection = useSwfEditorStore((s) => s.diagram.ongoingConnection);
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

    const isValidConnection = useCallback<RF.IsValidConnection>(
      (edgeOrConnection) => {
        const state = swfEditorStoreApi.getState();
        const edgeId = state.diagram.edgeIdBeingUpdated;
        const edgeType = edgeId ? (reactFlowInstance?.getEdge(edgeId)?.type as EdgeType) : undefined;

        const ongoingConnectionHierarchy = buildHierarchy({
          nodeId: state.diagram.ongoingConnection?.nodeId,
          edges: state.computed(state).getDiagramData().swfEdges,
        });

        return (
          // Reflexive edges are not allowed for SWF
          edgeOrConnection.source !== edgeOrConnection.target &&
          // Matches SWFs structure.
          checkIsValidConnection(state.computed(state).getDiagramData().nodesById, edgeOrConnection, edgeType) &&
          // Does not form cycles.
          !!edgeOrConnection.target &&
          !ongoingConnectionHierarchy.dependencies.has(edgeOrConnection.target) &&
          !!edgeOrConnection.source &&
          !ongoingConnectionHierarchy.dependents.has(edgeOrConnection.source)
        );
      },
      [swfEditorStoreApi, reactFlowInstance]
    );

    const onNodesChange = useCallback<RF.OnNodesChange>(
      (changes) => {
        swfEditorStoreApi.setState((state) => {
          for (const change of changes) {
            switch (change.type) {
              case "add":
                console.debug(`SWF DIAGRAM: 'onNodesChange' --> add '${change.item.id}'`);
                //TODO: edit mode
                break;
              case "dimensions":
                console.debug(`SWF DIAGRAM: 'onNodesChange' --> dimensions '${change.id}'`);
                //TODO: edit mode
                break;
              case "position":
                console.debug(`SWF DIAGRAM: 'onNodesChange' --> position '${change.id}'`);
                state.dispatch(state).diagram.setNodeStatus(change.id, { dragging: change.dragging });
                state.layout(state).setNodePosition(change.id, change.position);
                break;
              case "remove":
                console.debug(`SWF DIAGRAM: 'onNodesChange' --> remove '${change.id}'`);
                //TODO: edit mode
                break;
              case "reset":
                state.dispatch(state).diagram.setNodeStatus(change.item.id, {
                  selected: false,
                  dragging: false,
                });
                break;
              case "select":
                state.dispatch(state).diagram.setNodeStatus(change.id, { selected: change.selected });
                break;
            }
          }
        });
      },
      [swfEditorStoreApi]
    );

    const resetToBeforeEditingBegan = useCallback(() => {
      swfEditorStoreApi.setState((state) => {
        state.swf.model = swfModelBeforeEditingRef.current;
        state.diagram.draggingNodes = [];
        state.diagram.draggingWaypoints = [];
        state.diagram.dropTargetNode = undefined;
        state.diagram.edgeIdBeingUpdated = undefined;
      });
    }, [swfEditorStoreApi, swfModelBeforeEditingRef]);

    const onNodeDrag = useCallback<RF.NodeDragHandler>(
      (e, node: RF.Node<SwfDiagramNodeData>) => {
        nodeIdBeingDraggedRef.current = node.id;
        swfEditorStoreApi.setState((state) => {
          state.diagram.dropTargetNode = getFirstNodeFittingBounds(
            node.id,
            {
              // We can't use node.data.swfObject because it hasn't been updated at this point yet.
              x: node.positionAbsolute?.x ?? 0,
              y: node.positionAbsolute?.y ?? 0,
              width: node.width ?? 0,
              height: node.height ?? 0,
            },
            MIN_NODE_SIZES[node.type as NodeType],
            state.diagram.snapGrid
          );
        });
      },
      [swfEditorStoreApi, getFirstNodeFittingBounds]
    );

    const onNodeDragStart = useCallback<RF.NodeDragHandler>(
      (e, node: RF.Node<SwfDiagramNodeData>, nodes) => {
        swfModelBeforeEditingRef.current = thisSwf.model;
        onNodeDrag(e, node, nodes);
      },
      [thisSwf.model, swfModelBeforeEditingRef, onNodeDrag]
    );

    const onNodeDragStop = useCallback<RF.NodeDragHandler>(
      (e, node: RF.Node<SwfDiagramNodeData>) => {
        try {
          swfEditorStoreApi.setState((state) => {
            console.debug("SWF DIAGRAM: `onNodeDragStop`");
            const nodeBeingDragged = state
              .computed(state)
              .getDiagramData()
              .nodesById.get(nodeIdBeingDraggedRef.current!);
            nodeIdBeingDraggedRef.current = null;
            if (!nodeBeingDragged) {
              return;
            }

            // Validate
            const dropTargetNode = swfEditorStoreApi.getState().diagram.dropTargetNode;
            if (
              dropTargetNode &&
              containment.has(dropTargetNode.type as NodeType) &&
              !state.computed(state).isDropTargetNodeValidForSelection
            ) {
              console.debug(
                `SWF DIAGRAM: Invalid containment: '${[
                  ...state.computed(state).getDiagramData().selectedNodeTypes,
                ].join("', '")}' inside '${dropTargetNode.type}'. Ignoring nodes dropped.`
              );
              resetToBeforeEditingBegan();
              return;
            }

            state.diagram.dropTargetNode = undefined;
          });
        } catch (e) {
          console.error(e);
          resetToBeforeEditingBegan();
        }
      },
      [swfEditorStoreApi, resetToBeforeEditingBegan]
    );

    const onEdgesChange = useCallback<RF.OnEdgesChange>(
      (changes) => {
        swfEditorStoreApi.setState((state) => {
          for (const change of changes) {
            switch (change.type) {
              case "select":
                console.debug(`SWF DIAGRAM: 'onEdgesChange' --> select '${change.id}'`);
                state.dispatch(state).diagram.setEdgeStatus(change.id, { selected: change.selected });
                break;
              case "remove":
                console.debug(`SWF DIAGRAM: 'onEdgesChange' --> remove '${change.id}'`);
                //TODO: Edit mode
                break;
              case "add":
              case "reset":
                console.debug(`SWF DIAGRAM: 'onEdgesChange' --> add/reset '${change.item.id}'. Ignoring`);
            }
          }
        });
      },
      [swfEditorStoreApi]
    );

    const onEdgeUpdate = useCallback<RF.OnEdgeUpdateFunc<SwfDiagramEdgeData>>(
      (oldEdge, newConnection) => {
        console.debug("SWF DIAGRAM: `onEdgeUpdate`", oldEdge, newConnection);
        swfEditorStoreApi.setState((state) => {
          const sourceNode = state.computed(state).getDiagramData().nodesById.get(newConnection.source!);
          const targetNode = state.computed(state).getDiagramData().nodesById.get(newConnection.target!);
          if (!sourceNode || !targetNode) {
            throw new Error("Cannot create connection without target and source nodes!");
          }

          const sourceBounds: RF.Rect = {
            height: sourceNode.height!,
            width: sourceNode.width!,
            ...sourceNode.position,
          };
          const targetBounds: RF.Rect = {
            height: targetNode.height!,
            width: targetNode.width!,
            ...targetNode.position,
          };
          if (!sourceBounds || !targetBounds) {
            throw new Error("Cannot create connection without target bounds!");
          }

          // --------- This is where we draw the line between the diagram and the model.

          // Delete the current edge from the model
          deleteEdge({
            definitions: state.swf.model,
            edge: {
              id: oldEdge.id,
              swfObject: oldEdge.data!.swfObject,
              sourceId: oldEdge.data!.swfSource!.id!,
              targetId: oldEdge.data!.swfTarget!.id!,
            },
          });

          const lastWaypoint = getSwfBoundsCenterPoint(targetBounds);
          const firstWaypoint = getSwfBoundsCenterPoint(sourceBounds);

          // Create a new edge in the model
          const newEdgeId = addEdge({
            definitions: state.swf.model,
            edge: {
              autoPositionedEdgeMarker: undefined,
              type: oldEdge.type as EdgeType,
              targetHandle: ((newConnection.targetHandle as PositionalNodeHandleId) ??
                getHandlePosition({ shapeBounds: targetBounds, waypoint: lastWaypoint })
                  .handlePosition) as PositionalNodeHandleId,
              sourceHandle: ((newConnection.sourceHandle as PositionalNodeHandleId) ??
                getHandlePosition({ shapeBounds: sourceBounds, waypoint: firstWaypoint })
                  .handlePosition) as PositionalNodeHandleId,
            },
            sourceNode: {
              type: sourceNode.type as NodeType,
              href: sourceNode.id,
              data: sourceNode.data,
              bounds: sourceBounds,
            },
            targetNode: {
              type: targetNode.type as NodeType,
              href: targetNode.id,
              data: targetNode.data,
              bounds: targetBounds,
            },
          });

          // Keep the updated edge selected
          state.diagram._selectedEdges = [newEdgeId];

          // Finish edge update atomically.
          state.diagram.ongoingConnection = undefined;
          state.diagram.edgeIdBeingUpdated = undefined;
        });
      },
      [swfEditorStoreApi]
    );

    const onEdgeUpdateStart = useCallback(
      (e: React.MouseEvent | React.TouchEvent, edge: RF.Edge, handleType: RF.HandleType) => {
        console.debug("SWF DIAGRAM: `onEdgeUpdateStart`");
        swfEditorStoreApi.setState((state) => {
          state.diagram.edgeIdBeingUpdated = edge.id;
        });
      },
      [swfEditorStoreApi]
    );

    const onEdgeUpdateEnd = useCallback(
      (e: MouseEvent | TouchEvent, edge: RF.Edge, handleType: RF.HandleType) => {
        console.debug("SWF DIAGRAM: `onEdgeUpdateEnd`");

        // Needed for when the edge update operation doesn't change anything.
        swfEditorStoreApi.setState((state) => {
          state.diagram.ongoingConnection = undefined;
          state.diagram.edgeIdBeingUpdated = undefined;
        });
      },
      [swfEditorStoreApi]
    );

    // Override Reactflow's behavior by intercepting the keydown event using its `capture` variant.
    const handleRfKeyDownCapture = useCallback(
      (e: React.KeyboardEvent) => {
        const s = swfEditorStoreApi.getState();

        if (e.key === "Escape") {
          if (s.computed(s).isDiagramEditingInProgress() && swfModelBeforeEditingRef.current) {
            console.debug(
              "SWF DIAGRAM: Intercepting Escape pressed and preventing propagation. Reverting SWF model to what it was before editing began."
            );

            e.stopPropagation();
            e.preventDefault();

            resetToBeforeEditingBegan();
          } else if (!s.diagram.ongoingConnection) {
            swfEditorStoreApi.setState((state) => {
              if (
                state.computed(s).getDiagramData().selectedNodesById.size > 0 ||
                state.computed(s).getDiagramData().selectedEdgesById.size > 0
              ) {
                console.debug("SWF DIAGRAM: Esc pressed. Desselecting everything.");
                state.diagram._selectedNodes = [];
                state.diagram._selectedEdges = [];
                e.stopPropagation();
                e.preventDefault();
              } else {
                // Let the
              }
            });
          } else {
            // Let the KeyboardShortcuts handle it.
          }
        }
      },
      [swfEditorStoreApi, swfModelBeforeEditingRef, resetToBeforeEditingBegan]
    );

    const [showEmptyState, setShowEmptyState] = useState(true);

    const nodes = useSwfEditorStore((s) => s.computed(s).getDiagramData(true).nodes);
    const edges = useSwfEditorStore((s) => s.computed(s).getDiagramData(true).edges);

    const canAutoGenerate = useSwfEditorStore((s) => s.diagram.autoLayout.canAutoGenerate);

    useMemo(() => {
      const state = swfEditorStoreApi.getState();
      const snapGrid = state.diagram.snapGrid;
      const nodesById = state.computed(state).getDiagramData().nodesById;
      const edgesById = state.computed(state).getDiagramData().edgesById;
      const swfNodes = state.computed(state).getDiagramData().nodes;

      getAutoLayoutedInfo({
        __readonly_snapGrid: snapGrid,
        __readonly_nodesById: nodesById,
        __readonly_edgesById: edgesById,
        __readonly_nodes: swfNodes,
      }).then((autoLayout) => {
        swfEditorStoreApi.setState((s) => {
          applyAutoLayoutToSwf({
            state: s,
            __readonly_autoLayoutedInfo: autoLayout,
          });
        });
      });
    }, [swfEditorStoreApi]);

    const isEmptyStateShowing = showEmptyState && nodes.length === 0;

    return (
      <>
        {isEmptyStateShowing && !canAutoGenerate && (
          <SwfDiagramEmptyState setShowEmptyState={setShowEmptyState} isReadOnly={settings.isReadOnly} />
        )}
        <DiagramContainerContextProvider container={container}>
          <svg style={{ position: "absolute", top: 0, left: 0 }}>
            <EdgeMarkers />
          </svg>

          <RF.ReactFlow
            connectionMode={RF.ConnectionMode.Loose} // Allow target handles to be used as source. This is very important for allowing the positional handles to be updated for the base of an edge.
            onKeyDownCapture={handleRfKeyDownCapture} // Override Reactflow's keyboard listeners.
            nodes={nodes}
            edges={edges}
            proOptions={{ hideAttribution: true }} // React Flow logo
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
            selectionMode={RF.SelectionMode.Full} // For selections happening inside Groups/DecisionServices it's better to leave it as "Full"
            isValidConnection={isValidConnection}
            connectionLineComponent={ConnectionLine}
            // (begin)
            // 'Starting to drag' and 'dragging' should have the same behavior. Otherwise,
            // clicking a node and letting it go, without moving
            onNodeDragStart={onNodeDragStart}
            onNodeDrag={onNodeDrag}
            // (end)
            onNodeDragStop={onNodeDragStop}
            nodesDraggable={!settings.isReadOnly}
            nodeTypes={nodeTypes}
            edgeTypes={edgeTypes}
            snapToGrid={true}
            snapGrid={rfSnapGrid}
            defaultViewport={viewport}
            fitView={false}
            fitViewOptions={FIT_VIEW_OPTIONS}
            attributionPosition={"bottom-right"}
            onInit={setReactFlowInstance}
            deleteKeyCode={settings.isReadOnly ? [] : DELETE_NODE_KEY_CODES}
          >
            <SelectionStatus />
            <TopRightCornerPanels availableHeight={container.current?.offsetHeight} />
            <DiagramCommands />
            {!isFirefox && <RF.Background />}
            <RF.Controls
              fitViewOptions={FIT_VIEW_OPTIONS}
              position={"bottom-right"}
              showInteractive={false} // Remove lock screen from zoombar
            />
            <SetConnectionToReactFlowStore />
            <ViewportWatcher />
          </RF.ReactFlow>
        </DiagramContainerContextProvider>
      </>
    );
  }
);

function SwfDiagramEmptyState({
  setShowEmptyState,
  isReadOnly,
}: {
  setShowEmptyState: React.Dispatch<React.SetStateAction<boolean>>;
  isReadOnly?: boolean;
}) {
  const { i18n } = useSwfEditorI18n();
  return (
    <Bullseye
      style={{
        position: "absolute",
        width: "100%",
        pointerEvents: "none",
        zIndex: 1,
        height: "auto",
        marginTop: "120px",
      }}
    >
      <div className={"kie-swf-editor--diagram-empty-state"}>
        <Button
          title={i18n.close}
          style={{
            position: "absolute",
            top: "8px",
            right: 0,
          }}
          variant={ButtonVariant.plain}
          icon={<TimesIcon />}
          onClick={() => setShowEmptyState(false)}
        />

        <EmptyState>
          <EmptyStateHeader>
            <EmptyStateIcon icon={MousePointerIcon} />
            <Title size={"md"} headingLevel={"h4"}>
              {i18n.nodes.swfDiagramEmpty}
            </Title>
          </EmptyStateHeader>
          {isReadOnly ? (
            <>
              <EmptyStateBody>{i18n.nodes.diagramHasNodesOrOpenAnotherFile}</EmptyStateBody>
            </>
          ) : (
            <>
              <EmptyStateBody>{i18n.nodes.startByDraggingNodes}</EmptyStateBody>
              <br />
            </>
          )}
        </EmptyState>
      </div>
    </Bullseye>
  );
}

export function ViewportWatcher() {
  const swfEditorStoreApi = useSwfEditorStoreApi();
  useOnViewportChange({
    onChange: (viewport: Viewport) => {
      swfEditorStoreApi.setState((state) => {
        state.diagram.viewport = {
          x: viewport.x,
          y: viewport.y,
          zoom: viewport.zoom,
        };
      });
    },
  });
  return <></>;
}

export function SetConnectionToReactFlowStore(props: {}) {
  const ongoingConnection = useSwfEditorStore((s) => s.diagram.ongoingConnection);
  const rfStoreApi = RF.useStoreApi();
  useEffect(() => {
    rfStoreApi.setState({
      connectionHandleId: ongoingConnection?.handleId,
      connectionHandleType: ongoingConnection?.handleType,
      connectionNodeId: ongoingConnection?.nodeId,
    });
  }, [ongoingConnection?.handleId, ongoingConnection?.handleType, ongoingConnection?.nodeId, rfStoreApi]);

  return <></>;
}

interface TopRightCornerPanelsProps {
  availableHeight?: number | undefined;
}

export function TopRightCornerPanels({ availableHeight }: TopRightCornerPanelsProps) {
  const { i18n } = useSwfEditorI18n();
  const diagram = useSwfEditorStore((s) => s.diagram);
  const swfEditorStoreApi = useSwfEditorStoreApi();
  const settings = useSettings();

  const toggleOverlaysPanel = useCallback(() => {
    swfEditorStoreApi.setState((state) => {
      state.diagram.overlaysPanel.isOpen = !state.diagram.overlaysPanel.isOpen;
    });
  }, [swfEditorStoreApi]);

  useLayoutEffect(() => {
    swfEditorStoreApi.setState((state) => {
      if (state.diagram.overlaysPanel.isOpen) {
        // This is necessary to make sure that the Popover is open at the correct position.
        setTimeout(() => {
          swfEditorStoreApi.setState((state) => {
            state.diagram.overlaysPanel.isOpen = true;
          });
        }, 300); // That's the animation duration to open/close panel.
      }
      state.diagram.overlaysPanel.isOpen = false;
    });
  }, [swfEditorStoreApi]);

  return (
    <>
      <RF.Panel position={"top-right"}>
        <Flex>
          {!settings.isReadOnly && (
            <aside className={"kie-swf-editor--autolayout-panel-toggle"}>
              <AutolayoutButton />
            </aside>
          )}
          <aside className={"kie-swf-editor--overlays-panel-toggle"}>
            <Popover
              className={"kie-swf-editor--overlay-panel-popover"}
              key={`${diagram.overlaysPanel.isOpen}`}
              aria-label="Overlays Panel"
              position={"bottom-end"}
              enableFlip={false}
              flipBehavior={["bottom-end"]}
              hideOnOutsideClick={false}
              showClose={false}
              isVisible={diagram.overlaysPanel.isOpen}
              bodyContent={<OverlaysPanel availableHeight={(availableHeight ?? 0) - AREA_ABOVE_OVERLAYS_PANEL} />}
            >
              <button
                className={"kie-swf-editor--overlays-panel-toggle-button"}
                onClick={toggleOverlaysPanel}
                title={i18n.nodes.overlays}
              >
                <VirtualMachineIcon />
              </button>
            </Popover>
          </aside>
        </Flex>
      </RF.Panel>
    </>
  );
}

export function SelectionStatus() {
  const { i18n } = useSwfEditorI18n();
  const rfStoreApi = RF.useStoreApi();

  const selectedNodesCount = useSwfEditorStore((s) => s.computed(s).getDiagramData().selectedNodesById.size);
  const selectedEdgesCount = useSwfEditorStore((s) => s.computed(s).getDiagramData().selectedEdgesById.size);
  const swfEditorStoreApi = useSwfEditorStoreApi();

  useEffect(() => {
    if (selectedNodesCount >= 2) {
      rfStoreApi.setState({ nodesSelectionActive: true });
    }
  }, [rfStoreApi, selectedNodesCount]);

  const onClose = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      swfEditorStoreApi.setState((state) => {
        state.diagram._selectedNodes = [];
        state.diagram._selectedEdges = [];
      });
    },
    [swfEditorStoreApi]
  );

  return (
    <>
      {(selectedNodesCount + selectedEdgesCount >= 2 && (
        <RF.Panel position={"top-center"}>
          <Label style={{ paddingLeft: "24px" }} onClose={onClose}>
            {(selectedEdgesCount === 0 && i18n.nodes.nodesSelected(selectedNodesCount)) ||
              (selectedNodesCount === 0 && i18n.nodes.edgesSelected(selectedEdgesCount)) ||
              `${selectedNodesCount === 1 ? i18n.nodes.oneNode : i18n.nodes.nodes(selectedNodesCount)}, 
              ${
                selectedEdgesCount === 1 ? i18n.nodes.oneEdge : i18n.nodes.edges(selectedEdgesCount)
              } ${i18n.nodes.selected}`}
          </Label>
        </RF.Panel>
      )) || <></>}
    </>
  );
}
