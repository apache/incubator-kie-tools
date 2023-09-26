import { XmlParserTsRootElementBaseType } from "@kie-tools/xml-parser-ts";
import { parseXmlQName } from "./xmlQNames";

export function getXmlNamespaceDeclarationName({
  model,
  namespace,
}: {
  model: XmlParserTsRootElementBaseType | undefined;
  namespace: string;
}) {
  const xmlnsEntry = Object.entries(model ?? {}).find(([k, v]) => v === namespace);
  if (!xmlnsEntry) {
    return undefined;
  }

  if (xmlnsEntry[0] === "@_xmlns") {
    return undefined;
  }

  return parseXmlQName(xmlnsEntry[0]).localPart;
}
