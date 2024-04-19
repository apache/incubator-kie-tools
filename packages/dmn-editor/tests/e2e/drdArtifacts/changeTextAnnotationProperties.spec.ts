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

test.describe("Change Properties - Text Annotation", () => {
  test.beforeEach(async ({ palette, nodes, textAnnotationPropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.open();
  });

  test("should change the Text Annotation node Format", async ({ nodes, textAnnotationPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.setFormat({
      newFormat: "plaintext",
    });

    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    expect(await textAnnotationPropertiesPanel.getFormat()).toBe("plaintext");
  });

  test("should change the Text Annotation node Text", async ({ nodes, textAnnotationPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.setText({
      newText: "new text content",
    });

    await nodes.select({ name: "new text content" });
    expect(await textAnnotationPropertiesPanel.getText()).toBe("new text content");
  });

  test("should change the Text Annotation node description", async ({ nodes, textAnnotationPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.setDescription({
      newDescription: "New Text Annotation Description",
    });

    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    expect(await textAnnotationPropertiesPanel.getDescription()).toBe("New Text Annotation Description");
  });

  test("should change the Text Annotation node font", async ({ diagram, nodes, textAnnotationPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });

    await expect(diagram.get()).toHaveScreenshot("change-text-annotation-font.png");
  });

  test("should reset the Text Annotation node font", async ({ nodes, textAnnotationPropertiesPanel }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1076",
    });
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1076");
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.resetFont();

    await expect(nodes.get({ name: DefaultNodeName.TEXT_ANNOTATION })).toHaveScreenshot(
      "reset-text-annotation-font.png"
    );
  });

  test("should change the Text Annotation node shape - fill color", async ({
    nodes,
    textAnnotationPropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.setFillColor({ color: "#f12200" });

    expect(await nodes.getPathAttribute({ nodeName: DefaultNodeName.TEXT_ANNOTATION, attribute: "fill" })).toEqual(
      "rgba(241, 34, 0, 0.1)"
    );
  });

  test("should change the Text Annotation node shape - stroke color", async ({
    nodes,
    textAnnotationPropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.setStrokeColor({ color: "#f12200" });

    expect(await nodes.getPathAttribute({ nodeName: DefaultNodeName.TEXT_ANNOTATION, attribute: "stroke" })).toEqual(
      "rgba(241, 34, 0, 1)"
    );
  });

  test("should change the Group node shape - position", async ({ diagram, nodes, textAnnotationPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.TEXT_ANNOTATION });
    await textAnnotationPropertiesPanel.setPosition({ x: "200", y: "200" });

    await expect(diagram.get()).toHaveScreenshot("change-text-annotation-position.png");
  });
});
