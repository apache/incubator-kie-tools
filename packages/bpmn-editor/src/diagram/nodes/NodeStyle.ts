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
import { BpmnNodeType } from "../BpmnDiagramDomain";
import { NODE_TYPES } from "../BpmnDiagramDomain";
import { Normalized } from "../../normalization/normalize";
import { DC__Font } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { NodeLabelPosition } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/NodeSvgs";
import { assertUnreachable } from "../../ts-ext/assertUnreachable";

export interface NodeStyle {
  fontCssProperties: React.CSSProperties;
}

export interface BpmnFontStyle {
  bold: boolean;
  italic: boolean;
  underline: boolean;
  strikeThrough: boolean;
  family?: string;
  size?: number;
}

export const DEFAULT_NODE_RED_FILL = 255;
export const DEFAULT_NODE_GREEN_FILL = 255;
export const DEFAULT_NODE_BLUE_FILL = 255;
export const DEFAULT_NODE_OPACITY = 0.9;
export const DEFAULT_NODE_FILL = `rgba(${DEFAULT_NODE_RED_FILL}, ${DEFAULT_NODE_GREEN_FILL}, ${DEFAULT_NODE_BLUE_FILL}, ${DEFAULT_NODE_OPACITY})`;
export const DEFAULT_NODE_STROKE_COLOR = "rgba(0, 0, 0, 1)";
export const DEFAULT_FONT_COLOR = "rgba(0, 0, 0, 1)";

export function useNodeStyle(args: { nodeType?: BpmnNodeType; isEnabled?: boolean }): NodeStyle {
  const bpmnFontStyle = useMemo(() => getBpmnFontStyle({ isEnabled: args.isEnabled }), [args.isEnabled]);

  return useMemo(() => getNodeStyle({ bpmnFontStyle }), [bpmnFontStyle]);
}

export function getNodeStyle({ bpmnFontStyle }: { bpmnFontStyle: BpmnFontStyle }): NodeStyle {
  return {
    fontCssProperties: getFontCssProperties(bpmnFontStyle),
  };
}

export function getBpmnFontStyle(args: {
  dcFont?: Normalized<DC__Font> | undefined;
  isEnabled?: boolean | undefined;
}): BpmnFontStyle {
  return {
    bold: args.isEnabled ? args.dcFont?.["@_isBold"] ?? false : false,
    italic: args.isEnabled ? args.dcFont?.["@_isItalic"] ?? false : false,
    underline: args.isEnabled ? args.dcFont?.["@_isUnderline"] ?? false : false,
    strikeThrough: args.isEnabled ? args.dcFont?.["@_isStrikeThrough"] ?? false : false,
    family: args.isEnabled ? args.dcFont?.["@_name"] : undefined,
    size: args.isEnabled ? args.dcFont?.["@_size"] : undefined,
  };
}

export function getFontCssProperties(bpmnFontStyle?: BpmnFontStyle): React.CSSProperties {
  let textDecoration = "";
  if (bpmnFontStyle?.underline) {
    textDecoration += "underline ";
  }
  if (bpmnFontStyle?.strikeThrough) {
    textDecoration += "line-through";
  }

  // Using default values here ensures that the editable Diagram rendered by ReactFlow and the SVG generated are the closest possible.
  return {
    fontWeight: bpmnFontStyle?.bold ? "bold" : "",
    fontStyle: bpmnFontStyle?.italic ? "italic" : "",
    fontFamily: bpmnFontStyle?.family ?? "arial",
    textDecoration,
    fontSize: bpmnFontStyle?.size ?? "16px",
    lineHeight: "1.5em", // This needs to be em `em` otherwise `@visx/text` breaks when generating the SVG.
  };
}

export function getNodeLabelPosition({ nodeType }: { nodeType: BpmnNodeType }): NodeLabelPosition {
  switch (nodeType) {
    case NODE_TYPES.subProcess:
      return "top-center";
    case NODE_TYPES.startEvent:
    case NODE_TYPES.intermediateCatchEvent:
    case NODE_TYPES.intermediateThrowEvent:
    case NODE_TYPES.endEvent:
    case NODE_TYPES.gateway:
    case NODE_TYPES.dataObject:
      return "center-bottom";
    case NODE_TYPES.group:
    case NODE_TYPES.textAnnotation:
      return "top-left";
    case NODE_TYPES.lane:
      return "center-left-vertical";
    case NODE_TYPES.task:
    case NODE_TYPES.unknown:
      // case NODE_TYPES.custom:
      return "center-center";
    default:
      assertUnreachable(nodeType);
  }
}
