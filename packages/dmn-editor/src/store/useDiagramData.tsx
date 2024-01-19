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

import {
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useMemo } from "react";
import * as RF from "reactflow";
import { NODE_LAYERS, useDmnEditorStore } from "./Store";
import { snapShapeDimensions, snapShapePosition } from "../diagram/SnapGrid";
import { DECISION_SERVICE_COLLAPSED_DIMENSIONS } from "../diagram/nodes/DefaultSizes";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { EDGE_TYPES } from "../diagram/edges/EdgeTypes";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { DmnDiagramNodeData, NodeDmnObjects } from "../diagram/nodes/Nodes";
import { getNodeTypeFromDmnObject } from "../diagram/maths/DmnMaths";
import { XmlQName, parseXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { buildXmlHref } from "../xml/xmlHrefs";
import { ___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches } from "../diagram/nodes/NodeSvgs";
import { ExternalDmnsIndex } from "../DmnEditor";
import { MIN_NODE_SIZES } from "../diagram/nodes/DefaultSizes";
import { Unpacked } from "../tsExt/tsExt";
import { NodeVisitor, EdgeVisitor, getAdjMatrix, traverse } from "../diagram/graph/graph";
import { KIE_DMN_UNKNOWN_NAMESPACE } from "../Dmn15Spec";

export const diagramColors = {
  hierarchyUp: "#0083a4",
  hierarchyDown: "#003fa4",
  selected: "#006ba4",
};

export function useDiagramData(externalDmnsByNamespace: ExternalDmnsIndex) {
  ___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches.flag =
    !___NASTY_HACK_FOR_SAFARI_to_force_redrawing_svgs_and_avoid_repaint_glitches.flag;

  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const diagram = useDmnEditorStore((s) => s.diagram);

  const { dmnEdgesByDmnElementRef, dmnShapesByHref, hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects } =
    useMemo(() => {
      const dmnEdgesByDmnElementRef = new Map<string, DMNDI15__DMNEdge & { index: number }>();
      const dmnShapesByHref = new Map<string, DMNDI15__DMNShape & { index: number; dmnElementRefQName: XmlQName }>();
      const hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects = new Set<string>();

      const diagramElements =
        thisDmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[diagram.drdIndex]["dmndi:DMNDiagramElement"] ??
        [];
      for (let i = 0; i < diagramElements.length; i++) {
        const e = diagramElements[i];
        // DMNEdge
        if (e.__$$element === "dmndi:DMNEdge") {
          dmnEdgesByDmnElementRef.set(e["@_dmnElementRef"], { ...e, index: i });
        }

        // DMNShape
        else if (e.__$$element === "dmndi:DMNShape") {
          let href: string;
          // @_dmnElementRef is a xsd:QName, meaning it can be prefixed with a namespace name.
          // If we find the namespace as a namespace declaration on the `definitions` object, then this shape represents a node from an included model.
          // Therefore, we need to add it to `dmnShapesForExternalNodesByDmnRefId`, so we can draw these nodes.
          // Do not skip adding it to the regular `dmnShapesByHref`, as nodes will query this.
          const dmnElementRefQName = parseXmlQName(e["@_dmnElementRef"]);
          if (dmnElementRefQName.prefix) {
            const namespace =
              thisDmn.model.definitions[`@_xmlns:${dmnElementRefQName.prefix}`] ?? KIE_DMN_UNKNOWN_NAMESPACE;
            href = buildXmlHref({ namespace, id: dmnElementRefQName.localPart });
            hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects.add(href);
          } else {
            href = buildXmlHref({ id: dmnElementRefQName.localPart });
          }

          dmnShapesByHref.set(href, { ...e, index: i, dmnElementRefQName });
        } else {
          // Ignore anything that is unknown.
        }
      }
      return {
        dmnEdgesByDmnElementRef,
        dmnShapesByHref,
        hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects,
      };
    }, [diagram.drdIndex, thisDmn.model.definitions]);

  const {
    nodes,
    edges,
    nodesById,
    edgesById,
    selectedNodeTypes,
    selectedNodesById,
    selectedEdgesById,
    drgElementsWithoutVisualRepresentationOnCurrentDrd,
  } = useMemo(() => {
    // console.time("nodes");

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

    function ackEdge({
      id,
      type,
      dmnObject,
      source,
      target,
    }: {
      id: string;
      dmnObject: DmnDiagramEdgeData["dmnObject"];
      type: EdgeType;
      source: string;
      target: string;
    }): RF.Edge<DmnDiagramEdgeData> {
      const data = {
        dmnObject,
        dmnEdge: id ? dmnEdgesByDmnElementRef.get(id) : undefined,
        dmnShapeSource: dmnShapesByHref.get(source),
        dmnShapeTarget: dmnShapesByHref.get(target),
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
      return edge;
    }

    // console.time("edges");

    const edges: RF.Edge<DmnDiagramEdgeData>[] = [
      // information requirements
      ...(thisDmn.model.definitions.drgElement ?? []).reduce<RF.Edge<DmnDiagramEdgeData>[]>((acc, dmnObject) => {
        if (dmnObject.__$$element === "decision") {
          acc.push(
            ...(dmnObject.informationRequirement ?? []).map((ir, index) =>
              ackEdge({
                id: ir["@_id"] ?? "",
                dmnObject: {
                  type: dmnObject.__$$element,
                  id: dmnObject["@_id"] ?? "",
                  requirementType: "informationRequirement",
                  index,
                },
                type: EDGE_TYPES.informationRequirement,
                source: (ir.requiredDecision ?? ir.requiredInput)!["@_href"],
                target: buildXmlHref({ id: dmnObject["@_id"]! }),
              })
            )
          );
        }
        // knowledge requirements
        if (dmnObject.__$$element === "decision" || dmnObject.__$$element === "businessKnowledgeModel") {
          acc.push(
            ...(dmnObject.knowledgeRequirement ?? []).map((kr, index) =>
              ackEdge({
                id: kr["@_id"] ?? "",
                dmnObject: {
                  type: dmnObject.__$$element,
                  id: dmnObject["@_id"] ?? "",
                  requirementType: "knowledgeRequirement",
                  index,
                },
                type: EDGE_TYPES.knowledgeRequirement,
                source: kr.requiredKnowledge["@_href"],
                target: buildXmlHref({ id: dmnObject["@_id"]! }),
              })
            )
          );
        }
        // authority requirements
        if (
          dmnObject.__$$element === "decision" ||
          dmnObject.__$$element === "businessKnowledgeModel" ||
          dmnObject.__$$element === "knowledgeSource"
        ) {
          acc.push(
            ...(dmnObject.authorityRequirement ?? []).map((ar, index) =>
              ackEdge({
                id: ar["@_id"] ?? "",
                dmnObject: {
                  type: dmnObject.__$$element,
                  id: dmnObject["@_id"] ?? "",
                  requirementType: "authorityRequirement",
                  index,
                },
                type: EDGE_TYPES.authorityRequirement,
                source: (ar.requiredInput ?? ar.requiredDecision ?? ar.requiredAuthority)!["@_href"],
                target: buildXmlHref({ id: dmnObject["@_id"]! }),
              })
            )
          );
        }
        return acc;
      }, []),
      // associations
      ...(thisDmn.model.definitions.artifact ?? []).flatMap((dmnObject, index) =>
        dmnObject.__$$element === "association"
          ? [
              ackEdge({
                id: dmnObject["@_id"] ?? "",
                dmnObject: {
                  type: dmnObject.__$$element,
                  id: dmnObject["@_id"] ?? "",
                  requirementType: "association",
                  index,
                },
                type: EDGE_TYPES.association,
                source: dmnObject.sourceRef?.["@_href"],
                target: dmnObject.targetRef?.["@_href"],
              }),
            ]
          : []
      ),
    ];

    // Selected edges go to the end of the array. This is necessary because z-index doesn't work on SVGs.
    const sortedEdges = edges.sort((a, b) => Number(selectedEdges.has(a.id)) - Number(selectedEdges.has(b.id)));

    // console.timeEnd("edges");

    function ackNode(dmnObjectQName: XmlQName, dmnObject: NodeDmnObjects, index: number) {
      const type = getNodeTypeFromDmnObject(dmnObject);
      if (!type) {
        return undefined;
      }

      // If the QName is composite, we try and get the namespace from the XML namespace declarations. If it's not found, we use `UNKNOWN_DMN_NAMESPACE`
      // If the QName is simple, we simply say that the namespace is undefined, which is the same as the default namespace.
      const dmnObjectNamespace = dmnObjectQName.prefix
        ? thisDmn.model.definitions[`@_xmlns:${dmnObjectQName.prefix}`] ?? KIE_DMN_UNKNOWN_NAMESPACE
        : undefined;

      const id = buildXmlHref({ namespace: dmnObjectNamespace, id: dmnObjectQName.localPart });

      const _shape = dmnShapesByHref.get(id);
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
        style: { ...snapShapeDimensions(diagram.snapGrid, shape, MIN_NODE_SIZES[type](diagram.snapGrid)) },
      };

      if (dmnObject?.__$$element === "decisionService") {
        const containedDecisions = [...(dmnObject.outputDecision ?? []), ...(dmnObject.encapsulatedDecision ?? [])];
        for (let i = 0; i < containedDecisions.length; i++) {
          parentIdsById.set(containedDecisions[i]["@_href"], data);
        }
        if (shape["@_isCollapsed"] || !!dmnObjectNamespace) {
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
    }

    const localNodes: RF.Node<DmnDiagramNodeData>[] = [
      ...(thisDmn.model.definitions.drgElement ?? []).flatMap((dmnObject, index) => {
        const newNode = ackNode({ type: "xml-qname", localPart: dmnObject["@_id"]! }, dmnObject, index);
        return newNode ? [newNode] : [];
      }),
      ...(thisDmn.model.definitions.artifact ?? []).flatMap((dmnObject, index) => {
        if (dmnObject.__$$element === "association") {
          return [];
        }

        const newNode = ackNode({ type: "xml-qname", localPart: dmnObject["@_id"]! }, dmnObject, index);
        return newNode ? [newNode] : [];
      }),
    ];

    // Assign parents & z-index to NODES
    for (let i = 0; i < localNodes.length; i++) {
      const parent = parentIdsById.get(localNodes[i].id);
      if (parent) {
        localNodes[i].data.parentRfNode = nodesById.get(
          buildXmlHref({ namespace: parent.dmnObjectNamespace, id: parent.dmnObjectQName.localPart })
        );
        localNodes[i].extent = undefined; // Allows the node to be dragged freely outside of parent's bounds.
        localNodes[i].zIndex = NODE_LAYERS.NESTED_NODES;
      }

      if (localNodes[i].type === NODE_TYPES.group) {
        localNodes[i].zIndex = NODE_LAYERS.GROUP_NODE;
      } else if (localNodes[i].type === NODE_TYPES.decisionService) {
        localNodes[i].zIndex = NODE_LAYERS.DECISION_SERVICE_NODE;
      }
    }

    const externalNodes = [...dmnShapesByHref.entries()].flatMap(([href, shape]) => {
      if (nodesById.get(href)) {
        return [];
      }

      if (!nodesById.get(href) && !hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects.has(href)) {
        // Unknown local node.
        console.warn(`DMN DIAGRAM: Found a shape that references a local DRG element that doesn't exist.`, shape);
        const newNode = ackNode(shape.dmnElementRefQName, null, -1);
        return newNode ? [newNode] : [];
      }

      const namespace = thisDmn.model.definitions[`@_xmlns:${shape.dmnElementRefQName.prefix}`];
      if (!namespace) {
        console.warn(
          `DMN DIAGRAM: Found a shape that references an external node with a namespace that is not declared at this DMN.`,
          shape
        );
        const newNode = ackNode(shape.dmnElementRefQName, null, -1);
        return newNode ? [newNode] : [];
      }

      const externalDmn = externalDmnsByNamespace.get(namespace);
      if (!externalDmn) {
        console.warn(
          `DMN DIAGRAM: Found a shape that references an external node from a namespace that is not provided on this DMN's external DMNs mapping.`,
          shape
        );
        const newNode = ackNode(shape.dmnElementRefQName, null, -1);
        return newNode ? [newNode] : [];
      }

      const externalDrgElementsById = (externalDmn.model.definitions.drgElement ?? []).reduce(
        (acc, e, index) => acc.set(e["@_id"]!, { element: e, index }),
        new Map<string, { index: number; element: Unpacked<DMN15__tDefinitions["drgElement"]> }>()
      );

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

    // console.timeEnd("nodes");

    if (diagram.overlays.enableNodeHierarchyHighlight) {
      assignClassesToHighlightedHierarchyNodes([...selectedNodesById.keys()], nodesById, edges);
    }

    return {
      nodes: sortedNodes,
      edges: sortedEdges,
      edgesById,
      nodesById,
      selectedNodeTypes,
      selectedNodesById,
      selectedEdgesById,
      drgElementsWithoutVisualRepresentationOnCurrentDrd,
    };
  }, [
    diagram._selectedNodes,
    diagram.draggingNodes,
    diagram.resizingNodes,
    diagram._selectedEdges,
    diagram.overlays.enableNodeHierarchyHighlight,
    diagram.snapGrid,
    thisDmn.model.definitions,
    hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects,
    dmnEdgesByDmnElementRef,
    dmnShapesByHref,
    externalDmnsByNamespace,
  ]);

  return {
    drgElementsWithoutVisualRepresentationOnCurrentDrd,
    dmnShapesByHref,
    dmnEdgesByDmnElementRef,
    nodesById,
    edgesById,
    nodes,
    edges,
    selectedNodeTypes,
    selectedNodesById,
    selectedEdgesById,
  };
}

export function assignClassesToHighlightedHierarchyNodes(
  selected: string[],
  nodes: Map<string, RF.Node>,
  edges: RF.Edge[]
) {
  const nodeVisitor: NodeVisitor = (nodeId, traversalDirection) => {
    const node = nodes.get(nodeId);
    if (node) {
      node.className = `hierarchy ${traversalDirection}`;
    }
  };

  const edgeVisitor: EdgeVisitor = (edge, traversalDirection) => {
    edge.className = `hierarchy ${traversalDirection}`;
  };

  const __selectedSet = new Set(selected);
  const __adjMatrix = getAdjMatrix(edges);

  traverse(__adjMatrix, __selectedSet, selected, "up", nodeVisitor, edgeVisitor);
  traverse(__adjMatrix, __selectedSet, selected, "down", nodeVisitor, edgeVisitor); // Traverse "down" after "up" because when there's a cycle, highlighting a node as a dependency is preferable.
}
