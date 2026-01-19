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
  SWF_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE,
  SwfEditorDiagramClipboard,
  buildClipboardFromDiagram,
  getClipboard,
} from "../clipboard/Clipboard";
import { nodeNatures } from "../mutations/NodeNature";

import { deleteNode } from "../mutations/deleteNode";
import { useSwfEditorStoreApi } from "../store/StoreContext";
import { SwfDiagramEdgeData } from "./edges/SwfEdges";
import { getBounds } from "./maths/SwfMaths";
import { SwfDiagramNodeData } from "./nodes/SwfNodes";
import { NodeType } from "./connections/graphStructure";
import { DEFAULT_VIEWPORT } from "./Diagram";
import { useCommands } from "../commands/CommandsContextProvider";
import { Specification } from "@serverlessworkflow/sdk-typescript";

export function DiagramCommands(props: {}) {
  const rfStoreApi = RF.useStoreApi();
  const swfEditorStoreApi = useSwfEditorStoreApi();
  const { commandsRef } = useCommands();
  const rf = RF.useReactFlow<SwfDiagramNodeData, SwfDiagramEdgeData>();

  // Cancel action
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.cancelAction = async () => {
      console.debug("SWF DIAGRAM: COMMANDS: Canceling action...");
      rfStoreApi.setState((rfState) => {
        if (rfState.connectionNodeId) {
          rfState.cancelConnection();
          swfEditorStoreApi.setState((state) => {
            state.diagram.ongoingConnection = undefined;
          });
        } else {
          (document.activeElement as any)?.blur?.();
        }

        return rfState;
      });
    };
  }, [swfEditorStoreApi, commandsRef, rfStoreApi]);

  // Reset position to origin
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.resetPosition = async () => {
      console.debug("SWF DIAGRAM: COMMANDS: Reseting position...");
      rf.setViewport(DEFAULT_VIEWPORT, { duration: 200 });
    };
  }, [commandsRef, rf]);

  // Focus on selection
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.focusOnSelection = async () => {
      console.debug("SWF DIAGRAM: COMMANDS: Focusing on selected bounds...");
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
          x: bounds["x"],
          y: bounds["y"],
          width: bounds["width"],
          height: bounds["height"],
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
      console.debug("SWF DIAGRAM: COMMANDS: Cutting selected nodes...");
      const { clipboard, copiedNodesById } = buildClipboardFromDiagram(
        rfStoreApi.getState(),
        swfEditorStoreApi.getState()
      );

      navigator.clipboard.writeText(JSON.stringify(clipboard)).then(() => {
        swfEditorStoreApi.setState((state) => {
          // Delete nodes
          rfStoreApi
            .getState()
            .getNodes()
            .forEach((node: RF.Node<SwfDiagramNodeData>) => {
              if (copiedNodesById.has(node.id)) {
                deleteNode({
                  __readonly_swfEdges: state.computed(state).getDiagramData().swfEdges,
                  definitions: state.swf.model,
                  __readonly_swfObjectId: node.data.swfObject?.["id"],
                  __readonly_nodeNature: nodeNatures[node.type as NodeType],
                });
                state.dispatch(state).diagram.setNodeStatus(node.id, {
                  selected: false,
                  dragging: false,
                });
              }
            });
        });
      });
    };
  }, [swfEditorStoreApi, commandsRef, rfStoreApi]);

  // Copy nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.copy = async () => {
      console.debug("SWF DIAGRAM: COMMANDS: Copying selected nodes...");
      const { clipboard } = buildClipboardFromDiagram(rfStoreApi.getState(), swfEditorStoreApi.getState());
      navigator.clipboard.writeText(JSON.stringify(clipboard));
    };
  }, [swfEditorStoreApi, commandsRef, rfStoreApi]);

  // Paste nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.paste = async () => {
      console.debug("SWF DIAGRAM: COMMANDS: Pasting nodes...");
      navigator.clipboard.readText().then((text) => {
        const clipboard = getClipboard<SwfEditorDiagramClipboard>(text, SWF_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE);
        if (!clipboard) {
          return;
        }

        swfEditorStoreApi.setState((state) => {
          state.swf.model.states ??= [] as unknown as Specification.States;
          // FIXME: when copying and pasting we will create possible id / name conflicts that must be handled before pasting
          state.swf.model.states.push(...clipboard.swfElements);

          state.diagram._selectedNodes = [...clipboard.swfElements].map((s) => s.id!);

          if (state.diagram._selectedNodes.length === 1) {
            state.focus.consumableId = state.diagram._selectedNodes[0];
          }
        });
      });
    };
  }, [swfEditorStoreApi, commandsRef]);

  // Select/deselect all nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.selectAll = async () => {
      console.debug("SWF DIAGRAM: COMMANDS: Selecting/Deselecting nodes...");
      const allNodeIds = rfStoreApi
        .getState()
        .getNodes()
        .map((s) => s.id);

      const allEdgeIds = rfStoreApi.getState().edges.map((s) => s.id);

      swfEditorStoreApi.setState((state) => {
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
  }, [swfEditorStoreApi, commandsRef, rfStoreApi]);

  // Toggle hierarchy highlights
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.toggleHierarchyHighlight = async () => {
      console.debug("SWF DIAGRAM: COMMANDS: Toggle hierarchy highlights...");
      swfEditorStoreApi.setState((state) => {
        state.diagram.overlays.enableNodeHierarchyHighlight = !state.diagram.overlays.enableNodeHierarchyHighlight;
      });
    };
  }, [swfEditorStoreApi, commandsRef]);

  return <></>;
}
