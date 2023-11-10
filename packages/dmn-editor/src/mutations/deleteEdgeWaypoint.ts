import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { addOrGetDrd } from "./addOrGetDrd";

export function deleteEdgeWaypoint({
  definitions,
  edgeIndex,
  waypointIndex,
}: {
  definitions: DMN15__tDefinitions;
  edgeIndex: number;
  waypointIndex: number;
}) {
  const { diagramElements } = addOrGetDrd({ definitions });

  const diagramElement = diagramElements[edgeIndex];
  if (diagramElement.__$$element !== "dmndi:DMNEdge") {
    throw new Error("Can't remove a waypoint from an element that is not a DMNEdge.");
  }

  if (waypointIndex > (diagramElement["di:waypoint"]?.length ?? 0) - 1) {
    throw new Error(
      `Can't remove waypoint with index '${waypointIndex}' from DMNEdge '${diagramElement["@_id"]}' because it doesn't exist.`
    );
  }

  diagramElement["di:waypoint"]!.splice(waypointIndex, 1);
}
