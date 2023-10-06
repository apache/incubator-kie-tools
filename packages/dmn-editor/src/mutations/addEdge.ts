import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DC__Bounds,
  DMN15__tAssociation,
  DMN15__tAuthorityRequirement,
  DMN15__tDecision,
  DMN15__tDefinitions,
  DMN15__tInformationRequirement,
  DMN15__tKnowledgeRequirement,
  DMNDI15__DMNEdge,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { TargetHandleId } from "../diagram/connections/PositionalTargetNodeHandles";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { _checkIsValidConnection } from "../diagram/connections/isValidConnection";
import { EDGE_TYPES } from "../diagram/edges/EdgeTypes";
import {
  getBoundsCenterPoint,
  getDiscreteAutoPositioningEdgeIdMarker,
  getPointForHandle,
} from "../diagram/maths/DmnMaths";
import { getRequirementsFromEdge } from "./addConnectedNode";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import { Unpacked } from "../tsExt/tsExt";
import { repopulateInputDataAndDecisionsOnDecisionService } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";

export function addEdge({
  definitions,
  sourceNode,
  targetNode,
  edge,
  keepWaypointsIfSameTarget,
}: {
  definitions: DMN15__tDefinitions;
  sourceNode: {
    type: NodeType;
    data: DmnDiagramNodeData;
    href: string;
    bounds: DC__Bounds;
    shapeId: string | undefined;
  };
  targetNode: {
    type: NodeType;
    data: DmnDiagramNodeData;
    href: string;
    bounds: DC__Bounds;
    shapeId: string | undefined;
    index: number;
  };
  edge: { type: EdgeType; handle: TargetHandleId };
  keepWaypointsIfSameTarget: boolean;
}) {
  if (!_checkIsValidConnection(sourceNode, targetNode, edge.type)) {
    throw new Error(`DMN MUTATION: Invalid structure: (${sourceNode.type}) --${edge.type}--> (${targetNode.type}) `);
  }

  const newEdgeId = generateUuid();

  let existingEdgeId: string | undefined = undefined;

  // Associations
  if (edge.type === EDGE_TYPES.association) {
    definitions.artifact ??= [];

    const newAssociation: DMN15__tAssociation = {
      "@_id": newEdgeId,
      "@_associationDirection": "Both",
      sourceRef: { "@_href": `${sourceNode.href}` },
      targetRef: { "@_href": `${targetNode.href}` },
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
      "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
    });
  }
  // Requirements
  else {
    const requirements = getRequirementsFromEdge(sourceNode, newEdgeId, edge.type);
    const drgElement = definitions.drgElement![targetNode.index] as DMN15__tDecision; // We cast to tDecision here because it has all three types of requirement.
    if (requirements?.informationRequirement) {
      drgElement.informationRequirement ??= [];
      const removed = removeFirstMatchIfPresent(drgElement.informationRequirement, (ir) =>
        doesInformationRequirementsPointTo(ir, sourceNode.href)
      );
      existingEdgeId = removed?.["@_id"];
      drgElement.informationRequirement?.push(
        ...requirements.informationRequirement.map((s) => ({
          ...s,
          "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
        }))
      );
    }
    //
    else if (requirements?.knowledgeRequirement) {
      drgElement.knowledgeRequirement ??= [];
      const removed = removeFirstMatchIfPresent(drgElement.knowledgeRequirement, (kr) =>
        doesKnowledgeRequirementsPointTo(kr, sourceNode.href)
      );
      existingEdgeId = removed?.["@_id"];
      drgElement.knowledgeRequirement?.push(
        ...requirements.knowledgeRequirement.map((s) => ({
          ...s,
          "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
        }))
      );
    }
    //
    else if (requirements?.authorityRequirement) {
      drgElement.authorityRequirement ??= [];
      const removed = removeFirstMatchIfPresent(drgElement.authorityRequirement, (ar) =>
        doesAuthorityRequirementsPointTo(ar, sourceNode.href)
      );
      existingEdgeId = removed?.["@_id"];
      drgElement.authorityRequirement?.push(
        ...requirements.authorityRequirement.map((s) => ({
          ...s,
          "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
        }))
      );
    }
  }

  const { diagramElements } = addOrGetDefaultDiagram({ definitions });

  // Remove existing
  const removedDmnEdge: DMNDI15__DMNEdge | undefined = removeFirstMatchIfPresent(
    diagramElements,
    (e) => e.__$$element === "dmndi:DMNEdge" && e["@_dmnElementRef"] === existingEdgeId
  );

  const newWaypoints = keepWaypointsIfSameTarget
    ? [
        ...(
          removedDmnEdge?.["di:waypoint"] ?? [
            getBoundsCenterPoint(sourceNode.bounds),
            getPointForHandle({ bounds: targetNode.bounds, handle: edge.handle }),
          ]
        ).slice(0, -1),
        getPointForHandle({ bounds: targetNode.bounds, handle: edge.handle }),
      ]
    : [getBoundsCenterPoint(sourceNode.bounds), getPointForHandle({ bounds: targetNode.bounds, handle: edge.handle })];

  const newDmnEdge: Unpacked<typeof diagramElements> = {
    __$$element: "dmndi:DMNEdge",
    "@_id": withoutDiscreteAutoPosinitioningMarker(removedDmnEdge?.["@_id"] ?? generateUuid()),
    "@_dmnElementRef": existingEdgeId ?? newEdgeId,
    "@_sourceElement": sourceNode.shapeId,
    "@_targetElement": targetNode.shapeId,
    "di:waypoint": newWaypoints,
  };

  // Replace with the new one.
  diagramElements.push(newDmnEdge);

  // FIXME: Tiago --> How to make this reactively?
  for (let i = 0; i < (definitions.drgElement ?? []).length; i++) {
    const drgElement = definitions.drgElement![i];
    if (drgElement.__$$element === "decisionService") {
      repopulateInputDataAndDecisionsOnDecisionService({ definitions, decisionService: drgElement });
    }
  }

  return { newDmnEdge };
}

function doesInformationRequirementsPointTo(a: DMN15__tInformationRequirement, nodeId: string) {
  return (
    a.requiredInput?.["@_href"] === `${nodeId}` || //
    a.requiredDecision?.["@_href"] === `${nodeId}`
  );
}

function doesKnowledgeRequirementsPointTo(a: DMN15__tKnowledgeRequirement, nodeId: string) {
  return a.requiredKnowledge?.["@_href"] === `${nodeId}`;
}

function doesAuthorityRequirementsPointTo(a: DMN15__tAuthorityRequirement, nodeId: string) {
  return (
    a.requiredInput?.["@_href"] === `${nodeId}` ||
    a.requiredDecision?.["@_href"] === `${nodeId}` ||
    a.requiredAuthority?.["@_href"] === `${nodeId}`
  );
}

function areAssociationsEquivalent(a: DMN15__tAssociation, b: DMN15__tAssociation) {
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

function tryKeepingEdgeId(existingEdgeId: string | undefined, newEdgeId: string) {
  return existingEdgeId ?? newEdgeId;
}
function withoutDiscreteAutoPosinitioningMarker(edgeId: string) {
  const marker = getDiscreteAutoPositioningEdgeIdMarker(edgeId);
  return marker ? edgeId.replace(`${marker}`, "") : edgeId;
}
