import "@patternfly/react-core/dist/styles/base.css";
import "reactflow/dist/style.css";
import * as React from "react";

import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import * as RF from "reactflow";

import { getMarshaller } from "@kie-tools/dmn-marshaller";
import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDecisionService,
  DMN14__tDefinitions,
  DMN14__tFunctionDefinition,
  DMN14__tGroup,
  DMN14__tInformationItem,
  DMN14__tInputData,
  DMN14__tItemDefinition,
  DMN14__tKnowledgeSource,
  DMN14__tTextAnnotation,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { v4 as uuid } from "uuid";
import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/dist/expressions";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { InfoAltIcon } from "@patternfly/react-icons/dist/js/icons/info-alt-icon";
import { BarsIcon } from "@patternfly/react-icons/dist/js/icons/bars-icon";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Tab, TabTitleText, Tabs } from "@patternfly/react-core/dist/js/components/Tabs";

import "./DmnEditor.css"; // Leave it for last, as this overrides some of the PF and RF styles.
import {
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
} from "@kie-tools/boxed-expression-component/dist/api";
import exp = require("constants");

const EMPTY_DMN_14 = `<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="https://www.omg.org/spec/DMN/20211108/MODEL/">
</definitions>`;

const SNAP_GRID = {
  x: 20,
  y: 20,
};

export enum DmnEditorTab {
  EDITOR,
  DATA_TYPES,
  INCLUDED_MODELS,
  DOCUMENTATION,
}

export type DmnEditorRef = {
  getContent(): string;
};

export type DmnExpression =
  | {
      type: "bkm";
      expression: DMN14__tBusinessKnowledgeModel;
    }
  | {
      type: "decision";
      expression: DMN14__tDecision;
    };

