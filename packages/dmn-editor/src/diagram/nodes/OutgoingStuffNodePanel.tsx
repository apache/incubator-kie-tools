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
  AssociationPath,
  AuthorityRequirementPath,
  InformationRequirementPath,
  KnowledgeRequirementPath,
} from "../edges/Edges";
import {
  InputDataNodeSvg,
  DecisionNodeSvg,
  BkmNodeSvg,
  DecisionServiceNodeSvg,
  KnowledgeSourceNodeSvg,
  TextAnnotationNodeSvg,
  GroupNodeSvg,
} from "./NodeSvgs";
import { NODE_TYPES } from "./NodeTypes";
import { EDGE_TYPES } from "../edges/EdgeTypes";
import { useSettings } from "../../settings/DmnEditorSettingsContext";

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
      case EDGE_TYPES.informationRequirement: {
        return "Add Information Requirement edge";
      }
      case EDGE_TYPES.knowledgeRequirement: {
        return "Add Knowledge Requirement edge";
      }
      case EDGE_TYPES.authorityRequirement: {
        return "Add Authority Requirement edge";
      }
      case EDGE_TYPES.association: {
        return "Add Association edge";
      }
      default: {
        throw new Error("Add Unknown edge type");
      }
    }
  }, []);

  const getNodeActionTitle = React.useCallback((nodeType: string): string => {
    switch (nodeType) {
      case NODE_TYPES.decision: {
        return "Add Decision node";
      }
      case NODE_TYPES.bkm: {
        return "Add BKM node";
      }
      case NODE_TYPES.knowledgeSource: {
        return "Add Knowledge Source node";
      }
      case NODE_TYPES.textAnnotation: {
        return "Add Text Annotation node";
      }
      default: {
        throw new Error("Add Unknown node type");
      }
    }
  }, []);

  return (
    <>
      <Flex className={"kie-dmn-editor--outgoing-stuff-node-panel"} style={style} gap={{ default: "gapNone" }}>
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
                  className={"kie-dmn-editor--round-svg-container"}
                  viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
                  style={{ padding: `${svgViewboxPadding}px` }}
                >
                  {edgeType === EDGE_TYPES.informationRequirement && (
                    <InformationRequirementPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {edgeType === EDGE_TYPES.knowledgeRequirement && (
                    <KnowledgeRequirementPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {edgeType === EDGE_TYPES.authorityRequirement && (
                    <AuthorityRequirementPath
                      d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},2`}
                      centerToConnectionPoint={false}
                    />
                  )}
                  {edgeType === EDGE_TYPES.association && (
                    <AssociationPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize},0`} strokeWidth={2} />
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
                  className={"kie-dmn-editor--round-svg-container"}
                  viewBox={`0 0 ${nodeSvgViewboxSize} ${nodeSvgViewboxSize}`}
                  style={{ padding: `${svgViewboxPadding}px` }}
                >
                  {nodeType === NODE_TYPES.inputData && <InputDataNodeSvg {...nodeSvgProps} isCollection={false} />}
                  {nodeType === NODE_TYPES.decision && (
                    <DecisionNodeSvg {...nodeSvgProps} isCollection={false} hasHiddenRequirements={false} />
                  )}
                  {nodeType === NODE_TYPES.bkm && <BkmNodeSvg {...nodeSvgProps} hasHiddenRequirements={false} />}
                  {nodeType === NODE_TYPES.decisionService && (
                    <DecisionServiceNodeSvg
                      {...nodeSvgProps}
                      y={0}
                      height={nodeSvgProps.width}
                      showSectionLabels={true}
                      isReadOnly={true}
                    />
                  )}
                  {nodeType === NODE_TYPES.knowledgeSource && (
                    <KnowledgeSourceNodeSvg {...nodeSvgProps} hasHiddenRequirements={false} />
                  )}
                  {nodeType === NODE_TYPES.textAnnotation && <TextAnnotationNodeSvg {...nodeSvgProps} />}
                  {nodeType === NODE_TYPES.group && <GroupNodeSvg {...nodeSvgProps} />}
                </svg>
              </RF.Handle>
            ))}
          </FlexItem>
        )}
      </Flex>
    </>
  );
}
