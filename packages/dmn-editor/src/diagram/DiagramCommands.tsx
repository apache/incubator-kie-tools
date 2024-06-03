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

import * as RF from "reactflow";
import * as React from "react";
import { useEffect } from "react";
import {
  DMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE,
  DmnEditorDiagramClipboard,
  buildClipboardFromDiagram,
  getClipboard,
} from "../clipboard/Clipboard";
import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";
import { NodeNature, nodeNatures } from "../mutations/NodeNature";
import { addOrGetDrd } from "../mutations/addOrGetDrd";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { EdgeDeletionMode, deleteEdge } from "../mutations/deleteEdge";
import { NodeDeletionMode, canRemoveNodeFromDrdOnly, deleteNode } from "../mutations/deleteNode";
import { repopulateInputDataAndDecisionsOnAllDecisionServices } from "../mutations/repopulateInputDataAndDecisionsOnDecisionService";
import { useDmnEditorStoreApi } from "../store/StoreContext";
import { DmnDiagramEdgeData } from "./edges/Edges";
import { CONTAINER_NODES_DESIRABLE_PADDING, getBounds } from "./maths/DmnMaths";
import { NODE_TYPES } from "./nodes/NodeTypes";
import { DmnDiagramNodeData } from "./nodes/Nodes";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { NodeType } from "./connections/graphStructure";
import { buildXmlHref, parseXmlHref } from "../xml/xmlHrefs";
import { DEFAULT_VIEWPORT } from "./Diagram";
import { useCommands } from "../commands/CommandsContextProvider";

