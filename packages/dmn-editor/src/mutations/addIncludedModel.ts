import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function addIncludedModel({
  definitions,
  includedModel,
}: {
  definitions: DMN15__tDefinitions;
  includedModel: {
    alias: string;
    namespace: string;
    xmlns: string;
  };
}) {
  const newImport = {
    "@_id": generateUuid(),
    "@_name": includedModel.alias.trim(),
    "@_importType": includedModel.xmlns,
    "@_namespace": includedModel.namespace,
  };

  definitions.import ??= [];
  definitions.import.push(newImport);

  // FIXME: Tiago --> Maybe using a random name would be better? We need to check for collisions! Can't override one existing.
  definitions[`@_xmlns:included${definitions.import.length - 1}`] = includedModel.namespace;

  return newImport;
}
