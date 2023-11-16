import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15__tDefinitions, DMN15__tImport } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getXmlNamespaceDeclarationName } from "../xml/xmlNamespaceDeclarations";

export function addImport({
  definitions,
  includedModel,
}: {
  definitions: DMN15__tDefinitions;
  includedModel: {
    name: string;
    namespace: string;
    xmlns: string;
    locationURI: string;
  };
}) {
  const newImport: DMN15__tImport = {
    "@_id": generateUuid(),
    "@_name": includedModel.name.trim(),
    "@_importType": includedModel.xmlns,
    "@_namespace": includedModel.namespace,
    "@_locationURI": includedModel.locationURI,
  };

  definitions.import ??= [];
  definitions.import.push(newImport);

  // Find the first unused index. This will prevent declaring two namespaces with the same name.
  let index = 0;
  while (definitions[`@_xmlns:included${index}`]) {
    index++;
  }

  definitions[`@_xmlns:included${index}`] = includedModel.namespace;

  return newImport;
}
