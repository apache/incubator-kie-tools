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

import {
  Meta,
  XmlParserTs,
  domParser,
  getInstanceNs,
  getInstanceNsFromJson,
  getParser,
} from "@kie-tools/xml-parser-ts";
import { getNsDeclarationPropName } from "@kie-tools/xml-parser-ts/dist/ns";
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
import { dmn3__tDefinitions as DMN10__tDefinitions } from "./schemas/dmn-1_0/ts-gen/types";
import { dmn__tDefinitions as DMN11__tDefinitions } from "./schemas/dmn-1_1/ts-gen/types";
import { DMN12__tDefinitions } from "./schemas/dmn-1_2/ts-gen/types";
import { DMN13__tDefinitions } from "./schemas/dmn-1_3/ts-gen/types";
import { DMN14__tDefinitions } from "./schemas/dmn-1_4/ts-gen/types";
import { DMN15__tDefinitions } from "./schemas/dmn-1_5/ts-gen/types";
import { ns as kie10ns } from "./schemas/kie-1_0/ts-gen/meta";
import { KIE_NS, LEGACY_KIE_NS__PRE_GWT_REMOVAL } from "./kie-extensions";

import "./kie-extensions"; // Necessary to include the type extensions and patch the ns maps. Do not remove.

export type DmnMarshaller<V extends DmnMarshallerVersions = "latest"> = InternalDmnMarshaller<V> & {
  originalVersion: DmnVersions;
  isLatest: boolean;
};

export type InternalDmnMarshaller<V extends DmnMarshallerVersions = "latest"> = V extends "1.0"
  ? DmnMarshaller10
  : V extends "1.1"
    ? DmnMarshaller11
    : V extends "1.2"
      ? DmnMarshaller12
      : V extends "1.3"
        ? DmnMarshaller13
        : V extends "1.4"
          ? DmnMarshaller14
          : V extends "1.5"
            ? DmnMarshaller15
            : V extends "latest"
              ? DmnLatestMarshaller
              : never;

export type DmnMarshallerBase = {
  instanceNs: Map<string, string>;
  root: { element: string; type: string };
  meta: Meta;
};

export type AllDmnMarshallers =
  | DmnMarshaller10
  | DmnMarshaller11
  | DmnMarshaller12
  | DmnMarshaller13
  | DmnMarshaller14
  | DmnMarshaller15;

export type KieExtensionVersions = "0.0" | "1.0";
export type DmnVersions = AllDmnMarshallers["version"];
export type DmnMarshallerVersions = AllDmnMarshallers["version"] | "latest";

export type DmnMarshaller10 = DmnMarshallerBase & {
  parser: { parse(): { Definitions: DMN10__tDefinitions } };
  builder: { build(json: { Definitions: DMN10__tDefinitions }): string };
  version: "1.0";
};
export type DmnMarshaller11 = DmnMarshallerBase & {
  parser: { parse(): { definitions: DMN11__tDefinitions } };
  builder: { build(json: { definitions: DMN11__tDefinitions }): string };
  version: "1.1";
};
export type DmnMarshaller12 = DmnMarshallerBase & {
  parser: { parse(): { definitions: DMN12__tDefinitions } };
  builder: { build(json: { definitions: DMN12__tDefinitions }): string };
  version: "1.2";
};
export type DmnMarshaller13 = DmnMarshallerBase & {
  parser: { parse(): { definitions: DMN13__tDefinitions } };
  builder: { build(json: { definitions: DMN13__tDefinitions }): string };
  version: "1.3";
};
export type DmnMarshaller14 = DmnMarshallerBase & {
  parser: { parse(): { definitions: DMN14__tDefinitions } };
  builder: { build(json: { definitions: DMN14__tDefinitions }): string };
  version: "1.4";
};
export type DmnMarshaller15 = DmnMarshallerBase & {
  parser: { parse(): { definitions: DMN15__tDefinitions } };
  builder: { build(json: { definitions: DMN15__tDefinitions }): string };
  version: "1.5";
};

