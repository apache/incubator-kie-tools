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
import { NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Change Properties - Sequence Flow", () => {
  test.beforeEach(async ({ palette, nodes, edges, sequenceFlowPropertiesPanel, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 }, thenRenameTo: "Task A" });
    await diagram.resetFocus();
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 400, y: 400 }, thenRenameTo: "Task B" });
    await diagram.resetFocus();

    const taskA = nodes.get({ name: "Task A" });
    const taskB = nodes.get({ name: "Task B" });

    await taskA.scrollIntoViewIfNeeded();
    await taskB.scrollIntoViewIfNeeded();

    const box = await taskA.boundingBox();
    expect(box).not.toBeNull();

    await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

    const addSequenceFlowHandle = taskA.getByTitle("Add Sequence Flow");
    const taskBBox = await taskB.boundingBox();
    expect(taskBBox).not.toBeNull();

    await addSequenceFlowHandle.dragTo(diagram.get(), {
      targetPosition: { x: taskBBox!.x + taskBBox!.width / 2, y: taskBBox!.y + taskBBox!.height / 2 },
    });

    const edge = await edges.get({ from: "Task A", to: "Task B" });
    await edge.scrollIntoViewIfNeeded();
    await edge.click({ force: true });

    await sequenceFlowPropertiesPanel.open();
  });

  test("should change the Sequence Flow name", async ({ sequenceFlowPropertiesPanel }) => {
    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "Normal Flow" });

    expect(await sequenceFlowPropertiesPanel.nameProperties.getName()).toBe("Normal Flow");
  });

  test("should change the Sequence Flow documentation", async ({ sequenceFlowPropertiesPanel }) => {
    await sequenceFlowPropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "This flow connects Task A to Task B",
    });

    expect(await sequenceFlowPropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "This flow connects Task A to Task B"
    );
  });

  test("should configure conditional expression", async ({ sequenceFlowPropertiesPanel }) => {
    await sequenceFlowPropertiesPanel.setConditionExpression({ expression: "${amount > 1000}" });

    expect(await sequenceFlowPropertiesPanel.getConditionExpression()).toBe("${amount > 1000}");
  });

  test("should set priority", async ({ sequenceFlowPropertiesPanel }) => {
    await sequenceFlowPropertiesPanel.setPriority({ priority: "1" });

    expect(await sequenceFlowPropertiesPanel.getPriority()).toBe("1");
  });
});

