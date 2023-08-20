import {
  DMN14__tDefinitions,
  DMNDI13__DMNEdge,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";

export function repositionNode({
  definitions,
  change,
}: {
  definitions: DMN14__tDefinitions;
  change: {
    shapeIndex: number;
    position: { x: number; y: number };
    sourceEdgeIndexes: number[];
    targetEdgeIndexes: number[];
  };
}) {
  const edgeIndexesAlreadyUpdated = new Set<number>();

  const { diagramElements } = addOrGetDefaultDiagram({ definitions });

  const bounds = (diagramElements?.[change.shapeIndex] as DMNDI13__DMNShape | undefined)?.["dc:Bounds"];
  if (!bounds) {
    throw new Error("Cannot reposition non-existent shape bounds");
  }

  const deltaX = change.position.x - (bounds?.["@_x"] ?? 0);
  const deltaY = change.position.y - (bounds?.["@_y"] ?? 0);

  bounds["@_x"] = change.position.x;
  bounds["@_y"] = change.position.y;

  const offsetEdges = (args: { edgeIndexes: number[]; waypoint: "last" | "first" }) => {
    for (const edgeIndex of args.edgeIndexes) {
      if (edgeIndexesAlreadyUpdated.has(edgeIndex)) {
        continue;
      }

      edgeIndexesAlreadyUpdated.add(edgeIndex);

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

  return {
    delta: {
      x: deltaX,
      y: deltaY,
    },
  };
}
