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

export const NODE_TYPES = {
  eventState: "node_eventState" as const,
  operationState: "node_operationState" as const,
  switchState: "node_switchState" as const,
  sleepState: "node_sleepState" as const,
  parallelState: "node_parallelState" as const,
  injectState: "node_injectState" as const,
  foreachState: "node_forEachState" as const,
  callbackState: "node_callbackState" as const,
  unknown: "node_unknown" as const,
};
