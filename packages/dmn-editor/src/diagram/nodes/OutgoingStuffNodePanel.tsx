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

export function OutgoingStuffNodePanel(props: { isVisible: boolean; nodes: NodeType[]; edges: EdgeType[] }) {
  const style: React.CSSProperties = React.useMemo(
    () => ({
      visibility: props.isVisible ? undefined : "hidden",
    }),
    [props.isVisible]
  );

  return (
    <>
      <Flex className={"kie-dmn-editor--outgoing-stuff-node-panel"} style={style}>
        {props.edges.length > 0 && (
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
                <svg
                  className={"kie-dmn-editor--round-svg-container"}
                  viewBox={`0 0 ${edgeSvgViewboxSize} ${edgeSvgViewboxSize}`}
                  style={{ padding: `${svgViewboxPadding}px` }}
                >
                  {e === EDGE_TYPES.informationRequirement && (
                    <InformationRequirementPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {e === EDGE_TYPES.knowledgeRequirement && (
                    <KnowledgeRequirementPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {e === EDGE_TYPES.authorityRequirement && (
                    <AuthorityRequirementPath
                      d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},2`}
                      centerToConnectionPoint={false}
                    />
                  )}
                  {e === EDGE_TYPES.association && (
                    <AssociationPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize},0`} strokeWidth={2} />
                  )}
                </svg>
              </RF.Handle>
            ))}
          </FlexItem>
        )}

        {props.nodes.length > 0 && (
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
                <svg
                  className={"kie-dmn-editor--round-svg-container"}
                  viewBox={`0 0 ${nodeSvgViewboxSize} ${nodeSvgViewboxSize}`}
                  style={{ padding: `${svgViewboxPadding}px` }}
                >
                  {n === NODE_TYPES.inputData && <InputDataNodeSvg {...nodeSvgProps} />}
                  {n === NODE_TYPES.decision && <DecisionNodeSvg {...nodeSvgProps} />}
                  {n === NODE_TYPES.bkm && <BkmNodeSvg {...nodeSvgProps} />}
                  {n === NODE_TYPES.decisionService && (
                    <DecisionServiceNodeSvg {...nodeSvgProps} y={0} height={nodeSvgProps.width} />
                  )}
                  {n === NODE_TYPES.knowledgeSource && <KnowledgeSourceNodeSvg {...nodeSvgProps} />}
                  {n === NODE_TYPES.textAnnotation && <TextAnnotationNodeSvg {...nodeSvgProps} />}
                  {n === NODE_TYPES.group && <GroupNodeSvg {...nodeSvgProps} />}
                </svg>
              </RF.Handle>
            ))}
          </FlexItem>
        )}
      </Flex>
    </>
  );
}