export const DmnEditor = React.forwardRef((props: { xml: string }, ref: React.Ref<DmnEditorRef>) => {
  const marshaller = useMemo(() => getMarshaller(props.xml.trim() || EMPTY_DMN_14), [props.xml]);

  const dmnInitial: { definitions: DMN14__tDefinitions } = useMemo(
    () => marshaller.parser.parse() as { definitions: DMN14__tDefinitions }, // FIXME: Casting to the latest version, but... what should we do?
    [marshaller.parser]
  );

  const [dmn, setDmn] = useState(dmnInitial);
  useEffect(() => {
    setDmn(dmnInitial);
  }, [dmnInitial]);

  useImperativeHandle(
    ref,
    () => ({
      getContent: () => marshaller.builder.build(dmn),
    }),
    [dmn, marshaller.builder]
  );

  const [nodes, setNodes, onNodesChange] = RF.useNodesState([]);
  const [edges, setEdges, onEdgesChange] = RF.useEdgesState([]);

  const defaultViewport = useMemo(() => {
    return { x: 100, y: 0, zoom: 1 };
  }, []);

  const fitViewOptions = useMemo(() => {
    return { maxZoom: 1, minZoom: 1 };
  }, []);

  const snapGrid = useMemo<[number, number]>(() => {
    return [SNAP_GRID.x, SNAP_GRID.y];
  }, []);

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

  const shapesById = useMemo(
    () =>
      (dmn.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [])
        .flatMap((diagram) => diagram["dmndi:DMNShape"] ?? [])
        .reduce((acc, shape) => acc.set(shape["@_dmnElementRef"], shape), new Map<string, DMNDI13__DMNShape>()),
    [dmn.definitions]
  );

  const itemDefsById = useMemo(() => {
    return (dmn.definitions.itemDefinition ?? []).reduce(
      (acc, item) => acc.set(item["@_id"]!, item),
      new Map<string, DMN14__tItemDefinition>()
    );
  }, [dmn.definitions.itemDefinition]);

  const getShapePosition = useCallback(
    (shape: DMNDI13__DMNShape) => {
      // Without snapping at opening
      // return {
      //   x: shape["dc:Bounds"]?.["@_x"] ?? 0,
      //   y: shape["dc:Bounds"]?.["@_y"] ?? 0,
      // };

      // With snapping at opening
      return {
        x: Math.floor((shape["dc:Bounds"]?.["@_x"] ?? 0) / snapGrid[0]) * snapGrid[0],
        y: Math.floor((shape["dc:Bounds"]?.["@_y"] ?? 0) / snapGrid[1]) * snapGrid[1],
      };
    },
    [snapGrid]
  );

  const [openExpression, setOpenExpression] = useState<DmnExpression | undefined>(undefined);

  const expressionDefinition = useMemo<ExpressionDefinition>(
    () => (openExpression ? dmnExpressionToBee(itemDefsById, openExpression) : newExpressionDefinition()),
    [itemDefsById, openExpression]
  );

  const dataTypes = useMemo(
    () =>
      [...itemDefsById.values()].map((item) => ({
        isCustom: true,
        typeRef: item.typeRef!,
        name: item["@_name"]!,
      })),
    [itemDefsById]
  );

  useEffect(() => {
    setNodes([
      //logic
      ...(dmn.definitions.inputData ?? []).map((inputData) => {
        const shape = shapesById.get(inputData["@_id"]!)!;
        return {
          id: inputData["@_id"]!,
          type: "inputData",
          position: getShapePosition(shape),
          data: { inputData, shape },
          style: { zIndex: 100 },
        };
      }),
      ...(dmn.definitions.decision ?? []).map((decision) => {
        const shape = shapesById.get(decision["@_id"]!)!;
        return {
          id: decision["@_id"]!,
          type: "decision",
          position: getShapePosition(shape),
          data: { decision, shape, setOpenExpression },
          style: { zIndex: 100 },
        };
      }),
      ...(dmn.definitions.businessKnowledgeModel ?? []).map((bkm) => {
        const shape = shapesById.get(bkm["@_id"]!)!;
        return {
          id: bkm["@_id"]!,
          type: "bkm",
          position: getShapePosition(shape),
          data: { bkm, shape, setOpenExpression },
          style: { zIndex: 100 },
        };
      }),

      // info
      ...(dmn.definitions.textAnnotation ?? []).map((textAnnotation) => {
        const shape = shapesById.get(textAnnotation["@_id"]!)!;
        return {
          id: textAnnotation["@_id"]!,
          type: "textAnnotation",
          position: getShapePosition(shape),
          data: { textAnnotation, shape },
          style: { zIndex: 100 },
        };
      }),
      ...(dmn.definitions.knowledgeSource ?? []).map((knowledgeSource) => {
        const shape = shapesById.get(knowledgeSource["@_id"]!)!;
        return {
          id: knowledgeSource["@_id"]!,
          type: "knowledgeSource",
          position: getShapePosition(shape),
          data: { knowledgeSource, shape },
          style: { zIndex: 100 },
        };
      }),

      // grouping
      ...(dmn.definitions.decisionService ?? []).map((decisionService) => {
        const shape = shapesById.get(decisionService["@_id"]!)!;
        return {
          id: decisionService["@_id"]!,
          type: "decisionService",
          position: getShapePosition(shape),
          data: { decisionService, shape },
          style: { zIndex: 10 },
        };
      }),
      ...(dmn.definitions.group ?? []).map((group) => {
        const shape = shapesById.get(group["@_id"]!)!;
        return {
          id: group["@_id"]!,
          type: "group",
          position: getShapePosition(shape),
          data: { group, shape },
          style: { zIndex: 10 },
        };
      }),
    ]);
  }, [
    dmn.definitions.businessKnowledgeModel,
    dmn.definitions.decision,
    dmn.definitions.decisionService,
    dmn.definitions.group,
    dmn.definitions.inputData,
    dmn.definitions.knowledgeSource,
    dmn.definitions.textAnnotation,
    getShapePosition,
    setNodes,
    shapesById,
  ]);

  useEffect(() => {
    const markerEnd = {
      width: 20,
      height: 20,
      type: RF.MarkerType.ArrowClosed,
      color: "black",
    };

    setEdges([
      // information requirement
      ...(dmn.definitions.decision ?? []).flatMap((decision) => [
        ...(decision.informationRequirement ?? []).map((ir) => {
          const source = (ir.requiredDecision?.["@_href"] ?? ir.requiredInput?.["@_href"] ?? "#").substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
          const target = decision["@_id"]!;
          return {
            id: ir["@_id"] ?? "",
            type: "informationRequirement",
            source,
            target,
            markerEnd,
          };
        }),
      ]),

      // knowledge requirement
      ...[...(dmn.definitions.decision ?? []), ...(dmn.definitions.businessKnowledgeModel ?? [])].flatMap((node) => [
        ...(node.knowledgeRequirement ?? []).map((kr) => {
          const source = (kr.requiredKnowledge?.["@_href"] ?? "#").substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
          const target = node["@_id"]!;
          return {
            id: kr["@_id"] ?? "",
            type: "knowledgeRequirement",
            source,
            target,
            markerEnd,
          };
        }),
      ]),

      // authority requirement
      ...[
        ...(dmn.definitions.decision ?? []),
        ...(dmn.definitions.businessKnowledgeModel ?? []),
        ...(dmn.definitions.knowledgeSource ?? []),
      ].flatMap((node) => [
        ...(node.authorityRequirement ?? []).map((ar) => {
          const source = (
            ar.requiredInput?.["@_href"] ??
            ar.requiredDecision?.["@_href"] ??
            ar.requiredAuthority?.["@_href"] ??
            "#"
          ).substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
          const target = node["@_id"]!;
          return {
            id: ar["@_id"] ?? "",
            type: "authorityRequirement",
            source,
            target,
            markerEnd,
          };
        }),
      ]),

      // association
      ...(dmn.definitions.association ?? []).map((node) => {
        const source = (node.sourceRef?.["@_href"] ?? "#").substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
        const target = (node.targetRef?.["@_href"] ?? "#").substring(1); // Remove a "#" that is added at the beginning of IDs on @_href's
        return {
          id: node["@_id"] ?? "",
          type: "association",
          source,
          target,
          markerEnd,
        };
      }),
    ]);
  }, [
    dmn.definitions.association,
    dmn.definitions.businessKnowledgeModel,
    dmn.definitions.decision,
    dmn.definitions.knowledgeSource,
    setEdges,
  ]);

  const _onNodesChange = useCallback<typeof onNodesChange>(
    (changes) => {
      for (const change of changes) {
        if (change.type === "position") {
          setDmn((prev) => {
            if (!change.position) {
              return prev;
            }

            const newDiagrams = [...(prev.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [])];
            const newShapes = [...(newDiagrams[0]?.["dmndi:DMNShape"] ?? [])];

            const shapeIndex = newShapes.findIndex(({ "@_dmnElementRef": ref }) => ref === change.id);
            if (shapeIndex < 0) {
              return prev;
            }

            newDiagrams[0]["dmndi:DMNShape"] = newShapes;
            newShapes[shapeIndex] = {
              ...newShapes[shapeIndex],
              "dc:Bounds": {
                ...newShapes[shapeIndex]["dc:Bounds"]!,
                "@_x": change.position.x,
                "@_y": change.position.y,
              },
            };

            return {
              ...prev,
              definitions: {
                ...prev.definitions,
                "dmndi:DMNDI": {
                  "dmndi:DMNDiagram": newDiagrams,
                },
              },
            };
          });
        }
      }

      return onNodesChange(changes);
    },
    [onNodesChange]
  );

  const _onEdgesChange = useCallback<typeof onEdgesChange>(
    (changes) => {
      for (const change of changes) {
        if (change.type === "add") {
          //
        }
      }
      return onEdgesChange(changes);
    },
    [onEdgesChange]
  );

  const rfContainer = useRef<HTMLDivElement>(null);
  const [reactFlowInstance, setReactFlowInstance] = useState<RF.ReactFlowInstance | undefined>(undefined);

  const onDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = "move";
  }, []);

  const onDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();

      if (!rfContainer.current || !reactFlowInstance) {
        return;
      }

      const type = e.dataTransfer.getData("application/reactflow");
      if (typeof type === "undefined" || !type) {
        return;
      }

      const rfBounds = rfContainer.current.getBoundingClientRect();
      const position = reactFlowInstance.project({
        x: e.clientX - rfBounds.left,
        y: e.clientY - rfBounds.top,
      });

      const id = generateUuid();

      setDmn((prev) => {
        // TODO: Implement this
        return { ...prev };
      });

      console.info(`Adding node of type '${type}' at position '${position.x},${position.y}'.`);
    },
    [reactFlowInstance]
  );

  const [tab, setTab] = useState(DmnEditorTab.EDITOR);
  const onTabChanged = useCallback((e, tab) => {
    setTab(tab);
  }, []);

  return (
    <>
      <>
        <Tabs activeKey={tab} onSelect={onTabChanged} isBox={true} role="region">
          <Tab eventKey={DmnEditorTab.EDITOR} title={<TabTitleText>Editor</TabTitleText>}>
            <>
              <div className={"kie-dmn-editor--diagram-container"} ref={rfContainer}>
                {!openExpression && (
                  <>
                    <Label style={{ position: "absolute", bottom: "8px" }}>{`DMN ${marshaller.version}`}</Label>
                    <RF.ReactFlow
                      zoomOnDoubleClick={false}
                      elementsSelectable={true}
                      nodes={nodes}
                      edges={edges}
                      panOnScroll={true}
                      selectionOnDrag={true}
                      panOnDrag={panOnDrag}
                      panActivationKeyCode={"Alt"}
                      selectionMode={RF.SelectionMode.Partial}
                      onNodesChange={onNodesChange} // FIXME: Selection is getting lost when dragging if I change to _onNodesChange.
                      onEdgesChange={onEdgesChange}
                      nodeTypes={nodeTypes}
                      edgeTypes={edgeTypes}
                      snapToGrid={true}
                      snapGrid={snapGrid}
                      defaultViewport={defaultViewport}
                      fitView={false}
                      fitViewOptions={fitViewOptions}
                      attributionPosition={"bottom-right"}
                      onInit={setReactFlowInstance}
                      onDrop={onDrop}
                      onDragOver={onDragOver}
                    >
                      <Status />
                      <Pallete />
                      <PanWhenAltPressed />
                      <RF.Background />
                      <RF.Controls fitViewOptions={fitViewOptions} position={"bottom-right"} />
                    </RF.ReactFlow>
                  </>
                )}
                {openExpression && (
                  <>
                    <br />
                    <Button
                      isSmall={true}
                      variant={ButtonVariant.tertiary}
                      onClick={() => setOpenExpression(undefined)}
                    >{`Back`}</Button>
                    <br />
                    <br />
                    <>
                      <BoxedExpressionEditor
                        decisionNodeId={openExpression.expression["@_id"]!}
                        expressionDefinition={expressionDefinition}
                        setExpressionDefinition={function (value: React.SetStateAction<ExpressionDefinition>): void {
                          throw new Error("Function not implemented.");
                        }}
                        dataTypes={dataTypes}
                        scrollableParentRef={rfContainer}
                      />
                    </>
                    <>{openExpression.expression["@_id"]}</>
                  </>
                )}
              </div>
            </>
          </Tab>
          <Tab eventKey={DmnEditorTab.DATA_TYPES} title={<TabTitleText>Data types</TabTitleText>}>
            <>Data types</>
          </Tab>
          <Tab eventKey={DmnEditorTab.INCLUDED_MODELS} title={<TabTitleText>Included models</TabTitleText>}>
            <>Included models</>
          </Tab>
          <Tab eventKey={DmnEditorTab.DOCUMENTATION} title={<TabTitleText>Documentation</TabTitleText>}>
            <>Documentation</>
          </Tab>
        </Tabs>
      </>
    </>
  );
});

