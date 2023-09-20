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
  subs as dmn10subs,
  elements as dmn10elements,
  meta as dmn10meta,
  root as dmn10root,
  ns as dmn10ns,
} from "./schemas/dmn-1_0/ts-gen/meta";
import {
  subs as dmn11subs,
  elements as dmn11elements,
  meta as dmn11meta,
  root as dmn11root,
  ns as dmn11ns,
} from "./schemas/dmn-1_1/ts-gen/meta";
import {
  subs as dmn12subs,
  elements as dmn12elements,
  meta as dmn12meta,
  root as dmn12root,
  ns as dmn12ns,
} from "./schemas/dmn-1_2/ts-gen/meta";
import {
  subs as dmn13subs,
  elements as dmn13elements,
  meta as dmn13meta,
  root as dmn13root,
  ns as dmn13ns,
} from "./schemas/dmn-1_3/ts-gen/meta";
import {
  subs as dmn14subs,
  elements as dmn14elements,
  meta as dmn14meta,
  root as dmn14root,
  ns as dmn14ns,
} from "./schemas/dmn-1_4/ts-gen/meta";
import {
  subs as dmn15subs,
  elements as dmn15elements,
  meta as dmn15meta,
  root as dmn15root,
  ns as dmn15ns,
} from "./schemas/dmn-1_5/ts-gen/meta";
// import { dmn3__tDefinitions as DMN10__tDefinitions } from "./schemas/dmn-1_0/ts-gen/types";
// import { dmn__tDefinitions as DMN11__tDefinitions } from "./schemas/dmn-1_1/ts-gen/types";
import { DMN15__tDefinitions } from "./schemas/dmn-1_5/ts-gen/types";
import { DMN14__tDefinitions } from "./schemas/dmn-1_4/ts-gen/types";
import { DMN13__tDefinitions } from "./schemas/dmn-1_3/ts-gen/types";
import { DMN12__tDefinitions } from "./schemas/dmn-1_2/ts-gen/types";
import "./kie-extensions"; // Necessary to include the type extensions and patch the ns maps. Do not remove.

export type DmnMarshaller = {
  parser: { parse(): DmnModel };
  builder: { build(json: DmnModel): string };
  instanceNs: Map<string, string>;
  root: { element: string; type: string };
  meta: Meta;
  version: "1.0" | "1.1" | "1.2" | "1.3" | "1.4" | "1.5";
};

export type DmnModel = {
  definitions: DMN15__tDefinitions; // Keeping the latest version for now, as the other should be retro-compatible with it.
};

const p10 = getParser<DmnModel>({
  ns: dmn10ns,
  meta: dmn10meta,
  subs: dmn10subs,
  elements: dmn10elements,
  root: dmn10root,
});

const p11 = getParser<DmnModel>({
  ns: dmn11ns,
  meta: dmn11meta,
  subs: dmn11subs,
  elements: dmn11elements,
  root: dmn11root,
});

const p12 = getParser<{ definitions: DMN12__tDefinitions }>({
  ns: dmn12ns,
  meta: dmn12meta,
  subs: dmn12subs,
  elements: dmn12elements,
  root: dmn12root,
});

const p13 = getParser<{ definitions: DMN13__tDefinitions }>({
  ns: dmn13ns,
  meta: dmn13meta,
  subs: dmn13subs,
  elements: dmn13elements,
  root: dmn13root,
});

const p14 = getParser<{ definitions: DMN14__tDefinitions }>({
  ns: dmn14ns,
  meta: dmn14meta,
  subs: dmn14subs,
  elements: dmn14elements,
  root: dmn14root,
});

const p15 = getParser<{ definitions: DMN15__tDefinitions }>({
  ns: dmn15ns,
  meta: dmn15meta,
  subs: dmn15subs,
  elements: dmn15elements,
  root: dmn15root,
});

export function getMarshaller(xml: string): DmnMarshaller {
  const domdoc = domParser.getDomDocument(xml);
  const instanceNs = getInstanceNs(domdoc);
  return _getMarshaller(domdoc, instanceNs);
}

