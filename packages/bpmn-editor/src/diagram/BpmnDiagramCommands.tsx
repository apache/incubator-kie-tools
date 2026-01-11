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

import * as React from "react";
import { useEffect } from "react";
import * as RF from "reactflow";
import {
  BPMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE,
  BpmnEditorDiagramClipboard,
  buildClipboardFromDiagram,
  getClipboard,
} from "../clipboard/Clipboard";
import { useCommands } from "../commands/CommandsContextProvider";
import { useBpmnEditorStoreApi } from "../store/StoreContext";
import { DEFAULT_VIEWPORT } from "@kie-tools/xyflow-react-kie-diagram/dist/diagram/XyFlowReactKieDiagram";
import { BpmnDiagramEdgeData, BpmnNodeType, DEFAULT_NODE_SIZES, NODE_TYPES } from "./BpmnDiagramDomain";
import { CONTAINER_NODES_DESIRABLE_PADDING, getBounds } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/DcMaths";
import { BpmnDiagramNodeData } from "./BpmnDiagramDomain";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { addConnectedNode } from "../mutations/addConnectedNode";
import { getNewBpmnIdRandomizer } from "../idRandomizer/bpmnIdRandomizer";
import { addOrGetProcessAndDiagramElements } from "../mutations/addOrGetProcessAndDiagramElements";
import { deleteEdge } from "../mutations/deleteEdge";
import { deleteNode } from "../mutations/deleteNode";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";

