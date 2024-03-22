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

test.describe("Change Properties - Knoledge Source", () => {
  test.beforeEach(async ({ palette, nodes, propertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    await propertiesPanel.open();
  });

  test("should change the Knoledge Source node name", async ({ nodes, propertiesPanel }) => {
    await propertiesPanel.changeNodeName({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: "Renamed Knoledge Source" });

    await expect(nodes.get({ name: "Renamed Knoledge Source" })).toBeVisible();
  });

  test("should change the Knoledge Source node description", async ({ propertiesPanel }) => {
    await propertiesPanel.changeNodeDescription({
      nodeName: DefaultNodeName.KNOWLEDGE_SOURCE,
      newDescription: "New Knoledge Source Description",
    });

    expect(await propertiesPanel.getNodeDescription({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE })).toBe(
      "New Knoledge Source Description"
    );
  });

  test("should change the Knoledge Source node Source Type", async ({ propertiesPanel }) => {
    await propertiesPanel.changeNodeSourceType({
      nodeName: DefaultNodeName.KNOWLEDGE_SOURCE,
      newSourceType: "New Knoledge Source Source Type",
    });

    expect(await propertiesPanel.getNodeSourceType({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE })).toBe(
      "New Knoledge Source Source Type"
    );
  });

  test("should change the Knoledge Source node Location URI", async ({ propertiesPanel }) => {
    await propertiesPanel.changeNodeLocationURI({
      nodeName: DefaultNodeName.KNOWLEDGE_SOURCE,
      newLocationURI: "New Location URI",
    });

    expect(await propertiesPanel.getNodeLocationURI({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE })).toBe(
      "New Location URI"
    );
  });

  test("should change the Knoledge Source node documentation links", async ({ propertiesPanel }) => {
    await propertiesPanel.addDocumentationLink({
      nodeName: DefaultNodeName.KNOWLEDGE_SOURCE,
      linkText: "Link Text",
      linkHref: "http://link.test.com",
    });

    const links = await propertiesPanel.getDocumentationLinks({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE });
    expect(links).toHaveLength(1);
    expect(links[0]).toHaveText("Link Text");
    expect(links[0]).toHaveAttribute("href", "http://link.test.com/");
  });

  test("should change the Knoledge Source node font - family", async ({ propertiesPanel }) => {
    await propertiesPanel.changeNodeFont({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, newFont: "Verdana" });

    expect(await propertiesPanel.getNodeFont({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE })).toBe("Verdana");
  });

  test.skip("should change the Knoledge Source node shape - background color", async ({ nodes, propertiesPanel }) => {
    // blocked https://github.com/microsoft/playwright/issues/19929#issuecomment-1377035969
  });
});
