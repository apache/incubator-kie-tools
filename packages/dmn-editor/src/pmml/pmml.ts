import { DMN15__tImport } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { buildXmlHref } from "../xml/xmlHrefs";
import * as __path from "path";

export const KIE_PMML_NAMESPACE = "https://kie.org/pmml";

export const allPmmlImportNamespaces = new Set([
  "https://www.dmg.org/PMML-4_4",
  "http://www.dmg.org/PMML-4_4", // http is not official, but there might be files using it.
  "https://www.dmg.org/PMML-4_3",
  "http://www.dmg.org/PMML-4_3",
  "https://www.dmg.org/PMML-4_2",
  "http://www.dmg.org/PMML-4_2",
  "https://www.dmg.org/PMML-4_1",
  "http://www.dmg.org/PMML-4_1",
  "https://www.dmg.org/PMML-4_0",
  "http://www.dmg.org/PMML-4_0",
  "https://www.dmg.org/PMML-3_2",
  "http://www.dmg.org/PMML-3_2",
  "https://www.dmg.org/PMML-3_1",
  "http://www.dmg.org/PMML-3_1",
  "https://www.dmg.org/PMML-3_0",
  "http://www.dmg.org/PMML-3_0",
  "https://www.dmg.org/PMML-2_1",
  "http://www.dmg.org/PMML-2_1",
  "https://www.dmg.org/PMML-2_0",
  "http://www.dmg.org/PMML-2_0",
  "https://www.dmg.org/PMML-1_1",
  "http://www.dmg.org/PMML-1_1",
]);

export function getPmmlNamespace({ fileRelativePath }: { fileRelativePath: string }) {
  return buildXmlHref({ namespace: KIE_PMML_NAMESPACE, id: fileRelativePath });
}

export function getPmmlNamespaceFromDmnImport({ dmnImport }: { dmnImport: DMN15__tImport }) {
  return dmnImport["@_locationURI"]
    ? getPmmlNamespace({ fileRelativePath: dmnImport["@_locationURI"] })
    : dmnImport["@_namespace"];
}