export function Status() {
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
      <aside style={{ width: "80px" }}>
        <div className="dndnode input-data" onDragStart={(event) => onDragStart(event, "inputData")} draggable>
          Input
        </div>
        <br />
        <div className="dndnode decision" onDragStart={(event) => onDragStart(event, "decision")} draggable>
          Decision
        </div>
        <br />
        <div className="dndnode bkm" onDragStart={(event) => onDragStart(event, "bkm")} draggable>
          BKM
        </div>
        <br />
        <div
          className="dndnode knowledge-source"
          onDragStart={(event) => onDragStart(event, "knowledgeSource")}
          draggable
        >
          Knowledge Source
        </div>
        <br />
        <div
          className="dndnode decision-service"
          onDragStart={(event) => onDragStart(event, "decisionService")}
          draggable
        >
          Decision Service
        </div>
        <br />
        <div
          className="dndnode text-annotation"
          onDragStart={(event) => onDragStart(event, "textAnnotation")}
          draggable
        >
          Text Annotation
        </div>
      </aside>
    </RF.Panel>
  );
}

const panOnDrag = [1, 2];

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

export function NewNodeToolbar(props: {}) {
  return (
    <RF.NodeToolbar position={RF.Position.Top} align={"center"}>
      <Label isCompact={true}>D</Label>
      <Label isCompact={true}>K</Label>
      <Label isCompact={true}>T</Label>
      <Label isCompact={true}>B</Label>
    </RF.NodeToolbar>
  );
}

