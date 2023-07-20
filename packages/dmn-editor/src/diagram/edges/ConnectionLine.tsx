import * as React from "react";
import { ConnectionLineComponentProps } from "reactflow";

export const ConnectionLine = ({
  fromPosition,
  toX,
  toY,
  fromNode,
  toPosition,
  connectionLineType,
  connectionLineStyle,
}: ConnectionLineComponentProps) => {
  const fromX = (fromNode?.position.x ?? 0) + (fromNode?.width ?? 0) / 2;
  const fromY = (fromNode?.position.y ?? 0) + (fromNode?.height ?? 0) / 2;

  return (
    <g>
      <path
        fill="none"
        stroke="#222"
        strokeWidth={1.5}
        className="animated"
        d={`M${fromX},${fromY} C ${fromX} ${toY} ${fromX} ${toY} ${toX},${toY}`}
      />
      <circle cx={toX} cy={toY} fill="#fff" r={3} stroke="#222" strokeWidth={1.5} />
    </g>
  );
};
