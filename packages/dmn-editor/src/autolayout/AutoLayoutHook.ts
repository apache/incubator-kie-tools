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

import * as Elk from "elkjs/lib/elk.bundled.js";
import { useCallback } from "react";
import { PositionalNodeHandleId } from "../diagram/connections/PositionalNodeHandles";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { addEdge } from "../mutations/addEdge";
import { repositionNode } from "../mutations/repositionNode";
import { resizeNode } from "../mutations/resizeNode";
import { updateDecisionServiceDividerLine } from "../mutations/updateDecisionServiceDividerLine";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { autolayout, FAKE_MARKER, visitNodeAndNested } from "./autolayout";

export function useAutoLayout() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();
  const isAlternativeInputDataShape = useDmnEditorStore((s) => s.computed(s).isAlternativeInputDataShape());

  return useCallback(async () => {
    const state = dmnEditorStoreApi.getState();
    const snapGrid = state.diagram.snapGrid;
    const nodesById = state.computed(state).getDiagramData(externalModelsByNamespace).nodesById;
    const edgesById = state.computed(state).getDiagramData(externalModelsByNamespace).edgesById;
    const nodes = state.computed(state).getDiagramData(externalModelsByNamespace).nodes;
    const edges = state.computed(state).getDiagramData(externalModelsByNamespace).edges;
    const drgEdges = state.computed(state).getDiagramData(externalModelsByNamespace).drgEdges;

    const { autolayouted, parentNodesById } = await autolayout({
      snapGrid,
      nodesById,
      edgesById,
      nodes,
      drgEdges,
      isAlternativeInputDataShape,
    });

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
            drdIndex: s.computed(s).getDrdIndex(),
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
            drdIndex: s.computed(s).getDrdIndex(),
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
          drdIndex: s.computed(s).getDrdIndex(),
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
          drdIndex: s.computed(s).getDrdIndex(),
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
}
