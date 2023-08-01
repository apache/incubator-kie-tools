import * as RF from "reactflow";

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";

import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { addConnectedNode } from "../mutations/addConnectedNode";
import { addEdge } from "../mutations/addEdge";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { repositionNode } from "../mutations/repositionNode";
import { resizeNode } from "../mutations/resizeNode";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { PALLETE_ELEMENT_MIME_TYPE, Pallete } from "./Pallete";
import { SNAP_GRID } from "./SnapGrid";
import { ConnectionLine } from "./connections/ConnectionLine";
import { TargetHandleId } from "./connections/NodeHandles";
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
  GroupNode,
  InputDataNode,
  KnowledgeSourceNode,
  TextAnnotationNode,
} from "./nodes/Nodes";
import { useDmnDiagramData } from "./useDmnDiagramData";
import { deleteNode } from "../mutations/deleteNode";

const PAN_ON_DRAG = [1, 2];

const FIT_VIEW_OPTIONS = { maxZoom: 1, minZoom: 1, duration: 400 };

const DEFAULT_VIEWPORT = { x: 100, y: 0, zoom: 1 };

export function Diagram({ container }: { container: React.RefObject<HTMLElement> }) {
  const snapGrid = useMemo<[number, number]>(() => [SNAP_GRID.x, SNAP_GRID.y], []);

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

  const { shapesById, nodesById, nodes, edges } = useDmnDiagramData();

  const [reactFlowInstance, setReactFlowInstance] = useState<RF.ReactFlowInstance | undefined>(undefined);

  const onConnect: RF.OnConnect = useCallback(
    (args) => {
      const sourceNode = nodesById.get(args.source!);
      const targetNode = nodesById.get(args.target!);
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
          edge: { type: args.sourceHandle as EdgeType, handle: args.targetHandle as TargetHandleId },
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
        });
      });
    },
    [dmnEditorStoreApi, nodesById]
  );

  const onDragOver = useCallback((e: React.DragEvent) => {
    if (!e.dataTransfer.types.find((t) => t === PALLETE_ELEMENT_MIME_TYPE)) {
      return;
    }

    e.preventDefault();
    e.dataTransfer.dropEffect = "move";
  }, []);

  const onDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();

      if (!container.current || !reactFlowInstance) {
        return;
      }

      const type = e.dataTransfer.getData(PALLETE_ELEMENT_MIME_TYPE) as NodeType;
      if (typeof type === "undefined" || !type) {
        return;
      }

      e.stopPropagation();

      // we need to remove the wrapper bounds, in order to get the correct position
      const rfBounds = container.current.getBoundingClientRect();
      const dropPoint = {
        x: e.clientX - rfBounds.left - reactFlowInstance.getViewport().x,
        y: e.clientY - rfBounds.top - reactFlowInstance.getViewport().y,
      };

      // --------- This is where we draw the line between the diagram and the model.

      dmnEditorStoreApi.setState((state) => {
        const newNodeId = addStandaloneNode({
          definitions: state.dmn.model.definitions,
          newNode: {
            type,
            bounds: {
              "@_x": dropPoint.x,
              "@_y": dropPoint.y,
              "@_width": DEFAULT_NODE_SIZES[type]["@_width"],
              "@_height": DEFAULT_NODE_SIZES[type]["@_height"],
            },
          },
        });
        state.diagram.selected = [newNodeId];
      });
    },
    [container, dmnEditorStoreApi, reactFlowInstance]
  );

  const [connection, setConnection] = useState<RF.OnConnectStartParams | undefined>(undefined);
  const onConnectStart = useCallback<RF.OnConnectStart>((a, b) => setConnection(b), []);
  const onConnectEnd = useCallback(
    (e: MouseEvent) => {
      const targetIsPane = (e.target as Element | null)?.classList?.contains("react-flow__pane");
      if (!targetIsPane || !container.current || !connection || !reactFlowInstance) {
        return;
      }

      // we need to remove the wrapper bounds, in order to get the correct position
      const rfBounds = container.current.getBoundingClientRect();
      const dropPoint = {
        x: e.clientX - rfBounds.left - reactFlowInstance.getViewport().x,
        y: e.clientY - rfBounds.top - reactFlowInstance.getViewport().y,
      };

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

      const sourceNodeBounds = shapesById.get(sourceNode.id)?.["dc:Bounds"];
      if (!sourceNodeBounds) {
        return;
      }

      const newNodeType = connection.handleId as NodeType;
      const sourceNodeType = sourceNode.type as NodeType;

      const edge = getDefaultEdgeTypeBetween(sourceNodeType as NodeType, newNodeType);
      if (!edge) {
        throw new Error(`Invalid structure: ${sourceNodeType} --(any)--> ${newNodeType}`);
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
              "@_width": DEFAULT_NODE_SIZES[newNodeType]["@_width"],
              "@_height": DEFAULT_NODE_SIZES[newNodeType]["@_height"],
            },
          },
        });

        state.diagram.selected = [newNodeId];
      });
    },
    [connection, container, dmnEditorStoreApi, nodesById, reactFlowInstance, shapesById]
  );

  const isValidConnection = useCallback<RF.IsValidConnection>(
    (edge) => checkIsValidConnection(nodesById, edge),
    [nodesById]
  );

  const onNodesChange = useCallback<RF.OnNodesChange>(
    (changes) => {
      if (!reactFlowInstance) {
        return;
      }

      dmnEditorStoreApi.setState((state) => {
        for (const change of changes) {
          switch (change.type) {
            case "add":
              state.dispatch.diagram.setNodeStatus(state, change.item.id, { selected: true });
              break;
            case "dimensions":
              state.dispatch.diagram.setNodeStatus(state, change.id, { resizing: change.resizing });
              if (change.dimensions) {
                resizeNode({
                  definitions: state.dmn.model.definitions,
                  change: {
                    shapeIndex: nodesById.get(change.id)!.data.shape.index,
                    sourceEdgeIndexes: edges
                      .flatMap((e) => (e.source === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []))
                      .reverse(),
                    targetEdgeIndexes: edges
                      .flatMap((e) => (e.target === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []))
                      .reverse(),
                    dimension: {
                      "@_width": change.dimensions?.width ?? 0,
                      "@_height": change.dimensions?.height ?? 0,
                    },
                  },
                });
              }
              break;
            case "position":
              state.dispatch.diagram.setNodeStatus(state, change.id, { dragging: change.dragging });
              if (change.positionAbsolute) {
                repositionNode({
                  definitions: state.dmn.model.definitions,
                  change: {
                    shapeIndex: nodesById.get(change.id)!.data.shape.index,
                    sourceEdgeIndexes: edges
                      .flatMap((e) => (e.source === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []))
                      .reverse(),
                    targetEdgeIndexes: edges
                      .flatMap((e) => (e.target === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []))
                      .reverse(),
                    position: {
                      "@_x": change.positionAbsolute.x,
                      "@_y": change.positionAbsolute.y,
                    },
                  },
                });
              }
              break;
            case "remove":
              const node = nodesById.get(change.id)!;
              deleteNode({
                definitions: state.dmn.model.definitions,
                node: {
                  type: node.type as NodeType,
                  id: node.id,
                  index: node.data.index,
                  shapeIndex: node.data.shape.index,
                },
                sourceEdgeIndexes: edges.flatMap((e) => (e.source === node.id && e.data?.dmnEdge ? [e] : [])).reverse(),
                targetEdgeIndexes: edges.flatMap((e) => (e.target === node.id && e.data?.dmnEdge ? [e] : [])).reverse(),
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
    [dmnEditorStoreApi, edges, nodesById, reactFlowInstance]
  );

  const onEdgeUpdate: RF.OnEdgeUpdateFunc = useCallback((args) => {
    //
  }, []);

  const onEdgesChange = useCallback<RF.OnEdgesChange>(() => {
    //
  }, []);

  return (
    <>
      <EdgeMarkers />
      <RF.ReactFlow
        onlyRenderVisibleElements={true}
        zoomOnDoubleClick={false}
        elementsSelectable={true}
        nodes={nodes}
        edges={edges}
        panOnScroll={true}
        selectionOnDrag={true}
        panOnDrag={PAN_ON_DRAG}
        panActivationKeyCode={"Alt"}
        selectionMode={RF.SelectionMode.Partial}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        edgesUpdatable={true}
        connectionLineComponent={ConnectionLine}
        onEdgeUpdate={onEdgeUpdate}
        onConnect={onConnect}
        onConnectStart={onConnectStart}
        onConnectEnd={onConnectEnd}
        isValidConnection={isValidConnection}
        nodeTypes={nodeTypes}
        edgeTypes={edgeTypes}
        snapToGrid={true}
        snapGrid={snapGrid}
        defaultViewport={DEFAULT_VIEWPORT}
        fitView={false}
        fitViewOptions={FIT_VIEW_OPTIONS}
        attributionPosition={"bottom-right"}
        onInit={setReactFlowInstance}
        onDrop={onDrop}
        onDragOver={onDragOver}
      >
        <SelectionStatus />
        <Pallete />
        <PropertiesPanelToggle />
        <PanWhenAltPressed />
        <KeyboardShortcuts />
        <RF.Background />
        <RF.Controls fitViewOptions={FIT_VIEW_OPTIONS} position={"bottom-right"} />
      </RF.ReactFlow>
    </>
  );
}

export function PropertiesPanelToggle() {
  const { propertiesPanel, dispatch } = useDmnEditorStore();
  return (
    <>
      {(!propertiesPanel.isOpen && (
        <RF.Panel position={"top-right"}>
          <aside className={"kie-dmn-editor--properties-panel-toggle"}>
            <button
              className={"kie-dmn-editor--properties-panel-toggle-button"}
              onClick={dispatch.propertiesPanel.toggle}
            >
              <InfoIcon size={"sm"} />
            </button>
          </aside>
        </RF.Panel>
      )) || <></>}
    </>
  );
}

export function SelectionStatus() {
  const rfStoreApi = RF.useStoreApi();

  const resetSelectedElements = RF.useStore((state) => state.resetSelectedElements);
  const { diagram } = useDmnEditorStore();

  useEffect(() => {
    if (diagram.selected.length >= 2) {
      rfStoreApi.setState({ nodesSelectionActive: true });
    }
  }, [rfStoreApi, diagram.selected.length]);

  const onClose = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      resetSelectedElements();
    },
    [resetSelectedElements]
  );
  return (
    <>
      {(diagram.selected.length >= 2 && (
        <RF.Panel position={"top-center"}>
          <Label style={{ paddingLeft: "24px" }} onClose={onClose}>{`${diagram.selected.length} nodes selected`}</Label>
        </RF.Panel>
      )) || <></>}
    </>
  );
}

export function KeyboardShortcuts() {
  const rfStoreApi = RF.useStoreApi();
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const { diagram } = useDmnEditorStore();

  const esc = RF.useKeyPress(["Escape"]);
  useEffect(() => {
    if (!esc) {
      return;
    }

    rfStoreApi.setState((prev) => {
      if (isConnecting) {
        prev.cancelConnection();
      } else {
        if (diagram.selected.length > 0) {
          prev.resetSelectedElements();
        }
        (document.activeElement as any)?.blur?.();
      }

      return prev;
    });
  }, [diagram.selected.length, esc, isConnecting, rfStoreApi]);

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
