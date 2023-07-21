import * as React from "react";
import { useEffect } from "react";
import * as RF from "reactflow";
import { NODE_TYPES, EDGE_TYPES } from "../nodes/NodeTypes";

export function ConnectionLine({ toX, toY, fromNode, fromHandle, connectionStatus }: RF.ConnectionLineComponentProps) {
  const fromX = (fromNode?.position.x ?? 0) + (fromNode?.width ?? 0) / 2;
  const fromY = (fromNode?.position.y ?? 0) + (fromNode?.height ?? 0) / 2;

  useEffect(() => {
    console.info(fromHandle?.id);
    console.info(connectionStatus);
  }, [connectionStatus, fromHandle?.id]);

  if (fromHandle?.id === EDGE_TYPES.informationRequirement) {
    return <path d={`M ${fromX},${fromY} L ${toX},${toY}`} style={{ strokeWidth: 1, stroke: "black" }} />;
  } else if (fromHandle?.id === EDGE_TYPES.knowledgeRequirement) {
    return <path d={`M ${fromX},${fromY} L ${toX},${toY}`} style={{ strokeWidth: 1, stroke: "green" }} />;
  } else if (fromHandle?.id === EDGE_TYPES.authorityRequirement) {
    return <path d={`M ${fromX},${fromY} L ${toX},${toY}`} style={{ strokeWidth: 1, stroke: "blue" }} />;
  } else if (fromHandle?.id === EDGE_TYPES.association) {
    return <path d={`M ${fromX},${fromY} L ${toX},${toY}`} style={{ strokeWidth: 1, stroke: "orange" }} />;
  } else if (fromHandle?.id === NODE_TYPES.decision) {
    return (
      <g>
        <path
          className="animated"
          d={`M ${fromX},${fromY} L ${toX},${toY}`}
          style={{ strokeWidth: 1, stroke: "black" }}
        />
        <circle cx={toX} cy={toY} fill="#fff" r={20} stroke="black" strokeWidth={1.5} />
      </g>
    );
  } else if (fromHandle?.id === NODE_TYPES.bkm) {
    return (
      <g>
        <path
          className="animated"
          d={`M ${fromX},${fromY} L ${toX},${toY}`}
          style={{ strokeWidth: 1, stroke: "green" }}
        />
        <circle cx={toX} cy={toY} fill="#fff" r={20} stroke="green" strokeWidth={1.5} />
      </g>
    );
  } else if (fromHandle?.id === NODE_TYPES.knowledgeSource) {
    return (
      <g>
        <path
          className="animated"
          d={`M ${fromX},${fromY} L ${toX},${toY}`}
          style={{ strokeWidth: 1, stroke: "blue" }}
        />
        <circle cx={toX} cy={toY} fill="#fff" r={20} stroke="blue" strokeWidth={1.5} />
      </g>
    );
  } else if (fromHandle?.id === NODE_TYPES.textAnnotation) {
    return (
      <g>
        <path d={`M ${fromX},${fromY} L ${toX},${toY}`} style={{ strokeWidth: 1, stroke: "orange" }} />
        <circle cx={toX} cy={toY} fill="#fff" r={20} stroke="orange" strokeWidth={1.5} />
      </g>
    );
  } else {
    throw new Error(`Unknown source of ConnectionLine '${fromHandle?.id}'.`);
  }
}
