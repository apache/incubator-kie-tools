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

const paintCompletedNode = async (node: Node) => {
  await window.editor.canvas.setBackgroundColor(node.uuid, "#d5f4e6");
};

const isPointingToAnyCompletedNode = (completedNodes: (Node | null)[], node: Node): boolean => {
  return (
    node.outEdges.filter((edge) => completedNodes.filter((node) => node && edge.target === node.uuid).length > 0)
      .length > 0
  );
};

export const paintCompletedNodes = (nodeNameList: string[], isWorkflowCompleted: boolean): void => {
  Promise.all(nodeNameList.map((name) => window.editor.session.getNodeByName(name).catch(() => null)))
    .then((completedNodes) =>
      completedNodes.forEach((completedNode) => {
        if (completedNode) {
          paintCompletedNode(completedNode);
          if (isWorkflowCompleted && !isPointingToAnyCompletedNode(completedNodes, completedNode)) {
            Promise.all(
              completedNode.outEdges
                .map((edge) => edge.target)
                .map((target) => window.editor.session.getNodeByUUID(target))
            )
              .then((outNodes) =>
                outNodes
                  .filter((outNode) => outNode.definition.id === "org.kie.workbench.common.stunner.sw.definition.End")
                  .forEach((outNode) => paintCompletedNode(outNode))
              )
              .then((_) => window.editor.canvas.draw());
          }
        }
      })
    )
    .then((_) => window.editor.canvas.draw());
};
