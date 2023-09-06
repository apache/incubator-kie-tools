import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import { NodeNature, nodeNatures } from "./NodeNature";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import { deleteEdge } from "./deleteEdge";

export function deleteNode({
  definitions,
  node,
  targetEdges,
}: {
  definitions: DMN15__tDefinitions;
  node: { type: NodeType; id: string };
  targetEdges: { id: string; data: DmnDiagramEdgeData }[];
}) {
  const { diagramElements } = addOrGetDefaultDiagram({ definitions });

  const uniqueTargetEdgeIds = new Set<string>();

  for (const edge of targetEdges) {
    if (uniqueTargetEdgeIds.has(edge.id)) {
      continue;
    } else {
      uniqueTargetEdgeIds.add(edge.id);
    }

    deleteEdge({ definitions, edge: { id: edge.id, dmnObject: edge.data.dmnObject } });
  }

  // FIXME: Tiago --> Delete extension elements when deleting nodes that contain expressions. What else needs to be clened up?

  // delete the DMNShape
  diagramElements?.splice(
    (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === node.id),
    1
  );

  // delete the dmnObject itself
  if (nodeNatures[node.type] === NodeNature.ARTIFACT) {
    definitions.artifact?.splice(
      (definitions.artifact ?? []).findIndex((a) => a["@_id"] === node.id),
      1
    );
  } else if (nodeNatures[node.type] === NodeNature.DRG_ELEMENT) {
    definitions.drgElement?.splice(
      (definitions.drgElement ?? []).findIndex((d) => d["@_id"] === node.id),
      1
    );
  } else {
    throw new Error(`Unknown node nature '${nodeNatures[node.type]}'.`);
  }
}
