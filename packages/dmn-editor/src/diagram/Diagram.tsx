import * as RF from "reactflow";

import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";

import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { TenantIcon } from "@patternfly/react-icons/dist/js/icons/tenant-icon";
import { addConnectedNode } from "../mutations/addConnectedNode";
import { addEdge } from "../mutations/addEdge";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { repositionNode } from "../mutations/repositionNode";
import { resizeNode } from "../mutations/resizeNode";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { DMN_EDITOR_PALLETE_ELEMENT_MIME_TYPE, Pallete } from "./Pallete";
import { offsetShapePosition, snapShapePosition } from "./SnapGrid";
import { ConnectionLine } from "./connections/ConnectionLine";
import { TargetHandleId } from "./connections/PositionalTargetNodeHandles";
import { EdgeType, NodeType, getDefaultEdgeTypeBetween } from "./connections/graphStructure";
import { checkIsValidConnection } from "./connections/isValidConnection";
import { EdgeMarkers } from "./edges/EdgeMarkers";
import { EDGE_TYPES } from "./edges/EdgeTypes";
import {
  AssociationEdge,
  AuthorityRequirementEdge,
  InformationRequirementEdge,
  KnowledgeRequirementEdge,
} from "./edges/Edges";
import { DEFAULT_NODE_SIZES } from "./nodes/DefaultSizes";
import { NODE_TYPES } from "./nodes/NodeTypes";
import {
  BkmNode,
  DecisionNode,
  DecisionServiceNode,
  DmnDiagramNodeData,
  GroupNode,
  InputDataNode,
  KnowledgeSourceNode,
  TextAnnotationNode,
} from "./nodes/Nodes";
import { deleteNode } from "../mutations/deleteNode";
import { DC__Bounds, DMN15__tDecisionService } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { OverlaysPanel } from "../overlaysPanel/OverlaysPanel";
import { deleteEdge } from "../mutations/deleteEdge";
import { DiagramContainerContextProvider } from "./DiagramContainerContext";
import { CONTAINER_NODES_DESIRABLE_PADDING, getBounds, idFromHref } from "./maths/DmnMaths";
import { addDecisionToDecisionService } from "../mutations/addDecisionToDecisionService";
import { deleteDecisionFromDecisionService } from "../mutations/deleteDecisionFromDecisionService";
import { addNodeToGroup } from "../mutations/addNodeToGroup";
import { deleteNodeFromGroup } from "../mutations/deleteNodeFromGroup";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";

const PAN_ON_DRAG = [1, 2];

const FIT_VIEW_OPTIONS = { maxZoom: 1, minZoom: 1, duration: 400 };

const DEFAULT_VIEWPORT = { x: 100, y: 0, zoom: 1 };

