import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

export function deleteIncludedModel(args: { definitions: DMN14__tDefinitions; index: number }) {
  args.definitions.import ??= [];
  args.definitions.import.splice(args.index, 1);
}
