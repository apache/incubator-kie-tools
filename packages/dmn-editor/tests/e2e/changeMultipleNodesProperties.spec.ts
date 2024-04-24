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

import { test, expect } from "./__fixtures__/base";
import { DefaultNodeName, NodeType } from "./__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Change Properties - Multiple Nodes", () => {
  test("should change multiple nodes font", async ({ nodes, palette, diagram, multipleNodesPropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
    await diagram.resetFocus();
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 100 } });
    await diagram.resetFocus();

    await multipleNodesPropertiesPanel.open();
    await nodes.selectMultiple({ names: [DefaultNodeName.INPUT_DATA, DefaultNodeName.DECISION] });
    await multipleNodesPropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });

    await expect(diagram.get()).toHaveScreenshot("change-multiple-nodes-font.png");
  });

  test("should reset multiple nodes shape", async ({ nodes, palette, diagram, multipleNodesPropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
    await nodes.resize({ nodeName: DefaultNodeName.INPUT_DATA, xOffset: 50, yOffset: 50 });
    await diagram.resetFocus();
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 400, y: 100 } });
    await nodes.resize({ nodeName: DefaultNodeName.DECISION, xOffset: 50, yOffset: 50 });
    await diagram.resetFocus();

    await multipleNodesPropertiesPanel.open();
    await nodes.selectMultiple({ names: [DefaultNodeName.INPUT_DATA, DefaultNodeName.DECISION] });
    await multipleNodesPropertiesPanel.resetShape();

    await expect(diagram.get()).toHaveScreenshot("change-multiple-nodes-shape.png");
  });

  test("should update shape properties when switching between nodes", async ({
    nodes,
    palette,
    diagram,
    inputDataPropertiesPanel,
    decisionPropertiesPanel,
  }) => {
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
    await nodes.resize({ nodeName: DefaultNodeName.INPUT_DATA, xOffset: 50, yOffset: 50 });
    await diagram.resetFocus();
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 400, y: 100 } });
    await nodes.resize({ nodeName: DefaultNodeName.DECISION, xOffset: 100, yOffset: 100 });
    await diagram.resetFocus();

    await inputDataPropertiesPanel.open();
    await nodes.select({ name: DefaultNodeName.INPUT_DATA });
    const { width: inputDataWidth, height: inputDataHeight } = await inputDataPropertiesPanel.getShape();
    expect(inputDataWidth).toEqual("200");
    expect(inputDataHeight).toEqual("120");

    await nodes.select({ name: DefaultNodeName.DECISION });
    const { width: decisionWidth, height: decisionHeight } = await decisionPropertiesPanel.getShape();
    expect(decisionWidth).toEqual("260");
    expect(decisionHeight).toEqual("180");
  });
});
