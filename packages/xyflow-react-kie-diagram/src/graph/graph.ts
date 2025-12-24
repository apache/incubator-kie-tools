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

export type GraphStructureAdjMatrix = Record<
  string,
  undefined | Record<string, undefined | { direction: GraphStructureHierarchyDirection; edge: GraphStructureEdge }>
>;

export type GraphStructureHierarchyDirection = "up" | "down";

export type GraphStructureEdge = {
  sourceId: string;
  targetId: string;
  id: string;
};

export type GraphStructureAdjacencyList = Map<string, { dependencies: Set<string> }>;

export function getAdjMatrix(edges: GraphStructureEdge[]): GraphStructureAdjMatrix {
  const __adjMatrix: GraphStructureAdjMatrix = {};
  for (const e of edges) {
    __adjMatrix[e.sourceId] ??= {};
    __adjMatrix[e.targetId] ??= {};
    __adjMatrix[e.sourceId]![e.targetId] = { direction: "up", edge: e };
    __adjMatrix[e.targetId]![e.sourceId] = { direction: "down", edge: e };
  }
  return __adjMatrix;
}

export type GraphStructureNodeVisitor = (nodeId: string, traversalDirection: GraphStructureHierarchyDirection) => void;
export type GraphStructureEdgeVisitor = (
  edge: GraphStructureEdge,
  traversalDirection: GraphStructureHierarchyDirection
) => void;

export function traverse(
  __adjMatrix: GraphStructureAdjMatrix,
  originalStartingNodeIds: Set<string>,
  curNodeIds: string[],
  traversalDirection: GraphStructureHierarchyDirection,
  nodeVisitor?: GraphStructureNodeVisitor,
  edgeVisitor?: GraphStructureEdgeVisitor,
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
      if (!(originalStartingNodeIds.has(edge.sourceId) && originalStartingNodeIds.has(edge.targetId))) {
        edgeVisitor?.(edge, traversalDirection);
      }

      return [adjNodeId];
    });
  });

  traverse(__adjMatrix, originalStartingNodeIds, nextNodeIds, traversalDirection, nodeVisitor, edgeVisitor, visited);
}

export function buildHierarchy({ nodeId, edges }: { nodeId: string | undefined | null; edges: GraphStructureEdge[] }) {
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
