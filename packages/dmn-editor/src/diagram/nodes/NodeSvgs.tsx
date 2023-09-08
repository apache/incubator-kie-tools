import * as React from "react";
import * as RF from "reactflow";

export type NodeSvgProps = RF.Dimensions & RF.XYPosition & { strokeWidth?: number };

const DEFAULT_NODE_FILL = "white";
const DEFAULT_NODE_STROKE_WIDTH = 1.5;
const DEFAULT_NODE_STROKE_COLOR = "black";

// This function makes sure that independent of strokeWidth, the size and position of the element is preserved. Much like `box-sizing: border-box`;
export function normalize<T extends NodeSvgProps>(_props: T) {
  const { strokeWidth: _strokeWidth, x: _x, y: _y, width: _width, height: _height, ...props } = _props;

  const strokeWidth = _strokeWidth ?? DEFAULT_NODE_STROKE_WIDTH;
  const halfStrokeWidth = strokeWidth / 2;

  const x = _x + halfStrokeWidth;
  const y = _y + halfStrokeWidth;
  const width = _width - strokeWidth;
  const height = _height - strokeWidth;

  return { strokeWidth, x, y, width, height, props };
}

export function InputDataNodeSvg(_props: NodeSvgProps) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const rx =
    typeof height === "number"
      ? height / 2
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 2
      : (() => {
          throw new Error("Can't calculate ry based on a string width.");
        })();

  return (
    <g>
      <rect
        {...props}
        x={x}
        y={y}
        width={width}
        height={height}
        fill={DEFAULT_NODE_FILL}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        rx={rx}
        ry={ry}
      />
    </g>
  );
}

export function DecisionNodeSvg(_props: NodeSvgProps) {
  return (
    <g>
      <rect
        {...normalize(_props)}
        fill={DEFAULT_NODE_FILL}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
      />
    </g>
  );
}

export function BkmNodeSvg(_props: NodeSvgProps) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const bevel = 25;
  return (
    <g>
      <polygon
        {...props}
        points={`${bevel},0 0,${bevel} 0,${height} ${width - bevel},${height} ${width},${height - bevel}, ${width},0`}
        fill={DEFAULT_NODE_FILL}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
    </g>
  );
}

export function KnowledgeSourceNodeSvg(_props: NodeSvgProps) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const quarterX = width / 4;
  const halfX = width / 2;
  const amplitude = 20;
  return (
    <g>
      <path
        {...props}
        d={`M0,${height - amplitude / 2} L0,0 M0,0 L${width},0 M${width},0 L${width},${height - amplitude / 2} Z`}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        fill={DEFAULT_NODE_FILL}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
      <path
        d={`M0,0 Q${quarterX},${amplitude} ${halfX},0 T${width},0`}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        fill={DEFAULT_NODE_FILL}
        strokeWidth={strokeWidth}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y - amplitude / 2 + height})`}
      />
    </g>
  );
}

export function DecisionServiceNodeSvg(
  __props: NodeSvgProps & { dividerLineLocalY?: number; showSectionLabels: boolean }
) {
  const { strokeWidth, x, y, width, height, props: _props } = normalize(__props);
  const { dividerLineLocalY, showSectionLabels, ...props } = _props;
  const cornerRadius = 40;
  const dividerLineCoors = {
    x: x + strokeWidth / 2,
    y: y + (dividerLineLocalY ? dividerLineLocalY : height / 2),
  };

  return (
    <g>
      <rect
        {...props}
        x={x}
        y={y}
        width={width}
        height={height}
        fill={"transparent"}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        rx={cornerRadius}
        ry={cornerRadius}
      />
      {showSectionLabels && (
        <>
          <text
            textAnchor={"middle"}
            dominantBaseline={"auto"}
            transform={`translate(${dividerLineCoors.x + width / 2},${dividerLineCoors.y - 6})`}
          >
            OUTPUT
          </text>
          <text
            textAnchor={"middle"}
            dominantBaseline={"hanging"}
            transform={`translate(${dividerLineCoors.x + width / 2},${dividerLineCoors.y + 6})`}
          >
            ENCAPSULATED
          </text>
        </>
      )}
      <path
        d={`M0,0 L${width},0`}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        transform={`translate(${dividerLineCoors.x},${dividerLineCoors.y})`}
      />
    </g>
  );
}

export function TextAnnotationNodeSvg(__props: NodeSvgProps & { showPlaceholder?: boolean }) {
  const { strokeWidth, x, y, width, height, props: _props } = normalize(__props);
  const { showPlaceholder, ...props } = _props;
  return (
    <g>
      <path
        {...props}
        x={x}
        y={y}
        d={`M20,0 L0,0 M0,0 L0,${height} M0,${height} L20,${height}`}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
      {showPlaceholder && (
        <text x={"20%"} y={"62.5%"} style={{ fontSize: "5em", fontWeight: "bold" }}>
          Text
        </text>
      )}
    </g>
  );
}

export function GroupNodeSvg(_props: NodeSvgProps & { strokeDasharray?: string }) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const strokeDasharray = props.strokeDasharray ?? "14,10,3,10";
  const cornerRadius = 40;
  return (
    <g>
      <rect
        {...props}
        x={x}
        y={y}
        width={width}
        height={height}
        fill={"transparent"}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        strokeDasharray={strokeDasharray}
        rx={cornerRadius}
        ry={cornerRadius}
      />
    </g>
  );
}
