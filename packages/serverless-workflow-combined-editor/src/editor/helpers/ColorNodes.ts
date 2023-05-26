/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Node } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api/StunnerEditorEnvelopeAPI";

const colorNode = async (node: Node, color: string) => {
  await window.editor.canvas.setBackgroundColor(node.uuid, color);
};

export const colorNodes = (nodeNameList: string[], color: string, colorConnectedEnds: boolean): void => {
  Promise.all(nodeNameList.map((name) => window.editor.session.getNodeByName(name).catch(() => null)))
    .then((nodeListToColor) => {
      const nodeList = nodeListToColor.filter((node) => node !== null) as Node[];
      return nodeList.forEach((node) => {
        if (node.definition.name !== "End") {
          colorNode(node, color);
          if (colorConnectedEnds) {
            Promise.all(
              node.outEdges.map((edge) => edge.target).map((target) => window.editor.session.getNodeByUUID(target))
            )
              .then((outNodes) => {
                return outNodes
                  .filter((outNode) => outNode.definition.id === "org.kie.workbench.common.stunner.sw.definition.End")
                  .forEach((outNode) => colorNode(outNode, color));
              })
              .then((_) => window.editor.canvas.draw());
          }
        }
      });
    })
    .then((_) => window.editor.canvas.draw());
};
