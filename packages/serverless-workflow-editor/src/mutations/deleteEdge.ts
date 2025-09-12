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

import { Specification } from "@severlessworkflow/sdk-typescript";
import { Unpacked } from "../tsExt/tsExt";
import { SwfEdge } from "../diagram/graph/graph";

export function deleteEdge({ definitions, edge }: { definitions: Specification.Workflow; edge: SwfEdge }) {
  const swfObjects: Specification.States = definitions.states ?? [];

  const swfObjectIndex = swfObjects.findIndex((d) => d["name"] === edge.swfObject.id);
  if (swfObjectIndex < 0) {
    throw new Error(`SWF MUTATION: Can't find SWF element with ID ${edge.swfObject.id}`);
  }

  // Get the source node pointing to node to be deleted
  const state = swfObjects.at(swfObjectIndex)!;

  switch (edge.swfObject.edgeType) {
    case "compensationTransition": {
      state.compensatedBy = undefined;
      break;
    }
    case "transition": {
      if (state.type != "switch") {
        state.transition = undefined;
      }
      break;
    }
    case "errorTransition": {
      if (state.type != "foreach" && state.type != "inject" && state.type != "callback") {
        const s = state as Exclude<
          Unpacked<Specification.States>,
          Specification.Injectstate | Specification.Foreachstate | Specification.Callbackstate
        >;
        const errorIndex = s.onErrors!.findIndex((error) => {
          if (error.transition instanceof Specification.Transition) {
            return (error.transition as Specification.Transition).nextState === edge.targetId;
          }
          return error.transition === edge.targetId;
        });

        s.onErrors!.splice(errorIndex, 1);
      }
      break;
    }
    case "defaultConditionTransition": {
      if (state.type === "switch") {
        const s = state as Specification.Switchstate;
        s.defaultCondition.transition = "";
      }
      break;
    }
    case "dataConditionTransition": {
      if (state.type === "switch") {
        const s = state as Specification.Databasedswitchstate;
        const condIndex = s.dataConditions!.findIndex((cond) => {
          if (cond instanceof Specification.Transitiondatacondition) {
            const dataCond = cond as Specification.Transitiondatacondition;
            if (dataCond.transition instanceof Specification.Transition) {
              return (dataCond.transition as Specification.Transition).nextState === edge.targetId;
            } else {
              return dataCond.transition === edge.targetId;
            }
          }
          return false;
        });

        s.onErrors!.splice(condIndex, 1);
      }
      break;
    }
    case "eventConditionTransition": {
      if (state.type === "switch") {
        const s = state as Specification.Eventbasedswitchstate;
        const condIndex = s.eventConditions!.findIndex((cond) => {
          if (cond instanceof Specification.Transitioneventcondition) {
            const eventCond = cond as Specification.Transitioneventcondition;
            if (eventCond.transition instanceof Specification.Transition) {
              return (eventCond.transition as Specification.Transition).nextState === edge.targetId;
            } else {
              return eventCond.transition === edge.targetId;
            }
          }
          return false;
        });

        s.onErrors!.splice(condIndex, 1);
      }
      break;
    }
  }
}
