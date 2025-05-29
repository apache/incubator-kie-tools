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

import { ns as bpmn20ns } from "../bpmn-2_0/ts-gen/meta";

export type UniqueNameIndex = Map<string, string>;

export const BPMN20_SPEC = {
  isValidName: (id: string, name: string | undefined, allUniqueNames: UniqueNameIndex): boolean => {
    return true; // FIXME: Tiago: Implement (valid name)
  },
};

export const allBpmnImportNamespaces = new Set([bpmn20ns.get("")!]);

export const KIE_BPMN_UNKNOWN_NAMESPACE = "https://kie.apache.org/bpmn/unknown";

export const BOUNDARY_EVENT_CANCEL_ACTIVITY_DEFAULT_VALUE = true;
export const START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE = true;