export function DiagramCommands(props: {}) {
  const rfStoreApi = RF.useStoreApi();
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { commandsRef } = useCommands();
  const { externalModelsByNamespace } = useExternalModels();
  const rf = RF.useReactFlow<DmnDiagramNodeData, DmnDiagramEdgeData>();

  // Cancel action
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.cancelAction = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Canceling action...");
      rfStoreApi.setState((rfState) => {
        if (rfState.connectionNodeId) {
          rfState.cancelConnection();
          dmnEditorStoreApi.setState((state) => {
            state.diagram.ongoingConnection = undefined;
          });
        } else {
          (document.activeElement as any)?.blur?.();
        }

        return rfState;
      });
    };
  }, [dmnEditorStoreApi, commandsRef, rfStoreApi]);

  // Reset position to origin
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.resetPosition = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Reseting position...");
      rf.setViewport(DEFAULT_VIEWPORT, { duration: 200 });
    };
  }, [commandsRef, rf]);

  // Focus on selection
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.focusOnSelection = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Focusing on selected bounds...");
      const selectedNodes = rf.getNodes().filter((s) => s.selected);
      if (selectedNodes.length <= 0) {
        return;
      }

      const bounds = getBounds({
        nodes: selectedNodes,
        padding: 100,
      });

      rf.fitBounds(
        {
          x: bounds["@_x"],
          y: bounds["@_y"],
          width: bounds["@_width"],
          height: bounds["@_height"],
        },
        { duration: 200 }
      );
    };
  }, [commandsRef, rf]);

  // Cut nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.cut = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Cutting selected nodes...");
      const { clipboard, copiedEdgesById, danglingEdgesById, copiedNodesById } = buildClipboardFromDiagram(
        rfStoreApi.getState(),
        dmnEditorStoreApi.getState()
      );

      navigator.clipboard.writeText(JSON.stringify(clipboard)).then(() => {
        dmnEditorStoreApi.setState((state) => {
          // Delete edges
          [...copiedEdgesById.values(), ...danglingEdgesById.values()].forEach((edge) => {
            deleteEdge({
              definitions: state.dmn.model.definitions,
              drdIndex: state.computed(state).getDrdIndex(),
              edge: { id: edge.id, dmnObject: edge.data!.dmnObject },
              mode: EdgeDeletionMode.FROM_DRG_AND_ALL_DRDS,
            });
            state.dispatch(state).diagram.setEdgeStatus(edge.id, {
              selected: false,
              draggingWaypoint: false,
            });
          });

          // Delete nodes
          rfStoreApi
            .getState()
            .getNodes()
            .forEach((node: RF.Node<DmnDiagramNodeData>) => {
              if (copiedNodesById.has(node.id)) {
                deleteNode({
                  __readonly_drgEdges: state.computed(state).getDiagramData(externalModelsByNamespace).drgEdges,
                  definitions: state.dmn.model.definitions,
                  __readonly_drdIndex: state.computed(state).getDrdIndex(),
                  __readonly_dmnObjectNamespace:
                    node.data.dmnObjectNamespace ?? state.dmn.model.definitions["@_namespace"],
                  __readonly_dmnObjectQName: node.data.dmnObjectQName,
                  __readonly_dmnObjectId: node.data.dmnObject?.["@_id"],
                  __readonly_nodeNature: nodeNatures[node.type as NodeType],
                  mode: NodeDeletionMode.FROM_DRG_AND_ALL_DRDS,
                  __readonly_externalModelTypesByNamespace: state
                    .computed(state)
                    .getExternalModelTypesByNamespace(externalModelsByNamespace),
                });
                state.dispatch(state).diagram.setNodeStatus(node.id, {
                  selected: false,
                  dragging: false,
                  resizing: false,
                });
              }
            });
        });
      });
    };
  }, [dmnEditorStoreApi, externalModelsByNamespace, commandsRef, rfStoreApi]);

  // Copy nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.copy = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Copying selected nodes...");
      const { clipboard } = buildClipboardFromDiagram(rfStoreApi.getState(), dmnEditorStoreApi.getState());
      navigator.clipboard.writeText(JSON.stringify(clipboard));
    };
  }, [dmnEditorStoreApi, commandsRef, rfStoreApi]);

  // Paste nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.paste = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Pasting nodes...");
      navigator.clipboard.readText().then((text) => {
        const clipboard = getClipboard<DmnEditorDiagramClipboard>(text, DMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE);
        if (!clipboard) {
          return;
        }

        getNewDmnIdRandomizer()
          .ack({
            json: clipboard.drgElements,
            type: "DMN15__tDefinitions",
            attr: "drgElement",
          })
          .ack({
            json: clipboard.artifacts,
            type: "DMN15__tDefinitions",
            attr: "artifact",
          })
          .ack({
            json: clipboard.shapes,
            type: "DMNDI15__DMNDiagram",
            attr: "dmndi:DMNDiagramElement",
            __$$element: "dmndi:DMNShape",
          })
          .ack({
            json: clipboard.edges,
            type: "DMNDI15__DMNDiagram",
            attr: "dmndi:DMNDiagramElement",
            __$$element: "dmndi:DMNEdge",
          })
          .ack<any>({
            // This `any` argument ideally wouldn't be here, but the type of DMN's `meta` is not composed with KIE's `meta` in compile-time
            json: clipboard.widths,
            type: "KIE__tComponentsWidthsExtension",
            attr: "kie:ComponentWidths",
          })
          .randomize({ skipAlreadyAttributedIds: false });

        dmnEditorStoreApi.setState((state) => {
          state.dmn.model.definitions.drgElement ??= [];
          state.dmn.model.definitions.drgElement.push(...clipboard.drgElements);
          state.dmn.model.definitions.artifact ??= [];
          state.dmn.model.definitions.artifact.push(...clipboard.artifacts);

          const { diagramElements, widths } = addOrGetDrd({
            definitions: state.dmn.model.definitions,
            drdIndex: state.computed(state).getDrdIndex(),
          });
          diagramElements.push(...clipboard.shapes.map((s) => ({ ...s, __$$element: "dmndi:DMNShape" as const })));
          diagramElements.push(...clipboard.edges.map((s) => ({ ...s, __$$element: "dmndi:DMNEdge" as const })));

          widths.push(...clipboard.widths);

          repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions: state.dmn.model.definitions });

          state.diagram._selectedNodes = [...clipboard.drgElements, ...clipboard.artifacts].map((s) =>
            buildXmlHref({ id: s["@_id"]! })
          );

          if (state.diagram._selectedNodes.length === 1) {
            state.focus.consumableId = parseXmlHref(state.diagram._selectedNodes[0]).id;
          }
        });
      });
    };
  }, [dmnEditorStoreApi, commandsRef]);

  // Select/deselect all nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.selectAll = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Selecting/Deselecting nodes...");
      const allNodeIds = rfStoreApi
        .getState()
        .getNodes()
        .map((s) => s.id);

      const allEdgeIds = rfStoreApi.getState().edges.map((s) => s.id);

      dmnEditorStoreApi.setState((state) => {
        const allSelectedNodesSet = new Set(state.diagram._selectedNodes);
        const allSelectedEdgesSet = new Set(state.diagram._selectedEdges);

        // If everything is selected, deselect everything.
        if (
          allNodeIds.every((id) => allSelectedNodesSet.has(id) && allEdgeIds.every((id) => allSelectedEdgesSet.has(id)))
        ) {
          state.diagram._selectedNodes = [];
          state.diagram._selectedEdges = [];
        } else {
          state.diagram._selectedNodes = allNodeIds;
          state.diagram._selectedEdges = allEdgeIds;
        }
      });
    };
  }, [dmnEditorStoreApi, commandsRef, rfStoreApi]);

  // Create group wrapping selection
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.createGroup = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Grouping nodes...");
      const selectedNodes = rf.getNodes().filter((s) => s.selected);
      if (selectedNodes.length <= 0) {
        return;
      }

      dmnEditorStoreApi.setState((state) => {
        if (state.diagram._selectedNodes.length <= 0) {
          return;
        }

        const { href: newNodeId } = addStandaloneNode({
          definitions: state.dmn.model.definitions,
          drdIndex: state.computed(state).getDrdIndex(),
          newNode: {
            type: NODE_TYPES.group,
            bounds: getBounds({
              nodes: selectedNodes,
              padding: CONTAINER_NODES_DESIRABLE_PADDING,
            }),
          },
        });

        state.dispatch(state).diagram.setNodeStatus(newNodeId, { selected: true });
      });
    };
  }, [dmnEditorStoreApi, commandsRef, rf]);

  // Toggle hierarchy highlights
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.toggleHierarchyHighlight = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Toggle hierarchy highlights...");
      dmnEditorStoreApi.setState((state) => {
        state.diagram.overlays.enableNodeHierarchyHighlight = !state.diagram.overlays.enableNodeHierarchyHighlight;
      });
    };
  }, [dmnEditorStoreApi, commandsRef]);

  // Show Properties panel
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.togglePropertiesPanel = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Toggle properties panel...");
      dmnEditorStoreApi.setState((state) => {
        state.diagram.propertiesPanel.isOpen = !state.diagram.propertiesPanel.isOpen;
      });
    };
  }, [dmnEditorStoreApi, commandsRef]);

  // Hide from DRD
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.hideFromDrd = async () => {
      console.debug("DMN DIAGRAM: COMMANDS: Hide node from DRD...");
      const nodesById = rf
        .getNodes()
        .reduce((acc, s) => acc.set(s.id, s), new Map<string, RF.Node<DmnDiagramNodeData>>());

      dmnEditorStoreApi.setState((state) => {
        const selectedNodeIds = new Set(state.diagram._selectedNodes);
        for (const edge of rf.getEdges()) {
          if (
            (selectedNodeIds.has(edge.source) &&
              canRemoveNodeFromDrdOnly({
                __readonly_externalDmnsIndex: state
                  .computed(state)
                  .getExternalModelTypesByNamespace(externalModelsByNamespace).dmns,
                definitions: state.dmn.model.definitions,
                __readonly_drdIndex: state.computed(state).getDrdIndex(),
                __readonly_dmnObjectNamespace:
                  nodesById.get(edge.source)!.data.dmnObjectNamespace ?? state.dmn.model.definitions["@_namespace"],
                __readonly_dmnObjectId: nodesById.get(edge.source)!.data.dmnObject?.["@_id"],
              })) ||
            (selectedNodeIds.has(edge.target) &&
              canRemoveNodeFromDrdOnly({
                __readonly_externalDmnsIndex: state
                  .computed(state)
                  .getExternalModelTypesByNamespace(externalModelsByNamespace).dmns,
                definitions: state.dmn.model.definitions,
                __readonly_drdIndex: state.computed(state).getDrdIndex(),
                __readonly_dmnObjectNamespace:
                  nodesById.get(edge.target)!.data.dmnObjectNamespace ?? state.dmn.model.definitions["@_namespace"],
                __readonly_dmnObjectId: nodesById.get(edge.target)!.data.dmnObject?.["@_id"],
              }))
          ) {
            deleteEdge({
              definitions: state.dmn.model.definitions,
              drdIndex: state.computed(state).getDrdIndex(),
              edge: { id: edge.id, dmnObject: edge.data!.dmnObject },
              mode: EdgeDeletionMode.FROM_CURRENT_DRD_ONLY,
            });
            state.dispatch(state).diagram.setEdgeStatus(edge.id, { selected: false, draggingWaypoint: false });
          }
        }

        for (const node of rf.getNodes().filter((s) => s.selected)) {
          // Prevent hiding artifact nodes from DRD;
          if (nodeNatures[node.type as NodeType] === NodeNature.ARTIFACT) {
            continue;
          }
          const { deletedDmnShapeOnCurrentDrd: deletedShape } = deleteNode({
            definitions: state.dmn.model.definitions,
            __readonly_drgEdges: [], // Deleting from DRD only.
            __readonly_externalModelTypesByNamespace: state
              .computed(state)
              .getExternalModelTypesByNamespace(externalModelsByNamespace),
            __readonly_drdIndex: state.computed(state).getDrdIndex(),
            __readonly_dmnObjectNamespace: node.data.dmnObjectNamespace ?? state.dmn.model.definitions["@_namespace"],
            __readonly_dmnObjectQName: node.data.dmnObjectQName,
            __readonly_dmnObjectId: node.data.dmnObject?.["@_id"],
            __readonly_nodeNature: nodeNatures[node.type as NodeType],
            mode: NodeDeletionMode.FROM_CURRENT_DRD_ONLY,
          });

          if (deletedShape) {
            state.dispatch(state).diagram.setNodeStatus(node.id, {
              selected: false,
              dragging: false,
              resizing: false,
            });
          }
        }
      });
    };
  }, [dmnEditorStoreApi, externalModelsByNamespace, commandsRef, rf]);
  return <></>;
}
