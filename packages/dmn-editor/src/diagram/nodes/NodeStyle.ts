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

import React, { useMemo } from "react";
import { DMNDI15__DMNStyle } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { NodeType } from "../connections/graphStructure";
import { NODE_TYPES } from "./NodeTypes";
import { NodeLabelPosition } from "./NodeSvgs";

export interface NodeStyle {
  fontCssProperties: React.CSSProperties;
  shapeStyle: ShapeStyle;
}

export interface ShapeStyle {
  fillColor: string;
  strokeColor: string;
  strokeWidth: number;
}

export interface DmnFontStyle {
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

export function useNodeStyle(args: {
  dmnStyle?: Normalized<DMNDI15__DMNStyle>;
  nodeType?: NodeType;
  isEnabled?: boolean;
}): NodeStyle {
  const fillColor = useMemo(
    () => getNodeShapeFillColor({ dmnStyle: args.dmnStyle, nodeType: args.nodeType, isEnabled: args.isEnabled }),
    [args.dmnStyle, args.isEnabled, args.nodeType]
  );

  const strokeColor = useMemo(
    () => getNodeShapeStrokeColor({ dmnStyle: args.dmnStyle, isEnabled: args.isEnabled }),
    [args.dmnStyle, args.isEnabled]
  );

  const dmnFontStyle = useMemo(
    () => getDmnFontStyle({ dmnStyle: args.dmnStyle, isEnabled: args.isEnabled }),
    [args.dmnStyle, args.isEnabled]
  );

  return useMemo(
    () =>
      getNodeStyle({
        fillColor,
        strokeColor,
        dmnFontStyle,
      }),
    [fillColor, dmnFontStyle, strokeColor]
  );
}

export function getNodeStyle({
  fillColor,
  strokeColor,
  dmnFontStyle,
}: {
  fillColor: string;
  strokeColor: string;
  dmnFontStyle: DmnFontStyle;
}): NodeStyle {
  return {
    fontCssProperties: getFontCssProperties(dmnFontStyle),
    shapeStyle: {
      fillColor,
      strokeColor,
      strokeWidth: DEFAULT_NODE_STROKE_WIDTH,
    },
  };
}

export function getNodeShapeFillColor(args: {
  dmnStyle?: Normalized<DMNDI15__DMNStyle> | undefined;
  nodeType?: NodeType | undefined;
  isEnabled?: boolean | undefined;
}) {
  const blue = args.dmnStyle?.["dmndi:FillColor"]?.["@_blue"];
  const green = args.dmnStyle?.["dmndi:FillColor"]?.["@_green"];
  const red = args.dmnStyle?.["dmndi:FillColor"]?.["@_red"];

  const opacity =
    args.nodeType === NODE_TYPES.decisionService ||
    args.nodeType === NODE_TYPES.group ||
    args.nodeType === NODE_TYPES.textAnnotation
      ? 0.1
      : DEFAULT_NODE_OPACITY;

  if (!args.isEnabled || blue === undefined || green === undefined || red === undefined) {
    return `rgba(${DEFAULT_NODE_RED_FILL}, ${DEFAULT_NODE_GREEN_FILL}, ${DEFAULT_NODE_BLUE_FILL}, ${opacity})`;
  }

  return `rgba(${red}, ${green}, ${blue}, ${opacity})`;
}

export function getNodeShapeStrokeColor(args: {
  dmnStyle?: Normalized<DMNDI15__DMNStyle> | undefined;
  isEnabled?: boolean | undefined;
}) {
  const blue = args.dmnStyle?.["dmndi:StrokeColor"]?.["@_blue"];
  const green = args.dmnStyle?.["dmndi:StrokeColor"]?.["@_green"];
  const red = args.dmnStyle?.["dmndi:StrokeColor"]?.["@_red"];

  if (!args.isEnabled || blue === undefined || green === undefined || red === undefined) {
    return DEFAULT_NODE_STROKE_COLOR;
  }
  return `rgba(${red}, ${green}, ${blue}, 1)`;
}

export function getDmnFontStyle(args: {
  dmnStyle?: Normalized<DMNDI15__DMNStyle> | undefined;
  isEnabled?: boolean | undefined;
}): DmnFontStyle {
  const blue = args.dmnStyle?.["dmndi:FontColor"]?.["@_blue"];
  const green = args.dmnStyle?.["dmndi:FontColor"]?.["@_green"];
  const red = args.dmnStyle?.["dmndi:FontColor"]?.["@_red"];

  const fontColor =
    !args.isEnabled || blue === undefined || green === undefined || red === undefined
      ? DEFAULT_FONT_COLOR
      : `rgba(${red}, ${green}, ${blue}, 1)`;

  return {
    bold: args.isEnabled ? args.dmnStyle?.["@_fontBold"] ?? false : false,
    italic: args.isEnabled ? args.dmnStyle?.["@_fontItalic"] ?? false : false,
    underline: args.isEnabled ? args.dmnStyle?.["@_fontUnderline"] ?? false : false,
    strikeThrough: args.isEnabled ? args.dmnStyle?.["@_fontStrikeThrough"] ?? false : false,
    family: args.isEnabled ? args.dmnStyle?.["@_fontFamily"] : undefined,
    size: args.isEnabled ? args.dmnStyle?.["@_fontSize"] : undefined,
    color: fontColor,
    fill: fontColor,
  };
}

export function getFontCssProperties(dmnFontStyle?: DmnFontStyle): React.CSSProperties {
  let textDecoration = "";
  if (dmnFontStyle?.underline) {
    textDecoration += "underline ";
  }
  if (dmnFontStyle?.strikeThrough) {
    textDecoration += "line-through";
  }

  // Using default values here ensures that the editable Diagram rendered by ReactFlow and the SVG generated are the closest possible.
  return {
    fontWeight: dmnFontStyle?.bold ? "bold" : "",
    fontStyle: dmnFontStyle?.italic ? "italic" : "",
    fontFamily: dmnFontStyle?.family ?? "arial",
    textDecoration,
    fontSize: dmnFontStyle?.size ?? "16px",
    color: dmnFontStyle?.color ?? "black",
    fill: dmnFontStyle?.fill ?? "black",
    lineHeight: "1.5em", // This needs to be em `em` otherwise `@visx/text` breaks when generating the SVG.
  };
}

type NodeLabelPositionProps =
  | { nodeType: Extract<NodeType, typeof NODE_TYPES.inputData>; isAlternativeInputDataShape: boolean }
  | { nodeType: Exclude<NodeType, typeof NODE_TYPES.inputData>; isAlternativeInputDataShape?: boolean };

export function getNodeLabelPosition({
  nodeType,
  isAlternativeInputDataShape,
}: NodeLabelPositionProps): NodeLabelPosition {
  switch (nodeType) {
    case NODE_TYPES.inputData:
      if (isAlternativeInputDataShape) {
        return "center-bottom";
      }
      return "center-center";
    case NODE_TYPES.decision:
      return "center-center";
    case NODE_TYPES.bkm:
      return "center-center";
    case NODE_TYPES.decisionService:
      return "top-center";
    case NODE_TYPES.knowledgeSource:
      return "center-left";
    case NODE_TYPES.textAnnotation:
      return "top-left";
    case NODE_TYPES.group:
      return "top-left";
    case NODE_TYPES.unknown:
      return "center-center";
    default:
      assertUnreachable(nodeType);
  }
}

export function assertUnreachable(_x: never): never {
  throw new Error("Didn't expect to get here: " + _x);
}
