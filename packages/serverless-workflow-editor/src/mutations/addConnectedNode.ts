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

import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/SwfNodeTypes";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { NodeNature, nodeNatures } from "./NodeNature";
import { Specification } from "@serverlessworkflow/sdk-typescript";
import { Unpacked } from "../tsExt/tsExt";
import * as RF from "reactflow";
import { addStandaloneNode } from "./addStandaloneNode";

export function addConnectedNode({
  definitions,
  sourceNode,
  newNode,
  edgeType,
}: {
  definitions: Specification.IWorkflow;
  sourceNode: { type: NodeType; href: string; bounds: RF.Rect };
  newNode: { type: NodeType; bounds: RF.Rect };
  edgeType: EdgeType;
}) {
  let newNodeId: string | undefined;

  const nature = nodeNatures[newNode.type];

  if (nature === NodeNature.SWF_STATE) {
    // Connections are stablished in the parent node so we create a standalone node
    // and then the parent is updated to point to the new node
    newNodeId = addStandaloneNode({ definitions, newNode });

    if (!newNodeId) {
      throw new Error(`SWF MUTATION: Node cannot be created.`);
    }

    const parentState = definitions.states.find((state) => state.name === sourceNode.href);

    if (!parentState) {
      throw new Error(`SWF MUTATION: Parent not found '${sourceNode.href}'.`);
    }

    updateParentTransions(definitions.states, sourceNode, newNodeId, edgeType);
  } else {
    throw new Error(`SWF MUTATION: Unknown node usage '${nature}'.`);
  }

  return newNodeId;
}

export function updateParentTransions(
  states: Specification.States,
  sourceNode: { type: NodeType; href: string; bounds: RF.Rect },
  targetId: string,
  edgeType: EdgeType
) {
  const parentState = states.find((state) => state.name === sourceNode.href);

  if (!parentState) {
    throw new Error(`SWF MUTATION: Parent not found '${sourceNode.href}'.`);
  }

  switch (edgeType) {
    case "edge_compensation": {
      parentState.compensatedBy = getCompensationFromEdge(sourceNode, targetId)?.compensatedBy;
      break;
    }
    case "edge_transition": {
      if (parentState.type != "switch") {
        parentState.transition = getTransitionFromEdge(sourceNode, targetId)?.transition;
      }
      break;
    }
    case "edge_error": {
      if (parentState.type != "foreach" && parentState.type != "inject" && parentState.type != "callback") {
        const state = parentState as Exclude<
          Unpacked<Specification.States>,
          Specification.IInjectstate | Specification.IForeachstate | Specification.ICallbackstate
        >;
        const error = getErrorFromEdge(sourceNode, targetId);

        if (!state.onErrors) {
          state.onErrors = error?.onErrors;
        } else {
          state.onErrors.concat(error!.onErrors!);
        }
      }
      break;
    }
    case "edge_defaultCondition": {
      if (parentState.type === "switch") {
        const state = parentState as Specification.Switchstate;
        state.defaultCondition = getDefaultConditionFromEdge(sourceNode, targetId)!.defaultCondition!;
      }
      break;
    }
    case "edge_dataCondition": {
      if (parentState.type === "switch" && "dataConditions" in parentState) {
        const dataCondition = getDataConditionFromEdge(sourceNode, targetId);
        const state = parentState as Specification.IDatabasedswitchstate;

        if (!state.dataConditions) {
          state.dataConditions = dataCondition!.dataConditions!;
        } else {
          state.dataConditions.concat(dataCondition!.dataConditions!);
        }
      }
      break;
    }
    case "edge_eventCondition": {
      if (parentState.type === "switch" && "eventConditions" in parentState) {
        const eventCondition = getEventConditionFromEdge(sourceNode, targetId);
        const state = parentState as Specification.IEventbasedswitchstate;

        if (!state.eventConditions) {
          state.eventConditions = eventCondition!.eventConditions!;
        } else {
          state.eventConditions.concat(eventCondition!.eventConditions!);
        }
      }
      break;
    }
  }
}

