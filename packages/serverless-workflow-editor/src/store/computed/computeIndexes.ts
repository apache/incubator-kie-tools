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

import { State } from "../Store";
import { Specification } from "@serverlessworkflow/sdk-typescript";
import { SwfEdge, SwfEdgeTypes } from "../../diagram/graph/graph";
import { Unpacked } from "../../tsExt/tsExt";
import { buildEdgeId } from "../../diagram/edges/useKieEdgePath";
import { ITransition } from "@serverlessworkflow/sdk-typescript/lib/definitions/transition";

export function computeIndexedSwf(definitions: State["swf"]["model"]) {
  const swfEdgesBySwfRef = new Map<string, SwfEdge & { index: number }>();
  const swfNodesByHref = new Map<string, Unpacked<Specification.States> & { index: number }>();

  const states = definitions.states;

  for (let i = 0; i < states.length; i++) {
    const state = states[i];

    // SWFShape
    // Use state name as id once the spec 0.8 is based on unique state names
    swfNodesByHref.set(state.name!, { ...state, index: i });

    // SWFNode
    const stateEdges = buildSwfEdgesForNode(state, i);

    for (let j = 0; j < stateEdges.length; j++) {
      swfEdgesBySwfRef.set(stateEdges[j].id!, { ...stateEdges[j], index: swfEdgesBySwfRef.size });
    }
  }
  return {
    swfEdgesBySwfRef,
    swfNodesByHref,
  };
}

function buildSwfEdgesForNode(node: Unpacked<Specification.States>, index: number): SwfEdge[] {
  const edges: SwfEdge[] = [];

  //compensation
  if (node.compensatedBy) {
    edges.push(getCompensationTransition(index, node));
  }

  switch (node.type) {
    case "sleep":
    case "event":
    case "operation":
    case "parallel": {
      const state = node as
        | Specification.ISleepstate
        | Specification.IEventstate
        | Specification.IOperationstate
        | Specification.Parallelstate;

      if (state.transition) {
        edges.push(getTransition(index, state, state.transition));
      }

      if (state.onErrors) {
        getErrorTransitions(index, state, state.onErrors).forEach((errorTransitions) => edges.push(errorTransitions));
      }

      break;
    }
    case "inject":
    case "foreach":
    case "callback": {
      const state = node as Specification.IInjectstate | Specification.IForeachstate | Specification.ICallbackstate;

      if (state.transition) {
        edges.push(getTransition(index, state, state.transition));
      }

      break;
    }

    case "switch": {
      const state = node as Specification.Switchstate;

      if ("dataConditions" in state) {
        const switchData = state as Specification.IDatabasedswitchstate;
        if (switchData.dataConditions) {
          getConditionTransitions(index, node, switchData.dataConditions).forEach((dataConditions) =>
            edges.push(dataConditions)
          );
        }
      } else {
        const switchEvent = state as Specification.IEventbasedswitchstate;
        if (switchEvent.eventConditions) {
          getConditionTransitions(index, node, switchEvent.eventConditions).forEach((eventConditions) =>
            edges.push(eventConditions)
          );
        }
      }

      if (state.defaultCondition?.transition) {
        edges.push(getDefaultTransition(index, state, state.defaultCondition.transition));
      }

      if (state.onErrors) {
        getErrorTransitions(index, state, state.onErrors).forEach((errorTransitions) => edges.push(errorTransitions));
      }

      break;
    }
    default: {
      break;
    }
  }

  return edges;
}

function getCompensationTransition(index: number, node: Unpacked<Specification.States>): SwfEdge {
  return buildSwfEdge(index, node, node.name!, node.compensatedBy!, "compensationTransition");
}

function getTransition(
  index: number,
  node: Unpacked<Specification.States>,
  transition: string | Specification.ITransition
): SwfEdge {
  return buildSwfEdge(index, node, node.name!, getNextNodeFromTransition(transition), "transition");
}

function getErrorTransitions(
  index: number,
  node: Unpacked<Specification.States>,
  errors: Specification.IError[]
): SwfEdge[] {
  const errorTransitions: SwfEdge[] = [];

  errors.forEach((error) => {
    errorTransitions.push(
      buildSwfEdge(index, node, node.name!, getNextNodeFromTransition(error.transition), "errorTransition")
    );
  });

  return errorTransitions;
}

function getDefaultTransition(
  index: number,
  node: Unpacked<Specification.States>,
  transition: string | Specification.ITransition
): SwfEdge {
  return buildSwfEdge(index, node, node.name!, getNextNodeFromTransition(transition), "defaultConditionTransition");
}

function getConditionTransitions(
  index: number,
  node: Unpacked<Specification.States>,
  conditions: Specification.Datacondition[] | Specification.Eventcondition[]
): SwfEdge[] {
  const conditionTransitions: SwfEdge[] = [];

  conditions.forEach((condition) => {
    if ("eventRef" in condition) {
      const transitionEventCondition = condition as Specification.ITransitioneventcondition;
      conditionTransitions.push(
        buildSwfEdge(
          index,
          node,
          node.name!,
          getNextNodeFromTransition(transitionEventCondition.transition),
          "eventConditionTransition"
        )
      );
    } else {
      // Specification.Enddatacondition won't be created for now
      const transitionDataCondition = condition as Specification.ITransitiondatacondition;
      conditionTransitions.push(
        buildSwfEdge(
          index,
          node,
          node.name!,
          getNextNodeFromTransition(transitionDataCondition.transition),
          "dataConditionTransition"
        )
      );
    }
  });

  return conditionTransitions;
}

function buildSwfEdge(
  index: number,
  node: Unpacked<Specification.States>,
  source: string,
  target: string,
  type: SwfEdgeTypes
): SwfEdge {
  return {
    sourceId: source,
    targetId: target,
    id: buildEdgeId(source, target, type),
    swfObject: {
      id: node.name!,
      type: node.type!,
      edgeType: type,
      index: index,
    },
  };
}

function getNextNodeFromTransition(transition: ITransition | string): string {
  if (typeof transition === "object") {
    return transition.nextState;
  }

  return transition;
}
