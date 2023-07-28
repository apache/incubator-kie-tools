import { DMNDI13__DMNEdge, DMNDI13__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Dispatch } from "../store/Store";
import { switchExpression } from "../switchExpression/switchExpression";

export function repositionNodes({
  dispatch: { dmn },
  changes,
}: {
  dispatch: { dmn: Dispatch["dmn"] };
  changes: {
    dmnDiagramElementIndex: number;
    position: { "@_x": number; "@_y": number };
    sourceEdgeIndexes: number[];
    targetEdgeIndexes: number[];
  }[];
}) {
  dmn.set(({ definitions }) => {
    const edgeIdsAlreadyUpdated = new Set<number>();

    const diagramElements = definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]?.["dmndi:DMNDiagramElement"] ?? [];

    for (const change of changes) {
      const bounds = (diagramElements?.[change.dmnDiagramElementIndex] as DMNDI13__DMNShape | undefined)?.["dc:Bounds"];
      if (!bounds) {
        throw new Error("Cannot reposition non-existent shape bounds");
      }

      const deltaX = change.position["@_x"] - (bounds?.["@_x"] ?? 0);
      const deltaY = change.position["@_y"] - (bounds?.["@_y"] ?? 0);

      bounds["@_x"] = change.position["@_x"];
      bounds["@_y"] = change.position["@_y"];

      const offsetEdges = (args: { edgeIndexes: number[]; waypoint: "last" | "first" }) => {
        for (const edgeIndex of args.edgeIndexes) {
          if (edgeIdsAlreadyUpdated.has(edgeIndex)) {
            continue;
          }

          edgeIdsAlreadyUpdated.add(edgeIndex);

          const edge = diagramElements[edgeIndex] as DMNDI13__DMNEdge | undefined;
          if (!edge || !edge["di:waypoint"]) {
            throw new Error("Cannot reposition non-existent edge");
          }

          const wapoint = switchExpression(args.waypoint, {
            first: edge["di:waypoint"][0],
            last: edge["di:waypoint"][edge["di:waypoint"].length - 1],
          });

          wapoint["@_x"] += deltaX;
          wapoint["@_y"] += deltaY;
        }
      };

      offsetEdges({ edgeIndexes: change.sourceEdgeIndexes, waypoint: "first" });
      offsetEdges({ edgeIndexes: change.targetEdgeIndexes, waypoint: "last" });
    }
  });
}
