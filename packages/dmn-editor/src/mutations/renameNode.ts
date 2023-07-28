import { DMN14__tTextAnnotation } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Dispatch } from "../store/Store";

export function renameDrgElement({
  newName,
  index,
  dispatch: { dmn },
}: {
  newName: string;
  index: number;
  dispatch: { dmn: Dispatch["dmn"] };
}) {
  dmn.set((model) => (model.definitions.drgElement![index]["@_name"] = newName));
}

export function updateTextAnnotation({
  newText,
  index,
  dispatch: { dmn },
}: {
  newText: string;
  index: number;
  dispatch: { dmn: Dispatch["dmn"] };
}) {
  dmn.set((model) => ((model.definitions.artifact![index] as DMN14__tTextAnnotation).text = newText));
}
