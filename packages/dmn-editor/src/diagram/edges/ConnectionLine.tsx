import * as React from "react";
import { useEffect } from "react";
import * as RF from "reactflow";

export function ConnectionLine({ toX, toY, fromNode, fromHandle }: RF.ConnectionLineComponentProps) {
  const fromX = (fromNode?.position.x ?? 0) + (fromNode?.width ?? 0) / 2;
  const fromY = (fromNode?.position.y ?? 0) + (fromNode?.height ?? 0) / 2;

  useEffect(() => {
    console.info(fromHandle?.id);
  }, [fromHandle?.id]);

  return (
    <g>
      <path fill="none" stroke="#222" strokeWidth={1.5} className="animated" d={`M${fromX},${fromY} L ${toX},${toY}`} />
      <circle cx={toX} cy={toY} fill="#fff" r={3} stroke="#222" strokeWidth={1.5} />
    </g>
  );
}
