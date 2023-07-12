import { TextEncoder, TextDecoder } from "util";
(global as any).TextEncoder = TextEncoder;
(global as any).TextDecoder = TextDecoder;

import * as jsdom from "jsdom";
import * as index from "..";

index.domParser.getDomDocument = (xml: string | Buffer) => {
  console.time("parsing dom took (jsdom)");
  const domdoc = new jsdom.JSDOM(xml, { contentType: "application/xml" }).window.document;
  console.timeEnd("parsing dom took (jsdom)");
  return domdoc;
};

export * from "..";
