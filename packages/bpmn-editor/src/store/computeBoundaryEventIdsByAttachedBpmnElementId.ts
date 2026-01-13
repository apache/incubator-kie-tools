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

import { State } from "./Store";

export function computeBoundaryEventIdsByAttachedBpmnElementId(definitions: State["bpmn"]["model"]["definitions"]) {
  const boundaryEventIdsByAttachedBpmnElementId = new Map<string, string[]>();

  for (const boundaryEvent of definitions.rootElement?.filter((s) => s.__$$element === "process")[0].flowElement ??
    []) {
    if (boundaryEvent.__$$element !== "boundaryEvent") {
      continue;
    }

    boundaryEventIdsByAttachedBpmnElementId.set(
      boundaryEvent["@_attachedToRef"], // force line-break
      [...(boundaryEventIdsByAttachedBpmnElementId.get(boundaryEvent["@_attachedToRef"]) ?? []), boundaryEvent["@_id"]]
    );
  }

  return boundaryEventIdsByAttachedBpmnElementId;
}
