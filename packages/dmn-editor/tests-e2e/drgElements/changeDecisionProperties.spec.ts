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

test.describe("Change Properties - Decision", () => {
  test.beforeEach(async ({ palette, nodes, decisionPropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.open();
  });

  test("should change the Decision node name", async ({ nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setName({ newName: "Renamed Decision" });

    await nodes.select({ name: "Renamed Decision" });
    await expect(nodes.get({ name: "Renamed Decision" })).toBeVisible();
    expect(await decisionPropertiesPanel.getName()).toBe("Renamed Decision");
  });

  test("should change the Decision node data type", async ({ nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setDataType({ newDataType: DataType.Number });

    await nodes.select({ name: DefaultNodeName.DECISION });
    await nodes.hover({ name: DefaultNodeName.DECISION });
    await expect(nodes.get({ name: DefaultNodeName.DECISION }).getByPlaceholder("Select a data type...")).toHaveValue(
      DataType.Number
    );
  });

  test("should change the Decision node description", async ({ nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setDescription({
      newDescription: "New Decision Description",
    });

    await nodes.select({ name: DefaultNodeName.DECISION });
    expect(await decisionPropertiesPanel.getDescription()).toBe("New Decision Description");
  });

  test("should change the Decision node question", async ({ nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setQuestion({
      newQuestion: "New Decision Question",
    });

    await nodes.select({ name: DefaultNodeName.DECISION });
    expect(await decisionPropertiesPanel.getQuestion()).toBe("New Decision Question");
  });

  test("should change the Decision node answers", async ({ nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setAllowedAnswers({
      newAllowedAnswers: "New Allowed Answers",
    });

    await nodes.select({ name: DefaultNodeName.DECISION });
    expect(await decisionPropertiesPanel.getAllowedAnswers()).toBe("New Allowed Answers");
  });

  test("should change the Decision node documentation links", async ({ nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.addDocumentationLink({
      linkText: "Link Text",
      linkHref: "http://link.test.com",
    });

    await nodes.select({ name: DefaultNodeName.DECISION });
    const links = await decisionPropertiesPanel.getDocumentationLinks();
    expect(links).toHaveLength(1);
    expect(links[0]).toHaveText("Link Text");
    expect(links[0]).toHaveAttribute("href", "http://link.test.com/");
  });

  test("should change the Decision node font", async ({ diagram, nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });

    await expect(diagram.get()).toHaveScreenshot("change-decision-font.png");
  });

  test("should reset the Decision node font", async ({ nodes, decisionPropertiesPanel }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1076",
    });

    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.resetFont();

    await expect(nodes.get({ name: DefaultNodeName.DECISION })).toHaveScreenshot("reset-decision-font.png");
  });

  test("should change the Decision node shape - fill color", async ({ nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setFillColor({ color: "#f12200" });

    expect(await nodes.getRectAttribute({ nodeName: DefaultNodeName.DECISION, attribute: "fill" })).toEqual(
      "rgba(241, 34, 0, 0.9)"
    );
  });

  test("should change the Decision node shape - stroke color", async ({ nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setStrokeColor({ color: "#f12200" });

    expect(await nodes.getRectAttribute({ nodeName: DefaultNodeName.DECISION, attribute: "stroke" })).toEqual(
      "rgba(241, 34, 0, 1)"
    );
  });

  test("should change the Decision node shape - position", async ({ diagram, nodes, decisionPropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.setPosition({ x: "200", y: "200" });

    await expect(diagram.get()).toHaveScreenshot("change-decision-position.png");
  });

  test("should change the Decision node properties using the BEE properties panel", async ({
    nodes,
    bee,
    beePropertiesPanel,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1459",
    });
    await nodes.edit({ name: DefaultNodeName.DECISION });
    await bee.selectExpressionMenu.selectContext();

    await bee.expression.asContext().expressionHeaderCell.select();

    await beePropertiesPanel.decisionNode.setDescription({ newDescription: "New Description" });
    expect(await beePropertiesPanel.decisionNode.getDescription()).toBe("New Description");

    await beePropertiesPanel.decisionNode.setAllowedAnswers({ newAllowedAnswers: "New Allowed Answers" });
    expect(await beePropertiesPanel.decisionNode.getAllowedAnswers()).toBe("New Allowed Answers");

    await beePropertiesPanel.decisionNode.setQuestion({ newQuestion: "New Question" });
    expect(await beePropertiesPanel.decisionNode.getQuestion()).toBe("New Question");
  });
});
