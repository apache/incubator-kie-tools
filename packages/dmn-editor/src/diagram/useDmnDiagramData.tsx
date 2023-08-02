import {
  DMN14__tDefinitions,
  DMNDI13__DMNEdge,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { useCallback, useMemo } from "react";
import * as RF from "reactflow";
import { NODE_LAYERS, useDmnEditorStore } from "../store/Store";
import { switchExpression } from "../switchExpression/switchExpression";
import { offsetShapePosition, snapShapeDimensions, snapShapePosition } from "./SnapGrid";
import { EdgeType } from "./connections/graphStructure";
import { EDGE_TYPES } from "./edges/EdgeTypes";
import { DmnEditorDiagramEdgeData } from "./edges/Edges";
import { NODE_TYPES } from "./nodes/NodeTypes";
import { DmnEditorDiagramNodeData } from "./nodes/Nodes";

export function useDmnDiagramData() {
  const { diagram, dmn } = useDmnEditorStore();

  const { edgesById, shapesById } = useMemo(
    () =>
      (dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [])
        .flatMap((diagram) => diagram["dmndi:DMNDiagramElement"] ?? [])
        .reduce(
          (acc, e, index) => {
            if (e.__$$element === "dmndi:DMNShape") {
              acc.shapesById.set(e["@_dmnElementRef"], { ...e, index });
            } else if (e.__$$element === "dmndi:DMNEdge") {
              acc.edgesById.set(e["@_dmnElementRef"], { ...e, index });
            }

            return acc;
          },
          {
            edgesById: new Map<string, DMNDI13__DMNEdge & { index: number }>(),
            shapesById: new Map<string, DMNDI13__DMNShape & { index: number }>(),
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
      dmnObject: DmnEditorDiagramEdgeData["dmnObject"];
      id: string;
      source: string;
      target: string;
    }): DmnEditorDiagramEdgeData => {
      return {
        dmnObject,
        dmnEdge: id ? edgesById.get(id) : undefined,
        dmnShapeSource: shapesById.get(source),
        dmnShapeTarget: shapesById.get(target),
      };
    },
    [edgesById, shapesById]
  );

  const { nodes, edges, nodesById } = useMemo(() => {
    // console.time("nodes");

    const nodesById = new Map<string, RF.Node<DmnEditorDiagramNodeData<any>>>();
    const parentIdsById = new Map<
      string,
      Unpacked<DMN14__tDefinitions["drgElement"] | DMN14__tDefinitions["artifact"]>
    >();

    const { selected, dragging, resizing } = {
      selected: new Set(diagram.selected),
      dragging: new Set(diagram.dragging),
      resizing: new Set(diagram.resizing),
    };

    function ackNode(
      dmnObject: Unpacked<DMN14__tDefinitions["drgElement"] | DMN14__tDefinitions["artifact"]>,
      index: number
    ) {
      const type = switchExpression(dmnObject.__$$element, {
        inputData: NODE_TYPES.inputData,
        decision: NODE_TYPES.decision,
        businessKnowledgeModel: NODE_TYPES.bkm,
        knowledgeSource: NODE_TYPES.knowledgeSource,
        decisionService: NODE_TYPES.decisionService,
        association: undefined,
        group: NODE_TYPES.group,
        textAnnotation: NODE_TYPES.textAnnotation,
      });

      if (!type) {
        return undefined;
      }

      const id = dmnObject["@_id"]!;
      const shape = shapesById.get(id)!;
      const newNode = {
        id,
        type,
        selected: selected.has(id),
        dragging: dragging.has(id),
        resizing: resizing.has(id),
        position: snapShapePosition(shape),
        data: { dmnObject, shape, index },
        zIndex: NODE_LAYERS.NODES,
        style: { ...snapShapeDimensions(shape) },
      };

      if (dmnObject.__$$element === "decisionService") {
        const containedDecisions = [...(dmnObject.outputDecision ?? []), ...(dmnObject.encapsulatedDecision ?? [])];
        for (let i = 0; i < containedDecisions.length; i++) {
          parentIdsById.set(idFromHref(containedDecisions[i]["@_href"]), dmnObject);
          newNode.zIndex = NODE_LAYERS.PARENT_NODES;
        }
      }

      if (dmnObject.__$$element === "group") {
        // FIXME: Tiago --> Need to find which nodes are encapsulated by a group.
      }

      nodesById.set(newNode.id, newNode);
      return newNode;
    }

    function newEdge({
      id,
      type,
      dmnObject,
      source,
      target,
    }: {
      id: string;
      dmnObject: DmnEditorDiagramEdgeData["dmnObject"];
      type: EdgeType;
      source: string;
      target: string;
    }) {
      return {
        data: getEdgeData({ id, source, target, dmnObject }),
        id,
        type,
        source,
        target,
      };
    }

    const nodes: RF.Node<DmnEditorDiagramNodeData<any>>[] = [
      ...(dmn.model.definitions.drgElement ?? []).flatMap((dmnObject, index) => {
        const newNode = ackNode(dmnObject, index);
        return newNode ? [newNode] : [];
      }),
      ...(dmn.model.definitions.artifact ?? []).flatMap((dmnObject, index) => {
        const newNode = ackNode(dmnObject, index);
        return newNode ? [newNode] : [];
      }),
    ];

    // Assign parents
    for (let i = 0; i < nodes.length; i++) {
      const parent = parentIdsById.get(nodes[i].id);
      if (parent) {
        nodes[i].parentNode = parent["@_id"]!;
        nodes[i].extent = "parent"; // FIXME: Tiago make these nodes deattach from parent when dragged outside. And vice-versa.
        nodes[i].zIndex = NODE_LAYERS.NESTED_NODES;

        // We need to "recalculate" the node position here from scratch, as to avoid double-snapping.
        const parentShape = shapesById.get(parent["@_id"]!)!;

        nodes[i].position = snapShapePosition(
          offsetShapePosition(nodes[i].data.shape, {
            x: -(parentShape["dc:Bounds"]?.["@_x"] ?? 0),
            y: -(parentShape["dc:Bounds"]?.["@_y"] ?? 0),
          })
        );
      }
    }

    // console.timeEnd("nodes");
    // console.time("edges");

    const edges: RF.Edge<DmnEditorDiagramEdgeData>[] = [
      // information requirements
      ...(dmn.model.definitions.drgElement ?? []).reduce<RF.Edge<DmnEditorDiagramEdgeData>[]>((acc, dmnObject) => {
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

    // console.timeEnd("edges");

    return {
      nodes,
      edges,
      nodesById,
    };
  }, [
    diagram.dragging,
    diagram.resizing,
    diagram.selected,
    getEdgeData,
    dmn.model.definitions.artifact,
    dmn.model.definitions.drgElement,
    shapesById,
  ]);

  return { shapesById, edgesById, nodesById, nodes, edges };
}

export function idFromHref(href: string | undefined) {
  return href?.substring(1) ?? "";
}

export type Unpacked<T> = T extends Array<infer U> ? U : never;
