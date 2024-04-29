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

test.describe("Resize node - Knowledge Source", () => {
  test.describe("Resize with snapping turned off", () => {
    test.beforeEach(async ({ overlays, palette }) => {
      await overlays.turnOffSnapping();
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
    });

    test("should increase Knowledge Source node size", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 50, yOffset: 50 });

      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(width).toEqual("210");
      expect(height).toEqual("130");
    });

    test("should decrease Knowledge Source node size", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 100, yOffset: 100 });
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: -20, yOffset: -20 });

      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(width).toEqual("240");
      expect(height).toEqual("160");
    });

    test("should not decrease below minimal Knowledge Source node size", async ({
      nodes,
      knowledgeSourcePropertiesPanel,
    }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: -50, yOffset: -50 });

      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(width).toEqual("160");
      expect(height).toEqual("80");
    });
  });

  test.describe("Resize with snapping turned on", () => {
    test.beforeEach(async ({ palette }) => {
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
    });

    test("should increase Knowledge Source node size", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 50, yOffset: 50 });

      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(width).toEqual("200");
      expect(height).toEqual("120");
    });

    test("should decrease Knowledge Source node size", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 100, yOffset: 100 });
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: -20, yOffset: -20 });

      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(width).toEqual("240");
      expect(height).toEqual("160");
    });

    test("should not decrease below minimal Knowledge Source node size", async ({
      nodes,
      knowledgeSourcePropertiesPanel,
    }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: -50, yOffset: -50 });

      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(width).toEqual("160");
      expect(height).toEqual("80");
    });
  });

  test.describe("Resize with non default snapping", () => {
    test.beforeEach(async ({ overlays, palette }) => {
      await overlays.setSnapping({ horizontal: "50", vertical: "50" });
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
    });

    test("should increase Knowledge Source node size", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 50, yOffset: 50 });

      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(width).toEqual("250");
      expect(height).toEqual("150");
    });

    test("should decrease Knowledge Source node size", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 100, yOffset: 100 });
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: -20, yOffset: -20 });

      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(width).toEqual("250");
      expect(height).toEqual("150");
    });

    test("should not decrease below minimal Knowledge Source node size", async ({
      nodes,
      knowledgeSourcePropertiesPanel,
    }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: -50, yOffset: -50 });

      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(width).toEqual("200");
      expect(height).toEqual("100");
    });
  });

  test.describe("Resize on top of other node - Knowledge Source", () => {
    test.beforeEach(async ({ diagram, palette }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 250, y: 150 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
    });

    test("should resize Knowledge Source on top of Decision node", async ({ nodes, diagram }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 200, yOffset: 0 });

      await expect(diagram.get()).toHaveScreenshot("resize-knowledge-source-on-top-of-decision.png");
    });

    test("should resize back Knowledge Source that is on top of Decision node", async ({ nodes, diagram }) => {
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 200, yOffset: 0 });
      await diagram.resetFocus();
      await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: -200, yOffset: 0 });

      await expect(diagram.get()).toHaveScreenshot("resize-back-knowledge-source-on-top-of-decision.png");
    });
  });

  test.describe("Resize in properties panel", () => {
    test.beforeEach(async ({ palette }) => {
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
    });

    test("should resize Knowledge Source node in properties panel", async ({
      diagram,
      nodes,
      knowledgeSourcePropertiesPanel,
    }) => {
      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      await knowledgeSourcePropertiesPanel.setShape({ width: "225", height: "225" });

      await diagram.resetFocus();

      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(height).toEqual("225");
      expect(width).toEqual("225");
    });

    test("should not resize Knowledge Source node below minimal size", async ({
      diagram,
      nodes,
      knowledgeSourcePropertiesPanel,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1074",
      });
      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      await knowledgeSourcePropertiesPanel.setShape({ width: "50", height: "50" });

      await diagram.resetFocus();

      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(height).toEqual("80");
      expect(width).toEqual("160");
    });

    test("should reset Knowledge Source node size", async ({ diagram, nodes, knowledgeSourcePropertiesPanel }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1075",
      });
      await knowledgeSourcePropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      await knowledgeSourcePropertiesPanel.setShape({ width: "300", height: "300" });
      await knowledgeSourcePropertiesPanel.resetShape();

      await diagram.resetFocus();

      await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
      const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
      expect(height).toEqual("80");
      expect(width).toEqual("160");
    });
  });
});
