import * as React from "react";
import {
  BkmNodeSvg,
  DecisionNodeSvg,
  DecisionServiceNodeSvg,
  GroupNodeSvg,
  InputDataNodeSvg,
  KnowledgeSourceNodeSvg,
  TextAnnotationNodeSvg,
} from "../diagram/nodes/NodeSvgs";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { QuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/question-circle-icon";

const radius = 34;
const svgViewboxPadding = Math.sqrt(Math.pow(radius, 2) / 2) - radius / 2; // This lets us create a square that will perfectly fit inside the button circle.

const nodeSvgProps = { width: 200, height: 120, x: 16, y: 48, strokeWidth: 16 };
const nodeSvgViewboxSize = nodeSvgProps.width + 2 * nodeSvgProps.strokeWidth;

export function RoundSvg({ children }: React.PropsWithChildren<{}>) {
  return (
    <svg
      className={"kie-dmn-editor--round-svg-container"}
      viewBox={`0 0 ${nodeSvgViewboxSize} ${nodeSvgViewboxSize}`}
      style={{ padding: `${svgViewboxPadding}px` }}
    >
      {children}
    </svg>
  );
}

export function NodeIcon(nodeType?: NodeType) {
  return switchExpression(nodeType, {
    [NODE_TYPES.inputData]: InputDataIcon,
    [NODE_TYPES.decision]: DecisionIcon,
    [NODE_TYPES.bkm]: BkmIcon,
    [NODE_TYPES.knowledgeSource]: KnowledgeSourceIcon,
    [NODE_TYPES.decisionService]: DecisionServiceIcon,
    [NODE_TYPES.group]: GroupIcon,
    [NODE_TYPES.textAnnotation]: TextAnnotationIcon,
    [NODE_TYPES.unknown]: UnknownIcon,
    default: () => <div>?</div>,
  });
}

export function InputDataIcon() {
  return (
    <RoundSvg>
      <InputDataNodeSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function DecisionIcon() {
  return (
    <RoundSvg>
      <DecisionNodeSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}
export function BkmIcon() {
  return (
    <RoundSvg>
      <BkmNodeSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}
export function KnowledgeSourceIcon() {
  return (
    <RoundSvg>
      <KnowledgeSourceNodeSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}
export function DecisionServiceIcon() {
  return (
    <RoundSvg>
      <DecisionServiceNodeSvg {...nodeSvgProps} y={12} height={nodeSvgProps.width} showSectionLabels={false} />
    </RoundSvg>
  );
}
export function GroupIcon() {
  return (
    <RoundSvg>
      <GroupNodeSvg {...nodeSvgProps} y={12} height={nodeSvgProps.width} strokeDasharray={"28,28"} />
    </RoundSvg>
  );
}
export function TextAnnotationIcon() {
  return (
    <RoundSvg>
      <TextAnnotationNodeSvg {...nodeSvgProps} showPlaceholder={true} />
    </RoundSvg>
  );
}
export function UnknownIcon() {
  return (
    <div style={{ display: "flex", alignItems: "center", justifyContent: "center", height: "100%" }}>
      <QuestionCircleIcon />
    </div>
  );
}