export function getCompensationFromEdge(
  sourceNode: { type: NodeType },
  target: string
): Pick<Specification.ISleepstate, "compensatedBy"> | undefined {
  const compesation:
    | undefined //
    | Required<Pick<Specification.ISleepstate, "compensatedBy">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.callbackState]: { compensatedBy: `${target}` },
    [NODE_TYPES.eventState]: { compensatedBy: `${target}` },
    [NODE_TYPES.foreachState]: { compensatedBy: `${target}` },
    [NODE_TYPES.injectState]: { compensatedBy: `${target}` },
    [NODE_TYPES.operationState]: { compensatedBy: `${target}` },
    [NODE_TYPES.parallelState]: { compensatedBy: `${target}` },
    [NODE_TYPES.sleepState]: { compensatedBy: `${target}` },
    [NODE_TYPES.switchState]: { compensatedBy: `${target}` },
    default: undefined,
  });

  return compesation;
}

export function getTransitionFromEdge(
  sourceNode: { type: NodeType },
  target: string
): Pick<Specification.ISleepstate, "transition"> | undefined {
  const transition:
    | undefined //
    | Required<Pick<Specification.ISleepstate, "transition">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.sleepState]: { transition: `${target}` },
    [NODE_TYPES.eventState]: { transition: `${target}` },
    [NODE_TYPES.operationState]: { transition: `${target}` },
    [NODE_TYPES.parallelState]: { transition: `${target}` },
    [NODE_TYPES.injectState]: { transition: `${target}` },
    [NODE_TYPES.foreachState]: { transition: `${target}` },
    [NODE_TYPES.callbackState]: { transition: `${target}` },
    default: undefined,
  });

  return transition;
}

export function getErrorFromEdge(
  sourceNode: { type: NodeType },
  target: string
): Pick<Specification.ISleepstate, "onErrors"> | undefined {
  const error:
    | undefined //
    | Required<Pick<Specification.ISleepstate, "onErrors">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.sleepState]: {
      onErrors: [new Specification.Error({ errorRef: "", transition: `${target}` }).asPlainObject()],
    },
    [NODE_TYPES.eventState]: {
      onErrors: [new Specification.Error({ errorRef: "", transition: `${target}` }).asPlainObject()],
    },
    [NODE_TYPES.operationState]: {
      onErrors: [new Specification.Error({ errorRef: "", transition: `${target}` }).asPlainObject()],
    },
    [NODE_TYPES.parallelState]: {
      onErrors: [new Specification.Error({ errorRef: "", transition: `${target}` }).asPlainObject()],
    },
    [NODE_TYPES.injectState]: {
      onErrors: [new Specification.Error({ errorRef: "", transition: `${target}` }).asPlainObject()],
    },
    [NODE_TYPES.foreachState]: {
      onErrors: [new Specification.Error({ errorRef: "", transition: `${target}` }).asPlainObject()],
    },
    [NODE_TYPES.callbackState]: {
      onErrors: [new Specification.Error({ errorRef: "", transition: `${target}` }).asPlainObject()],
    },
    default: undefined,
  });

  return error;
}

export function getDefaultConditionFromEdge(
  sourceNode: { type: NodeType },
  target: string
): Pick<Specification.Switchstate, "defaultCondition"> | undefined {
  const defaultCondition:
    | undefined //
    | Required<Pick<Specification.Switchstate, "defaultCondition">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.switchState]: {
      defaultCondition: new Specification.Defaultconditiondef({ transition: `${target}` }).asPlainObject(),
    },
    default: undefined,
  });

  return defaultCondition;
}

export function getDataConditionFromEdge(
  sourceNode: { type: NodeType },
  target: string
): Pick<Specification.IDatabasedswitchstate, "dataConditions"> | undefined {
  const dataCondition:
    | undefined //
    | Pick<Specification.IDatabasedswitchstate, "dataConditions"> = switchExpression(sourceNode.type, {
    [NODE_TYPES.switchState]: {
      dataConditions: [
        new Specification.Transitiondatacondition({ condition: "", transition: `${target}` }).asPlainObject(),
      ],
    },
    default: undefined,
  });

  return dataCondition;
}

export function getEventConditionFromEdge(
  sourceNode: { type: NodeType },
  target: string
): Pick<Specification.IEventbasedswitchstate, "eventConditions"> | undefined {
  const eventCondition:
    | undefined //
    | Required<Pick<Specification.IEventbasedswitchstate, "eventConditions">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.switchState]: {
      eventConditions: [
        new Specification.Transitioneventcondition({ eventRef: "", transition: `${target}` }).asPlainObject(),
      ],
    },
    default: undefined,
  });

  return eventCondition;
}
