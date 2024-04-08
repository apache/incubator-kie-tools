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
import {
  AssociationPath,
  AuthorityRequirementPath,
  DmnDiagramEdgeData,
  InformationRequirementPath,
  KnowledgeRequirementPath,
} from "../diagram/edges/Edges";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";
import { Computed, SnapGrid, State } from "../store/Store";
import { EdgeMarkers } from "../diagram/edges/EdgeMarkers";
import { EDGE_TYPES } from "../diagram/edges/EdgeTypes";
import { getSnappedMultiPointAnchoredEdgePath } from "../diagram/edges/getSnappedMultiPointAnchoredEdgePath";
import {
  InputDataNodeSvg,
  DecisionNodeSvg,
  BkmNodeSvg,
  KnowledgeSourceNodeSvg,
  DecisionServiceNodeSvg,
  GroupNodeSvg,
  TextAnnotationNodeSvg,
  UnknownNodeSvg,
  NodeLabelPosition,
  AlternativeInputDataNodeSvg,
} from "../diagram/nodes/NodeSvgs";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { useMemo } from "react";
import {
  assertUnreachable,
  getDmnFontStyle,
  getNodeLabelPosition,
  getNodeShapeFillColor,
  getNodeShapeStrokeColor,
  getNodeStyle,
} from "../diagram/nodes/NodeStyle";
import { NodeType } from "../diagram/connections/graphStructure";
import { buildFeelQNameFromXmlQName } from "../feel/buildFeelQName";
import { Text } from "@visx/text";
import { TypeOrReturnType } from "../store/ComputedStateCache";
import { UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { DataTypeIndex } from "../dataTypes/DataTypes";

export function DmnDiagramSvg({
  nodes,
  edges,
  snapGrid,
  thisDmn,
  importsByNamespace,
  isAlternativeInputDataShape,
  allDataTypesById,
  allTopLevelItemDefinitionUniqueNames,
}: {
  nodes: RF.Node<DmnDiagramNodeData>[];
  edges: RF.Edge<DmnDiagramEdgeData>[];
  snapGrid: SnapGrid;
  thisDmn: State["dmn"];
  importsByNamespace: TypeOrReturnType<Computed["importsByNamespace"]>;
  isAlternativeInputDataShape: boolean;
  allDataTypesById: DataTypeIndex;
  allTopLevelItemDefinitionUniqueNames: UniqueNameIndex;
}) {
  const { nodesSvg, nodesById } = useMemo(() => {
    const nodesById = new Map<string, RF.Node<DmnDiagramNodeData>>();

    const nodesSvg = nodes.map((node) => {
      const { fontCssProperties: fontStyle, shapeStyle } = getNodeStyle({
        fillColor: getNodeShapeFillColor({
          dmnStyle: node.data.shape["di:Style"],
          nodeType: node.type as NodeType,
          isEnabled: true,
        }),
        strokeColor: getNodeShapeStrokeColor({ dmnStyle: node.data.shape["di:Style"], isEnabled: true }),
        dmnFontStyle: getDmnFontStyle({ dmnStyle: node.data.shape["di:Style"], isEnabled: true }),
      });

      nodesById.set(node.id, node);

      const { height, width, ...style } = node.style!;

      const isCollection =
        node.data?.dmnObject?.__$$element === "inputData"
          ? allDataTypesById.get(
              allTopLevelItemDefinitionUniqueNames.get(node.data.dmnObject.variable?.["@_typeRef"] ?? "") ?? ""
            )?.itemDefinition?.["@_isCollection"] ?? false
          : node.data?.dmnObject?.__$$element === "decision"
          ? allDataTypesById.get(
              allTopLevelItemDefinitionUniqueNames.get(node.data.dmnObject.variable?.["@_typeRef"] ?? "") ?? ""
            )?.itemDefinition?.["@_isCollection"] ?? false
          : false;

      const label =
        node.data?.dmnObject?.__$$element === "group"
          ? node.data.dmnObject?.["@_label"] ?? node.data?.dmnObject?.["@_name"] ?? "<Empty>"
          : node.data?.dmnObject?.__$$element === "textAnnotation"
          ? node.data.dmnObject?.["@_label"] ?? node.data?.dmnObject?.text?.__$$text ?? "<Empty>"
          : buildFeelQNameFromXmlQName({
              namedElement: node.data!.dmnObject!,
              importsByNamespace,
              model: thisDmn.model.definitions,
              namedElementQName: node.data!.dmnObjectQName,
              relativeToNamespace: thisDmn.model.definitions["@_namespace"],
            }).full;

      return (
        <g data-kie-dmn-node-id={node.id} key={node.id}>
          {node.type === NODE_TYPES.inputData &&
            (isAlternativeInputDataShape ? (
              <AlternativeInputDataNodeSvg
                width={node.width!}
                height={node.height!}
                x={node.positionAbsolute!.x}
                y={node.positionAbsolute!.y}
                {...style}
                {...shapeStyle}
                isIcon={false}
                isCollection={isCollection}
              />
            ) : (
              <InputDataNodeSvg
                width={node.width!}
                height={node.height!}
                x={node.positionAbsolute!.x}
                y={node.positionAbsolute!.y}
                {...style}
                {...shapeStyle}
                isCollection={isCollection}
              />
            ))}
          {node.type === NODE_TYPES.decision && (
            <DecisionNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.positionAbsolute!.x}
              y={node.positionAbsolute!.y}
              {...style}
              {...shapeStyle}
              isCollection={isCollection}
              hasHiddenRequirements={node.data.hasHiddenRequirements ?? false}
            />
          )}
          {node.type === NODE_TYPES.bkm && (
            <BkmNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.positionAbsolute!.x}
              y={node.positionAbsolute!.y}
              {...style}
              {...shapeStyle}
              hasHiddenRequirements={node.data.hasHiddenRequirements ?? false}
            />
          )}
          {node.type === NODE_TYPES.knowledgeSource && (
            <KnowledgeSourceNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.positionAbsolute!.x}
              y={node.positionAbsolute!.y}
              {...style}
              {...shapeStyle}
              hasHiddenRequirements={node.data.hasHiddenRequirements ?? false}
            />
          )}
          {node.type === NODE_TYPES.decisionService && (
            <DecisionServiceNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.positionAbsolute!.x}
              y={node.positionAbsolute!.y}
              showSectionLabels={false}
              isReadonly={true}
              {...style}
              {...shapeStyle}
            />
          )}
          {node.type === NODE_TYPES.group && (
            <GroupNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.positionAbsolute!.x}
              y={node.positionAbsolute!.y}
              {...style}
              {...(shapeStyle as any)}
            />
          )}
          {node.type === NODE_TYPES.textAnnotation && (
            <TextAnnotationNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.positionAbsolute!.x}
              y={node.positionAbsolute!.y}
              {...style}
              {...shapeStyle}
            />
          )}
          {node.type === NODE_TYPES.unknown && (
            <UnknownNodeSvg
              width={node.width!}
              height={node.height!}
              x={node.positionAbsolute!.x}
              y={node.positionAbsolute!.y}
              {...style}
              {...(shapeStyle as any)}
            />
          )}
          <>
            {label.split("\n").map((labelLine, i) => (
              <Text
                key={i}
                lineHeight={fontStyle.lineHeight}
                style={{ ...fontStyle }}
                dy={`calc(1.5em * ${i})`}
                {...getNodeLabelSvgTextAlignmentProps(
                  node,
                  getNodeLabelPosition({ nodeType: node.type as NodeType, isAlternativeInputDataShape })
                )}
              >
                {labelLine}
              </Text>
            ))}
          </>
        </g>
      );
    });

    return { nodesSvg, nodesById };
  }, [
    allDataTypesById,
    allTopLevelItemDefinitionUniqueNames,
    importsByNamespace,
    isAlternativeInputDataShape,
    nodes,
    thisDmn.model.definitions,
  ]);

  return (
    <>
      <EdgeMarkers />
      {edges.map((e) => {
        const s = nodesById?.get(e.source);
        const t = nodesById?.get(e.target);
        const { path } = getSnappedMultiPointAnchoredEdgePath({
          snapGrid,
          dmnEdge: e.data?.dmnEdge,
          dmnShapeSource: e.data?.dmnShapeSource,
          dmnShapeTarget: e.data?.dmnShapeTarget,
          sourceNodeBounds: {
            x: s?.positionAbsolute?.x,
            y: s?.positionAbsolute?.y,
            width: s?.width,
            height: s?.height,
          },
          targetNodeBounds: {
            x: t?.positionAbsolute?.x,
            y: t?.positionAbsolute?.y,
            width: t?.width,
            height: t?.height,
          },
        });
        return (
          <React.Fragment key={e.id}>
            {e.type === EDGE_TYPES.informationRequirement && <InformationRequirementPath d={path} />}
            {e.type === EDGE_TYPES.knowledgeRequirement && <KnowledgeRequirementPath d={path} />}
            {e.type === EDGE_TYPES.authorityRequirement && (
              <AuthorityRequirementPath d={path} centerToConnectionPoint={true} />
            )}
            {e.type === EDGE_TYPES.association && <AssociationPath d={path} />}
          </React.Fragment>
        );
      })}
      {nodesSvg}
    </>
  );
}

