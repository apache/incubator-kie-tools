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

import { BpmnLatestModel } from "@kie-tools/bpmn-marshaller";
import { getNewBpmnIdRandomizer } from "../idRandomizer/bpmnIdRandomizer";
import { State } from "../store/Store";

export type Normalized<T> = WithRequiredDeep<T, "@_id">;

type WithRequiredDeep<T, K extends keyof any> = T extends undefined
  ? T
  : T extends Array<infer U>
    ? Array<WithRequiredDeep<U, K>>
    : { [P in keyof T]: WithRequiredDeep<T[P], K> } & (K extends keyof T
        ? { [P in K]-?: NonNullable<WithRequiredDeep<T[P], K>> }
        : T);

export function normalize(model: BpmnLatestModel): State["bpmn"]["model"] {
  getNewBpmnIdRandomizer()
    .ack({
      json: model.definitions.import,
      type: "BPMN20__tDefinitions",
      attr: "import",
    })
    .ack({
      json: model.definitions["bpmndi:BPMNDiagram"],
      type: "BPMN20__tDefinitions",
      attr: "bpmndi:BPMNDiagram",
    })
    .randomize({ skipAlreadyAttributedIds: true });

  const normalizedModel = model as Normalized<BpmnLatestModel>;

  return normalizedModel;
}
