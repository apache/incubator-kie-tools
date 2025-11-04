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
import {
  DataObjectNodeSvg,
  EndEventNodeSvg,
  GatewayNodeSvg,
  GroupNodeSvg,
  IntermediateCatchEventNodeSvg,
  IntermediateThrowEventNodeSvg,
  LaneNodeSvg,
  StartEventNodeSvg,
  TaskNodeSvg,
  TextAnnotationNodeSvg,
  SubProcessNodeSvg,
  EventVariantSymbolSvg,
  NODE_COLORS,
} from "./NodeSvgs";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { BpmnNodeType, EventVariant, GatewayVariant, SubProcessVariant, TaskVariant } from "../BpmnDiagramDomain";
import { NODE_TYPES } from "../BpmnDiagramDomain";
import { QuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/question-circle-icon";
import { nodeSvgProps, RoundSvg } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/NodeIcons";
import {
  BOUNDARY_EVENT_CANCEL_ACTIVITY_DEFAULT_VALUE,
  START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/Bpmn20Spec";

export function NodeIcon({ nodeType }: { nodeType: BpmnNodeType }) {
  return switchExpression(nodeType, {
    [NODE_TYPES.startEvent]: StartEventIcon,
    [NODE_TYPES.task]: TaskIcon,
    [NODE_TYPES.dataObject]: DataObjectIcon,
    [NODE_TYPES.textAnnotation]: TextAnnotationIcon,
    [NODE_TYPES.unknown]: UnknownIcon,
    default: () => <div>?</div>,
  });
}

export function EventDefinitionIcon({
  variant,
  filled,
  fill,
  stroke,
}: {
  variant?: EventVariant;
  filled: boolean;
  fill?: string;
  stroke: string;
}) {
  const cx = nodeSvgProps.x + nodeSvgProps.width / 2;
  const cy = nodeSvgProps.y + nodeSvgProps.height / 2;

  const r = nodeSvgProps.width / 2;

  return (
    <RoundSvg>
      <EventVariantSymbolSvg
        variant={variant ?? "none"}
        strokeWidth={16}
        isIcon={true}
        filled={filled}
        stroke={stroke}
        fill={fill}
        x={nodeSvgProps.x}
        y={nodeSvgProps.x}
        cx={cx}
        cy={cy}
        innerCircleRadius={r - 10}
        outerCircleRadius={r}
      />
    </RoundSvg>
  );
}

export function StartEventIcon({ variant }: { variant?: EventVariant }) {
  return (
    <RoundSvg>
      <StartEventNodeSvg
        {...nodeSvgProps}
        variant={variant ?? "none"}
        isInterrupting={START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE}
      />
    </RoundSvg>
  );
}

export function IntermediateCatchEventIcon({ variant }: { variant?: EventVariant }) {
  return (
    <RoundSvg>
      <IntermediateCatchEventNodeSvg
        {...nodeSvgProps}
        rimWidth={40}
        variant={variant ?? "none"}
        isInterrupting={BOUNDARY_EVENT_CANCEL_ACTIVITY_DEFAULT_VALUE}
      />
    </RoundSvg>
  );
}

export function IntermediateThrowEventIcon({ variant }: { variant?: EventVariant }) {
  return (
    <RoundSvg>
      <IntermediateThrowEventNodeSvg {...nodeSvgProps} rimWidth={40} variant={variant ?? "none"} />
    </RoundSvg>
  );
}

export function EndEventIcon({ variant }: { variant?: EventVariant }) {
  return (
    <RoundSvg>
      <EndEventNodeSvg {...nodeSvgProps} variant={variant ?? "none"} />
    </RoundSvg>
  );
}

export function TaskIcon({ variant, isIcon }: { variant?: TaskVariant; isIcon?: boolean }) {
  return (
    <RoundSvg>
      <TaskNodeSvg {...nodeSvgProps} variant={variant ?? "none"} isIcon={isIcon ?? false} />
    </RoundSvg>
  );
}

export function CallActivityIcon() {
  return (
    <RoundSvg>
      <TaskNodeSvg {...nodeSvgProps} markers={["CallActivityPaletteIcon"]} variant={"none"} />
    </RoundSvg>
  );
}

export function GatewayIcon({ variant, isIcon }: { variant?: GatewayVariant; isIcon?: boolean }) {
  return (
    <RoundSvg>
      <GatewayNodeSvg {...nodeSvgProps} width={200} height={200} variant={variant ?? "none"} isIcon={isIcon ?? false} />
    </RoundSvg>
  );
}

export function LaneIcon() {
  return (
    <RoundSvg>
      <LaneNodeSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function SubProcessIcon({ variant }: { variant?: SubProcessVariant }) {
  return (
    <RoundSvg>
      <SubProcessNodeSvg
        {...nodeSvgProps}
        strokeWidth={20}
        rimWidth={20}
        borderRadius={20}
        variant={variant ?? "other"}
      />
    </RoundSvg>
  );
}

export function DataObjectIcon(props: { padding?: string; height?: number; viewBox?: number; transform?: string }) {
  return (
    <RoundSvg padding={props.padding ?? "0px"} height={props.height} viewBox={props.viewBox}>
      <DataObjectNodeSvg
        {...nodeSvgProps}
        showArrow={false}
        showFoldedPage={false}
        isIcon={true}
        width={80}
        height={100}
        strokeWidth={10}
        transform={props.transform ?? "translate(80, 60)"}
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

export function UnknownNodeIcon() {
  return (
    <RoundSvg>
      <QuestionCircleIcon width={"100%"} height={"100%"} />
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
