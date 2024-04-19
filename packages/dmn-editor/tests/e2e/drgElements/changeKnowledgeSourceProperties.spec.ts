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

test.describe("Change Properties - Knowledge Source", () => {
  test.beforeEach(async ({ palette, nodes, knowledgeSourcePropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.open();
  });

  test("should change the Knowledge Source node name", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.setName({
      newName: "Renamed Knowledge Source",
    });

    await nodes.select({ name: "Renamed Knowledge Source" });
    await expect(nodes.get({ name: "Renamed Knowledge Source" })).toBeVisible();
    expect(await knowledgeSourcePropertiesPanel.getName()).toBe("Renamed Knowledge Source");
  });

  test("should change the Knowledge Source node description", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.setDescription({
      newDescription: "New Knowledge Source Description",
    });

    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    expect(await knowledgeSourcePropertiesPanel.getDescription()).toBe("New Knowledge Source Description");
  });

  test("should change the Knowledge Source node Source Type", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.setSourceType({
      newSourceType: "New Knowledge Source Source Type",
    });

    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    expect(await knowledgeSourcePropertiesPanel.getSourceType()).toBe("New Knowledge Source Source Type");
  });

  test("should change the Knowledge Source node Location URI", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.setLocationURI({
      newLocationURI: "New Location URI",
    });

    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    expect(await knowledgeSourcePropertiesPanel.getLocationURI()).toBe("New Location URI");
  });

  test("should change the Knowledge Source node documentation links", async ({
    nodes,
    knowledgeSourcePropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.addDocumentationLink({
      linkText: "Link Text",
      linkHref: "http://link.test.com",
    });

    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    const links = await knowledgeSourcePropertiesPanel.getDocumentationLinks();
    expect(links).toHaveLength(1);
    expect(links[0]).toHaveText("Link Text");
    expect(links[0]).toHaveAttribute("href", "http://link.test.com/");
  });

  test("should change the Knowledge Source node font", async ({ diagram, nodes, knowledgeSourcePropertiesPanel }) => {
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });

    await expect(diagram.get()).toHaveScreenshot("change-knowledge-source-font.png");
  });

  test("should reset the Knowledge Source node font", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1076",
    });
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1076");
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.setFont({
      fontSize: "40",
      bold: true,
      italic: true,
      underline: true,
      striketrough: true,
      color: "#f12200",
      fontFamily: "Verdana",
    });
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.resetFont();

    await expect(nodes.get({ name: DefaultNodeName.KNOWLEDGE_SOURCE })).toHaveScreenshot(
      "reset-knowledge-source-font.png"
    );
  });

  test("should change the Knowledge Source node shape - fill color", async ({
    nodes,
    knowledgeSourcePropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.setFillColor({ color: "#f12200" });

    expect(await nodes.getPathAttribute({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, attribute: "fill" })).toEqual(
      "rgba(241, 34, 0, 0.9)"
    );
  });

  test("should change the Knowledge Source node shape - stroke color", async ({
    nodes,
    knowledgeSourcePropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.setStrokeColor({ color: "#f12200" });

    expect(await nodes.getPathAttribute({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, attribute: "stroke" })).toEqual(
      "rgba(241, 34, 0, 1)"
    );
  });

  test("should change the Knowledge Source node shape - position", async ({
    diagram,
    nodes,
    knowledgeSourcePropertiesPanel,
  }) => {
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.setPosition({ x: "200", y: "200" });

    await expect(diagram.get()).toHaveScreenshot("change-knowledge-source-position.png");
  });
});
