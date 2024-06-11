/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import * as RF from "reactflow";
import { DEFAULT_INTRACTION_WIDTH } from "../maths/DmnMaths";
import { DEFAULT_NODE_FILL, DEFAULT_NODE_STROKE_COLOR, DEFAULT_NODE_STROKE_WIDTH } from "./NodeStyle";

export type NodeLabelPosition = "center-bottom" | "center-center" | "top-center" | "center-left" | "top-left";

export type NodeSvgProps = RF.Dimensions &
  RF.XYPosition & {
    fillColor?: string;
    strokeColor?: string;
    strokeWidth?: number;
  };

export const ___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches = { flag: false };

// This function makes sure that independent of strokeWidth, the size and position of the element is preserved. Much like `box-sizing: border-box`;
export function normalize<T extends NodeSvgProps>(_props: T) {
  const {
    strokeWidth: _strokeWidth,
    x: _x,
    y: _y,
    width: _width,
    height: _height,
    fillColor: _fillColor,
    strokeColor: _strokeColor,
    ...props
  } = _props;

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
    fillColor: _fillColor,
    strokeColor: _strokeColor,
    props,
  };
}

export function InputDataNodeSvg(__props: NodeSvgProps & { isCollection: boolean }) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { isCollection, ...props },
  } = normalize(__props);

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
    <>
      <rect
        {...props}
        x={x}
        y={y}
        width={width}
        height={height}
        fill={fillColor ?? DEFAULT_NODE_FILL}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        rx={rx}
        ry={ry}
      />
      {isCollection && (
        <NodeCollectionMarker
          x={x}
          y={y}
          width={width}
          height={height}
          fillColor={fillColor}
          strokeColor={strokeColor}
          strokeWidth={strokeWidth}
          anchor={"bottom"}
        />
      )}
    </>
  );
}

export function AlternativeInputDataNodeSvg(
  __props: NodeSvgProps & { isCollection: boolean; isIcon: boolean; transform?: string }
) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { isCollection, isIcon, ...props },
  } = normalize(__props);

  const bevel = 25;
  const arrowStartingX = 6;
  const arrowStartingY = 10;

  return (
    <>
      <polygon
        {...props}
        points={`0,0 0,${height} ${width},${height} ${width},${bevel} ${width - bevel},0 ${width - bevel},0`}
        fill={fillColor ?? DEFAULT_NODE_FILL}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        strokeWidth={strokeWidth}
        transform={isIcon ? __props.transform : `translate(${x},${y})`}
      />
      {isIcon === false && (
        <>
          <polygon
            {...props}
            points={`${width - bevel},0 ${width - bevel},${bevel} ${width},${bevel}`}
            fill={fillColor ?? DEFAULT_NODE_FILL}
            stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
            strokeLinejoin={"round"}
            strokeWidth={strokeWidth}
            transform={`translate(${x},${y})`}
          />
          <polygon
            {...props}
            points={`${arrowStartingX},${arrowStartingY} ${arrowStartingX},20 20,20 20,26 30,15 20,4 20,${arrowStartingY} `}
            fill={fillColor ?? DEFAULT_NODE_FILL}
            stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
            strokeLinejoin={"round"}
            strokeWidth={strokeWidth}
            transform={`translate(${x},${y})`}
          />
        </>
      )}
      {isCollection && (
        <NodeCollectionMarker
          x={x}
          y={y}
          width={width}
          height={height}
          fillColor={fillColor}
          strokeColor={strokeColor}
          strokeWidth={strokeWidth}
          anchor={"bottom"}
        />
      )}
    </>
  );
}

export function DecisionNodeSvg(__props: NodeSvgProps & { isCollection: boolean; hasHiddenRequirements: boolean }) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { isCollection, hasHiddenRequirements, ...props },
  } = normalize(__props);

  return (
    <>
      <rect
        x={x}
        y={y}
        strokeWidth={strokeWidth}
        width={width}
        height={height}
        fill={fillColor ?? DEFAULT_NODE_FILL}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        {...props}
      />
      {isCollection && (
        <NodeCollectionMarker
          x={x}
          y={y}
          width={width}
          height={height}
          fillColor={fillColor}
          strokeColor={strokeColor}
          strokeWidth={strokeWidth}
          anchor="top"
        />
      )}
      {hasHiddenRequirements && (
        <NodeHiddenRequirementMarker
          x={x}
          y={y}
          width={width}
          height={height}
          strokeColor={strokeColor}
          strokeWidth={strokeWidth}
          anchor={"middle"}
        />
      )}
    </>
  );
}

