import { DMNDI13__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Dispatch } from "../store/Store";

export function repositionNodes({
  changes,
  dispatch: { dmn },
}: {
  changes: { dmnDiagramElementIndex: number; position: { "@_x": number; "@_y": number } }[];
  dispatch: { dmn: Dispatch["dmn"] };
}) {
  dmn.set(({ definitions }) => {
    for (const change of changes) {
      const bounds = (
        definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]?.["dmndi:DMNDiagramElement"]?.[
          change.dmnDiagramElementIndex
        ] as DMNDI13__DMNShape
      )["dc:Bounds"];

      bounds!["@_x"] = change.position["@_x"];
      bounds!["@_y"] = change.position["@_y"];
    }
  });
}