export function _getMarshaller(domdoc: Document, instanceNs: Map<string, string>): DmnMarshaller {
  // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  if (instanceNs.get(dmn10ns.get("")!) !== undefined) {
    return {
      instanceNs,
      version: "1.0",
      root: dmn10root,
      meta: dmn10meta,
      parser: { parse: () => p10.parse({ type: "domdoc", domdoc, instanceNs }).json },
      builder: { build: (json: DmnModel) => p10.build({ json, instanceNs }) },
    };
    // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  } else if (instanceNs.get(dmn11ns.get("")!) !== undefined) {
    return {
      instanceNs,
      version: "1.1",
      root: dmn11root,
      meta: dmn11meta,
      parser: { parse: () => p11.parse({ type: "domdoc", domdoc, instanceNs }).json },
      builder: { build: (json: DmnModel) => p11.build({ json, instanceNs }) },
    };
    // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  } else if (instanceNs.get(dmn12ns.get("")!) !== undefined) {
    return {
      instanceNs,
      version: "1.2",
      root: dmn12root,
      meta: dmn12meta,
      parser: { parse: () => p12.parse({ type: "domdoc", domdoc, instanceNs }).json },
      builder: {
        build: (json: { definitions: DMN12__tDefinitions }) => p12.build({ json, instanceNs }),
      },
    };
    // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  } else if (instanceNs.get(dmn13ns.get("")!) !== undefined) {
    return {
      instanceNs,
      version: "1.3",
      root: dmn13root,
      meta: dmn13meta,
      parser: { parse: () => p13.parse({ type: "domdoc", domdoc, instanceNs }).json },
      builder: {
        build: (json: { definitions: DMN13__tDefinitions }) => p13.build({ json, instanceNs }),
      },
    };
    // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  } else if (instanceNs.get(dmn14ns.get("")!) !== undefined) {
    return {
      instanceNs,
      version: "1.4",
      root: dmn14root,
      meta: dmn14meta,
      parser: { parse: () => p14.parse({ type: "domdoc", domdoc, instanceNs }).json },
      builder: {
        build: (json: { definitions: DMN14__tDefinitions }) => p14.build({ json, instanceNs }),
      },
    };
  } else if (instanceNs.get(dmn15ns.get("")!) !== undefined) {
    return {
      instanceNs,
      version: "1.5",
      root: dmn15root,
      meta: dmn15meta,
      parser: { parse: () => p15.parse({ type: "domdoc", domdoc, instanceNs }).json },
      builder: {
        build: (json: { definitions: DMN15__tDefinitions }) => p15.build({ json, instanceNs }),
      },
    };
  } else {
    throw new Error(
      `Unknown version declared for DMN. Instance NS --> '${JSON.stringify([...instanceNs.entries()])}'.`
    );
  }
}

export function upgradeToLatest(
  domdoc: Document,
  instanceNs: Map<string, string>
): { domdoc: Document; instanceNs: Map<string, string> } {
  // const defs = domdoc.documentElement;
  // switch (instanceNs.get("")) {
  //   case dmn10ns.get(""):
  //     defs.setAttribute("a", "0");
  //   case dmn11ns.get(""):
  //     defs.setAttribute("a", "" + (Number(defs.getAttribute("a")) ?? 0) + 1);
  //   case dmn12ns.get(""):
  //     defs.setAttribute("a", "" + (Number(defs.getAttribute("a")) ?? 0) + 1);
  //   case dmn13ns.get(""):
  //     defs.setAttribute("a", "" + (Number(defs.getAttribute("a")) ?? 0) + 1);
  //   case dmn14ns.get(""):
  //     defs.setAttribute("a", "" + (Number(defs.getAttribute("a")) ?? 0) + 1);
  //   case dmn15ns.get(""):
  //     defs.setAttribute("a", "" + (Number(defs.getAttribute("a")) ?? 0) + 1);
  // }

  return { domdoc, instanceNs };
}
