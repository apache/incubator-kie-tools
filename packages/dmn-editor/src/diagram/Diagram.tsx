import * as RF from "reactflow";

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";

import {
  DMN14__tAssociation,
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDecisionService,
  DMN14__tDefinitions,
  DMN14__tGroup,
  DMN14__tInformationItem,
  DMN14__tInputData,
  DMN14__tKnowledgeSource,
  DMN14__tTextAnnotation,
  DMNDI13__DMNEdge,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { BarsIcon } from "@patternfly/react-icons/dist/js/icons/bars-icon";
import { InfoAltIcon } from "@patternfly/react-icons/dist/js/icons/info-alt-icon";
import { v4 as uuid } from "uuid";
import { DmnNodeWithExpression } from "./DmnNodeWithExpression";
import { NsweHandles } from "./edges/NsweHandles";
import { MIN_SIZE_FOR_NODES, SNAP_GRID, snapShapeDimensions, snapShapePosition } from "./SnapGrid";
import {
  AssociationEdge,
  AuthorityRequirementEdge,
  InformationRequirementEdge,
  KnowledgeRequirementEdge,
} from "./edges/Edges";
import { ConnectionLine } from "./edges/ConnectionLine";

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

  const onEdgeUpdate = useCallback(() => {
    /** */
  }, []);

  const snapGrid = useMemo<[number, number]>(() => [SNAP_GRID.x, SNAP_GRID.y], []);

  const nodeTypes = useMemo(
    () => ({
      // grouping
      decisionService: DecisionServiceNode,
      group: GroupNode,

      // logic
      inputData: InputDataNode,
      decision: DecisionNode,
      bkm: BkmNode,

      // info
      knowledgeSource: KnowledgeSourceNode,
      textAnnotation: TextAnnotationNode,
    }),
    []
  );

  const edgeTypes = useMemo(() => {
    return {
      informationRequirement: InformationRequirementEdge,
      authorityRequirement: AuthorityRequirementEdge,
      knowledgeRequirement: KnowledgeRequirementEdge,
      association: AssociationEdge,
    };
  }, []);

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

  const onInfo = useCallback(() => {
    setPropertiesPanelOpen(true);
  }, [setPropertiesPanelOpen]);

  useEffect(() => {
    setNodes([
      ...(dmn.definitions.drgElement ?? []).map((drgElement) => {
        const shape = shapesById.get(drgElement["@_id"]!)!;

        if (drgElement.__$$element === "inputData") {
          return {
            id: drgElement["@_id"]!,
            type: "inputData",
            position: snapShapePosition(shape),
            data: { inputData: drgElement, shape },
            style: { ...snapShapeDimensions(shape) },
          };
        } else if (drgElement.__$$element === "decision") {
          return {
            id: drgElement["@_id"]!,
            type: "decision",
            position: snapShapePosition(shape),
            data: { decision: drgElement, shape, setOpenNodeWithExpression, onInfo },
            style: { ...snapShapeDimensions(shape) },
          };
        } else if (drgElement.__$$element === "businessKnowledgeModel") {
          return {
            id: drgElement["@_id"]!,
            type: "bkm",
            position: snapShapePosition(shape),
            data: { bkm: drgElement, shape, setOpenNodeWithExpression, onInfo },
            style: { ...snapShapeDimensions(shape) },
          };
        } else if (drgElement.__$$element === "decisionService") {
          return {
            id: drgElement["@_id"]!,
            type: "decisionService",
            position: snapShapePosition(shape),
            data: { decisionService: drgElement, shape, onInfo },
            style: { zIndex: 1, ...snapShapeDimensions(shape) },
          };
        } else if (drgElement.__$$element === "knowledgeSource") {
          return {
            id: drgElement["@_id"]!,
            type: "knowledgeSource",
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
              type: "group",
              position: snapShapePosition(shape),
              data: { group: artifact, shape, onInfo },
              style: { zIndex: 1, ...snapShapeDimensions(shape) },
            };
          } else if (artifact.__$$element === "textAnnotation") {
            return {
              id: artifact["@_id"]!,
              type: "textAnnotation",
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

  useEffect(() => {
    const markerEnd = {
      width: 20,
      height: 20,
      type: RF.MarkerType.ArrowClosed,
      color: "black",
    };

    setEdges([
      // information requirement
      ...(dmn.definitions.drgElement ?? [])
        .filter(({ __$$element }) => __$$element === "decision")
        .flatMap((decision: DMN14__tDecision) => [
          ...(decision.informationRequirement ?? []).map((ir) => {
            const source = (ir.requiredDecision?.["@_href"] ?? ir.requiredInput?.["@_href"] ?? "#").substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
            const target = decision["@_id"]!;
            const id = ir["@_id"];
            return {
              data: {
                dmnEdge: id ? edgesById.get(id) : undefined,
                dmnShapeSource: shapesById.get(source),
                dmnShapeTarget: shapesById.get(target),
              },
              updatable: true,
              id: id ?? "",
              type: "informationRequirement",
              source,
              target,
              markerEnd,
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
            const id = kr["@_id"];
            return {
              data: {
                dmnEdge: id ? edgesById.get(id) : undefined,
                dmnShapeSource: shapesById.get(source),
                dmnShapeTarget: shapesById.get(target),
              },
              updatable: true,
              id: id ?? "",
              type: "knowledgeRequirement",
              source,
              target,
              markerEnd,
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
            const id = ar["@_id"];
            return {
              data: {
                dmnEdge: id ? edgesById.get(id) : undefined,
                dmnShapeSource: shapesById.get(source),
                dmnShapeTarget: shapesById.get(target),
              },
              updatable: true,
              id: id ?? "",
              type: "authorityRequirement",
              source,
              target,
              markerEnd,
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
          const id = artifact["@_id"];
          return {
            data: {
              dmnEdge: id ? edgesById.get(id) : undefined,
              dmnShapeSource: shapesById.get(source),
              dmnShapeTarget: shapesById.get(target),
            },
            updatable: true,
            id: id ?? "",
            type: "association",
            source,
            target,
            markerEnd,
          };
        }),
    ]);
  }, [dmn.definitions.artifact, dmn.definitions.drgElement, setEdges, edgesById, shapesById]);

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
      e.stopPropagation();

      if (!container.current || !reactFlowInstance) {
        return;
      }

      const type = e.dataTransfer.getData("application/reactflow");
      if (typeof type === "undefined" || !type) {
        return;
      }

      const rfBounds = container.current.getBoundingClientRect();
      const position = reactFlowInstance.project({
        x: e.clientX - rfBounds.left,
        y: e.clientY - rfBounds.top,
      });

      console.info(`Adding node of type '${type}' at position '${position.x},${position.y}'.`);
    },
    [container, reactFlowInstance]
  );

  return (
    <>
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

export function Pallete() {
  const onDragStart = useCallback((event, nodeType) => {
    event.dataTransfer.setData("application/reactflow", nodeType);
    event.dataTransfer.effectAllowed = "move";
  }, []);

  return (
    <RF.Panel position={"top-left"}>
      <aside className={"kie-dmn-editor--pallete"}>
        <button
          className={"kie-dmn-editor--pallete-button dndnode input-data"}
          onDragStart={(event) => onDragStart(event, "inputData")}
          draggable={true}
        >
          I
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode decision"}
          onDragStart={(event) => onDragStart(event, "decision")}
          draggable={true}
        >
          D
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode bkm"}
          onDragStart={(event) => onDragStart(event, "bkm")}
          draggable={true}
        >
          B
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode knowledge-source"}
          onDragStart={(event) => onDragStart(event, "knowledgeSource")}
          draggable={true}
        >
          K
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode decision-service"}
          onDragStart={(event) => onDragStart(event, "decisionService")}
          draggable={true}
        >
          D
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode text-annotation"}
          onDragStart={(event) => onDragStart(event, "textAnnotation")}
          draggable={true}
        >
          T
        </button>
        <button className={"kie-dmn-editor--pallete-button dndnode text-annotation"}>G</button>
      </aside>
    </RF.Panel>
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

export function EmptyLabel() {
  return (
    <span style={{ fontFamily: "serif" }}>
      <i style={{ opacity: 0.8 }}>{`<Empty>`}</i>
      <br />
      <i style={{ opacity: 0.5, fontSize: "0.8em", lineHeight: "0.8em" }}>{`Double-click to name`}</i>
    </span>
  );
}

export function InfoToolbar(props: {}) {
  return (
    <RF.NodeToolbar position={RF.Position.Left} align={"center"}>
      <Flex direction={{ default: "column" }}>
        <Button variant={ButtonVariant.plain} style={{ padding: 0, margin: 0 }}>
          <InfoAltIcon />
        </Button>
        <Button variant={ButtonVariant.plain} style={{ padding: 0, margin: 0 }}>
          <BarsIcon size={"sm"} style={{ width: "0.5em" }} />
        </Button>
      </Flex>
    </RF.NodeToolbar>
  );
}

export function DataTypeToolbar(props: {
  variable: DMN14__tInformationItem | undefined;
  shape: DMNDI13__DMNShape | undefined;
}) {
  return (
    <RF.NodeToolbar position={RF.Position.Bottom} align={"start"}>
      <Label
        style={{
          maxWidth: (props.shape?.["dc:Bounds"]?.["@_width"] ?? 0) - 16,
          background: "white",
          fontFamily: "monospace",
          paddingRight: "16px",
        }}
        isCompact={true}
      >{`🔹 ${props.variable?.["@_typeRef"] ?? "<Undefined>"}`}</Label>
    </RF.NodeToolbar>
  );
}

const handleStyle: React.CSSProperties = {
  display: "flex",
  position: "unset",
  transform: "unset",
};

export function OutgoingStuffToolbar(props: { isVisible: boolean }) {
  return (
    <>
      {props.isVisible && (
        <Flex className={"kie-dmn-editor--node-outgoing-stuff-toolbar"}>
          <FlexItem>
            <RF.Handle type="source" style={handleStyle} position={RF.Position.Top}>
              I
            </RF.Handle>
            <RF.Handle type="source" style={handleStyle} position={RF.Position.Top}>
              K
            </RF.Handle>
            <RF.Handle type="source" style={handleStyle} position={RF.Position.Top}>
              A
            </RF.Handle>
            <RF.Handle type="source" style={handleStyle} position={RF.Position.Top}>
              -
            </RF.Handle>
          </FlexItem>

          <FlexItem>
            <RF.Handle type="source" style={handleStyle} position={RF.Position.Top}>
              D
            </RF.Handle>
            <RF.Handle type="source" style={handleStyle} position={RF.Position.Top}>
              K
            </RF.Handle>
            <RF.Handle type="source" style={handleStyle} position={RF.Position.Top}>
              T
            </RF.Handle>
            <RF.Handle type="source" style={handleStyle} position={RF.Position.Top}>
              B
            </RF.Handle>
          </FlexItem>
        </Flex>
      )}
    </>
  );
}

const resizerControlStyle = {
  background: "transparent",
  border: "none",
};

export function ResizerHandle(props: {}) {
  return (
    <RF.NodeResizeControl
      style={resizerControlStyle}
      minWidth={MIN_SIZE_FOR_NODES.width}
      minHeight={MIN_SIZE_FOR_NODES.height}
    >
      <div
        style={{
          position: "absolute",
          top: "-10px",
          left: "-10px",
          width: "12px",
          height: "12px",
          backgroundColor: "black",
          clipPath: "polygon(0 100%, 100% 100%, 100% 0)",
          borderRadius: "4px",
        }}
      />
    </RF.NodeResizeControl>
  );
}

export function EditExpressionButton(props: { isVisible: boolean; onClick: () => void }) {
  return (
    <>
      {props.isVisible && (
        <Label onClick={props.onClick} className={"kie-dmn-editor--edit-expression-label"}>
          Edit
        </Label>
      )}
    </>
  );
}

export function InfoButton(props: { isVisible: boolean; onClick: () => void }) {
  return (
    <>
      {props.isVisible && (
        <div className={"kie-dmn-editor--info-label-toolbar"}>
          <Label onClick={props.onClick} className={"kie-dmn-editor--info-label"}>
            <InfoIcon style={{ width: "0.7em", height: "0.7em" }} />
          </Label>
        </div>
      )}
    </>
  );
}

export function useHoveredInfo(ref: React.RefObject<HTMLElement>) {
  const [isHovered, setHovered] = React.useState(false);

  useEffect(() => {
    function onEnter(e: MouseEvent) {
      setHovered(true);
    }

    function onLeave() {
      setHovered(false);
    }

    const r = ref.current;

    r?.addEventListener("mouseenter", onEnter);
    r?.addEventListener("mouseleave", onLeave);
    return () => {
      r?.removeEventListener("mouseleave", onLeave);
      r?.removeEventListener("mouseenter", onEnter);
    };
  }, [ref]);

  return isHovered;
}

export function InputDataNode({
  data: { inputData, shape, onInfo },
  selected,
}: RF.NodeProps<{ inputData: DMN14__tInputData; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  useEffect(() => {
    ref.current!.parentElement!.style.zIndex = `${isHovered ? 200 : selected ? 100 : 10}`;
  }, [selected, isHovered]);

  return (
    <>
      <NsweHandles />
      {/* <DataTypeToolbar variable={inputData.variable} shape={shape} /> */}
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--input-data-node"}>
        <OutgoingStuffToolbar isVisible={isHovered || selected} />
        <InfoButton isVisible={isHovered || selected} onClick={onInfo} />
        {inputData["@_label"] ?? inputData["@_name"] ?? <EmptyLabel />}
      </div>
      {selected && <ResizerHandle />}
    </>
  );
}

export function DecisionNode({
  data: { decision, shape, setOpenNodeWithExpression, onInfo },
  selected,
}: RF.NodeProps<{
  decision: DMN14__tDecision;
  shape: DMNDI13__DMNShape;
  setOpenNodeWithExpression: React.Dispatch<React.SetStateAction<DmnNodeWithExpression | undefined>>;
  onInfo: () => void;
}>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  useEffect(() => {
    ref.current!.parentElement!.style.zIndex = `${isHovered ? 200 : selected ? 100 : 10}`;
  }, [selected, isHovered]);

  return (
    <>
      <NsweHandles />
      {/* <DataTypeToolbar variable={decision.variable} shape={shape} /> */}
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--decision-node"}>
        <EditExpressionButton
          isVisible={isHovered || selected}
          onClick={() => setOpenNodeWithExpression({ type: "decision", content: decision })}
        />
        <OutgoingStuffToolbar isVisible={isHovered || selected} />
        <InfoButton isVisible={isHovered || selected} onClick={onInfo} />
        {decision["@_label"] ?? decision["@_name"] ?? <EmptyLabel />}
      </div>
      {selected && <ResizerHandle />}
    </>
  );
}

export function BkmNode({
  data: { bkm, shape, setOpenNodeWithExpression, onInfo },
  selected,
}: RF.NodeProps<{
  bkm: DMN14__tBusinessKnowledgeModel;
  shape: DMNDI13__DMNShape;
  onInfo: () => void;
  setOpenNodeWithExpression: React.Dispatch<React.SetStateAction<DmnNodeWithExpression | undefined>>;
}>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  useEffect(() => {
    ref.current!.parentElement!.style.zIndex = `${isHovered ? 200 : selected ? 100 : 10}`;
  }, [selected, isHovered]);

  return (
    <>
      <NsweHandles />
      <OutgoingStuffToolbar isVisible={isHovered || selected} />
      {/* <DataTypeToolbar variable={bkm.variable} shape={shape} /> */}
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--bkm-node"}>
        <EditExpressionButton
          isVisible={isHovered || selected}
          onClick={() => setOpenNodeWithExpression({ type: "bkm", content: bkm })}
        />
        <OutgoingStuffToolbar isVisible={isHovered || selected} />
        <InfoButton isVisible={isHovered || selected} onClick={onInfo} />
        {bkm["@_label"] ?? bkm["@_name"] ?? <EmptyLabel />}
      </div>
      {selected && <ResizerHandle />}
    </>
  );
}

export function TextAnnotationNode({
  data: { textAnnotation, shape, onInfo },
  selected,
}: RF.NodeProps<{ textAnnotation: DMN14__tTextAnnotation; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  useEffect(() => {
    ref.current!.parentElement!.style.zIndex = `${isHovered ? 200 : selected ? 100 : 10}`;
  }, [selected, isHovered]);

  return (
    <>
      <NsweHandles />
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--text-annotation-node"}>
        <OutgoingStuffToolbar isVisible={isHovered || selected} />
        <InfoButton isVisible={isHovered || selected} onClick={onInfo} />
        {textAnnotation["@_label"] ?? textAnnotation.text ?? <EmptyLabel />}
      </div>
      {selected && <ResizerHandle />}
    </>
  );
}

export function DecisionServiceNode({
  data: { decisionService, shape, onInfo },
  selected,
}: RF.NodeProps<{ decisionService: DMN14__tDecisionService; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  return (
    <>
      <NsweHandles />
      {selected && <ResizerHandle />}
      {/* <DataTypeToolbar variable={decisionService.variable} shape={shape} /> */}
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--decision-service-node"}>
        <OutgoingStuffToolbar isVisible={isHovered || selected} />
        <InfoButton isVisible={isHovered || selected} onClick={onInfo} />
        {decisionService["@_label"] ?? decisionService["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function KnowledgeSourceNode({
  data: { knowledgeSource, shape, onInfo },
  selected,
}: RF.NodeProps<{ knowledgeSource: DMN14__tKnowledgeSource; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  useEffect(() => {
    ref.current!.parentElement!.style.zIndex = `${isHovered ? 200 : selected ? 100 : 10}`;
  }, [selected, isHovered]);

  return (
    <>
      <NsweHandles />
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--knowledge-source-node"}>
        <OutgoingStuffToolbar isVisible={isHovered || selected} />
        <InfoButton isVisible={isHovered || selected} onClick={onInfo} />
        {knowledgeSource["@_label"] ?? knowledgeSource["@_name"] ?? <EmptyLabel />}
      </div>
      {selected && <ResizerHandle />}
    </>
  );
}

export function GroupNode({
  data: { group, shape },
  selected,
}: RF.NodeProps<{ group: DMN14__tGroup; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <div className={"kie-dmn-editor--node kie-dmn-editor--group-node"}>
        {group["@_label"] ?? group["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export const generateUuid = () => {
  return `_${uuid()}`.toLocaleUpperCase();
};
