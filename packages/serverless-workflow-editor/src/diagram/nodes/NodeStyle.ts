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

import React from "react";
import { NodeType } from "../connections/graphStructure";
import { NODE_TYPES } from "./SwfNodeTypes";
import { NodeLabelPosition } from "./SwfNodeSvgs";

export interface NodeStyle {
  fontCssProperties: React.CSSProperties;
  shapeStyle: ShapeStyle;
}

export interface ShapeStyle {
  fillColor: string;
  strokeColor: string;
  strokeWidth: number;
}

export interface SwfFontStyle {
  bold: boolean;
  italic: boolean;
  underline: boolean;
  strikeThrough: boolean;
  family?: string;
  size?: number;
  color: string;
  fill: string;
}

export interface Color {
  blue: number;
  green: number;
  red: number;
  opacity: number;
}

export const DEFAULT_NODE_RED_FILL = 255;
export const DEFAULT_NODE_GREEN_FILL = 255;
export const DEFAULT_NODE_BLUE_FILL = 255;
export const DEFAULT_NODE_OPACITY = 0.9;
export const DEFAULT_NODE_FILL = `rgba(${DEFAULT_NODE_RED_FILL}, ${DEFAULT_NODE_GREEN_FILL}, ${DEFAULT_NODE_BLUE_FILL}, ${DEFAULT_NODE_OPACITY})`;
export const DEFAULT_NODE_STROKE_WIDTH = 1.5;
export const DEFAULT_NODE_STROKE_COLOR = "rgba(0, 0, 0, 1)";
export const DEFAULT_FONT_COLOR = "rgba(0, 0, 0, 1)";
export const DEFAULT_FONT_STYLE: SwfFontStyle = {
  bold: false,
  italic: false,
  underline: false,
  strikeThrough: false,
  family: undefined,
  size: undefined,
  color: DEFAULT_FONT_COLOR,
  fill: DEFAULT_FONT_COLOR,
};

export function getNodeStyle({
  fillColor,
  strokeColor,
  swfFontStyle,
}: {
  fillColor: string;
  strokeColor: string;
  swfFontStyle: SwfFontStyle;
}): NodeStyle {
  return {
    fontCssProperties: getFontCssProperties(swfFontStyle),
    shapeStyle: {
      fillColor,
      strokeColor,
      strokeWidth: DEFAULT_NODE_STROKE_WIDTH,
    },
  };
}

export function getFontCssProperties(swfFontStyle?: SwfFontStyle): React.CSSProperties {
  let textDecoration = "";
  if (swfFontStyle?.underline) {
    textDecoration += "underline ";
  }
  if (swfFontStyle?.strikeThrough) {
    textDecoration += "line-through";
  }

  // Using default values here ensures that the editable Diagram rendered by ReactFlow and the SVG generated are the closest possible.
  return {
    fontWeight: swfFontStyle?.bold ? "bold" : "",
    fontStyle: swfFontStyle?.italic ? "italic" : "",
    fontFamily: swfFontStyle?.family ?? "arial",
    textDecoration,
    fontSize: swfFontStyle?.size ?? "16px",
    color: swfFontStyle?.color ?? "black",
    fill: swfFontStyle?.fill ?? "black",
    lineHeight: "1.5em", // This needs to be em `em` otherwise `@visx/text` breaks when generating the SVG.
  };
}

type NodeLabelPositionProps = { nodeType: NodeType };

// Set label position for the different types of nodes
export function getNodeLabelPosition({ nodeType }: NodeLabelPositionProps): NodeLabelPosition {
  switch (nodeType) {
    case NODE_TYPES.callbackState:
      return "center-center";
    case NODE_TYPES.eventState:
      return "center-center";
    case NODE_TYPES.foreachState:
      return "center-center";
    case NODE_TYPES.injectState:
      return "center-center";
    case NODE_TYPES.operationState:
      return "center-center";
    case NODE_TYPES.parallelState:
      return "center-center";
    case NODE_TYPES.sleepState:
      return "center-center";
    case NODE_TYPES.switchState:
      return "center-center";
    case NODE_TYPES.unknown:
      return "center-center";
    default:
      assertUnreachable(nodeType);
  }
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here: " + _x);
}
