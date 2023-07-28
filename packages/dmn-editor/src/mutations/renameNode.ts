import { DMN14__tTextAnnotation } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Dispatch } from "../store/Store";

export function renameDrgElement({
  dispatch: { dmn },
  newName,
  index,
}: {
  dispatch: { dmn: Dispatch["dmn"] };
  newName: string;
  index: number;
}) {
  dmn.set((model) => (model.definitions.drgElement![index]["@_name"] = newName));
}

export function updateTextAnnotation({
  dispatch: { dmn },
  newText,
  index,
}: {
  dispatch: { dmn: Dispatch["dmn"] };
  newText: string;
  index: number;
}) {
  dmn.set((model) => ((model.definitions.artifact![index] as DMN14__tTextAnnotation).text = newText));
}