export function Diagram({ container }: { container: React.RefObject<HTMLElement> }) {
  const nodeTypes: Record<NodeType, any> = useMemo(
    () => ({
      [NODE_TYPES.decisionService]: DecisionServiceNode,
      [NODE_TYPES.group]: GroupNode,
      [NODE_TYPES.inputData]: InputDataNode,
      [NODE_TYPES.decision]: DecisionNode,
      [NODE_TYPES.bkm]: BkmNode,
      [NODE_TYPES.knowledgeSource]: KnowledgeSourceNode,
      [NODE_TYPES.textAnnotation]: TextAnnotationNode,
    }),
    []
  );

  const edgeTypes: Record<EdgeType, any> = useMemo(() => {
    return {
      [EDGE_TYPES.informationRequirement]: InformationRequirementEdge,
      [EDGE_TYPES.authorityRequirement]: AuthorityRequirementEdge,
      [EDGE_TYPES.knowledgeRequirement]: KnowledgeRequirementEdge,
      [EDGE_TYPES.association]: AssociationEdge,
    };
  }, []);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const diagram = useDmnEditorStore((s) => s.diagram);

  const {
    dmnShapesByDmnRefId,
    nodesById,
    edgesById,
    nodes,
    edges,
    isDropTargetNodeValidForSelection,
    selectedNodeTypes,
  } = useDmnEditorDerivedStore();

  const [reactFlowInstance, setReactFlowInstance] = useState<RF.ReactFlowInstance | undefined>(undefined);

  const onConnect = useCallback<RF.OnConnect>(
    (connection) => {
      console.debug("DMN DIAGRAM: `onConnect`: ", connection);

      const sourceNode = nodesById.get(connection.source!);
      const targetNode = nodesById.get(connection.target!);
      if (!sourceNode || !targetNode) {
        throw new Error("Cannot create connection without target and source nodes!");
      }

      const sourceBounds = sourceNode.data.shape["dc:Bounds"];
      const targetBounds = targetNode.data.shape["dc:Bounds"];
      if (!sourceBounds || !targetBounds) {
        throw new Error("Cannot create connection without target bounds!");
      }

      // --------- This is where we draw the line between the diagram and the model.

      dmnEditorStoreApi.setState((state) => {
        addEdge({
          definitions: state.dmn.model.definitions,
          edge: { type: connection.sourceHandle as EdgeType, handle: connection.targetHandle as TargetHandleId },
          sourceNode: {
            type: sourceNode.type as NodeType,
            id: sourceNode.id,
            bounds: sourceBounds,
            shapeId: sourceNode.data.shape["@_id"],
          },
          targetNode: {
            type: targetNode.type as NodeType,
            id: targetNode.id,
            bounds: targetBounds,
            index: targetNode.data.index,
            shapeId: targetNode.data.shape["@_id"],
          },
          keepWaypointsIfSameTarget: false,
        });
      });
    },
    [dmnEditorStoreApi, nodesById]
  );

  const getFirstNodeFittingBounds = useCallback(
    (nodeIdToIgnore: string, bounds: DC__Bounds) =>
      reactFlowInstance
        ?.getNodes()
        .reverse()
        .find(
          ({ id, type, data: { shape: candidate } }) =>
            id !== nodeIdToIgnore && // don't ever use the node being dragged
            bounds["@_x"] >= (candidate["dc:Bounds"]?.["@_x"] ?? 0) &&
            bounds["@_y"] >= (candidate["dc:Bounds"]?.["@_y"] ?? 0) &&
            bounds["@_x"] + bounds["@_width"] <=
              (candidate["dc:Bounds"]?.["@_x"] ?? 0) + (candidate["dc:Bounds"]?.["@_width"] ?? 0) &&
            bounds["@_y"] + bounds["@_height"] <=
              (candidate["dc:Bounds"]?.["@_y"] ?? 0) + (candidate["dc:Bounds"]?.["@_height"] ?? 0)
        ),
    [reactFlowInstance]
  );
  const onDragOver = useCallback(
    (e: React.DragEvent) => {
      if (!e.dataTransfer.types.find((t) => t === DMN_EDITOR_PALLETE_ELEMENT_MIME_TYPE)) {
        return;
      }

      e.preventDefault();
      e.dataTransfer.dropEffect = "move";

      if (!container.current || !reactFlowInstance) {
        return;
      }

      const containerBounds = container.current!.getBoundingClientRect();
      const dropPoint = reactFlowInstance.project({
        x: e.clientX - containerBounds.left,
        y: e.clientY - containerBounds.top,
      });

      dmnEditorStoreApi.setState((state) => {
        state.diagram.dropTargetNode = getFirstNodeFittingBounds("", {
          "@_x": dropPoint.x,
          "@_y": dropPoint.y,
          "@_width": 0,
          "@_height": 0,
        }) as typeof state.diagram.dropTargetNode;
      });
    },
    [container, dmnEditorStoreApi, getFirstNodeFittingBounds, reactFlowInstance]
  );

  const onDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();

      if (!container.current || !reactFlowInstance) {
        return;
      }

      const type = e.dataTransfer.getData(DMN_EDITOR_PALLETE_ELEMENT_MIME_TYPE) as NodeType;
      if (typeof type === "undefined" || !type) {
        return;
      }

      e.stopPropagation();

      // we need to remove the wrapper bounds, in order to get the correct position
      const containerBounds = container.current.getBoundingClientRect();
      const dropPoint = reactFlowInstance.project({
        x: e.clientX - containerBounds.left,
        y: e.clientY - containerBounds.top,
      });

      // --------- This is where we draw the line between the diagram and the model.

      dmnEditorStoreApi.setState((state) => {
        const newNodeId = addStandaloneNode({
          definitions: state.dmn.model.definitions,
          newNode: {
            type,
            bounds: {
              "@_x": dropPoint.x,
              "@_y": dropPoint.y,
              "@_width": DEFAULT_NODE_SIZES[type](diagram.snapGrid)["@_width"],
              "@_height": DEFAULT_NODE_SIZES[type](diagram.snapGrid)["@_height"],
            },
          },
        });
        state.diagram.selectedNodes = [newNodeId];
      });
    },
    [container, diagram.snapGrid, dmnEditorStoreApi, reactFlowInstance]
  );

  const [connection, setConnection] = useState<RF.OnConnectStartParams | undefined>(undefined);

  const onConnectStart = useCallback<RF.OnConnectStart>((e, newConnection) => {
    console.debug("DMN DIAGRAM: `onConnectStart`");
    setConnection(newConnection);
  }, []);

  const onConnectEnd = useCallback(
    (e: MouseEvent) => {
      console.debug("DMN DIAGRAM: `onConnectEnd`");
      setConnection(undefined);

      const targetIsPane = (e.target as Element | null)?.classList?.contains("react-flow__pane");
      if (!targetIsPane || !container.current || !connection || !reactFlowInstance) {
        return;
      }

      // we need to remove the container bounds, in order to get the correct position
      const containerBounds = container.current.getBoundingClientRect();
      const dropPoint = reactFlowInstance.project({
        x: e.clientX - containerBounds.left,
        y: e.clientY - containerBounds.top,
      });

      // only try to create node if source handle is compatible
      if (!Object.values(NODE_TYPES).find((n) => n === connection.handleId)) {
        return;
      }

      if (!connection.nodeId) {
        return;
      }

      const sourceNode = nodesById.get(connection.nodeId);
      if (!sourceNode) {
        return;
      }

      const sourceNodeBounds = dmnShapesByDmnRefId.get(sourceNode.id)?.["dc:Bounds"];
      if (!sourceNodeBounds) {
        return;
      }

      const newNodeType = connection.handleId as NodeType;
      const sourceNodeType = sourceNode.type as NodeType;

      const edge = getDefaultEdgeTypeBetween(sourceNodeType as NodeType, newNodeType);
      if (!edge) {
        throw new Error(`DMN DIAGRAM: Invalid structure: ${sourceNodeType} --(any)--> ${newNodeType}`);
      }

      // --------- This is where we draw the line between the diagram and the model.

      dmnEditorStoreApi.setState((state) => {
        const newNodeId = addConnectedNode({
          definitions: state.dmn.model.definitions,
          edge,
          sourceNode: {
            id: sourceNode.id,
            type: sourceNodeType as NodeType,
            bounds: sourceNodeBounds,
            shapeId: sourceNode.data.shape["@_id"],
          },
          newNode: {
            type: newNodeType,
            bounds: {
              "@_x": dropPoint.x,
              "@_y": dropPoint.y,
              "@_width": DEFAULT_NODE_SIZES[newNodeType](diagram.snapGrid)["@_width"],
              "@_height": DEFAULT_NODE_SIZES[newNodeType](diagram.snapGrid)["@_height"],
            },
          },
        });

        state.diagram.selectedNodes = [newNodeId];
      });
    },
    [connection, container, diagram.snapGrid, dmnEditorStoreApi, nodesById, reactFlowInstance, dmnShapesByDmnRefId]
  );

  const isValidConnection = useCallback<RF.IsValidConnection>(
    (edgeOrConnection) => checkIsValidConnection(nodesById, edgeOrConnection),
    [nodesById]
  );

  const onNodesChange = useCallback<RF.OnNodesChange>(
    (changes) => {
      if (!reactFlowInstance) {
        return;
      }

      dmnEditorStoreApi.setState((state) => {
        const edgeIndexesAlreadyUpdated = new Set<number>();
        for (const change of changes) {
          switch (change.type) {
            case "add":
              console.debug(`DMN DIAGRAM: 'onNodesChange' --> add '${change.item.id}'`);
              state.dispatch.diagram.setNodeStatus(state, change.item.id, { selected: true });
              break;
            case "dimensions":
              console.debug(`DMN DIAGRAM: 'onNodesChange' --> dimensions '${change.id}'`);
              state.dispatch.diagram.setNodeStatus(state, change.id, { resizing: change.resizing });
              if (change.dimensions) {
                const node = nodesById.get(change.id)!;
                resizeNode({
                  definitions: state.dmn.model.definitions,
                  change: {
                    nodeType: node.type as NodeType,
                    shapeIndex: nodesById.get(change.id)!.data.shape.index,
                    sourceEdgeIndexes: edges.flatMap((e) =>
                      e.source === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                    ),
                    targetEdgeIndexes: edges.flatMap((e) =>
                      e.target === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                    ),
                    dimension: {
                      "@_width": change.dimensions?.width ?? 0,
                      "@_height": change.dimensions?.height ?? 0,
                    },
                  },
                });
              }
              break;
            case "position":
              console.debug(`DMN DIAGRAM: 'onNodesChange' --> position '${change.id}'`);
              state.dispatch.diagram.setNodeStatus(state, change.id, { dragging: change.dragging });
              if (change.positionAbsolute) {
                const node = nodesById.get(change.id)!;
                const { delta } = repositionNode({
                  definitions: state.dmn.model.definitions,
                  edgeIndexesAlreadyUpdated,
                  change: {
                    nodeType: node.type as NodeType,
                    selectedEdges: state.diagram.selectedEdges,
                    shapeIndex: node.data.shape.index,
                    sourceEdgeIndexes: edges.flatMap((e) =>
                      e.source === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                    ),
                    targetEdgeIndexes: edges.flatMap((e) =>
                      e.target === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                    ),
                    position: change.positionAbsolute,
                  },
                });

                // Update nested
                if (node.type === NODE_TYPES.decisionService) {
                  const decisionService = node.data.dmnObject as DMN15__tDecisionService;
                  const nested = [
                    ...(decisionService.outputDecision ?? []),
                    ...(decisionService.encapsulatedDecision ?? []),
                  ];

                  for (let i = 0; i < nested.length; i++) {
                    const nestedNode = nodesById.get(idFromHref(nested[i]["@_href"]))!;
                    const snappedNestedNodeShapeWithAppliedDelta = snapShapePosition(
                      diagram.snapGrid,
                      offsetShapePosition(nestedNode.data.shape, delta)
                    );
                    repositionNode({
                      definitions: state.dmn.model.definitions,
                      edgeIndexesAlreadyUpdated,
                      change: {
                        nodeType: nestedNode.type as NodeType,
                        selectedEdges: edges.map((e) => e.id),
                        shapeIndex: nestedNode.data.shape.index,
                        sourceEdgeIndexes: edges.flatMap((e) =>
                          e.source === nestedNode.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                        ),
                        targetEdgeIndexes: edges.flatMap((e) =>
                          e.target === nestedNode.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                        ),

                        position: snappedNestedNodeShapeWithAppliedDelta,
                      },
                    });
                  }
                }
              }
              break;
            case "remove":
              console.debug(`DMN DIAGRAM: 'onNodesChange' --> remove '${change.id}'`);
              const node = nodesById.get(change.id)!;
              if (node.parentNode && changes.find((s) => s.type === "remove" && s.id === node.parentNode)) {
                continue;
              }
              deleteNode({
                definitions: state.dmn.model.definitions,
                node: {
                  type: node.type as NodeType,
                  id: node.id,
                },
                targetEdges: edges
                  .flatMap((e) => (e.target === node.id ? [{ id: e.id, data: e.data! }] : []))
                  .reverse(),
              });
              state.dispatch.diagram.setNodeStatus(state, node.id, {
                selected: false,
                dragging: false,
                resizing: false,
              });
              break;
            case "reset":
              state.dispatch.diagram.setNodeStatus(state, change.item.id, {
                selected: false,
                dragging: false,
                resizing: false,
              });
              break;
            case "select":
              state.dispatch.diagram.setNodeStatus(state, change.id, { selected: change.selected });
              break;
          }
        }
      });
    },
    [reactFlowInstance, dmnEditorStoreApi, nodesById, edges, diagram.snapGrid]
  );

  const nodeBeingDraggedRef = useRef<RF.Node | null>(null);
  const onNodeDrag = useCallback<RF.NodeDragHandler>(
    (e, node: RF.Node<DmnDiagramNodeData<any>>) => {
      nodeBeingDraggedRef.current = node;

      dmnEditorStoreApi.setState((state) => {
        state.diagram.dropTargetNode = getFirstNodeFittingBounds(node.id, {
          // We can't use node.data.dmnObject because it hasn't been updated at this point yet.
          "@_x": node.positionAbsolute?.x ?? 0,
          "@_y": node.positionAbsolute?.y ?? 0,
          "@_width": node.width ?? 0,
          "@_height": node.height ?? 0,
        }) as typeof state.diagram.dropTargetNode;
      });
    },
    [dmnEditorStoreApi, getFirstNodeFittingBounds]
  );

  const onNodeDragStop = useCallback<RF.NodeDragHandler>(
    (e, node) => {
      console.debug("DMN DIAGRAM: `onNodeDragStop`");
      const nodeBeingDragged = nodeBeingDraggedRef.current!;
      nodeBeingDraggedRef.current = null;
      if (!nodeBeingDragged) {
        return;
      }

      dmnEditorStoreApi.setState((state) => {
        const dropTargetNode = state.diagram.dropTargetNode;
        state.diagram.dropTargetNode = undefined;

        // Un-parent
        if (nodeBeingDragged.parentNode) {
          const parentNode = nodesById.get(nodeBeingDragged.parentNode);
          if (parentNode?.type === NODE_TYPES.decisionService && nodeBeingDragged.type === NODE_TYPES.decision) {
            for (let i = 0; i < state.diagram.selectedNodes.length; i++) {
              deleteDecisionFromDecisionService({
                definitions: state.dmn.model.definitions,
                decisionId: state.diagram.selectedNodes[i],
                decisionServiceId: parentNode.id,
              });
            }
          } else if (parentNode?.type === NODE_TYPES.group) {
            for (let i = 0; i < state.diagram.selectedNodes.length; i++) {
              deleteNodeFromGroup({
                definitions: state.dmn.model.definitions,
                nodeId: state.diagram.selectedNodes[i],
              });
            }
          } else {
            console.debug(
              `DMN DIAGRAM: Ignoring '${nodeBeingDragged.type}' with parent '${dropTargetNode?.type}' dropping somewhere..`
            );
          }
        }

        // Validate
        if (!isDropTargetNodeValidForSelection) {
          console.debug(
            `DMN DIAGRAM: Invalid containment: '${[...selectedNodeTypes].join("', '")}' inside '${
              dropTargetNode?.type
            }'. Ignoring nodes dropped.`
          );
          return;
        }

        // Parent
        if (dropTargetNode?.type === NODE_TYPES.decisionService) {
          for (let i = 0; i < state.diagram.selectedNodes.length; i++) {
            addDecisionToDecisionService({
              definitions: state.dmn.model.definitions,
              decisionId: state.diagram.selectedNodes[i], // We can assume that all selected nodes are Decisions because the contaiment was validated above.
              decisionServiceId: dropTargetNode.id,
            });
          }
        } else if (dropTargetNode?.type === NODE_TYPES.group) {
          for (let i = 0; i < state.diagram.selectedNodes.length; i++) {
            addNodeToGroup({
              definitions: state.dmn.model.definitions,
              nodeId: state.diagram.selectedNodes[i],
            });
          }
        } else {
          console.debug(`DMN DIAGRAM: Ignoring '${nodeBeingDragged.type}' dropped on top of '${dropTargetNode?.type}'`);
        }
      });
    },
    [dmnEditorStoreApi, isDropTargetNodeValidForSelection, nodesById, selectedNodeTypes]
  );

  const onEdgesChange = useCallback<RF.OnEdgesChange>(
    (changes) => {
      dmnEditorStoreApi.setState((state) => {
        for (const change of changes) {
          switch (change.type) {
            case "select":
              console.debug(`DMN DIAGRAM: 'onEdgesChange' --> select '${change.id}'`);
              state.dispatch.diagram.setEdgeStatus(state, change.id, { selected: change.selected });
              break;
            case "remove":
              console.debug(`DMN DIAGRAM: 'onEdgesChange' --> remove '${change.id}'`);
              const edge = edgesById.get(change.id);
              if (edge?.data) {
                deleteEdge({
                  definitions: state.dmn.model.definitions,
                  edge: { id: change.id, dmnObject: edge.data.dmnObject },
                });
                state.dispatch.diagram.setEdgeStatus(state, change.id, { selected: false, draggingWaypoint: false });
              }
              break;
            case "add":
            case "reset":
              console.debug(`DMN DIAGRAM: 'onEdgesChange' --> add/reset '${change.item.id}'. Ignoring`);
          }
        }
      });
    },
    [dmnEditorStoreApi, edgesById]
  );

  const rfSnapGrid = useMemo<[number, number]>(
    () => (diagram.snapGrid.isEnabled ? [diagram.snapGrid.x, diagram.snapGrid.y] : [1, 1]),
    [diagram.snapGrid.isEnabled, diagram.snapGrid.x, diagram.snapGrid.y]
  );

  const onEdgesUpdate = useCallback<RF.OnEdgeUpdateFunc>(
    (oldEdge, newConnection) => {
      console.debug("DMN DIAGRAM: `onEdgesUpdate`", oldEdge, newConnection);

      const sourceNode = nodesById.get(newConnection.source!);
      const targetNode = nodesById.get(newConnection.target!);
      if (!sourceNode || !targetNode) {
        throw new Error("Cannot create connection without target and source nodes!");
      }

      const sourceBounds = sourceNode.data.shape["dc:Bounds"];
      const targetBounds = targetNode.data.shape["dc:Bounds"];
      if (!sourceBounds || !targetBounds) {
        throw new Error("Cannot create connection without target bounds!");
      }

      // --------- This is where we draw the line between the diagram and the model.

      dmnEditorStoreApi.setState((state) => {
        const { newDmnEdge } = addEdge({
          definitions: state.dmn.model.definitions,
          edge: { type: newConnection.sourceHandle as EdgeType, handle: newConnection.targetHandle as TargetHandleId },
          sourceNode: {
            type: sourceNode.type as NodeType,
            id: sourceNode.id,
            bounds: sourceBounds,
            shapeId: sourceNode.data.shape["@_id"],
          },
          targetNode: {
            type: targetNode.type as NodeType,
            id: targetNode.id,
            bounds: targetBounds,
            index: targetNode.data.index,
            shapeId: targetNode.data.shape["@_id"],
          },
          keepWaypointsIfSameTarget: true,
        });

        // The DMN Edge changed nodes, so we need to delete the old one, but keep the waypoints!
        // FIXME: What about other DMNEdge properties? Style etc. Should we keep those too?
        if (newDmnEdge["@_dmnElementRef"] !== oldEdge.id) {
          const { dmnEdge: deletedDmnEdge } = deleteEdge({
            definitions: state.dmn.model.definitions,
            edge: { id: oldEdge.id, dmnObject: oldEdge.data!.dmnObject },
          });

          const deletedWaypoints = deletedDmnEdge?.["di:waypoint"];

          if (oldEdge.source !== newConnection.source && deletedWaypoints) {
            newDmnEdge["di:waypoint"] = [newDmnEdge["di:waypoint"]![0], ...deletedWaypoints.slice(1)];
          }

          if (oldEdge.target !== newConnection.target && deletedWaypoints) {
            newDmnEdge["di:waypoint"] = [
              ...deletedWaypoints.slice(0, deletedWaypoints.length - 1),
              newDmnEdge["di:waypoint"]![newDmnEdge["di:waypoint"]!.length - 1],
            ];
          }
        }

        // Keep the updated edge selected
        state.diagram.selectedEdges = [newDmnEdge["@_dmnElementRef"]!];
      });
    },
    [dmnEditorStoreApi, nodesById]
  );

  const onEdgeUpdateStart = useCallback(
    (e: React.MouseEvent | React.TouchEvent, edge: RF.Edge, handleType: RF.HandleType) => {
      console.debug("DMN DIAGRAM: `onEdgeUpdateStart`");
      dmnEditorStoreApi.setState((state) => {
        state.diagram.edgeIdBeingUpdated = edge.id;
      });
    },
    [dmnEditorStoreApi]
  );

  const onEdgeUpdateEnd = useCallback(
    (e: MouseEvent | TouchEvent, edge: RF.Edge, handleType: RF.HandleType) => {
      console.debug("DMN DIAGRAM: `onEdgeUpdateEnd`");
      setConnection(undefined);
      dmnEditorStoreApi.setState((state) => {
        state.diagram.edgeIdBeingUpdated = undefined;
      });
    },
    [dmnEditorStoreApi]
  );

  return (
    <>
      <DiagramContainerContextProvider container={container}>
        <EdgeMarkers />
        <RF.ReactFlow
          nodes={nodes}
          edges={edges}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          onEdgeUpdateStart={onEdgeUpdateStart}
          onEdgeUpdateEnd={onEdgeUpdateEnd}
          onEdgeUpdate={onEdgesUpdate}
          onlyRenderVisibleElements={true}
          zoomOnDoubleClick={false}
          elementsSelectable={true}
          panOnScroll={true}
          selectionOnDrag={true}
          panOnDrag={PAN_ON_DRAG}
          panActivationKeyCode={"Alt"}
          selectionMode={RF.SelectionMode.Full} // For selections happening inside Groups/DecisionServices it's better to leave it as "Full"
          isValidConnection={isValidConnection}
          connectionLineComponent={ConnectionLine}
          onConnect={onConnect}
          onConnectStart={onConnectStart}
          onConnectEnd={onConnectEnd}
          // (begin)
          // 'Starting to drag' and 'dragging' should have the same behavior. Otherwise,
          // clicking a node and letting it go, without moving, won't work properly, and
          // Decisions will be removed from Decision Services.
          onNodeDragStart={onNodeDrag}
          onNodeDrag={onNodeDrag}
          // (end)
          onNodeDragStop={onNodeDragStop}
          nodeTypes={nodeTypes}
          edgeTypes={edgeTypes}
          snapToGrid={true}
          snapGrid={rfSnapGrid}
          defaultViewport={DEFAULT_VIEWPORT}
          fitView={false}
          fitViewOptions={FIT_VIEW_OPTIONS}
          attributionPosition={"bottom-right"}
          onInit={setReactFlowInstance}
          // (begin)
          // Used to make the Pallete work by dropping nodes on the Reactflow Canvas
          onDrop={onDrop}
          onDragOver={onDragOver}
          // (end)
        >
          <SelectionStatus />
          <Pallete />
          <TopRightCornerPanels />
          <PanWhenAltPressed />
          <KeyboardShortcuts />
          {/** FIXME: Tiago --> The background is making the Diagram VERY slow on Firefox. Render this conditionally. */}
          <RF.Background />
          <RF.Controls fitViewOptions={FIT_VIEW_OPTIONS} position={"bottom-right"} />
          <SetConnectionToReactFlowStore connection={connection} />
        </RF.ReactFlow>
      </DiagramContainerContextProvider>
    </>
  );
}

