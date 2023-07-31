import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DC__Bounds,
  DMN14__tAssociation,
  DMN14__tAuthorityRequirement,
  DMN14__tDecision,
  DMN14__tDefinitions,
  DMN14__tInformationRequirement,
  DMN14__tKnowledgeRequirement,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { TargetHandleId } from "../diagram/connections/NodeHandles";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { _checkIsValidConnection } from "../diagram/connections/isValidConnection";
import { EDGE_TYPES } from "../diagram/edges/EdgeTypes";
import { getBoundsCenterPoint, getPointForHandle } from "../diagram/maths/DmnMaths";
import { getRequirementsFromEdge } from "./addConnectedNode";

export function addEdge({
  definitions,
  sourceNode,
  targetNode,
  edge,
}: {
  definitions: DMN14__tDefinitions;
  sourceNode: { type: NodeType; id: string; bounds: DC__Bounds; shapeId: string | undefined };
  targetNode: { type: NodeType; id: string; bounds: DC__Bounds; shapeId: string | undefined; index: number };
  edge: { type: EdgeType; handle: TargetHandleId };
}) {
  if (!_checkIsValidConnection(sourceNode, targetNode, edge.type)) {
    throw new Error(`Invalid structure: (${sourceNode.type}) --${edge.type}--> (${targetNode.type}) `);
  }

  const newEdgeId = generateUuid();

  let existingEdgeId: string | undefined = undefined;

  // Associations
  if (edge.type === EDGE_TYPES.association) {
    definitions.artifact ??= [];

    const newAssociation: DMN14__tAssociation = {
      "@_id": newEdgeId,
      "@_associationDirection": "Both",
      sourceRef: { "@_href": `#${sourceNode.id}` },
      targetRef: { "@_href": `#${targetNode.id}` },
    };

    // Remove previously existing association
    const removed = removeFirstMatchIfPresent(
      definitions.artifact,
      (a) => a.__$$element === "association" && areAssociationsEquivalent(a, newAssociation)
    );
    existingEdgeId = removed?.["@_id"];

    // Replace with the new one.
    definitions.artifact?.push({
      __$$element: "association",
      ...newAssociation,
    });
  }
  // Requirements
  else {
    const requirements = getRequirementsFromEdge(sourceNode, newEdgeId, edge.type);
    const drgElement = definitions.drgElement![targetNode.index] as DMN14__tDecision; // We cast to tDecision here because it has all three types of requirement.
    if (requirements?.informationRequirement) {
      drgElement.informationRequirement ??= [];
      const removed = removeFirstMatchIfPresent(drgElement.informationRequirement, (ir) =>
        doesInformationRequirementsPointTo(ir, sourceNode.id)
      );
      existingEdgeId = removed?.["@_id"];
      drgElement.informationRequirement?.push(...(requirements?.informationRequirement ?? []));
    }
    //
    else if (requirements?.knowledgeRequirement) {
      drgElement.knowledgeRequirement ??= [];
      const removed = removeFirstMatchIfPresent(drgElement.knowledgeRequirement, (kr) =>
        doesKnowledgeRequirementsPointTo(kr, sourceNode.id)
      );
      existingEdgeId = removed?.["@_id"];
      drgElement.knowledgeRequirement?.push(...(requirements?.knowledgeRequirement ?? []));
    }
    //
    else if (requirements?.authorityRequirement) {
      drgElement.authorityRequirement ??= [];
      const removed = removeFirstMatchIfPresent(drgElement.authorityRequirement, (ar) =>
        doesAuthorityRequirementsPointTo(ar, sourceNode.id)
      );
      existingEdgeId = removed?.["@_id"];
      drgElement.authorityRequirement?.push(...(requirements?.authorityRequirement ?? []));
    }
  }

  definitions["dmndi:DMNDI"] ??= {};
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"] ??= [];
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0] ??= {};
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0]["dmndi:DMNDiagramElement"] ??= [];

  // Remove existing
  removeFirstMatchIfPresent(
    definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0]["dmndi:DMNDiagramElement"],
    (e) => e.__$$element === "dmndi:DMNEdge" && e["@_dmnElementRef"] === existingEdgeId
  );

  // Replace with the new one.
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0]["dmndi:DMNDiagramElement"].push({
    __$$element: "dmndi:DMNEdge",
    "@_id": generateUuid(),
    "@_dmnElementRef": newEdgeId,
    "@_sourceElement": sourceNode.shapeId,
    "@_targetElement": targetNode.shapeId,
    "di:waypoint": [
      getBoundsCenterPoint(sourceNode.bounds),
      getPointForHandle({ bounds: targetNode.bounds, handle: edge.handle }),
    ],
  });
}

function doesInformationRequirementsPointTo(a: DMN14__tInformationRequirement, drgElementId: string) {
  return (
    a.requiredInput?.["@_href"] === `#${drgElementId}` || //
    a.requiredDecision?.["@_href"] === `#${drgElementId}`
  );
}

function doesKnowledgeRequirementsPointTo(a: DMN14__tKnowledgeRequirement, drgElementId: string) {
  return a.requiredKnowledge?.["@_href"] === `#${drgElementId}`;
}

function doesAuthorityRequirementsPointTo(a: DMN14__tAuthorityRequirement, drgElementId: string) {
  return (
    a.requiredInput?.["@_href"] === `#${drgElementId}` ||
    a.requiredDecision?.["@_href"] === `#${drgElementId}` ||
    a.requiredAuthority?.["@_href"] === `#${drgElementId}`
  );
}

function areAssociationsEquivalent(a: DMN14__tAssociation, b: DMN14__tAssociation) {
  return (
    (a.sourceRef["@_href"] === b.sourceRef["@_href"] && a.targetRef["@_href"] === b.targetRef["@_href"]) ||
    (a.sourceRef["@_href"] === b.targetRef["@_href"] && a.targetRef["@_href"] === b.sourceRef["@_href"])
  );
}

function removeFirstMatchIfPresent<T>(arr: T[], predicate: Parameters<Array<T>["findIndex"]>[0]): T | undefined {
  const index = arr.findIndex(predicate);
  const removed = arr[index] ?? undefined;
  arr.splice(index, index >= 0 ? 1 : 0);
  return removed;
}
