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
      expect(task["@_drools:taskName"]).toBe("grpc-api-call-task");
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

  test.describe("Custom Task Data Mapping inputs", () => {
    test.describe("Built-in style Custom Task with reserved inputs (e.g. Rest, Milestone)", () => {
      test.beforeEach(async ({ customTasks, nodes, taskPropertiesPanel }) => {
        await customTasks.dragCustomTask({
          customTaskId: "reserved-inputs-task",
          targetPosition: { x: 300, y: 300 },
          thenRenameTo: "Reserved Task",
        });
        await expect(nodes.get({ name: "Reserved Task" })).toBeAttached();
        await nodes.select({ name: "Reserved Task", position: NodePosition.CENTER });
        await taskPropertiesPanel.open();
      });

      test("should hide reserved inputs from the Data Mapping modal and preserve them in the BPMN model", async ({
        taskPropertiesPanel,
        page,
        jsonModel,
      }) => {
        const getModelInputNames = () =>
          jsonModel.getTasks().then((t) => t[0].ioSpecification?.dataInput?.map((i) => i["@_name"]));

        // Initial task state contains reserved inputs ("Url", "HEADER_Accept") and regular input ("Message")
        expect(await getModelInputNames()).toEqual(["Url", "HEADER_Accept", "Message"]);

        await taskPropertiesPanel.openDataMappingModal();

        const modal = page.getByRole("dialog");
        const visibleNameInputs = modal.getByLabel("name");

        // Only the unreserved input ("Message") is shown in the modal
        await expect(visibleNameInputs).toHaveCount(1);
        await expect(visibleNameInputs.first()).toHaveValue("Message");

        // Reserved inputs ("Url", "HEADER_Accept") are not listed: the only name input is "Message" (asserted above)
        await expect(page.getByText("This input should be entered as a property for the task.")).toHaveCount(0);

        // Saving and closing the modal must keep reserved inputs untouched in the BPMN model
        await taskPropertiesPanel.closeDataMappingModal();
        expect((await getModelInputNames())?.sort()).toEqual(["HEADER_Accept", "Message", "Url"]);
      });

      test("should not save user-typed inputs whose names match reserved names/patterns", async ({
        taskPropertiesPanel,
        jsonModel,
        page,
      }) => {
        await taskPropertiesPanel.openDataMappingModal();
        await taskPropertiesPanel.addDataInputInModal({ name: "HEADER_Other" });

        // The reserved-name validation error is shown while the user types a reserved name/pattern
        await expect(page.getByText("This input should be entered as a property for the task.")).toBeVisible();

        await taskPropertiesPanel.closeDataMappingModal();

        const names = (await jsonModel.getTasks())[0].ioSpecification?.dataInput?.map((i) => i["@_name"]);
        expect(names?.filter((n) => n === "HEADER_Other")).toEqual([]);
        expect(names).toEqual(expect.arrayContaining(["Url", "HEADER_Accept", "Message"]));
      });
    });

    test.describe("WID-style Custom Task with unreserved inputs", () => {
      test("should display all parameters in the Data Mapping modal when no inputs are reserved", async ({
        customTasks,
        nodes,
        taskPropertiesPanel,
        page,
        jsonModel,
      }) => {
        await customTasks.dragCustomTask({
          customTaskId: "wid-like-task",
          targetPosition: { x: 300, y: 300 },
          thenRenameTo: "WID Task",
        });
        await expect(nodes.get({ name: "WID Task" })).toBeAttached();
        await nodes.select({ name: "WID Task", position: NodePosition.CENTER });
        await taskPropertiesPanel.open();

        const getModelInputNames = () =>
          jsonModel.getTasks().then((t) => t[0].ioSpecification?.dataInput?.map((i) => i["@_name"]));

        // Initial task model has all WID parameters
        expect(await getModelInputNames()).toEqual(["Param1", "Param2"]);

        await taskPropertiesPanel.openDataMappingModal();

        const modal = page.getByRole("dialog");
        const visibleNameInputs = modal.getByLabel("name");

        // All parameters are visible and editable in the modal
        await expect(visibleNameInputs).toHaveCount(2);
        await expect(visibleNameInputs.nth(0)).toHaveValue("Param1");
        await expect(visibleNameInputs.nth(1)).toHaveValue("Param2");

        // Adding an extra input in the modal should also succeed
        await taskPropertiesPanel.addDataInputInModal({ name: "Param3" });
        await taskPropertiesPanel.closeDataMappingModal();

        expect((await getModelInputNames())?.sort()).toEqual(["Param1", "Param2", "Param3"]);
      });
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
      jsonModel,
      edges,
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

      const restApiTaskId = await nodes.getId({ name: "Rest API call Task Flow" });
      const sequenceFlows = await jsonModel.getSequenceFlows();
      expect(sequenceFlows.length).toBe(2);
      expect(sequenceFlows[0]["@_sourceRef"]).toBe(startEventId);
      expect(sequenceFlows[0]["@_targetRef"]).toBe(restApiTaskId);
      await expect(edges.getByIds({ from: startEventId, to: restApiTaskId })).toBeVisible();
      expect(sequenceFlows[1]["@_sourceRef"]).toBe(restApiTaskId);
      expect(sequenceFlows[1]["@_targetRef"]).toBe(endEventId);
      await expect(edges.getByIds({ from: restApiTaskId, to: endEventId })).toBeVisible();
    });
  });
});
