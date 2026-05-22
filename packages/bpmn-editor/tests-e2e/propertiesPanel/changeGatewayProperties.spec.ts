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
import { NodeType, GatewayNodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor, page }) => {
  await page.setViewportSize({ width: 1920, height: 1080 });
  await editor.open();
});

test.describe("Change Properties - Exclusive Gateway", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();

    await nodes.getByType(NodeType.GATEWAY).click();
  });

  test("should change the Gateway name", async ({ gatewayPropertiesPanel }) => {
    await gatewayPropertiesPanel.nameProperties.setName({ newName: "Decision Point" });

    expect(await gatewayPropertiesPanel.nameProperties.getName()).toBe("Decision Point");
  });

  test("should change the Gateway documentation", async ({ gatewayPropertiesPanel }) => {
    await gatewayPropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "This gateway routes based on order amount",
    });

    expect(await gatewayPropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "This gateway routes based on order amount"
    );
  });
});

test.describe("Change Properties - Exclusive Gateway with Default Flow", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
    await nodes.getByType(NodeType.GATEWAY).click();
  });

  test("should configure Exclusive Gateway properties", async ({ gatewayPropertiesPanel, page }) => {
    await gatewayPropertiesPanel.nameProperties.setName({ newName: "Exclusive Decision" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("exclusive-gateway-configured.png");
  });
});

test.describe("Change Properties - Parallel Gateway", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
    await nodes.getByType(NodeType.GATEWAY).click();

    await nodes.morph({ node: nodes.getByType(NodeType.GATEWAY), to: GatewayNodeType.PARALLEL });
  });

  test("should configure Parallel Gateway properties", async ({ gatewayPropertiesPanel, page }) => {
    await gatewayPropertiesPanel.nameProperties.setName({ newName: "Parallel Split" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("parallel-gateway-configured.png");
  });
});

test.describe("Change Properties - Inclusive Gateway", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
    await nodes.getByType(NodeType.GATEWAY).click();

    await nodes.morph({ node: nodes.getByType(NodeType.GATEWAY), to: GatewayNodeType.INCLUSIVE });
  });

  test("should configure Inclusive Gateway properties", async ({ gatewayPropertiesPanel }) => {
    await gatewayPropertiesPanel.nameProperties.setName({ newName: "Inclusive Decision" });

    expect(await gatewayPropertiesPanel.nameProperties.getName()).toBe("Inclusive Decision");
  });
});

test.describe("Change Properties - Event-Based Gateway", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
    await nodes.getByType(NodeType.GATEWAY).click();

    await nodes.morph({ node: nodes.getByType(NodeType.GATEWAY), to: GatewayNodeType.EVENT_BASED });
  });

  test("should configure Event-Based Gateway name", async ({ gatewayPropertiesPanel }) => {
    await gatewayPropertiesPanel.nameProperties.setName({ newName: "Event Gateway" });

    expect(await gatewayPropertiesPanel.nameProperties.getName()).toBe("Event Gateway");
  });
});

test.describe("Change Properties - Complex Gateway", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
    await nodes.getByType(NodeType.GATEWAY).click();

    await nodes.morph({ node: nodes.getByType(NodeType.GATEWAY), to: GatewayNodeType.COMPLEX });
  });

  test("should configure Complex Gateway name", async ({ gatewayPropertiesPanel }) => {
    await gatewayPropertiesPanel.nameProperties.setName({ newName: "Complex Decision" });

    expect(await gatewayPropertiesPanel.nameProperties.getName()).toBe("Complex Decision");
  });
});