export const DMN_PARSERS: Record<DmnVersions, XmlParserTs<any>> = {
  "1.0": getParser<{ [dmn10root.element]: DMN10__tDefinitions }>({
    ns: dmn10ns,
    meta: dmn10meta,
    subs: dmn10subs,
    elements: dmn10elements,
    root: dmn10root,
  }),
  "1.1": getParser<{ [dmn11root.element]: DMN11__tDefinitions }>({
    ns: dmn11ns,
    meta: dmn11meta,
    subs: dmn11subs,
    elements: dmn11elements,
    root: dmn11root,
  }),
  "1.2": getParser<{ [dmn12root.element]: DMN12__tDefinitions }>({
    ns: dmn12ns,
    meta: dmn12meta,
    subs: dmn12subs,
    elements: dmn12elements,
    root: dmn12root,
  }),
  "1.3": getParser<{ [dmn13root.element]: DMN13__tDefinitions }>({
    ns: dmn13ns,
    meta: dmn13meta,
    subs: dmn13subs,
    elements: dmn13elements,
    root: dmn13root,
  }),
  "1.4": getParser<{ [dmn14root.element]: DMN14__tDefinitions }>({
    ns: dmn14ns,
    meta: dmn14meta,
    subs: dmn14subs,
    elements: dmn14elements,
    root: dmn14root,
  }),
  "1.5": getParser<{ [dmn15root.element]: DMN15__tDefinitions }>({
    ns: dmn15ns,
    meta: dmn15meta,
    subs: dmn15subs,
    elements: dmn15elements,
    root: dmn15root,
  }),
};

const FEEL_NS = "feel:";

export const FEEL_NAMESPACES: Record<DmnVersions, string> = {
  "1.0": "http://www.omg.org/spec/FEEL/20140401",
  "1.1": "http://www.omg.org/spec/FEEL/20140401",
  "1.2": "http://www.omg.org/spec/DMN/20180521/FEEL/",
  "1.3": "https://www.omg.org/spec/DMN/20191111/FEEL/",
  "1.4": "https://www.omg.org/spec/DMN/20211108/FEEL/",
  "1.5": "https://www.omg.org/spec/DMN/20230324/FEEL/",
};

const feel12ns = new Map<string, string>([
  [FEEL_NS, FEEL_NAMESPACES["1.2"]],
  [FEEL_NAMESPACES["1.2"], FEEL_NS],
]);

const feel13ns = new Map<string, string>([
  [FEEL_NS, FEEL_NAMESPACES["1.3"]],
  [FEEL_NAMESPACES["1.3"], FEEL_NS],
]);

const feel14ns = new Map<string, string>([
  [FEEL_NS, FEEL_NAMESPACES["1.4"]],
  [FEEL_NAMESPACES["1.4"], FEEL_NS],
]);

const feel15ns = new Map<string, string>([
  [FEEL_NS, FEEL_NAMESPACES["1.5"]],
  [FEEL_NAMESPACES["1.5"], FEEL_NS],
]);

const kie10nsRelativeToDmns = new Map<string, string>([
  [kie10ns.get("")!, KIE_NS],
  [KIE_NS, kie10ns.get("")!],
]);

export const DMN_VERSIONS_TIMELINE: DmnVersions[] = ["1.0", "1.1", "1.2", "1.3", "1.4", "1.5"];
export const DMN_LATEST_VERSION = "1.5" as const;
export type DmnLatestMarshaller = DmnMarshaller15;
export type DmnLatestModel = { [dmn15root.element]: DMN15__tDefinitions };

export type DmnMarshallerOpts<V extends DmnMarshallerVersions> = { upgradeTo?: V };

