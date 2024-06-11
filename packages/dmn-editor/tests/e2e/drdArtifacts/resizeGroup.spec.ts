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
import { DefaultNodeName, NodePosition, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Resize node - Group", () => {
  test.describe("Resize with snapping turned off", () => {
    test.beforeEach(async ({ overlays, palette }) => {
      await overlays.turnOffSnapping();
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
    });

    test("should increase Group node size", async ({ nodes, groupPropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: 50, yOffset: 50 });

      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("370");
      expect(width).toEqual("370");
    });

    test("should decrease Group node size", async ({ nodes, groupPropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: 100, yOffset: 100 });
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: -20, yOffset: -20 });

      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("400");
      expect(width).toEqual("400");
    });

    test("should not decrease below minimal Group node size", async ({ nodes, groupPropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: -50, yOffset: -50 });

      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("270");
      expect(width).toEqual("280");
    });
  });

  test.describe("Resize with snapping turned on", () => {
    test.beforeEach(async ({ palette }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
    });

    test("should increase Group node size", async ({ nodes, groupPropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: 50, yOffset: 50 });

      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("360");
      expect(width).toEqual("360");
    });

    test("should decrease Group node size", async ({ nodes, groupPropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: 100, yOffset: 100 });
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: -20, yOffset: -20 });

      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("400");
      expect(width).toEqual("400");
    });

    test("should not decrease below minimal Group node size", async ({ nodes, groupPropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: -50, yOffset: -50 });

      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("260");
      expect(width).toEqual("280");
    });
  });

  test.describe("Resize with non default snapping", () => {
    test.beforeEach(async ({ overlays, palette }) => {
      await overlays.setSnapping({ horizontal: "50", vertical: "50" });
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
    });

    test("should increase Group node size", async ({ nodes, groupPropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: 50, yOffset: 50 });

      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("400");
      expect(width).toEqual("400");
    });

    test("should decrease Group node size", async ({ nodes, groupPropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: 100, yOffset: 100 });
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: -20, yOffset: -20 });

      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("400");
      expect(width).toEqual("400");
    });

    test("should not decrease below minimal Group node size", async ({ nodes, groupPropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: -50, yOffset: -50 });

      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("300");
      expect(width).toEqual("300");
    });
  });

  test.describe("Resize on top of other node - Group", () => {
    test.beforeEach(async ({ palette }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 450, y: 150 } });
    });

    test("should resize Group on top of Decision node", async ({ nodes, diagram }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: 200, yOffset: 0 });

      await expect(diagram.get()).toHaveScreenshot("resize-group-on-top-of-decision.png");
    });

    test("should resize back Group that is on top of Decision node", async ({ nodes, diagram }) => {
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: 200, yOffset: 0 });
      await diagram.resetFocus();
      await nodes.resize({ nodeName: DefaultNodeName.GROUP, position: NodePosition.TOP, xOffset: -200, yOffset: 0 });

      await expect(diagram.get()).toHaveScreenshot("resize-back-group-on-top-of-decision.png");
    });
  });

  test.describe("Resize in properties panel", () => {
    test.beforeEach(async ({ palette }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
    });

    test("should resize Group node", async ({ diagram, nodes, groupPropertiesPanel }) => {
      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      await groupPropertiesPanel.setShape({ width: "325", height: "325" });

      await diagram.resetFocus();

      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("325");
      expect(width).toEqual("325");
    });

    test("should not resize below minimal Group node size", async ({ diagram, nodes, groupPropertiesPanel }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1074",
      });
      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      await groupPropertiesPanel.setShape({ width: "100", height: "100" });

      await diagram.resetFocus();

      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(height).toEqual("200");
      expect(width).toEqual("280");
    });

    test("should reset Group node size", async ({ diagram, nodes, groupPropertiesPanel }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1075",
      });
      await groupPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      await groupPropertiesPanel.setShape({ width: "325", height: "325" });

      await groupPropertiesPanel.resetShape();
      await diagram.resetFocus();

      await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
      const { width, height } = await groupPropertiesPanel.getShape();
      expect(width).toEqual("280");
      expect(height).toEqual("200");
    });
  });
});
