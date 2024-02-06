/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useMemo } from "react";
import {
  AlternativeInputDataNodeSvg,
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

export function RoundSvg({
  children,
  hasPadding,
  height,
  viewBox,
}: React.PropsWithChildren<{ hasPadding?: boolean; height?: number; viewBox?: number }>) {
  const style = useMemo(() => (hasPadding ? { padding: `${svgViewboxPadding}px` } : { height }), [hasPadding, height]);

  const nodeSvgViewboxSize = useMemo(() => {
    return viewBox ?? nodeSvgProps.width + 2 * nodeSvgProps.strokeWidth;
  }, [viewBox]);

  return (
    <svg
      className={"kie-dmn-editor--round-svg-container"}
      viewBox={`0 0 ${nodeSvgViewboxSize} ${nodeSvgViewboxSize}`}
      style={style}
    >
      {children}
    </svg>
  );
}

export function NodeIcon({
  isAlternativeInputDataShape,
  nodeType,
}: {
  isAlternativeInputDataShape?: boolean;
  nodeType?: NodeType;
}) {
  return switchExpression(nodeType, {
    [NODE_TYPES.inputData]: isAlternativeInputDataShape ? AlternativeInputDataIcon : InputDataIcon,
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

export function InputDataIcon(props: { hasPadding?: boolean; height?: number }) {
  return (
    <RoundSvg hasPadding={props.hasPadding ?? true} height={props.height}>
      <InputDataNodeSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function AlternativeInputDataIcon(props: { height?: number; viewBox?: number; transform?: string }) {
  return (
    <RoundSvg hasPadding={false} height={props.height} viewBox={props.viewBox}>
      <AlternativeInputDataNodeSvg
        {...nodeSvgProps}
        isIcon={true}
        width={80}
        height={100}
        strokeWidth={8}
        transform={props.transform ?? "translate(80, 60)"}
      />
    </RoundSvg>
  );
}

export function DecisionIcon() {
  return (
    <RoundSvg hasPadding={true}>
      <DecisionNodeSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}
export function BkmIcon() {
  return (
    <RoundSvg hasPadding={true}>
      <BkmNodeSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}
export function KnowledgeSourceIcon() {
  return (
    <RoundSvg hasPadding={true}>
      <KnowledgeSourceNodeSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}
export function DecisionServiceIcon() {
  return (
    <RoundSvg hasPadding={true}>
      <DecisionServiceNodeSvg
        {...nodeSvgProps}
        y={12}
        height={nodeSvgProps.width}
        showSectionLabels={false}
        isReadonly={true}
      />
    </RoundSvg>
  );
}
export function GroupIcon() {
  return (
    <RoundSvg hasPadding={true}>
      <GroupNodeSvg {...nodeSvgProps} y={12} height={nodeSvgProps.width} strokeDasharray={"28,28"} />
    </RoundSvg>
  );
}
export function TextAnnotationIcon() {
  return (
    <RoundSvg hasPadding={true}>
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
