import { Meta, Parser, getInstanceNs, getParser } from "@kie-tools/xml-parser-ts";
import { meta as dmn12meta, root as dmn12root, ns as dmn12ns } from "./schemas/dmn-1_2/ts-gen/meta";
import { meta as dmn13meta, root as dmn13root, ns as dmn13ns } from "./schemas/dmn-1_3/ts-gen/meta";
import { meta as dmn14meta, root as dmn14root, ns as dmn14ns } from "./schemas/dmn-1_4/ts-gen/meta";
import { DMN14__tDefinitions } from "./schemas/dmn-1_4/ts-gen/types";
import { DMN13__tDefinitions } from "./schemas/dmn-1_3/ts-gen/types";
import { DMN12__tDefinitions } from "./schemas/dmn-1_2/ts-gen/types";
import "./extensions";

type DmnMarshaller = {
  parser: Parser<DmnDefinitions>;
  instanceNs: Map<string, string>;
  root: { element: string; type: string };
  meta: Meta;
  version: "1.0" | "1.1" | "1.2" | "1.3" | "1.4";
};

export type DmnDefinitions = DMN12__tDefinitions | DMN13__tDefinitions | DMN14__tDefinitions;

export function getMarshaller(xml: string): DmnMarshaller {
  const instanceNs = getInstanceNs(xml);

  // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  if (instanceNs.get(dmn12ns.get("")!) !== undefined) {
    console.log("Detected version is DMN 1.2");
    return {
      instanceNs,
      version: "1.2",
      root: dmn12root,
      meta: dmn12meta,
      parser: getParser<DmnDefinitions>({
        ns: dmn12ns,
        meta: dmn12meta,
        root: dmn12root,
      }),
    };
    // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  } else if (instanceNs.get(dmn13ns.get("")!) !== undefined) {
    console.log("Detected version is DMN 1.3");
    return {
      instanceNs,
      version: "1.3",
      root: dmn13root,
      meta: dmn13meta,
      parser: getParser<DmnDefinitions>({
        ns: dmn13ns,
        meta: dmn13meta,
        root: dmn13root,
      }),
    };
    // Do not remove this '!== undefined', as "" is a valid namespace on the instanceNs map, although it is a falsy value.
  } else if (instanceNs.get(dmn14ns.get("")!) !== undefined) {
    console.log("Detected version is DMN 1.4");
    return {
      instanceNs,
      version: "1.4",
      root: dmn14root,
      meta: dmn14meta,
      parser: getParser<DmnDefinitions>({
        ns: dmn14ns,
        meta: dmn14meta,
        root: dmn14root,
      }),
    };
  } else {
    throw new Error(
      `Unknown version declared for DMN. Instance NS --> '${JSON.stringify([...instanceNs.entries()])}'.`
    );
  }
}
