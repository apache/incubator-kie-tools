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

  test("should change the Text Annotation node Format", async ({ textAnnotationPropertiesPanel }) => {
    await textAnnotationPropertiesPanel.setFormat({
      nodeName: DefaultNodeName.TEXT_ANNOTATION,
      newFormat: "plaintext",
    });

    expect(await textAnnotationPropertiesPanel.getFormat({ nodeName: DefaultNodeName.TEXT_ANNOTATION })).toBe(
      "plaintext"
    );
  });

  test("should change the Text Annotation node Text", async ({ textAnnotationPropertiesPanel }) => {
    await textAnnotationPropertiesPanel.setText({
      nodeName: DefaultNodeName.TEXT_ANNOTATION,
      newText: "new text content",
    });

    expect(await textAnnotationPropertiesPanel.getText({ nodeName: "new text content" })).toBe("new text content");
  });

  test("should change the Text Annotation node description", async ({ textAnnotationPropertiesPanel }) => {
    await textAnnotationPropertiesPanel.setDescription({
      nodeName: DefaultNodeName.TEXT_ANNOTATION,
      newDescription: "New Text Annotation Description",
    });

    expect(await textAnnotationPropertiesPanel.getDescription({ nodeName: DefaultNodeName.TEXT_ANNOTATION })).toBe(
      "New Text Annotation Description"
    );
  });

  test("should change the Text Annotation node font - family", async ({ textAnnotationPropertiesPanel }) => {
    await textAnnotationPropertiesPanel.setFont({ nodeName: DefaultNodeName.TEXT_ANNOTATION, newFont: "Verdana" });

    expect(await textAnnotationPropertiesPanel.getFont({ nodeName: DefaultNodeName.TEXT_ANNOTATION })).toBe("Verdana");
  });

  test.skip("should change the Text Annotation node shape - background color", async ({
    nodes,
    textAnnotationPropertiesPanel,
  }) => {
    // blocked https://github.com/microsoft/playwright/issues/19929#issuecomment-1377035969
  });
});
