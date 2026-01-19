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

import { Unpacked } from "../tsExt/tsExt";
import * as RF from "reactflow";
import { State } from "../store/Store";
import { NodeNature, nodeNatures } from "../mutations/NodeNature";
import { SwfDiagramNodeData } from "../diagram/nodes/SwfNodes";
import { NodeType } from "../diagram/connections/graphStructure";
import { Specification } from "@serverlessworkflow/sdk-typescript";

export const SWF_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE = "application/json+kie-swf-editor--diagram" as const;

export type SwfEditorDiagramClipboard = {
  mimeType: typeof SWF_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE;
  swfElements: NonNullable<Unpacked<Specification.States>>[];
};

export function buildClipboardFromDiagram(rfState: RF.ReactFlowState, swfEditorState: State) {
  const copiedNodesById = new Map<string, RF.Node<SwfDiagramNodeData>>();

  const selectedNodesById = rfState
    .getNodes()
    .reduce((acc, n) => (n.selected ? acc.set(n.id, n) : acc), new Map<string, RF.Node<SwfDiagramNodeData>>());

  const clipboard = [...selectedNodesById.values()].reduce<SwfEditorDiagramClipboard>(
    (acc, _node: RF.Node<SwfDiagramNodeData>) => {
      function accNode(node: RF.Node<SwfDiagramNodeData>) {
        const nodeNature = nodeNatures[node.type as NodeType];

        // Swf Elements
        if (nodeNature === NodeNature.SWF_STATE) {
          const swfObject = JSON.parse(JSON.stringify(node.data.swfObject)) as Unpacked<Specification.States>;

          acc.swfElements.unshift(swfObject as any);
        } else if (nodeNature === NodeNature.UNKNOWN) {
          // Ignore.
        } else {
          throw new Error(`Unknwon node nature '${nodeNature}'`);
        }

        copiedNodesById.set(node.id, node);
      }

      if (!_node.selected) {
        return acc;
      }

      accNode(_node);

      return acc;
    },
    {
      mimeType: SWF_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE,
      swfElements: [],
    }
  );

  return { clipboard, copiedNodesById };
}

export function getClipboard<T extends { mimeType: string }>(text: string, mimeType: string): T | undefined {
  let potentialClipboard: T | undefined;
  try {
    potentialClipboard = JSON.parse(text);
  } catch (e) {
    console.debug("SWF DIAGRAM: Ignoring pasted content. Not a valid JSON.");
    return undefined;
  }

  if (!potentialClipboard || potentialClipboard.mimeType !== mimeType) {
    console.debug("SWF DIAGRAM: Ignoring pasted content. MIME type doesn't match.");
    return undefined;
  }

  return potentialClipboard;
}
