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
  BPMN20__tLane,
  BPMN20__tProcess,
  BPMNDI__BPMNEdge,
  BPMNDI__BPMNShape,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import * as RF from "reactflow";
import { Normalized } from "../normalization/normalize";
import { State } from "../store/Store";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { BpmnDiagramEdgeData, BpmnDiagramNodeData, BpmnNodeType } from "../diagram/BpmnDiagramDomain";
import { NodeNature, nodeNatures } from "../mutations/_NodeNature";

export const BPMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE = "application/json+kie-bpmn-editor--diagram" as const;

export type BpmnEditorDiagramClipboard = {
  mimeType: typeof BPMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE;
  namespaceWhereClipboardWasCreatedFrom: string;
  processFlowElements: NonNullable<Unpacked<Normalized<BPMN20__tProcess>["flowElement"]>>[];
  artifacts: NonNullable<Unpacked<Normalized<BPMN20__tProcess>["artifact"]>>[];
  shapes: Normalized<BPMNDI__BPMNShape>[];
  edges: Normalized<BPMNDI__BPMNEdge>[];
  lanes: Normalized<BPMN20__tLane>[];
};

export function buildClipboardFromDiagram(xyFlowState: RF.ReactFlowState, bpmnEditorState: State) {
  const copiedEdgesById = new Map<string, RF.Edge<BpmnDiagramEdgeData>>();
  const copiedNodesById = new Map<string, RF.Node<BpmnDiagramNodeData>>();
  const danglingEdgesById = new Map<string, RF.Edge<BpmnDiagramEdgeData>>();

  const nodesById = xyFlowState
    .getNodes()
    .reduce((acc, n) => acc.set(n.id, n), new Map<string, RF.Node<BpmnDiagramNodeData>>());

  const selectedNodesById = xyFlowState
    .getNodes()
    .reduce((acc, n) => (n.selected ? acc.set(n.id, n) : acc), new Map<string, RF.Node<BpmnDiagramNodeData>>());

  const processFlowElements = new Map<string, NonNullable<Unpacked<Normalized<BPMN20__tProcess>["flowElement"]>>>();
  const shapes = new Map<string, Normalized<BPMNDI__BPMNShape>>();
  const subProcessNodes = new Map<string, Set<string>>();

  const clipboard = [...selectedNodesById.values()].reduce<BpmnEditorDiagramClipboard>(
    (acc, _node: RF.Node<BpmnDiagramNodeData>) => {
      function accNode(node: RF.Node<BpmnDiagramNodeData>) {
        const nodeNature = nodeNatures[node.type as BpmnNodeType];

        // Artifacts: Groups, Text Annotation, Unknown
        if (nodeNature === NodeNature.ARTIFACT) {
          acc.artifacts.unshift(node.data.bpmnElement as any);
        }
        // Container: Sub process
        else if (nodeNature === NodeNature.CONTAINER) {
          const bpmnSubProcess = JSON.parse(JSON.stringify(node.data.bpmnElement)) as Normalized<
            Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>
          >;

          if (bpmnSubProcess.__$$element === "subProcess") {
            for (const flowElement of bpmnSubProcess.flowElement ?? []) {
              const bpmnNode = nodesById.get(flowElement["@_id"]);
              if (!bpmnNode) {
                continue;
              }

              // Save nested Flow Elements id and shape
              subProcessNodes.set(
                bpmnSubProcess["@_id"],
                (subProcessNodes.get(bpmnSubProcess["@_id"]) ?? new Set()).add(flowElement["@_id"])
              );
              const { ...bpmnShape } = bpmnNode.data.shape;
              shapes.set(bpmnShape["@_id"], bpmnShape);
              accNode(bpmnNode);
            }
          }

          processFlowElements.set(bpmnSubProcess["@_id"], bpmnSubProcess);
        }
        // Lane
        else if (nodeNature === NodeNature.LANE) {
          const lane = JSON.parse(JSON.stringify(node.data.bpmnElement)) as Normalized<NonNullable<BPMN20__tLane>>;

          const laneNode = nodesById.get(lane["@_id"]!);
          if (!laneNode) {
            return;
          }

          for (const flowNodeRef of lane.flowNodeRef ?? []) {
            const bpmnNode = nodesById.get(flowNodeRef.__$$text);
            if (!bpmnNode) {
              continue;
            }

            accNode(bpmnNode);
          }
          acc.lanes.push(lane as any);
        }
        // Process Flow Element: Start Event, End Event, Itermediate Event, Task, Gateway, Data Object
        else if (nodeNature === NodeNature.PROCESS_FLOW_ELEMENT) {
          // Casting to `BPMN20__tProcess` because it has all requirement types.
          const bpmnProcessFlow = JSON.parse(JSON.stringify(node.data.bpmnElement)) as Normalized<
            Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>
          >;

          if (bpmnProcessFlow.__$$element === "adHocSubProcess" || bpmnProcessFlow.__$$element === "subChoreography") {
            for (const flowElement of bpmnProcessFlow.flowElement ?? []) {
              const bpmnNode = nodesById.get(flowElement["@_id"]);
              if (!bpmnNode) {
                // Decision Service has a reference to an unknown Decision. Ignoring.
                continue;
              }

              // Save nested Flow Elements id and shape
              subProcessNodes.set(
                bpmnProcessFlow["@_id"],
                (subProcessNodes.get(bpmnProcessFlow["@_id"]) ?? new Set()).add(flowElement["@_id"])
              );
              const { ...bpmnShape } = bpmnNode.data.shape;
              shapes.set(bpmnShape["@_id"], bpmnShape);
              accNode(bpmnNode);
            }
          }

          processFlowElements.set(bpmnProcessFlow["@_id"], bpmnProcessFlow);
        } else if (nodeNature === NodeNature.UNKNOWN) {
          // Ignore.
        } else {
          throw new Error(`Unknwon node nature '${nodeNature}'`);
        }

        copiedNodesById.set(node.id, node);

        const { ...bpmnShape } = node.data.shape;
        shapes.set(bpmnShape["@_id"], bpmnShape);
      }

      if (!_node.selected) {
        return acc;
      }

      accNode(_node);
      return acc;
    },
    {
      mimeType: BPMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE,
      namespaceWhereClipboardWasCreatedFrom: bpmnEditorState.bpmn.model.definitions["@_targetNamespace"],
      processFlowElements: [],
      artifacts: [],
      shapes: [],
      edges: [],
      lanes: [],
    }
  );

  clipboard.edges = xyFlowState.edges.flatMap((edge: RF.Edge<BpmnDiagramEdgeData>) => {
    if (copiedNodesById.has(edge.source) && !copiedNodesById.has(edge.target)) {
      danglingEdgesById.set(edge.id, edge); // Edges that point to nodes that are not part of the clipboard need to be removed when 'cut' is executed.
    }

    if (copiedNodesById.has(edge.source) && copiedNodesById.has(edge.target)) {
      if (!edge.data?.bpmnEdge) {
        return [];
      }

      copiedEdgesById.set(edge.id, edge);
      const { ...bpmnEdge } = edge.data!.bpmnEdge!;

      processFlowElements.set(
        edge.data.bpmnElement["@_id"],
        edge.data.bpmnElement as Normalized<
          Extract<Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>, { __$$element: "sequenceFlow" }>
        >
      );

      return bpmnEdge ?? [];
    } else {
      return [];
    }
  });

  // Filter Flow Elements that are part of Sub Processes
  subProcessNodes.forEach((flowElements) => {
    flowElements.forEach((flowElement) => {
      processFlowElements.delete(flowElement);
    });
  });

  clipboard.processFlowElements = [...processFlowElements.values()];
  clipboard.shapes = [...shapes.values()];

  return { clipboard, copiedEdgesById, copiedNodesById, danglingEdgesById };
}

export function getClipboard<T extends { mimeType: string }>(text: string, mimeType: string): T | undefined {
  let potentialClipboard: T | undefined;
  try {
    potentialClipboard = JSON.parse(text);
  } catch (e) {
    console.debug("BPMN DIAGRAM: Ignoring pasted content. Not a valid JSON.");
    return undefined;
  }

  if (!potentialClipboard || potentialClipboard.mimeType !== mimeType) {
    console.debug("BPMN DIAGRAM: Ignoring pasted content. MIME type doesn't match.");
    return undefined;
  }

  return potentialClipboard;
}
