import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { NodeNature, nodeNatures } from "./NodeNature";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import { repopulateInputDataAndDecisionsOnDecisionService } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { XmlQName, buildXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";

export function deleteNode({
  definitions,
  dmnObject,
  dmnObjectQName,
}: {
  definitions: DMN15__tDefinitions;
  dmnObject: { type: NodeType; id: string };
  dmnObjectQName: XmlQName;
}) {
  const { diagramElements } = addOrGetDefaultDiagram({ definitions });

  // Edges are deleted by a separate call to `deleteEdge`.

  // FIXME: Tiago --> Delete extension elements when deleting nodes that contain expressions. What else needs to be clened up?

  // delete the DMNShape
  const shapeDmnElementRef = buildXmlQName(dmnObjectQName);
  diagramElements?.splice(
    (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === shapeDmnElementRef),
    1
  );

  // External nodes don't have a dmnObject associated with it, just the shape..
  if (!dmnObjectQName.prefix) {
    // Delete the dmnObject itself
    if (nodeNatures[dmnObject.type] === NodeNature.ARTIFACT) {
      definitions.artifact?.splice(
        (definitions.artifact ?? []).findIndex((a) => a["@_id"] === dmnObject.id),
        1
      );
    } else if (nodeNatures[dmnObject.type] === NodeNature.DRG_ELEMENT) {
      definitions.drgElement?.splice(
        (definitions.drgElement ?? []).findIndex((d) => d["@_id"] === dmnObject.id),
        1
      );
    } else {
      throw new Error(`Unknown node nature '${nodeNatures[dmnObject.type]}'.`);
    }
  }

  // FIXME: Tiago --> How to make this reactively?
  for (let i = 0; i < (definitions.drgElement ?? []).length; i++) {
    const drgElement = definitions.drgElement![i];
    if (drgElement.__$$element === "decisionService") {
      repopulateInputDataAndDecisionsOnDecisionService({ definitions, decisionService: drgElement });
    }
  }
}
