import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDecisionService,
  DMN14__tGroup,
  DMN14__tInformationItem,
  DMN14__tInputData,
  DMN14__tKnowledgeSource,
  DMN14__tTextAnnotation,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import * as React from "react";
import { useCallback, useEffect } from "react";
import * as RF from "reactflow";
import { ConnectionTargetHandles } from "../connections/ConnectionTargetHandles";
import { outgoing } from "../connections/graphStructure";

import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { MIN_SIZE_FOR_NODES } from "../SnapGrid";
import { DmnNodeWithExpression } from "./DmnNodeWithExpression";
import { NODE_TYPES } from "./NodeTypes";
import { EDGE_TYPES } from "../edges/EdgeTypes";

export function InputDataNode({
  data: { inputData, shape, onInfo },
  selected,
  id,
}: RF.NodeProps<{ inputData: DMN14__tInputData; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  useEffect(() => {
    ref.current!.parentElement!.style.zIndex = `${isHovered ? 200 : selected ? 100 : 10}`;
  }, [selected, isHovered]);

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget);

  return (
    <>
      <ConnectionTargetHandles isTargeted={isTargeted && isValidTarget} />

      {/* <DataTypeToolbar variable={inputData.variable} shape={shape} /> */}
      <div ref={ref} className={`kie-dmn-editor--node kie-dmn-editor--input-data-node ${className}`}>
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && (isHovered || selected)}
          nodes={outgoing.inputData.nodes}
          edges={outgoing.inputData.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
        {inputData["@_label"] ?? inputData["@_name"] ?? <EmptyLabel />}
      </div>
      {selected && <NodeResizerHandle />}
    </>
  );
}

export function DecisionNode({
  data: { decision, shape, setOpenNodeWithExpression, onInfo },
  selected,
  id,
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

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget);

  return (
    <>
      <ConnectionTargetHandles isTargeted={isTargeted && isValidTarget} />

      {/* <DataTypeToolbar variable={decision.variable} shape={shape} /> */}
      <div ref={ref} className={`kie-dmn-editor--node kie-dmn-editor--decision-node ${className}`}>
        <EditExpressionNodePanel
          isVisible={!isTargeted && (isHovered || selected)}
          onClick={() => setOpenNodeWithExpression({ type: NODE_TYPES.decision, content: decision })}
        />
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && (isHovered || selected)}
          nodes={outgoing.decision.nodes}
          edges={outgoing.decision.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
        {decision["@_label"] ?? decision["@_name"] ?? <EmptyLabel />}
      </div>
      {selected && <NodeResizerHandle />}
    </>
  );
}

export function BkmNode({
  data: { bkm, shape, setOpenNodeWithExpression, onInfo },
  selected,
  id,
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

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget);

  return (
    <>
      <ConnectionTargetHandles isTargeted={isTargeted && isValidTarget} />

      {/* <DataTypeToolbar variable={bkm.variable} shape={shape} /> */}
      <div ref={ref} className={`kie-dmn-editor--node kie-dmn-editor--bkm-node ${className}`}>
        <EditExpressionNodePanel
          isVisible={!isTargeted && (isHovered || selected)}
          onClick={() => setOpenNodeWithExpression({ type: NODE_TYPES.bkm, content: bkm })}
        />
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && (isHovered || selected)}
          nodes={outgoing.bkm.nodes}
          edges={outgoing.bkm.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
        {bkm["@_label"] ?? bkm["@_name"] ?? <EmptyLabel />}
      </div>
      {selected && <NodeResizerHandle />}
    </>
  );
}

export function KnowledgeSourceNode({
  data: { knowledgeSource, shape, onInfo },
  selected,
  id,
}: RF.NodeProps<{ knowledgeSource: DMN14__tKnowledgeSource; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  useEffect(() => {
    ref.current!.parentElement!.style.zIndex = `${isHovered ? 200 : selected ? 100 : 10}`;
  }, [selected, isHovered]);

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget);

  return (
    <>
      <ConnectionTargetHandles isTargeted={isTargeted && isValidTarget} />

      <div ref={ref} className={`kie-dmn-editor--node kie-dmn-editor--knowledge-source-node ${className}`}>
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && (isHovered || selected)}
          nodes={outgoing.knowledgeSource.nodes}
          edges={outgoing.knowledgeSource.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
        {knowledgeSource["@_label"] ?? knowledgeSource["@_name"] ?? <EmptyLabel />}
      </div>
      {selected && <NodeResizerHandle />}
    </>
  );
}

