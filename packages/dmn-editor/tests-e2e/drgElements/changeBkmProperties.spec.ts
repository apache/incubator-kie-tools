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
import { DataType } from "../__fixtures__/dataTypes";
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Change Properties - BKM", () => {
  test.beforeEach(async ({ palette, nodes, bkmPropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.open();
  });

  test("should change the BKM node name", async ({ nodes, bkmPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.setName({ newName: "Renamed BKM" });

    await nodes.select({ name: "Renamed BKM" });
    await expect(nodes.get({ name: "Renamed BKM" })).toBeVisible();
    expect(await bkmPropertiesPanel.getName()).toBe("Renamed BKM");
  });

  test("should change the BKM node data type", async ({ nodes, bkmPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.setDataType({ newDataType: DataType.Number });

    await nodes.hover({ name: DefaultNodeName.BKM });
    await expect(nodes.get({ name: DefaultNodeName.BKM }).getByPlaceholder("Select a data type...")).toHaveValue(
      DataType.Number
    );
  });

  test("should change the BKM node description", async ({ nodes, bkmPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.setDescription({
      newDescription: "New BKM Description",
    });

    await nodes.select({ name: DefaultNodeName.BKM });
    expect(await bkmPropertiesPanel.getDescription()).toBe("New BKM Description");
  });

  test("should change the BKM node documentation links", async ({ nodes, bkmPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.addDocumentationLink({
      linkText: "Link Text",
      linkHref: "http://link.test.com",
    });

    await nodes.select({ name: DefaultNodeName.BKM });
    const links = await bkmPropertiesPanel.getDocumentationLinks();
    expect(links).toHaveLength(1);
    expect(links[0]).toHaveText("Link Text");
    expect(links[0]).toHaveAttribute("href", "http://link.test.com/");
  });

  test("should change the BKM node font", async ({ diagram, nodes, bkmPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });

    await expect(diagram.get()).toHaveScreenshot("change-bkm-font.png");
  });

  test("should reset the BKM node font", async ({ nodes, bkmPropertiesPanel }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1076",
    });

    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.resetFont();

    await expect(nodes.get({ name: DefaultNodeName.BKM })).toHaveScreenshot("reset-bkm-font.png");
  });

  test("should change the BKM node shape - fill color", async ({ nodes, bkmPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.setFillColor({ color: "#f12200" });

    expect(await nodes.getPolygonAttribute({ nodeName: DefaultNodeName.BKM, attribute: "fill" })).toEqual(
      "rgba(241, 34, 0, 0.9)"
    );
  });

  test("should change the BKM node shape - stroke color", async ({ nodes, bkmPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.setStrokeColor({ color: "#f12200" });

    expect(await nodes.getPolygonAttribute({ nodeName: DefaultNodeName.BKM, attribute: "stroke" })).toEqual(
      "rgba(241, 34, 0, 1)"
    );
  });

  test("should change the BKM node shape - position", async ({ diagram, nodes, bkmPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.BKM });
    await bkmPropertiesPanel.setPosition({ x: "200", y: "200" });

    await expect(diagram.get()).toHaveScreenshot("change-bkm-position.png");
  });
  test("should change the BKM node properties using the BEE properties panel", async ({
    nodes,
    beePropertiesPanel,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1459",
    });
    await nodes.edit({ name: DefaultNodeName.BKM });
    await beePropertiesPanel.bkmNode.expressionHeaderCell().select();

    await beePropertiesPanel.bkmNode.setDescription({ newDescription: "New Description" });
    expect(await beePropertiesPanel.bkmNode.getDescription()).toBe("New Description");
  });
});
