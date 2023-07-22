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
import { Pallete } from "./Pallete";
import { SNAP_GRID, snapShapeDimensions, snapShapePosition } from "./SnapGrid";
import { ConnectionLine } from "./connections/ConnectionLine";
import { EDGE_TYPES } from "./edges/EdgeTypes";
import {
  AssociationEdge,
  AuthorityRequirementEdge,
  InformationRequirementEdge,
  KnowledgeRequirementEdge,
} from "./edges/Edges";
import { DmnNodeWithExpression } from "./nodes/DmnNodeWithExpression";
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
import { checkIsValidConnection } from "./connections/isValidConnection";
import { EdgeType, NodeType } from "./connections/graphStructure";
import { EdgeMarkers } from "./edges/EdgeMarkers";

const PAN_ON_DRAG = [1, 2];

const FIT_VIEW_OPTIONS = { maxZoom: 1, minZoom: 1, duration: 400 };

const DEFAULT_VIEWPORT = { x: 100, y: 0, zoom: 1 };

export function Diagram({
  dmn,
  setDmn,
  container,
  isPropertiesPanelOpen,
  setOpenNodeWithExpression,
  onSelect,
  setPropertiesPanelOpen,
}: {
  dmn: { definitions: DMN14__tDefinitions };
  setDmn: React.Dispatch<React.SetStateAction<{ definitions: DMN14__tDefinitions }>>;
  container: React.RefObject<HTMLElement>;
  isPropertiesPanelOpen: boolean;
  setOpenNodeWithExpression: React.Dispatch<React.SetStateAction<DmnNodeWithExpression | undefined>>;
  setPropertiesPanelOpen: React.Dispatch<React.SetStateAction<boolean>>;
  onSelect: (nodes: string[]) => void;
}) {
  const [nodes, setNodes, onNodesChange] = RF.useNodesState([]);
  const [edges, setEdges, onEdgesChange] = RF.useEdgesState([]);

  const onEdgeUpdate = useCallback((args) => {
    console.log("TIAGO WRITE: Edge updated! --> ", args);
  }, []);

  const onInfo = useCallback(() => {
    setPropertiesPanelOpen(true);
  }, [setPropertiesPanelOpen]);

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

  const nodesById = useMemo(
    () =>
      nodes.reduce((acc, a) => {
        acc.set(a.id, a);
        return acc;
      }, new Map<string, RF.Node>()),
    [nodes]
  );

  const { edgesById, shapesById } = useMemo(
    () =>
      (dmn.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [])
        .flatMap((diagram) => diagram["dmndi:DMNDiagramElement"] ?? [])
        .reduce(
          (acc, e) => {
            if (e.__$$element === "dmndi:DMNShape") {
              acc.shapesById.set(e["@_dmnElementRef"], e);
            } else if (e.__$$element === "dmndi:DMNEdge") {
              acc.edgesById.set(e["@_dmnElementRef"], e);
            }

            return acc;
          },
          {
            edgesById: new Map<string, DMNDI13__DMNEdge>(),
            shapesById: new Map<string, DMNDI13__DMNShape>(),
          }
        ),
    [dmn.definitions]
  );

  useEffect(() => {
    setNodes([
      ...(dmn.definitions.drgElement ?? []).map((drgElement) => {
        const shape = shapesById.get(drgElement["@_id"]!)!;

        if (drgElement.__$$element === "inputData") {
          return {
            id: drgElement["@_id"]!,
            type: NODE_TYPES.inputData,
            position: snapShapePosition(shape),
            data: { inputData: drgElement, shape, onInfo },
            style: { ...snapShapeDimensions(shape) },
          };
        } else if (drgElement.__$$element === "decision") {
          return {
            id: drgElement["@_id"]!,
            type: NODE_TYPES.decision,
            position: snapShapePosition(shape),
            data: { decision: drgElement, shape, setOpenNodeWithExpression, onInfo },
            style: { ...snapShapeDimensions(shape) },
          };
        } else if (drgElement.__$$element === "businessKnowledgeModel") {
          return {
            id: drgElement["@_id"]!,
            type: NODE_TYPES.bkm,
            position: snapShapePosition(shape),
            data: { bkm: drgElement, shape, setOpenNodeWithExpression, onInfo },
            style: { ...snapShapeDimensions(shape) },
          };
        } else if (drgElement.__$$element === "decisionService") {
          return {
            id: drgElement["@_id"]!,
            type: NODE_TYPES.decisionService,
            position: snapShapePosition(shape),
            data: { decisionService: drgElement, shape, onInfo },
            style: { zIndex: 1, ...snapShapeDimensions(shape) },
          };
        } else if (drgElement.__$$element === "knowledgeSource") {
          return {
            id: drgElement["@_id"]!,
            type: NODE_TYPES.knowledgeSource,
            position: snapShapePosition(shape),
            data: { knowledgeSource: drgElement, shape, onInfo },
            style: { ...snapShapeDimensions(shape) },
          };
        } else {
          throw new Error("Unknown type of drgElement for nodes.");
        }
      }),
      ...(dmn.definitions.artifact ?? [])
        .filter(({ __$$element }) => __$$element === "group" || __$$element === "textAnnotation")
        .map((artifact) => {
          const shape = shapesById.get(artifact["@_id"]!)!;
          if (artifact.__$$element === "group") {
            return {
              id: artifact["@_id"]!,
              type: NODE_TYPES.group,
              position: snapShapePosition(shape),
              data: { group: artifact, shape, onInfo },
              style: { zIndex: 1, ...snapShapeDimensions(shape) },
            };
          } else if (artifact.__$$element === "textAnnotation") {
            return {
              id: artifact["@_id"]!,
              type: NODE_TYPES.textAnnotation,
              position: snapShapePosition(shape),
              data: { textAnnotation: artifact, shape, onInfo },
              style: { ...snapShapeDimensions(shape) },
            };
          } else {
            throw new Error("Unknown type of artifact for nodes.");
          }
        }),
    ]);
  }, [dmn.definitions.drgElement, dmn.definitions.artifact, onInfo, setNodes, setOpenNodeWithExpression, shapesById]);

  const getEdgeData = useCallback(
    ({ id, source, target }: { id: string; source: string; target: string }) => {
      return {
        dmnEdge: id ? edgesById.get(id) : undefined,
        dmnShapeSource: shapesById.get(source),
        dmnShapeTarget: shapesById.get(target),
      };
    },
    [edgesById, shapesById]
  );

  useEffect(() => {
    setEdges([
      // information requirement
      ...(dmn.definitions.drgElement ?? [])
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
              markerEnd: "closed-arrow",
              target,
            };
          }),
        ]),

      // knowledge requirement
      ...(dmn.definitions.drgElement ?? [])
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
              markerEnd: "open-arrow",
              source,
              target,
            };
          }),
        ]),

      // authority requirement
      ...(dmn.definitions.drgElement ?? [])
        .filter(
          ({ __$$element }) =>
            __$$element === "decision" || __$$element === "businessKnowledgeModel" || __$$element === "knowledgeSource"
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
              markerEnd: "closed-circle",
              source,
              target,
            };
          }),
        ]),

      // association
      ...(dmn.definitions.artifact ?? [])
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
    ]);
  }, [dmn.definitions.artifact, dmn.definitions.drgElement, setEdges, getEdgeData]);

  const [reactFlowInstance, setReactFlowInstance] = useState<RF.ReactFlowInstance | undefined>(undefined);

  useEffect(() => {
    onSelect(nodes.flatMap((n) => (n.selected ? [n.id] : [])));
  }, [nodes, onSelect]);

  const onDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = "move";
  }, []);

  const onDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();

      if (!container.current || !reactFlowInstance) {
        return;
      }

      const type = e.dataTransfer.getData("application/reactflow") as NodeType;
      if (typeof type === "undefined" || !type) {
        return;
      }

      e.stopPropagation();

      const rfBounds = container.current.getBoundingClientRect();
      const position = reactFlowInstance.project({
        x: e.clientX - rfBounds.left,
        y: e.clientY - rfBounds.top,
      });

      console.info(`TIAGO WRITE: Adding node of type '${type}' at position '${position.x},${position.y}'.`);
    },
    [container, reactFlowInstance]
  );

  const [connection, setConnection] = useState<RF.OnConnectStartParams | undefined>(undefined);
  const onConnectStart = useCallback<RF.OnConnectStart>((a, b) => setConnection(b), []);

  const onConnectEnd = useCallback(
    (event) => {
      const targetIsPane = event.target.classList.contains("react-flow__pane");
      if (targetIsPane && container.current && connection) {
        // we need to remove the wrapper bounds, in order to get the correct position
        const { top, left } = container.current.getBoundingClientRect();
        const dropPoint = { x: event.clientX - left, y: event.clientY - top };
        console.log(`TIAGO WRITE: Creating node at ${dropPoint.x},${dropPoint.y} -->${JSON.stringify(connection)}`);
      }
    },
    [connection, container]
  );

  const isValidConnection = useCallback<RF.IsValidConnection>(
    (edge) => checkIsValidConnection(nodesById, edge),
    [nodesById]
  );

  return (
    <>
      <EdgeMarkers />
      <RF.ReactFlow
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
        onConnectStart={onConnectStart}
        onConnect={onEdgeUpdate}
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
        <PropertiesPanelToggle
          isPropertiesPanelOpen={isPropertiesPanelOpen}
          setPropertiesPanelOpen={setPropertiesPanelOpen}
        />
        <PanWhenAltPressed />
        <RF.Background />
        <RF.Controls fitViewOptions={FIT_VIEW_OPTIONS} position={"bottom-right"} />
      </RF.ReactFlow>
    </>
  );
}

export function PropertiesPanelToggle({
  setPropertiesPanelOpen,
  isPropertiesPanelOpen,
}: {
  isPropertiesPanelOpen: boolean;
  setPropertiesPanelOpen: React.Dispatch<React.SetStateAction<boolean>>;
}) {
  return (
    <>
      {(!isPropertiesPanelOpen && (
        <RF.Panel position={"top-right"}>
          <aside className={"kie-dmn-editor--properties-panel-toggle"}>
            <button
              className={"kie-dmn-editor--properties-panel-toggle-button"}
              onClick={() => setPropertiesPanelOpen((prev) => !prev)}
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
