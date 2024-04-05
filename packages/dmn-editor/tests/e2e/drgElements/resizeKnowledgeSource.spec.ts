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

test.describe("Resize node - Knowledge Source", () => {
  test.beforeEach(async ({ overlays, palette }) => {
    await overlays.turnOffSnapping();
    await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
  });

  test("should increase Knowledge Source node size", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
    await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 50, yOffset: 50 });

    await knowledgeSourcePropertiesPanel.open();
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
    expect(width).toEqual("210");
    expect(height).toEqual("130");
  });

  test("should decrease Knowledge Source node size", async ({ nodes, knowledgeSourcePropertiesPanel }) => {
    await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: 100, yOffset: 100 });
    await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: -20, yOffset: -20 });

    await knowledgeSourcePropertiesPanel.open();
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
    expect(width).toEqual("240");
    expect(height).toEqual("160");
  });

  test("should not decrease below minimal Knowledge Source node size", async ({
    nodes,
    knowledgeSourcePropertiesPanel,
  }) => {
    await nodes.resize({ nodeName: DefaultNodeName.KNOWLEDGE_SOURCE, xOffset: -50, yOffset: -50 });

    await knowledgeSourcePropertiesPanel.open();
    await nodes.select({ name: DefaultNodeName.KNOWLEDGE_SOURCE });
    const { width, height } = await knowledgeSourcePropertiesPanel.getShape();
    expect(width).toEqual("160");
    expect(height).toEqual("80");
  });
});
