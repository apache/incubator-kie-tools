import { Meta, Parser, getInstanceNs, getParser } from "@kie-tools/xml-parser-ts";
import { meta as bpmn20meta, root as bpmn20root, ns as bpmn20ns } from "./schemas/bpmn-2_0_2/ts-gen/meta";

type BpmnMarshaller = {
  parser: Parser<any>;
  instanceNs: Map<string, string>;
  root: { element: string; type: string };
  meta: Meta;
  version: "2.0";
};

export function getMarshaller(xml: string): BpmnMarshaller {
  const instanceNs = getInstanceNs(xml);

  return {
    instanceNs,
    version: "2.0",
    root: bpmn20root,
    meta: bpmn20meta,
    parser: getParser({
      ns: bpmn20ns,
      meta: bpmn20meta,
      root: bpmn20root,
    }),
  };
}