export function TextAnnotationNode({
  data: { textAnnotation, shape, onInfo },
  selected,
  id,
}: RF.NodeProps<{ textAnnotation: DMN14__tTextAnnotation; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  useEffect(() => {
    ref.current!.parentElement!.style.zIndex = `${isHovered ? 200 : selected ? 100 : 10}`;
  }, [selected, isHovered]);

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget);

  return (
    <>
      <ConnectionTargetHandles isTargeted={isTargeted && isValidTarget} />

      <div ref={ref} className={`kie-dmn-editor--node kie-dmn-editor--text-annotation-node ${className}`}>
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && (isHovered || selected)}
          nodes={outgoing.textAnnotation.nodes}
          edges={outgoing.textAnnotation.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
        {textAnnotation["@_label"] ?? textAnnotation.text ?? <EmptyLabel />}
      </div>
      {selected && <NodeResizerHandle />}
    </>
  );
}

export function DecisionServiceNode({
  data: { decisionService, shape, onInfo },
  selected,
  id,
}: RF.NodeProps<{ decisionService: DMN14__tDecisionService; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  const { isTargeted, isValidTarget, isConnecting } = useTargetStatus(id, isHovered);
  const className = useNodeClassName(isConnecting, isValidTarget);

  return (
    <>
      <ConnectionTargetHandles isTargeted={isTargeted && isValidTarget} />

      {selected && <NodeResizerHandle />}
      {/* <DataTypeToolbar variable={decisionService.variable} shape={shape} /> */}
      <div ref={ref} className={`kie-dmn-editor--node kie-dmn-editor--decision-service-node ${className}`}>
        <OutgoingStuffNodePanel
          isVisible={!isConnecting && !isTargeted && (isHovered || selected)}
          nodes={outgoing.decisionService.nodes}
          edges={outgoing.decisionService.edges}
        />
        <InfoNodePanel isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
        {decisionService["@_label"] ?? decisionService["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}

export function GroupNode({
  data: { group, shape },
  selected,
}: RF.NodeProps<{ group: DMN14__tGroup; shape: DMNDI13__DMNShape }>) {
  return (
    <>
      <div className={`kie-dmn-editor--node kie-dmn-editor--group-node`}>
        {group["@_label"] ?? group["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
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

export function OutgoingStuffNodePanel(props: { isVisible: boolean; nodes: string[]; edges: string[] }) {
  const style: React.CSSProperties = React.useMemo(
    () => ({
      visibility: props.isVisible ? undefined : "hidden",
    }),
    [props.isVisible]
  );

  return (
    <>
      <Flex className={"kie-dmn-editor--node-outgoing-stuff-toolbar"} style={style}>
        <FlexItem>
          {props.edges.map((e) => (
            <RF.Handle
              key={e}
              id={e}
              isConnectableEnd={false}
              type={"source"}
              style={handleStyle}
              position={RF.Position.Top}
            >
              {e.charAt(5)}
            </RF.Handle>
          ))}
        </FlexItem>

        <FlexItem>
          {props.nodes.map((n) => (
            <RF.Handle
              key={n}
              id={n}
              isConnectableEnd={false}
              type={"source"}
              style={handleStyle}
              position={RF.Position.Top}
            >
              {n.charAt(5)}
            </RF.Handle>
          ))}
        </FlexItem>
      </Flex>
    </>
  );
}

const resizerControlStyle = {
  background: "transparent",
  border: "none",
};

export function NodeResizerHandle(props: {}) {
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

export function EditExpressionNodePanel(props: { isVisible: boolean; onClick: () => void }) {
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

export function InfoNodePanel(props: { isVisible: boolean; onClick: () => void }) {
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

// Hooks

export function useTargetStatus(id: string, isHovered: boolean) {
  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const connectionHandleId = RF.useStore(useCallback((state) => state.connectionHandleId, []));
  const isValidConnection = RF.useStore(useCallback((state) => state.isValidConnection, []));
  const isTargeted = !!connectionNodeId && connectionNodeId !== id && isHovered;

  const isValidTarget =
    isValidConnection?.({
      source: connectionNodeId,
      target: id,
      sourceHandle: connectionHandleId,
      targetHandle: null, // We don't use targetHandles, as target handles are only different in position, not in semantic.
    }) ?? false;

  return { isTargeted, isValidTarget, isConnecting: !!connectionNodeId };
}

export function useNodeClassName(isConnecting: boolean, isValidTarget: boolean) {
  const connectionHandleId = RF.useStore(useCallback((state) => state.connectionHandleId, []));
  const isEdgeConnection = !!Object.values(EDGE_TYPES).find((s) => s === connectionHandleId);
  return isEdgeConnection && isConnecting && !isValidTarget ? "dimmed" : "normal";
}

//

export function InputDataNodeSvg(props: RF.Dimensions & RF.XYPosition) {
  const rx =
    typeof props.height === "number"
      ? props.height / 2
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  return <rect {...props} fill={"#fff"} stroke={"black"} strokeWidth={1.5} rx={rx} ry={"50%"} />;
}

export function DecisionNodeSvg(props: RF.Dimensions & RF.XYPosition) {
  return (
    <g>
      <rect {...props} fill={"#fff"} stroke={"black"} strokeWidth={1.5} />
    </g>
  );
}

export function BkmNodeSvg(props: RF.Dimensions & RF.XYPosition) {
  return (
    <polygon
      points={`20,0 0,20 0,${props.height} ${props.width - 20},${props.height} ${props.width},${props.height - 20}, ${
        props.width
      },0`}
      fill={"#fff"}
      stroke={"black"}
      strokeWidth={1.5}
      transform={`translate(${props.x},${props.y})`}
    />
  );
}

export function KnowledgeSourceNodeSvg(props: RF.Dimensions & RF.XYPosition) {
  const quarterX = props.width / 4;
  const halfX = props.width / 2;
  const amplitude = 20;
  return (
    <g>
      <path
        {...props}
        d={`M1,${props.height - 1 - amplitude / 2} L1,1 M1,1 L${props.width - 1},1 M${props.width - 1},1 L${
          props.width - 1
        },${props.height - 1 - amplitude / 2}`}
        stroke={"black"}
        strokeWidth={1.5}
        transform={`translate(${props.x},${props.y})`}
      />

      {/* <path d="M 0 80 Q 42.5 0, 85 80 T 170 80" stroke="black" fill="transparent"/> */}
      <path
        d={`M0,0 Q${quarterX},${amplitude} ${halfX},0 T${props.width},0`}
        stroke={"black"}
        fill={"transparent"}
        strokeWidth={1.5}
        transform={`translate(${props.x},${props.y - amplitude / 2 + props.height - 1})`}
      />
    </g>
  );
}

export function DecisionServiceNodeSvg(props: RF.Dimensions & RF.XYPosition & { dividerLineY: number }) {
  return (
    <g>
      <rect {...props} fill={"#fff"} stroke={"black"} strokeWidth={1.5} rx={40} ry={40} />
      <path d={`M0,${props.dividerLineY} L${props.width},${props.dividerLineY}`} />
    </g>
  );
}

export function TextAnnotationNodeSvg(props: RF.Dimensions & RF.XYPosition) {
  return (
    <path
      {...props}
      d={`M20,1 L1,1 M1,1 L1,${props.height - 1} M1,${props.height - 1} L20,${props.height - 1}`}
      stroke={"black"}
      strokeWidth={1.5}
      transform={`translate(${props.x},${props.y})`}
    />
  );
}

export function GroupNodeSvg(props: RF.Dimensions & RF.XYPosition) {
  return <rect {...props} fill={"#fff"} stroke={"black"} strokeWidth={1.5} />;
}
