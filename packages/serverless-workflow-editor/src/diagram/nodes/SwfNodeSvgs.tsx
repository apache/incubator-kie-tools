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

export function EventstateSvg(__props: NodeSvgProps) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { ...props },
  } = normalize(__props);

  const rx =
    typeof height === "number"
      ? height / 10
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 20
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
    </>
  );
}

export function OperationstateSvg(__props: NodeSvgProps) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { ...props },
  } = normalize(__props);

  const rx =
    typeof height === "number"
      ? height / 10
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 20
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
    </>
  );
}

export function SwitchstateSvg(__props: NodeSvgProps) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { ...props },
  } = normalize(__props);

  const rx =
    typeof height === "number"
      ? height / 10
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 20
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
    </>
  );
}

export function SleepstateSvg(__props: NodeSvgProps) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { ...props },
  } = normalize(__props);

  const rx =
    typeof height === "number"
      ? height / 10
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 20
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
    </>
  );
}

export function ParallelstateSvg(__props: NodeSvgProps) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { ...props },
  } = normalize(__props);

  const rx =
    typeof height === "number"
      ? height / 10
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 20
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
    </>
  );
}

export function InjectstateSvg(__props: NodeSvgProps) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { ...props },
  } = normalize(__props);

  const rx =
    typeof height === "number"
      ? height / 10
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 20
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
    </>
  );
}

export function ForeachstateSvg(__props: NodeSvgProps) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { ...props },
  } = normalize(__props);

  const rx =
    typeof height === "number"
      ? height / 10
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 20
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
    </>
  );
}

export function CallbackstateSvg(__props: NodeSvgProps) {
  const {
    strokeWidth,
    x,
    y,
    width,
    height,
    fillColor,
    strokeColor,
    props: { ...props },
  } = normalize(__props);

  const rx =
    typeof height === "number"
      ? height / 10
      : (() => {
          throw new Error("Can't calculate rx based on a string height.");
        })();

  const ry =
    typeof width === "number"
      ? width / 20
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
    </>
  );
}

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