export function SetConnectionToReactFlowStore({ connection }: { connection: RF.OnConnectStartParams | undefined }) {
  const rfStoreApi = RF.useStoreApi();
  useEffect(() => {
    rfStoreApi.setState({
      connectionHandleId: connection?.handleId,
      connectionHandleType: connection?.handleType,
      connectionNodeId: connection?.nodeId,
    });
  }, [connection?.handleId, connection?.handleType, connection?.nodeId, rfStoreApi]);

  return <></>;
}

export function TopRightCornerPanels() {
  const dispatch = useDmnEditorStore((s) => s.dispatch);
  const diagram = useDmnEditorStore((s) => s.diagram);
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const toggleOverlaysPanel = useCallback(() => {
    dmnEditorStoreApi.setState((state) => dispatch.diagram.toggleOverlaysPanel(state));
  }, [dispatch.diagram, dmnEditorStoreApi]);

  useLayoutEffect(() => {
    dmnEditorStoreApi.setState((state) => {
      if (state.diagram.overlaysPanel.isOpen) {
        // This is necessary to make sure that the Popover is open at the correct position.
        setTimeout(() => {
          dmnEditorStoreApi.setState((state) => {
            state.diagram.overlaysPanel.isOpen = true;
          });
        }, 300); // That's the animation duration to open/close the properties panel.
      }
      state.diagram.overlaysPanel.isOpen = false;
    });
  }, [dmnEditorStoreApi, diagram.propertiesPanel.isOpen]);

  return (
    <>
      <RF.Panel position={"top-right"} style={{ display: "flex" }}>
        <aside className={"kie-dmn-editor--overlays-panel-toggle"}>
          <Popover
            key={`${diagram.propertiesPanel.isOpen}`}
            aria-label="Advanced popover usages example"
            position={"left-start"}
            hideOnOutsideClick={false}
            isVisible={diagram.overlaysPanel.isOpen}
            enableFlip={true}
            headerContent={<div>Overlays</div>}
            bodyContent={<OverlaysPanel />}
          >
            <button className={"kie-dmn-editor--overlays-panel-toggle-button"} onClick={toggleOverlaysPanel}>
              <TenantIcon size={"sm"} />
            </button>
          </Popover>
        </aside>
        {!diagram.propertiesPanel.isOpen && (
          <aside className={"kie-dmn-editor--properties-panel-toggle"}>
            <button
              className={"kie-dmn-editor--properties-panel-toggle-button"}
              onClick={dispatch.diagram.propertiesPanel.toggle}
            >
              <InfoIcon size={"sm"} />
            </button>
          </aside>
        )}
      </RF.Panel>
    </>
  );
}

