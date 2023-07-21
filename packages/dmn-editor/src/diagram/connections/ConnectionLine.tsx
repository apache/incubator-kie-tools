import * as React from "react";
import * as RF from "reactflow";
import { NODE_TYPES } from "../nodes/NodeTypes";
import { EDGE_TYPES } from "../edges/EdgeTypes";
import { snapPoint } from "../SnapGrid";

export function ConnectionLine({ toX, toY, fromNode, fromHandle, connectionStatus }: RF.ConnectionLineComponentProps) {
  const { "@_x": fromX, "@_y": fromY } = snapPoint({
    "@_x": (fromNode?.position.x ?? 0) + (fromNode?.width ?? 0) / 2,
    "@_y": (fromNode?.position.y ?? 0) + (fromNode?.height ?? 0) / 2,
  });

  if (fromHandle?.id === EDGE_TYPES.informationRequirement) {
    return <path d={`M ${fromX},${fromY} L ${toX},${toY}`} style={{ strokeWidth: 1, stroke: "black" }} />;
  } else if (fromHandle?.id === EDGE_TYPES.knowledgeRequirement) {
    return <path d={`M ${fromX},${fromY} L ${toX},${toY}`} style={{ strokeWidth: 1, stroke: "green" }} />;
  } else if (fromHandle?.id === EDGE_TYPES.authorityRequirement) {
    return <path d={`M ${fromX},${fromY} L ${toX},${toY}`} style={{ strokeWidth: 1, stroke: "blue" }} />;
  } else if (fromHandle?.id === EDGE_TYPES.association) {
    return <path d={`M ${fromX},${fromY} L ${toX},${toY}`} style={{ strokeWidth: 1, stroke: "orange" }} />;
  } else if (fromHandle?.id === NODE_TYPES.decision) {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint({ "@_x": toX, "@_y": toY });
    return (
      <g>
        <path
          className="animated"
          d={`M ${fromX},${fromY} L ${toXsnapped},${toYsnapped}`}
          style={{ strokeWidth: 1, stroke: "black" }}
        />
        <circle cx={toXsnapped} cy={toYsnapped} fill="#fff" r={20} stroke="black" strokeWidth={1.5} />
      </g>
    );
  } else if (fromHandle?.id === NODE_TYPES.bkm) {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint({ "@_x": toX, "@_y": toY });
    return (
      <g>
        <path
          className="animated"
          d={`M ${fromX},${fromY} L ${toXsnapped},${toYsnapped}`}
          style={{ strokeWidth: 1, stroke: "green" }}
        />
        <circle cx={toXsnapped} cy={toYsnapped} fill="#fff" r={20} stroke="green" strokeWidth={1.5} />
      </g>
    );
  } else if (fromHandle?.id === NODE_TYPES.knowledgeSource) {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint({ "@_x": toX, "@_y": toY });
    return (
      <g>
        <path
          className="animated"
          d={`M ${fromX},${fromY} L ${toXsnapped},${toYsnapped}`}
          style={{ strokeWidth: 1, stroke: "blue" }}
        />
        <circle cx={toXsnapped} cy={toYsnapped} fill="#fff" r={20} stroke="blue" strokeWidth={1.5} />
      </g>
    );
  } else if (fromHandle?.id === NODE_TYPES.textAnnotation) {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint({ "@_x": toX, "@_y": toY });
    return (
      <g>
        <path d={`M ${fromX},${fromY} L ${toXsnapped},${toYsnapped}`} style={{ strokeWidth: 1, stroke: "orange" }} />
        <circle cx={toXsnapped} cy={toYsnapped} fill="#fff" r={20} stroke="orange" strokeWidth={1.5} />
      </g>
    );
  } else {
    throw new Error(`Unknown source of ConnectionLine '${fromHandle?.id}'.`);
  }
}
