import * as RF from "reactflow";

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";

import {
  DMN14__tAssociation,
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDefinitions,
  DMN14__tKnowledgeSource,
  DMNDI13__DMNEdge,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { addConnectedNode } from "../mutations/addConnectedNode";
import { addEdge } from "../mutations/addEdge";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { useDmnEditor } from "../store/Store";
import { PALLETE_ELEMENT_MIME_TYPE, Pallete } from "./Pallete";
import { MIN_SIZE_FOR_NODES, SNAP_GRID, snapShapeDimensions, snapShapePosition } from "./SnapGrid";
import { ConnectionLine } from "./connections/ConnectionLine";
import { TargetHandleId } from "./connections/NodeHandles";
import { EdgeType, NodeType, getDefaultEdgeTypeBetween } from "./connections/graphStructure";
import { checkIsValidConnection } from "./connections/isValidConnection";
import { EdgeMarkers } from "./edges/EdgeMarkers";
import { EDGE_TYPES } from "./edges/EdgeTypes";
import {
  AssociationEdge,
  AuthorityRequirementEdge,
  DmnEditorDiagramEdgeData,
  InformationRequirementEdge,
  KnowledgeRequirementEdge,
} from "./edges/Edges";
import { DEFAULT_NODE_SIZES } from "./nodes/DefaultSizes";
import { NODE_TYPES } from "./nodes/NodeTypes";
import {
  BkmNode,
  DecisionNode,
  DecisionServiceNode,
  DmnEditorDiagramNodeData,
  GroupNode,
  InputDataNode,
  KnowledgeSourceNode,
  TextAnnotationNode,
} from "./nodes/Nodes";
import { repositionNodes as repositionDiagramElements } from "../mutations/repositionNodes";

const PAN_ON_DRAG = [1, 2];

const FIT_VIEW_OPTIONS = { maxZoom: 1, minZoom: 1, duration: 400 };

const DEFAULT_VIEWPORT = { x: 100, y: 0, zoom: 1 };

export function Diagram({
  container,
  onSelect,
}: {
  container: React.RefObject<HTMLElement>;
  onSelect: (nodes: string[]) => void;
}) {
  const snapGrid = useMemo<[number, number]>(() => [SNAP_GRID.x, SNAP_GRID.y], []);

  const nodeTypes: Record<NodeType, any> = useMemo(
    () => ({
      // grouping
      node_decisionService: DecisionServiceNode,
      node_group: GroupNode,

      // logic
      node_inputData: InputDataNode,
      node_decision: DecisionNode,
      node_bkm: BkmNode,

      // info
      node_knowledgeSource: KnowledgeSourceNode,
      node_textAnnotation: TextAnnotationNode,
    }),
    []
  );

  const edgeTypes: Record<EdgeType, any> = useMemo(() => {
    return {
      edge_informationRequirement: InformationRequirementEdge,
      edge_authorityRequirement: AuthorityRequirementEdge,
      edge_knowledgeRequirement: KnowledgeRequirementEdge,
      edge_association: AssociationEdge,
    };
  }, []);

  const { dmn, dispatch } = useDmnEditor();

  const { shapesById, nodesById, nodes: nodesFromDmn, edges: edgesFromDmn } = useDmnDiagramData(dmn.model);
  const [nodes, setNodes, onNodesChange] = RF.useNodesState<DmnEditorDiagramNodeData<any>>(nodesFromDmn);
  const [edges, setEdges, onEdgesChange] = RF.useEdgesState<DmnEditorDiagramEdgeData>(edgesFromDmn);
  useEffect(() => {
    setNodes(nodesFromDmn);
    setEdges(edgesFromDmn);
  }, [edgesFromDmn, nodesFromDmn, setEdges, setNodes]);

  const [reactFlowInstance, setReactFlowInstance] = useState<RF.ReactFlowInstance | undefined>(undefined);

  const onEdgeUpdate: RF.OnEdgeUpdateFunc = useCallback((args) => {}, []);

  const onConnect: RF.OnConnect = useCallback(
    (args) => {
      const sourceNode = nodesById.get(args.source!);
      const targetNode = nodesById.get(args.target!);
      if (!sourceNode || !targetNode) {
        throw new Error("Cannot create connection without target and source nodes!");
      }

      const sourceBounds = shapesById.get(sourceNode.id)?.["dc:Bounds"];
      const targetBounds = shapesById.get(targetNode.id)?.["dc:Bounds"];
      if (!sourceBounds || !targetBounds) {
        throw new Error("Cannot create connection without target bounds!");
      }

      // --------- This is where we draw the line between the diagram and the model.

      addEdge({
        dispatch: { dmn: dispatch.dmn },
        edge: { type: args.sourceHandle as EdgeType, handle: args.targetHandle as TargetHandleId },
        sourceNode: { type: sourceNode.type as NodeType, id: sourceNode.id, bounds: sourceBounds },
        targetNode: {
          type: targetNode.type as NodeType,
          id: targetNode.id,
          bounds: targetBounds,
          index: targetNode.data.index,
        },
      });
    },
    [dispatch.dmn, nodesById, shapesById]
  );

  useEffect(() => {
    onSelect(nodes.flatMap((n) => (n.selected ? [n.id] : [])));
  }, [nodes, onSelect]);

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

      addStandaloneNode({
        dispatch: { dmn: dispatch.dmn },
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
    },
    [container, dispatch.dmn, reactFlowInstance]
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

      addConnectedNode({
        dispatch: { dmn: dispatch.dmn },
        edge,
        sourceNode: {
          id: sourceNode.id,
          type: sourceNodeType as NodeType,
          bounds: sourceNodeBounds,
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
    },
    [connection, container, dispatch.dmn, nodesById, reactFlowInstance, shapesById]
  );

  const onNodeDragStop = useCallback<RF.NodeDragHandler>(
    (e, node, nodes: RF.Node<DmnEditorDiagramNodeData<any>>[]) => {
      repositionDiagramElements({
        dispatch: { dmn: dispatch.dmn },
        changes: nodes.map((node) => ({
          dmnDiagramElementIndex: node.data.shape.index,
          position: {
            "@_x": node.positionAbsolute?.x ?? 0,
            "@_y": node.positionAbsolute?.y ?? 0,
          },
        })),
      });
    },
    [dispatch.dmn]
  );

  const isValidConnection = useCallback<RF.IsValidConnection>(
    (edge) => checkIsValidConnection(nodesById, edge),
    [nodesById]
  );

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
        onNodesChange={onNodesChange} // FIXME: Selection is getting lost when dragging if I change to _onNodesChange.
        onEdgesChange={onEdgesChange}
        edgesUpdatable={true}
        connectionLineComponent={ConnectionLine}
        onEdgeUpdate={onEdgeUpdate}
        onConnect={onConnect}
        onConnectStart={onConnectStart}
        onConnectEnd={onConnectEnd}
        isValidConnection={isValidConnection}
        onNodeDragStop={onNodeDragStop}
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
  const { propertiesPanel, dispatch } = useDmnEditor();
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
  const nodes = RF.useNodes();
  const { setState: setStore, getState: getStore } = RF.useStoreApi();

  const selectedCount = useMemo(() => {
    return nodes.filter((s) => s.selected).length;
  }, [nodes]);

  useEffect(() => {
    if (selectedCount >= 2) {
      setStore((prev) => ({
        ...prev,
        nodesSelectionActive: true,
      }));
    }
  }, [selectedCount, setStore]);

  const onClose = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      getStore().resetSelectedElements();
    },
    [getStore]
  );
  return (
    <>
      {(selectedCount >= 2 && (
        <RF.Panel position={"top-center"}>
          <Label style={{ paddingLeft: "24px" }} onClose={onClose}>{`${selectedCount} nodes selected`}</Label>
        </RF.Panel>
      )) || <></>}
    </>
  );
}

export function KeyboardShortcuts() {
  const { setState } = RF.useStoreApi();
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));

  const esc = RF.useKeyPress(["Escape"]);
  useEffect(() => {
    if (!esc) {
      return;
    }

    setState((prev) => {
      if (isConnecting) {
        prev.cancelConnection();
      } else {
        const selected = prev.getNodes().flatMap((n) => (n.selected ? [n.id] : []));
        if (selected.length > 0) {
          prev.resetSelectedElements();
        }
        (document.activeElement as any)?.blur?.();
      }

      return prev;
    });
  }, [esc, isConnecting, setState]);

  const selectAll = RF.useKeyPress(["a", "Meta+a"]);
  useEffect(() => {
    if (!selectAll) {
      return;
    }

    setState((prev) => {
      const unselected = prev.getNodes().flatMap((n) => (!n.selected ? [n.id] : []));
      if (unselected.length > 0) {
        prev.addSelectedNodes(prev.getNodes().map((s) => s.id));
      } else {
        prev.resetSelectedElements();
      }

      return prev;
    });
  }, [selectAll, setState]);

  return <></>;
}

