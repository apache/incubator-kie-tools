import { DMN15__tImport } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { allDmnImportNamespaces } from "../Dmn15Spec";
import { allPmmlImportNamespaces, getPmmlNamespaceFromDmnImport } from "../pmml/pmml";

export function getNamespaceOfDmnImport({ dmnImport }: { dmnImport: DMN15__tImport }) {
  if (allDmnImportNamespaces.has(dmnImport["@_importType"])) {
    return dmnImport["@_namespace"];
  } else if (allPmmlImportNamespaces.has(dmnImport["@_importType"])) {
    return getPmmlNamespaceFromDmnImport({ dmnImport });
  } else {
    return dmnImport["@_namespace"];
  }
}