export function SelectionStatus() {
  const rfStoreApi = RF.useStoreApi();

  const diagram = useDmnEditorStore((s) => s.diagram);
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  useEffect(() => {
    if (diagram.selectedNodes.length >= 2) {
      rfStoreApi.setState({ nodesSelectionActive: true });
    }
  }, [rfStoreApi, diagram.selectedNodes.length]);

  const onClose = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      dmnEditorStoreApi.setState((state) => {
        state.diagram.selectedNodes = [];
        state.diagram.selectedEdges = [];
      });
    },
    [dmnEditorStoreApi]
  );

  return (
    <>
      {(diagram.selectedNodes.length + diagram.selectedEdges.length >= 2 && (
        <RF.Panel position={"top-center"}>
          <Label style={{ paddingLeft: "24px" }} onClose={onClose}>
            {(diagram.selectedEdges.length === 0 && `${diagram.selectedNodes.length} nodes selected`) ||
              (diagram.selectedNodes.length === 0 && `${diagram.selectedEdges.length} edges selected`) ||
              `${diagram.selectedNodes.length} node${diagram.selectedNodes.length === 1 ? "" : "s"}, ${
                diagram.selectedEdges.length
              } edge${diagram.selectedEdges.length === 1 ? "" : "s"} selected`}
          </Label>
        </RF.Panel>
      )) || <></>}
    </>
  );
}

