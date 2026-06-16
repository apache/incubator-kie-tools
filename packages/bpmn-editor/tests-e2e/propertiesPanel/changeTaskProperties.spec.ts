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
import { DefaultNodeName, NodeType, TaskNodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor, page }) => {
  await page.setViewportSize({ width: 1920, height: 1080 });
  await editor.open();
  await editor.setInitialProcessId();
});

test.describe("Change Properties - Task Node", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
  });

  test("should change the Task name", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.nameProperties.setName({ newName: "Process Order" });

    expect(await taskPropertiesPanel.nameProperties.getName()).toBe("Process Order");
    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("task-name-changed.png");
  });

  test("should change the Task documentation", async ({ taskPropertiesPanel }) => {
    await taskPropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "This task processes customer orders",
    });

    expect(await taskPropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "This task processes customer orders"
    );
  });
});

test.describe("Change Properties - User Task", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();

    await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.USER });
  });

  test("should configure User Task actors", async ({ taskPropertiesPanel }) => {
    await taskPropertiesPanel.setActors({ actors: "john, mary, admin" });

    expect(await taskPropertiesPanel.getActors()).toBe("john,mary,admin");
  });

  test("should configure User Task groups", async ({ taskPropertiesPanel }) => {
    await taskPropertiesPanel.setGroups({ groups: "managers, supervisors" });

    expect(await taskPropertiesPanel.getGroups()).toBe("managers, supervisors");
  });

  test("should configure User Task name", async ({ taskPropertiesPanel }) => {
    await taskPropertiesPanel.setTaskName({ taskName: "ApproveOrder" });

    expect(await taskPropertiesPanel.getTaskName()).toBe("ApproveOrder");
  });

  test("should configure User Task with actors and groups", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.setActors({ actors: "john, mary" });
    await taskPropertiesPanel.setGroups({ groups: "managers" });
    await taskPropertiesPanel.setTaskName({ taskName: "ReviewDocument" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("user-task-full-configuration.png");
  });

  test("should set async flag on User Task", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.setAsync({ isAsync: true });

    const asyncCheckbox = page.getByRole("checkbox", { name: /async/i });
    await expect(asyncCheckbox).toBeChecked();
  });
});

test.describe("Change Properties - Service Task", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();

    await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.SERVICE });
  });

  test("should configure Service Task implementation", async ({ taskPropertiesPanel }) => {
    await taskPropertiesPanel.setImplementation({ implementation: "Java" });

    expect(await taskPropertiesPanel.getImplementation()).toBe("Java");
  });

  test("should configure Service Task interface and operation", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.setInterface({ interfaceName: "OrderService" });
    await taskPropertiesPanel.setOperation({ operationName: "processOrder" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot(
      "service-task-interface-operation.png"
    );
  });
});

test.describe("Change Properties - Script Task", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();

    await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.SCRIPT });
  });

  test("should configure Script Task script content", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.setScript({
      script: 'System.out.println("Processing order: " + orderId);',
    });

    const scriptTextarea = page.getByPlaceholder(/Enter code/i);
    await expect(scriptTextarea).toHaveValue('System.out.println("Processing order: " + orderId);');
  });
});

test.describe("Change Properties - Business Rule Task", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();

    await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.BUSINESS_RULE });
  });

  test("should configure Business Rule Task with DRL rule flow group", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.setRuleFlowGroup({ ruleFlowGroup: "order-validation-rules" });

    const ruleFlowInput = page.getByPlaceholder(/Enter a Rule flow group/i);
    await expect(ruleFlowInput).toHaveValue("order-validation-rules");
  });

  test("should configure Business Rule Task with DMN model", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.setDmnModel({
      relativePath: "models/decision.dmn",
      namespace: "https://example.com/dmn",
      modelName: "OrderDecision",
    });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("business-rule-task-dmn-model.png");
  });
});

test.describe("Change Properties - Task Multi-Instance", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.addProcessVariable({ name: "orderItems", dataType: "Object" });
    await palette.addProcessVariable({ name: "approvers", dataType: "Object" });
    await palette.addProcessVariable({ name: "tasks", dataType: "Object" });
    await palette.closeProcessVariables();

    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();

    await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.USER });
  });

  test("should configure parallel multi-instance", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.setMultiInstance({ type: "parallel" });
    await taskPropertiesPanel.setCollectionExpression({ expression: "orderItems" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("task-multi-instance-parallel.png");
  });

  test("should configure sequential multi-instance", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.setMultiInstance({ type: "sequential" });
    await taskPropertiesPanel.setCollectionExpression({ expression: "approvers" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot(
      "task-multi-instance-sequential.png"
    );
  });

  test("should configure multi-instance with completion condition", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.setMultiInstance({ type: "parallel" });
    await taskPropertiesPanel.setCollectionExpression({ expression: "tasks" });
    await taskPropertiesPanel.setCompletionCondition({ condition: "${nrOfCompletedInstances >= 3}" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot(
      "task-multi-instance-completion-condition.png"
    );
  });
});

test.describe("Change Properties - Task Data I/O", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();

    await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.USER });
  });

  test("should add data input to Task", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.addDataInput({ name: "orderId" });

    const dataMappingSection = page.getByLabel(/⇆Data mapping/i);
    await expect(dataMappingSection).toBeVisible();
  });

  test("should add data output to Task", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.addDataOutput({ name: "result" });

    const dataMappingSection = page.getByLabel(/⇆Data mapping/i);
    await expect(dataMappingSection).toBeVisible();
  });

  test("should add multiple data inputs and outputs", async ({ taskPropertiesPanel, page }) => {
    await taskPropertiesPanel.openDataMappingModal();

    await taskPropertiesPanel.addDataInputInModal({ name: "orderId" });
    await taskPropertiesPanel.addDataInputInModal({ name: "customerId" });

    await taskPropertiesPanel.addDataOutputInModal({ name: "approved" });
    await taskPropertiesPanel.addDataOutputInModal({ name: "message" });

    await taskPropertiesPanel.closeDataMappingModal();

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("task-multiple-data-io.png");
  });
});
