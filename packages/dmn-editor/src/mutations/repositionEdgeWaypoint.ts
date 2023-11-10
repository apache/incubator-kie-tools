import { DC__Point, DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { addOrGetDrd } from "./addOrGetDrd";

export function repositionEdgeWaypoint({
  definitions,
  drdIndex,
  edgeIndex,
  waypointIndex,
  waypoint,
}: {
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  edgeIndex: number;
  waypointIndex: number;
  waypoint: DC__Point;
}) {
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  const diagramElement = diagramElements[edgeIndex];
  if (diagramElement.__$$element !== "dmndi:DMNEdge") {
    throw new Error("Can't remove a waypoint from an element that is not a DMNEdge.");
  }

  if (waypointIndex > (diagramElement["di:waypoint"]?.length ?? 0) - 1) {
    throw new Error(
      `Can't reposition waypoint with index '${waypointIndex}' from DMNEdge '${diagramElement["@_id"]}' because it doesn't exist.`
    );
  }

  diagramElement["di:waypoint"]![waypointIndex] = waypoint;
}
