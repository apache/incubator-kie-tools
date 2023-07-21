import * as RF from "reactflow";
import * as React from "react";
import { useCallback } from "react";
import { NodeType } from "./connections/graphStructure";
import { NODE_TYPES } from "./nodes/NodeTypes";

export function Pallete() {
  const onDragStart = useCallback((event: React.DragEvent, nodeType: NodeType) => {
    event.dataTransfer.setData("application/reactflow", nodeType);
    event.dataTransfer.effectAllowed = "move";
  }, []);

  return (
    <RF.Panel position={"top-left"}>
      <aside className={"kie-dmn-editor--pallete"}>
        <button
          className={"kie-dmn-editor--pallete-button dndnode input-data"}
          onDragStart={(event) => onDragStart(event, NODE_TYPES.inputData)}
          draggable={true}
        >
          I
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode decision"}
          onDragStart={(event) => onDragStart(event, NODE_TYPES.decision)}
          draggable={true}
        >
          D
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode bkm"}
          onDragStart={(event) => onDragStart(event, NODE_TYPES.bkm)}
          draggable={true}
        >
          B
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode knowledge-source"}
          onDragStart={(event) => onDragStart(event, NODE_TYPES.knowledgeSource)}
          draggable={true}
        >
          K
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode decision-service"}
          onDragStart={(event) => onDragStart(event, NODE_TYPES.decisionService)}
          draggable={true}
        >
          D
        </button>
        <button
          className={"kie-dmn-editor--pallete-button dndnode text-annotation"}
          onDragStart={(event) => onDragStart(event, NODE_TYPES.textAnnotation)}
          draggable={true}
        >
          T
        </button>
        <button className={"kie-dmn-editor--pallete-button dndnode text-annotation"}>G</button>
      </aside>
    </RF.Panel>
  );
}
