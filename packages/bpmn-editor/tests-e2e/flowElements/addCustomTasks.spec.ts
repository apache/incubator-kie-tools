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

import { test, expect } from "../__fixtures__/base";
import { NodeType, NodePosition } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor, nodes }) => {
  await editor.openCustomTasks({ nodes });
});

test.describe("Add Custom Tasks", () => {
  test.describe("Custom Tasks Palette", () => {
    test("should display custom tasks button in palette", async ({ page }) => {
      const customTasksButton = page.getByTitle("Custom Tasks");
      await expect(customTasksButton).toBeVisible();
    });

    test("should open custom tasks palette on click", async ({ customTasks, page }) => {
      await customTasks.openPalette();

      const popover = page.getByTestId("kie-tools--bpmn-editor--custom-tasks-popover");
      await expect(popover).toBeVisible();
    });

    test("should close custom tasks palette on second click", async ({ customTasks, page }) => {
      await customTasks.openPalette();
      await customTasks.closePalette();

      const popover = page.getByTestId("kie-tools--bpmn-editor--custom-tasks-popover");
      await expect(popover).not.toBeVisible();
    });

    test("should list available custom tasks", async ({ customTasks }) => {
      const availableTasks = await customTasks.getAvailableCustomTasks();

      expect(availableTasks.length).toBeGreaterThan(0);
      expect(availableTasks).toContain("Rest API call Task");
      expect(availableTasks).toContain("gRPC API call Task");
    });
  });

  test.describe("Rest API call Task", () => {
    test("should add Rest API call Task from custom tasks palette", async ({ customTasks, nodes, jsonModel }) => {
      await customTasks.dragCustomTask({
        customTaskId: "rest-api-call-task",
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Rest API call Task New",
      });
      await expect(nodes.get({ name: "Rest API call Task New" })).toBeAttached();

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe("Rest API call Task New");
      expect(task["@_drools:taskName"]).toBe("rest-api-call-task");
    });

    test("should add two Rest API call Tasks from palette", async ({ customTasks, nodes, diagram }) => {
      await customTasks.dragCustomTask({
        customTaskId: "rest-api-call-task",
        targetPosition: { x: 200, y: 200 },
        thenRenameTo: "Rest API call Task A",
      });

      await customTasks.dragCustomTask({
        customTaskId: "rest-api-call-task",
        targetPosition: { x: 400, y: 300 },
        thenRenameTo: "Rest API call Task B",
      });

      await diagram.resetFocus();

      await expect(nodes.get({ name: "Rest API call Task A" })).toBeAttached();
      await expect(nodes.get({ name: "Rest API call Task B" })).toBeAttached();
    });
  });

  test.describe("gRPC API call Task", () => {
    test("should add gRPC API call Task from custom tasks palette", async ({ customTasks, nodes, jsonModel }) => {
      await customTasks.dragCustomTask({
        customTaskId: "grpc-api-call-task",
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "gRPC API call Task New",
      });
      await expect(nodes.get({ name: "gRPC API call Task New" })).toBeAttached();

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe("gRPC API call Task New");
      expect((task as any)["@_drools:taskName"]).toBe("grpc-api-call-task");
    });

    test("should add two gRPC API call Tasks from palette", async ({ customTasks, nodes, diagram }) => {
      await customTasks.dragCustomTask({
        customTaskId: "grpc-api-call-task",
        targetPosition: { x: 200, y: 200 },
        thenRenameTo: "gRPC API call Task A",
      });

      await customTasks.dragCustomTask({
        customTaskId: "grpc-api-call-task",
        targetPosition: { x: 400, y: 300 },
        thenRenameTo: "gRPC API call Task B",
      });

      await diagram.resetFocus();

      await expect(nodes.get({ name: "gRPC API call Task A" })).toBeAttached();
      await expect(nodes.get({ name: "gRPC API call Task B" })).toBeAttached();
    });
  });

  test("should rename custom task", async ({ customTasks, nodes }) => {
    await customTasks.dragCustomTask({
      customTaskId: "rest-api-call-task",
      targetPosition: { x: 300, y: 300 },
      thenRenameTo: "Fetch User Data",
    });

    await expect(nodes.get({ name: "Fetch User Data" })).toBeAttached();
  });

  test("should delete custom task", async ({ customTasks, nodes }) => {
    await customTasks.dragCustomTask({
      customTaskId: "grpc-api-call-task",
      targetPosition: { x: 300, y: 300 },
      thenRenameTo: "gRPC Task to Delete",
    });

    await nodes.delete({ name: "gRPC Task to Delete" });

    await expect(nodes.get({ name: "gRPC Task to Delete" })).not.toBeAttached();
  });

  test("should move custom task to new position", async ({ customTasks, nodes, diagram }) => {
    await customTasks.dragCustomTask({
      customTaskId: "rest-api-call-task",
      targetPosition: { x: 300, y: 300 },
      thenRenameTo: "Move Test Task",
    });

    await expect(nodes.getByType(NodeType.TASK).last()).toBeAttached();

    await nodes.getByType(NodeType.TASK).last().scrollIntoViewIfNeeded();

    const taskBox = await nodes.getNodeBounds({ name: "Move Test Task" });

    await nodes.dragNodeToPosition({
      name: "Move Test Task",
      fromPosition: NodePosition.LEFT,
      toPosition: { x: 500, y: 400 },
    });

    const boxAfter = await nodes.getNodeBounds({ name: "Move Test Task" });
    expect(boxAfter.x).not.toBe(taskBox.x);
    expect(boxAfter.y).not.toBe(taskBox.y);
  });

  test.describe("Custom Tasks in Process Flow", () => {
    test("should create process with Start Event, Rest API call Task, and End Event", async ({
      palette,
      customTasks,
      nodes,
      diagram,
    }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 200 } });
      await customTasks.dragCustomTask({
        customTaskId: "rest-api-call-task",
        targetPosition: { x: 300, y: 200 },
        thenRenameTo: "Rest API call Task Flow",
      });
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 500, y: 200 } });

      await expect(nodes.getByType(NodeType.START_EVENT)).toBeAttached();

      await expect(nodes.get({ name: "Rest API call Task Flow" })).toBeAttached();

      await expect(nodes.getByType(NodeType.END_EVENT)).toBeVisible();

      // Connect Start Event -> Rest API call Task
      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      await nodes.createSequenceFlow({ from: startEventId, to: "Rest API call Task Flow" });

      // Connect Rest API call Task -> End Event
      const endEventId = await nodes.getIdByType(NodeType.END_EVENT);
      await nodes.createSequenceFlow({ from: "Rest API call Task Flow", to: endEventId });

      await diagram.resetFocus();

      await expect(diagram.get()).toHaveScreenshot("rest-api-call-task-in-process-flow.png");
    });
  });
});
