import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function deleteIncludedModel(args: { definitions: DMN15__tDefinitions; index: number }) {
  args.definitions.import ??= [];
  args.definitions.import.splice(args.index, 1);
}
