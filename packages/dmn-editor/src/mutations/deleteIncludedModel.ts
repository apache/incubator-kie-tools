import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getXmlNamespaceName } from "../xml/xmlNamespaceDeclarations";

export function deleteIncludedModel({ definitions, index }: { definitions: DMN15__tDefinitions; index: number }) {
  definitions.import ??= [];
  const [deleted] = definitions.import.splice(index, 1);

  const namespaceName = getXmlNamespaceName({
    model: definitions,
    namespace: deleted["@_namespace"],
  });
  if (namespaceName) {
    delete definitions[`@_xmlns:${namespaceName}`];
  }
}
