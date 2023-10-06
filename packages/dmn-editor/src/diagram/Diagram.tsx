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
import { MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE, Palette } from "./Palette";
import { offsetShapePosition, snapShapeDimensions, snapShapePosition } from "./SnapGrid";
import { ConnectionLine } from "./connections/ConnectionLine";
import { TargetHandleId } from "./connections/PositionalTargetNodeHandles";
import { EdgeType, NodeType, containment, getDefaultEdgeTypeBetween } from "./connections/graphStructure";
import { checkIsValidConnection } from "./connections/isValidConnection";
import { EdgeMarkers } from "./edges/EdgeMarkers";
import { EDGE_TYPES } from "./edges/EdgeTypes";
import {
  AssociationEdge,
  AuthorityRequirementEdge,
  DmnDiagramEdgeData,
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
import {
  CONTAINER_NODES_DESIRABLE_PADDING,
  getBounds,
  getContainmentRelationship,
  getNodeTypeFromDmnObject,
} from "./maths/DmnMaths";
import { addDecisionToDecisionService } from "../mutations/addDecisionToDecisionService";
import { deleteDecisionFromDecisionService } from "../mutations/deleteDecisionFromDecisionService";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { useDmnEditor } from "../DmnEditorContext";
import {
  ExternalNode,
  MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS,
} from "../externalNodes/ExternalNodesPanel";
import { addShape } from "../mutations/addShape";
import { buildXmlQName } from "../xml/xmlQNames";
import { original } from "immer";
import { getXmlNamespaceDeclarationName } from "../xml/xmlNamespaceDeclarations";
import { buildXmlHref } from "../xml/xmlHrefs";
import { VirtualMachineIcon } from "@patternfly/react-icons/dist/js/icons/virtual-machine-icon";

const PAN_ON_DRAG = [1, 2];

const FIT_VIEW_OPTIONS: RF.FitViewOptions = { maxZoom: 1, minZoom: 0.1, duration: 400 };

const DEFAULT_VIEWPORT = { x: 100, y: 0, zoom: 1 };

const nodeTypes: Record<NodeType, any> = {
  [NODE_TYPES.decisionService]: DecisionServiceNode,
  [NODE_TYPES.group]: GroupNode,
  [NODE_TYPES.inputData]: InputDataNode,
  [NODE_TYPES.decision]: DecisionNode,
  [NODE_TYPES.bkm]: BkmNode,
  [NODE_TYPES.knowledgeSource]: KnowledgeSourceNode,
  [NODE_TYPES.textAnnotation]: TextAnnotationNode,
};

const edgeTypes: Record<EdgeType, any> = {
  [EDGE_TYPES.informationRequirement]: InformationRequirementEdge,
  [EDGE_TYPES.authorityRequirement]: AuthorityRequirementEdge,
  [EDGE_TYPES.knowledgeRequirement]: KnowledgeRequirementEdge,
  [EDGE_TYPES.association]: AssociationEdge,
};

export function Diagram({ container }: { container: React.RefObject<HTMLElement> }) {
  // Contexts

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const diagram = useDmnEditorStore((s) => s.diagram);
  const thisDmn = useDmnEditorStore((s) => s.dmn);

  const { dmnModelBeforeEditingRef } = useDmnEditor();

  const {
    dmnShapesByHref,
    nodesById,
    edgesById,
    nodes,
    edges,
    isDropTargetNodeValidForSelection,
    isDiagramEditingInProgress,
    selectedNodeTypes,
    externalDmnsByNamespace,
  } = useDmnEditorDerivedStore();

  // State

  const [reactFlowInstance, setReactFlowInstance] = useState<
    RF.ReactFlowInstance<DmnDiagramNodeData, DmnDiagramEdgeData> | undefined
  >(undefined);

  const [connection, setConnection] = useState<RF.OnConnectStartParams | undefined>(undefined);

  // Refs

  const nodeIdBeingDraggedRef = useRef<string | null>(null);

  // Memos

  const rfSnapGrid = useMemo<[number, number]>(
    () => (diagram.snapGrid.isEnabled ? [diagram.snapGrid.x, diagram.snapGrid.y] : [1, 1]),
    [diagram.snapGrid.isEnabled, diagram.snapGrid.x, diagram.snapGrid.y]
  );

  // Callbacks

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
            data: sourceNode.data,
            href: sourceNode.id,
            bounds: sourceBounds,
            shapeId: sourceNode.data.shape["@_id"],
          },
          targetNode: {
            type: targetNode.type as NodeType,
            href: targetNode.id,
            data: targetNode.data,
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
        .reverse() // Respect the nodes z-index.
        .find(
          (node) =>
            node.id !== nodeIdToIgnore && // don't ever use the node being dragged
            getContainmentRelationship({ bounds: bounds!, container: node.data.shape["dc:Bounds"]! }).isInside
        ),
    [reactFlowInstance]
  );

  const onDragOver = useCallback(
    (e: React.DragEvent) => {
      if (
        !e.dataTransfer.types.find(
          (t) =>
            t === MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE ||
            t === MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS
        )
      ) {
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
        });
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

      // we need to remove the wrapper bounds, in order to get the correct position
      const containerBounds = container.current.getBoundingClientRect();
      const dropPoint = reactFlowInstance.project({
        x: e.clientX - containerBounds.left,
        y: e.clientY - containerBounds.top,
      });

      if (e.dataTransfer.getData(MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE)) {
        const typeOfNewNodeFromPalette = e.dataTransfer.getData(
          MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE
        ) as NodeType;
        e.stopPropagation();

        // --------- This is where we draw the line between the diagram and the model.

        dmnEditorStoreApi.setState((state) => {
          const newNodeId = addStandaloneNode({
            definitions: state.dmn.model.definitions,
            newNode: {
              type: typeOfNewNodeFromPalette,
              bounds: {
                "@_x": dropPoint.x,
                "@_y": dropPoint.y,
                "@_width": DEFAULT_NODE_SIZES[typeOfNewNodeFromPalette](diagram.snapGrid)["@_width"],
                "@_height": DEFAULT_NODE_SIZES[typeOfNewNodeFromPalette](diagram.snapGrid)["@_height"],
              },
            },
          });
          state.diagram.selectedNodes = [newNodeId];
        });
      } else if (e.dataTransfer.getData(MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS)) {
        e.stopPropagation();
        const externalNode = JSON.parse(
          e.dataTransfer.getData(MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS)
        ) as ExternalNode;

        // --------- This is where we draw the line between the diagram and the model.

        const externalDrgElement = (
          externalDmnsByNamespace.get(externalNode.externalDrgElementNamespace)?.model.definitions.drgElement ?? []
        ).find((s) => s["@_id"] === externalNode.externalDrgElementId);
        if (!externalDrgElement) {
          throw new Error(`Can't find DRG element with id '${externalNode.externalDrgElementId}'.`);
        }

        const externalNodeType = getNodeTypeFromDmnObject(externalDrgElement)!;
        const defaultExternalNodeDimensions = DEFAULT_NODE_SIZES[externalNodeType](diagram.snapGrid);

        dmnEditorStoreApi.setState((state) => {
          const namespaceName = getXmlNamespaceDeclarationName({
            model: original(state.dmn.model.definitions),
            namespace: externalNode.externalDrgElementNamespace,
          });

          if (!namespaceName) {
            throw new Error(`Can't find namespace name for '${externalNode.externalDrgElementNamespace}'.`);
          }

          addShape({
            definitions: state.dmn.model.definitions,
            nodeType: externalNodeType,
            shape: {
              "@_dmnElementRef": buildXmlQName({
                type: "xml-qname",
                prefix: namespaceName,
                localPart: externalDrgElement["@_id"]!,
              }),
              "@_isCollapsed": true,
              "dc:Bounds": {
                "@_x": dropPoint.x,
                "@_y": dropPoint.y,
                "@_width": defaultExternalNodeDimensions["@_width"],
                "@_height": defaultExternalNodeDimensions["@_height"],
              },
            },
          });
          state.diagram.selectedNodes = [
            buildXmlHref({
              namespace: externalNode.externalDrgElementNamespace,
              id: externalNode.externalDrgElementId,
            }),
          ];
        });

        console.debug(`DMN DIAGRAM: Adding external node`, JSON.stringify(externalNode));
      }
    },
    [container, externalDmnsByNamespace, diagram.snapGrid, dmnEditorStoreApi, reactFlowInstance]
  );

  useEffect(() => {
    const edgeUpdaterSource = document.querySelectorAll(
      ".react-flow__edgeupdater-source, .react-flow__edgeupdater-target"
    );
    if (connection) {
      edgeUpdaterSource.forEach((e) => e.classList.add("hidden"));
    } else {
      edgeUpdaterSource.forEach((e) => e.classList.remove("hidden"));
    }
  }, [connection]);

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

      const sourceNodeBounds = dmnShapesByHref.get(sourceNode.id)?.["dc:Bounds"];
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
        const newDmnObejctHref = addConnectedNode({
          definitions: state.dmn.model.definitions,
          edge,
          sourceNode: {
            href: sourceNode.id,
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

        state.diagram.selectedNodes = [newDmnObejctHref];
      });
    },
    [connection, container, diagram.snapGrid, dmnEditorStoreApi, nodesById, reactFlowInstance, dmnShapesByHref]
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
        const controlWaypointsByEdge = new Map<number, Set<number>>();

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
                // We only need to resize the node if its snapped dimensions change, as snapping is non-destructive.
                const snappedShape = snapShapeDimensions(diagram.snapGrid, node.data.shape);
                if (
                  snappedShape.width !== change.dimensions.width ||
                  snappedShape.height !== change.dimensions.height
                ) {
                  resizeNode({
                    definitions: state.dmn.model.definitions,
                    dmnShapesByHref,
                    change: {
                      isExternal: !!node.data.dmnObjectQName.prefix,
                      nodeType: node.type as NodeType,
                      index: node.data.index,
                      shapeIndex: node.data.shape.index,
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
              }
              break;
            case "position":
              console.debug(`DMN DIAGRAM: 'onNodesChange' --> position '${change.id}'`);
              state.dispatch.diagram.setNodeStatus(state, change.id, { dragging: change.dragging });
              if (change.positionAbsolute) {
                const node = nodesById.get(change.id)!;
                const { delta } = repositionNode({
                  definitions: state.dmn.model.definitions,
                  controlWaypointsByEdge,
                  change: {
                    type: "absolute",
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

                // FIXME: Tiago --> This should be inside `repositionNode` I guess?
                // Update nested
                // External Decision Services will have encapsulated and output decisions, but they aren't depicted in the graph.
                if (node.type === NODE_TYPES.decisionService && !node.data.dmnObjectQName.prefix) {
                  const decisionService = node.data.dmnObject as DMN15__tDecisionService;
                  const nested = [
                    ...(decisionService.outputDecision ?? []),
                    ...(decisionService.encapsulatedDecision ?? []),
                  ];

                  for (let i = 0; i < nested.length; i++) {
                    const nestedNode = nodesById.get(nested[i]["@_href"])!;
                    const snappedNestedNodeShapeWithAppliedDelta = snapShapePosition(
                      diagram.snapGrid,
                      offsetShapePosition(nestedNode.data.shape, delta)
                    );
                    repositionNode({
                      definitions: state.dmn.model.definitions,
                      controlWaypointsByEdge,
                      change: {
                        type: "absolute",
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
              deleteNode({
                definitions: state.dmn.model.definitions,
                dmnObjectQName: node.data.dmnObjectQName,
                dmnObject: { type: node.type as NodeType, id: node.data.dmnObject["@_id"]! },
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
    [reactFlowInstance, dmnEditorStoreApi, nodesById, edges, dmnShapesByHref, diagram.snapGrid]
  );

  const resetToBeforeEditingBegan = useCallback(() => {
    dmnEditorStoreApi.setState((state) => {
      state.dmn.model = dmnModelBeforeEditingRef.current;
      state.diagram.draggingNodes = [];
      state.diagram.draggingWaypoints = [];
      state.diagram.resizingNodes = [];
      state.diagram.dropTargetNode = undefined;
      state.diagram.edgeIdBeingUpdated = undefined;
    });
  }, [dmnEditorStoreApi, dmnModelBeforeEditingRef]);

  const onNodeDrag = useCallback<RF.NodeDragHandler>(
    (e, node: RF.Node<DmnDiagramNodeData>) => {
      nodeIdBeingDraggedRef.current = node.id;
      dmnEditorStoreApi.setState((state) => {
        state.diagram.dropTargetNode = getFirstNodeFittingBounds(node.id, {
          // We can't use node.data.dmnObject because it hasn't been updated at this point yet.
          "@_x": node.positionAbsolute?.x ?? 0,
          "@_y": node.positionAbsolute?.y ?? 0,
          "@_width": node.width ?? 0,
          "@_height": node.height ?? 0,
        });
      });
    },
    [dmnEditorStoreApi, getFirstNodeFittingBounds]
  );

  const onNodeDragStart = useCallback<RF.NodeDragHandler>(
    (e, node: RF.Node<DmnDiagramNodeData>, nodes) => {
      dmnModelBeforeEditingRef.current = thisDmn.model;
      onNodeDrag(e, node, nodes);
    },
    [thisDmn.model, dmnModelBeforeEditingRef, onNodeDrag]
  );

  const onNodeDragStop = useCallback<RF.NodeDragHandler>(
    (e, node: RF.Node<DmnDiagramNodeData>) => {
      console.debug("DMN DIAGRAM: `onNodeDragStop`");
      const nodeBeingDragged = nodesById.get(nodeIdBeingDraggedRef.current!);
      nodeIdBeingDraggedRef.current = null;
      if (!nodeBeingDragged) {
        return;
      }

      // Validate
      const dropTargetNode = dmnEditorStoreApi.getState().diagram.dropTargetNode;
      if (dropTargetNode && containment.has(dropTargetNode.type as NodeType) && !isDropTargetNodeValidForSelection) {
        console.debug(
          `DMN DIAGRAM: Invalid containment: '${[...selectedNodeTypes].join("', '")}' inside '${
            dropTargetNode.type
          }'. Ignoring nodes dropped.`
        );
        resetToBeforeEditingBegan();
        return;
      }

      try {
        dmnEditorStoreApi.setState((state) => {
          state.diagram.dropTargetNode = undefined;

          if (!node.dragging) {
            return;
          }

          // Un-parent
          if (nodeBeingDragged.data.parentRfNode) {
            const p = nodesById.get(nodeBeingDragged.data.parentRfNode.id);
            if (p?.type === NODE_TYPES.decisionService && nodeBeingDragged.type === NODE_TYPES.decision) {
              for (let i = 0; i < state.diagram.selectedNodes.length; i++) {
                deleteDecisionFromDecisionService({
                  definitions: state.dmn.model.definitions,
                  decisionId: nodesById.get(state.diagram.selectedNodes[i])!.data.dmnObject["@_id"]!, // We can assume that all selected nodes are Decisions because the contaiment was validated above.
                  decisionServiceId: nodesById.get(p.id)!.data.dmnObject["@_id"]!,
                });
              }
            } else {
              console.debug(
                `DMN DIAGRAM: Ignoring '${nodeBeingDragged.type}' with parent '${dropTargetNode?.type}' dropping somewhere..`
              );
            }
          }

          // Parent
          if (dropTargetNode?.type === NODE_TYPES.decisionService) {
            for (let i = 0; i < state.diagram.selectedNodes.length; i++) {
              addDecisionToDecisionService({
                definitions: state.dmn.model.definitions,
                decisionId: nodesById.get(state.diagram.selectedNodes[i])!.data.dmnObject["@_id"]!, // We can assume that all selected nodes are Decisions because the contaiment was validated above.
                decisionServiceId: nodesById.get(dropTargetNode.id)!.data.dmnObject["@_id"]!,
              });
            }
          } else {
            console.debug(
              `DMN DIAGRAM: Ignoring '${nodeBeingDragged.type}' dropped on top of '${dropTargetNode?.type}'`
            );
          }
        });
      } catch (e) {
        console.error(e);
        resetToBeforeEditingBegan();
      }
    },
    [dmnEditorStoreApi, isDropTargetNodeValidForSelection, nodesById, resetToBeforeEditingBegan, selectedNodeTypes]
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
            href: sourceNode.id,
            data: sourceNode.data,
            bounds: sourceBounds,
            shapeId: sourceNode.data.shape["@_id"],
          },
          targetNode: {
            type: targetNode.type as NodeType,
            href: targetNode.id,
            data: targetNode.data,
            bounds: targetBounds,
            index: targetNode.data.index,
            shapeId: targetNode.data.shape["@_id"],
          },
          keepWaypointsIfSameTarget: true,
        });

        // The DMN Edge changed nodes, so we need to delete the old one, but keep the waypoints!
        // FIXME: Tiago --> What about other DMNEdge properties? Style etc. Should we keep those too?
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

  // Override Reactflow's behavior by intercepting the keydown event using its `capture` variant.
  const handleRfKeyDownCapture = useCallback(
    (e: React.KeyboardEvent) => {
      if (e.key === "Escape") {
        if (isDiagramEditingInProgress && dmnModelBeforeEditingRef.current) {
          console.debug(
            "DMN DIAGRAM: Intercepting Escape pressed and preventing propagation. Reverting DMN model to what it was before editing began."
          );

          e.stopPropagation();
          e.preventDefault();

          resetToBeforeEditingBegan();
        } else if (!connection) {
          dmnEditorStoreApi.setState((state) => {
            if (state.diagram.selectedNodes.length > 0 || state.diagram.selectedEdges.length > 0) {
              console.debug("DMN DIAGRAM: Esc pressed. Desselecting everything.");
              state.diagram.selectedNodes = [];
              state.diagram.selectedEdges = [];
              e.preventDefault();
            } else if (state.diagram.selectedNodes.length <= 0 && state.diagram.selectedEdges.length <= 0) {
              console.debug("DMN DIAGRAM: Esc pressed. Closing all open panels.");
              state.diagram.propertiesPanel.isOpen = false;
              state.diagram.overlaysPanel.isOpen = false;
              state.diagram.externalNodesPanel.isOpen = false;
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
    [connection, dmnEditorStoreApi, dmnModelBeforeEditingRef, isDiagramEditingInProgress, resetToBeforeEditingBegan]
  );

  return (
    <>
      <DiagramContainerContextProvider container={container}>
        <EdgeMarkers />
        <RF.ReactFlow
          onKeyDownCapture={handleRfKeyDownCapture} // Override Reactflow's keyboard listeners.
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
          onNodeDragStart={onNodeDragStart}
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
          // Used to make the Palette work by dropping nodes on the Reactflow Canvas
          onDrop={onDrop}
          onDragOver={onDragOver}
          // (end)
        >
          <SelectionStatus />
          <Palette />
          <TopRightCornerPanels />
          <PanWhenAltPressed />
          <KeyboardShortcuts setConnection={setConnection} />
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

  const togglePropertiesPanel = useCallback(() => {
    dmnEditorStoreApi.setState((state) => dispatch.diagram.togglePropertiesPanel(state));
  }, [dispatch.diagram, dmnEditorStoreApi]);

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
            key={`${diagram.overlaysPanel.isOpen}`}
            aria-label="Overlays Panel"
            position={"bottom-end"}
            hideOnOutsideClick={false}
            isVisible={diagram.overlaysPanel.isOpen}
            enableFlip={true}
            headerContent={<div>Overlays</div>}
            bodyContent={<OverlaysPanel />}
          >
            <button className={"kie-dmn-editor--overlays-panel-toggle-button"} onClick={toggleOverlaysPanel}>
              <VirtualMachineIcon size={"sm"} />
            </button>
          </Popover>
        </aside>
        {!diagram.propertiesPanel.isOpen && (
          <aside className={"kie-dmn-editor--properties-panel-toggle"}>
            <button className={"kie-dmn-editor--properties-panel-toggle-button"} onClick={togglePropertiesPanel}>
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

export function KeyboardShortcuts({
  setConnection,
}: {
  setConnection: React.Dispatch<React.SetStateAction<RF.OnConnectStartParams | undefined>>;
}) {
  const rfStoreApi = RF.useStoreApi();
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const rf = RF.useReactFlow();

  const space = RF.useKeyPress(["Space"]);
  useEffect(() => {
    if (!space) {
      return;
    }

    rf.setViewport(DEFAULT_VIEWPORT, { duration: 200 });
  }, [rf, space]);

  const b = RF.useKeyPress(["b"]);
  useEffect(() => {
    if (!b) {
      return;
    }

    const selectedNodes = rf.getNodes().filter((s) => s.selected);
    if (selectedNodes.length <= 0) {
      return;
    }

    const bounds = getBounds({ nodes: selectedNodes, padding: 100 });
    rf.fitBounds(
      {
        x: bounds["@_x"],
        y: bounds["@_y"],
        width: bounds["@_width"],
        height: bounds["@_height"],
      },
      { duration: 200 }
    );
  }, [b, rf]);

  const esc = RF.useKeyPress(["Escape"]);
  useEffect(() => {
    if (!esc) {
      return;
    }

    rfStoreApi.setState((rfState) => {
      if (rfState.connectionNodeId) {
        console.debug("DMN DIAGRAM: Esc pressed. Cancelling connection.");
        rfState.cancelConnection();
        setConnection(undefined);
      } else {
        (document.activeElement as any)?.blur?.();
      }

      return rfState;
    });
  }, [dmnEditorStoreApi, esc, rfStoreApi, setConnection]);

  const cut = RF.useKeyPress(["Meta+x"]);
  useEffect(() => {
    if (!cut) {
      return;
    }
  }, [cut]);
  const copy = RF.useKeyPress(["Meta+c"]);
  useEffect(() => {
    if (!copy) {
      return;
    }
  }, [copy]);
  const paste = RF.useKeyPress(["Meta+v"]);
  useEffect(() => {
    if (!paste) {
      return;
    }
  }, [paste]);

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

  const h = RF.useKeyPress(["h"]);
  useEffect(() => {
    if (!h) {
      return;
    }

    dmnEditorStoreApi.setState((state) => {
      state.diagram.overlays.enableNodeHierarchyHighlight = !state.diagram.overlays.enableNodeHierarchyHighlight;
    });
  }, [dmnEditorStoreApi, h]);

  const i = RF.useKeyPress(["i"]);
  useEffect(() => {
    if (!i) {
      return;
    }

    dmnEditorStoreApi.setState((state) => {
      state.diagram.propertiesPanel.isOpen = !state.diagram.propertiesPanel.isOpen;
    });
  }, [dmnEditorStoreApi, i]);

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
