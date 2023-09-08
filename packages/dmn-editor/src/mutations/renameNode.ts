import {
  DMN15__tDefinitions,
  DMN15__tGroup,
  DMN15__tTextAnnotation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function renameDrgElement({
  definitions,
  newName,
  index,
}: {
  definitions: DMN15__tDefinitions;
  newName: string;
  index: number;
}) {
  definitions.drgElement![index]["@_name"] = newName;
}

export function renameGroupNode({
  definitions,
  newName,
  index,
}: {
  definitions: DMN15__tDefinitions;
  newName: string;
  index: number;
}) {
  (definitions.artifact![index] as DMN15__tGroup)["@_name"] = newName;
}

export function updateTextAnnotation({
  definitions,
  newText,
  index,
}: {
  definitions: DMN15__tDefinitions;
  newText: string;
  index: number;
}) {
  (definitions.artifact![index] as DMN15__tTextAnnotation).text = newText;
}
