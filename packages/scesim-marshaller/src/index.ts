import { Meta, Parser, getInstanceNs, getParser } from "@kie-tools/xml-parser-ts";
import { meta as scesim100meta, root as scesim100root, ns as scesim100ns } from "./schemas/scesim-1_8_0/ts-gen/meta";

type SceSimMarshaller = {
  parser: Parser<any>;
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
    root: scesim100root,
    meta: scesim100meta,
    parser: getParser({
      ns: scesim100ns,
      meta: scesim100meta,
      root: scesim100root,
    }),
  };
}
