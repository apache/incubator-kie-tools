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

test.describe("Add node - Sub-process", () => {
  test.describe("Add from palette", () => {
    test("should add Sub-process node from palette", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 100 } });

      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      const subProcess = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(subProcess.__$$element).toBe("subProcess");
    });

    test("should add two Sub-process nodes from palette in a row", async ({ palette, nodes, diagram }) => {
      test.info().annotations.push({ type: TestAnnotations.REGRESSION });

      await palette.dragNewNode({
        type: NodeType.SUB_PROCESS,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Sub-process A",
      });

      await palette.dragNewNode({
        type: NodeType.SUB_PROCESS,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: "Sub-process B",
      });

      await diagram.resetFocus();

      await expect(nodes.get({ name: "Sub-process A" })).toBeAttached();
      await expect(nodes.get({ name: "Sub-process B" })).toBeAttached();
    });
  });

  test.describe("Add connected Sub-process node", () => {
    test("should add connected Task from Sub-process", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 100 } });

      const subProcess = await nodes.get({ name: "New Sub-process" });
      await expect(subProcess).toBeAttached();

      await nodes.showNodeHandles({ name: "New Sub-process" });

      const addTaskHandle = subProcess.getByTitle("Add Task");
      await expect(addTaskHandle).toBeVisible();

      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 600, y: 100 } });

      await diagram.zoomOut({ clicks: 1 });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should create sequence flow from Start Event to Sub-process", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();

      const subProcess = await nodes.get({ name: "New Sub-process" });
      await expect(subProcess).toBeAttached();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.START_EVENT) });

      const addSequenceFlowHandle = startEvent.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const subProcessBox = await nodes.getNodeBounds({ name: "New Sub-process" });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: {
          x: subProcessBox.x + subProcessBox.width / 2,
          y: subProcessBox.y + subProcessBox.height / 2,
        },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-start-event-to-subprocess.png");
    });

    test("should create sequence flow from Gateway to Sub-process", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      const subProcess = await nodes.get({ name: "New Sub-process" });
      await expect(subProcess).toBeAttached();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.GATEWAY) });

      const addSequenceFlowHandle = gateway.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const subProcessBox = await nodes.getNodeBounds({ name: "New Sub-process" });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: {
          x: subProcessBox.x + subProcessBox.width / 2,
          y: subProcessBox.y + subProcessBox.height / 2,
        },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-gateway-to-subprocess.png");
    });
  });

  test.describe("Sub-process morphing", () => {
    const morphTestCases = [
      { title: "Event", screenshot: "morph-subprocess-to-event.png" },
      { title: "Multi-instance", screenshot: "morph-subprocess-to-multi-instance.png" },
      { title: "Ad-hoc", screenshot: "morph-subprocess-to-adhoc.png" },
    ];

    for (const { title, screenshot } of morphTestCases) {
      test(`should morph Sub-process to ${title} Sub-process`, async ({ palette, nodes, page, diagram }) => {
        await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

        const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
        await expect(subProcess).toBeAttached();

        await nodes.morphNode({ nodeLocator: subProcess, targetMorphType: title });

        await expect(diagram.get()).toHaveScreenshot(screenshot);
      });
    }
  });

  test.describe("Nested elements inside Sub-process", () => {
    test("should add Start Event inside Sub-process", async ({ palette, nodes, diagram }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
      await expect(subProcess).toBeAttached();

      const box = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await palette.dragNewNode({
        type: NodeType.START_EVENT,
        targetPosition: { x: box.x + 50, y: box.y + box.height + 50 },
      });

      await expect(diagram.get()).toHaveScreenshot("add-start-event-inside-subprocess.png");
    });

    test("should add Task inside Sub-process", async ({ palette, nodes, diagram }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
      await expect(subProcess).toBeAttached();

      const box = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: box.x + 50, y: box.y + 50 } });

      await expect(diagram.get()).toHaveScreenshot("add-task-inside-subprocess.png");
    });

    test("should add Gateway inside Sub-process", async ({ palette, nodes, diagram }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
      await expect(subProcess).toBeAttached();

      const box = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await palette.dragNewNode({
        type: NodeType.GATEWAY,
        targetPosition: { x: box.x + box.width / 4, y: box.y + 100 },
      });

      await expect(diagram.get()).toHaveScreenshot("add-gateway-inside-subprocess.png");
    });

    test("should create complete flow inside Sub-process: Start Event -> Task -> End Event", async ({
      palette,
      nodes,
      diagram,
    }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
      await expect(subProcess).toBeAttached();

      const box = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await palette.dragNewNode({
        type: NodeType.START_EVENT,
        targetPosition: { x: box.x + box.width / 8, y: box.y + 100 },
      });
      await palette.dragNewNode({
        type: NodeType.TASK,
        targetPosition: { x: box.x + box.width / 3, y: box.y + 70 },
      });
      await palette.dragNewNode({
        type: NodeType.END_EVENT,
        targetPosition: { x: box.x + box.width - 100, y: box.y + 100 },
      });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();

      const task = await nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      const endEvent = nodes.getByType(NodeType.END_EVENT);
      await expect(endEvent).toBeVisible();

      // Connect Start Event -> Task
      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.START_EVENT) });
      const handle1 = startEvent.getByTitle("Add Sequence Flow");
      await expect(handle1).toBeVisible();
      const taskBox = await nodes.getNodeBounds({ name: "New Task" });
      await handle1.dragTo(diagram.get(), {
        targetPosition: { x: taskBox.x + taskBox.width / 2, y: taskBox.y + taskBox.height / 2 },
      });

      // Connect Task -> End Event
      await nodes.showNodeHandles({ name: "New Task" });
      const handle2 = task.getByTitle("Add Sequence Flow");
      await expect(handle2).toBeVisible();
      const endBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.END_EVENT) });
      await handle2.dragTo(diagram.get(), {
        targetPosition: { x: endBox.x + endBox.width / 2, y: endBox.y + endBox.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("complete-flow-inside-subprocess.png");
    });
  });

  test.describe("Sub-process operations", () => {
    test("should delete sub-process", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await nodes.delete({ name: DefaultNodeName.SUB_PROCESS });

      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move sub-process to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.getByType(NodeType.SUB_PROCESS);
      await expect(subProcess).toBeAttached();
      await subProcess.scrollIntoViewIfNeeded();

      const subProcessBox = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await subProcess.dragTo(diagram.get(), {
        sourcePosition: { x: 20, y: subProcessBox.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });
      expect(boxAfter.x).not.toBe(subProcessBox.x);
      expect(boxAfter.y).not.toBe(subProcessBox.y);
    });

    test("should rename sub-process", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await nodes.rename({ current: DefaultNodeName.SUB_PROCESS, new: "Order Processing" });

      await expect(nodes.get({ name: "Order Processing" })).toBeAttached();

      const subProcess = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(subProcess.__$$element).toBe("subProcess");
      expect(subProcess["@_name"]).toBe("Order Processing");
    });
  });
});
