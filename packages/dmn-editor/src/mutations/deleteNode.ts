import { DMN14__tTextAnnotation } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Dispatch } from "../store/Store";
import { NodeType } from "../diagram/connections/graphStructure";

export function deleteNode({
  dispatch: { dmn },
  node,
}: {
  dispatch: { dmn: Dispatch["dmn"] };
  node: { id: string; index: number };
}) {
  dmn.set((model) => {
    // TODO: Implement
  });
}
