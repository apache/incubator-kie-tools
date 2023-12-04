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

import * as RF from "reactflow";
import { DmnDiagramEdgeData } from "../edges/Edges";

export type AdjMatrix = Record<
  string,
  undefined | Record<string, undefined | { direction: HierarchyDirection; edge: RF.Edge }>
>;

export type HierarchyDirection = "up" | "down";

export function getAdjMatrix(edges: RF.Edge<any>[]): AdjMatrix {
  const __adjMatrix: AdjMatrix = {};
  for (const e of edges) {
    __adjMatrix[e.source] ??= {};
    __adjMatrix[e.target] ??= {};
    __adjMatrix[e.source]![e.target] = { direction: "up", edge: e };
    __adjMatrix[e.target]![e.source] = { direction: "down", edge: e };
  }
  return __adjMatrix;
}

export type NodeVisitor = (nodeId: string, traversalDirection: HierarchyDirection) => void;
export type EdgeVisitor = (edge: RF.Edge<DmnDiagramEdgeData>, traversalDirection: HierarchyDirection) => void;

export function traverse(
  __adjMatrix: AdjMatrix,
  originalStartingNodeIds: Set<string>,
  curNodeIds: string[],
  traversalDirection: HierarchyDirection,
  nodeVisitor?: NodeVisitor,
  edgeVisitor?: EdgeVisitor,
  visited = new Set<string>()
) {
  if (curNodeIds.length <= 0) {
    return;
  }

  const nextNodeIds = curNodeIds.flatMap((curNodeId) => {
    if (visited.has(curNodeId)) {
      return [];
    }

    // Only paint nodes if they're not selected.
    if (!originalStartingNodeIds.has(curNodeId)) {
      nodeVisitor?.(curNodeId, traversalDirection);
    }

    const curNodeAdjs = __adjMatrix[curNodeId] ?? {};
    return Object.keys(curNodeAdjs).flatMap((adjNodeId) => {
      const { edge, direction: edgeDirection } = curNodeAdjs[adjNodeId]!;
      if (traversalDirection !== edgeDirection) {
        return [];
      }

      visited.add(curNodeId);

      // Only paint edges if at least one of the endpoints is not selected.
      if (!(originalStartingNodeIds.has(edge.source) && originalStartingNodeIds.has(edge.target))) {
        edgeVisitor?.(edge, traversalDirection);
      }

      return [adjNodeId];
    });
  });

  traverse(__adjMatrix, originalStartingNodeIds, nextNodeIds, traversalDirection, nodeVisitor, edgeVisitor, visited);
}

export function buildHierarchy({
  nodeId,
  edges,
}: {
  nodeId: string | undefined | null;
  edges: RF.Edge<DmnDiagramEdgeData>[];
}) {
  if (!nodeId) {
    return { dependencies: new Set<string>(), dependents: new Set<string>() };
  }

  const selected = [nodeId];
  const __selectedSet = new Set(selected);
  const __adjMatrix = getAdjMatrix(edges);

  const down = new Set<string>();
  traverse(__adjMatrix, __selectedSet, selected, "down", (nodeId) => {
    down.add(nodeId);
  });

  const up = new Set<string>();
  traverse(__adjMatrix, __selectedSet, selected, "up", (nodeId) => {
    up.add(nodeId);
  });

  return { dependencies: down, dependents: up };
}
