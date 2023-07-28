import { DMNDI13__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Dispatch } from "../store/Store";

export function resizeNodes({
  changes,
  dispatch: { dmn },
}: {
  changes: { dmnDiagramElementIndex: number; dimension: { "@_width": number; "@_height": number } }[];
  dispatch: { dmn: Dispatch["dmn"] };
}) {
  dmn.set(({ definitions }) => {
    for (const change of changes) {
      const bounds = (
        definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]?.["dmndi:DMNDiagramElement"]?.[
          change.dmnDiagramElementIndex
        ] as DMNDI13__DMNShape
      )["dc:Bounds"];

      bounds!["@_width"] = change.dimension["@_width"];
      bounds!["@_height"] = change.dimension["@_height"];
    }
  });
}
