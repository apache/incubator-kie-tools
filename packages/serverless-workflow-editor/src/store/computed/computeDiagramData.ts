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
import { snapShapeDimensions, snapShapePosition } from "../../diagram/SnapGrid";
import { EdgeType, NodeType } from "../../diagram/connections/graphStructure";
import { SwfDiagramEdgeData } from "../../diagram/edges/SwfEdges";
import {
  SwfEdge,
  SwfEdgeTypes,
  SwfAdjacencyList,
  EdgeVisitor,
  NodeVisitor,
  getAdjMatrix,
  traverse,
} from "../../diagram/graph/graph";
import { getNodeTypeFromSwfObject } from "../../diagram/maths/SwfMaths";
import { DEFAULT_NODE_SIZES, MIN_NODE_SIZES } from "../../diagram/nodes/SwfDefaultSizes";
import { ___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches } from "../../diagram/nodes/SwfNodeSvgs";
import { SwfDiagramNodeData, NodeSwfObjects } from "../../diagram/nodes/SwfNodes";
import { TypeOrReturnType } from "../ComputedStateCache";
import { Computed, State } from "../Store";
import { Specification } from "@severlessworkflow/sdk-typescript";
import { Unpacked } from "../../tsExt/tsExt";

export const NODE_LAYERS = {
  NODES: 1000, // We need a difference > 1000 here, since ReactFlow will add 1000 to the z-index when a node is selected.
};

interface EdgeArgs {
  id: string;
  swfObject: SwfDiagramEdgeData["swfObject"];
  type: EdgeType;
  source: string;
  target: string;
}

type AckEdge = (args: EdgeArgs) => RF.Edge<SwfDiagramEdgeData>;

type AckNode = (swfObject: NodeSwfObjects, index: number) => RF.Node<SwfDiagramNodeData> | undefined;

