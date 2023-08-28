import * as RF from "reactflow";

import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useMemo, useState } from "react";

import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { TenantIcon } from "@patternfly/react-icons/dist/js/icons/tenant-icon";
import { addConnectedNode } from "../mutations/addConnectedNode";
import { addEdge } from "../mutations/addEdge";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { repositionNode } from "../mutations/repositionNode";
import { resizeNode } from "../mutations/resizeNode";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { PALLETE_ELEMENT_MIME_TYPE, Pallete } from "./Pallete";
import { offsetShapePosition, snapShapePosition } from "./SnapGrid";
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
import { idFromHref, useDmnDiagramData } from "./useDmnDiagramData";
import { deleteNode } from "../mutations/deleteNode";
import { DMN15__tDecisionService } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { OverlaysPanel } from "../overlaysPanel/OverlaysPanel";

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
  const { diagram } = useDmnEditorStore();

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
              "@_width": DEFAULT_NODE_SIZES[newNodeType](diagram.snapGrid)["@_width"],
              "@_height": DEFAULT_NODE_SIZES[newNodeType](diagram.snapGrid)["@_height"],
            },
          },
        });

        state.diagram.selectedNodes = [newNodeId];
      });
    },
    [connection, container, diagram.snapGrid, dmnEditorStoreApi, nodesById, reactFlowInstance, shapesById]
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
              state.dispatch.diagram.setNodeStatus(state, change.id, { dragging: change.dragging });
              if (change.positionAbsolute) {
                const node = nodesById.get(change.id)!;
                const { delta } = repositionNode({
                  definitions: state.dmn.model.definitions,
                  change: {
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
                      change: {
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
              const node = nodesById.get(change.id)!;
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
    [dmnEditorStoreApi, edges, nodesById, reactFlowInstance, diagram.snapGrid]
  );

  const onEdgesChange = useCallback<RF.OnEdgesChange>(
    (changes) => {
      dmnEditorStoreApi.setState((state) => {
        for (const change of changes) {
          switch (change.type) {
            case "select":
              state.dispatch.diagram.setEdgeStatus(state, change.id, { selected: change.selected });
              break;
            case "add":
            case "remove":
            case "reset":
              console.log("CHANGED EDGE -->", change);
          }
        }
      });
    },
    [dmnEditorStoreApi]
  );

  const rfSnapGrid = useMemo<[number, number]>(
    () => (diagram.snapGrid.isEnabled ? [diagram.snapGrid.x, diagram.snapGrid.y] : [1, 1]),
    [diagram.snapGrid.isEnabled, diagram.snapGrid.x, diagram.snapGrid.y]
  );

  return (
    <>
      <EdgeMarkers />
      <RF.ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onlyRenderVisibleElements={true}
        zoomOnDoubleClick={false}
        elementsSelectable={true}
        panOnScroll={true}
        selectionOnDrag={true}
        panOnDrag={PAN_ON_DRAG}
        panActivationKeyCode={"Alt"}
        selectionMode={RF.SelectionMode.Partial}
        connectionLineComponent={ConnectionLine}
        onConnect={onConnect}
        onConnectStart={onConnectStart}
        onConnectEnd={onConnectEnd}
        isValidConnection={isValidConnection}
        nodeTypes={nodeTypes}
        edgeTypes={edgeTypes}
        snapToGrid={true}
        snapGrid={rfSnapGrid}
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
        <TopRightCornerPanels />
        <PanWhenAltPressed />
        <KeyboardShortcuts />
        <RF.Background />{" "}
        {/** FIXME: Tiago --> This is making the Diagram VERY slow on Firefox. Render this conditionally. */}
        <RF.Controls fitViewOptions={FIT_VIEW_OPTIONS} position={"bottom-right"} />
      </RF.ReactFlow>
    </>
  );
}

export function TopRightCornerPanels() {
  const { propertiesPanel, dispatch, diagram } = useDmnEditorStore();
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
  }, [dmnEditorStoreApi, propertiesPanel.isOpen]);

  return (
    <>
      <RF.Panel position={"top-right"} style={{ display: "flex" }}>
        <aside className={"kie-dmn-editor--overlays-panel-toggle"}>
          <Popover
            key={`${propertiesPanel.isOpen}`}
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
        {!propertiesPanel.isOpen && (
          <aside className={"kie-dmn-editor--properties-panel-toggle"}>
            <button
              className={"kie-dmn-editor--properties-panel-toggle-button"}
              onClick={dispatch.propertiesPanel.toggle}
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

  const resetSelectedElements = RF.useStore((state) => state.resetSelectedElements);
  const { diagram } = useDmnEditorStore();

  useEffect(() => {
    if (diagram.selectedNodes.length >= 2) {
      rfStoreApi.setState({ nodesSelectionActive: true });
    }
  }, [rfStoreApi, diagram.selectedNodes.length]);

  const onClose = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      resetSelectedElements();
    },
    [resetSelectedElements]
  );
  return (
    <>
      {(diagram.selectedNodes.length >= 2 && (
        <RF.Panel position={"top-center"}>
          <Label
            style={{ paddingLeft: "24px" }}
            onClose={onClose}
          >{`${diagram.selectedNodes.length} nodes selected`}</Label>
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
