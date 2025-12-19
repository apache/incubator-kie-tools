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

import ELK, * as Elk from "elkjs/lib/elk.bundled.js";
import { Edge, Node } from "reactflow";
import { NodeType } from "../diagram/connections/graphStructure";
import { SwfDiagramEdgeData } from "../diagram/edges/SwfEdges";
import { DEFAULT_NODE_SIZES } from "../diagram/nodes/SwfDefaultSizes";
import { SwfDiagramNodeData } from "../diagram/nodes/SwfNodes";
import { SnapGrid } from "../store/Store";

const elk = new ELK();

export const ELK_OPTIONS = {
  algorithm: "layered",
  "layered.layering.strategy": "INTERACTIVE",
  "layered.nodePlacement.strategy": "SIMPLE",
  "nodePlacement.bk.fixedAlignment": "BALANCED",
  "elk.direction": "DOWN",
  edgeRouting: "ORTHOGONAL",
  "elk.nodePlacement.favorStraightEdges": "true",
  "layered.mergeEdges": "false",
  "spacing.nodeNode": "60",
  "spacing.edgeEdgeBetweenLayers": "15",
  "spacing.edgeNodeBetweenLayers": "35",
  "layered.spacing.nodeNodeBetweenLayers": "100",
  "elk.padding": "[top=-40,left=20,bottom=0,right=0]",
  "layering.strategy": "LONGEST_PATH_SOURCE",
};

export const FAKE_MARKER = "__$FAKE$__";

// Get calculated layout from ELKjs
export async function getAutoLayoutedInfo({
  __readonly_snapGrid,
  __readonly_nodesById,
  __readonly_edgesById,
  __readonly_nodes,
}: {
  __readonly_snapGrid: SnapGrid;
  __readonly_nodesById: Map<string, Node<SwfDiagramNodeData, string | undefined>>;
  __readonly_edgesById: Map<string, Edge<SwfDiagramEdgeData>>;
  __readonly_nodes: Node<SwfDiagramNodeData, string | undefined>[];
}) {
  // map all the nodes to elkNodes
  const elkNodes = __readonly_nodes.flatMap((node) => {
    const defaultSize = DEFAULT_NODE_SIZES[node.type as NodeType]({
      snapGrid: __readonly_snapGrid,
    });
    const elkNode: Elk.ElkNode = {
      id: node.id,
      width: defaultSize["width"],
      height: defaultSize["height"],
      children: [],
      layoutOptions: {},
    };

    return [elkNode];
  });

  // Concatenate real and fake edges to pass to ELK.
  const elkEdges = [
    ...[...__readonly_edgesById.values()].flatMap((e) => {
      // Not all nodes are present in all SWF
      if (__readonly_nodesById.has(e.source) && __readonly_nodesById.has(e.target)) {
        return {
          id: e.id,
          sources: [e.source],
          targets: [e.target],
        };
      } else {
        return [];
      }
    }),
  ];

  // Run ELK.
  const autoLayoutedInfo = await runElk(elkNodes, elkEdges, ELK_OPTIONS);

  return autoLayoutedInfo;
}

async function runElk(
  nodes: Elk.ElkNode[],
  edges: { id: string; sources: string[]; targets: string[] }[],
  options: Elk.LayoutOptions = {}
): Promise<{ isHorizontal: boolean; nodes: Elk.ElkNode[] | undefined; edges: Elk.ElkExtendedEdge[] | undefined }> {
  const isHorizontal = options?.["elk.direction"] === "DOWN";

  const graph: Elk.ElkNode = {
    id: "root",
    layoutOptions: options,
    children: nodes,
    edges,
  };

  const layoutedGraph = await elk.layout(graph);
  return {
    isHorizontal,
    nodes: layoutedGraph.children,
    edges: layoutedGraph.edges as any[],
  };
}

export function visitNodeAndNested(
  elkNode: Elk.ElkNode,
  positionOffset: { x: number; y: number },
  visitor: (elkNode: Elk.ElkNode, positionOffset: { x: number; y: number }) => void
) {
  visitor(elkNode, positionOffset);
  for (const nestedNode of elkNode.children ?? []) {
    visitNodeAndNested(
      nestedNode,
      {
        x: elkNode.x! + positionOffset.x,
        y: elkNode.y! + positionOffset.y,
      },
      visitor
    );
  }
}
