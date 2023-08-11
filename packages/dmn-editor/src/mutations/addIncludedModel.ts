import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

export function addIncludedModel(args: {
  definitions: DMN14__tDefinitions;
  includedModel: {
    alias: string;
    namespace: string;
    xmlns: string;
  };
}) {
  const newImport = {
    "@_id": generateUuid(),
    "@_name": args.includedModel.alias,
    "@_importType": args.includedModel.xmlns,
    "@_namespace": args.includedModel.namespace,
  };

  args.definitions.import ??= [];
  args.definitions.import.push(newImport);

  return newImport;
}
