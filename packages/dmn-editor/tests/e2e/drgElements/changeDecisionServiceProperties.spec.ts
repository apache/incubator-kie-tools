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
    await decisionServicePropertiesPanel.setName({
      from: DefaultNodeName.DECISION_SERVICE,
      to: "Renamed Decision Service",
    });

    await expect(nodes.get({ name: "Renamed Decision Service" })).toBeVisible();
    expect(await decisionServicePropertiesPanel.getName({ nodeName: "Renamed Decision Service" })).toBe(
      "Renamed Decision Service"
    );
  });

  test("should change the Decision Service node data type", async ({ nodes, decisionServicePropertiesPanel }) => {
    await decisionServicePropertiesPanel.setDataType({
      nodeName: DefaultNodeName.DECISION_SERVICE,
      newDataType: DataType.Number,
    });

    await nodes.hover({ name: DefaultNodeName.DECISION_SERVICE });
    await expect(
      nodes.get({ name: DefaultNodeName.DECISION_SERVICE }).getByPlaceholder("Select a data type...")
    ).toHaveValue(DataType.Number);
  });

  test("should change the Decision Service node description", async ({ decisionServicePropertiesPanel }) => {
    await decisionServicePropertiesPanel.setDescription({
      nodeName: DefaultNodeName.DECISION_SERVICE,
      newDescription: "New Decision Service Description",
    });

    expect(
      await decisionServicePropertiesPanel.getNodeDescription({ nodeName: DefaultNodeName.DECISION_SERVICE })
    ).toBe("New Decision Service Description");
  });

  test("should change the Decision Service node documentation links", async ({ decisionServicePropertiesPanel }) => {
    await decisionServicePropertiesPanel.addDocumentationLink({
      nodeName: DefaultNodeName.DECISION_SERVICE,
      linkText: "Link Text",
      linkHref: "http://link.test.com",
    });

    const links = await decisionServicePropertiesPanel.getDocumentationLinks({
      nodeName: DefaultNodeName.DECISION_SERVICE,
    });
    expect(links).toHaveLength(1);
    expect(links[0]).toHaveText("Link Text");
    expect(links[0]).toHaveAttribute("href", "http://link.test.com/");
  });

  test("should change the Decision Service node font - family", async ({ decisionServicePropertiesPanel }) => {
    await decisionServicePropertiesPanel.setFont({
      nodeName: DefaultNodeName.DECISION_SERVICE,
      newFont: "Verdana",
    });

    expect(await decisionServicePropertiesPanel.getNodeFont({ nodeName: DefaultNodeName.DECISION_SERVICE })).toBe(
      "Verdana"
    );
  });

  test.skip("should change the Decision Service node shape - background color", async ({
    nodes,
    decisionServicePropertiesPanel,
  }) => {
    // blocked https://github.com/microsoft/playwright/issues/19929#issuecomment-1377035969
  });
});
