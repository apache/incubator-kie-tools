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

const handleButtonSize = 34; // That's the size of the button. This is a "magic number", as it was obtained from the rendered page.
const svgViewboxPadding = Math.sqrt(Math.pow(handleButtonSize, 2) / 2) - handleButtonSize / 2; // This lets us create a square that will perfectly fit inside the button circle.

const edgeSvgViewboxSize = 25;

const nodeSvgProps = { width: 100, height: 70, x: 0, y: 15, strokeWidth: 8 };
const nodeSvgViewboxSize = nodeSvgProps.width;

export const handleStyle: React.CSSProperties = {
  display: "flex",
  position: "unset",
  transform: "unset",
  // position: "relative",
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
                  {e === "edge_informationRequirement" && (
                    <InformationRequirementPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {e === "edge_knowledgeRequirement" && (
                    <KnowledgeRequirementPath d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},0`} />
                  )}
                  {e === "edge_authorityRequirement" && (
                    <AuthorityRequirementPath
                      d={`M2,${edgeSvgViewboxSize - 2} L${edgeSvgViewboxSize - 2},2`}
                      centerToConnectionPoint={false}
                    />
                  )}
                  {e === "edge_association" && (
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
                  {n === "node_inputData" && <InputDataNodeSvg {...nodeSvgProps} />}
                  {n === "node_decision" && <DecisionNodeSvg {...nodeSvgProps} />}
                  {n === "node_bkm" && <BkmNodeSvg {...nodeSvgProps} />}
                  {n === "node_decisionService" && (
                    <DecisionServiceNodeSvg {...nodeSvgProps} y={0} height={nodeSvgProps.width} />
                  )}
                  {n === "node_knowledgeSource" && <KnowledgeSourceNodeSvg {...nodeSvgProps} />}
                  {n === "node_textAnnotation" && <TextAnnotationNodeSvg {...nodeSvgProps} />}
                  {n === "node_group" && <GroupNodeSvg {...nodeSvgProps} />}
                </svg>
              </RF.Handle>
            ))}
          </FlexItem>
        )}
      </Flex>
    </>
  );
}