export function computeDiagramData(
  diagram: State["diagram"],
  definitions: State["swf"]["model"],
  indexedSwf: TypeOrReturnType<Computed["indexedSwf"]>,
  nodesPosition: Map<string, RF.XYPosition>,
  edgesWaypoints: Map<string, RF.XYPosition[]>,
  requiresLayout?: boolean
) {
  // console.time("nodes");
  ___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches.flag =
    !___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches.flag;

  const selectedNodesById = new Map<string, RF.Node<SwfDiagramNodeData>>();
  const selectedEdgesById = new Map<string, RF.Edge<SwfDiagramEdgeData>>();
  const selectedNodeTypes = new Set<NodeType>();
  const nodesById = new Map<string, RF.Node<SwfDiagramNodeData>>();
  const edgesById = new Map<string, RF.Edge<SwfDiagramEdgeData>>();
  const edges: RF.Edge<SwfDiagramEdgeData>[] = [];
  const swfEdges: SwfEdge[] = [];
  const swfAdjacencyList: SwfAdjacencyList = new Map();

  if (requiresLayout && (!nodesPosition || nodesPosition.size == 0)) {
    return {
      swfEdges,
      swfAdjacencyList,
      nodes: [] as RF.Node<SwfDiagramNodeData>[],
      edges,
      edgesById,
      nodesById,
      selectedNodeTypes,
      selectedNodesById,
      selectedEdgesById,
    };
  }

  const { selectedNodes, draggingNodes, selectedEdges } = {
    selectedNodes: new Set(diagram._selectedNodes),
    draggingNodes: new Set(diagram.draggingNodes),
    selectedEdges: new Set(diagram._selectedEdges),
  };

  // console.time("edges");
  const ackEdge: AckEdge = ({ id, type, swfObject, source, target }) => {
    const data = {
      swfObject,
      swfEdge: id ? indexedSwf.swfEdgesBySwfRef.get(id) : undefined,
      swfSource: indexedSwf.swfNodesByHref.get(source),
      swfTarget: indexedSwf.swfNodesByHref.get(target),
    };

    const edge: RF.Edge<SwfDiagramEdgeData> = {
      data,
      id,
      type,
      source,
      target,
      selected: selectedEdges.has(id),
    };

    edgesById.set(edge.id, edge);
    if (edge.selected) {
      selectedEdgesById.set(edge.id, edge);
    }

    edges.push(edge);

    swfEdges.push({ id, sourceId: source, targetId: target, swfObject });

    const targetAdjancyList = swfAdjacencyList.get(target);
    if (!targetAdjancyList) {
      swfAdjacencyList.set(target, { dependencies: new Set([source]) });
    } else {
      targetAdjancyList.dependencies.add(source);
    }

    return edge;
  };

  // trasitions
  ackTransitionEdges(definitions.states, ackEdge);

  // console.timeEnd("edges");

  // nodes
  const ackNode: AckNode = (swfObject, index) => {
    const type = getNodeTypeFromSwfObject(swfObject);
    if (!type) {
      return undefined;
    }

    const nodeName = swfObject!.name!;

    const data: SwfDiagramNodeData = {
      swfObject,
      index,
      parentRfNode: undefined,
    };

    // if there is no position calculated for the node in the state go for x:0 y:0
    let position = nodesPosition.get(nodeName);
    if (!position) {
      position = { x: 0, y: 0 };
      nodesPosition.set(nodeName, { ...position });
    }

    const bounds: RF.Rect = { ...DEFAULT_NODE_SIZES[type]({ snapGrid: diagram.snapGrid }), ...position };

    const newNode: RF.Node<SwfDiagramNodeData> = {
      id: nodeName,
      type,
      selected: selectedNodes.has(nodeName),
      dragging: draggingNodes.has(nodeName),
      position: snapShapePosition(diagram.snapGrid, bounds),
      data,
      zIndex: NODE_LAYERS.NODES,
      style: {
        ...snapShapeDimensions(diagram.snapGrid, bounds, MIN_NODE_SIZES[type]({ snapGrid: diagram.snapGrid })),
      },
    };

    nodesById.set(newNode.id, newNode);
    if (newNode.selected) {
      selectedNodesById.set(newNode.id, newNode);
      selectedNodeTypes.add(newNode.type as NodeType);
    }

    return newNode;
  };

  const nodes: RF.Node<SwfDiagramNodeData>[] = [
    ...(definitions.states ?? []).flatMap((swfObject, index) => {
      const newNode = ackNode(swfObject, index);
      return newNode ? [newNode] : [];
    }),
  ];

  // Selected edges go to the end of the array. This is necessary because z-index doesn't work on SVGs.
  const sortedEdges = edges
    .filter((e) => nodesById.has(e.source) && nodesById.has(e.target))
    .sort((a, b) => Number(selectedEdges.has(a.id)) - Number(selectedEdges.has(b.id)));

  // console.timeEnd("nodes");
  if (diagram.overlays.enableNodeHierarchyHighlight) {
    assignClassesToHighlightedHierarchyNodes(diagram._selectedNodes, nodesById, edgesById, swfEdges);
  }

  //clean up unused nodes position
  if (nodesPosition) {
    for (const key of nodesPosition.keys()) {
      if (!nodesById.get(key)) {
        nodesPosition.delete(key);
      }
    }
  }

  //clean up unused edges waypoits
  if (edgesWaypoints) {
    for (const key of edgesWaypoints.keys()) {
      if (!edgesById.get(key)) {
        edgesWaypoints.delete(key);
      }
    }
  }

  return {
    swfEdges,
    swfAdjacencyList,
    nodes: nodes,
    edges: sortedEdges,
    edgesById,
    nodesById,
    selectedNodeTypes,
    selectedNodesById,
    selectedEdgesById,
  };
}

function ackTransitionEdges(states: Specification.States, ackEdge: AckEdge) {
  for (let i = 0; i < states.length; i++) {
    const state = states[i];

    getEdgeArgsForNode(state, i).forEach((edge) => {
      ackEdge(edge);
    });
  }
}

