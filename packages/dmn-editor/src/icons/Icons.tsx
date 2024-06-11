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

export type NodeIcons =
  | { isAlternativeInputDataShape: boolean; nodeType: typeof NODE_TYPES.inputData }
  | {
      isAlternativeInputDataShape?: boolean;
      nodeType: Exclude<NodeType, typeof NODE_TYPES.inputData>;
    };

export function RoundSvg({
  children,
  padding,
  height,
  viewBox,
}: React.PropsWithChildren<{ padding?: string; height?: number; viewBox?: number }>) {
  const style = useMemo(
    () => (padding !== undefined ? { padding, height } : { padding: `${svgViewboxPadding}px`, height }),
    [padding, height]
  );

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

export function NodeIcon({ isAlternativeInputDataShape, nodeType }: NodeIcons) {
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

export function InputDataIcon(props: { padding?: string; height?: number }) {
  return (
    <RoundSvg padding={props.padding} height={props.height}>
      <InputDataNodeSvg {...nodeSvgProps} isCollection={false} />
    </RoundSvg>
  );
}

export function AlternativeInputDataIcon(props: {
  padding?: string;
  height?: number;
  viewBox?: number;
  transform?: string;
}) {
  return (
    <RoundSvg padding={props.padding ?? "0px"} height={props.height} viewBox={props.viewBox}>
      <AlternativeInputDataNodeSvg
        {...nodeSvgProps}
        isIcon={true}
        width={80}
        height={100}
        strokeWidth={10}
        transform={props.transform ?? "translate(80, 60)"}
        isCollection={false}
      />
    </RoundSvg>
  );
}

export function DecisionIcon() {
  return (
    <RoundSvg>
      <DecisionNodeSvg {...nodeSvgProps} hasHiddenRequirements={false} isCollection={false} />
    </RoundSvg>
  );
}
export function BkmIcon() {
  return (
    <RoundSvg>
      <BkmNodeSvg {...nodeSvgProps} hasHiddenRequirements={false} />
    </RoundSvg>
  );
}
export function KnowledgeSourceIcon() {
  return (
    <RoundSvg>
      <KnowledgeSourceNodeSvg {...nodeSvgProps} hasHiddenRequirements={false} />
    </RoundSvg>
  );
}
export function DecisionServiceIcon() {
  return (
    <RoundSvg>
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
