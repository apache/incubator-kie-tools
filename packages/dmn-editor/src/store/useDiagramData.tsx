import {
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useCallback, useMemo } from "react";
import * as RF from "reactflow";
import { NODE_LAYERS, useDmnEditorStore } from "./Store";
import { snapShapeDimensions, snapShapePosition } from "../diagram/SnapGrid";
import { EdgeType } from "../diagram/connections/graphStructure";
import { EDGE_TYPES } from "../diagram/edges/EdgeTypes";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { DmnDiagramNodeData, DmnDiagramNodeDataExternalInfo } from "../diagram/nodes/Nodes";
import { getNodeTypeFromDmnObject } from "../diagram/maths/DmnMaths";
import { useDmnEditorDependencies } from "../includedModels/DmnEditorDependenciesContext";
import { XmlQName, parseXmlQName, buildXmlQName } from "../xml/qNames";
import { buildXmlHref, idFromHref } from "../xml/href";

export const diagramColors = {
  hierarchyUp: "#0083a4",
  hierarchyDown: "#003fa4",
  selected: "#006ba4",
};

export function useDiagramData() {
  const dmn = useDmnEditorStore((s) => s.dmn);
  const diagram = useDmnEditorStore((s) => s.diagram);

  const { dmnEdgesByDmnRefId, dmnShapesByDmnRefId, dmnShapesForExternalNodesByDmnRefId } = useMemo(
    () =>
      (dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [])
        .flatMap((diagram) => diagram["dmndi:DMNDiagramElement"] ?? [])
        .reduce(
          (acc, e, index) => {
            if (e.__$$element === "dmndi:DMNShape") {
              // @_dmnElementRef is a xsd:QName, meaning it can be namespaced.
              // If we find the namespace as a namespace declaration on the `definitions` object, then this shape represents a node from an included model.
              // Therefore, we need to add it to `dmnShapesForExternalNodesByDmnRefId`, so we can draw these nodes.
              // Do not skip adding it to the regular `dmnShapesByDmnRefId`, as nodes will query this.
              const dmnElementRefQName = parseXmlQName(e["@_dmnElementRef"]);
              if (dmnElementRefQName.prefix && dmn.model.definitions[`@_xmlns:${dmnElementRefQName.prefix}`]) {
                const href = buildXmlHref({
                  namespace: dmn.model.definitions[`@_xmlns:${dmnElementRefQName.prefix}`]!,
                  id: dmnElementRefQName.localPart,
                });
                acc.dmnShapesForExternalNodesByDmnRefId.set(e["@_dmnElementRef"], {
                  ...e,
                  index,
                  dmnElementRefQName: {
                    localPart: dmnElementRefQName.localPart,
                    prefix: dmnElementRefQName.prefix,
                  },
                  href,
                });

                acc.dmnShapesByDmnRefId.set(href, { ...e, index });
              } else {
                acc.dmnShapesByDmnRefId.set(e["@_dmnElementRef"], { ...e, index });
              }
            } else if (e.__$$element === "dmndi:DMNEdge") {
              acc.dmnEdgesByDmnRefId.set(e["@_dmnElementRef"], { ...e, index });
            }

            return acc;
          },
          {
            dmnEdgesByDmnRefId: new Map<string, DMNDI15__DMNEdge & { index: number }>(),
            dmnShapesByDmnRefId: new Map<string, DMNDI15__DMNShape & { index: number }>(),
            dmnShapesForExternalNodesByDmnRefId: new Map<
              string,
              DMNDI15__DMNShape & { index: number; dmnElementRefQName: XmlQName; href: string }
            >(),
          }
        ),
    [dmn.model.definitions]
  );

  const getEdgeData = useCallback(
    ({
      id,
      source,
      target,
      dmnObject,
    }: {
      dmnObject: DmnDiagramEdgeData["dmnObject"];
      id: string;
      source: string;
      target: string;
    }): DmnDiagramEdgeData => {
      return {
        dmnObject,
        dmnEdge: id ? dmnEdgesByDmnRefId.get(id) : undefined,
        dmnShapeSource: dmnShapesByDmnRefId.get(source),
        dmnShapeTarget: dmnShapesByDmnRefId.get(target),
      };
    },
    [dmnEdgesByDmnRefId, dmnShapesByDmnRefId]
  );

  const { dependenciesByNamespace } = useDmnEditorDependencies();

  const { nodes, edges, nodesById, edgesById } = useMemo(() => {
    // console.time("nodes");

    const nodesById = new Map<string, RF.Node<DmnDiagramNodeData<any>>>();
    const edgesById = new Map<string, RF.Edge<DmnDiagramEdgeData>>();
    const parentIdsById = new Map<
      string,
      Unpacked<DMN15__tDefinitions["drgElement"] | DMN15__tDefinitions["artifact"]>
    >();

    const { selectedNodes, draggingNodes, resizingNodes, selectedEdges } = {
      selectedNodes: new Set(diagram.selectedNodes),
      draggingNodes: new Set(diagram.draggingNodes),
      resizingNodes: new Set(diagram.resizingNodes),
      selectedEdges: new Set(diagram.selectedEdges),
    };

    function newEdge({
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
      const edge: RF.Edge<DmnDiagramEdgeData> = {
        data: getEdgeData({ id, source, target, dmnObject }),
        id,
        type,
        source,
        target,
        sourceHandle: type, // We have one source handle for each edge type. This is what makes the edge updaters work.
        selected: selectedEdges.has(id),
      };
      edgesById.set(edge.id, edge);
      return edge;
    }

    // console.time("edges");

    const edges: RF.Edge<DmnDiagramEdgeData>[] = [
      // information requirements
      ...(dmn.model.definitions.drgElement ?? []).reduce<RF.Edge<DmnDiagramEdgeData>[]>((acc, dmnObject) => {
        if (dmnObject.__$$element === "decision") {
          acc.push(
            ...(dmnObject.informationRequirement ?? []).map((ir) =>
              newEdge({
                id: ir["@_id"] ?? "",
                dmnObject: {
                  type: dmnObject.__$$element,
                  id: dmnObject["@_id"] ?? "",
                  requirementType: "informationRequirement",
                },
                type: EDGE_TYPES.informationRequirement,
                source: idFromHref((ir.requiredDecision ?? ir.requiredInput)?.["@_href"]),
                target: dmnObject["@_id"]!,
              })
            )
          );
        }
        // knowledge requirements
        if (dmnObject.__$$element === "decision" || dmnObject.__$$element === "businessKnowledgeModel") {
          acc.push(
            ...(dmnObject.knowledgeRequirement ?? []).map((kr) =>
              newEdge({
                id: kr["@_id"] ?? "",
                dmnObject: {
                  type: dmnObject.__$$element,
                  id: dmnObject["@_id"] ?? "",
                  requirementType: "knowledgeRequirement",
                },
                type: EDGE_TYPES.knowledgeRequirement,
                source: idFromHref(kr.requiredKnowledge?.["@_href"]),
                target: dmnObject["@_id"]!,
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
            ...(dmnObject.authorityRequirement ?? []).map((ar) =>
              newEdge({
                id: ar["@_id"] ?? "",
                dmnObject: {
                  type: dmnObject.__$$element,
                  id: dmnObject["@_id"] ?? "",
                  requirementType: "authorityRequirement",
                },
                type: EDGE_TYPES.authorityRequirement,
                source: idFromHref((ar.requiredInput ?? ar.requiredDecision ?? ar.requiredAuthority)?.["@_href"]),
                target: dmnObject["@_id"]!,
              })
            )
          );
        }
        return acc;
      }, []),
      // associations
      ...(dmn.model.definitions.artifact ?? []).flatMap((dmnObject) =>
        dmnObject.__$$element === "association"
          ? [
              newEdge({
                id: dmnObject["@_id"] ?? "",
                dmnObject: {
                  type: dmnObject.__$$element,
                  id: dmnObject["@_id"] ?? "",
                  requirementType: "association",
                },
                type: EDGE_TYPES.association,
                source: idFromHref(dmnObject.sourceRef?.["@_href"]),
                target: idFromHref(dmnObject.targetRef?.["@_href"]),
              }),
            ]
          : []
      ),
    ];

    // Selected edges go to the end of the array. This is necessary because z-index doesn't work on SVGs.
    const sortedEdges = edges.sort((a, b) => Number(selectedEdges.has(a.id)) - Number(selectedEdges.has(b.id)));

    // console.timeEnd("edges");

    function ackNode(
      dmnObject: Unpacked<DMN15__tDefinitions["drgElement"] | DMN15__tDefinitions["artifact"]>,
      index: number,
      externalInfo: DmnDiagramNodeDataExternalInfo
    ) {
      const type = getNodeTypeFromDmnObject(dmnObject);
      if (!type) {
        return undefined;
      }

      const { id, shape } = externalInfo.isExternal
        ? { id: externalInfo.href, shape: dmnShapesForExternalNodesByDmnRefId.get(buildXmlQName(externalInfo.qName))! }
        : { id: dmnObject["@_id"]!, shape: dmnShapesByDmnRefId.get(dmnObject["@_id"]!)! };

      const newNode: RF.Node<DmnDiagramNodeData<any>> = {
        id,
        type,
        selected: selectedNodes.has(id),
        dragging: draggingNodes.has(id),
        resizing: resizingNodes.has(id),
        position: snapShapePosition(diagram.snapGrid, shape),
        data: {
          ...externalInfo,
          dmnObject,
          shape,
          index,
          parentRfNode: undefined,
        },
        zIndex: NODE_LAYERS.NODES,
        style: { ...snapShapeDimensions(diagram.snapGrid, shape) },
      };

      if (dmnObject.__$$element === "decisionService") {
        const containedDecisions = [...(dmnObject.outputDecision ?? []), ...(dmnObject.encapsulatedDecision ?? [])];
        for (let i = 0; i < containedDecisions.length; i++) {
          parentIdsById.set(idFromHref(containedDecisions[i]["@_href"]), dmnObject);
        }
      }

      nodesById.set(newNode.id, newNode);
      return newNode;
    }

    const localNodes: RF.Node<DmnDiagramNodeData<any>>[] = [
      ...(dmn.model.definitions.drgElement ?? []).flatMap((dmnObject, index) => {
        const newNode = ackNode(dmnObject, index, { isExternal: false });
        return newNode ? [newNode] : [];
      }),
      ...(dmn.model.definitions.artifact ?? []).flatMap((dmnObject, index) => {
        const newNode = ackNode(dmnObject, index, { isExternal: false });
        return newNode ? [newNode] : [];
      }),
    ];

    if (diagram.overlays.enableNodeHierarchyHighlight) {
      assignClassesToHighlightedHierarchyNodes([...selectedNodes], nodesById, edges);
    }

    // Assign parents & z-index to NODES
    for (let i = 0; i < localNodes.length; i++) {
      const parent = parentIdsById.get(localNodes[i].id);
      if (parent) {
        localNodes[i].data.parentRfNode = nodesById.get(parent["@_id"]!);
        localNodes[i].extent = undefined; // Allows the node to be dragged freely outside of parent's bounds.
        localNodes[i].zIndex = NODE_LAYERS.NESTED_NODES;

        // ⬇ This code is if we want to use Reactflow's parenting mechanism.
        //
        // nodes[i].parentNode = parent["@_id"]!;
        // We need to "recalculate" the node position here from scratch, as to avoid double-snapping.
        // const parentShape = dmnShapesByDmnRefId.get(parent["@_id"]!)!;

        // nodes[i].position = snapShapePosition(
        //   diagram.snapGrid,
        //   offsetShapePosition(nodes[i].data.shape, {
        //     x: -(parentShape["dc:Bounds"]?.["@_x"] ?? 0),
        //     y: -(parentShape["dc:Bounds"]?.["@_y"] ?? 0),
        //   })
        // );
      }

      if (localNodes[i].type === NODE_TYPES.group) {
        localNodes[i].zIndex = NODE_LAYERS.GROUP_NODE;
      }

      if (localNodes[i].type === NODE_TYPES.decisionService) {
        localNodes[i].zIndex = NODE_LAYERS.DECISION_SERVICE_NODE;
      }
    }

    const externalNodes = [...dmnShapesForExternalNodesByDmnRefId.values()].flatMap((shape) => {
      const namespace = dmn.model.definitions[`@_xmlns:${shape.dmnElementRefQName.prefix}`];
      if (!namespace) {
        throw new Error(`Can't find namespace for alias '${shape.dmnElementRefQName.prefix}'.`);
      }

      const externalDrgElements = dependenciesByNamespace[namespace]?.model.definitions.drgElement ?? [];
      const index = externalDrgElements.findIndex((s) => s["@_id"] === shape.dmnElementRefQName.localPart); // FIXME: Tiago --> O(n) for each external node.. Not good.
      if (index < 0) {
        throw new Error("Can't find drgElement for shape with dmnElementRef " + shape["@_dmnElementRef"]);
      }

      const newNode = ackNode(externalDrgElements[index], index, {
        isExternal: true,
        qName: shape.dmnElementRefQName,
        href: shape.href,
      });
      return newNode ? [newNode] : [];
    });

    // Groups are always at the back. Decision Services after groups, then everything else.
    const sortedNodes = [...localNodes, ...externalNodes]
      .sort((a, b) => Number(b.type === NODE_TYPES.decisionService) - Number(a.type === NODE_TYPES.decisionService))
      .sort((a, b) => Number(b.type === NODE_TYPES.group) - Number(a.type === NODE_TYPES.group));

    // console.timeEnd("nodes");

    return {
      nodes: sortedNodes,
      edges: sortedEdges,
      edgesById,
      nodesById,
    };
  }, [
    diagram.selectedNodes,
    diagram.draggingNodes,
    diagram.resizingNodes,
    diagram.selectedEdges,
    diagram.overlays.enableNodeHierarchyHighlight,
    diagram.snapGrid,
    dmnShapesForExternalNodesByDmnRefId,
    getEdgeData,
    dmnShapesByDmnRefId,
    dmn.model.definitions,
    dependenciesByNamespace,
  ]);

  return { dmnShapesByDmnRefId, dmnEdgesByDmnRefId, nodesById, edgesById, nodes, edges };
}

export function assignClassesToHighlightedHierarchyNodes(
  selected: string[],
  nodes: Map<string, RF.Node>,
  edges: RF.Edge[]
) {
  function traverse(curNodeIds: string[], traversalDirection: HierarchyDirection, visited = new Set<string>()) {
    if (curNodeIds.length <= 0) {
      return;
    }

    const nextNodeIds = curNodeIds.flatMap((curNodeId) => {
      if (visited.has(curNodeId)) {
        return [];
      }

      // Only paint nodes if they're not selected.
      if (!__selectedSet.has(curNodeId)) {
        nodes.get(curNodeId)!.className = `hierarchy ${traversalDirection}`;
      }

      const curNodeAdjs = __adjMatrix[curNodeId] ?? {};
      return Object.keys(curNodeAdjs).flatMap((adjNodeId) => {
        const { edge, direction: edgeDirection } = curNodeAdjs[adjNodeId]!;
        if (traversalDirection !== edgeDirection) {
          return [];
        }

        visited.add(curNodeId);
        // Only paint edges if at least one of the endpoints is not selected.
        if (!(__selectedSet.has(edge.source) && __selectedSet.has(edge.target))) {
          edge.className = `hierarchy ${traversalDirection}`;
        }

        return [adjNodeId];
      });
    });

    traverse(nextNodeIds, traversalDirection, visited);
  }

  const __selectedSet = new Set(selected);
  const __adjMatrix: AdjMatrix = {};

  for (const e of edges) {
    __adjMatrix[e.source] ??= {};
    __adjMatrix[e.target] ??= {};
    __adjMatrix[e.source]![e.target] = { direction: "up", edge: e };
    __adjMatrix[e.target]![e.source] = { direction: "down", edge: e };
  }

  traverse(selected, "up");
  traverse(selected, "down"); // Traverse "down" after "up" because when there's a cycle, highlighting a node as a dependency is preferable.
}

export type AdjMatrix = Record<
  string,
  undefined | Record<string, undefined | { direction: HierarchyDirection; edge: RF.Edge }>
>;

export type HierarchyDirection = "up" | "down";

export type Unpacked<T> = T extends Array<infer U> ? U : never;
