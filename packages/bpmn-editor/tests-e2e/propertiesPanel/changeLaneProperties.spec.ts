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

test.beforeEach(async ({ editor, page }) => {
  await editor.open();
  await page.setViewportSize({ width: 1920, height: 1080 });
});

test.describe("Change Properties - Lane", () => {
  test.beforeEach(async ({ palette, nodes, lanePropertiesPanel, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.LANE, targetPosition: { x: 200, y: 150 } });
    await lanePropertiesPanel.open();
  });

  test("should change the Lane name", async ({ lanePropertiesPanel }) => {
    await lanePropertiesPanel.nameProperties.setName({ newName: "Sales Department" });
    expect(await lanePropertiesPanel.nameProperties.getName()).toBe("Sales Department");
  });

  test("should change the Lane documentation", async ({ lanePropertiesPanel }) => {
    await lanePropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "Handles all sales-related activities",
    });

    expect(await lanePropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "Handles all sales-related activities"
    );
  });
});

test.describe("Change Properties - Multiple Lanes", () => {
  test("should configure multiple lanes in a pool", async ({ palette, nodes, lanePropertiesPanel, diagram, page }) => {
    await palette.dragNewNode({
      type: NodeType.LANE,
      targetPosition: { x: 200, y: 50 },
    });

    await lanePropertiesPanel.open();
    await lanePropertiesPanel.nameProperties.setName({ newName: "Sales" });

    await palette.dragNewNode({
      type: NodeType.LANE,
      targetPosition: { x: 200, y: 500 },
    });
    await lanePropertiesPanel.open();
    await lanePropertiesPanel.nameProperties.setName({ newName: "Marketing" });

    await expect(diagram.get()).toHaveScreenshot("multiple-lanes.png");
  });
});

test.describe("Change Properties - Lane with Tasks", () => {
  test("should create lane with tasks", async ({ palette, lanePropertiesPanel, diagram, page }) => {
    await palette.dragNewNode({
      type: NodeType.LANE,
      targetPosition: { x: 200, y: 150 },
    });

    await lanePropertiesPanel.open();
    await lanePropertiesPanel.nameProperties.setName({ newName: "Processing Lane" });

    await palette.dragNewNode({
      type: NodeType.TASK,
      targetPosition: { x: 300, y: 300 },
      thenRenameTo: "Task 1",
    });

    await palette.dragNewNode({
      type: NodeType.TASK,
      targetPosition: { x: 600, y: 300 },
      thenRenameTo: "Task 2",
    });

    await expect(diagram.get()).toHaveScreenshot("lane-with-tasks.png");
  });

  test("should move task between lanes", async ({ palette, nodes, lanePropertiesPanel, diagram, page }) => {
    await palette.dragNewNode({
      type: NodeType.LANE,
      targetPosition: { x: 200, y: 50 },
    });

    await lanePropertiesPanel.open();
    await lanePropertiesPanel.nameProperties.setName({ newName: "Lane 1" });

    await palette.dragNewNode({
      type: NodeType.LANE,
      targetPosition: { x: 200, y: 500 },
    });

    await lanePropertiesPanel.open();
    await lanePropertiesPanel.nameProperties.setName({ newName: "Lane 2" });

    await palette.dragNewNode({
      type: NodeType.TASK,
      targetPosition: { x: 300, y: 150 },
      thenRenameTo: "Movable Task",
    });

    const task = nodes.get({ name: "Movable Task" });
    const box = await task.boundingBox();
    expect(box).not.toBeNull();

    await page.mouse.move(box!.x + box!.width / 2, box!.y + box!.height / 2);
    await page.mouse.down();
    await page.mouse.move(400, 600);
    await page.mouse.up();

    await expect(diagram.get()).toHaveScreenshot("task-moved-between-lanes.png");
  });
});
