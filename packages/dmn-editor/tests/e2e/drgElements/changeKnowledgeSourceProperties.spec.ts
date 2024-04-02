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

test.describe("Change Properties - Knowledge Source", () => {
  test.beforeEach(async ({ palette, nodes, knowledgeSourcePropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await knowledgeSourcePropertiesPanel.open();
  });

  test("should change the Knowledge Source node name", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
    await knowledgeSourcePropertiesPanel.setName({
      from: DefaultNodeName.KNOWLEDGE_SOURCE,
      to: "Renamed Knowledge Source",
    });

    await expect(nodes.get({ name: "Renamed Knowledge Source" })).toBeVisible();
    expect(await knowledgeSourcePropertiesPanel.getName({ nodeName: "Renamed Knowledge Source" })).toBe(
      "Renamed Knowledge Source"
    );
  });

  test("should change the Knowledge Source node description", async ({ knowledgeSourcePropertiesPanel }) => {
    await knowledgeSourcePropertiesPanel.setDescription({
      nodeName: DefaultNodeName.KNOWLEDGE_SOURCE,
      newDescription: "New Knowledge Source Description",
    });

    expect(
      await knowledgeSourcePropertiesPanel.getNodeDescription({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE })
    ).toBe("New Knowledge Source Description");
  });

  test("should change the Knowledge Source node Source Type", async ({ knowledgeSourcePropertiesPanel }) => {
    await knowledgeSourcePropertiesPanel.setSourceType({
      nodeName: DefaultNodeName.KNOWLEDGE_SOURCE,
      newSourceType: "New Knowledge Source Source Type",
    });

    expect(await knowledgeSourcePropertiesPanel.getNodeSourceType({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE })).toBe(
      "New Knowledge Source Source Type"
    );
  });

  test("should change the Knowledge Source node Location URI", async ({ knowledgeSourcePropertiesPanel }) => {
    await knowledgeSourcePropertiesPanel.setLocationURI({
      nodeName: DefaultNodeName.KNOWLEDGE_SOURCE,
      newLocationURI: "New Location URI",
    });

    expect(
      await knowledgeSourcePropertiesPanel.getNodeLocationURI({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE })
    ).toBe("New Location URI");
  });

  test("should change the Knowledge Source node documentation links", async ({ knowledgeSourcePropertiesPanel }) => {
    await knowledgeSourcePropertiesPanel.addDocumentationLink({
      nodeName: DefaultNodeName.KNOWLEDGE_SOURCE,
      linkText: "Link Text",
      linkHref: "http://link.test.com",
    });

    const links = await knowledgeSourcePropertiesPanel.getDocumentationLinks({
      nodeName: DefaultNodeName.KNOWLEDGE_SOURCE,
    });
    expect(links).toHaveLength(1);
    expect(links[0]).toHaveText("Link Text");
    expect(links[0]).toHaveAttribute("href", "http://link.test.com/");
  });

  test("should change the Knowledge Source node font - family", async ({ knowledgeSourcePropertiesPanel }) => {
    await knowledgeSourcePropertiesPanel.setFont({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, newFont: "Verdana" });

    expect(await knowledgeSourcePropertiesPanel.getNodeFont({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE })).toBe(
      "Verdana"
    );
  });

  test.skip("should change the Knowledge Source node shape - background color", async ({
    nodes,
    knowledgeSourcePropertiesPanel,
  }) => {
    // blocked https://github.com/microsoft/playwright/issues/19929#issuecomment-1377035969
  });
});
