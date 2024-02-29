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

import { expect } from "@playwright/test";
import { test } from "../__fixtures__/base";
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("MUTATION - Add standalone node", () => {
  test("Input Data", async ({ palette, nodes, diagram }) => {
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });

    expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot();
  });

  test("Decision", async ({ palette, nodes, diagram }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });

    expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot();
  });

  test("BKM", async ({ palette, nodes, diagram }) => {
    await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });

    expect(nodes.get({ name: DefaultNodeName.BKM })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot();
  });

  test("Knowledge Source", async ({ palette, nodes, diagram }) => {
    await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });

    expect(nodes.get({ name: DefaultNodeName.KNOWLEDGE_SOURCE })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot();
  });

  test("Decision Service", async ({ palette, nodes, diagram }) => {
    await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });

    expect(nodes.get({ name: DefaultNodeName.DECISION_SERVICE })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot();
  });

  test("Group", async ({ palette, nodes, diagram }) => {
    await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });

    expect(nodes.get({ name: DefaultNodeName.GROUP })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot();
  });

  test("Text Annotation", async ({ palette, nodes, diagram }) => {
    await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });

    expect(nodes.get({ name: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot();
  });
});
