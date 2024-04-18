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

test.describe("Change Properties - Group", () => {
  test.beforeEach(async ({ palette, nodes, groupPropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    await groupPropertiesPanel.open();
  });

  test("should change the Group node name", async ({ nodes, groupPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    await groupPropertiesPanel.setName({ newName: "Renamed Group" });

    await nodes.select({ name: "Renamed Group", position: NodePosition.TOP });
    await expect(nodes.get({ name: "Renamed Group" })).toBeVisible();
    expect(await groupPropertiesPanel.getName()).toBe("Renamed Group");
  });

  test("should change the Group node description", async ({ nodes, groupPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    await groupPropertiesPanel.setDescription({
      newDescription: "New Group Description",
    });

    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    expect(await groupPropertiesPanel.getDescription()).toBe("New Group Description");
  });

  test("should change the Group node font", async ({ diagram, nodes, groupPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    await groupPropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });

    await expect(diagram.get()).toHaveScreenshot("change-group-font.png");
  });

  test("should reset the Group node font", async ({ nodes, groupPropertiesPanel }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1076",
    });
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1076");
    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    await groupPropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });
    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    await groupPropertiesPanel.resetFont();

    await expect(nodes.get({ name: DefaultNodeName.GROUP })).toHaveScreenshot("reset-group-font.png");
  });

  test("should change the Group node shape - fill color", async ({ nodes, groupPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    await groupPropertiesPanel.setFillColor({ color: "#f12200" });

    expect(await nodes.getRectAttribute({ nodeName: DefaultNodeName.GROUP, attribute: "fill" })).toEqual(
      "rgba(241, 34, 0, 0.1)"
    );
  });

  test("should change the Group node shape - stroke color", async ({ nodes, groupPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    await groupPropertiesPanel.setStrokeColor({ color: "#f12200" });

    expect(await nodes.getRectAttribute({ nodeName: DefaultNodeName.GROUP, attribute: "stroke" })).toEqual(
      "rgba(241, 34, 0, 1)"
    );
  });

  test("should change the Group node shape - position", async ({ diagram, nodes, groupPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.GROUP, position: NodePosition.TOP });
    await groupPropertiesPanel.setPosition({ x: "200", y: "200" });

    await expect(diagram.get()).toHaveScreenshot("change-group-position.png");
  });
});
