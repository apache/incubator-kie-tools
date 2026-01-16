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

import { NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/SwfNodeTypes";

export enum NodeNature {
  SWF_STATE = "SWF_ELEMENT",
  UNKNOWN = "UNKNOWN",
}

export const nodeNatures: Record<NodeType, NodeNature> = {
  [NODE_TYPES.callbackState]: NodeNature.SWF_STATE,
  [NODE_TYPES.eventState]: NodeNature.SWF_STATE,
  [NODE_TYPES.foreachState]: NodeNature.SWF_STATE,
  [NODE_TYPES.injectState]: NodeNature.SWF_STATE,
  [NODE_TYPES.operationState]: NodeNature.SWF_STATE,
  [NODE_TYPES.parallelState]: NodeNature.SWF_STATE,
  [NODE_TYPES.sleepState]: NodeNature.SWF_STATE,
  [NODE_TYPES.switchState]: NodeNature.SWF_STATE,
  [NODE_TYPES.unknown]: NodeNature.UNKNOWN,
};
