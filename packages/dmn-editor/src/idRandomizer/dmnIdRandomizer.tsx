import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  elements as dmn15elements,
  meta as dmn15meta,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { XmlParserTsIdRandomizer, XmlParserTsIdRandomizerMatcher } from "@kie-tools/xml-parser-ts/dist/idRandomizer";
import { buildXmlHref, parseXmlHref } from "../xml/xmlHrefs";

export function getNewDmnIdRandomizer() {
  return new XmlParserTsIdRandomizer({
    meta: dmn15meta,
    elements: dmn15elements,
    newIdGenerator: generateUuid,
    matchers: [tDmnElementReferenceIdRandomizerMatcher],
  });
}

export const tDmnElementReferenceIdRandomizerMatcher: XmlParserTsIdRandomizerMatcher<typeof dmn15meta> = ({
  parentJson,
  metaTypeName,
  attr,
}) => {
  if (metaTypeName === "DMN15__tDMNElementReference" && attr === "@_href") {
    const href = parseXmlHref(parentJson[attr]);
    return [
      href.id,
      ({ newId }) => {
        console.debug(
          `ID RANDOMIZER: [anyURI] Updating id from ${href.id} to ${newId} @ (${String(metaTypeName)}.${String(
            attr
          )}: ${parentJson[attr]})`
        );
        parentJson[attr] = buildXmlHref({ ...href, id: newId });
      },
    ];
  }
};
