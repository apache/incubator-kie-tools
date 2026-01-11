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

export type GraphStructure<N, E> = Map<N, Map<E, Set<N>>>;

export enum ContainmentMode {
  IGNORE = "IGNORE", // Used for Group nodes of BPMN/DMN, for exmaple.
  INSIDE = "INSIDE", // Used for DMN Decision Services or BPMN Lanes, for example.
  BORDER = "BORDER", // Used for BPMN Boundary Events, for example.
  INVALID_INSIDE = "INVALID_INSIDE", // Used for when nodes are dragged inside a container node that doesn't allow them.
  INVALID_BORDER = "INVALID_BORDER", // Used for when nodes are dragged at the border of a container node that doesn't allow them.
  INVALID_NON_INSIDE_CONTAINER = "INVALID_NON_INSIDE_CONTAINER", // Used for when nodes are dragged inside a node that is not a container node.
  INVALID_IGNORE = "INVALID_IGNORE", // Used for when the containment is invalid, but shouldn't alter the container node's depiction.
}

export type ContainmentMap<N> = Map<N, Map<ContainmentMode, Set<N>>>;

function outgoingNodes<N, E>(graphStructure: GraphStructure<N, E>, srcNodeType: N): N[] {
  return Array.from((graphStructure.get(srcNodeType) ?? new Map()).values()).flatMap((tgt) => [...tgt]);
}

function outgoingEdges<N, E>(graphStructure: GraphStructure<N, E>, srcNodeType: N): E[] {
  return Array.from((graphStructure.get(srcNodeType) ?? new Map()).keys());
}

export function getDefaultEdgeTypeBetween<N, E>(
  graphStructure: GraphStructure<N, E>,
  source: N,
  target: N
): E | undefined {
  const edges = getEdgeTypesBetween(graphStructure, source, target);
  if (edges.length > 1) {
    console.debug(
      `GRAPH STRUCTURE: Multiple edges possible for ${source} --> ${target}. Choosing first one in structure definition: ${edges[0]}.`
    );
  }

  return edges[0];
}

export function getEdgeTypesBetween<N, E>(graphStructure: GraphStructure<N, E>, source: N, target: N): E[] {
  const sourceStructure = graphStructure.get(source);
  if (!sourceStructure) {
    return [];
  }

  const possibleEdges: E[] = [];
  for (const [e, t] of [...sourceStructure.entries()]) {
    if (t.has(target)) {
      possibleEdges.push(e);
    }
  }

  return possibleEdges;
}