function getEdgeArgsForNode(node: Unpacked<Specification.States>, index: number): EdgeArgs[] {
  const edges: EdgeArgs[] = [];

  //compensation
  if (node.compensatedBy) {
    edges.push(getCompensationTrasitionArgs(index, node));
  }

  switch (node.type) {
    case "sleep": {
      const state: Specification.Sleepstate = node;

      if (state.transition) {
        edges.push(getTransitionArgs(index, state, state.transition));
      }

      if (state.onErrors) {
        getErrorTrasitionsArgs(index, state, state.onErrors).forEach((errorargs) => edges.push(errorargs));
      }

      break;
    }
    case "event": {
      const state: Specification.Eventstate = node;

      if (state.transition) {
        edges.push(getTransitionArgs(index, state, state.transition));
      }

      if (state.onErrors) {
        getErrorTrasitionsArgs(index, state, state.onErrors).forEach((errorargs) => edges.push(errorargs));
      }

      break;
    }
    case "operation": {
      const state: Specification.Operationstate = node;

      if (state.transition) {
        edges.push(getTransitionArgs(index, state, state.transition));
      }

      if (state.onErrors) {
        getErrorTrasitionsArgs(index, state, state.onErrors).forEach((errorargs) => edges.push(errorargs));
      }

      break;
    }
    case "parallel": {
      const state: Specification.Parallelstate = node;

      if (state.transition) {
        edges.push(getTransitionArgs(index, state, state.transition));
      }

      if (state.onErrors) {
        getErrorTrasitionsArgs(index, state, state.onErrors).forEach((errorargs) => edges.push(errorargs));
      }

      break;
    }
    case "switch": {
      const state: Specification.Switchstate = node;

      if (state instanceof Specification.Databasedswitchstate) {
        const switchData: Specification.Databasedswitchstate = state;
        if (switchData.dataConditions) {
          getConditionTrasitionsArgs(index, node, switchData.dataConditions).forEach((dataConditions) =>
            edges.push(dataConditions)
          );
        }
      } else {
        const switchEvent: Specification.Eventbasedswitchstate = state;
        if (switchEvent.eventConditions) {
          getConditionTrasitionsArgs(index, node, switchEvent.eventConditions).forEach((eventConditions) =>
            edges.push(eventConditions)
          );
        }
      }

      if (state.defaultCondition!.transition) {
        edges.push(getDefaultTransitionArgs(index, state, state.defaultCondition.transition));
      }

      if (state.onErrors) {
        getErrorTrasitionsArgs(index, state, state.onErrors).forEach((errorargs) => edges.push(errorargs));
      }

      break;
    }
    case "inject": {
      const state: Specification.Injectstate = node;

      if (state.transition) {
        edges.push(getTransitionArgs(index, state, state.transition));
      }

      break;
    }
    case "foreach": {
      const state: Specification.Foreachstate = node;

      if (state.transition) {
        edges.push(getTransitionArgs(index, state, state.transition));
      }

      break;
    }
    case "callback": {
      const state: Specification.Callbackstate = node;

      if (state.transition) {
        edges.push(getTransitionArgs(index, state, state.transition));
      }

      break;
    }
    default: {
      break;
    }
  }

  return edges;
}

function getCompensationTrasitionArgs(index: number, node: Unpacked<Specification.States>): EdgeArgs {
  return buildEdgeArgs(index, node, node.compensatedBy!, "edge_compensation", "compensationTransition");
}

function getTransitionArgs(
  index: number,
  node: Unpacked<Specification.States>,
  transition: string | Specification.Transition
): EdgeArgs {
  let transitionStr: string | undefined = undefined;
  if (transition instanceof Specification.Transition) {
    transitionStr = transition.nextState;
  } else {
    transitionStr = transition;
  }

  return buildEdgeArgs(index, node, transitionStr, "edge_transition", "transition");
}

