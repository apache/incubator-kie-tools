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
import * as RF from "reactflow";
import { EdgeType, NodeType } from "../connections/graphStructure";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import {
  TransitionPath,
  ErrorTransitionPath,
  EventConditionTransitionPath,
  DefaultConditionTransitionPath,
  DataConditionTransitionPath,
  CompensationTransitionPath,
} from "../edges/SwfEdges";
import {
  EventstateSvg,
  OperationstateSvg,
  SwitchstateSvg,
  SleepstateSvg,
  ParallelstateSvg,
  InjectstateSvg,
  ForeachstateSvg,
  CallbackstateSvg,
} from "./SwfNodeSvgs";
import { NODE_TYPES } from "./SwfNodeTypes";
import { EDGE_TYPES } from "../edges/SwfEdgeTypes";
import { useSettings } from "../../settings/SwfEditorSettingsContext";

const handleButtonSize = 34; // That's the size of the button. This is a "magic number", as it was obtained from the rendered page.
const svgViewboxPadding = Math.sqrt(Math.pow(handleButtonSize, 2) / 2) - handleButtonSize / 2; // This lets us create a square that will perfectly fit inside the button circle.

const edgeSvgViewboxSize = 25;

const nodeSvgProps = { width: 100, height: 70, x: 0, y: 15, strokeWidth: 8 };
const nodeSvgViewboxSize = nodeSvgProps.width;

export const handleStyle: React.CSSProperties = {
  display: "flex",
  position: "unset",
  transform: "unset",
};

export function OutgoingStuffNodePanel(props: {
  isVisible: boolean;
  nodeTypes: NodeType[];
  edgeTypes: EdgeType[];
  nodeHref: string;
}) {
  const settings = useSettings();
  const style: React.CSSProperties = React.useMemo(
    () => ({
      visibility: !settings.isReadOnly && props.isVisible ? undefined : "hidden",
    }),
    [props.isVisible, settings.isReadOnly]
  );

  const getEdgeActionTitle = React.useCallback((edgeType: string): string => {
    switch (edgeType) {
      case EDGE_TYPES.compensationTransition: {
        return "Add Compensation Transition";
      }
      case EDGE_TYPES.dataConditionTransition: {
        return "Add Data Condition Transition";
      }
      case EDGE_TYPES.defaultConditionTransition: {
        return "Add Default Transition";
      }
      case EDGE_TYPES.errorTransition: {
        return "Add Error Transition";
      }
      case EDGE_TYPES.eventConditionTransition: {
        return "Add Event Condition Transition";
      }
      case EDGE_TYPES.transition: {
        return "Add Transition";
      }
      default: {
        throw new Error("Add Unknown edge type");
      }
    }
  }, []);

  const getNodeActionTitle = React.useCallback((nodeType: string): string => {
    switch (nodeType) {
      case NODE_TYPES.callbackState: {
        return "Add Callback State";
      }
      case NODE_TYPES.eventState: {
        return "Add Event State";
      }
      case NODE_TYPES.foreachState: {
        return "Add ForEach State";
      }
      case NODE_TYPES.injectState: {
        return "Add Inject State";
      }
      case NODE_TYPES.operationState: {
        return "Add Operation State";
      }
      case NODE_TYPES.parallelState: {
        return "Add Parallel State";
      }
      case NODE_TYPES.sleepState: {
        return "Add Sleep State";
      }
      case NODE_TYPES.switchState: {
        return "Add Switch State";
      }
      default: {
        throw new Error("Add Unknown node type");
      }
    }
  }, []);

  return (
    <>
      <Flex className={"kie-swf-editor--outgoing-stuff-node-panel"} style={style} gap={{ default: "gapNone" }}>
        {props.edgeTypes.length > 0 && (
          <FlexItem>
            {props.edgeTypes.map((edgeType) => (
              <RF.Handle
                key={edgeType}
                id={edgeType}
                isConnectableEnd={false}
                type={"source"}
                style={handleStyle}
                position={RF.Position.Top}
                title={getEdgeActionTitle(edgeType)}
                data-testid={`${props.nodeHref}-add-${edgeType}`}
              >
                <svg
                  className={"kie-swf-editor--round-svg-container"}
                  viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
                  style={{ padding: `${svgViewboxPadding}px` }}
                >
                  {edgeType === EDGE_TYPES.compensationTransition && (
                    <CompensationTransitionPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {edgeType === EDGE_TYPES.transition && (
                    <TransitionPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {edgeType === EDGE_TYPES.errorTransition && (
                    <ErrorTransitionPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {edgeType === EDGE_TYPES.defaultConditionTransition && (
                    <DefaultConditionTransitionPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {edgeType === EDGE_TYPES.dataConditionTransition && (
                    <DataConditionTransitionPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {edgeType === EDGE_TYPES.eventConditionTransition && (
                    <EventConditionTransitionPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                </svg>
              </RF.Handle>
            ))}
          </FlexItem>
        )}

        {props.nodeTypes.length > 0 && (
          <FlexItem>
            {props.nodeTypes.map((nodeType) => (
              <RF.Handle
                key={nodeType}
                id={nodeType}
                isConnectableEnd={false}
                type={"source"}
                style={handleStyle}
                position={RF.Position.Top}
                title={getNodeActionTitle(nodeType)}
                data-testid={`${props.nodeHref}-add-${nodeType}`}
              >
                <svg
                  className={"kie-swf-editor--round-svg-container"}
                  viewBox={`0 0 ${nodeSvgViewboxSize} ${nodeSvgViewboxSize}`}
                  style={{ padding: `${svgViewboxPadding}px` }}
                >
                  {nodeType === NODE_TYPES.callbackState && <CallbackstateSvg {...nodeSvgProps} />}
                  {nodeType === NODE_TYPES.eventState && <EventstateSvg {...nodeSvgProps} />}
                  {nodeType === NODE_TYPES.foreachState && <ForeachstateSvg {...nodeSvgProps} />}
                  {nodeType === NODE_TYPES.injectState && <InjectstateSvg {...nodeSvgProps} />}
                  {nodeType === NODE_TYPES.operationState && <OperationstateSvg {...nodeSvgProps} />}
                  {nodeType === NODE_TYPES.parallelState && <ParallelstateSvg {...nodeSvgProps} />}
                  {nodeType === NODE_TYPES.sleepState && <SleepstateSvg {...nodeSvgProps} />}
                  {nodeType === NODE_TYPES.switchState && <SwitchstateSvg {...nodeSvgProps} />}
                </svg>
              </RF.Handle>
            ))}
          </FlexItem>
        )}
      </Flex>
    </>
  );
}
