import { Meta, domParser, getInstanceNs, getParser } from "@kie-tools/xml-parser-ts";
import {
  meta as scesim18meta,
  root as scesim18root,
  subs as scesim18subs,
  elements as scesim18elements,
  ns as scesim18ns,
} from "./schemas/scesim-1_8/ts-gen/meta";
import { SceSim__ScenarioSimulationModelType } from "./schemas/scesim-1_8/ts-gen/types";

type SceSimMarshaller = {
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
    parser: { parse: () => p.parse({ xml, instanceNs }).json },
    builder: { build: (json: SceSimModel) => p.build({ json, instanceNs }) },
  };
}
