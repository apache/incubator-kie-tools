import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

export function deleteNode({
  definitions,
  node,
}: {
  definitions: DMN14__tDefinitions;
  node: { id: string; index: number; shapeIndex: number };
}) {
  definitions.drgElement?.splice(node.index, 1);
  definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]["dmndi:DMNDiagramElement"]?.splice(node.shapeIndex, 1);

  // TODO:
  // delete diagram edges
  // delete requirements
  // check for node type
}
