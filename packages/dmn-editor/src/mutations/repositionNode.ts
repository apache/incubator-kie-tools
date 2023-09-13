import {
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import { NodeType } from "../diagram/connections/graphStructure";
import { getCentralizedDecisionServiceDividerLine } from "./updateDecisionServiceDividerLine";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";

export function repositionNode({
  definitions,
  edgeIndexesAlreadyUpdated,
  change,
}: {
  definitions: DMN15__tDefinitions;
  edgeIndexesAlreadyUpdated: Set<number>;
  change: {
    nodeType: NodeType;
    shapeIndex: number;
    sourceEdgeIndexes: number[];
    targetEdgeIndexes: number[];
    selectedEdges: string[];
  } & (
    | {
        type: "absolute";
        position: { x: number; y: number };
      }
    | {
        type: "offset";
        offset: { deltaX: number; deltaY: number };
      }
  );
}) {
  const { diagramElements } = addOrGetDefaultDiagram({ definitions });

  const shape = diagramElements?.[change.shapeIndex] as DMNDI15__DMNShape | undefined;
  const shapeBounds = shape?.["dc:Bounds"];
  if (!shapeBounds) {
    throw new Error("Cannot reposition non-existent shape bounds");
  }

  let deltaX: number;
  let deltaY: number;
  if (change.type === "absolute") {
    deltaX = change.position.x - (shapeBounds?.["@_x"] ?? 0);
    deltaY = change.position.y - (shapeBounds?.["@_y"] ?? 0);
    shapeBounds["@_x"] = change.position.x;
    shapeBounds["@_y"] = change.position.y;
  } else if (change.type === "offset") {
    deltaX = change.offset.deltaX;
    deltaY = change.offset.deltaY;
    shapeBounds["@_x"] += change.offset.deltaX;
    shapeBounds["@_y"] += change.offset.deltaY;
  } else {
    throw new Error(`Unknown type of node position change '${(change as any).type}'.`);
  }

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

  if (change.nodeType === NODE_TYPES.decisionService) {
    shape["dmndi:DMNDecisionServiceDividerLine"] ??= getCentralizedDecisionServiceDividerLine(shapeBounds);
    const w = shape["dmndi:DMNDecisionServiceDividerLine"]["di:waypoint"]!;

    w[0]["@_x"] += deltaX;
    w[0]["@_y"] += deltaY;

    w[1]["@_x"] += deltaX;
    w[1]["@_y"] += deltaY;
  }

  return {
    delta: {
      x: deltaX,
      y: deltaY,
    },
    newPosition: {
      x: shapeBounds["@_x"],
      y: shapeBounds["@_y"],
    },
  };
}