export function BpmnDiagramCommands(props: {}) {
  const xyFlowStoreApi = RF.useStoreApi();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const { commandsRef } = useCommands();
  const xyFlow = RF.useReactFlow<BpmnDiagramNodeData, BpmnDiagramEdgeData>();

  // Cancel action
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.cancelAction = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Canceling action...");
      xyFlowStoreApi.setState((xyFlowState) => {
        if (xyFlowState.connectionNodeId) {
          xyFlowState.cancelConnection();
          bpmnEditorStoreApi.setState((state) => {
            state.xyFlowReactKieDiagram.ongoingConnection = undefined;
          });
        } else {
          (document.activeElement as any)?.blur?.();
        }

        return xyFlowState;
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlowStoreApi]);

  // Reset position to origin
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.resetPosition = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Reseting position...");
      xyFlow.setViewport(DEFAULT_VIEWPORT, { duration: 200 });
    };
  }, [commandsRef, xyFlow]);

  // Focus on selection
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.focusOnSelection = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Focusing on selected bounds...");
      const selectedNodes = xyFlow.getNodes().filter((s) => s.selected);
      if (selectedNodes.length <= 0) {
        return;
      }

      const bounds = getBounds({
        nodes: selectedNodes,
        padding: 100,
      });

      xyFlow.fitBounds(
        {
          x: bounds["@_x"],
          y: bounds["@_y"],
          width: bounds["@_width"],
          height: bounds["@_height"],
        },
        { duration: 200 }
      );
    };
  }, [commandsRef, xyFlow]);

  // Cut nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.cut = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Cutting selected nodes...");
      const { clipboard, copiedEdgesById, danglingEdgesById, copiedNodesById } = buildClipboardFromDiagram(
        xyFlowStoreApi.getState(),
        bpmnEditorStoreApi.getState()
      );

      navigator.clipboard.writeText(JSON.stringify(clipboard)).then(() => {
        bpmnEditorStoreApi.setState((state) => {
          [...copiedEdgesById.values(), ...danglingEdgesById.values()].forEach((edge) => {
            deleteEdge({
              definitions: state.bpmn.model.definitions,
              __readonly_edgeId: edge.id,
            });
            state.dispatch(state).setEdgeStatus(edge.id, {
              selected: false,
              draggingWaypoint: false,
            });
          });

          // Delete nodes
          xyFlowStoreApi
            .getState()
            .getNodes()
            .forEach((node: RF.Node<BpmnDiagramNodeData>) => {
              if (copiedNodesById.has(node.id)) {
                deleteNode({
                  definitions: state.bpmn.model.definitions,
                  __readonly_bpmnElementId: node.data.bpmnElement?.["@_id"],
                  __readonly_bpmnEdgeData: state
                    .computed(state)
                    .getDiagramData()
                    .edges.flatMap((edge) => edge.data!),
                });
                state.dispatch(state).setNodeStatus(node.id, {
                  selected: false,
                  dragging: false,
                  resizing: false,
                });
              }
            });
        });
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlowStoreApi]);

  // Copy nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.copy = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Copying selected nodes...");
      const { clipboard } = buildClipboardFromDiagram(xyFlowStoreApi.getState(), bpmnEditorStoreApi.getState());
      navigator.clipboard.writeText(JSON.stringify(clipboard));
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlowStoreApi]);

  // Paste nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.paste = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Pasting nodes...");
      navigator.clipboard.readText().then((text) => {
        const clipboard = getClipboard<BpmnEditorDiagramClipboard>(text, BPMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE);
        if (!clipboard) {
          return;
        }

        getNewBpmnIdRandomizer()
          .ack({
            json: clipboard.processFlowElements,
            type: "BPMN20__tProcess",
            attr: "flowElement",
          })
          .ack({
            json: clipboard.artifacts,
            type: "BPMN20__tProcess",
            attr: "artifact",
          })
          .ack({
            json: clipboard.lanes,
            type: "BPMN20__tLaneSet",
            attr: "lane",
          })
          .ack({
            json: clipboard.shapes,
            type: "BPMNDI__BPMNPlane",
            attr: "di:DiagramElement",
            __$$element: "bpmndi:BPMNShape",
          })
          .ack({
            json: clipboard.edges,
            type: "BPMNDI__BPMNPlane",
            attr: "di:DiagramElement",
            __$$element: "bpmndi:BPMNEdge",
          })
          .randomize({ skipAlreadyAttributedIds: false });

        bpmnEditorStoreApi.setState((state) => {
          const { process, diagramElements } = addOrGetProcessAndDiagramElements({
            definitions: state.bpmn.model.definitions,
          });
          process.flowElement ??= [];
          process.flowElement.push(...clipboard.processFlowElements);
          process.artifact ??= [];
          process.artifact.push(...clipboard.artifacts);

          process.laneSet ??= [{ "@_id": generateUuid() }];
          process.laneSet[0].lane ??= [];
          process.laneSet[0].lane.push(...clipboard.lanes);

          diagramElements.push(
            ...clipboard.shapes.map((s) => ({
              ...s,
              __$$element: "bpmndi:BPMNShape" as const,
            }))
          );
          diagramElements.push(
            ...clipboard.edges.map((s) => ({
              ...s,
              __$$element: "bpmndi:BPMNEdge" as const,
            }))
          );

          state.xyFlowReactKieDiagram._selectedNodes = [
            ...clipboard.processFlowElements.filter((flowElement) => flowElement.__$$element !== "sequenceFlow"),
            ...clipboard.artifacts,
          ].map((s) => s["@_id"]);

          state.xyFlowReactKieDiagram._selectedEdges = [...clipboard.edges].map((s) => s["@_bpmnElement"]!);
        });
      });
    };
  }, [bpmnEditorStoreApi, commandsRef]);

  // Select/deselect all nodes
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.selectAll = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Selecting/Deselecting nodes...");
      const allNodeIds = xyFlowStoreApi
        .getState()
        .getNodes()
        .map((s) => s.id);

      const allEdgeIds = xyFlowStoreApi.getState().edges.map((s) => s.id);

      bpmnEditorStoreApi.setState((state) => {
        const allSelectedNodesSet = new Set(state.xyFlowReactKieDiagram._selectedNodes);
        const allSelectedEdgesSet = new Set(state.xyFlowReactKieDiagram._selectedEdges);

        // If everything is selected, deselect everything.
        if (
          allNodeIds.every((id) => allSelectedNodesSet.has(id) && allEdgeIds.every((id) => allSelectedEdgesSet.has(id)))
        ) {
          state.xyFlowReactKieDiagram._selectedNodes = [];
          state.xyFlowReactKieDiagram._selectedEdges = [];
        } else {
          state.xyFlowReactKieDiagram._selectedNodes = allNodeIds;
          state.xyFlowReactKieDiagram._selectedEdges = allEdgeIds;
        }
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlowStoreApi]);

  // Create group wrapping selection
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.createGroup = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Grouping nodes...");
      const selectedNodes = xyFlow.getNodes().filter((s) => s.selected);
      if (selectedNodes.length <= 0) {
        return;
      }

      bpmnEditorStoreApi.setState((state) => {
        if (state.xyFlowReactKieDiagram._selectedNodes.length <= 0) {
          return;
        }

        const { id: newNodeId } = addStandaloneNode({
          definitions: state.bpmn.model.definitions,
          __readonly_newNode: {
            type: NODE_TYPES.group,
            bounds: getBounds({
              nodes: selectedNodes,
              padding: CONTAINER_NODES_DESIRABLE_PADDING,
            }),
            data: undefined,
          },
          __readonly_element: "group",
        });

        state.dispatch(state).reset(state.bpmn.model);
        state.dispatch(state).setNodeStatus(newNodeId, { selected: true });
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlow]);

  // Create task node from current node
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.appendTaskNode = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Appending Task node...");
      const selectedNodes = xyFlow.getNodes().filter((s) => s.selected);
      if (selectedNodes.length !== 1) {
        return;
      }

      // Check node type
      if (
        selectedNodes[0].type !== NODE_TYPES.startEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateCatchEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateThrowEvent &&
        selectedNodes[0].type !== NODE_TYPES.task &&
        selectedNodes[0].type !== NODE_TYPES.subProcess &&
        selectedNodes[0].type !== NODE_TYPES.gateway
      ) {
        return;
      }

      bpmnEditorStoreApi.setState((state) => {
        const { id: newNodeId } = addConnectedNode({
          definitions: state.bpmn.model.definitions,
          __readonly_sourceNode: {
            bounds: selectedNodes[0].data.shape["dc:Bounds"],
            id: selectedNodes[0].id,
            shapeId: selectedNodes[0].data.shape["@_id"],
            type: selectedNodes[0].type as BpmnNodeType,
          },
          __readonly_newNode: {
            type: NODE_TYPES.task,
            bounds: {
              "@_x":
                selectedNodes[0].data.shape["dc:Bounds"]["@_x"] +
                selectedNodes[0].data.shape["dc:Bounds"]["@_width"] +
                100,
              "@_y": selectedNodes[0].data.shape["dc:Bounds"]["@_y"],
              "@_width": DEFAULT_NODE_SIZES[NODE_TYPES.task]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_width"],
              "@_height": DEFAULT_NODE_SIZES[NODE_TYPES.task]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_height"],
            },
          },
        });

        state.dispatch(state).reset(state.bpmn.model);
        state.dispatch(state).setNodeStatus(newNodeId, { selected: true });
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlow]);

  // Create gateway node from current node
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.appendGatewayNode = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Gateway node...");
      const selectedNodes = xyFlow.getNodes().filter((s) => s.selected);
      if (selectedNodes.length !== 1) {
        return;
      }

      // Check node type
      if (
        selectedNodes[0].type !== NODE_TYPES.startEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateCatchEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateThrowEvent &&
        selectedNodes[0].type !== NODE_TYPES.task &&
        selectedNodes[0].type !== NODE_TYPES.subProcess &&
        selectedNodes[0].type !== NODE_TYPES.gateway
      ) {
        return;
      }

      bpmnEditorStoreApi.setState((state) => {
        const { id: newNodeId } = addConnectedNode({
          definitions: state.bpmn.model.definitions,
          __readonly_sourceNode: {
            bounds: selectedNodes[0].data.shape["dc:Bounds"],
            id: selectedNodes[0].id,
            shapeId: selectedNodes[0].data.shape["@_id"],
            type: selectedNodes[0].type as BpmnNodeType,
          },
          __readonly_newNode: {
            type: NODE_TYPES.gateway,
            bounds: {
              "@_x":
                selectedNodes[0].data.shape["dc:Bounds"]["@_x"] +
                selectedNodes[0].data.shape["dc:Bounds"]["@_width"] +
                100,
              "@_y": selectedNodes[0].data.shape["dc:Bounds"]["@_y"],
              "@_width": DEFAULT_NODE_SIZES[NODE_TYPES.gateway]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_width"],
              "@_height": DEFAULT_NODE_SIZES[NODE_TYPES.gateway]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_height"],
            },
          },
        });

        state.dispatch(state).reset(state.bpmn.model);
        state.dispatch(state).setNodeStatus(newNodeId, { selected: true });
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlow]);

  // Create intermediate catch event from current node
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.appendIntermediateCatchEventNode = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Appending Intermediate Catch event node...");
      const selectedNodes = xyFlow.getNodes().filter((s) => s.selected);
      if (selectedNodes.length !== 1) {
        return;
      }

      // Check node type
      if (
        selectedNodes[0].type !== NODE_TYPES.startEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateCatchEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateThrowEvent &&
        selectedNodes[0].type !== NODE_TYPES.task &&
        selectedNodes[0].type !== NODE_TYPES.subProcess &&
        selectedNodes[0].type !== NODE_TYPES.gateway
      ) {
        return;
      }

      bpmnEditorStoreApi.setState((state) => {
        const { id: newNodeId } = addConnectedNode({
          definitions: state.bpmn.model.definitions,
          __readonly_sourceNode: {
            bounds: selectedNodes[0].data.shape["dc:Bounds"],
            id: selectedNodes[0].id,
            shapeId: selectedNodes[0].data.shape["@_id"],
            type: selectedNodes[0].type as BpmnNodeType,
          },
          __readonly_newNode: {
            type: NODE_TYPES.intermediateCatchEvent,
            bounds: {
              "@_x":
                selectedNodes[0].data.shape["dc:Bounds"]["@_x"] +
                selectedNodes[0].data.shape["dc:Bounds"]["@_width"] +
                100,
              "@_y": selectedNodes[0].data.shape["dc:Bounds"]["@_y"],
              "@_width": DEFAULT_NODE_SIZES[NODE_TYPES.intermediateCatchEvent]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_width"],
              "@_height": DEFAULT_NODE_SIZES[NODE_TYPES.intermediateCatchEvent]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_height"],
            },
          },
        });

        state.dispatch(state).reset(state.bpmn.model);
        state.dispatch(state).setNodeStatus(newNodeId, { selected: true });
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlow]);

  // Create intermediate throw event from current node
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.appendIntermediateThrowEventNode = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Appending Intermediate Throw event node...");
      const selectedNodes = xyFlow.getNodes().filter((s) => s.selected);
      if (selectedNodes.length !== 1) {
        return;
      }

      // Check node type
      if (
        selectedNodes[0].type !== NODE_TYPES.startEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateCatchEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateThrowEvent &&
        selectedNodes[0].type !== NODE_TYPES.task &&
        selectedNodes[0].type !== NODE_TYPES.subProcess &&
        selectedNodes[0].type !== NODE_TYPES.gateway
      ) {
        return;
      }

      bpmnEditorStoreApi.setState((state) => {
        const { id: newNodeId } = addConnectedNode({
          definitions: state.bpmn.model.definitions,
          __readonly_sourceNode: {
            bounds: selectedNodes[0].data.shape["dc:Bounds"],
            id: selectedNodes[0].id,
            shapeId: selectedNodes[0].data.shape["@_id"],
            type: selectedNodes[0].type as BpmnNodeType,
          },
          __readonly_newNode: {
            type: NODE_TYPES.intermediateThrowEvent,
            bounds: {
              "@_x":
                selectedNodes[0].data.shape["dc:Bounds"]["@_x"] +
                selectedNodes[0].data.shape["dc:Bounds"]["@_width"] +
                100,
              "@_y": selectedNodes[0].data.shape["dc:Bounds"]["@_y"],
              "@_width": DEFAULT_NODE_SIZES[NODE_TYPES.intermediateThrowEvent]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_width"],
              "@_height": DEFAULT_NODE_SIZES[NODE_TYPES.intermediateThrowEvent]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_height"],
            },
          },
        });

        state.dispatch(state).reset(state.bpmn.model);
        state.dispatch(state).setNodeStatus(newNodeId, { selected: true });
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlow]);

  // Create end event from current node
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.appendEndEventNode = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Appending End Event node...");
      const selectedNodes = xyFlow.getNodes().filter((s) => s.selected);
      if (selectedNodes.length !== 1) {
        return;
      }

      // Check node type
      if (
        selectedNodes[0].type !== NODE_TYPES.intermediateCatchEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateThrowEvent &&
        selectedNodes[0].type !== NODE_TYPES.task &&
        selectedNodes[0].type !== NODE_TYPES.subProcess &&
        selectedNodes[0].type !== NODE_TYPES.gateway
      ) {
        return;
      }

      bpmnEditorStoreApi.setState((state) => {
        const { id: newNodeId } = addConnectedNode({
          definitions: state.bpmn.model.definitions,
          __readonly_sourceNode: {
            bounds: selectedNodes[0].data.shape["dc:Bounds"],
            id: selectedNodes[0].id,
            shapeId: selectedNodes[0].data.shape["@_id"],
            type: selectedNodes[0].type as BpmnNodeType,
          },
          __readonly_newNode: {
            type: NODE_TYPES.endEvent,
            bounds: {
              "@_x":
                selectedNodes[0].data.shape["dc:Bounds"]["@_x"] +
                selectedNodes[0].data.shape["dc:Bounds"]["@_width"] +
                100,
              "@_y": selectedNodes[0].data.shape["dc:Bounds"]["@_y"],
              "@_width": DEFAULT_NODE_SIZES[NODE_TYPES.endEvent]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_width"],
              "@_height": DEFAULT_NODE_SIZES[NODE_TYPES.endEvent]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_height"],
            },
          },
        });

        state.dispatch(state).reset(state.bpmn.model);
        state.dispatch(state).setNodeStatus(newNodeId, { selected: true });
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlow]);

  // Create Text Annotation from current node
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.appendTextAnnotationNode = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Appending Text Annotation node...");
      const selectedNodes = xyFlow.getNodes().filter((s) => s.selected);
      if (selectedNodes.length !== 1) {
        return;
      }

      // Check node type
      if (
        selectedNodes[0].type !== NODE_TYPES.startEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateCatchEvent &&
        selectedNodes[0].type !== NODE_TYPES.intermediateThrowEvent &&
        selectedNodes[0].type !== NODE_TYPES.task &&
        selectedNodes[0].type !== NODE_TYPES.endEvent &&
        selectedNodes[0].type !== NODE_TYPES.subProcess &&
        selectedNodes[0].type !== NODE_TYPES.gateway &&
        selectedNodes[0].type !== NODE_TYPES.lane &&
        selectedNodes[0].type !== NODE_TYPES.dataObject &&
        selectedNodes[0].type !== NODE_TYPES.group
      ) {
        return;
      }

      bpmnEditorStoreApi.setState((state) => {
        const { id: newNodeId } = addConnectedNode({
          definitions: state.bpmn.model.definitions,
          __readonly_sourceNode: {
            bounds: selectedNodes[0].data.shape["dc:Bounds"],
            id: selectedNodes[0].id,
            shapeId: selectedNodes[0].data.shape["@_id"],
            type: selectedNodes[0].type as BpmnNodeType,
          },
          __readonly_newNode: {
            type: NODE_TYPES.textAnnotation,
            bounds: {
              "@_x":
                selectedNodes[0].data.shape["dc:Bounds"]["@_x"] +
                selectedNodes[0].data.shape["dc:Bounds"]["@_width"] +
                100,
              "@_y": selectedNodes[0].data.shape["dc:Bounds"]["@_y"],
              "@_width": DEFAULT_NODE_SIZES[NODE_TYPES.textAnnotation]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_width"],
              "@_height": DEFAULT_NODE_SIZES[NODE_TYPES.textAnnotation]({
                snapGrid: state.xyFlowReactKieDiagram.snapGrid,
              })["@_height"],
            },
          },
        });

        state.dispatch(state).reset(state.bpmn.model);
        state.dispatch(state).setNodeStatus(newNodeId, { selected: true });
      });
    };
  }, [bpmnEditorStoreApi, commandsRef, xyFlow]);

  // Show Properties panel
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }
    commandsRef.current.togglePropertiesPanel = async () => {
      console.debug("BPMN DIAGRAM: COMMANDS: Toggle properties panel...");
      bpmnEditorStoreApi.setState((state) => {
        state.propertiesPanel.isOpen = !state.propertiesPanel.isOpen;
      });
    };
  }, [bpmnEditorStoreApi, commandsRef]);

  return <></>;
}
