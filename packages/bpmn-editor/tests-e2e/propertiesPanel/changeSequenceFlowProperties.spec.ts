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
  await editor.setInitialProcessId();
});

test.describe("Change Properties - Sequence Flow", () => {
  test.beforeEach(async ({ palette, nodes, edges, sequenceFlowPropertiesPanel, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 }, thenRenameTo: "Task A" });
    await diagram.resetFocus();
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 400, y: 400 }, thenRenameTo: "Task B" });
    await diagram.resetFocus();

    await nodes.get({ name: "Task A" }).scrollIntoViewIfNeeded();
    await nodes.get({ name: "Task B" }).scrollIntoViewIfNeeded();

    await nodes.createSequenceFlow({ from: "Task A", to: "Task B" });

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

    await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
    const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
    expect(gatewayId).not.toBe("");

    await expect(nodes.get({ name: "High Amount" })).toBeAttached();
    await expect(nodes.get({ name: "Low Amount" })).toBeAttached();

    await nodes.createSequenceFlow({ from: gatewayId, to: "High Amount" });
    await nodes.createSequenceFlow({ from: gatewayId, to: "Low Amount" });

    const edge = await edges.get({ from: gatewayId, to: "High Amount" });
    await edge.scrollIntoViewIfNeeded();
    await edge.click({ force: true });
    await sequenceFlowPropertiesPanel.open();
  });

  test("should configure conditional flow from gateway", async ({
    edges,
    sequenceFlowPropertiesPanel,
    nodes,
    jsonModel,
  }) => {
    const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
    expect(gatewayId).not.toBe("");

    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "High Amount Path" });
    await sequenceFlowPropertiesPanel.setConditionExpression({ expression: "${amount > 5000}" });

    await (await edges.get({ from: gatewayId, to: "High Amount" })).click();

    const highAmountId = await nodes.getId({ name: "High Amount" });
    const sequenceFlows = await jsonModel.getSequenceFlows();
    const highAmountFlow = sequenceFlows.find((flow) => flow["@_targetRef"] === highAmountId);
    expect(highAmountFlow?.["@_name"]).toBe("High Amount Path");
    expect(highAmountFlow?.conditionExpression?.__$$text).toBe("${amount > 5000}");
  });

  test("should configure multiple conditional flows", async ({
    edges,
    sequenceFlowPropertiesPanel,
    nodes,
    jsonModel,
  }) => {
    const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
    expect(gatewayId).not.toBe("");

    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "High Amount" });
    await sequenceFlowPropertiesPanel.setConditionExpression({ expression: "${amount > 5000}" });

    await (await edges.get({ from: gatewayId, to: "Low Amount" })).click();
    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "Low Amount" });
    await sequenceFlowPropertiesPanel.setConditionExpression({ expression: "${amount <= 5000}" });

    const highAmountId = await nodes.getId({ name: "High Amount" });
    const lowAmountId = await nodes.getId({ name: "Low Amount" });
    const sequenceFlows = await jsonModel.getSequenceFlows();

    const highAmountFlow = sequenceFlows.find((flow) => flow["@_targetRef"] === highAmountId);
    expect(highAmountFlow?.["@_name"]).toBe("High Amount");
    expect(highAmountFlow?.conditionExpression?.__$$text).toBe("${amount > 5000}");

    const lowAmountFlow = sequenceFlows.find((flow) => flow["@_targetRef"] === lowAmountId);
    expect(lowAmountFlow?.["@_name"]).toBe("Low Amount");
    expect(lowAmountFlow?.conditionExpression?.__$$text).toBe("${amount <= 5000}");
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

    await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();

    await expect(nodes.get({ name: "Condition A" })).toBeAttached();

    await expect(nodes.get({ name: "Default Path" })).toBeAttached();

    const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
    await nodes.createSequenceFlow({ from: gatewayId, to: "Condition A" });
    await nodes.createSequenceFlow({ from: gatewayId, to: "Default Path" });
  });

  test("should configure default flow", async ({ edges, sequenceFlowPropertiesPanel, nodes, jsonModel }) => {
    const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
    expect(gatewayId).not.toBe("");

    await (await edges.get({ from: gatewayId, to: "Condition A" })).click();
    await sequenceFlowPropertiesPanel.setConditionExpression({ expression: "${approved == true}" });

    await (await edges.get({ from: gatewayId, to: "Default Path" })).click();
    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "Default" });

    const conditionAId = await nodes.getId({ name: "Condition A" });
    const defaultPathId = await nodes.getId({ name: "Default Path" });
    const sequenceFlows = await jsonModel.getSequenceFlows();

    const conditionAFlow = sequenceFlows.find((flow) => flow["@_targetRef"] === conditionAId);
    expect(conditionAFlow?.conditionExpression?.__$$text).toBe("${approved == true}");

    const defaultFlow = sequenceFlows.find((flow) => flow["@_targetRef"] === defaultPathId);
    expect(defaultFlow?.["@_name"]).toBe("Default");
  });
});
