import { Meta, XmlParserTs, getInstanceNs, getParser } from "@kie-tools/xml-parser-ts";
import { meta as scesim18meta, root as scesim18root, ns as scesim18ns } from "./schemas/scesim-1_8/ts-gen/meta";
import { SceSim__ScenarioSimulationModelType } from "./schemas/scesim-1_8/ts-gen/types";

export type SceSimModel = { ScenarioSimulationModel: SceSim__ScenarioSimulationModelType };

export type SceSimMarshaller = {
  parser: XmlParserTs<SceSimModel>;
  instanceNs: Map<string, string>;
  root: { element: string; type: string };
  meta: Meta;
  version: "1.8";
};

export function getMarshaller(xml: string): SceSimMarshaller {
  const instanceNs = getInstanceNs(xml);

  return {
    instanceNs,
    version: "1.8",
    root: scesim18root,
    meta: scesim18meta,
    parser: getParser<SceSimModel>({
      ns: scesim18ns,
      meta: scesim18meta,
      root: scesim18root,
    }),
  };
}