export function getMarshaller<V extends DmnMarshallerVersions>(
  xml: string,
  opts?: DmnMarshallerOpts<V>
): DmnMarshaller<V> {
  const originalDomdoc = domParser.getDomDocument(xml);
  const originalInstanceNs = getInstanceNs(originalDomdoc);

  const originalMarshaller = getMarshallerForFixedVersion(originalDomdoc, originalInstanceNs);

  // `opts.upgradeTo` is optional. It defaults to not upgrading at all. "latest" is an alias to whatever the `DMN_LATEST_VERSION` constante declares.
  const targetVersion: DmnVersions =
    opts?.upgradeTo === "latest" ? DMN_LATEST_VERSION : opts?.upgradeTo ?? originalMarshaller.version;

  // If the XML is already on the latest version, we don't do anything else and just return the marshaller.
  if (originalMarshaller.version === targetVersion) {
    return {
      ...(originalMarshaller as InternalDmnMarshaller<V>),
      originalVersion: originalMarshaller.version,
      isLatest: true,
    };
  }

  // At this point, we know that the XML version and the target version are different.
  // We check if `targetVersion` is not prior to the actual version on the XML. If it is, it's an error. We don't downgrade DMN XMLs.
  if (DMN_VERSIONS_TIMELINE.indexOf(originalMarshaller.version) > DMN_VERSIONS_TIMELINE.indexOf(targetVersion)) {
    throw new Error(
      `DMN MARSHALLER: Cannot build DMN ${targetVersion} marshaller from a model that's already in version '${originalMarshaller.version}'. Downgrading DMN models is not possible.`
    );
  }

  // Ok, now we have a valid scenario where we need to do the upgrades.
  // We go version by version, cascading through this switch statement.
  let dmn10: { [dmn10root.element]: DMN10__tDefinitions } | undefined = undefined;
  let dmn11: { [dmn11root.element]: DMN11__tDefinitions } | undefined = undefined;
  let dmn12: { [dmn12root.element]: DMN12__tDefinitions } | undefined = undefined;
  let dmn13: { [dmn13root.element]: DMN13__tDefinitions } | undefined = undefined;
  let dmn14: { [dmn14root.element]: DMN14__tDefinitions } | undefined = undefined;
  let dmn15: { [dmn15root.element]: DMN15__tDefinitions } | undefined = undefined;
  switch (originalMarshaller.version) {
    case "1.0":
      dmn10 = dmn10 ?? originalMarshaller.parser.parse();
      dmn11 = upgrade10to11(dmn10);
      if (targetVersion === "1.1") break;
    case "1.1":
      dmn11 = dmn11 ?? (originalMarshaller as DmnMarshaller11).parser.parse();
      dmn12 = upgrade11to12(dmn11);
      if (targetVersion === "1.2") break;
    case "1.2":
      dmn12 = dmn12 ?? (originalMarshaller as DmnMarshaller12).parser.parse();
      dmn13 = upgrade12to13(dmn12);
      if (targetVersion === "1.3") break;
    case "1.3":
      dmn13 = dmn13 ?? (originalMarshaller as DmnMarshaller13).parser.parse();
      dmn14 = upgrade13to14(dmn13);
      if (targetVersion === "1.4") break;
    case "1.4":
      dmn14 = dmn14 ?? (originalMarshaller as DmnMarshaller14).parser.parse();
      dmn15 = upgrade14to15(dmn14);
      if (targetVersion === "1.5") break;
    case "1.5":
      throw new Error(
        "DMN MARSHALLER: Unexpected error. Shouldn't ever try to migrate a DMN 1.5, as DMN 1.5 is latest."
      );
  }

  // DMN 1.0 won't ever be here, because it is the first verison.
  const upgradedJson = dmn15 ?? dmn14 ?? dmn13 ?? dmn12 ?? dmn11;
  if (!upgradedJson) {
    throw new Error("DMN MARSHALLER: Unexpected error. At least one upgraded model should've been created.");
  }

  // Get the correct parser based on the new version.
  const parserForUpgradedJson = DMN_PARSERS[targetVersion];
  if (!parserForUpgradedJson) {
    throw new Error(`DMN MARSHALLER: Unexpected error. Couldn't find parser for version '${targetVersion}'.`);
  }

  // We need to gerenate the instanceNs because the migrations will have changed it.
  const upgradedInstanceNs = getInstanceNsFromJson(upgradedJson.definitions);

  // Generate the XML based on the upgraded json. Might be a little slower, but it better simulates a manual upgrade, reusing the same mechanisms.
  // This cast to `any` here is intentional, as we can't determine what's the model version in a way the TS compiler can understand, since upgrades are dynamic.
  const upgradedXml = parserForUpgradedJson.build({ json: upgradedJson as any, instanceNs: upgradedInstanceNs });

  // Generate the marshaller based on the upgraded XML, as if the caller had done it themself.
  const upgradedMarshaller = getMarshallerForFixedVersion(domParser.getDomDocument(upgradedXml), upgradedInstanceNs);

  return {
    ...(upgradedMarshaller as InternalDmnMarshaller<V>),
    originalVersion: originalMarshaller.version,
    isLatest: targetVersion === DMN_LATEST_VERSION,
  };
}

