import React, { useMemo } from "react";
import { DMNDI15__DMNStyle } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeType } from "../connections/graphStructure";

export interface NodeStyle {
  fontStyle: React.CSSProperties;
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
  dmnStyle?: DMNDI15__DMNStyle;
  nodeType?: NodeType;
  isEnabled?: boolean;
}): NodeStyle {
  const fillColor = useMemo(() => {
    const blue = args.dmnStyle?.["dmndi:FillColor"]?.["@_blue"];
    const green = args.dmnStyle?.["dmndi:FillColor"]?.["@_green"];
    const red = args.dmnStyle?.["dmndi:FillColor"]?.["@_red"];

    const opacity =
      args.nodeType === "node_decisionService" || args.nodeType === "node_group" ? 0.1 : DEFAULT_NODE_OPACITY;
    if (!args.isEnabled || blue === undefined || green === undefined || red === undefined) {
      return `rgba(${DEFAULT_NODE_RED_FILL}, ${DEFAULT_NODE_GREEN_FILL}, ${DEFAULT_NODE_BLUE_FILL}, ${opacity})`;
    }

    return `rgba(${red}, ${green}, ${blue}, ${opacity})`;
  }, [args.dmnStyle, args.nodeType, args.isEnabled]);
  const strokeColor = useMemo(() => {
    const blue = args.dmnStyle?.["dmndi:StrokeColor"]?.["@_blue"];
    const green = args.dmnStyle?.["dmndi:StrokeColor"]?.["@_green"];
    const red = args.dmnStyle?.["dmndi:StrokeColor"]?.["@_red"];

    if (!args.isEnabled || blue === undefined || green === undefined || red === undefined) {
      return DEFAULT_NODE_STROKE_COLOR;
    }
    return `rgba(${red}, ${green}, ${blue}, 1)`;
  }, [args.dmnStyle, args.isEnabled]);

  const fontProperties = useMemo(() => {
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
    };
  }, [args.dmnStyle, args.isEnabled]);

  return {
    fontStyle: getFonteStyle(fontProperties),
    shapeStyle: {
      fillColor,
      strokeColor,
      strokeWidth: DEFAULT_NODE_STROKE_WIDTH,
    },
  };
}

export function getFonteStyle(fontProperties?: DmnFontStyle): React.CSSProperties {
  let textDecoration = "";
  if (fontProperties?.underline) {
    textDecoration += "underline ";
  }
  if (fontProperties?.strikeThrough) {
    textDecoration += "line-through";
  }

  return {
    fontWeight: fontProperties?.bold ? "bold" : "",
    fontStyle: fontProperties?.italic ? "italic" : "",
    fontFamily: fontProperties?.family,
    textDecoration,
    fontSize: fontProperties?.size,
    color: fontProperties?.color,
  };
}