const SVG_NODE_LABEL_TEXT_PADDING_ALL = 10;
const SVG_NODE_LABEL_TEXT_ADDITIONAL_PADDING_TOP_LEFT = 8;

export function getNodeLabelSvgTextAlignmentProps(n: RF.Node<DmnDiagramNodeData>, labelPosition: NodeLabelPosition) {
  switch (labelPosition) {
    case "center-bottom":
      const cbTx = n.position.x! + n.width! / 2;
      const cbTy = n.position.y! + n.height! + 4;
      const cbWidth = n.width!;
      return {
        verticalAnchor: "start",
        textAnchor: "middle",
        transform: `translate(${cbTx},${cbTy})`,
        width: cbWidth,
      } as const;

    case "center-center":
      const ccTx = n.position.x! + n.width! / 2;
      const ccTy = n.position.y! + n.height! / 2;
      const ccWidth = n.width! - 2 * SVG_NODE_LABEL_TEXT_PADDING_ALL;
      return {
        verticalAnchor: "middle",
        textAnchor: "middle",
        transform: `translate(${ccTx},${ccTy})`,
        width: ccWidth,
      } as const;

    case "top-center":
      const tcTx = n.position.x! + n.width! / 2;
      const tcTy = n.position.y! + SVG_NODE_LABEL_TEXT_PADDING_ALL;
      const tcWidth = n.width! - 2 * SVG_NODE_LABEL_TEXT_PADDING_ALL;
      return {
        verticalAnchor: "start",
        textAnchor: "middle",
        transform: `translate(${tcTx},${tcTy})`,
        width: tcWidth,
      } as const;

    case "center-left":
      const clTx = n.position.x! + SVG_NODE_LABEL_TEXT_PADDING_ALL;
      const clTy = n.position.y! + n.height! / 2;
      const clWidth = n.width! - 2 * SVG_NODE_LABEL_TEXT_PADDING_ALL;
      return {
        verticalAnchor: "middle",
        textAnchor: "start",
        transform: `translate(${clTx},${clTy})`,
        width: clWidth,
      } as const;

    case "top-left":
      const tlTx = n.position.x! + SVG_NODE_LABEL_TEXT_PADDING_ALL + SVG_NODE_LABEL_TEXT_ADDITIONAL_PADDING_TOP_LEFT;
      const tlTy = n.position.y! + SVG_NODE_LABEL_TEXT_PADDING_ALL + SVG_NODE_LABEL_TEXT_ADDITIONAL_PADDING_TOP_LEFT;
      const tlWidth =
        n.width! - 2 * SVG_NODE_LABEL_TEXT_PADDING_ALL - 2 * SVG_NODE_LABEL_TEXT_ADDITIONAL_PADDING_TOP_LEFT;
      return {
        verticalAnchor: "start",
        textAnchor: "start",
        transform: `translate(${tlTx},${tlTy})`,
        width: tlWidth,
      } as const;
    default:
      assertUnreachable(labelPosition);
  }
}