export function getKieExtensionVersion(instanceNs: Map<string, string>): KieExtensionVersions {
  if (instanceNs.get(LEGACY_KIE_NS__PRE_GWT_REMOVAL) !== undefined) {
    return "0.0";
  }
  // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  else if (instanceNs.get(kie10ns.get("")!) !== undefined) {
    return "1.0";
  } else {
    throw new Error(
      `DMN MARSHALLER: Unknown version declared for DMN. Instance NS --> '${JSON.stringify([
        ...instanceNs.entries(),
      ])}'.`
    );
  }
}

export function getDmnVersion(instanceNs: Map<string, string>): DmnVersions {
  // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  if (instanceNs.get(dmn10ns.get("")!) !== undefined) {
    return "1.0";
  }
  // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  else if (instanceNs.get(dmn11ns.get("")!) !== undefined) {
    return "1.1";
  }
  // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  else if (instanceNs.get(dmn12ns.get("")!) !== undefined) {
    return "1.2";
  }
  // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  else if (instanceNs.get(dmn13ns.get("")!) !== undefined) {
    return "1.3";
  }
  // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  else if (instanceNs.get(dmn14ns.get("")!) !== undefined) {
    return "1.4";
  }
  // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  else if (instanceNs.get(dmn15ns.get("")!) !== undefined) {
    return "1.5";
  }
  // None.. throw error
  else {
    throw new Error(
      `DMN MARSHALLER: Unknown version declared for DMN. Instance NS --> '${JSON.stringify([
        ...instanceNs.entries(),
      ])}'.`
    );
  }
}

export function getMarshallerForFixedVersion(domdoc: Document, instanceNs: Map<string, string>): AllDmnMarshallers {
  const version = getDmnVersion(instanceNs);

  switch (version) {
    case "1.0":
      return {
        instanceNs,
        version: "1.0",
        root: dmn10root,
        meta: dmn10meta,
        parser: { parse: () => DMN_PARSERS["1.0"].parse({ type: "domdoc", domdoc, instanceNs }).json },
        builder: { build: (json) => DMN_PARSERS["1.0"].build({ json, instanceNs }) },
      };
    case "1.1":
      return {
        instanceNs,
        version: "1.1",
        root: dmn11root,
        meta: dmn11meta,
        parser: { parse: () => DMN_PARSERS["1.1"].parse({ type: "domdoc", domdoc, instanceNs }).json },
        builder: { build: (json) => DMN_PARSERS["1.1"].build({ json, instanceNs }) },
      };
    case "1.2":
      return {
        instanceNs,
        version: "1.2",
        root: dmn12root,
        meta: dmn12meta,
        parser: { parse: () => DMN_PARSERS["1.2"].parse({ type: "domdoc", domdoc, instanceNs }).json },
        builder: { build: (json) => DMN_PARSERS["1.2"].build({ json, instanceNs }) },
      };
    case "1.3":
      return {
        instanceNs,
        version: "1.3",
        root: dmn13root,
        meta: dmn13meta,
        parser: { parse: () => DMN_PARSERS["1.3"].parse({ type: "domdoc", domdoc, instanceNs }).json },
        builder: { build: (json) => DMN_PARSERS["1.3"].build({ json, instanceNs }) },
      };
    case "1.4":
      return {
        instanceNs,
        version: "1.4",
        root: dmn14root,
        meta: dmn14meta,
        parser: { parse: () => DMN_PARSERS["1.4"].parse({ type: "domdoc", domdoc, instanceNs }).json },
        builder: { build: (json) => DMN_PARSERS["1.4"].build({ json, instanceNs }) },
      };
    case "1.5":
      return {
        instanceNs,
        version: "1.5",
        root: dmn15root,
        meta: dmn15meta,
        parser: { parse: () => DMN_PARSERS["1.5"].parse({ type: "domdoc", domdoc, instanceNs }).json },
        builder: { build: (json) => DMN_PARSERS["1.5"].build({ json, instanceNs }) },
      };
    default:
      throw new Error(
        `DMN MARSHALLER: Unknown version declared for DMN. Instance NS --> '${JSON.stringify([
          ...instanceNs.entries(),
        ])}'.`
      );
  }
}

