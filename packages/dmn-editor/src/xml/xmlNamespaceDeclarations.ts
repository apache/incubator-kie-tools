import { XmlParserTsRootElementBaseType } from "@kie-tools/xml-parser-ts";
import { parseXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";

export function getXmlNamespaceDeclarationName({
  model,
  namespace,
}: {
  model: XmlParserTsRootElementBaseType | undefined;
  namespace: string;
}) {
  const xmlnsEntry = Object.entries(model ?? {}).find(
    ([k, v]) => v === namespace && (k === "@_xmlns" || k.startsWith("@_xmlns:"))
  );
  if (!xmlnsEntry) {
    return undefined;
  }

  if (xmlnsEntry[0] === "@_xmlns") {
    return undefined;
  }

  return parseXmlQName(xmlnsEntry[0]).localPart;
}