export function BkmNodeSvg(__props: NodeSvgProps & { hasHiddenRequirements: boolean }) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { hasHiddenRequirements, ...props },
  } = normalize(__props);
  const bevel = 25;
  return (
    <>
      <polygon
        {...props}
        points={`${bevel},0 0,${bevel} 0,${height} ${width - bevel},${height} ${width},${height - bevel}, ${width},0`}
        fill={fillColor ?? DEFAULT_NODE_FILL}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
      {hasHiddenRequirements && (
        <NodeHiddenRequirementMarker
          x={x}
          y={y}
          width={width}
          height={height}
          strokeColor={strokeColor}
          strokeWidth={strokeWidth}
          anchor={"middle"}
        />
      )}
    </>
  );
}

export function KnowledgeSourceNodeSvg(__props: NodeSvgProps & { hasHiddenRequirements: boolean }) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height: totalHeight,
    fillColor,
    strokeColor,
    props: { hasHiddenRequirements, ...props },
  } = normalize(__props);
  const amplitude = 20;
  const height = totalHeight - amplitude / 2; // Need to leave some space for the wave at the bottom.

  const straightLines = `M${width},${height} L${width},0 L0,0 L0,${height}`;
  const bottomWave = `Q${width / 4},${height + amplitude} ${width / 2},${height} T${width},${height}`;
  return (
    <>
      <path
        {...props}
        d={`${straightLines} ${bottomWave} Z`}
        fill={fillColor ?? DEFAULT_NODE_FILL}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
      {hasHiddenRequirements && (
        <NodeHiddenRequirementMarker
          x={x}
          y={y}
          width={width}
          height={totalHeight}
          strokeColor={strokeColor}
          strokeWidth={strokeWidth}
          anchor={"left"}
        />
      )}
    </>
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
    isReadonly: boolean;
  }
