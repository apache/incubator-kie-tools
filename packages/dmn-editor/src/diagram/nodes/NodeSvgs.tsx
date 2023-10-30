import * as React from "react";
import * as RF from "reactflow";
import { DEFAULT_INTRACTION_WIDTH } from "../maths/DmnMaths";

export type NodeSvgProps = RF.Dimensions & RF.XYPosition & { strokeWidth?: number };

const DEFAULT_NODE_FILL = "white";
const DEFAULT_NODE_STROKE_WIDTH = 1.5;
const DEFAULT_NODE_STROKE_COLOR = "black";

export const ___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches = { flag: false };

// This function makes sure that independent of strokeWidth, the size and position of the element is preserved. Much like `box-sizing: border-box`;
export function normalize<T extends NodeSvgProps>(_props: T) {
  const { strokeWidth: _strokeWidth, x: _x, y: _y, width: _width, height: _height, ...props } = _props;

  const strokeWidth = _strokeWidth ?? DEFAULT_NODE_STROKE_WIDTH;
  const halfStrokeWidth = strokeWidth / 2;

  const x = _x + halfStrokeWidth;
  const y = _y + halfStrokeWidth;
  const width = _width - strokeWidth;
  const height = _height - strokeWidth;

  return {
    strokeWidth,
    x,
    y,
    width: width + (___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches.flag ? 0.1 : 0),
    height: height + (___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches.flag ? 0 : 0.1),
    props,
  };
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
  const { strokeWidth, x, y, width, height: totalHeight, props } = normalize(_props);
  const amplitude = 20;
  const height = totalHeight - amplitude / 2; // Need to leave some space for the wave at the bottom.

  const straightLines = `M${width},${height} L${width},0 L0,0 L0,${height}`;
  const bottomWave = `Q${width / 4},${height + amplitude} ${width / 2},${height} T${width},${height}`;
  return (
    <g>
      <path
        {...props}
        d={`${straightLines} ${bottomWave} Z`}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        fill={DEFAULT_NODE_FILL}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
    </g>
  );
}

export const containerNodeInteractionRectCssClassName = "kie-dmn-editor--node-containerNodeInteractionRect";

export const DecisionServiceNodeSvg = React.forwardRef<
  SVGRectElement,
  NodeSvgProps & {
    dividerLineRef?: React.RefObject<SVGPathElement>;
    dividerLineLocalY?: number;
    showSectionLabels: boolean;
    isCollapsed?: boolean;
  }
>((__props, ref) => {
  const { strokeWidth, x, y, width, height, props: _props } = normalize(__props);
  const interactionRect = normalize({ ...__props, strokeWidth: DEFAULT_INTRACTION_WIDTH / 2 });
  const { dividerLineLocalY, showSectionLabels, dividerLineRef, isCollapsed, ...props } = _props;
  const dividerLineCoords = {
    x: x + strokeWidth / 2,
    y: y + (dividerLineLocalY ? dividerLineLocalY : height / 2),
  };

  return (
    <g>
      {!isCollapsed && (
        <>
          <path
            ref={dividerLineRef}
            className={"kie-dmn-editor--node-decisionService-interactionDividerLine"}
            d={`M0,0 L${width},0`}
            strokeWidth={DEFAULT_INTRACTION_WIDTH / 2}
            style={{ stroke: "transparent !important" }}
            transform={`translate(${dividerLineCoords.x},${dividerLineCoords.y})`}
          />
          <path
            d={`M0,0 L${width},0`}
            strokeLinejoin={"round"}
            strokeWidth={strokeWidth}
            stroke={DEFAULT_NODE_STROKE_COLOR}
            transform={`translate(${dividerLineCoords.x},${dividerLineCoords.y})`}
          />
        </>
      )}
      {/* The border Rect of the Decision Service takes precedence over the Divider Line, therefore it comes after */}
      <rect
        {...props}
        x={x}
        y={y}
        width={width}
        height={height}
        strokeWidth={strokeWidth}
        fill={"transparent"}
        stroke={DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        rx={"40"}
        ry={"40"}
        className={"kie-dmn-editor--node-decisionService-visibleRect"}
      />
      <rect
        ref={ref}
        {...interactionRect}
        fill={"transparent"}
        stroke={"transparent"}
        strokeLinejoin={"round"}
        rx={"30"}
        ry={"30"}
        className={containerNodeInteractionRectCssClassName}
      />
      {showSectionLabels && !isCollapsed && (
        <>
          <text
            className={"kie-dmn-editor--decision-service-label"}
            textAnchor={"middle"}
            dominantBaseline={"auto"}
            transform={`translate(${dividerLineCoords.x + width / 2},${dividerLineCoords.y - 6})`}
          >
            OUTPUT
          </text>
          <text
            className={"kie-dmn-editor--decision-service-label"}
            textAnchor={"middle"}
            dominantBaseline={"hanging"}
            transform={`translate(${dividerLineCoords.x + width / 2},${dividerLineCoords.y + 6})`}
          >
            ENCAPSULATED
          </text>
        </>
      )}
    </g>
  );
});

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

export const GroupNodeSvg = React.forwardRef<SVGRectElement, NodeSvgProps & { strokeDasharray?: string }>(
  (__props, ref) => {
    const { strokeWidth, x, y, width, height, props } = normalize(__props);
    const interactionRect = normalize({ ...__props, strokeWidth: DEFAULT_INTRACTION_WIDTH / 2 });
    const strokeDasharray = props.strokeDasharray ?? "14,10,3,10";
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
          rx={40}
          ry={40}
        />
        <rect
          ref={ref}
          {...interactionRect}
          fill={"transparent"}
          stroke={"transparent"}
          rx={"30"}
          ry={"30"}
          className={containerNodeInteractionRectCssClassName}
        />
      </g>
    );
  }
);

export const UnknownNodeSvg = (_props: NodeSvgProps & { strokeDasharray?: string }) => {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const strokeDasharray = props.strokeDasharray ?? "2,4";
  return (
    <g>
      <rect
        {...props}
        x={x}
        y={y}
        width={width}
        height={height}
        fill={"transparent"}
        stroke={"red"}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        strokeDasharray={strokeDasharray}
      />
    </g>
  );
};
