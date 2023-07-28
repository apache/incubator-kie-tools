import * as RF from "reactflow";
import * as React from "react";
import { useCallback } from "react";
import { NodeType } from "./connections/graphStructure";
import { NODE_TYPES } from "./nodes/NodeTypes";
import {
  BkmNodeSvg,
  DecisionNodeSvg,
  DecisionServiceNodeSvg,
  GroupNodeSvg,
  InputDataNodeSvg,
  KnowledgeSourceNodeSvg,
  TextAnnotationNodeSvg,
} from "./nodes/NodeSvgs";

const radius = 34;
const svgViewboxPadding = Math.sqrt(Math.pow(radius, 2) / 2) - radius / 2; // This lets us create a square that will perfectly fit inside the button circle.

const nodeSvgProps = { width: 200, height: 120, x: 16, y: 48, strokeWidth: 16 };
const nodeSvgViewboxSize = nodeSvgProps.width + 2 * nodeSvgProps.strokeWidth;

export const PALLETE_ELEMENT_MIME_TYPE = "application/kie-dmn-editor";

export function Pallete() {
  const onDragStart = useCallback((event: React.DragEvent, nodeType: NodeType) => {
    event.dataTransfer.setData(PALLETE_ELEMENT_MIME_TYPE, nodeType);
    event.dataTransfer.effectAllowed = "move";
  }, []);

  return (
    <>
      <RF.Panel position={"top-left"}>
        <aside className={"kie-dmn-editor--pallete"}>
          <button
            className={"kie-dmn-editor--pallete-button dndnode input-data"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.inputData)}
            draggable={true}
          >
            <RoundSvg>
              <InputDataNodeSvg {...nodeSvgProps} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode decision"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.decision)}
            draggable={true}
          >
            <RoundSvg>
              <DecisionNodeSvg {...nodeSvgProps} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode bkm"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.bkm)}
            draggable={true}
          >
            <RoundSvg>
              <BkmNodeSvg {...nodeSvgProps} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode knowledge-source"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.knowledgeSource)}
            draggable={true}
          >
            <RoundSvg>
              <KnowledgeSourceNodeSvg {...nodeSvgProps} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode decision-service"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.decisionService)}
            draggable={true}
          >
            <RoundSvg>
              <DecisionServiceNodeSvg {...nodeSvgProps} y={12} height={nodeSvgProps.width} />
            </RoundSvg>
          </button>
        </aside>
        <br />
        <aside className={"kie-dmn-editor--pallete"}>
          <button className={"kie-dmn-editor--pallete-button dndnode text-annotation"}>
            <RoundSvg>
              <GroupNodeSvg {...nodeSvgProps} y={12} height={nodeSvgProps.width} strokeDasharray={"28,28"} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode text-annotation"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.textAnnotation)}
            draggable={true}
          >
            <RoundSvg>
              <TextAnnotationNodeSvg {...nodeSvgProps} showPlaceholder={true} />
            </RoundSvg>
          </button>
        </aside>
      </RF.Panel>
    </>
  );
}

function RoundSvg({ children }: React.PropsWithChildren<{}>) {
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
