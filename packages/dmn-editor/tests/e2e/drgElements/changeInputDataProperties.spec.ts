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
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Change Properties - Input Data", () => {
  test.beforeEach(async ({ palette, nodes, inputDataPropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.INPUT_DATA });
    await inputDataPropertiesPanel.open();
  });

  test("should change the Input Data node name", async ({ nodes, inputDataPropertiesPanel }) => {
    await inputDataPropertiesPanel.setName({ from: DefaultNodeName.INPUT_DATA, to: "Renamed Input Data" });

    await expect(nodes.get({ name: "Renamed Input Data" })).toBeVisible();
    expect(await inputDataPropertiesPanel.getName({ nodeName: "Renamed Input Data" })).toBe("Renamed Input Data");
  });

  test("should change the Input Data node data type", async ({ nodes, inputDataPropertiesPanel }) => {
    await inputDataPropertiesPanel.setDataType({ nodeName: DefaultNodeName.INPUT_DATA, newDataType: DataType.Number });

    await nodes.hover({ name: DefaultNodeName.INPUT_DATA });
    await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA }).getByPlaceholder("Select a data type...")).toHaveValue(
      DataType.Number
    );
  });

  test("should change the Input Data node description", async ({ inputDataPropertiesPanel }) => {
    await inputDataPropertiesPanel.setDescription({
      nodeName: DefaultNodeName.INPUT_DATA,
      newDescription: "New Input Data Description",
    });

    expect(await inputDataPropertiesPanel.getDescription({ nodeName: DefaultNodeName.INPUT_DATA })).toBe(
      "New Input Data Description"
    );
  });

  test("should change the Input Data node documentation links", async ({ inputDataPropertiesPanel }) => {
    await inputDataPropertiesPanel.addDocumentationLink({
      nodeName: DefaultNodeName.INPUT_DATA,
      linkText: "Link Text",
      linkHref: "http://link.test.com",
    });

    const links = await inputDataPropertiesPanel.getDocumentationLinks({ nodeName: DefaultNodeName.INPUT_DATA });
    expect(links).toHaveLength(1);
    expect(links[0]).toHaveText("Link Text");
    expect(links[0]).toHaveAttribute("href", "http://link.test.com/");
  });

  test("should change the Input Data node font - family", async ({ inputDataPropertiesPanel }) => {
    await inputDataPropertiesPanel.setFont({ nodeName: DefaultNodeName.INPUT_DATA, newFont: "Verdana" });

    expect(await inputDataPropertiesPanel.getFont({ nodeName: DefaultNodeName.INPUT_DATA })).toBe("Verdana");
  });

  test.skip("should change the Input Data node shape - background color", async ({
    nodes,
    inputDataPropertiesPanel,
  }) => {
    // blocked https://github.com/microsoft/playwright/issues/19929#issuecomment-1377035969
  });
});
