import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getXmlNamespaceDeclarationName } from "../xml/xmlNamespaceDeclarations";

export function deleteImport({ definitions, index }: { definitions: DMN15__tDefinitions; index: number }) {
  definitions.import ??= [];
  const [deleted] = definitions.import.splice(index, 1);

  const namespaceName = getXmlNamespaceDeclarationName({
    model: definitions,
    namespace: deleted["@_namespace"],
  });
  if (namespaceName) {
    delete definitions[`@_xmlns:${namespaceName}`];
  }
}
