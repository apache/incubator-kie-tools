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

import { DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { XmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import * as Elk from "elkjs/lib/elk.bundled.js";
import * as RF from "reactflow";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { PositionalNodeHandleId } from "../diagram/connections/PositionalNodeHandles";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { addEdge } from "../mutations/addEdge";
import { repositionNode } from "../mutations/repositionNode";
import { resizeNode } from "../mutations/resizeNode";
import { updateDecisionServiceDividerLine } from "../mutations/updateDecisionServiceDividerLine";
import { State } from "../store/Store";
import { AutolayoutParentNode, FAKE_MARKER, visitNodeAndNested } from "../autolayout/autoLayoutInfo";
import { ExternalDmnsIndex, ExternalModelsIndex } from "../DmnEditor";

export function applyAutoLayoutToDrd({
  state,
  __readonly_autoLayoutedInfo,
  __readonly_parentNodesById,
  __readonly_nodesById,
  __readonly_edgesById,
  __readonly_edges,
  __readonly_dmnShapesByHref,
  __readonly_drdIndex,
  __readonly_dmnObjectNamespace,
  __readonly_externalDmnsIndex,
  __readonly_externalModelsByNamespace,
}: {
  state: State;
  __readonly_autoLayoutedInfo: {
    isHorizontal: boolean;
    nodes: Elk.ElkNode[] | undefined;
    edges: Elk.ElkExtendedEdge[] | undefined;
  };
  __readonly_parentNodesById: Map<string, AutolayoutParentNode>;
  __readonly_nodesById: Map<string, RF.Node<DmnDiagramNodeData, string | undefined>>;
  __readonly_edgesById: Map<string, RF.Edge<DmnDiagramEdgeData>>;
  __readonly_edges: RF.Edge<DmnDiagramEdgeData>[];
  __readonly_dmnShapesByHref: Map<
    string,
    Normalized<DMNDI15__DMNShape> & {
      index: number;
      dmnElementRefQName: XmlQName;
    }
  >;
  __readonly_drdIndex: number;
  __readonly_dmnObjectNamespace: string | undefined;
  __readonly_externalDmnsIndex: ExternalDmnsIndex;
  __readonly_externalModelsByNamespace: ExternalModelsIndex | undefined;
}) {
  // 7. Update all nodes positions skipping empty groups, which will be positioned manually after all nodes are done being repositioned.
  const autolayoutedElkNodesById = new Map<string, Elk.ElkNode>();

  for (const topLevelElkNode of __readonly_autoLayoutedInfo.nodes ?? []) {
    visitNodeAndNested(topLevelElkNode, { x: 100, y: 100 }, (elkNode, positionOffset) => {
      if (elkNode.id.includes(FAKE_MARKER)) {
        return;
      }

      autolayoutedElkNodesById.set(elkNode.id, elkNode);

      const nodeId = elkNode.id;
      const node = __readonly_nodesById.get(nodeId)!;

      repositionNode({
        definitions: state.dmn.model.definitions,
        drdIndex: __readonly_drdIndex,
        controlWaypointsByEdge: new Map(),
        change: {
          nodeType: node.type as NodeType,
          type: "absolute",
          position: {
            x: elkNode.x! + positionOffset.x,
            y: elkNode.y! + positionOffset.y,
          },
          selectedEdges: [...__readonly_edgesById.keys()],
          shapeIndex: node.data?.shape.index,
          sourceEdgeIndexes: __readonly_edges.flatMap((e) =>
            e.source === nodeId && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
          ),
          targetEdgeIndexes: __readonly_edges.flatMap((e) =>
            e.target === nodeId && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
          ),
        },
      });
    });
  }

  // 8. Resize all nodes using the sizes calculated by ELK.
  for (const topLevelElkNode of __readonly_autoLayoutedInfo.nodes ?? []) {
    visitNodeAndNested(topLevelElkNode, { x: 0, y: 0 }, (elkNode) => {
      if (elkNode.id.includes(FAKE_MARKER)) {
        return;
      }

      const nodeId = elkNode.id;
      const node = __readonly_nodesById.get(nodeId)!;

      resizeNode({
        definitions: state.dmn.model.definitions,
        drdIndex: __readonly_drdIndex,
        __readonly_dmnShapesByHref: __readonly_dmnShapesByHref,
        snapGrid: state.diagram.snapGrid,
        __readonly_dmnObjectNamespace,
        __readonly_externalDmnsIndex,
        __readonly_href: nodeId,
        __readonly_dmnObjectId: node.data.dmnObject?.["@_id"] ?? "",
        change: {
          index: node.data.index,
          isExternal: !!node.data.dmnObjectQName.prefix,
          nodeType: node.type as NodeType,
          dimension: {
            "@_width": elkNode.width!,
            "@_height": elkNode.height!,
          },
          shapeIndex: node.data?.shape.index,
          sourceEdgeIndexes: __readonly_edges.flatMap((e) =>
            e.source === nodeId && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
          ),
          targetEdgeIndexes: __readonly_edges.flatMap((e) =>
            e.target === nodeId && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
          ),
        },
      });
    });
  }

  // 9. Updating Decision Service divider lines after all nodes are repositioned and resized.
  for (const [parentNodeId] of __readonly_parentNodesById) {
    const parentNode = __readonly_nodesById.get(parentNodeId);
    if (parentNode?.type !== NODE_TYPES.decisionService) {
      continue;
    }

    const elkNode = autolayoutedElkNodesById.get(parentNodeId);
    if (!elkNode) {
      throw new Error(`Couldn't find Decision Service with id ${parentNode.id} at the autolayouted nodes map`);
    }

    /**
     * The second children of a Decision Service elkNode is a node representing the Encapsulated section.
     * It's Y position will be exactly where the divider line should be.
     */
    const dividerLinerLocalYPosition = elkNode.children?.[1]?.y;
    if (!dividerLinerLocalYPosition) {
      throw new Error(
        `Couldn't find second child (which represents the Encapuslated Decision section) of Decision Service with id ${parentNode.id} at the autolayouted nodes map`
      );
    }

    updateDecisionServiceDividerLine({
      definitions: state.dmn.model.definitions,
      drdIndex: __readonly_drdIndex,
      __readonly_dmnShapesByHref,
      __readonly_dmnObjectNamespace,
      __readonly_externalDmnsIndex,
      drgElementIndex: parentNode.data.index,
      shapeIndex: parentNode.data.shape.index,
      snapGrid: state.diagram.snapGrid,
      localYPosition: dividerLinerLocalYPosition,
      __readonly_decisionServiceHref: parentNode.id,
    });
  }

  // 10. Update the edges. Edges always go from top to bottom, removing waypoints.
  for (const elkEdge of __readonly_autoLayoutedInfo.edges ?? []) {
    if (elkEdge.id.includes(FAKE_MARKER)) {
      continue;
    }

    const edge = __readonly_edgesById.get(elkEdge.id)!;

    const sourceNode = __readonly_nodesById.get(elkEdge.sources[0])!;
    const targetNode = __readonly_nodesById.get(elkEdge.targets[0])!;

    // If the target is an external node, we don't have to create the edge.
    if (targetNode.data.dmnObjectQName.prefix) {
      continue;
    }

    addEdge({
      definitions: state.dmn.model.definitions,
      drdIndex: __readonly_drdIndex,
      edge: {
        autoPositionedEdgeMarker: undefined,
        type: edge.type as EdgeType,
        targetHandle: PositionalNodeHandleId.Bottom,
        sourceHandle: PositionalNodeHandleId.Top,
      },
      sourceNode: {
        type: sourceNode.type as NodeType,
        href: sourceNode.id,
        data: sourceNode.data,
        bounds: sourceNode.data.shape["dc:Bounds"]!,
        shapeId: sourceNode.data.shape["@_id"],
      },
      targetNode: {
        type: targetNode.type as NodeType,
        href: targetNode.id,
        data: targetNode.data,
        bounds: targetNode.data.shape["dc:Bounds"]!,
        index: targetNode.data.index,
        shapeId: targetNode.data.shape["@_id"],
      },
      keepWaypoints: false,
      externalModelsByNamespace: __readonly_externalModelsByNamespace,
    });
  }
}
