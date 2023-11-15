import {
  DMN15__tDefinitions,
  DMN15__tItemDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getNewItemDefinition } from "../dataTypes/DataTypeSpec";

export function addTopLevelItemDefinition({
  definitions,
  partial,
}: {
  definitions: DMN15__tDefinitions;
  partial?: Partial<DMN15__tItemDefinition>;
}) {
  const newItemDefinition = getNewItemDefinition(partial);
  definitions.itemDefinition ??= [];
  definitions.itemDefinition.unshift(newItemDefinition);
  return newItemDefinition;
}
