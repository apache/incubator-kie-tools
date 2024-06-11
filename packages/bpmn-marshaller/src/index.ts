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

import { Meta, getInstanceNs, domParser, getParser } from "@kie-tools/xml-parser-ts";
import {
  meta as bpmn20meta,
  elements as bpmn20elements,
  subs as bpmn20subs,
  root as bpmn20root,
  ns as bpmn20ns,
} from "./schemas/bpmn-2_0/ts-gen/meta";

import { BPMN20__tDefinitions } from "./schemas/bpmn-2_0/ts-gen/types";

type BpmnDefinitions = { definitions: BPMN20__tDefinitions };

type BpmnMarshaller = {
  parser: { parse(): BpmnDefinitions };
  builder: { build(json: BpmnDefinitions): string };
  instanceNs: Map<string, string>;
  root: { element: string; type: string };
  meta: Meta;
  version: "2.0";
};

export function getMarshaller(xml: string): BpmnMarshaller {
  const domdoc = domParser.getDomDocument(xml);
  const instanceNs = getInstanceNs(domdoc);

  const p = getParser<BpmnDefinitions>({
    ns: bpmn20ns,
    meta: bpmn20meta,
    subs: bpmn20subs,
    elements: bpmn20elements,
    root: bpmn20root,
  });

  return {
    instanceNs,
    version: "2.0",
    root: bpmn20root,
    meta: bpmn20meta,
    parser: { parse: () => p.parse({ type: "domdoc", domdoc, instanceNs }).json },
    builder: { build: (json: BpmnDefinitions) => p.build({ json, instanceNs }) },
  };
}
