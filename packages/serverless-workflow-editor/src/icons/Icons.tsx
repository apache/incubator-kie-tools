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
  EventstateSvg,
  OperationstateSvg,
  SwitchstateSvg,
  SleepstateSvg,
  ParallelstateSvg,
  InjectstateSvg,
  ForeachstateSvg,
  CallbackstateSvg,
} from "../diagram/nodes/SwfNodeSvgs";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/SwfNodeTypes";
import { QuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/question-circle-icon";

const radius = 34;
const svgViewboxPadding = Math.sqrt(Math.pow(radius, 2) / 2) - radius / 2; // This lets us create a square that will perfectly fit inside the button circle.

const nodeSvgProps = { width: 200, height: 120, x: 16, y: 48, strokeWidth: 16 };

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
      className={"kie-swf-editor--round-svg-container"}
      viewBox={`0 0 ${nodeSvgViewboxSize} ${nodeSvgViewboxSize}`}
      style={style}
    >
      {children}
    </svg>
  );
}

export function NodeIcon(nodeType: NodeType) {
  return switchExpression(nodeType, {
    [NODE_TYPES.callbackState]: CallbackStateIcon,
    [NODE_TYPES.eventState]: EventStateIcon,
    [NODE_TYPES.foreachState]: ForEachStateIcon,
    [NODE_TYPES.injectState]: InjectStateIcon,
    [NODE_TYPES.operationState]: OperationStateIcon,
    [NODE_TYPES.parallelState]: ParallelStateIcon,
    [NODE_TYPES.sleepState]: SleepStateIcon,
    [NODE_TYPES.switchState]: SwitchStateIcon,
    [NODE_TYPES.unknown]: UnknownIcon,
    default: () => <div>?</div>,
  });
}

export function CallbackStateIcon() {
  return (
    <RoundSvg>
      <CallbackstateSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function EventStateIcon() {
  return (
    <RoundSvg>
      <EventstateSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function ForEachStateIcon() {
  return (
    <RoundSvg>
      <ForeachstateSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function InjectStateIcon() {
  return (
    <RoundSvg>
      <InjectstateSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function OperationStateIcon() {
  return (
    <RoundSvg>
      <OperationstateSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function ParallelStateIcon() {
  return (
    <RoundSvg>
      <ParallelstateSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function SleepStateIcon() {
  return (
    <RoundSvg>
      <SleepstateSvg {...nodeSvgProps} />
    </RoundSvg>
  );
}

export function SwitchStateIcon() {
  return (
    <RoundSvg>
      <SwitchstateSvg {...nodeSvgProps} />
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
