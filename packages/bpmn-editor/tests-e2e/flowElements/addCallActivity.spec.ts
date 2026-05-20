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

import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { test, expect } from "../__fixtures__/base";
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Call Activity", () => {
  test.describe("Add from palette", () => {
    test("should add Call Activity node from palette", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 100, y: 100 } });

      await expect(nodes.get({ name: DefaultNodeName.CALL_ACTIVITY })).toBeAttached();

      const callActivity = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(callActivity.__$$element).toBe("callActivity");
      expect(callActivity["@_name"]).toBe(DefaultNodeName.CALL_ACTIVITY);
    });

    test("should add two Call Activity nodes from palette in a row", async ({ palette, nodes, diagram }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
      });

      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "First Call Activity",
      });

      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Second Call Activity",
      });

      await diagram.resetFocus();

      await expect(nodes.get({ name: "First Call Activity" })).toBeAttached();
      await expect(nodes.get({ name: "Second Call Activity" })).toBeAttached();
    });
  });

  test.describe("Add connected Call Activity node", () => {
    test("should add connected Task node from Call Activity", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 100, y: 100 },
      });

      const callActivity = nodes.get({ name: "New Call Activity" });
      await expect(callActivity).toBeAttached();

      await nodes.showNodeHandles({ name: "New Call Activity" });

      const addTaskHandle = callActivity.getByTitle("Add Task");
      await expect(addTaskHandle).toBeVisible();

      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should add connected Gateway node from Call Activity", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 100, y: 100 },
      });

      const callActivity = nodes.get({ name: "New Call Activity" });
      await expect(callActivity).toBeAttached();

      await nodes.showNodeHandles({ name: "New Call Activity" });

      const addGatewayHandle = callActivity.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(diagram.get()).toHaveScreenshot("add-gateway-node-from-call-activity.png");
    });

    test("should create sequence flow from Call Activity to End Event", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 100, y: 100 },
      });
      await diagram.resetFocus();
      await palette.dragNewNode({
        type: NodeType.END_EVENT,
        targetPosition: { x: 300, y: 100 },
      });

      const callActivity = nodes.get({ name: "New Call Activity" });
      await expect(callActivity).toBeAttached();

      const endEvent = nodes.getByType(NodeType.END_EVENT);
      await expect(endEvent).toBeVisible();

      await nodes.showNodeHandles({ name: "New Call Activity" });

      const addSequenceFlowHandle = callActivity.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const endEventBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.END_EVENT) });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: endEventBox.x + endEventBox.width / 2, y: endEventBox.y + endEventBox.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-call-activity-to-end-event.png");
    });

    test("should add connected Call Activity from Start Event", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.START_EVENT,
        targetPosition: { x: 100, y: 100 },
      });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeAttached();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.START_EVENT) });

      const addTaskHandle = startEvent.getByTitle("Add Task");
      await expect(addTaskHandle).toBeVisible();

      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      const task = nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      await nodes.morphNode({ nodeLocator: task, targetMorphType: "Call activity" });

      await expect(diagram.get()).toHaveScreenshot("add-call-activity-from-start-event.png");
    });

    test("should create sequence flow from Call Activity to another Call Activity", async ({
      diagram,
      palette,
      nodes,
    }) => {
      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "First Call Activity",
      });

      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 350, y: 100 },
        thenRenameTo: "Second Call Activity",
      });

      const firstCallActivity = nodes.get({ name: "First Call Activity" });
      await expect(firstCallActivity).toBeAttached();

      const secondCallActivity = nodes.get({ name: "Second Call Activity" });
      await expect(secondCallActivity).toBeAttached();

      await nodes.showNodeHandles({ name: "First Call Activity" });

      const addSequenceFlowHandle = firstCallActivity.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const secondCallActivityBox = await nodes.getNodeBounds({ name: "Second Call Activity" });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: {
          x: secondCallActivityBox.x + secondCallActivityBox.width / 2,
          y: secondCallActivityBox.y + secondCallActivityBox.height / 2,
        },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-call-activity-to-call-activity.png");
    });

    test("should create sequence flow from Gateway to Call Activity", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.GATEWAY,
        targetPosition: { x: 100, y: 100 },
      });

      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 350, y: 100 },
      });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      const callActivity = nodes.get({ name: "New Call Activity" });
      await expect(callActivity).toBeAttached();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.GATEWAY) });

      const addSequenceFlowHandle = gateway.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const callActivityBox = await nodes.getNodeBounds({ name: "New Call Activity" });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: {
          x: callActivityBox.x + callActivityBox.width / 2,
          y: callActivityBox.y + callActivityBox.height / 2,
        },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-gateway-to-call-activity.png");
    });

    test("should create sequence flow from Call Activity to Gateway", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 100, y: 100 },
      });

      await palette.dragNewNode({
        type: NodeType.GATEWAY,
        targetPosition: { x: 350, y: 100 },
      });

      const callActivity = nodes.get({ name: "New Call Activity" });
      await expect(callActivity).toBeAttached();

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      await nodes.showNodeHandles({ name: "New Call Activity" });

      const addSequenceFlowHandle = callActivity.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const gatewayBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.GATEWAY) });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: gatewayBox.x + gatewayBox.width / 2, y: gatewayBox.y + gatewayBox.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-call-activity-to-gateway.png");
    });
  });

  test.describe("Call Activity in process flow", () => {
    test.beforeEach(async ({ editor, page }) => {
      await page.setViewportSize({ width: 1920, height: 1080 });
      await editor.open();
    });

    test("should create a complete process with Call Activity", async ({ jsonModel, palette, nodes, diagram }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 150 } });
      await palette.dragNewNode({
        type: NodeType.TASK,
        targetPosition: { x: 250, y: 150 },
        thenRenameTo: "Prepare Data",
      });
      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 500, y: 150 },
        thenRenameTo: "Execute Subprocess",
      });
      await palette.dragNewNode({
        type: NodeType.TASK,
        targetPosition: { x: 750, y: 150 },
        thenRenameTo: "Process Results",
      });
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 1000, y: 150 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeAttached();

      const prepareData = nodes.get({ name: "Prepare Data" });
      const callActivity = nodes.get({ name: "Execute Subprocess" });
      const processResults = nodes.get({ name: "Process Results" });
      const endEvent = nodes.getByType(NodeType.END_EVENT);

      // Connect Start Event -> Prepare Data
      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.START_EVENT) });
      const handle1 = startEvent.getByTitle("Add Sequence Flow");
      await expect(handle1).toBeVisible();
      let targetBox = await nodes.getNodeBounds({ name: "Prepare Data" });
      await handle1.dragTo(diagram.get(), {
        targetPosition: { x: targetBox.x + targetBox.width / 2, y: targetBox.y + targetBox.height / 2 },
      });

      // Connect Prepare Data -> Execute Subprocess
      await nodes.showNodeHandles({ name: "Prepare Data" });
      const handle2 = prepareData.getByTitle("Add Sequence Flow");
      await expect(handle2).toBeVisible();
      targetBox = await nodes.getNodeBounds({ name: "Execute Subprocess" });
      await handle2.dragTo(diagram.get(), {
        targetPosition: { x: targetBox.x + targetBox.width / 2, y: targetBox.y + targetBox.height / 2 },
      });

      // Connect Execute Subprocess -> Process Results
      await nodes.showNodeHandles({ name: "Execute Subprocess" });
      const handle3 = callActivity.getByTitle("Add Sequence Flow");
      await expect(handle3).toBeVisible();
      targetBox = await nodes.getNodeBounds({ name: "Process Results" });
      await handle3.dragTo(diagram.get(), {
        targetPosition: { x: targetBox.x + targetBox.width / 2, y: targetBox.y + targetBox.height / 2 },
      });

      // Connect Process Results -> End Event
      await nodes.showNodeHandles({ name: "Process Results" });
      const handle4 = processResults.getByTitle("Add Sequence Flow");
      await expect(handle4).toBeVisible();
      targetBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.END_EVENT) });
      await handle4.dragTo(diagram.get(), {
        targetPosition: { x: targetBox.x + targetBox.width / 2, y: targetBox.y + targetBox.height / 2 },
      });

      await diagram.resetFocus();

      const process = await jsonModel.getProcess();
      const callActivityElement = process.flowElement?.find(
        (el: { __$$element: string; "@_name"?: string }) => el["@_name"] === "Execute Subprocess"
      );
      expect(callActivityElement.__$$element).toBe("callActivity");
      expect(callActivityElement["@_name"]).toBe("Execute Subprocess");

      await expect(diagram.get()).toHaveScreenshot("complete-process-with-call-activity.png");
    });
  });

  test.describe("Call Activity operations", () => {
    test("should delete call activity", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });

      await nodes.delete({ name: DefaultNodeName.CALL_ACTIVITY });

      await expect(nodes.get({ name: DefaultNodeName.CALL_ACTIVITY })).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move call activity to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });

      const callActivity = nodes.getByType(NodeType.CALL_ACTIVITY);
      await expect(callActivity).toBeAttached();
      await callActivity.scrollIntoViewIfNeeded();

      const callActivityBox = await nodes.getNodeBounds({ name: DefaultNodeName.CALL_ACTIVITY });

      await callActivity.dragTo(diagram.get(), {
        sourcePosition: { x: 20, y: callActivityBox.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await nodes.getNodeBounds({ name: DefaultNodeName.CALL_ACTIVITY });
      expect(boxAfter.x).not.toBe(callActivityBox.x);
      expect(boxAfter.y).not.toBe(callActivityBox.y);
    });

    test("should rename call activity", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });

      await nodes.rename({ current: DefaultNodeName.CALL_ACTIVITY, new: "Invoke Subprocess" });

      await expect(nodes.get({ name: "Invoke Subprocess" })).toBeAttached();

      const callActivity = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(callActivity.__$$element).toBe("callActivity");
      expect(callActivity["@_name"]).toBe("Invoke Subprocess");
    });
  });
});
