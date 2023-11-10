import { DC__Point, DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { addOrGetDrd } from "./addOrGetDrd";

export function addEdgeWaypoint({
  definitions,
  edgeIndex,
  beforeIndex,
  waypoint,
}: {
  definitions: DMN15__tDefinitions;
  edgeIndex: number;
  beforeIndex: number;
  waypoint: DC__Point;
}) {
  const { diagramElements } = addOrGetDrd({ definitions });

  const diagramElement = diagramElements[edgeIndex];
  if (diagramElement.__$$element !== "dmndi:DMNEdge") {
    throw new Error("Can't remove a waypoint from an element that is not a DMNEdge.");
  }

  if (beforeIndex > (diagramElement["di:waypoint"]?.length ?? 0) - 1) {
    throw new Error(
      `Can't add waypoint before index '${beforeIndex}' to DMNEdge '${diagramElement["@_id"]}' because the waypoint array is smaller than 'beforeIndex' requires.`
    );
  }

  diagramElement["di:waypoint"]!.splice(beforeIndex, 0, waypoint);
}
