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
import { DefaultNodeName, NodeType, TaskNodeType, NodePosition, EventNodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Call Activity", () => {
  test.describe("Add from palette", () => {
    test("should add Call Activity node from palette", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 100, y: 100 } });

      await expect(nodes.get({ name: DefaultNodeName.CALL_ACTIVITY })).toBeAttached();

      const callActivity = (await jsonModel.getCallActivities())[0];
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

      const callActivity = nodes.get({ name: DefaultNodeName.CALL_ACTIVITY });
      await expect(callActivity).toBeAttached();

      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: DefaultNodeName.CALL_ACTIVITY,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should add connected Gateway node from Call Activity", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.CALL_ACTIVITY,
        targetPosition: { x: 100, y: 100 },
      });

      const callActivity = nodes.get({ name: DefaultNodeName.CALL_ACTIVITY });
      await expect(callActivity).toBeAttached();

      await nodes.dragNewConnectedNode({
        type: NodeType.GATEWAY,
        from: DefaultNodeName.CALL_ACTIVITY,
        targetPosition: { x: 300, y: 100 },
      });

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

      const callActivity = nodes.get({ name: DefaultNodeName.CALL_ACTIVITY });
      await expect(callActivity).toBeAttached();
      await expect(nodes.getByType(NodeType.END_EVENT)).toBeVisible();

      const endEventId = await nodes.getIdByType(NodeType.END_EVENT);
      await nodes.createSequenceFlow({
        from: DefaultNodeName.CALL_ACTIVITY,
        to: endEventId,
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

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: startEventId,
        targetPosition: { x: 300, y: 100 },
      });

      const task = nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();

      await nodes.morph({ node: task, to: TaskNodeType.CALL_ACTIVITY });

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

      await nodes.createSequenceFlow({
        from: "First Call Activity",
        to: "Second Call Activity",
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
      await expect(nodes.get({ name: DefaultNodeName.CALL_ACTIVITY })).toBeAttached();

      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      await nodes.createSequenceFlow({
        from: gatewayId,
        to: DefaultNodeName.CALL_ACTIVITY,
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

      const callActivity = nodes.get({ name: DefaultNodeName.CALL_ACTIVITY });
      await expect(callActivity).toBeAttached();
      await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();

      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      await nodes.createSequenceFlow({
        from: DefaultNodeName.CALL_ACTIVITY,
        to: gatewayId,
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

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      await nodes.createSequenceFlow({
        from: startEventId,
        to: "Prepare Data",
      });
      await nodes.createSequenceFlow({
        from: "Prepare Data",
        to: "Execute Subprocess",
      });
      await nodes.createSequenceFlow({
        from: "Execute Subprocess",
        to: "Process Results",
      });
      const endEventId = await nodes.getIdByType(NodeType.END_EVENT);
      await nodes.createSequenceFlow({
        from: "Process Results",
        to: endEventId,
      });
      await expect(diagram.get()).toHaveScreenshot("complete-process-with-call-activity.png");

      const callActivityElement = (await jsonModel.getCallActivities())[0];
      expect(callActivityElement?.__$$element).toBe("callActivity");
      expect(callActivityElement?.["@_name"]).toBe("Execute Subprocess");
    });
  });

  test.describe("Call Activity operations", () => {
    test("should delete call activity", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });
      await nodes.delete({ name: DefaultNodeName.CALL_ACTIVITY });
      await expect(nodes.get({ name: DefaultNodeName.CALL_ACTIVITY })).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process?.flowElement?.length).toBe(0);
    });

    test("should move call activity to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });

      const callActivity = nodes.getByType(NodeType.CALL_ACTIVITY);
      await expect(callActivity).toBeAttached();
      await callActivity.scrollIntoViewIfNeeded();

      const callActivityBox = await nodes.getNodeBounds({ name: DefaultNodeName.CALL_ACTIVITY });

      await nodes.dragNodeToPosition({
        name: DefaultNodeName.CALL_ACTIVITY,
        fromPosition: NodePosition.LEFT,
        toPosition: { x: 500, y: 400 },
      });

      const boxAfter = await nodes.getNodeBounds({ name: DefaultNodeName.CALL_ACTIVITY });
      expect(boxAfter.x).not.toBe(callActivityBox.x);
      expect(boxAfter.y).not.toBe(callActivityBox.y);
    });

    test("should rename call activity", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });
      await nodes.rename({ current: DefaultNodeName.CALL_ACTIVITY, new: "Invoke Subprocess" });
      await expect(nodes.get({ name: "Invoke Subprocess" })).toBeAttached();

      const callActivity = (await jsonModel.getCallActivities())[0];
      expect(callActivity.__$$element).toBe("callActivity");
      expect(callActivity["@_name"]).toBe("Invoke Subprocess");
    });
  });

  test.describe("Call Activity default values", () => {
    test("should have default values", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });

      const callActivity = (await jsonModel.getCallActivities())[0];
      expect(callActivity.__$$element).toBe("callActivity");
      expect(callActivity["@_name"]).toBe(DefaultNodeName.CALL_ACTIVITY);
      expect(callActivity["@_drools:independent"]).toBe(false);
      expect(callActivity["@_drools:waitForCompletion"]).toBe(true);
      expect(callActivity.extensionElements?.["drools:metaData"]?.length).toBe(3);
      expect(callActivity.extensionElements?.["drools:metaData"]?.[0]?.["@_name"]).toBe("customAbortParent");
      expect(callActivity.extensionElements?.["drools:metaData"]?.[0]?.["drools:metaValue"].__$$text).toBe("true");
      expect(callActivity.extensionElements?.["drools:metaData"]?.[1]?.["@_name"]).toBe("customAsync");
      expect(callActivity.extensionElements?.["drools:metaData"]?.[1]?.["drools:metaValue"].__$$text).toBe("false");
      expect(callActivity.extensionElements?.["drools:metaData"]?.[2]?.["@_name"]).toBe("customAutoStart");
      expect(callActivity.extensionElements?.["drools:metaData"]?.[2]?.["drools:metaValue"].__$$text).toBe("false");
    });

    test("morphing away should remove default values", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.CALL_ACTIVITY }), to: TaskNodeType.TASK });

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe(DefaultNodeName.CALL_ACTIVITY);
      // as any is required to check for undefined
      expect((task as any)["@_drools:independent"]).toBe(undefined);
      expect((task as any)["@_drools:waitForCompletion"]).toBe(undefined);
      expect(task.extensionElements?.["drools:metaData"]?.length).toBe(undefined);
    });
  });
});