export function PanWhenAltPressed() {
  const altPressed = RF.useKeyPress("Alt");
  const store = RF.useStoreApi();

  useEffect(() => {
    store.setState({
      nodesDraggable: !altPressed,
      nodesConnectable: !altPressed,
      elementsSelectable: !altPressed,
    });
  }, [altPressed, store]);

  return <></>;
}

export function useDmnDiagramData(model: { definitions: DMN14__tDefinitions }) {
  const { edgesById, shapesById } = useMemo(
    () =>
      (model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [])
        .flatMap((diagram) => diagram["dmndi:DMNDiagramElement"] ?? [])
        .reduce(
          (acc, e, index) => {
            if (e.__$$element === "dmndi:DMNShape") {
              acc.shapesById.set(e["@_dmnElementRef"], { ...e, index });
            } else if (e.__$$element === "dmndi:DMNEdge") {
              acc.edgesById.set(e["@_dmnElementRef"], { ...e, index });
            }

            return acc;
          },
          {
            edgesById: new Map<string, DMNDI13__DMNEdge & { index: number }>(),
            shapesById: new Map<string, DMNDI13__DMNShape & { index: number }>(),
          }
        ),
    [model.definitions]
  );

  const getEdgeData = useCallback(
    ({ id, source, target }: { id: string; source: string; target: string }): DmnEditorDiagramEdgeData => {
      return {
        dmnEdge: id ? edgesById.get(id) : undefined,
        dmnShapeSource: shapesById.get(source),
        dmnShapeTarget: shapesById.get(target),
      };
    },
    [edgesById, shapesById]
  );

  const { nodes, edges, nodesById } = useMemo(() => {
    const nodesById = new Map<string, RF.Node<DmnEditorDiagramNodeData<any>>>();
    return {
      nodes: [
        ...(model.definitions.drgElement ?? []).map((drgElement, index) => {
          const shape = shapesById.get(drgElement["@_id"]!)!;

          if (drgElement.__$$element === "inputData") {
            const n = {
              id: drgElement["@_id"]!,
              type: NODE_TYPES.inputData,
              position: snapShapePosition(shape),
              data: { dmnObject: drgElement, shape, index },
              style: { ...snapShapeDimensions(shape) },
            };
            nodesById.set(n.id, n);
            return n;
          } else if (drgElement.__$$element === "decision") {
            const n = {
              id: drgElement["@_id"]!,
              type: NODE_TYPES.decision,
              position: snapShapePosition(shape),
              data: { dmnObject: drgElement, shape, index },
              style: { ...snapShapeDimensions(shape) },
            };
            nodesById.set(n.id, n);
            return n;
          } else if (drgElement.__$$element === "businessKnowledgeModel") {
            const n = {
              id: drgElement["@_id"]!,
              type: NODE_TYPES.bkm,
              position: snapShapePosition(shape),
              data: { dmnObject: drgElement, shape, index },
              style: { ...snapShapeDimensions(shape) },
            };
            nodesById.set(n.id, n);
            return n;
          } else if (drgElement.__$$element === "decisionService") {
            const n = {
              id: drgElement["@_id"]!,
              type: NODE_TYPES.decisionService,
              position: snapShapePosition(shape),
              data: { dmnObject: drgElement, shape, index },
              style: { zIndex: 1, ...snapShapeDimensions(shape) },
            };
            nodesById.set(n.id, n);
            return n;
          } else if (drgElement.__$$element === "knowledgeSource") {
            const n = {
              id: drgElement["@_id"]!,
              type: NODE_TYPES.knowledgeSource,
              position: snapShapePosition(shape),
              data: { dmnObject: drgElement, shape, index },
              style: { ...snapShapeDimensions(shape) },
            };
            nodesById.set(n.id, n);
            return n;
          } else {
            throw new Error("Unknown type of drgElement for nodes.");
          }
        }),
        ...(model.definitions.artifact ?? [])
          .filter(({ __$$element }) => __$$element === "group" || __$$element === "textAnnotation")
          .map((artifact, index) => {
            const shape = shapesById.get(artifact["@_id"]!)!;
            if (artifact.__$$element === "group") {
              const n = {
                id: artifact["@_id"]!,
                type: NODE_TYPES.group,
                position: snapShapePosition(shape),
                data: { dmnObject: artifact, shape, index },
                style: { zIndex: 1, ...snapShapeDimensions(shape) },
              };
              nodesById.set(n.id, n);
              return n;
            } else if (artifact.__$$element === "textAnnotation") {
              const n = {
                id: artifact["@_id"]!,
                type: NODE_TYPES.textAnnotation,
                position: snapShapePosition(shape),
                data: { dmnObject: artifact, shape, index },
                style: { ...snapShapeDimensions(shape) },
              };
              nodesById.set(n.id, n);
              return n;
            } else {
              throw new Error("Unknown type of artifact for nodes.");
            }
          }),
      ] as RF.Node[],
      edges: [
        // information requirement
        ...(model.definitions.drgElement ?? [])
          .filter(({ __$$element }) => __$$element === "decision")
          .flatMap((decision: DMN14__tDecision) => [
            ...(decision.informationRequirement ?? []).map((ir) => {
              const source = (ir.requiredDecision?.["@_href"] ?? ir.requiredInput?.["@_href"] ?? "#").substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
              const target = decision["@_id"]!;
              const id = ir["@_id"] ?? "";
              return {
                data: getEdgeData({ id, source, target }),
                id,
                type: EDGE_TYPES.informationRequirement,
                source,
                target,
              };
            }),
          ]),

        // knowledge requirement
        ...(model.definitions.drgElement ?? [])
          .filter(({ __$$element }) => __$$element === "decision" || __$$element === "businessKnowledgeModel")
          .flatMap((node: DMN14__tDecision | DMN14__tBusinessKnowledgeModel) => [
            ...(node.knowledgeRequirement ?? []).map((kr) => {
              const source = (kr.requiredKnowledge?.["@_href"] ?? "#").substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
              const target = node["@_id"]!;
              const id = kr["@_id"] ?? "";
              return {
                data: getEdgeData({ id, source, target }),
                id,
                type: EDGE_TYPES.knowledgeRequirement,
                source,
                target,
              };
            }),
          ]),

        // authority requirement
        ...(model.definitions.drgElement ?? [])
          .filter(
            ({ __$$element }) =>
              __$$element === "decision" ||
              __$$element === "businessKnowledgeModel" ||
              __$$element === "knowledgeSource"
          )
          .flatMap((node: DMN14__tDecision | DMN14__tBusinessKnowledgeModel | DMN14__tKnowledgeSource) => [
            ...(node.authorityRequirement ?? []).map((ar) => {
              const source = (
                ar.requiredInput?.["@_href"] ??
                ar.requiredDecision?.["@_href"] ??
                ar.requiredAuthority?.["@_href"] ??
                "#"
              ).substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
              const target = node["@_id"]!;
              const id = ar["@_id"] ?? "";
              return {
                data: getEdgeData({ id, source, target }),
                id,
                type: EDGE_TYPES.authorityRequirement,
                source,
                target,
              };
            }),
          ]),

        // association
        ...(model.definitions.artifact ?? [])
          .filter(({ __$$element }) => __$$element === "association")
          .flatMap((artifact) => {
            const association = artifact as DMN14__tAssociation;
            const source = (association.sourceRef?.["@_href"] ?? "#").substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
            const target = (association.targetRef?.["@_href"] ?? "#").substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
            const id = artifact["@_id"] ?? "";
            return {
              data: getEdgeData({ id, source, target }),
              id,
              type: EDGE_TYPES.association,
              source,
              target,
            };
          }),
      ],
      nodesById,
    };
  }, [getEdgeData, model.definitions.artifact, model.definitions.drgElement, shapesById]);

  return { shapesById, edgesById, nodesById, nodes, edges };
}
