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

test.beforeEach(async ({ editor, page }) => {
  await page.setViewportSize({ width: 1920, height: 1080 });
  await editor.open();
});

test.describe("Change Properties - Data Object", () => {
  test.beforeEach(async ({ palette, nodes, page }) => {
    await palette.dragNewNode({ type: NodeType.DATA_OBJECT, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.get({ name: DefaultNodeName.DATA_OBJECT })).toBeAttached();

    await nodes.get({ name: DefaultNodeName.DATA_OBJECT }).click();
  });

  test("should change the Data Object name", async ({ dataObjectPropertiesPanel }) => {
    await dataObjectPropertiesPanel.nameProperties.setName({ newName: "Customer Data" });

    expect(await dataObjectPropertiesPanel.nameProperties.getName()).toBe("Customer Data");
  });

  test("should change the Data Object documentation", async ({ dataObjectPropertiesPanel }) => {
    await dataObjectPropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "Contains customer information for processing",
    });

    expect(await dataObjectPropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "Contains customer information for processing"
    );
  });

  test("should set item subject reference", async ({ dataObjectPropertiesPanel }) => {
    await dataObjectPropertiesPanel.setItemSubjectRef({ itemSubjectRef: "tCustomer" });

    expect(await dataObjectPropertiesPanel.getItemSubjectRef()).toBe("tCustomer");
  });
});
