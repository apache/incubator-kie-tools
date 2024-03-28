/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { XmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import * as RF from "reactflow";
import { KIE_DMN_UNKNOWN_NAMESPACE } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { snapShapeDimensions, snapShapePosition } from "../../diagram/SnapGrid";
import { EdgeType, NodeType } from "../../diagram/connections/graphStructure";
import { EDGE_TYPES } from "../../diagram/edges/EdgeTypes";
import { DmnDiagramEdgeData } from "../../diagram/edges/Edges";
import { DrgEdge, DrgAdjacencyList, EdgeVisitor, NodeVisitor, getAdjMatrix, traverse } from "../../diagram/graph/graph";
import { getNodeTypeFromDmnObject } from "../../diagram/maths/DmnMaths";
import { DECISION_SERVICE_COLLAPSED_DIMENSIONS, MIN_NODE_SIZES } from "../../diagram/nodes/DefaultSizes";
import { ___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches } from "../../diagram/nodes/NodeSvgs";
import { NODE_TYPES } from "../../diagram/nodes/NodeTypes";
import { DmnDiagramNodeData, NodeDmnObjects } from "../../diagram/nodes/Nodes";
import { Unpacked } from "../../tsExt/tsExt";
import { buildXmlHref, parseXmlHref } from "../../xml/xmlHrefs";
import { TypeOrReturnType } from "../ComputedStateCache";
import { Computed, State } from "../Store";
import { getDecisionServicePropertiesRelativeToThisDmn } from "../../mutations/addExistingDecisionServiceToDrd";

export const NODE_LAYERS = {
  GROUP_NODE: 0,
  NODES: 1000, // We need a difference > 1000 here, since ReactFlow will add 1000 to the z-index when a node is selected.
  DECISION_SERVICE_NODE: 2000, // We need a difference > 1000 here, since ReactFlow will add 1000 to the z-index when a node is selected.
  NESTED_NODES: 4000,
};

type AckEdge = (args: {
  id: string;
  dmnObject: DmnDiagramEdgeData["dmnObject"];
  type: EdgeType;
  source: string;
  target: string;
}) => RF.Edge<DmnDiagramEdgeData>;

type AckNode = (
  dmnObjectQName: XmlQName,
  dmnObject: NodeDmnObjects,
  index: number
) => RF.Node<DmnDiagramNodeData> | undefined;

export function computeDiagramData(
  diagram: State["diagram"],
  definitions: State["dmn"]["model"]["definitions"],
  externalModelTypesByNamespace: TypeOrReturnType<Computed["getExternalModelTypesByNamespace"]>,
  indexedDrd: TypeOrReturnType<Computed["indexedDrd"]>,
  isAlternativeInputDataShape: boolean
) {
  // console.time("nodes");
  ___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches.flag =
    !___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches.flag;

  const drgElementsWithoutVisualRepresentationOnCurrentDrd: string[] = [];

  const selectedNodesById = new Map<string, RF.Node<DmnDiagramNodeData>>();
  const selectedEdgesById = new Map<string, RF.Edge<DmnDiagramEdgeData>>();
  const selectedNodeTypes = new Set<NodeType>();
  const nodesById = new Map<string, RF.Node<DmnDiagramNodeData>>();
  const edgesById = new Map<string, RF.Edge<DmnDiagramEdgeData>>();
  const parentIdsById = new Map<string, DmnDiagramNodeData>();

  const { selectedNodes, draggingNodes, resizingNodes, selectedEdges } = {
    selectedNodes: new Set(diagram._selectedNodes),
    draggingNodes: new Set(diagram.draggingNodes),
    resizingNodes: new Set(diagram.resizingNodes),
    selectedEdges: new Set(diagram._selectedEdges),
  };

  // console.time("edges");
  const edges: RF.Edge<DmnDiagramEdgeData>[] = [];

  const drgEdges: DrgEdge[] = [];
  const drgAdjacencyList: DrgAdjacencyList = new Map();

  const ackEdge: AckEdge = ({ id, type, dmnObject, source, target }) => {
    const data = {
      dmnObject,
      dmnEdge: id ? indexedDrd.dmnEdgesByDmnElementRef.get(id) : undefined,
      dmnShapeSource: indexedDrd.dmnShapesByHref.get(source),
      dmnShapeTarget: indexedDrd.dmnShapesByHref.get(target),
    };

    const edge: RF.Edge<DmnDiagramEdgeData> = {
      data,
      id,
      type,
      source,
      target,
      selected: selectedEdges.has(id),
    };

    edgesById.set(edge.id, edge);
    if (edge.selected) {
      selectedEdgesById.set(edge.id, edge);
    }

    edges.push(edge);

    drgEdges.push({ id, sourceId: source, targetId: target, dmnObject });

    const targetAdjancyList = drgAdjacencyList.get(target);
    if (!targetAdjancyList) {
      drgAdjacencyList.set(target, { dependencies: new Set([source]) });
    } else {
      targetAdjancyList.dependencies.add(source);
    }

    return edge;
  };

  // requirements
  ackRequirementEdges(definitions["@_namespace"], definitions["@_namespace"], definitions.drgElement, ackEdge);

  // associations
  (definitions.artifact ?? []).forEach((dmnObject, index) => {
    if (dmnObject.__$$element !== "association") {
      return;
    }

    ackEdge({
      id: dmnObject["@_id"]!,
      dmnObject: {
        namespace: definitions["@_namespace"],
        type: dmnObject.__$$element,
        id: dmnObject["@_id"]!,
        requirementType: "association",
        index,
      },
      type: EDGE_TYPES.association,
      source: dmnObject.sourceRef?.["@_href"],
      target: dmnObject.targetRef?.["@_href"],
    });
  });

  // console.timeEnd("edges");
  const ackNode: AckNode = (dmnObjectQName, dmnObject, index) => {
    const type = getNodeTypeFromDmnObject(dmnObject);
    if (!type) {
      return undefined;
    }

    // If the QName is composite, we try and get the namespace from the XML namespace declarations. If it's not found, we use `UNKNOWN_DMN_NAMESPACE`
    // If the QName is simple, we simply say that the namespace is undefined, which is the same as the default namespace.
    const dmnObjectNamespace = dmnObjectQName.prefix
      ? definitions[`@_xmlns:${dmnObjectQName.prefix}`] ?? KIE_DMN_UNKNOWN_NAMESPACE
      : undefined;

    const id = buildXmlHref({ namespace: dmnObjectNamespace, id: dmnObjectQName.localPart });

    const _shape = indexedDrd.dmnShapesByHref.get(id);
    if (!_shape) {
      drgElementsWithoutVisualRepresentationOnCurrentDrd.push(id);
      return undefined;
    }

    const { dmnElementRefQName, ...shape } = _shape;

    const data: DmnDiagramNodeData = {
      dmnObjectNamespace,
      dmnObjectQName,
      dmnObject,
      shape,
      index,

      // Properties to be overridden
      hasHiddenRequirements: false,
      parentRfNode: undefined,
    };

    const newNode: RF.Node<DmnDiagramNodeData> = {
      id,
      type,
      selected: selectedNodes.has(id),
      dragging: draggingNodes.has(id),
      resizing: resizingNodes.has(id),
      position: snapShapePosition(diagram.snapGrid, shape),
      data,
      zIndex: NODE_LAYERS.NODES,
      style: {
        ...snapShapeDimensions(
          diagram.snapGrid,
          shape,
          MIN_NODE_SIZES[type]({ snapGrid: diagram.snapGrid, isAlternativeInputDataShape })
        ),
      },
    };

    if (dmnObject?.__$$element === "decisionService") {
      const { containedDecisionHrefsRelativeToThisDmn } = getDecisionServicePropertiesRelativeToThisDmn({
        thisDmnsNamespace: definitions["@_namespace"],
        decisionServiceNamespace: dmnObjectNamespace ?? definitions["@_namespace"],
        decisionService: dmnObject,
      });

      for (let i = 0; i < containedDecisionHrefsRelativeToThisDmn.length; i++) {
        parentIdsById.set(containedDecisionHrefsRelativeToThisDmn[i], data);
      }

      if (shape["@_isCollapsed"]) {
        newNode.style = {
          ...newNode.style,
          ...DECISION_SERVICE_COLLAPSED_DIMENSIONS,
        };
      }
    }

    nodesById.set(newNode.id, newNode);
    if (newNode.selected) {
      selectedNodesById.set(newNode.id, newNode);
      selectedNodeTypes.add(newNode.type as NodeType);
    }
    return newNode;
  };

  const localNodes: RF.Node<DmnDiagramNodeData>[] = [
    ...(definitions.drgElement ?? []).flatMap((dmnObject, index) => {
      const newNode = ackNode({ type: "xml-qname", localPart: dmnObject["@_id"]! }, dmnObject, index);
      return newNode ? [newNode] : [];
    }),
    ...(definitions.artifact ?? []).flatMap((dmnObject, index) => {
      if (dmnObject.__$$element === "association") {
        return [];
      }

      const newNode = ackNode({ type: "xml-qname", localPart: dmnObject["@_id"]! }, dmnObject, index);
      return newNode ? [newNode] : [];
    }),
  ];

  const externalDrgElementsByIdByNamespace = [...externalModelTypesByNamespace.dmns.entries()].reduce(
    (acc, [namespace, externalDmn]) => {
      // Taking advantage of the loop to add the edges here...
      ackRequirementEdges(
        definitions["@_namespace"],
        externalDmn.model.definitions["@_namespace"],
        externalDmn.model.definitions.drgElement,
        ackEdge
      );

      return acc.set(
        namespace,
        (externalDmn.model.definitions.drgElement ?? []).reduce(
          (acc, e, index) => acc.set(e["@_id"]!, { element: e, index }),
          new Map<string, { index: number; element: Unpacked<DMN15__tDefinitions["drgElement"]> }>()
        )
      );
    },
    new Map<string, Map<string, { index: number; element: Unpacked<DMN15__tDefinitions["drgElement"]> }>>()
  );

  const externalNodes = [...indexedDrd.dmnShapesByHref.entries()].flatMap(([href, shape]) => {
    if (nodesById.get(href)) {
      return [];
    }

    if (!nodesById.get(href) && !indexedDrd.hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects.has(href)) {
      // Unknown local node.
      console.warn(`DMN DIAGRAM: Found a shape that references a local DRG element that doesn't exist.`, shape);
      const newNode = ackNode(shape.dmnElementRefQName, null, -1);
      return newNode ? [newNode] : [];
    }

    const namespace = definitions[`@_xmlns:${shape.dmnElementRefQName.prefix}`];
    if (!namespace) {
      console.warn(
        `DMN DIAGRAM: Found a shape that references an external node with a namespace that is not declared at this DMN.`,
        shape
      );
      const newNode = ackNode(shape.dmnElementRefQName, null, -1);
      return newNode ? [newNode] : [];
    }

    const externalDrgElementsById = externalDrgElementsByIdByNamespace.get(namespace);
    if (!externalDrgElementsById) {
      console.warn(
        `DMN DIAGRAM: Found a shape that references an external node from a namespace that is not provided on this DMN's external DMNs mapping.`,
        shape
      );
      const newNode = ackNode(shape.dmnElementRefQName, null, -1);
      return newNode ? [newNode] : [];
    }

    const externalDrgElement = externalDrgElementsById.get(shape.dmnElementRefQName.localPart);
    if (!externalDrgElement) {
      console.warn(`DMN DIAGRAM: Found a shape that references a non-existent node from an external DMN.`, shape);
      const newNode = ackNode(shape.dmnElementRefQName, null, -1);
      return newNode ? [newNode] : [];
    }

    const newNode = ackNode(shape.dmnElementRefQName, externalDrgElement.element, externalDrgElement.index);
    return newNode ? [newNode] : [];
  });

  // Groups are always at the back. Decision Services after groups, then everything else.
  const sortedNodes = [...localNodes, ...externalNodes]
    .sort((a, b) => Number(b.type === NODE_TYPES.decisionService) - Number(a.type === NODE_TYPES.decisionService))
    .sort((a, b) => Number(b.type === NODE_TYPES.group) - Number(a.type === NODE_TYPES.group));

  // Selected edges go to the end of the array. This is necessary because z-index doesn't work on SVGs.
  const sortedEdges = edges
    .filter((e) => nodesById.has(e.source) && nodesById.has(e.target))
    .sort((a, b) => Number(selectedEdges.has(a.id)) - Number(selectedEdges.has(b.id)));

  // Search on the node list for the missing dependencies on the DRD.
  for (const node of sortedNodes) {
    for (const dependencyNodeId of drgAdjacencyList.get(node.id)?.dependencies ?? new Set()) {
      if (!nodesById.get(dependencyNodeId)) {
        node.data.hasHiddenRequirements = true;
        break;
      }
    }
  }

  // console.timeEnd("nodes");
  if (diagram.overlays.enableNodeHierarchyHighlight) {
    assignClassesToHighlightedHierarchyNodes(diagram._selectedNodes, nodesById, edgesById, drgEdges);
  }

  // Assign parents & z-index to NODES
  for (let i = 0; i < sortedNodes.length; i++) {
    const parentNodeData = parentIdsById.get(sortedNodes[i].id);
    if (parentNodeData) {
      sortedNodes[i].data.parentRfNode = nodesById.get(
        buildXmlHref({ namespace: parentNodeData.dmnObjectNamespace, id: parentNodeData.dmnObjectQName.localPart })
      );
      sortedNodes[i].extent = undefined; // Allows the node to be dragged freely outside of parent's bounds.
      sortedNodes[i].zIndex = NODE_LAYERS.NESTED_NODES;
    }

    if (sortedNodes[i].type === NODE_TYPES.group) {
      sortedNodes[i].zIndex = NODE_LAYERS.GROUP_NODE;
    } else if (sortedNodes[i].type === NODE_TYPES.decisionService) {
      sortedNodes[i].zIndex = NODE_LAYERS.DECISION_SERVICE_NODE;
    }
  }

  return {
    drgEdges,
    drgAdjacencyList,
    nodes: sortedNodes,
    edges: sortedEdges,
    edgesById,
    nodesById,
    selectedNodeTypes,
    selectedNodesById,
    selectedEdgesById,
    drgElementsWithoutVisualRepresentationOnCurrentDrd,
  };
}

function ackRequirementEdges(
  thisDmnsNamespace: string,
  drgElementsNamespace: string,
  drgElements: DMN15__tDefinitions["drgElement"],
  ackEdge: AckEdge
) {
  const namespace = drgElementsNamespace === thisDmnsNamespace ? "" : drgElementsNamespace;

  for (const dmnObject of drgElements ?? []) {
    // information requirements
    if (dmnObject.__$$element === "decision") {
      (dmnObject.informationRequirement ?? []).forEach((ir, index) => {
        const irHref = parseXmlHref((ir.requiredDecision ?? ir.requiredInput)!["@_href"]);
        ackEdge({
          id: ir["@_id"]!,
          dmnObject: {
            namespace: drgElementsNamespace,
            type: dmnObject.__$$element,
            id: dmnObject["@_id"]!,
            requirementType: "informationRequirement",
            index,
          },
          type: EDGE_TYPES.informationRequirement,
          source: buildXmlHref({ namespace: irHref.namespace ?? namespace, id: irHref.id }),
          target: buildXmlHref({ namespace, id: dmnObject["@_id"]! }),
        });
      });
    }
    // knowledge requirements
    if (dmnObject.__$$element === "decision" || dmnObject.__$$element === "businessKnowledgeModel") {
      (dmnObject.knowledgeRequirement ?? []).forEach((kr, index) => {
        const krHref = parseXmlHref(kr.requiredKnowledge["@_href"]);
        ackEdge({
          id: kr["@_id"]!,
          dmnObject: {
            namespace: drgElementsNamespace,
            type: dmnObject.__$$element,
            id: dmnObject["@_id"]!,
            requirementType: "knowledgeRequirement",
            index,
          },
          type: EDGE_TYPES.knowledgeRequirement,
          source: buildXmlHref({ namespace: krHref.namespace ?? namespace, id: krHref.id }),
          target: buildXmlHref({ namespace, id: dmnObject["@_id"]! }),
        });
      });
    }
    // authority requirements
    if (
      dmnObject.__$$element === "decision" ||
      dmnObject.__$$element === "businessKnowledgeModel" ||
      dmnObject.__$$element === "knowledgeSource"
    ) {
      (dmnObject.authorityRequirement ?? []).forEach((ar, index) => {
        const arHref = parseXmlHref((ar.requiredInput ?? ar.requiredDecision ?? ar.requiredAuthority)!["@_href"]);
        ackEdge({
          id: ar["@_id"]!,
          dmnObject: {
            namespace: drgElementsNamespace,
            type: dmnObject.__$$element,
            id: dmnObject["@_id"]!,
            requirementType: "authorityRequirement",
            index,
          },
          type: EDGE_TYPES.authorityRequirement,
          source: buildXmlHref({ namespace: arHref.namespace ?? namespace, id: arHref.id }),
          target: buildXmlHref({ namespace, id: dmnObject["@_id"]! }),
        });
      });
    }
  }
}

export function assignClassesToHighlightedHierarchyNodes(
  selected: string[],
  nodesById: Map<string, RF.Node>,
  edgesById: Map<string, RF.Edge>,
  drgEdges: DrgEdge[]
) {
  const nodeVisitor: NodeVisitor = (nodeId, traversalDirection) => {
    const node = nodesById.get(nodeId);
    if (node) {
      node.className = `hierarchy ${traversalDirection}`;
    }
  };

  const edgeVisitor: EdgeVisitor = (edge, traversalDirection) => {
    const rfEdge = edgesById.get(edge.id);
    if (rfEdge) {
      rfEdge.className = `hierarchy ${traversalDirection}`;
    }
  };

  const __selectedSet = new Set(selected);
  const __adjMatrix = getAdjMatrix(drgEdges);

  traverse(__adjMatrix, __selectedSet, selected, "up", nodeVisitor, edgeVisitor);
  traverse(__adjMatrix, __selectedSet, selected, "down", nodeVisitor, edgeVisitor); // Traverse "down" after "up" because when there's a cycle, highlighting a node as a dependency is preferable.
}