export function OutgoingEdgesToolbar(props: {}) {
  return (
    <RF.NodeToolbar position={RF.Position.Right} align={"center"}>
      <Label isCompact={true}>I</Label>
      <br />
      <Label isCompact={true}>K</Label>
      <br />
      <Label isCompact={true}>A</Label>
      <br />
      <Label isCompact={true}>-</Label>
    </RF.NodeToolbar>
  );
}

export function InputDataNode({
  data: { inputData, shape },
}: RF.NodeProps<{ inputData: DMN14__tInputData; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <NsweHandles />
      <NewNodeToolbar />
      <OutgoingEdgesToolbar />
      {/* <DataTypeToolbar variable={inputData.variable} shape={shape} /> */}
      <InfoToolbar />
      <div className={"kie-dmn-editor--node kie-dmn-editor--input-data-node"} style={{ ...getShapeDimensions(shape) }}>
        {inputData["@_label"] ?? inputData["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function EditExpressionButton(props: { isVisible: boolean; onClick: () => void }) {
  const onClick = useCallback((e: React.MouseEvent) => {
    e.stopPropagation();
    e.preventDefault();
  }, []);

  return (
    <>
      {props.isVisible && (
        <Label
          onMouseDownCapture={onClick}
          onMouseUpCapture={onClick}
          onClick={props.onClick}
          className={"kie-dmn-editor--edit-expression-label"}
        >
          Edit
        </Label>
      )}
    </>
  );
}

export function useHoveredInfo(ref: React.RefObject<HTMLElement>) {
  const [isHovered, setHovered] = useState(false);

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

export function DecisionNode({
  data: { decision, shape, setOpenExpression },
}: RF.NodeProps<{
  decision: DMN14__tDecision;
  shape: DMNDI13__DMNShape;
  setOpenExpression: React.Dispatch<React.SetStateAction<DmnExpression | undefined>>;
}>) {
  const ref = useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  return (
    <>
      <NsweHandles />
      <NewNodeToolbar />
      <OutgoingEdgesToolbar />
      {/* <DataTypeToolbar variable={decision.variable} shape={shape} /> */}
      <InfoToolbar />
      <div
        ref={ref}
        className={"kie-dmn-editor--node kie-dmn-editor--decision-node"}
        style={{ ...getShapeDimensions(shape) }}
      >
        <EditExpressionButton
          isVisible={isHovered}
          onClick={() => setOpenExpression({ type: "decision", expression: decision })}
        />
        {decision["@_label"] ?? decision["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function BkmNode({
  data: { bkm, shape, setOpenExpression },
}: RF.NodeProps<{
  bkm: DMN14__tBusinessKnowledgeModel;
  shape: DMNDI13__DMNShape;
  setOpenExpression: React.Dispatch<React.SetStateAction<DmnExpression | undefined>>;
}>) {
  const ref = useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  return (
    <>
      <NsweHandles />
      <NewNodeToolbar />
      <OutgoingEdgesToolbar />
      {/* <DataTypeToolbar variable={bkm.variable} shape={shape} /> */}
      <InfoToolbar />
      <div
        ref={ref}
        className={"kie-dmn-editor--node kie-dmn-editor--bkm-node"}
        style={{ ...getShapeDimensions(shape) }}
      >
        <EditExpressionButton
          isVisible={isHovered}
          onClick={() => setOpenExpression({ type: "bkm", expression: bkm })}
        />
        {bkm["@_label"] ?? bkm["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function TextAnnotationNode({
  data: { textAnnotation, shape },
}: RF.NodeProps<{ textAnnotation: DMN14__tTextAnnotation; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <NsweHandles />
      <NewNodeToolbar />
      <OutgoingEdgesToolbar />
      <InfoToolbar />
      <div
        style={{ ...getShapeDimensions(shape) }}
        className={"kie-dmn-editor--node kie-dmn-editor--text-annotation-node"}
      >
        {textAnnotation["@_label"] ?? textAnnotation.text ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function DecisionServiceNode({
  data: { decisionService, shape },
}: RF.NodeProps<{ decisionService: DMN14__tDecisionService; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <NsweHandles />
      <NewNodeToolbar />
      <OutgoingEdgesToolbar />
      {/* <DataTypeToolbar variable={decisionService.variable} shape={shape} /> */}
      <InfoToolbar />
      <div
        style={{ ...getShapeDimensions(shape) }}
        className={"kie-dmn-editor--node kie-dmn-editor--decision-service-node"}
      >
        {decisionService["@_label"] ?? decisionService["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function KnowledgeSourceNode({
  data: { knowledgeSource, shape },
}: RF.NodeProps<{ knowledgeSource: DMN14__tKnowledgeSource; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <NsweHandles />
      <NewNodeToolbar />
      <OutgoingEdgesToolbar />
      <InfoToolbar />
      <div
        style={{ ...getShapeDimensions(shape) }}
        className={"kie-dmn-editor--node kie-dmn-editor--knowledge-source-node"}
      >
        {knowledgeSource["@_label"] ?? knowledgeSource["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function GroupNode({
  data: { group, shape },
}: RF.NodeProps<{ group: DMN14__tGroup; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <div style={{ ...getShapeDimensions(shape) }} className={"kie-dmn-editor--node kie-dmn-editor--group-node"}>
        {group["@_label"] ?? group["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function InformationRequirementEdge({ sourceX, sourceY, targetX, targetY, markerEnd }: RF.EdgeProps) {
  const [path] = RF.getStraightPath({ sourceX, sourceY, targetX, targetY });
  return <RF.BaseEdge path={path} markerEnd={markerEnd} style={{ strokeWidth: 1, stroke: "black" }} />;
}

export function AssociationEdge({ sourceX, sourceY, targetX, targetY, markerEnd }: RF.EdgeProps) {
  const [path] = RF.getStraightPath({ sourceX, sourceY, targetX, targetY });
  return (
    <RF.BaseEdge
      path={path}
      markerEnd={markerEnd}
      style={{ strokeDasharray: "2,10", strokeWidth: 1, stroke: "black" }}
    />
  );
}

export function AuthorityRequirementEdge({ sourceX, sourceY, targetX, targetY, markerEnd }: RF.EdgeProps) {
  const [path] = RF.getStraightPath({ sourceX, sourceY, targetX, targetY });
  return (
    <RF.BaseEdge
      path={path}
      markerEnd={markerEnd}
      style={{ strokeDasharray: "5,5", strokeWidth: 1, stroke: "black" }}
    />
  );
}

export function KnowledgeRequirementEdge({ sourceX, sourceY, targetX, targetY, markerEnd }: RF.EdgeProps) {
  const [path] = RF.getStraightPath({ sourceX, sourceY, targetX, targetY });
  return (
    <RF.BaseEdge
      path={path}
      markerEnd={markerEnd}
      style={{ strokeDasharray: "5,5", strokeWidth: 1, stroke: "black" }}
    />
  );
}

export function NsweHandles() {
  return (
    <>
      <RF.Handle
        id={"target-south"}
        type={"target"}
        position={RF.Position.Bottom}
        isConnectable={false}
        style={{ opacity: 0, margin: "4px" }}
      />
      <RF.Handle
        id={"sorce-north"}
        type={"source"}
        position={RF.Position.Top}
        isConnectable={false}
        style={{ opacity: 0, margin: "4px" }}
      />
    </>
  );
}

function getShapeDimensions(shape: DMNDI13__DMNShape) {
  // Without snapping at opening
  // return {
  //   width: shape["dc:Bounds"]?.["@_width"],
  //   height: shape["dc:Bounds"]?.["@_height"],
  // };

  // With snapping at opening
  return {
    width: Math.floor((shape["dc:Bounds"]?.["@_width"] ?? 0) / SNAP_GRID.x) * SNAP_GRID.x,
    height: Math.floor((shape["dc:Bounds"]?.["@_height"] ?? 0) / SNAP_GRID.y) * SNAP_GRID.y,
  };
}

export const generateUuid = () => {
  return `_${uuid()}`.toLocaleUpperCase();
};
function newNodeFromType(id: string, position: RF.XYPosition, type: string) {
  const defaultShape = {};
  switch (type) {
    case "inputData":
      return {
        inputData: {
          "@_id": id,
        },
        shape: defaultShape,
      };
    case "decision":
      return {
        decision: {
          "@_id": id,
        },
        shape: defaultShape,
      };
    case "bkm":
      return {
        bkm: {
          "@_id": id,
        },
        shape: defaultShape,
      };
    case "decisionService":
      return {
        decisionService: {
          "@_id": id,
        },
        shape: defaultShape,
      };
    case "knowledgeSource":
      return {
        knowledgeSource: {
          "@_id": id,
        },
        shape: defaultShape,
      };
    case "textAnnotation":
      return {
        textAnnotation: {
          "@_id": id,
        },
        shape: defaultShape,
      };
    case "group":
      return {
        group: {
          "@_id": id,
        },
        shape: defaultShape,
      };
  }
}

function newExpressionDefinition(): ExpressionDefinition {
  return {
    id: uuid(),
    logicType: ExpressionDefinitionLogicType.Undefined,
    dataType: DmnBuiltInDataType.Undefined,
  };
}

function dmnExpressionToBee(
  itemDefsById: Map<string, DMN14__tItemDefinition>,
  dmnExpression: DmnExpression
): ExpressionDefinition {
  if (dmnExpression.type == "bkm") {
    return exprToBee({ functionDefinition: dmnExpression.expression.encapsulatedLogic });
  } else if (dmnExpression.type == "decision") {
    return exprToBee(dmnExpression.expression);
  } else {
    throw new Error("Unknown DMN Expression type");
  }
}

function exprToBee(expr: DMN14__tDecision | DMN14__tFunctionDefinition | undefined): any {
  if (!expr) {
    return newExpressionDefinition();
  } else if (expr.literalExpression) {
    return {
      logicType: ExpressionDefinitionLogicType.Literal,
    };
  } else if (expr.decisionTable) {
    return {
      logicType: ExpressionDefinitionLogicType.DecisionTable,
    };
  } else if (expr.relation) {
    return {
      logicType: ExpressionDefinitionLogicType.Relation,
    };
  } else if (expr.context) {
    return {
      logicType: ExpressionDefinitionLogicType.Context,
    };
  } else if (expr.invocation) {
    return {
      logicType: ExpressionDefinitionLogicType.Invocation,
    };
  } else if (expr.functionDefinition) {
    const func = expr.functionDefinition;
    const basic = {
      id: func!["@_id"]!,
      dataType: DmnBuiltInDataType.Any,
      logicType: ExpressionDefinitionLogicType.Function as const,
      formalParameters: (func?.formalParameter ?? []).map((p) => ({
        id: p["@_id"]!,
        name: p["@_name"]!,
        dataType: p["@_typeRef"]! as DmnBuiltInDataType,
      })),
    };

    return (() => {
      switch (func!["@_kind"]) {
        case "FEEL":
          return {
            ...basic,
            functionKind: FunctionExpressionDefinitionKind.Feel,
            expression: exprToBee(func),
          };
        case "Java":
          return {
            ...basic,
            functionKind: FunctionExpressionDefinitionKind.Java,
            className: "",
            methodName: "",
            classFieldId: "",
            methodFieldId: "",
          };
        case "PMML":
          return {
            ...basic,
            functionKind: FunctionExpressionDefinitionKind.Pmml,
            document: "",
            model: "",
            documentFieldId: "",
            modelFieldId: "",
          };
        default:
          throw new Error("");
      }
    })();
  } else if (expr.list) {
    return {
      logicType: ExpressionDefinitionLogicType.List,
    };
  } else {
    return {
      logicType: ExpressionDefinitionLogicType.Undefined,
    };
  }
}
