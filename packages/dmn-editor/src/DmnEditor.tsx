import * as React from "react";

import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import * as RF from "reactflow";
import "reactflow/dist/style.css";
import "./DmnEditor.css";

import { getMarshaller } from "@kie-tools/dmn-marshaller";
import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDecisionService,
  DMN14__tDefinitions,
  DMN14__tGroup,
  DMN14__tInputData,
  DMN14__tKnowledgeSource,
  DMN14__tTextAnnotation,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { v4 as uuid } from "uuid";

const EMPTY_DMN_14 = `<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="https://www.omg.org/spec/DMN/20211108/MODEL/">
</definitions>`;

const SNAP_GRID = {
  x: 20,
  y: 20,
};

export type DmnEditorRef = {
  getContent(): string;
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

  useEffect(() => {
    setNodes([
      // grouping
      ...(dmn.definitions.decisionService ?? []).map((decisionService) => {
        const shape = shapesById.get(decisionService["@_id"]!)!;
        return {
          id: decisionService["@_id"]!,
          type: "decisionService",
          position: getShapePosition(shape),
          data: { decisionService, shape },
        };
      }),
      ...(dmn.definitions.group ?? []).map((group) => {
        const shape = shapesById.get(group["@_id"]!)!;
        return {
          id: group["@_id"]!,
          type: "group",
          position: getShapePosition(shape),
          data: { group, shape },
        };
      }),

      //logic
      ...(dmn.definitions.inputData ?? []).map((inputData) => {
        const shape = shapesById.get(inputData["@_id"]!)!;
        return {
          id: inputData["@_id"]!,
          type: "inputData",
          position: getShapePosition(shape),
          data: { inputData, shape },
        };
      }),
      ...(dmn.definitions.decision ?? []).map((decision) => {
        const shape = shapesById.get(decision["@_id"]!)!;
        return {
          id: decision["@_id"]!,
          type: "decision",
          position: getShapePosition(shape),
          data: { decision, shape },
        };
      }),
      ...(dmn.definitions.businessKnowledgeModel ?? []).map((bkm) => {
        const shape = shapesById.get(bkm["@_id"]!)!;
        return {
          id: bkm["@_id"]!,
          type: "bkm",
          position: getShapePosition(shape),
          data: { bkm, shape },
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
        };
      }),
      ...(dmn.definitions.knowledgeSource ?? []).map((knowledgeSource) => {
        const shape = shapesById.get(knowledgeSource["@_id"]!)!;
        return {
          id: knowledgeSource["@_id"]!,
          type: "knowledgeSource",
          position: getShapePosition(shape),
          data: { knowledgeSource, shape },
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

  const onDragOver = useCallback((event) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = "move";
  }, []);

  const onDrop = useCallback(
    (e) => {
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

  return (
    <>
      <b>Version:</b> {marshaller.version}
      <div className={"kie-dmn-editor--diagram-container"} ref={rfContainer}>
        <RF.ReactFlow
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
          <Pallete />
          <PanWhenAltPressed />
          <RF.Background />
          <RF.Controls fitViewOptions={fitViewOptions} position={"bottom-right"} />
        </RF.ReactFlow>
      </div>
    </>
  );
});

export function Pallete() {
  const onDragStart = useCallback((event, nodeType) => {
    event.dataTransfer.setData("application/reactflow", nodeType);
    event.dataTransfer.effectAllowed = "move";
  }, []);

  return (
    <RF.Panel position={"top-left"}>
      <aside style={{ width: "40px" }}>
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
    <span>
      <i>{`(empty)`}</i>
    </span>
  );
}

export function InputDataNode({
  data: { inputData, shape },
}: RF.NodeProps<{ inputData: DMN14__tInputData; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <NsweHandles />
      <div className={"kie-dmn-editor--node kie-dmn-editor--input-data-node"} style={{ ...getShapeDimensions(shape) }}>
        {inputData["@_name"] ??
          inputData["@_label"] ??
          inputData.variable?.["@_label"] ??
          inputData.variable?.["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function DecisionNode({
  data: { decision, shape },
}: RF.NodeProps<{ decision: DMN14__tDecision; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <NsweHandles />
      <div className={"kie-dmn-editor--node kie-dmn-editor--decision-node"} style={{ ...getShapeDimensions(shape) }}>
        {decision["@_name"] ??
          decision["@_label"] ??
          decision.variable?.["@_label"] ??
          decision.variable?.["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function BkmNode({
  data: { bkm, shape },
}: RF.NodeProps<{ bkm: DMN14__tBusinessKnowledgeModel; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <NsweHandles />
      <div
        style={{ ...getShapeDimensions(shape) }}
        className={"kie-dmn-editor--node kie-dmn-editor--bkm-node"}
        onClick={() => {}}
      >
        {bkm["@_name"] ?? bkm["@_label"] ?? bkm.variable?.["@_label"] ?? bkm.variable?.["@_name"] ?? <EmptyLabel />}
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
      <div
        style={{ ...getShapeDimensions(shape) }}
        className={"kie-dmn-editor--node kie-dmn-editor--decision-service-node"}
      >
        {decisionService["@_label"] ?? decisionService["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function GroupNode({
  data: { group, shape },
}: RF.NodeProps<{ group: DMN14__tGroup; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <NsweHandles />
      <div style={{ ...getShapeDimensions(shape) }} className={"kie-dmn-editor--node kie-dmn-editor--group-node"}>
        {group["@_label"] ?? group["@_name"] ?? <EmptyLabel />}
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
      <div
        style={{ ...getShapeDimensions(shape) }}
        className={"kie-dmn-editor--node kie-dmn-editor--knowledge-source-node"}
      >
        {knowledgeSource["@_label"] ?? knowledgeSource["@_name"] ?? <EmptyLabel />}
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
