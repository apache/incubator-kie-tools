import { Meta, XmlParserTs, getInstanceNs, getParser } from "@kie-tools/xml-parser-ts";
import { meta as bpmn20meta, root as bpmn20root, ns as bpmn20ns } from "./schemas/bpmn-2_0/ts-gen/meta";
import { BPMN20__tDefinitions } from "./schemas/bpmn-2_0/ts-gen/types";

type BpmnDefinitions = { definitions: BPMN20__tDefinitions };
type BpmnMarshaller = {
  parser: XmlParserTs<BpmnDefinitions>;
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
    parser: getParser<BpmnDefinitions>({
      ns: bpmn20ns,
      meta: bpmn20meta,
      root: bpmn20root,
    }),
  };
}
