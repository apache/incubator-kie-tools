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

import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/SwfNodeTypes";
import { NodeNature, nodeNatures } from "./NodeNature";
import { Specification } from "@serverlessworkflow/sdk-typescript";
import * as RF from "reactflow";
import { Unpacked } from "../tsExt/tsExt";

export function addStandaloneNode({
  definitions,
  newNode,
}: {
  definitions: Specification.IWorkflow;
  newNode: { type: NodeType; bounds: RF.Rect };
}) {
  let newState: Unpacked<Specification.States>;
  const nature = nodeNatures[newNode.type];

  if (nature === NodeNature.SWF_STATE) {
    definitions.states ??= [] as unknown as Specification.States;

    newState = switchExpression(newNode.type as Exclude<NodeType, "node_unknown">, {
      [NODE_TYPES.callbackState]: {
        ...new Specification.Callbackstate(definitions).asPlainObject(),
        ...getNextAvailableName("New Callback State", definitions.states, 0),
      },
      [NODE_TYPES.eventState]: {
        ...new Specification.Eventstate(definitions).asPlainObject(),
        ...getNextAvailableName("New Event State", definitions.states, 0),
      },
      [NODE_TYPES.foreachState]: {
        ...new Specification.Foreachstate(definitions).asPlainObject(),
        ...getNextAvailableName("New ForEach State", definitions.states, 0),
      },
      [NODE_TYPES.injectState]: {
        ...new Specification.Injectstate(definitions).asPlainObject(),
        ...getNextAvailableName("New Inject State", definitions.states, 0),
      },
      [NODE_TYPES.operationState]: {
        ...new Specification.Operationstate(definitions).asPlainObject(),
        ...getNextAvailableName("New Operation State", definitions.states, 0),
      },
      [NODE_TYPES.parallelState]: {
        ...new Specification.Parallelstate(definitions).asPlainObject(),
        ...getNextAvailableName("New Parallel State", definitions.states, 0),
      },
      [NODE_TYPES.sleepState]: {
        ...new Specification.Sleepstate(definitions).asPlainObject(),
        ...getNextAvailableName("New Sleep State", definitions.states, 0),
      },
      [NODE_TYPES.switchState]: {
        ...new Specification.Databasedswitchstate(definitions).asPlainObject(),
        ...getNextAvailableName("New Data Switch State", definitions.states, 0),
      },
    });

    definitions.states?.push(newState);
  } else {
    throw new Error(`Unknown node usage '${nature}'.`);
  }

  return newState.name;
}

export function getNextAvailableName(
  name: string,
  states: Specification.States,
  count: number
): { id: string; name: string } {
  if (!states || states.length === 0) {
    return { id: name, name };
  }
  states.forEach((state) => {
    if (state.name === name) {
      const index = name.lastIndexOf("_");
      if (index >= 0) {
        name = name.slice(0, index);
      }
      count++;
      name = getNextAvailableName(name + "_" + count, states, count).name;
    }
  });

  return { id: name, name };
}
