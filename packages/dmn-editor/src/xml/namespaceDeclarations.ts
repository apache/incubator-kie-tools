import { XmlParserTsRootElementBaseType } from "@kie-tools/xml-parser-ts";
import { parseXmlQName } from "./qNames";

export function getXmlNamespaceName({
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

  return parseXmlQName(xmlnsEntry[0]).localPart;
}
