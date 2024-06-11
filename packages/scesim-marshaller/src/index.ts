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

import { Meta, domParser, getInstanceNs, getParser } from "@kie-tools/xml-parser-ts";
import {
  meta as scesim18meta,
  root as scesim18root,
  subs as scesim18subs,
  elements as scesim18elements,
  ns as scesim18ns,
} from "./schemas/scesim-1_8/ts-gen/meta";
import { SceSim__ScenarioSimulationModelType } from "./schemas/scesim-1_8/ts-gen/types";

export type SceSimMarshaller = {
  parser: { parse(): SceSimModel };
  builder: { build(json: SceSimModel): string };
  instanceNs: Map<string, string>;
  root: { element: string; type: string };
  meta: Meta;
  version: "1.8";
};

export type SceSimModel = {
  ScenarioSimulationModel: SceSim__ScenarioSimulationModelType;
};

export function getMarshaller(xml: string): SceSimMarshaller {
  const domdoc = domParser.getDomDocument(xml);
  const instanceNs = getInstanceNs(domdoc);

  const p = getParser<SceSimModel>({
    ns: scesim18ns,
    meta: scesim18meta,
    subs: scesim18subs,
    elements: scesim18elements,
    root: scesim18root,
  });

  return {
    instanceNs,
    version: "1.8",
    root: scesim18root,
    meta: scesim18meta,
    parser: { parse: () => p.parse({ type: "domdoc", domdoc, instanceNs }).json },
    builder: { build: (json: SceSimModel) => p.build({ json, instanceNs }) },
  };
}
