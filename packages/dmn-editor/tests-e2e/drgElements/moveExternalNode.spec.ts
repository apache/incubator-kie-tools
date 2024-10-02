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

import { expect, test } from "../__fixtures__/base";
import { TabName } from "../__fixtures__/editor";
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ stories }) => {
  await stories.openEmptyWithAvailableExternalModels();
});

test.describe("Move external node - Decision", () => {
  test("move an external node from Decision Service to out", async ({
    editor,
    palette,
    diagram,
    nodes,
    includedModels,
  }) => {
    await editor.changeTab({ tab: TabName.INCLUDED_MODELS });

    await includedModels.getIncludeModelButton().click();
    await includedModels.fillModelToInclude({ modelName: "sumDiffDs.dmn" });
    await includedModels.selectModel({ modelName: "sumDiffDs.dmn" });
    await includedModels.fillModelName({ modelName: "MY_MODEL" });
    await includedModels.includeModel();

    await editor.changeTab({ tab: TabName.EDITOR });
    await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 400, y: 200 } });

    await palette.toggleExternalNodesPanel();

    await palette.dragExternalNode({
      includedModelName: "MY_MODEL",
      nodeName: "Sum",
      targetPosition: {
        x: 200,
        y: 200,
      },
    });

    await palette.dragExternalNode({
      includedModelName: "MY_MODEL",
      nodeName: "Diff",
      targetPosition: {
        x: 300,
        y: 300,
      },
    });

    // We're hiding the panel now.
    await palette.toggleExternalNodesPanel();

    await diagram.resetFocus();
    await nodes.move({ name: "Sum", targetPosition: { x: 500, y: 300 } });
    await nodes.move({ name: "Diff", targetPosition: { x: 500, y: 430 } });

    // We move it to make it sure that the nodes are added inside the Decision Services
    // and are really attached to it, not only "visually over it".
    await nodes.move({ name: DefaultNodeName.DECISION_SERVICE, targetPosition: { x: 100, y: 400 } });

    await expect(diagram.get()).toHaveScreenshot(
      "move-external-decision-inside-decision-service-before-move-to-out.png"
    );

    await diagram.resetFocus();

    await nodes.move({ name: "Sum", targetPosition: { x: 400, y: 120 } });
    await nodes.move({ name: "Diff", targetPosition: { x: 400, y: 400 } });

    // Move again to make sure that nodes are not attached anymore to Decision Service.
    await nodes.move({ name: DefaultNodeName.DECISION_SERVICE, targetPosition: { x: 600, y: 100 } });

    await expect(diagram.get()).toHaveScreenshot(
      "move-external-decision-inside-decision-service-after-move-to-out.png"
    );
  });

  test("move an external decision from one Decision Service to another Decision Service", async ({
    editor,
    page,
    palette,
    diagram,
    nodes,
    includedModels,
  }) => {
    await editor.changeTab({ tab: TabName.INCLUDED_MODELS });

    await includedModels.getIncludeModelButton().click();
    await includedModels.fillModelToInclude({ modelName: "sumDiffDs.dmn" });
    await includedModels.selectModel({ modelName: "sumDiffDs.dmn" });
    await includedModels.fillModelName({ modelName: "MY_MODEL" });
    await includedModels.includeModel();

    await editor.changeTab({ tab: TabName.EDITOR });
    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 250, y: 50 },
      thenRenameTo: DefaultNodeName.DECISION_SERVICE + " 1",
    });
    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 650, y: 50 },
      thenRenameTo: DefaultNodeName.DECISION_SERVICE + " 2",
    });

    await palette.toggleExternalNodesPanel();

    await palette.dragExternalNode({
      includedModelName: "MY_MODEL",
      nodeName: "Sum",
      targetPosition: {
        x: 600,
        y: 400,
      },
    });

    await palette.dragExternalNode({
      includedModelName: "MY_MODEL",
      nodeName: "Diff",
      targetPosition: {
        x: 200,
        y: 400,
      },
    });

    // We're hiding the panel now.
    await palette.toggleExternalNodesPanel();

    await diagram.resetFocus();
    await nodes.move({ name: "Sum", targetPosition: { x: 350, y: 150 } });
    await nodes.move({ name: "Diff", targetPosition: { x: 350, y: 250 } });
    await nodes.move({ name: DefaultNodeName.DECISION_SERVICE + " 1", targetPosition: { x: 200, y: 300 } });
    await nodes.move({ name: DefaultNodeName.DECISION_SERVICE + " 2", targetPosition: { x: 600, y: 300 } });

    await expect(diagram.get()).toHaveScreenshot("move-external-decision-from-ds-to-another-ds-before-move.png");

    await diagram.resetFocus();

    await nodes.move({ name: "Sum", targetPosition: { x: 680, y: 350 } });
    await nodes.move({ name: "Diff", targetPosition: { x: 680, y: 500 } });

    // We move it to make it sure that the nodes are added inside the Decision Services
    // and are really attached to it, not only "visually over it".
    await nodes.move({ name: DefaultNodeName.DECISION_SERVICE + " 1", targetPosition: { x: 100, y: 100 } });
    await nodes.move({ name: DefaultNodeName.DECISION_SERVICE + " 2", targetPosition: { x: 400, y: 400 } });

    await expect(diagram.get()).toHaveScreenshot("move-external-decision-from-ds-to-another-ds-after-move.png");
  });

  test("move an external decision out from Decision Service", async ({
    editor,
    page,
    palette,
    diagram,
    nodes,
    includedModels,
  }) => {
    await editor.changeTab({ tab: TabName.INCLUDED_MODELS });

    await includedModels.getIncludeModelButton().click();
    await includedModels.fillModelToInclude({ modelName: "sumDiffDs.dmn" });
    await includedModels.selectModel({ modelName: "sumDiffDs.dmn" });
    await includedModels.fillModelName({ modelName: "MY_MODEL" });
    await includedModels.includeModel();

    await editor.changeTab({ tab: TabName.EDITOR });
    await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 400, y: 200 } });

    await palette.toggleExternalNodesPanel();

    await palette.dragExternalNode({
      includedModelName: "MY_MODEL",
      nodeName: "Sum",
      targetPosition: {
        x: 200,
        y: 200,
      },
    });

    await palette.dragExternalNode({
      includedModelName: "MY_MODEL",
      nodeName: "Diff",
      targetPosition: {
        x: 300,
        y: 300,
      },
    });

    // We're hiding the panel now.
    await palette.toggleExternalNodesPanel();

    await diagram.resetFocus();
    await nodes.move({ name: "Sum", targetPosition: { x: 500, y: 300 } });
    await nodes.move({ name: "Diff", targetPosition: { x: 500, y: 430 } });

    // We move it to make it sure that the nodes are added inside the Decision Services
    // and are really attached to it, not only "visually over it".
    await nodes.move({ name: DefaultNodeName.DECISION_SERVICE, targetPosition: { x: 100, y: 400 } });

    await expect(diagram.get()).toHaveScreenshot("move-external-decision-out-from-decision-service-before.png");

    await nodes.move({ name: "Sum", targetPosition: { x: 530, y: 100 } });
    await nodes.move({ name: "Diff", targetPosition: { x: 530, y: 200 } });
    await nodes.move({ name: DefaultNodeName.DECISION_SERVICE, targetPosition: { x: 120, y: 200 } });

    await diagram.resetFocus();

    await expect(diagram.get()).toHaveScreenshot("move-external-decision-out-from-decision-service-after.png");
  });
});
