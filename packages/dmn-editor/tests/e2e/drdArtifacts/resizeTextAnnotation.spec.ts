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

test.describe("Resize node - Text Annotation", () => {
  test.describe("Resize with snapping turned off", () => {
    test.beforeEach(async ({ overlays, palette }) => {
      await overlays.turnOffSnapping();
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
    });

    test("should increase Text Annotation node size", async ({ nodes, textAnnotationPropertiesPanel }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: 50,
        yOffset: 50,
      });

      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("250");
      expect(width).toEqual("250");
    });

    test("should decrease Text Annotation node size", async ({ nodes, textAnnotationPropertiesPanel }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: 100,
        yOffset: 100,
      });
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: -20,
        yOffset: -20,
      });

      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("280");
      expect(width).toEqual("280");
    });

    test("should not decrease below minimal Text Annotation node size", async ({
      nodes,
      textAnnotationPropertiesPanel,
    }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: -50,
        yOffset: -50,
      });

      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("150");
      expect(width).toEqual("200");
    });
  });

  test.describe("Resize with snapping turned on", () => {
    test.beforeEach(async ({ palette }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
    });

    test("should increase Text Annotation node size", async ({ nodes, textAnnotationPropertiesPanel }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: 50,
        yOffset: 50,
      });

      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("240");
      expect(width).toEqual("240");
    });

    test("should decrease Text Annotation node size", async ({ nodes, textAnnotationPropertiesPanel }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: 100,
        yOffset: 100,
      });
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: -20,
        yOffset: -20,
      });

      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("280");
      expect(width).toEqual("280");
    });

    test("should not decrease below minimal Text Annotation node size", async ({
      nodes,
      textAnnotationPropertiesPanel,
    }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: -50,
        yOffset: -50,
      });

      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("140");
      expect(width).toEqual("200");
    });
  });

  test.describe("Resize with non default snapping", () => {
    test.beforeEach(async ({ overlays, palette }) => {
      await overlays.setSnapping({ horizontal: "50", vertical: "50" });
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
    });

    test("should increase Text Annotation node size", async ({ nodes, textAnnotationPropertiesPanel }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: 50,
        yOffset: 50,
      });

      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("250");
      expect(width).toEqual("250");
    });

    test("should decrease Text Annotation node size", async ({ nodes, textAnnotationPropertiesPanel }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: 100,
        yOffset: 100,
      });
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: -20,
        yOffset: -20,
      });

      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("250");
      expect(width).toEqual("250");
    });

    test("should not decrease below minimal Text Annotation node size", async ({
      nodes,
      textAnnotationPropertiesPanel,
    }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: -50,
        yOffset: -50,
      });

      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("150");
      expect(width).toEqual("200");
    });
  });

  test.describe("Resize on top of other node - Text Annotation", () => {
    test.beforeEach(async ({ diagram, palette }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 450, y: 150 } });
      await diagram.resetFocus();
    });

    test("should resize Text Annotation on top of Decision node", async ({ nodes, diagram }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: 200,
        yOffset: 0,
      });

      await expect(diagram.get()).toHaveScreenshot("resize-text-annotation-on-top-of-decision.png");
    });

    test("should resize back Text Annotation that is on top of Decision node", async ({ nodes, diagram }) => {
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: 200,
        yOffset: 0,
      });
      await diagram.resetFocus();
      await nodes.resize({
        nodeName: DefaultNodeName.TEXT_ANNOTATION,
        position: NodePosition.TOP,
        xOffset: -200,
        yOffset: 0,
      });

      await expect(diagram.get()).toHaveScreenshot("resize-back-text-annotation-on-top-of-decision.png");
    });
  });

  test.describe("Resize in properties panel", () => {
    test.beforeEach(async ({ palette }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
    });

    test("should resize Text Annotation node in properties panel", async ({
      diagram,
      nodes,
      textAnnotationPropertiesPanel,
    }) => {
      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION, position: NodePosition.TOP });
      await textAnnotationPropertiesPanel.setShape({ width: "325", height: "325" });

      await diagram.resetFocus();

      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION, position: NodePosition.TOP });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("325");
      expect(width).toEqual("325");
    });

    test("should not resize Text Annotation node below minimal size", async ({
      diagram,
      nodes,
      textAnnotationPropertiesPanel,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1074",
      });
      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION, position: NodePosition.TOP });
      await textAnnotationPropertiesPanel.setShape({ width: "50", height: "50" });

      await diagram.resetFocus();

      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION, position: NodePosition.TOP });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(height).toEqual("60");
      expect(width).toEqual("200");
    });

    test("should reset Text Annotation node size", async ({ diagram, nodes, textAnnotationPropertiesPanel }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1075",
      });
      await textAnnotationPropertiesPanel.open();
      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION, position: NodePosition.TOP });
      await textAnnotationPropertiesPanel.setShape({ width: "300", height: "300" });
      await textAnnotationPropertiesPanel.resetShape();

      await diagram.resetFocus();

      await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION, position: NodePosition.TOP });
      const { width, height } = await textAnnotationPropertiesPanel.getShape();
      expect(width).toEqual("200");
      expect(height).toEqual("60");
    });
  });
});
