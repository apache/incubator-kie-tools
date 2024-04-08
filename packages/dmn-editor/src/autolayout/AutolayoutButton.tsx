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

import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DC__Bounds } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import OptimizeIcon from "@patternfly/react-icons/dist/js/icons/optimize-icon";
import ELK, * as Elk from "elkjs/lib/elk.bundled.js";
import * as React from "react";
import { PositionalNodeHandleId } from "../diagram/connections/PositionalNodeHandles";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { getAdjMatrix, traverse } from "../diagram/graph/graph";
import { getContainmentRelationship } from "../diagram/maths/DmnMaths";
import { DEFAULT_NODE_SIZES, MIN_NODE_SIZES } from "../diagram/nodes/DefaultSizes";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { addEdge } from "../mutations/addEdge";
import { repositionNode } from "../mutations/repositionNode";
import { resizeNode } from "../mutations/resizeNode";
import { updateDecisionServiceDividerLine } from "../mutations/updateDecisionServiceDividerLine";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";

const elk = new ELK();

export const ELK_OPTIONS = {
  "elk.algorithm": "layered",
  "elk.direction": "UP",
  // By making width a lot bigger than height, we make sure disjoint graph components are placed horizontally, never vertically
  "elk.aspectRatio": "9999999999",
  // spacing
  "elk.spacing.nodeNode": "60",
  "elk.spacing.componentComponent": "200",
  "layered.spacing.edgeEdgeBetweenLayers": "0",
  "layered.spacing.edgeNodeBetweenLayers": "0",
  "layered.spacing.nodeNodeBetweenLayers": "100",
  // edges
  "elk.edgeRouting": "ORTHOGONAL",
  "elk.layered.mergeEdges": "true", // we need this to make sure space is consistent between layers.
  "elk.layered.mergeHierarchyEdges": "true",
  // positioning
  "elk.partitioning.activate": "true",
  "elk.nodePlacement.favorStraightEdges": "true",
  "elk.nodePlacement.bk.fixedAlignment": "LEFTDOWN",
  "elk.nodePlacement.bk.edgeStraightening": "IMPROVE_STRAIGHTNESS",
  //
  "layering.strategy": "LONGEST_PATH_SOURCE",
};

const PARENT_NODE_ELK_OPTIONS = {
  "elk.padding": "[left=60, top=60, right=80, bottom=60]",
  "elk.spacing.componentComponent": "60",
};

export interface AutolayoutParentNode {
  decisionServiceSection: "output" | "encapsulated" | "n/a";
  elkNode: Elk.ElkNode;
  contained: Set<string>;
  dependents: Set<string>;
  dependencies: Set<string>;
  contains: (otherNode: { id: string; bounds: DC__Bounds | undefined }) => {
    isInside: boolean;
    decisionServiceSection: AutolayoutParentNode["decisionServiceSection"];
  };
  hasDependencyTo: (otherNode: { id: string }) => boolean;
  isDependencyOf: (otherNode: { id: string }) => boolean;
}

const FAKE_MARKER = "__$FAKE$__";

