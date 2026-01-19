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

import { PositionalNodeHandleId } from "../diagram/connections/PositionalNodeHandles";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { _checkIsValidConnection } from "../diagram/connections/isValidConnection";
import { EDGE_TYPES } from "../diagram/edges/SwfEdgeTypes";
import { Unpacked } from "../tsExt/tsExt";
import { SwfDiagramNodeData } from "../diagram/nodes/SwfNodes";
import { AutoPositionedEdgeMarker } from "../diagram/edges/AutoPositionedEdgeMarker";
import { Specification } from "@serverlessworkflow/sdk-typescript";
import * as RF from "reactflow";

export function addEdge({
  definitions,
  sourceNode,
  targetNode,
  edge,
}: {
  definitions: Specification.IWorkflow;
  sourceNode: {
    type: NodeType;
    data: SwfDiagramNodeData;
    href: string;
    bounds: RF.Rect;
  };
  targetNode: {
    type: NodeType;
    data: SwfDiagramNodeData;
    href: string;
    bounds: RF.Rect;
  };
  edge: {
    type: EdgeType;
    targetHandle: PositionalNodeHandleId;
    sourceHandle: PositionalNodeHandleId;
    autoPositionedEdgeMarker: AutoPositionedEdgeMarker | undefined;
  };
}) {
  if (!_checkIsValidConnection(sourceNode, targetNode, edge.type)) {
    throw new Error(`SWF MUTATION: Invalid structure: (${sourceNode.type}) --${edge.type}--> (${targetNode.type}) `);
  }

  const newEdgeId =
    sourceNode.data.swfObject?.name + "_" + targetNode.data.swfObject?.name + "_" + edge.type.toString();

  // Compensation
  if (edge.type === EDGE_TYPES.compensationTransition) {
    sourceNode.data.swfObject!.compensatedBy = targetNode.data.swfObject!.name;
  }
  // Transition
  else if (edge.type === EDGE_TYPES.transition && sourceNode.type !== "node_switchState") {
    const state = sourceNode.data.swfObject as Exclude<Unpacked<Specification.States>, Specification.Switchstate>;
    state.transition = targetNode.data.swfObject!.name;
  }
  // Error
  else if (
    edge.type === EDGE_TYPES.errorTransition &&
    sourceNode.type !== "node_injectState" &&
    sourceNode.type !== "node_forEachState" &&
    sourceNode.type !== "node_callbackState"
  ) {
    const state = sourceNode.data.swfObject as Exclude<
      Unpacked<Specification.States>,
      Specification.IInjectstate | Specification.IForeachstate | Specification.ICallbackstate
    >;
    const error = new Specification.Error(definitions).asPlainObject();
    error.transition = targetNode.data.swfObject!.name!;

    if (!state.onErrors) {
      state.onErrors = [error];
    } else {
      // remove pevious if there is a match
      state.onErrors!.forEach((error) => {
        let index = -1;
        if (typeof error.transition === "object") {
          const transition: Specification.ITransition = error.transition;
          if (transition.nextState === targetNode.data.swfObject!.name!) {
            index = state.onErrors!.indexOf(error, 0);
          }
        } else if (error.transition === targetNode.data.swfObject!.name!) {
          index = state.onErrors!.indexOf(error, 0);
        }
        if (index > -1) {
          state.onErrors!.splice(index, 1);
        }
      });

      state.onErrors.push(error);
    }
  }
  // DefaultCondition
  else if (edge.type === EDGE_TYPES.defaultConditionTransition && sourceNode.type === "node_switchState") {
    const state = sourceNode.data.swfObject as Specification.Switchstate;
    state.defaultCondition = new Specification.Defaultconditiondef(definitions).asPlainObject();
    state.defaultCondition.transition = targetNode.data.swfObject!.name!;
  }
  // Conditions
  else if (edge.type === EDGE_TYPES.dataConditionTransition && sourceNode.type === "node_switchState") {
    const state = sourceNode.data.swfObject as Exclude<
      Unpacked<Specification.States>,
      Specification.IInjectstate | Specification.IForeachstate | Specification.ICallbackstate
    >;

    if ("dataConditions" in state) {
      const dataSwitch: Specification.IDatabasedswitchstate = state;
      const dataCondition = new Specification.Transitiondatacondition(definitions).asPlainObject();
      dataCondition.transition = targetNode.data.swfObject!.name!;

      if (!dataSwitch.dataConditions) {
        dataSwitch.dataConditions = [dataCondition];
      } else {
        // remove pevious if there is a match
        dataSwitch.dataConditions!.forEach((condition) => {
          if ("transition" in condition) {
            const dataTransition: Specification.ITransitiondatacondition = condition;
            let index = -1;

            if (typeof dataTransition.transition === "object") {
              const transition: Specification.ITransition = dataTransition.transition;
              if (transition.nextState === targetNode.data.swfObject!.name!) {
                index = dataSwitch.dataConditions!.indexOf(condition, 0);
              }
            } else if (dataTransition.transition === targetNode.data.swfObject!.name!) {
              index = dataSwitch.dataConditions!.indexOf(condition, 0);
            }

            if (index > -1) {
              dataSwitch.dataConditions!.splice(index, 1);
            }
          }
        });

        dataSwitch.dataConditions.push(dataCondition);
      }
    }

    if ("eventConditions" in state) {
      const eventSwitch: Specification.IEventbasedswitchstate = state;
      const eventCondition = new Specification.Transitioneventcondition(definitions).asPlainObject();
      eventCondition.transition = targetNode.data.swfObject!.name!;

      if (!eventSwitch.eventConditions) {
        eventSwitch.eventConditions = [eventCondition];
      } else {
        // remove pevious if there is a match
        eventSwitch.eventConditions!.forEach((condition) => {
          if ("eventRef" in condition) {
            const eventTransition = condition as Specification.ITransitioneventcondition;
            let index = -1;

            if (typeof eventTransition.transition === "object") {
              const transition = eventTransition.transition;
              if (transition.nextState === targetNode.data.swfObject!.name!) {
                index = eventSwitch.eventConditions!.indexOf(condition, 0);
              }
            } else if (eventTransition.transition === targetNode.data.swfObject!.name!) {
              index = eventSwitch.eventConditions!.indexOf(condition, 0);
            }

            if (index > -1) {
              eventSwitch.eventConditions!.splice(index, 1);
            }
          }
        });

        eventSwitch.eventConditions.push(eventCondition);
      }
    }
  }

  return newEdgeId;
}
