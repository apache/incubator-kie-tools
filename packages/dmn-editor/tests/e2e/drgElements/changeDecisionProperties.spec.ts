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

test.describe("Change Properties - Decision", () => {
  test.beforeEach(async ({ palette, nodes, propertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.DECISION });
    await propertiesPanel.open();
  });

  test("should change the Decision node name", async ({ nodes, generalProperties }) => {
    await generalProperties.changeNodeName({ from: DefaultNodeName.DECISION, to: "Renamed Decision" });

    await expect(nodes.get({ name: "Renamed Decision" })).toBeVisible();
  });

  test("should change the Decision node data type", async ({ nodes, generalProperties }) => {
    await generalProperties.changeNodeDataType({ nodeName: DefaultNodeName.DECISION, newDataType: DataType.Number });

    await nodes.hover({ name: DefaultNodeName.DECISION });
    await expect(nodes.get({ name: DefaultNodeName.DECISION }).getByPlaceholder("Select a data type...")).toHaveValue(
      DataType.Number
    );
  });

  test("should change the Decision node description", async ({ generalProperties }) => {
    await generalProperties.changeNodeDescription({
      nodeName: DefaultNodeName.DECISION,
      newDescription: "New Decision Description",
    });

    expect(await generalProperties.getNodeDescription({ nodeName: DefaultNodeName.DECISION })).toBe(
      "New Decision Description"
    );
  });

  test("should change the Decision node question", async ({ decisionProperties }) => {
    await decisionProperties.changeNodeQuestion({
      nodeName: DefaultNodeName.DECISION,
      newQuestion: "New Decision Question",
    });

    expect(await decisionProperties.getNodeQuestion({ nodeName: DefaultNodeName.DECISION })).toBe(
      "New Decision Question"
    );
  });

  test("should change the Decision node answers", async ({ decisionProperties }) => {
    await decisionProperties.changeNodeAllowedAnswers({
      nodeName: DefaultNodeName.DECISION,
      newAllowedAnswers: "New Allowed Answers",
    });

    expect(await decisionProperties.getNodeAllowedAnswers({ nodeName: DefaultNodeName.DECISION })).toBe(
      "New Allowed Answers"
    );
  });

  test("should change the Decision node documentation links", async ({ generalProperties }) => {
    await generalProperties.addDocumentationLink({
      nodeName: DefaultNodeName.DECISION,
      linkText: "Link Text",
      linkHref: "http://link.test.com",
    });

    const links = await generalProperties.getDocumentationLinks({ nodeName: DefaultNodeName.DECISION });
    expect(links).toHaveLength(1);
    expect(links[0]).toHaveText("Link Text");
    expect(links[0]).toHaveAttribute("href", "http://link.test.com/");
  });

  test("should change the Decision node font - family", async ({ generalProperties }) => {
    await generalProperties.changeNodeFont({ nodeName: DefaultNodeName.DECISION, newFont: "Verdana" });

    expect(await generalProperties.getNodeFont({ nodeName: DefaultNodeName.DECISION })).toBe("Verdana");
  });

  test.skip("should change the Decision node shape - background color", async ({ nodes, propertiesPanel }) => {
    // blocked https://github.com/microsoft/playwright/issues/19929#issuecomment-1377035969
  });
});
