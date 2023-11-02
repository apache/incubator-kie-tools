import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeNature } from "./NodeNature";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import {
  repopulateInputDataAndDecisionsOnAllDecisionServices,
  repopulateInputDataAndDecisionsOnDecisionService,
} from "./repopulateInputDataAndDecisionsOnDecisionService";
import { XmlQName, buildXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";

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
  const { diagramElements, widthsExtension } = addOrGetDefaultDiagram({ definitions });

  // Edges need to be deleted by a separate call to `deleteEdge` prior to this.

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
      const deleted = definitions.drgElement?.splice(
        (definitions.drgElement ?? []).findIndex((d) => d["@_id"] === dmnObjectId),
        1
      );

      const deletedIdsOnDrgElementTree = getNewDmnIdRandomizer()
        .ack({ json: deleted, type: "DMN15__tDefinitions", attr: "drgElement" })
        .getOriginalIds();

      // Delete widths
      widthsExtension["kie:ComponentWidths"] = widthsExtension["kie:ComponentWidths"]?.filter((w) => {
        !deletedIdsOnDrgElementTree.has(w["@_dmnElementRef"]!);
      });
    } else if (nodeNature === NodeNature.UNKNOWN) {
      // Ignore. There's no dmnObject here.
    } else {
      throw new Error(`Unknown node nature '${nodeNature}'.`);
    }
  }

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions });
}
