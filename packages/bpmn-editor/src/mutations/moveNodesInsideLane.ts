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

import { State } from "../store/Store";
import { addOrGetLanes } from "./addOrGetLanes";

export function moveNodesInsideLane({
  definitions,
  __readonly_laneId,
  __readonly_nodeIds,
}: {
  definitions: State["bpmn"]["model"]["definitions"];
  __readonly_laneId: string | undefined;
  __readonly_nodeIds: string[];
}) {
  const { lanes } = addOrGetLanes({ definitions });

  const targetLane = lanes.find((s) => s["@_id"] === __readonly_laneId);
  if (!targetLane) {
    throw new Error(`Could not find Lane with ID ${__readonly_laneId}.`);
  }

  const existingFlowNodeRefs = new Set(targetLane.flowNodeRef?.map((s) => s.__$$text));

  targetLane.flowNodeRef ??= [];

  // Only add those which are not present already.
  targetLane.flowNodeRef.push(
    ...__readonly_nodeIds.filter((s) => !existingFlowNodeRefs.has(s)).map((nodeId) => ({ __$$text: nodeId }))
  );
}
