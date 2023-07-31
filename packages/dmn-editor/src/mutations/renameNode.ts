import {
  DMN14__tDefinitions,
  DMN14__tTextAnnotation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

export function renameDrgElement({
  definitions,
  newName,
  index,
}: {
  definitions: DMN14__tDefinitions;
  newName: string;
  index: number;
}) {
  definitions.drgElement![index]["@_name"] = newName;
}

export function updateTextAnnotation({
  definitions,
  newText,
  index,
}: {
  definitions: DMN14__tDefinitions;
  newText: string;
  index: number;
}) {
  (definitions.artifact![index] as DMN14__tTextAnnotation).text = newText;
}
