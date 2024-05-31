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
import ELK, * as Elk from "elkjs/lib/elk.bundled.js";
import { NodeType } from "../diagram/connections/graphStructure";
import { DrgEdge, getAdjMatrix, traverse } from "../diagram/graph/graph";
import { getContainmentRelationship } from "../diagram/maths/DmnMaths";
import { DEFAULT_NODE_SIZES, MIN_NODE_SIZES } from "../diagram/nodes/DefaultSizes";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { SnapGrid } from "../store/Store";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import { Edge, Node } from "reactflow";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";

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

export const FAKE_MARKER = "__$FAKE$__";

export async function getAutoLayoutedInfo({
  __readonly_snapGrid,
  __readonly_nodesById,
  __readonly_edgesById,
  __readonly_nodes,
  __readonly_drgEdges,
  __readonly_isAlternativeInputDataShape,
}: {
  __readonly_snapGrid: SnapGrid;
  __readonly_nodesById: Map<string, Node<DmnDiagramNodeData, string | undefined>>;
  __readonly_edgesById: Map<string, Edge<DmnDiagramEdgeData>>;
  __readonly_nodes: Node<DmnDiagramNodeData, string | undefined>[];
  __readonly_drgEdges: DrgEdge[];
  __readonly_isAlternativeInputDataShape: boolean;
}) {
  const parentNodesById = new Map<string, AutolayoutParentNode>();
  const nodeParentsById = new Map<string, Set<string>>();

  /**
    Used to tell ELK that dependencies of nodes' children should be considered the node's dependency too.
    This allows us to not rely on INCLUDE_STRATEGY hierarchy handling on ELK, keeping disjoint graph components separate, rendering side-by-side.
   */
  const fakeEdgesForElk = new Set<Elk.ElkExtendedEdge>();

  const adjMatrix = getAdjMatrix(__readonly_drgEdges);

  // 1. First we populate the `parentNodesById` map so that we know exactly what parent nodes we're dealing with. Decision Service nodes have two fake nodes to represent Output and Encapsulated sections.
  for (const node of __readonly_nodes) {
    const dependencies = new Set<string>();
    const dependents = new Set<string>();

    if (node.data?.dmnObject?.__$$element === "decisionService") {
      const outputs = new Set([...(node.data.dmnObject.outputDecision ?? []).map((s) => s["@_href"])]);
      const encapsulated = new Set([...(node.data.dmnObject.encapsulatedDecision ?? []).map((s) => s["@_href"])]);

      const idOfFakeNodeForOutputSection = `${node.id}${FAKE_MARKER}dsOutput`;
      const idOfFakeNodeForEncapsulatedSection = `${node.id}${FAKE_MARKER}dsEncapsulated`;

      const dsSize = MIN_NODE_SIZES[NODE_TYPES.decisionService]({ snapGrid: __readonly_snapGrid });
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
      const groupSize = DEFAULT_NODE_SIZES[NODE_TYPES.group]({ snapGrid: __readonly_snapGrid });
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
            snapGrid: __readonly_snapGrid,
            isAlternativeInputDataShape: __readonly_isAlternativeInputDataShape,
            containerMinSizes: MIN_NODE_SIZES[NODE_TYPES.group],
            boundsMinSizes: MIN_NODE_SIZES[__readonly_nodesById.get(id)?.type as NodeType],
          }).isInside,
          decisionServiceSection: "n/a",
        }),
        isDependencyOf: ({ id }) => dependents.has(id),
        hasDependencyTo: ({ id }) => dependencies.has(id),
      });
    }
  }

  // 2. Then we map all the nodes to elkNodes, including the parents. We mutate parents on the fly when iterating over the nodes list.
  const elkNodes = __readonly_nodes.flatMap((node) => {
    const parent = parentNodesById.get(node.id);
    if (parent) {
      return [];
    }

    const defaultSize = DEFAULT_NODE_SIZES[node.type as NodeType]({
      snapGrid: __readonly_snapGrid,
      isAlternativeInputDataShape: __readonly_isAlternativeInputDataShape,
    });
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

    const p = __readonly_nodesById.get(parentNode.elkNode.id);
    if (p?.type === NODE_TYPES.group && parentNode.elkNode.children?.length === 0) {
      continue; // Ignore empty group nodes.
    } else {
      elkNodes.push(parentNode.elkNode);
    }
  }

  // 4. After we have all containment and hierarchical relationships defined, we can add the fake edges so that ELK creates the structure correctly.
  for (const node of __readonly_nodes) {
    const parentNodes = [...parentNodesById.values()];

    const dependents = parentNodes.filter((p) => p.hasDependencyTo({ id: node.id }));
    for (const dependent of dependents) {
      // Not all nodes are present in all DRD
      if (__readonly_nodesById.has(node.id) && __readonly_nodesById.has(dependent.elkNode.id)) {
        fakeEdgesForElk.add({
          id: `${generateUuid()}${FAKE_MARKER}__fake`,
          sources: [node.id],
          targets: [dependent.elkNode.id],
        });
      }

      for (const p of nodeParentsById.get(node.id) ?? []) {
        // Not all nodes are present in all DRD
        if (__readonly_nodesById.has(p) && __readonly_nodesById.has(dependent.elkNode.id)) {
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
      if (__readonly_nodesById.has(node.id) && __readonly_nodesById.has(dependency.elkNode.id)) {
        fakeEdgesForElk.add({
          id: `${generateUuid()}${FAKE_MARKER}__fake`,
          sources: [dependency.elkNode.id],
          targets: [node.id],
        });
      }

      for (const p of nodeParentsById.get(node.id) ?? []) {
        // Not all nodes are present in all DRD
        if (__readonly_nodesById.has(p) && __readonly_nodesById.has(dependency.elkNode.id)) {
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
    ...[...__readonly_edgesById.values()].flatMap((e) => {
      // Not all nodes are present in all DRD
      if (__readonly_nodesById.has(e.source) && __readonly_nodesById.has(e.target)) {
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
  const autoLayoutedInfo = await runElk(elkNodes, elkEdges, ELK_OPTIONS);
  return {
    __readonly_autoLayoutedInfo: autoLayoutedInfo,
    __readonly_parentNodesById: parentNodesById,
  };
}

async function runElk(
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

export function visitNodeAndNested(
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
