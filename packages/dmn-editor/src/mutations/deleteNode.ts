import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { NodeNature, nodeNatures } from "./NodeNature";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import { repopulateInputDataAndDecisionsOnDecisionService } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { XmlQName, buildXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";

export function deleteNode({
  definitions,
  nodeNature,
  dmnObjectId,
  dmnObjectQName,
}: {
  definitions: DMN15__tDefinitions;
  nodeNature: NodeNature;
  dmnObjectId: string | undefined;
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

  // External or unknown nodes don't have a dmnObject associated with it, just the shape..
  if (!dmnObjectQName.prefix) {
    // Delete the dmnObject itself
    if (nodeNature === NodeNature.ARTIFACT) {
      definitions.artifact?.splice(
        (definitions.artifact ?? []).findIndex((a) => a["@_id"] === dmnObjectId),
        1
      );
    } else if (nodeNature === NodeNature.DRG_ELEMENT) {
      definitions.drgElement?.splice(
        (definitions.drgElement ?? []).findIndex((d) => d["@_id"] === dmnObjectId),
        1
      );
    } else if (nodeNature === NodeNature.UNKNOWN) {
      // Ignore. There's no dmnObject here.
    } else {
      throw new Error(`Unknown node nature '${nodeNature}'.`);
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