>((__props, ref) => {
  const { strokeWidth, x, y, width, height, fillColor, strokeColor, props: _props } = normalize(__props);
  const {
    strokeWidth: interactionRectStrokeWidth,
    x: interactionRectX,
    y: interactionRectY,
    width: interactionRectWidth,
    height: interactionRectHeight,
    props: _interactionRectProps,
  } = normalize({ ...__props, strokeWidth: DEFAULT_INTRACTION_WIDTH / 2 });

  const { dividerLineLocalY, showSectionLabels, dividerLineRef, isCollapsed, isReadonly, ...props } = _props;
  const dividerLineCoords = {
    x: x + strokeWidth / 2,
    y: y + (dividerLineLocalY ? dividerLineLocalY : height / 2),
  };

  const {
    dividerLineLocalY: interactionRectDividerLineLocalY,
    showSectionLabels: interactionRectShowSectionLabels,
    dividerLineRef: interactionRectDividerLineRef,
    isCollapsed: interactionRectIsCollapsed,
    isReadonly: interactionRectIsReadonly,
    ...interactionRectProps
  } = _interactionRectProps;

  return (
    <>
      {!isCollapsed && (
        <>
          <path
            ref={dividerLineRef}
            className={`kie-dmn-editor--node-decisionService-interactionDividerLine ${isReadonly ? "readonly" : ""}`}
            d={`M0,0 L${width},0`}
            strokeWidth={DEFAULT_INTRACTION_WIDTH / 2}
            style={{ stroke: "transparent !important" }}
            transform={`translate(${dividerLineCoords.x},${dividerLineCoords.y})`}
          />
          <path
            d={`M0,0 L${width},0`}
            strokeLinejoin={"round"}
            strokeWidth={strokeWidth}
            stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
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
        fill={fillColor ?? "transparent"}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        strokeLinejoin={"round"}
        rx={"40"}
        ry={"40"}
        className={"kie-dmn-editor--node-decisionService-visibleRect"}
      />
      <rect
        {...interactionRectProps}
        ref={ref}
        x={interactionRectX}
        y={interactionRectY}
        width={interactionRectWidth}
        height={interactionRectHeight}
        strokeWidth={interactionRectStrokeWidth}
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
    </>
  );
});

export function TextAnnotationNodeSvg(__props: NodeSvgProps & { showPlaceholder?: boolean }) {
  const { strokeWidth, x, y, width, height, fillColor, strokeColor, props: _props } = normalize(__props);
  const { showPlaceholder, ...props } = _props;
  return (
    <>
      <rect
        x={x}
        y={y}
        width={width}
        height={height}
        fill={fillColor ?? "transparent"}
        stroke={"transparent"}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
      <path
        {...props}
        x={x}
        y={y}
        fill={fillColor ?? "transparent"}
        d={`M20,0 L0,0 M0,0 L0,${height} M0,${height} L20,${height}`}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        strokeWidth={strokeWidth}
        strokeLinejoin={"round"}
        transform={`translate(${x},${y})`}
      />
      {showPlaceholder && (
        <text x={"20%"} y={"62.5%"} style={{ fontSize: "5em", fontWeight: "bold" }}>
          Text
        </text>
      )}
    </>
  );
}

export const GroupNodeSvg = React.forwardRef<SVGRectElement, NodeSvgProps & { strokeDasharray?: string }>(
  (__props, ref) => {
    const { strokeWidth, x, y, width, height, fillColor, strokeColor, props } = normalize(__props);
    const {
      strokeWidth: interactionRectStrokeWidth,
      x: interactionRectX,
      y: interactionRectY,
      width: interactionRectWidth,
      height: interactionRectHeight,
      props: _interactionRectProps,
    } = normalize({ ...__props, strokeWidth: DEFAULT_INTRACTION_WIDTH / 2 });

    const { strokeDasharray: interactionRectStrokeDasharray, ...interactionRectProps } = _interactionRectProps;

    const strokeDasharray = props.strokeDasharray ?? "14,10,3,10";
    return (
      <>
        <rect
          {...props}
          x={x}
          y={y}
          width={width}
          height={height}
          fill={fillColor ?? "transparent"}
          stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
          strokeLinejoin={"round"}
          strokeWidth={strokeWidth}
          strokeDasharray={strokeDasharray}
          rx={40}
          ry={40}
        />
        <rect
          {...interactionRectProps}
          ref={ref}
          x={interactionRectX}
          y={interactionRectY}
          width={interactionRectWidth}
          height={interactionRectHeight}
          strokeWidth={interactionRectStrokeWidth}
          fill={"transparent"}
          stroke={"transparent"}
          rx={"30"}
          ry={"30"}
          className={containerNodeInteractionRectCssClassName}
        />
      </>
    );
  }
);

export function UnknownNodeSvg(_props: NodeSvgProps & { strokeDasharray?: string }) {
  const { strokeWidth, x, y, width, height, props } = normalize(_props);
  const strokeDasharray = props.strokeDasharray ?? "2,4";
  return (
    <>
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
    </>
  );
}

function NodeCollectionMarker({
  strokeWidth,
  strokeColor,
  fillColor,
  x,
  y,
  width,
  height,
  anchor,
}: {
  strokeWidth: number;
  strokeColor?: string;
  fillColor?: string;
  x: number;
  y: number;
  width: number;
  height: number;
  anchor: "top" | "bottom";
}) {
  const xPosition = x + width / 2;
  // Arbitrary space between the lines
  const xSpacing = 7;
  const y1Position = anchor === "bottom" ? y + height - 4 : y + 4;
  const y2Position = anchor === "bottom" ? y + height - 18 : y + 18;

  return (
    <>
      <line
        x1={xPosition - xSpacing}
        x2={xPosition - xSpacing}
        y1={y1Position}
        y2={y2Position}
        strokeWidth={strokeWidth}
        fill={fillColor ?? DEFAULT_NODE_FILL}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
      />
      <line
        x1={xPosition}
        x2={xPosition}
        y1={y1Position}
        y2={y2Position}
        strokeWidth={strokeWidth}
        fill={fillColor ?? DEFAULT_NODE_FILL}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
      />
      <line
        x1={xPosition + xSpacing}
        x2={xPosition + xSpacing}
        y1={y1Position}
        y2={y2Position}
        strokeWidth={strokeWidth}
        fill={fillColor ?? DEFAULT_NODE_FILL}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
      />
    </>
  );
}

function NodeHiddenRequirementMarker({
  strokeWidth,
  strokeColor,
  x,
  y,
  width,
  height,
  anchor,
}: {
  strokeWidth: number;
  strokeColor?: string;
  x: number;
  y: number;
  width: number;
  height: number;
  anchor: "middle" | "left";
}) {
  // Set the radius to 1 to create a dot.
  const dotRadius = 1;
  const xPosition = anchor === "middle" ? x + width / 2 : x + width / 4;
  // Arbitrary spacing between the dots;
  const xSpacing = 7;
  // For the nodes where we position in the middle we need to take into account the "Edit" button
  // making it necessary to be into a heigher position.
  const yPosition = anchor === "middle" ? y + height - 18 : y + height - 11;

  return (
    <>
      <circle
        r={dotRadius}
        cx={xPosition - xSpacing}
        cy={yPosition}
        strokeWidth={strokeWidth}
        fill={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
      />
      <circle
        r={dotRadius}
        cx={xPosition}
        cy={yPosition}
        strokeWidth={strokeWidth}
        fill={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
      />
      <circle
        r={dotRadius}
        cx={xPosition + xSpacing}
        cy={yPosition}
        strokeWidth={strokeWidth}
        fill={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
        stroke={strokeColor ?? DEFAULT_NODE_STROKE_COLOR}
      />
    </>
  );
}
