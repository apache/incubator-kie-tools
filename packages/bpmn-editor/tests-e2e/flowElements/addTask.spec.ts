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
import { DefaultNodeName, NodeType, NodePosition } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Task", () => {
  test.describe("Add from palette", () => {
    test("should add Task node from palette", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();

      const task = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(task.__$$element).toBe("task");
    });

    test("should add two Task nodes from palette in a row", async ({ palette, nodes, diagram }) => {
      test.info().annotations.push({ type: TestAnnotations.REGRESSION });

      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 }, thenRenameTo: "Task A" });
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 }, thenRenameTo: "Task B" });

      await diagram.resetFocus();

      await expect(nodes.get({ name: "Task A" })).toBeAttached();
      await expect(nodes.get({ name: "Task B" })).toBeAttached();
    });
  });

  test.describe("Task type morphing", () => {
    const singleMorphCases = [
      { morphType: "User task", expectedElement: "userTask", screenshot: "morph-task-to-user-task.png" },
      { morphType: "Service task", expectedElement: "serviceTask", screenshot: "morph-task-to-service-task.png" },
      { morphType: "Script task", expectedElement: "scriptTask", screenshot: "morph-task-to-script-task.png" },
      {
        morphType: "Business Rule task",
        expectedElement: "businessRuleTask",
        screenshot: "morph-task-to-business-rule-task.png",
      },
    ];

    for (const { morphType, expectedElement, screenshot } of singleMorphCases) {
      test(`should morph Task to ${morphType}`, async ({ jsonModel, palette, nodes, diagram, page }) => {
        await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });

        await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });

        const task = await nodes.get({ name: DefaultNodeName.TASK });
        await nodes.morphNode({ nodeLocator: task, targetMorphType: morphType });

        const result = await jsonModel.getFlowElement({ elementIndex: 0 });
        expect(result.__$$element).toBe(expectedElement);
        expect(result["@_name"]).toBe(DefaultNodeName.TASK);

        await expect(diagram.get()).toHaveScreenshot(screenshot);
      });
    }

    test("should morph User Task to Service Task", async ({ jsonModel, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });

      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });

      const task = await nodes.get({ name: DefaultNodeName.TASK });
      await nodes.morphNode({ nodeLocator: task, targetMorphType: "User task" });

      expect((await jsonModel.getFlowElement({ elementIndex: 0 })).__$$element).toBe("userTask");

      await nodes.hideNodeHandles();

      await nodes.morphNode({ nodeLocator: task, targetMorphType: "Service task" });

      const taskElement = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(taskElement.__$$element).toBe("serviceTask");
      expect(taskElement["@_name"]).toBe(DefaultNodeName.TASK);
    });

    test("should morph Script Task back to generic Task", async ({ jsonModel, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });

      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });

      const task = await nodes.get({ name: DefaultNodeName.TASK });
      await nodes.morphNode({ nodeLocator: task, targetMorphType: "Script task" });

      expect((await jsonModel.getFlowElement({ elementIndex: 0 })).__$$element).toBe("scriptTask");

      await nodes.hideNodeHandles();

      await nodes.morphNode({ nodeLocator: task, targetMorphType: "Task", exact: true });

      const taskElement = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(taskElement.__$$element).toBe("task");
      expect(taskElement["@_name"]).toBe(DefaultNodeName.TASK);
    });
  });

  test.describe("Add connected Task node", () => {
    test("should add connected Task from Start Event", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.START_EVENT) });

      const addTaskHandle = startEvent.getByTitle("Add Task");
      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should add connected Task from Gateway", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.GATEWAY) });

      const addTaskHandle = gateway.getByTitle("Add Task");
      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should add connected Task from another Task", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

      const task = await nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      await nodes.showNodeHandles({ name: "New Task" });

      const addTaskHandle = task.getByTitle("Add Task");
      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      const secondTask = (await nodes.get({ name: "New Task" })).nth(1);
      await expect(secondTask).toBeAttached();
    });

    test("should create sequence flow from Task to End Event", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const task = await nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      const endEvent = nodes.getByType(NodeType.END_EVENT);
      await expect(endEvent).toBeVisible();
      const endEventId = (await endEvent.getAttribute("data-nodehref")) ?? "";

      await nodes.showNodeHandles({ name: "New Task" });

      const addSequenceFlowHandle = task.getByTitle("Add Sequence Flow");

      const endEventBox = await nodes.getNodeBounds({ id: endEventId });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: endEventBox.x + endEventBox.width / 2, y: endEventBox.y + endEventBox.height / 2 },
      });

      const edge = await edges.get({ from: DefaultNodeName.TASK, to: endEventId });
      await expect(edge).toBeAttached();
    });

    test("should create sequence flow from Task to Gateway", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 350, y: 100 } });

      const task = await nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();
      const gatewayId = (await gateway.getAttribute("data-nodehref")) ?? "";

      await nodes.showNodeHandles({ name: "New Task" });

      const addSequenceFlowHandle = task.getByTitle("Add Sequence Flow");

      const gatewayBox = await nodes.getNodeBounds({ id: gatewayId });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: gatewayBox.x + gatewayBox.width / 2, y: gatewayBox.y + gatewayBox.height / 2 },
      });

      const edge = await edges.get({ from: DefaultNodeName.TASK, to: gatewayId });
      await expect(edge).toBeAttached();
    });
  });

  test.describe("Task operations", () => {
    test("should delete task", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.delete({ name: DefaultNodeName.TASK });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move task to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });

      const task = nodes.getByType(NodeType.TASK);
      await expect(task).toBeAttached();
      await task.scrollIntoViewIfNeeded();

      const taskBox = await nodes.getNodeBounds({ name: DefaultNodeName.TASK });

      await task.dragTo(diagram.get(), {
        sourcePosition: { x: 20, y: taskBox.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await nodes.getNodeBounds({ name: DefaultNodeName.TASK });
      expect(boxAfter.x).not.toBe(taskBox.x);
      expect(boxAfter.y).not.toBe(taskBox.y);
    });

    test("should rename task", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.rename({ current: DefaultNodeName.TASK, new: "Process Order" });

      await expect(nodes.get({ name: "Process Order" })).toBeAttached();

      const task = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe("Process Order");
    });
  });
});
