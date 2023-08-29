import {
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";

export function repositionNode({
  definitions,
  edgeIndexesAlreadyUpdated,
  change,
}: {
  definitions: DMN15__tDefinitions;
  edgeIndexesAlreadyUpdated: Set<number>;
  change: {
    shapeIndex: number;
    position: { x: number; y: number };
    sourceEdgeIndexes: number[];
    targetEdgeIndexes: number[];
    selectedEdges: string[];
  };
}) {
  const { diagramElements } = addOrGetDefaultDiagram({ definitions });

  const bounds = (diagramElements?.[change.shapeIndex] as DMNDI15__DMNShape | undefined)?.["dc:Bounds"];
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

      const edge = diagramElements[edgeIndex] as DMNDI15__DMNEdge | undefined;
      if (!edge || !edge["di:waypoint"]) {
        throw new Error("Cannot reposition non-existent edge");
      }

      const isEdgeSelected = change.selectedEdges.indexOf(edge["@_dmnElementRef"]!) >= 0;

      const waypoints = switchExpression(args.waypoint, {
        first: isEdgeSelected
          ? [...edge["di:waypoint"]].slice(0, -1) // All except last element
          : [edge["di:waypoint"][0]],
        last: isEdgeSelected
          ? [...edge["di:waypoint"]].slice(1, edge["di:waypoint"].length) // All except first element
          : [edge["di:waypoint"][edge["di:waypoint"].length - 1]],
      });

      for (const w of waypoints) {
        w["@_x"] += deltaX;
        w["@_y"] += deltaY;
      }
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