// UPGRADES

const kieLegacyNs = new Map([
  ["kie:", LEGACY_KIE_NS__PRE_GWT_REMOVAL],
  [LEGACY_KIE_NS__PRE_GWT_REMOVAL, "kie:"],
]);

////////////////////////
// DMN 1.0 TO DMN 1.1 //
////////////////////////

export function upgrade10to11(dmn10: { Definitions: DMN10__tDefinitions }): { definitions: DMN11__tDefinitions } {
  throw new Error("DMN MARSHALLER: Upgrading from DMN 1.0 is not supported. Minimum version is 1.2.");
}

////////////////////////
// DMN 1.1 TO DMN 1.2 //
////////////////////////

export function upgrade11to12(dmn11: { definitions: DMN11__tDefinitions }): { definitions: DMN12__tDefinitions } {
  throw new Error("DMN MARSHALLER: Upgrading from DMN 1.1 is not supported. Minimum version is 1.2.");
}

////////////////////////
// DMN 1.2 TO DMN 1.5 //
////////////////////////

export function upgrade12to13(dmn12: { definitions: DMN12__tDefinitions }): { definitions: DMN13__tDefinitions } {
  const instanceNs = getInstanceNsFromJson(dmn12.definitions);

  // Upgrade DMN namespace
  dmn12.definitions[
    getNsDeclarationPropName({
      namespace: dmn12ns.get("")!,
      atInstanceNs: instanceNs,
      fallingBackToNs: dmn12ns,
    })
  ] = dmn13ns.get("")!;

  // Upgrade DMNDI namespace
  dmn12.definitions[
    getNsDeclarationPropName({
      namespace: dmn12ns.get("dmndi:")!,
      atInstanceNs: instanceNs,
      fallingBackToNs: dmn12ns,
    })
  ] = dmn13ns.get("dmndi:")!;

  // Upgrade or add KIE namespace if not there yet
  dmn12.definitions[
    getNsDeclarationPropName({
      namespace: LEGACY_KIE_NS__PRE_GWT_REMOVAL,
      atInstanceNs: instanceNs,
      fallingBackToNs: kieLegacyNs,
    })
  ] = kie10ns.get("")!;

  // Upgrade FEEL namespace
  dmn12.definitions[
    getNsDeclarationPropName({
      namespace: FEEL_NAMESPACES["1.2"],
      atInstanceNs: instanceNs,
      fallingBackToNs: feel12ns,
    })
  ] = FEEL_NAMESPACES["1.3"];

  if (dmn12.definitions["@_typeLanguage"] === FEEL_NAMESPACES["1.2"]) {
    dmn12.definitions["@_typeLanguage"] = FEEL_NAMESPACES["1.3"];
  }

  if (dmn12.definitions["@_expressionLanguage"] === FEEL_NAMESPACES["1.2"]) {
    dmn12.definitions["@_expressionLanguage"] = FEEL_NAMESPACES["1.3"];
  }

  return dmn12;
}