export function AutolayoutButton() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();
  const isAlternativeInputDataShape = useDmnEditorStore((s) => s.computed(s).isAlternativeInputDataShape());

  const onApply = React.useCallback(async () => {
    const parentNodesById = new Map<string, AutolayoutParentNode>();
    const nodeParentsById = new Map<string, Set<string>>();

    /**
      Used to tell ELK that dependencies of nodes' children should be considered the node's dependency too.
      This allows us to not rely on INCLUDE_STRATEGY hierarchy handling on ELK, keeping disjoint graph components separate, rendering side-by-side.
     */
    const fakeEdgesForElk = new Set<Elk.ElkExtendedEdge>();

    const state = dmnEditorStoreApi.getState();

    const snapGrid = state.diagram.snapGrid;
    const nodesById = state.computed(state).getDiagramData(externalModelsByNamespace).nodesById;
    const edgesById = state.computed(state).getDiagramData(externalModelsByNamespace).edgesById;
    const nodes = state.computed(state).getDiagramData(externalModelsByNamespace).nodes;
    const edges = state.computed(state).getDiagramData(externalModelsByNamespace).edges;
    const drgEdges = state.computed(state).getDiagramData(externalModelsByNamespace).drgEdges;

    const adjMatrix = getAdjMatrix(drgEdges);

    // 1. First we populate the `parentNodesById` map so that we know exactly what parent nodes we're dealing with. Decision Service nodes have two fake nodes to represent Output and Encapsulated sections.
    for (const node of nodes) {
      const dependencies = new Set<string>();
      const dependents = new Set<string>();

      if (node.data?.dmnObject?.__$$element === "decisionService") {
        const outputs = new Set([...(node.data.dmnObject.outputDecision ?? []).map((s) => s["@_href"])]);
        const encapsulated = new Set([...(node.data.dmnObject.encapsulatedDecision ?? []).map((s) => s["@_href"])]);

        const idOfFakeNodeForOutputSection = `${node.id}${FAKE_MARKER}dsOutput`;
        const idOfFakeNodeForEncapsulatedSection = `${node.id}${FAKE_MARKER}dsEncapsulated`;

        const dsSize = MIN_NODE_SIZES[NODE_TYPES.decisionService]({ snapGrid });
        parentNodesById.set(node.id, {
          elkNode: {
            id: node.id,
            width: dsSize["@_width"],
            height: dsSize["@_height"],
            children: [
              {
                id: idOfFakeNodeForOutputSection,
                width: dsSize["@_width"],
                height: dsSize["@_height"] / 2,
                children: [],
                layoutOptions: {
                  ...ELK_OPTIONS,
                  ...PARENT_NODE_ELK_OPTIONS,
                },
              },
              {
                id: idOfFakeNodeForEncapsulatedSection,
                width: dsSize["@_width"],
                height: dsSize["@_height"] / 2,
                children: [],
                layoutOptions: {
                  ...ELK_OPTIONS,
                  ...PARENT_NODE_ELK_OPTIONS,
                },
              },
            ],
            layoutOptions: {
              "elk.algorithm": "layered",
              "elk.direction": "UP",
              "elk.aspectRatio": "9999999999",
              "elk.partitioning.activate": "true",
              "elk.spacing.nodeNode": "0",
              "elk.spacing.componentComponent": "0",
              "layered.spacing.edgeEdgeBetweenLayers": "0",
              "layered.spacing.edgeNodeBetweenLayers": "0",
              "layered.spacing.nodeNodeBetweenLayers": "0",
              "elk.padding": "[left=0, top=0, right=0, bottom=0]",
            },
          },
          decisionServiceSection: "output",
          dependencies,
          dependents,
          contained: outputs,
          contains: ({ id }) => ({
            isInside: outputs.has(id) || encapsulated.has(id),
            decisionServiceSection: outputs.has(id) ? "output" : encapsulated.has(id) ? "encapsulated" : "n/a",
          }),
          isDependencyOf: ({ id }) => dependents.has(id),
          hasDependencyTo: ({ id }) => dependencies.has(id),
        });

        fakeEdgesForElk.add({
          id: `${node.id}${FAKE_MARKER}fakeOutputEncapsulatedEdge`,
          sources: [idOfFakeNodeForEncapsulatedSection],
          targets: [idOfFakeNodeForOutputSection],
        });
      } else if (node.data?.dmnObject?.__$$element === "group") {
        const groupSize = DEFAULT_NODE_SIZES[NODE_TYPES.group]({ snapGrid });
        const groupBounds = node.data.shape["dc:Bounds"];
        parentNodesById.set(node.id, {
          decisionServiceSection: "n/a",
          elkNode: {
            id: node.id,
            width: groupBounds?.["@_width"] ?? groupSize["@_width"],
            height: groupBounds?.["@_height"] ?? groupSize["@_height"],
            children: [],
            layoutOptions: {
              ...ELK_OPTIONS,
              ...PARENT_NODE_ELK_OPTIONS,
            },
          },
          dependencies,
          dependents,
          contained: new Set(),
          contains: ({ id, bounds }) => ({
            isInside: getContainmentRelationship({
              bounds: bounds!,
              container: groupBounds!,
              snapGrid,
              isAlternativeInputDataShape,
              containerMinSizes: MIN_NODE_SIZES[NODE_TYPES.group],
              boundsMinSizes: MIN_NODE_SIZES[nodesById.get(id)?.type as NodeType],
            }).isInside,
            decisionServiceSection: "n/a",
          }),
          isDependencyOf: ({ id }) => dependents.has(id),
          hasDependencyTo: ({ id }) => dependencies.has(id),
        });
      }
    }

    // 2. Then we map all the nodes to elkNodes, including the parents. We mutate parents on the fly when iterating over the nodes list.
    const elkNodes = nodes.flatMap((node) => {
      const parent = parentNodesById.get(node.id);
      if (parent) {
        return [];
      }

      const defaultSize = DEFAULT_NODE_SIZES[node.type as NodeType]({ snapGrid, isAlternativeInputDataShape });
      const elkNode: Elk.ElkNode = {
        id: node.id,
        width: node.data.shape["dc:Bounds"]?.["@_width"] ?? defaultSize["@_width"],
        height: node.data.shape["dc:Bounds"]?.["@_height"] ?? defaultSize["@_height"],
        children: [],
        layoutOptions: {
          "partitioning.partition":
            // Since textAnnotations and knowledgeSources are not related to the logic, we leave them at the bottom.
            (node.type as NodeType) === NODE_TYPES.textAnnotation ||
            (node.type as NodeType) === NODE_TYPES.knowledgeSource
              ? "0"
              : "1",
        },
      };

      // FIXME: Tiago --> Improve performance here as part of https://github.com/apache/incubator-kie-issues/issues/451.
      const parents = [...parentNodesById.values()].filter(
        (p) => p.contains({ id: elkNode.id, bounds: node.data.shape["dc:Bounds"] }).isInside
      );
      if (parents.length > 0) {
        const decisionServiceSection = parents[0].contains({
          id: elkNode.id,
          bounds: node.data.shape["dc:Bounds"],
        }).decisionServiceSection;

        // The only relationship that ELK will know about is the first matching container for this node.
        if (decisionServiceSection === "n/a") {
          parents[0].elkNode.children?.push(elkNode);
        } else if (decisionServiceSection === "output") {
          parents[0].elkNode.children?.[0].children?.push(elkNode);
        } else if (decisionServiceSection === "encapsulated") {
          parents[0].elkNode.children?.[1].children?.push(elkNode);
        } else {
          throw new Error(`Unknown decisionServiceSection ${decisionServiceSection}`);
        }

        for (const p of parents) {
          p.contained?.add(elkNode.id); // We need to keep track of nodes that are contained by multiple groups, but ELK will only know about one of those containment relationships.
          nodeParentsById.set(node.id, new Set([...(nodeParentsById.get(node.id) ?? []), p.elkNode.id]));
        }
        return [];
      }

      return [elkNode];
    });

    // 3. After we have all containment relationships defined, we can proceed to resolving the hierarchical relationships.
    for (const [_, parentNode] of parentNodesById) {
      traverse(adjMatrix, parentNode.contained, [...parentNode.contained], "down", (n) => {
        parentNode.dependencies.add(n);
      });
      traverse(adjMatrix, parentNode.contained, [...parentNode.contained], "up", (n) => {
        parentNode.dependents.add(n);
      });

      const p = nodesById.get(parentNode.elkNode.id);
      if (p?.type === NODE_TYPES.group && parentNode.elkNode.children?.length === 0) {
        continue; // Ignore empty group nodes.
      } else {
        elkNodes.push(parentNode.elkNode);
      }
    }

    // 4. After we have all containment and hierarchical relationships defined, we can add the fake edges so that ELK creates the structure correctly.
    for (const node of nodes) {
      const parentNodes = [...parentNodesById.values()];

      const dependents = parentNodes.filter((p) => p.hasDependencyTo({ id: node.id }));
      for (const dependent of dependents) {
        // Not all nodes are present in all DRD
        if (nodesById.has(node.id) && nodesById.has(dependent.elkNode.id)) {
          fakeEdgesForElk.add({
            id: `${generateUuid()}${FAKE_MARKER}__fake`,
            sources: [node.id],
            targets: [dependent.elkNode.id],
          });
        }

        for (const p of nodeParentsById.get(node.id) ?? []) {
          // Not all nodes are present in all DRD
          if (nodesById.has(p) && nodesById.has(dependent.elkNode.id)) {
            fakeEdgesForElk.add({
              id: `${generateUuid()}${FAKE_MARKER}__fake`,
              sources: [p],
              targets: [dependent.elkNode.id],
            });
          }
        }
      }

      const dependencies = parentNodes.filter((p) => p.isDependencyOf({ id: node.id }));
      for (const dependency of dependencies) {
        // Not all nodes are present in all DRD
        if (nodesById.has(node.id) && nodesById.has(dependency.elkNode.id)) {
          fakeEdgesForElk.add({
            id: `${generateUuid()}${FAKE_MARKER}__fake`,
            sources: [dependency.elkNode.id],
            targets: [node.id],
          });
        }

        for (const p of nodeParentsById.get(node.id) ?? []) {
          // Not all nodes are present in all DRD
          if (nodesById.has(p) && nodesById.has(dependency.elkNode.id)) {
            fakeEdgesForElk.add({
              id: `${generateUuid()}${FAKE_MARKER}__fake`,
              sources: [dependency.elkNode.id],
              targets: [p],
            });
          }
        }
      }
    }

    // 5. Concatenate real and fake edges to pass to ELK.
    const elkEdges = [
      ...fakeEdgesForElk,
      ...[...edgesById.values()].flatMap((e) => {
        // Not all nodes are present in all DRD
        if (nodesById.has(e.source) && nodesById.has(e.target)) {
          return {
            id: e.id,
            sources: [e.source],
            targets: [e.target],
          };
        } else {
          return [];
        }
      }),
    ];

    // 6. Run ELK.
    const autolayouted = await runElk(elkNodes, elkEdges, ELK_OPTIONS);

    // 7. Update all nodes positions skipping empty groups, which will be positioned manually after all nodes are done being repositioned.
    dmnEditorStoreApi.setState((s) => {
      const autolayoutedElkNodesById = new Map<string, Elk.ElkNode>();

      for (const topLevelElkNode of autolayouted.nodes ?? []) {
        visitNodeAndNested(topLevelElkNode, { x: 100, y: 100 }, (elkNode, positionOffset) => {
          if (elkNode.id.includes(FAKE_MARKER)) {
            return;
          }

          autolayoutedElkNodesById.set(elkNode.id, elkNode);

          const nodeId = elkNode.id;
          const node = s.computed(s).getDiagramData(externalModelsByNamespace).nodesById.get(nodeId)!;

          repositionNode({
            definitions: s.dmn.model.definitions,
            drdIndex: s.diagram.drdIndex,
            controlWaypointsByEdge: new Map(),
            change: {
              nodeType: node.type as NodeType,
              type: "absolute",
              position: {
                x: elkNode.x! + positionOffset.x,
                y: elkNode.y! + positionOffset.y,
              },
              selectedEdges: [...edgesById.keys()],
              shapeIndex: node.data?.shape.index,
              sourceEdgeIndexes: edges.flatMap((e) =>
                e.source === nodeId && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
              ),
              targetEdgeIndexes: edges.flatMap((e) =>
                e.target === nodeId && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
              ),
            },
          });
        });
      }

      // 8. Resize all nodes using the sizes calculated by ELK.
      for (const topLevelElkNode of autolayouted.nodes ?? []) {
        visitNodeAndNested(topLevelElkNode, { x: 0, y: 0 }, (elkNode) => {
          if (elkNode.id.includes(FAKE_MARKER)) {
            return;
          }

          const nodeId = elkNode.id;
          const node = s.computed(s).getDiagramData(externalModelsByNamespace).nodesById.get(nodeId)!;

          resizeNode({
            definitions: s.dmn.model.definitions,
            drdIndex: s.diagram.drdIndex,
            dmnShapesByHref: s.computed(s).indexedDrd().dmnShapesByHref,
            snapGrid,
            change: {
              index: node.data.index,
              isExternal: !!node.data.dmnObjectQName.prefix,
              nodeType: node.type as NodeType,
              dimension: {
                "@_width": elkNode.width!,
                "@_height": elkNode.height!,
              },
              shapeIndex: node.data?.shape.index,
              sourceEdgeIndexes: edges.flatMap((e) =>
                e.source === nodeId && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
              ),
              targetEdgeIndexes: edges.flatMap((e) =>
                e.target === nodeId && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
              ),
            },
          });
        });
      }

      // 9. Updating Decision Service divider lines after all nodes are repositioned and resized.
      for (const [parentNodeId] of parentNodesById) {
        const parentNode = s.computed(s).getDiagramData(externalModelsByNamespace).nodesById.get(parentNodeId);
        if (parentNode?.type !== NODE_TYPES.decisionService) {
          continue;
        }

        const elkNode = autolayoutedElkNodesById.get(parentNodeId);
        if (!elkNode) {
          throw new Error(`Couldn't find Decision Service with id ${parentNode.id} at the autolayouted nodes map`);
        }

        /**
         * The second children of a Decision Service elkNode is a node representing the Encapsulated section.
         * It's Y position will be exactly where the divider line should be.
         */
        const dividerLinerLocalYPosition = elkNode.children?.[1]?.y;
        if (!dividerLinerLocalYPosition) {
          throw new Error(
            `Couldn't find second child (which represents the Encapuslated Decision section) of Decision Service with id ${parentNode.id} at the autolayouted nodes map`
          );
        }

        updateDecisionServiceDividerLine({
          definitions: s.dmn.model.definitions,
          drdIndex: s.diagram.drdIndex,
          dmnShapesByHref: s.computed(s).indexedDrd().dmnShapesByHref,
          drgElementIndex: parentNode.data.index,
          shapeIndex: parentNode.data.shape.index,
          snapGrid,
          localYPosition: dividerLinerLocalYPosition,
        });
      }

      // 10. Update the edges. Edges always go from top to bottom, removing waypoints.
      for (const elkEdge of autolayouted.edges ?? []) {
        if (elkEdge.id.includes(FAKE_MARKER)) {
          continue;
        }

        const edge = s.computed(s).getDiagramData(externalModelsByNamespace).edgesById.get(elkEdge.id)!;

        const sourceNode = s.computed(s).getDiagramData(externalModelsByNamespace).nodesById.get(elkEdge.sources[0])!;
        const targetNode = s.computed(s).getDiagramData(externalModelsByNamespace).nodesById.get(elkEdge.targets[0])!;

        // If the target is an external node, we don't have to create the edge.
        if (targetNode.data.dmnObjectQName.prefix) {
          continue;
        }

        addEdge({
          definitions: s.dmn.model.definitions,
          drdIndex: s.diagram.drdIndex,
          edge: {
            autoPositionedEdgeMarker: undefined,
            type: edge.type as EdgeType,
            targetHandle: PositionalNodeHandleId.Bottom,
            sourceHandle: PositionalNodeHandleId.Top,
          },
          sourceNode: {
            type: sourceNode.type as NodeType,
            href: sourceNode.id,
            data: sourceNode.data,
            bounds: sourceNode.data.shape["dc:Bounds"]!,
            shapeId: sourceNode.data.shape["@_id"],
          },
          targetNode: {
            type: targetNode.type as NodeType,
            href: targetNode.id,
            data: targetNode.data,
            bounds: targetNode.data.shape["dc:Bounds"]!,
            index: targetNode.data.index,
            shapeId: targetNode.data.shape["@_id"],
          },
          keepWaypoints: false,
        });
      }
    });
  }, [dmnEditorStoreApi, externalModelsByNamespace, isAlternativeInputDataShape]);

  return (
    <button className={"kie-dmn-editor--autolayout-panel-toggle-button"} onClick={onApply} title={"Autolayout (beta)"}>
      <OptimizeIcon />
    </button>
  );
}

//

export async function runElk(
  nodes: Elk.ElkNode[],
  edges: { id: string; sources: string[]; targets: string[] }[],
  options: Elk.LayoutOptions = {}
): Promise<{ isHorizontal: boolean; nodes: Elk.ElkNode[] | undefined; edges: Elk.ElkExtendedEdge[] | undefined }> {
  const isHorizontal = options?.["elk.direction"] === "RIGHT";

  const graph: Elk.ElkNode = {
    id: "root",
    layoutOptions: options,
    children: nodes,
    edges,
  };

  const layoutedGraph = await elk.layout(graph);
  return {
    isHorizontal,
    nodes: layoutedGraph.children,
    edges: layoutedGraph.edges as any[],
  };
}

function visitNodeAndNested(
  elkNode: Elk.ElkNode,
  positionOffset: { x: number; y: number },
  visitor: (elkNode: Elk.ElkNode, positionOffset: { x: number; y: number }) => void
) {
  visitor(elkNode, positionOffset);
  for (const nestedNode of elkNode.children ?? []) {
    visitNodeAndNested(
      nestedNode,
      {
        x: elkNode.x! + positionOffset.x,
        y: elkNode.y! + positionOffset.y,
      },
      visitor
    );
  }
}
