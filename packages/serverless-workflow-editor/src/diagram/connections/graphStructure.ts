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

import { NODE_TYPES } from "../nodes/SwfNodeTypes";
import { EDGE_TYPES } from "../edges/SwfEdgeTypes";

type Values<T> = T[keyof T];

export type NodeType = Values<typeof NODE_TYPES>;
export type EdgeType = Values<typeof EDGE_TYPES>;

// All outgoing connetions are allowed for all nodes for now
export const graphStructure: Map<NodeType, Map<EdgeType, Set<NodeType>>> = new Map([
  [
    NODE_TYPES.callbackState,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.compensationTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.transition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
    ]),
  ],
  [
    NODE_TYPES.eventState,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.compensationTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.errorTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.transition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
    ]),
  ],
  [
    NODE_TYPES.foreachState,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.compensationTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.transition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
    ]),
  ],
  [
    NODE_TYPES.injectState,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.compensationTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.transition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
    ]),
  ],
  [
    NODE_TYPES.operationState,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.compensationTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.errorTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.transition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
    ]),
  ],
  [
    NODE_TYPES.parallelState,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.compensationTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.errorTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.transition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
    ]),
  ],
  [
    NODE_TYPES.sleepState,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.compensationTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.errorTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.transition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
    ]),
  ],
  [
    NODE_TYPES.switchState,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.compensationTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.dataConditionTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.defaultConditionTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.errorTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.eventConditionTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
    ]),
  ],
  [
    NODE_TYPES.callbackState,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.compensationTransition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
      [
        EDGE_TYPES.transition,
        new Set([
          NODE_TYPES.callbackState,
          NODE_TYPES.eventState,
          NODE_TYPES.foreachState,
          NODE_TYPES.injectState,
          NODE_TYPES.operationState,
          NODE_TYPES.parallelState,
          NODE_TYPES.sleepState,
          NODE_TYPES.switchState,
        ]),
      ],
    ]),
  ],
]);

export const outgoingStructure = {
  [NODE_TYPES.callbackState]: {
    nodes: outgoingNodes(NODE_TYPES.callbackState),
    edges: outgoingEdges(NODE_TYPES.callbackState),
  },
  [NODE_TYPES.eventState]: {
    nodes: outgoingNodes(NODE_TYPES.eventState),
    edges: outgoingEdges(NODE_TYPES.eventState),
  },
  [NODE_TYPES.foreachState]: {
    nodes: outgoingNodes(NODE_TYPES.foreachState),
    edges: outgoingEdges(NODE_TYPES.foreachState),
  },
  [NODE_TYPES.injectState]: {
    nodes: outgoingNodes(NODE_TYPES.injectState),
    edges: outgoingEdges(NODE_TYPES.injectState),
  },
  [NODE_TYPES.operationState]: {
    nodes: outgoingNodes(NODE_TYPES.operationState),
    edges: outgoingEdges(NODE_TYPES.operationState),
  },
  [NODE_TYPES.parallelState]: {
    nodes: outgoingNodes(NODE_TYPES.parallelState),
    edges: outgoingEdges(NODE_TYPES.parallelState),
  },
  [NODE_TYPES.sleepState]: {
    nodes: outgoingNodes(NODE_TYPES.sleepState),
    edges: outgoingEdges(NODE_TYPES.sleepState),
  },
  [NODE_TYPES.switchState]: {
    nodes: outgoingNodes(NODE_TYPES.switchState),
    edges: outgoingEdges(NODE_TYPES.switchState),
  },
};

export const containment = new Map<NodeType, Set<NodeType>>([
  // Containment is not supported for now keeping the settings
  //[NODE_TYPES.somenode, new Set([NODE_TYPES.some node])],
]);

function outgoingNodes(srcNodeType: NodeType): NodeType[] {
  // FIXME : Duplicates are causing issues in edit mode because we have all edges x all nodes combination
  // Probably the best approach is to return possible nodes connections filtered by edge type.
  // return Array.from((graphStructure.get(srcNodeType) ?? new Map()).values()).flatMap((tgt) => [...tgt]);
  return [
    NODE_TYPES.callbackState,
    NODE_TYPES.eventState,
    NODE_TYPES.foreachState,
    NODE_TYPES.injectState,
    NODE_TYPES.operationState,
    NODE_TYPES.parallelState,
    NODE_TYPES.sleepState,
  ];
}

function outgoingEdges(srcNodeType: NodeType): EdgeType[] {
  return Array.from((graphStructure.get(srcNodeType) ?? new Map()).keys());
}

export function getDefaultEdgeTypeBetween(source: NodeType, target: NodeType): EdgeType | undefined {
  const edges = getEdgeTypesBetween(source, target);
  if (edges.length > 1) {
    console.debug(
      `Multiple edges possible for ${source} --> ${target}. Choosing first one in structure definition: ${edges[0]}.`
    );
  }

  return edges[0];
}

export function getEdgeTypesBetween(source: NodeType, target: NodeType): EdgeType[] {
  const sourceStructure = graphStructure.get(source);
  if (!sourceStructure) {
    return [];
  }

  const possibleEdges: EdgeType[] = [];
  for (const [e, t] of [...sourceStructure.entries()]) {
    if (t.has(target)) {
      possibleEdges.push(e);
    }
  }

  return possibleEdges;
}
