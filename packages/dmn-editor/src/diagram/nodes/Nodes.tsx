import * as RF from "reactflow";
import * as React from "react";
import { useCallback, useEffect } from "react";
import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDecisionService,
  DMN14__tGroup,
  DMN14__tInputData,
  DMN14__tKnowledgeSource,
  DMN14__tTextAnnotation,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { NsweHandles } from "../edges/NsweHandles";
import {
  bkmOutgoing,
  decisionOutgoing,
  decisionServiceOutgoing,
  inputDataOutgoing,
  knowledgeSourceOutgoing,
  textAnnotationOutgoing,
} from "../edges/OutgoingHandleIds";
import {
  useHoveredInfo,
  OutgoingStuffToolbar,
  InfoButton,
  EmptyLabel,
  ResizerHandle,
  EditExpressionButton,
} from "../Diagram";
import { DmnNodeWithExpression } from "./DmnNodeWithExpression";
import { _checkIsValidConnection } from "./isValidConnection";

export function InputDataNode({
  data: { inputData, shape, onInfo },
  selected,
}: RF.NodeProps<{ inputData: DMN14__tInputData; shape: DMNDI13__DMNShape; onInfo: () => void }>) {
  const ref = React.useRef<HTMLDivElement>(null);
  const isHovered = useHoveredInfo(ref);

  useEffect(() => {
    ref.current!.parentElement!.style.zIndex = `${isHovered ? 200 : selected ? 100 : 10}`;
  }, [selected, isHovered]);

  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const isTargeted = !!connectionNodeId && connectionNodeId !== inputData["@_id"] && isHovered;

  return (
    <>
      <NsweHandles isTargeted={isTargeted} />
      {/* <DataTypeToolbar variable={inputData.variable} shape={shape} /> */}
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--input-data-node"}>
        <OutgoingStuffToolbar
          isVisible={!connectionNodeId && !isTargeted && (isHovered || selected)}
          nodes={inputDataOutgoing.nodes}
          edges={inputDataOutgoing.edges}
        />
        <InfoButton isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
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

  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const isTargeted = !!connectionNodeId && connectionNodeId !== decision["@_id"] && isHovered;

  return (
    <>
      <NsweHandles isTargeted={isTargeted} />
      {/* <DataTypeToolbar variable={decision.variable} shape={shape} /> */}
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--decision-node"}>
        <EditExpressionButton
          isVisible={!isTargeted && (isHovered || selected)}
          onClick={() => setOpenNodeWithExpression({ type: "decision", content: decision })}
        />
        <OutgoingStuffToolbar
          isVisible={!connectionNodeId && !isTargeted && (isHovered || selected)}
          nodes={decisionOutgoing.nodes}
          edges={decisionOutgoing.edges}
        />
        <InfoButton isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
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

  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const isTargeted = !!connectionNodeId && connectionNodeId !== bkm["@_id"] && isHovered;

  return (
    <>
      <NsweHandles isTargeted={isTargeted} />

      {/* <DataTypeToolbar variable={bkm.variable} shape={shape} /> */}
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--bkm-node"}>
        <EditExpressionButton
          isVisible={!isTargeted && (isHovered || selected)}
          onClick={() => setOpenNodeWithExpression({ type: "bkm", content: bkm })}
        />
        <OutgoingStuffToolbar
          isVisible={!connectionNodeId && !isTargeted && (isHovered || selected)}
          nodes={bkmOutgoing.nodes}
          edges={bkmOutgoing.edges}
        />
        <InfoButton isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
        {bkm["@_label"] ?? bkm["@_name"] ?? <EmptyLabel />}
      </div>
      {selected && <ResizerHandle />}
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

  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const isTargeted = !!connectionNodeId && connectionNodeId !== knowledgeSource["@_id"] && isHovered;

  return (
    <>
      <NsweHandles isTargeted={isTargeted} />
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--knowledge-source-node"}>
        <OutgoingStuffToolbar
          isVisible={!connectionNodeId && !isTargeted && (isHovered || selected)}
          nodes={knowledgeSourceOutgoing.nodes}
          edges={knowledgeSourceOutgoing.edges}
        />
        <InfoButton isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
        {knowledgeSource["@_label"] ?? knowledgeSource["@_name"] ?? <EmptyLabel />}
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

  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const isTargeted = !!connectionNodeId && connectionNodeId !== textAnnotation["@_id"] && isHovered;

  return (
    <>
      <NsweHandles isTargeted={isTargeted} />
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--text-annotation-node"}>
        <OutgoingStuffToolbar
          isVisible={!connectionNodeId && !isTargeted && (isHovered || selected)}
          nodes={textAnnotationOutgoing.nodes}
          edges={textAnnotationOutgoing.edges}
        />
        <InfoButton isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
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

  const connectionNodeId = RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const isTargeted = !!connectionNodeId && connectionNodeId !== decisionService["@_id"] && isHovered;

  return (
    <>
      <NsweHandles isTargeted={isTargeted} />
      {selected && <ResizerHandle />}
      {/* <DataTypeToolbar variable={decisionService.variable} shape={shape} /> */}
      <div ref={ref} className={"kie-dmn-editor--node kie-dmn-editor--decision-service-node"}>
        <OutgoingStuffToolbar
          isVisible={!connectionNodeId && !isTargeted && (isHovered || selected)}
          nodes={decisionServiceOutgoing.nodes}
          edges={decisionServiceOutgoing.edges}
        />
        <InfoButton isVisible={!isTargeted && (isHovered || selected)} onClick={onInfo} />
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
      <div className={"kie-dmn-editor--node kie-dmn-editor--group-node"}>
        {group["@_label"] ?? group["@_name"] ?? <EmptyLabel />}
      </div>
    </>
  );
}