export function KeyboardShortcuts() {
  const rfStoreApi = RF.useStoreApi();
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const diagram = useDmnEditorStore((s) => s.diagram);
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const esc = RF.useKeyPress(["Escape"]);
  useEffect(() => {
    if (!esc) {
      return;
    }

    rfStoreApi.setState((prev) => {
      if (isConnecting) {
        prev.cancelConnection();
      } else {
        if (diagram.selectedNodes.length > 0) {
          prev.resetSelectedElements();
        }
        (document.activeElement as any)?.blur?.();
      }

      return prev;
    });
  }, [diagram.selectedNodes.length, esc, isConnecting, rfStoreApi]);

  const selectAll = RF.useKeyPress(["a", "Meta+a"]);
  useEffect(() => {
    if (!selectAll) {
      return;
    }

    rfStoreApi.setState((prev) => {
      const unselected = prev.getNodes().flatMap((n) => (!n.selected ? [n.id] : []));
      if (unselected.length > 0) {
        prev.addSelectedNodes(prev.getNodes().map((s) => s.id));
      } else {
        prev.resetSelectedElements();
      }

      return prev;
    });
  }, [rfStoreApi, selectAll]);

  const g = RF.useKeyPress(["g"]);
  useEffect(() => {
    if (!g) {
      return;
    }

    dmnEditorStoreApi.setState((state) => {
      if (state.diagram.selectedNodes.length <= 0) {
        return;
      }

      const newNodeId = addStandaloneNode({
        definitions: state.dmn.model.definitions,
        newNode: {
          type: NODE_TYPES.group,
          bounds: getBounds({
            nodes: rfStoreApi.getState().getNodes(),
            padding: CONTAINER_NODES_DESIRABLE_PADDING,
          }),
        },
      });

      state.dispatch.diagram.setNodeStatus(state, newNodeId, { selected: true });
    });
  }, [dmnEditorStoreApi, g, rfStoreApi]);

  return <></>;
}

export function PanWhenAltPressed() {
  const altPressed = RF.useKeyPress("Alt");
  const rfStoreApi = RF.useStoreApi();

  useEffect(() => {
    rfStoreApi.setState({
      nodesDraggable: !altPressed,
      nodesConnectable: !altPressed,
      elementsSelectable: !altPressed,
    });
  }, [altPressed, rfStoreApi]);

  return <></>;
}
