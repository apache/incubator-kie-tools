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
import { DataType } from "../__fixtures__/jsonModel";
import { DefaultNodeName, NodePosition, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Change Properties - Decision Service", () => {
  test.beforeEach(async ({ palette, nodes, decisionServicePropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.open();
  });

  test("should change the Decision Service node name", async ({ nodes, decisionServicePropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.setName({
      newName: "Renamed Decision Service",
    });

    await nodes.select({ name: "Renamed Decision Service" });
    await expect(nodes.get({ name: "Renamed Decision Service" })).toBeVisible();
    expect(await decisionServicePropertiesPanel.getName()).toBe("Renamed Decision Service");
  });

  test("should change the Decision Service node data type", async ({ nodes, decisionServicePropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.setDataType({
      newDataType: DataType.Number,
    });

    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await nodes.hover({ name: DefaultNodeName.DECISION_SERVICE });
    await expect(
      nodes.get({ name: DefaultNodeName.DECISION_SERVICE }).getByPlaceholder("Select a data type...")
    ).toHaveValue(DataType.Number);
  });

  test("should change the Decision Service node description", async ({ nodes, decisionServicePropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.setDescription({
      newDescription: "New Decision Service Description",
    });

    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    expect(await decisionServicePropertiesPanel.getDescription()).toBe("New Decision Service Description");
  });

  test("should change the Decision Service node documentation links", async ({
    nodes,
    decisionServicePropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.addDocumentationLink({
      linkText: "Link Text",
      linkHref: "http://link.test.com",
    });

    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    const links = await decisionServicePropertiesPanel.getDocumentationLinks();
    expect(links).toHaveLength(1);
    expect(links[0]).toHaveText("Link Text");
    expect(links[0]).toHaveAttribute("href", "http://link.test.com/");
  });

  test("should change the Decision Service node font", async ({ diagram, nodes, decisionServicePropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });

    await expect(diagram.get()).toHaveScreenshot("change-decision-service-font.png");
  });

  test("should reset the Decision Service node font", async ({ nodes, decisionServicePropertiesPanel }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1076",
    });
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1076");
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.resetFont();

    await expect(nodes.get({ name: DefaultNodeName.DECISION_SERVICE })).toHaveScreenshot(
      "reset-decision-service-font.png"
    );
  });

  test("should change the Decision Service node shape - fill color", async ({
    nodes,
    decisionServicePropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.setFillColor({ color: "#f12200" });

    expect(await nodes.getRectAttribute({ nodeName: DefaultNodeName.DECISION_SERVICE, attribute: "fill" })).toEqual(
      "rgba(241, 34, 0, 0.1)"
    );
  });

  test("should change the Decision Service node shape - stroke color", async ({
    nodes,
    decisionServicePropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    await decisionServicePropertiesPanel.setStrokeColor({ color: "#f12200" });

    expect(await nodes.getRectAttribute({ nodeName: DefaultNodeName.DECISION_SERVICE, attribute: "stroke" })).toEqual(
      "rgba(241, 34, 0, 1)"
    );
  });

  test("should change the Decision Service node shape - position", async ({
    diagram,
    nodes,
    decisionServicePropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.DECISION_SERVICE });
    await decisionServicePropertiesPanel.setPosition({ x: "200", y: "200" });

    await expect(diagram.get()).toHaveScreenshot("change-decision-service-position.png");
  });
});
