import { Meta, XmlParserTs, getInstanceNs, domParser, getParser } from "@kie-tools/xml-parser-ts";
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
    parser: { parse: () => p.parse({ xml, domdoc, instanceNs }).json },
    builder: { build: (json: BpmnDefinitions) => p.build({ json, instanceNs }) },
  };
}

export const foo = "bar";
console.log("BPMN Marshaller is alive.");