////////////////////////
// DMN 1.3 TO DMN 1.4 //
////////////////////////

export function upgrade13to14(dmn13: { definitions: DMN13__tDefinitions }): { definitions: DMN14__tDefinitions } {
  const instanceNs = getInstanceNsFromJson(dmn13.definitions);

  // Upgrade DMN namespace
  dmn13.definitions[
    getNsDeclarationPropName({
      namespace: dmn13ns.get("")!,
      atInstanceNs: instanceNs,
      fallingBackToNs: dmn13ns,
    })
  ] = dmn14ns.get("")!;

  // Upgrade DMNDI namespace
  dmn13.definitions[
    getNsDeclarationPropName({
      namespace: dmn13ns.get("dmndi:")!,
      atInstanceNs: instanceNs,
      fallingBackToNs: dmn13ns,
    })
  ] = dmn14ns.get("dmndi:")!;

  // Upgrade FEEL namespace
  dmn13.definitions[
    getNsDeclarationPropName({
      namespace: FEEL_NAMESPACES["1.3"],
      atInstanceNs: instanceNs,
      fallingBackToNs: feel13ns,
    })
  ] = FEEL_NAMESPACES["1.4"];

  // Add KIE namespace if not there yet.
  dmn13.definitions[
    getNsDeclarationPropName({
      namespace: kie10ns.get("")!,
      atInstanceNs: instanceNs,
      fallingBackToNs: kie10nsRelativeToDmns,
    })
  ] = kie10ns.get("")!;

  if (dmn13.definitions["@_typeLanguage"] === FEEL_NAMESPACES["1.3"]) {
    dmn13.definitions["@_typeLanguage"] = FEEL_NAMESPACES["1.4"];
  }

  if (dmn13.definitions["@_expressionLanguage"] === FEEL_NAMESPACES["1.3"]) {
    dmn13.definitions["@_expressionLanguage"] = FEEL_NAMESPACES["1.4"];
  }

  return dmn13;
}

////////////////////////
// DMN 1.4 TO DMN 1.5 //
////////////////////////

export function upgrade14to15(dmn14: { definitions: DMN14__tDefinitions }): { definitions: DMN15__tDefinitions } {
  const instanceNs = getInstanceNsFromJson(dmn14.definitions);

  // Upgrade DMN namespace
  dmn14.definitions[
    getNsDeclarationPropName({
      namespace: dmn14ns.get("")!,
      atInstanceNs: instanceNs,
      fallingBackToNs: dmn14ns,
    })
  ] = dmn15ns.get("")!;

  // Upgrade DMNDI namespace
  dmn14.definitions[
    getNsDeclarationPropName({
      namespace: dmn14ns.get("dmndi:")!,
      atInstanceNs: instanceNs,
      fallingBackToNs: dmn14ns,
    })
  ] = dmn15ns.get("dmndi:")!;

  // Upgrade FEEL namespace
  dmn14.definitions[
    getNsDeclarationPropName({
      namespace: FEEL_NAMESPACES["1.4"],
      atInstanceNs: instanceNs,
      fallingBackToNs: feel14ns,
    })
  ] = FEEL_NAMESPACES["1.5"];

  // Add KIE namespace if not there yet.
  dmn14.definitions[
    getNsDeclarationPropName({
      namespace: kie10ns.get("")!,
      atInstanceNs: instanceNs,
      fallingBackToNs: kie10nsRelativeToDmns,
    })
  ] = kie10ns.get("")!;

  if (dmn14.definitions["@_typeLanguage"] === FEEL_NAMESPACES["1.4"]) {
    dmn14.definitions["@_typeLanguage"] = FEEL_NAMESPACES["1.5"];
  }

  if (dmn14.definitions["@_expressionLanguage"] === FEEL_NAMESPACES["1.4"]) {
    dmn14.definitions["@_expressionLanguage"] = FEEL_NAMESPACES["1.5"];
  }

  // FIXME: Tiago --> Convert deprecated `allowedValues` to `typeConstraint` on ItemDefinitions
  return dmn14;
}