function getErrorTrasitionsArgs(
  index: number,
  node: Unpacked<Specification.States>,
  errors: Specification.Error[]
): EdgeArgs[] {
  const edgeArgs: EdgeArgs[] = [];

  errors.forEach((error) => {
    let transitionStr: string | undefined = undefined;
    if (error.transition instanceof Specification.Transition) {
      transitionStr = error.transition.nextState;
    } else {
      transitionStr = error.transition;
    }

    edgeArgs.push(buildEdgeArgs(index, node, transitionStr, "edge_error", "errorTransition"));
  });

  return edgeArgs;
}

function getDefaultTransitionArgs(
  index: number,
  node: Unpacked<Specification.States>,
  transition: string | Specification.Transition
): EdgeArgs {
  let transitionStr: string | undefined = undefined;
  if (transition instanceof Specification.Transition) {
    transitionStr = transition.nextState;
  } else {
    transitionStr = transition;
  }

  return buildEdgeArgs(index, node, transitionStr, "edge_defaultCondition", "defaultConditionTransition");
}

function getConditionTrasitionsArgs(
  index: number,
  node: Unpacked<Specification.States>,
  conditions: Specification.Datacondition[] | Specification.Eventcondition[]
): EdgeArgs[] {
  const conditionTransitions: EdgeArgs[] = [];

  conditions.forEach((condition) => {
    // Specification.Enddatacondition won't be created for now

    if (condition instanceof Specification.Transitiondatacondition) {
      const transitionDataCondition: Specification.Transitiondatacondition = condition;

      let transitionStr: string | undefined = undefined;
      if (transitionDataCondition.transition instanceof Specification.Transition) {
        transitionStr = transitionDataCondition.transition.nextState;
      } else {
        transitionStr = transitionDataCondition.transition;
      }

      conditionTransitions.push(
        buildEdgeArgs(index, node, transitionStr, "edge_dataCondition", "dataConditionTransition")
      );
    }

    // Specification.Enddeventcondition won't be created for now

    if (condition instanceof Specification.Transitioneventcondition) {
      const transitionEventCondition: Specification.Transitioneventcondition = condition;

      let transitionStr: string | undefined = undefined;
      if (transitionEventCondition.transition instanceof Specification.Transition) {
        transitionStr = transitionEventCondition.transition.nextState;
      } else {
        transitionStr = transitionEventCondition.transition;
      }

      conditionTransitions.push(
        buildEdgeArgs(index, node, transitionStr, "edge_eventCondition", "eventConditionTransition")
      );
    }
  });

  return conditionTransitions;
}

function buildEdgeArgs(
  index: number,
  node: Unpacked<Specification.States>,
  target: string,
  type: EdgeType,
  swfType: SwfEdgeTypes
): EdgeArgs {
  return {
    id: node.name + "_" + target + "_" + swfType.toString(),
    swfObject: {
      type: node.type!,
      id: node.name!,
      edgeType: swfType,
      index,
    },
    type: type,
    source: node.name!,
    target: target,
  };
}

export function assignClassesToHighlightedHierarchyNodes(
  selected: string[],
  nodesById: Map<string, RF.Node>,
  edgesById: Map<string, RF.Edge>,
  drgEdges: SwfEdge[]
) {
  const nodeVisitor: NodeVisitor = (nodeId, traversalDirection) => {
    const node = nodesById.get(nodeId);
    if (node) {
      node.className = `hierarchy ${traversalDirection}`;
    }
  };

  const edgeVisitor: EdgeVisitor = (edge, traversalDirection) => {
    const rfEdge = edgesById.get(edge.id);
    if (rfEdge) {
      rfEdge.className = `hierarchy ${traversalDirection}`;
    }
  };

  const __selectedSet = new Set(selected);
  const __adjMatrix = getAdjMatrix(drgEdges);

  traverse(__adjMatrix, __selectedSet, selected, "up", nodeVisitor, edgeVisitor);
  traverse(__adjMatrix, __selectedSet, selected, "down", nodeVisitor, edgeVisitor); // Traverse "down" after "up" because when there's a cycle, highlighting a node as a dependency is preferable.
}