test.describe("Change Properties - Conditional Sequence Flow from Gateway", () => {
  test.beforeEach(async ({ palette, nodes, edges, sequenceFlowPropertiesPanel, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 250 } });

    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 50 }, thenRenameTo: "High Amount" });

    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 400 }, thenRenameTo: "Low Amount" });

    const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
    await expect(gateway).toBeVisible();
    const gatewayId = (await gateway.getAttribute("data-nodehref")) ?? "";

    const highAmountTask = nodes.get({ name: "High Amount" });
    await expect(highAmountTask).toBeAttached();

    const lowAmountTask = nodes.get({ name: "Low Amount" });
    await expect(lowAmountTask).toBeAttached();

    let box = await gateway.boundingBox();
    expect(box).not.toBeNull();
    await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

    const addTaskHandle1 = gateway.getByTitle("Add Sequence Flow");
    await expect(addTaskHandle1).toBeVisible();

    let taskBox = await highAmountTask.boundingBox();
    expect(taskBox).not.toBeNull();

    await addTaskHandle1.dragTo(diagram.get(), {
      targetPosition: { x: taskBox!.x + taskBox!.width / 2, y: taskBox!.y + taskBox!.height / 2 },
    });

    box = await gateway.boundingBox();
    expect(box).not.toBeNull();
    await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

    const addTaskHandle2 = gateway.getByTitle("Add Sequence Flow");
    await expect(addTaskHandle2).toBeVisible();

    taskBox = await lowAmountTask.boundingBox();
    expect(taskBox).not.toBeNull();

    await addTaskHandle2.dragTo(diagram.get(), {
      targetPosition: { x: taskBox!.x + taskBox!.width / 2, y: taskBox!.y + taskBox!.height / 2 },
    });

    const edge = await edges.get({ from: gatewayId, to: "High Amount" });
    await edge.scrollIntoViewIfNeeded();
    await edge.click({ force: true });
    await sequenceFlowPropertiesPanel.open();
  });

  test("should configure conditional flow from gateway", async ({
    edges,
    sequenceFlowPropertiesPanel,
    diagram,
    nodes,
    page,
  }) => {
    const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
    const gatewayId = (await gateway.getAttribute("data-nodehref")) ?? "";

    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "High Amount Path" });
    await sequenceFlowPropertiesPanel.setConditionExpression({ expression: "${amount > 5000}" });

    const edge = await edges.get({ from: gatewayId, to: "High Amount" });
    await edge.click();

    await expect(diagram.get()).toHaveScreenshot("gateway-conditional-flow-high.png");
  });

  test("should configure multiple conditional flows", async ({
    edges,
    sequenceFlowPropertiesPanel,
    diagram,
    nodes,
    page,
  }) => {
    const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
    const gatewayId = (await gateway.getAttribute("data-nodehref")) ?? "";

    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "High Amount" });
    await sequenceFlowPropertiesPanel.setConditionExpression({ expression: "${amount > 5000}" });

    const edge2 = await edges.get({ from: gatewayId, to: "Low Amount" });
    await edge2.click();
    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "Low Amount" });
    await sequenceFlowPropertiesPanel.setConditionExpression({ expression: "${amount <= 5000}" });

    await expect(diagram.get()).toHaveScreenshot("gateway-multiple-conditional-flows.png");
  });
});

test.describe("Change Properties - Default Sequence Flow", () => {
  test.beforeEach(async ({ palette, nodes, edges, sequenceFlowPropertiesPanel, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 250 } });

    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 50 }, thenRenameTo: "Condition A" });

    await palette.dragNewNode({
      type: NodeType.TASK,
      targetPosition: { x: 300, y: 400 },
      thenRenameTo: "Default Path",
    });

    const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
    await expect(gateway).toBeVisible();

    const conditionATask = nodes.get({ name: "Condition A" });
    await expect(conditionATask).toBeAttached();

    const defaultPathTask = nodes.get({ name: "Default Path" });
    await expect(defaultPathTask).toBeAttached();

    let box = await gateway.boundingBox();
    expect(box).not.toBeNull();
    await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

    const addTaskHandle1 = gateway.getByTitle("Add Sequence Flow");
    await expect(addTaskHandle1).toBeVisible();

    let taskBox = await conditionATask.boundingBox();
    expect(taskBox).not.toBeNull();

    await addTaskHandle1.dragTo(diagram.get(), {
      targetPosition: { x: taskBox!.x + taskBox!.width / 2, y: taskBox!.y + taskBox!.height / 2 },
    });

    box = await gateway.boundingBox();
    expect(box).not.toBeNull();
    await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

    const addTaskHandle2 = gateway.getByTitle("Add Sequence Flow");
    await expect(addTaskHandle2).toBeVisible();

    taskBox = await defaultPathTask.boundingBox();
    expect(taskBox).not.toBeNull();

    await addTaskHandle2.dragTo(diagram.get(), {
      targetPosition: { x: taskBox!.x + taskBox!.width / 2, y: taskBox!.y + taskBox!.height / 2 },
    });
  });

  test("should configure default flow", async ({ edges, sequenceFlowPropertiesPanel, diagram, nodes, page }) => {
    const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
    const gatewayId = (await gateway.getAttribute("data-nodehref")) ?? "";

    const edge1 = await edges.get({ from: gatewayId, to: "Condition A" });
    await edge1.click();
    await sequenceFlowPropertiesPanel.setConditionExpression({ expression: "${approved == true}" });

    const edge2 = await edges.get({ from: gatewayId, to: "Default Path" });
    await edge2.click();
    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "Default" });

    await expect(diagram.get()).toHaveScreenshot("gateway-default-flow.png");
  });
});
