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
});
