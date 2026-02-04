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
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import "./OutgoingStuffNodePanel.css";
import { useState } from "react";
import { useXyFlowReactKieDiagramStore } from "../store/Store";

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

export type OutgoingStuffNodePanelNodeMapping<N extends string> = Record<
  N,
  {
    actionTitle: string;
    icon: React.ComponentType<typeof nodeSvgProps>;
  }
>;

export type OutgoingStuffNodePanelEdgeMapping<E extends string> = Record<
  E,
  {
    actionTitle: string;
    icon: React.ComponentType<{ viewboxSize: number }>;
  }
>;

export function OutgoingStuffNodePanel<N extends string, E extends string>(props: {
  isVisible: boolean;
  nodeTypes: N[];
  edgeTypes: E[];
  nodeHref: string;
  nodeMapping: OutgoingStuffNodePanelNodeMapping<N>;
  edgeMapping: OutgoingStuffNodePanelEdgeMapping<E>;
}) {
  const dragging = useXyFlowReactKieDiagramStore((s) => !!s.xyFlowReactKieDiagram.ongoingConnection);
  const style: React.CSSProperties = React.useMemo(
    () => ({
      visibility: props.isVisible && !dragging ? undefined : "hidden",
    }),
    [dragging, props.isVisible]
  );

  return (
    <>
      {/* Only here for when Nodes don't have anything in their OutgoingStuff panels, as every node needs a 'source' handle. */}
      {props.edgeTypes.length + props.nodeTypes.length === 0 && (
        <>
          <RF.Handle
            key={"unique-source-handle"} // Arbitrary string. Shouldn't be referenced.
            id={"unique-source-handle"} // Arbitrary string. Shouldn't be referenced.
            isConnectableEnd={false}
            isConnectableStart={false}
            type={"source"}
            style={{ ...handleStyle, opacity: 0 }} // Invisible on purpose, as this shouldn't be interacted with.
            position={RF.Position.Top} // Doesn't really impact anything.
            title={""}
          />
        </>
      )}
      <>
        <Flex className={"xyflow-react-kie-diagram--outgoing-stuff-node-panel"} style={style}>
          {props.edgeTypes.length > 0 && (
            <FlexItem>
              {props.edgeTypes.map((edgeType) => {
                const Icon = props.edgeMapping[edgeType].icon;
                return (
                  <RF.Handle
                    key={edgeType}
                    id={edgeType}
                    isConnectableEnd={false}
                    type={"source"}
                    style={handleStyle}
                    position={RF.Position.Top}
                    title={props.edgeMapping[edgeType].actionTitle}
                    data-testid={`${props.nodeHref}-add-${edgeType}`}
                  >
                    <svg
                      className={"xyflow-react-kie-diagram--round-svg-container"}
                      viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
                      style={{ padding: `${svgViewboxPadding}px` }}
                    >
                      <Icon viewboxSize={edgeSvgViewboxSize} />
                    </svg>
                  </RF.Handle>
                );
              })}
            </FlexItem>
          )}

          {props.nodeTypes.length > 0 && (
            <FlexItem>
              {props.nodeTypes.map((nodeType) => {
                const Icon = props.nodeMapping[nodeType].icon;
                return (
                  <RF.Handle
                    key={nodeType}
                    id={nodeType}
                    isConnectableEnd={false}
                    type={"source"}
                    style={handleStyle}
                    position={RF.Position.Top}
                    title={props.nodeMapping[nodeType].actionTitle}
                    data-testid={`${props.nodeHref}-add-${nodeType}`}
                  >
                    <svg
                      className={"xyflow-react-kie-diagram--round-svg-container"}
                      viewBox={`0 0 ${nodeSvgViewboxSize} ${nodeSvgViewboxSize}`}
                      style={{ padding: `${svgViewboxPadding}px` }}
                    >
                      <Icon {...nodeSvgProps} />
                    </svg>
                  </RF.Handle>
                );
              })}
            </FlexItem>
          )}
        </Flex>
      </>
    </>
  );
}
