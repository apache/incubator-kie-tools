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
import { DefaultNodeName, NodeType, NodePosition, TaskNodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Task", () => {
  test.describe("Add from palette", () => {
    test("should add Task node from palette", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });
      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();

      const task = (await jsonModel.getTasks())[0];
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
      { morphType: TaskNodeType.USER, expectedElement: "userTask", screenshot: "morph-task-to-user-task.png" },
      { morphType: TaskNodeType.SERVICE, expectedElement: "serviceTask", screenshot: "morph-task-to-service-task.png" },
      { morphType: TaskNodeType.SCRIPT, expectedElement: "scriptTask", screenshot: "morph-task-to-script-task.png" },
      {
        morphType: TaskNodeType.BUSINESS_RULE,
        expectedElement: "businessRuleTask",
        screenshot: "morph-task-to-business-rule-task.png",
      },
    ];

    for (const { morphType, expectedElement, screenshot } of singleMorphCases) {
      test(`should morph Task to ${morphType}`, async ({ jsonModel, palette, nodes, diagram, page }) => {
        await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
        await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
        await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: morphType });
        await expect(diagram.get()).toHaveScreenshot(screenshot);

        const process = await jsonModel.getProcess();
        const result = process?.flowElement?.[0];
        expect(result?.__$$element).toBe(expectedElement);
        expect(result?.["@_name"]).toBe(DefaultNodeName.TASK);
      });
    }

    test("should morph User Task to Service Task", async ({ jsonModel, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.USER });
      await nodes.hideNodeHandles();
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.SERVICE });

      const serviceTask = (await jsonModel.getServiceTasks())[0];
      expect(serviceTask.__$$element).toBe("serviceTask");
      expect(serviceTask["@_name"]).toBe(DefaultNodeName.TASK);
    });

    test("should morph Script Task back to generic Task", async ({ jsonModel, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.SCRIPT });
      await nodes.hideNodeHandles();
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.TASK });

      const genericTask = (await jsonModel.getTasks())[0];
      expect(genericTask.__$$element).toBe("task");
      expect(genericTask["@_name"]).toBe(DefaultNodeName.TASK);
    });

    test("should have default values - task", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe(DefaultNodeName.TASK);
      expect(task.extensionElements?.["drools:metaData"]?.length).toBe(undefined);
      expect(task.ioSpecification).toBe(undefined);
    });

    test("should have default values - userTask", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.USER });

      const userTask = (await jsonModel.getUserTasks())[0];
      expect(userTask.__$$element).toBe("userTask");
      expect(userTask["@_name"]).toBe(DefaultNodeName.TASK);
      expect(userTask.extensionElements?.["drools:metaData"]?.length).toBe(2);
      expect(userTask.extensionElements?.["drools:metaData"]?.[0]?.["@_name"]).toBe("customAsync");
      expect(userTask.extensionElements?.["drools:metaData"]?.[0]?.["drools:metaValue"].__$$text).toBe("false");
      expect(userTask.extensionElements?.["drools:metaData"]?.[1]?.["@_name"]).toBe("customAutoStart");
      expect(userTask.extensionElements?.["drools:metaData"]?.[1]?.["drools:metaValue"].__$$text).toBe("false");
      expect(userTask.ioSpecification).toBeDefined();
    });

    test("should have default values - serviceTask", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.SERVICE });

      const serviceTask = (await jsonModel.getServiceTasks())[0];
      expect(serviceTask.__$$element).toBe("serviceTask");
      expect(serviceTask["@_name"]).toBe(DefaultNodeName.TASK);
      expect(serviceTask.extensionElements?.["drools:metaData"]?.length).toBe(2);
      expect(serviceTask.extensionElements?.["drools:metaData"]?.[0]?.["@_name"]).toBe("customAsync");
      expect(serviceTask.extensionElements?.["drools:metaData"]?.[0]?.["drools:metaValue"].__$$text).toBe("false");
      expect(serviceTask.extensionElements?.["drools:metaData"]?.[1]?.["@_name"]).toBe("customAutoStart");
      expect(serviceTask.extensionElements?.["drools:metaData"]?.[1]?.["drools:metaValue"].__$$text).toBe("false");
      expect(serviceTask.ioSpecification).toBeDefined();
    });

    test("should have default values - scriptTask", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.SCRIPT });

      const scriptTask = (await jsonModel.getScriptTasks())[0];
      expect(scriptTask.__$$element).toBe("scriptTask");
      expect(scriptTask["@_name"]).toBe(DefaultNodeName.TASK);
      expect(scriptTask.extensionElements?.["drools:metaData"]?.length).toBe(2);
      expect(scriptTask.extensionElements?.["drools:metaData"]?.[0]?.["@_name"]).toBe("customAsync");
      expect(scriptTask.extensionElements?.["drools:metaData"]?.[0]?.["drools:metaValue"].__$$text).toBe("false");
      expect(scriptTask.extensionElements?.["drools:metaData"]?.[1]?.["@_name"]).toBe("customAutoStart");
      expect(scriptTask.extensionElements?.["drools:metaData"]?.[1]?.["drools:metaValue"].__$$text).toBe("false");
      expect(scriptTask.ioSpecification).toBe(undefined);
    });

    test("should have default values - businessRuleTask", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.BUSINESS_RULE });

      const businessRuleTask = (await jsonModel.getBusinessRuleTasks())[0];
      expect(businessRuleTask.__$$element).toBe("businessRuleTask");
      expect(businessRuleTask["@_name"]).toBe(DefaultNodeName.TASK);
      expect(businessRuleTask.extensionElements?.["drools:metaData"]?.length).toBe(2);
      expect(businessRuleTask.extensionElements?.["drools:metaData"]?.[0]?.["@_name"]).toBe("customAsync");
      expect(businessRuleTask.extensionElements?.["drools:metaData"]?.[0]?.["drools:metaValue"].__$$text).toBe("false");
      expect(businessRuleTask.extensionElements?.["drools:metaData"]?.[1]?.["@_name"]).toBe("customAutoStart");
      expect(businessRuleTask.extensionElements?.["drools:metaData"]?.[1]?.["drools:metaValue"].__$$text).toBe("false");
      expect(businessRuleTask.ioSpecification).toBeDefined();
    });

    test("should remove default values after morphing away - userTask", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.USER });
      await nodes.hideNodeHandles();
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.TASK });

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe(DefaultNodeName.TASK);
      expect(task.extensionElements?.["drools:metaData"]?.length).toBe(undefined);
      expect(task.ioSpecification).toBe(undefined);
    });

    test("should remove default values after morphing away - serviceTask", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.SERVICE });
      await nodes.hideNodeHandles();
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.TASK });

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe(DefaultNodeName.TASK);
      expect(task.extensionElements?.["drools:metaData"]?.length).toBe(undefined);
      expect(task.ioSpecification).toBe(undefined);
    });

    test("should remove default values after morphing away - scriptTask", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.SCRIPT });
      await nodes.hideNodeHandles();
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.TASK });

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe(DefaultNodeName.TASK);
      expect(task.extensionElements?.["drools:metaData"]?.length).toBe(undefined);
      expect(task.ioSpecification).toBe(undefined);
    });

    test("should remove default values after morphing away - businessRuleTask", async ({
      palette,
      nodes,
      jsonModel,
    }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.select({ name: DefaultNodeName.TASK, position: NodePosition.CENTER });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.BUSINESS_RULE });
      await nodes.hideNodeHandles();
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.TASK });

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe(DefaultNodeName.TASK);
      expect(task.extensionElements?.["drools:metaData"]?.length).toBe(undefined);
      expect(task.ioSpecification).toBe(undefined);
    });
  });

  test.describe("Add connected Task node", () => {
    test("should add connected Task from Start Event", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: startEventId,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should add connected Task from Gateway", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: gatewayId,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should add connected Task from another Task", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

      const task = await nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();

      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: DefaultNodeName.TASK,
        targetPosition: { x: 300, y: 100 },
      });

      const secondTask = (await nodes.get({ name: DefaultNodeName.TASK })).nth(1);
      await expect(secondTask).toBeAttached();
    });

    test("should create sequence flow from Task to End Event", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const task = await nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();

      const endEvent = nodes.getByType(NodeType.END_EVENT);
      await expect(endEvent).toBeVisible();
      const endEventId = await nodes.getIdByType(NodeType.END_EVENT);
      expect(endEventId).not.toBe("");

      await nodes.createSequenceFlow({
        from: DefaultNodeName.TASK,
        to: endEventId,
      });

      const edge = await edges.get({ from: DefaultNodeName.TASK, to: endEventId });
      await expect(edge).toBeAttached();
    });

    test("should create sequence flow from Task to Gateway", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 350, y: 100 } });

      const task = await nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();
      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      expect(gatewayId).not.toBe("");

      await nodes.createSequenceFlow({
        from: DefaultNodeName.TASK,
        to: gatewayId,
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
      expect(process?.flowElement?.length).toBe(0);
    });

    test("should move task to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });

      const task = nodes.getByType(NodeType.TASK);
      await expect(task).toBeAttached();
      await task.scrollIntoViewIfNeeded();

      const taskBox = await nodes.getNodeBounds({ name: DefaultNodeName.TASK });

      await nodes.dragNodeToPosition({
        name: DefaultNodeName.TASK,
        fromPosition: NodePosition.LEFT,
        toPosition: { x: 500, y: 400 },
      });

      const boxAfter = await nodes.getNodeBounds({ name: DefaultNodeName.TASK });
      expect(boxAfter.x).not.toBe(taskBox.x);
      expect(boxAfter.y).not.toBe(taskBox.y);
    });

    test("should rename task", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.rename({ current: DefaultNodeName.TASK, new: "Process Order" });
      await expect(nodes.get({ name: "Process Order" })).toBeAttached();

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
      expect(task["@_name"]).toBe("Process Order");
    });
  });
});
