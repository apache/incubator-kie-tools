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
import { EdgeType } from "../__fixtures__/edges";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("MUTATION - Delete node", () => {
  test.describe("Standalone", () => {
    test("Input Data", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.delete({ name: DefaultNodeName.INPUT_DATA });

      await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).not.toBeAttached();
    });

    test("Decision", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await nodes.delete({ name: DefaultNodeName.DECISION });

      await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
    });

    test("Text Annotation", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      await nodes.delete({ name: DefaultNodeName.TEXT_ANNOTATION });

      await expect(nodes.get({ name: DefaultNodeName.TEXT_ANNOTATION })).not.toBeAttached();
    });

    test("Decision Service", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
      await nodes.delete({ name: DefaultNodeName.DECISION_SERVICE });

      await expect(nodes.get({ name: DefaultNodeName.DECISION_SERVICE })).not.toBeAttached();
    });

    test("BKM", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });
      await nodes.delete({ name: DefaultNodeName.BKM });

      await expect(nodes.get({ name: DefaultNodeName.BKM })).not.toBeAttached();
    });

    test("Knowledge Source", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
      await nodes.delete({ name: DefaultNodeName.KNOWLEDGE_SOURCE });

      await expect(nodes.get({ name: DefaultNodeName.KNOWLEDGE_SOURCE })).not.toBeAttached();
    });
  });

  test.describe("Container", () => {
    test("Group", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 130, y: 130 } });

      await expect(nodes.get({ name: DefaultNodeName.GROUP })).toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();

      await nodes.delete({ name: DefaultNodeName.GROUP });

      await expect(nodes.get({ name: DefaultNodeName.GROUP })).not.toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
    });

    test("Decision Service", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 130, y: 130 } });

      await expect(nodes.get({ name: DefaultNodeName.DECISION_SERVICE })).toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();

      await nodes.delete({ name: DefaultNodeName.DECISION_SERVICE });

      await expect(nodes.get({ name: DefaultNodeName.DECISION_SERVICE })).not.toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
    });
  });

  test.describe("With Single Relationship", () => {
    test("From", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });

      await nodes.delete({ name: DefaultNodeName.INPUT_DATA });

      await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).not.toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
    });

    test("To", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });

      await nodes.delete({ name: DefaultNodeName.DECISION });

      await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
    });

    test("From and To", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });

      await nodes.delete({ name: DefaultNodeName.INPUT_DATA });
      await nodes.delete({ name: DefaultNodeName.DECISION });

      await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).not.toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
    });
  });

  test.describe("With Multiple Relationships", () => {
    test("Single From", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: "Decision One",
      });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.DECISION,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Decision Two",
      });

      await nodes.delete({ name: DefaultNodeName.INPUT_DATA });

      await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).not.toBeAttached();
      await expect(nodes.get({ name: "Decision One" })).toBeAttached();
      await expect(nodes.get({ name: "Decision Two" })).toBeAttached();
    });

    test("Single To", async ({ palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Input One",
      });
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 300, y: 100 },
        thenRenameTo: "Input Two",
      });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: "Input One",
        to: DefaultNodeName.DECISION,
      });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: "Input Two",
        to: DefaultNodeName.DECISION,
      });

      await nodes.delete({ name: DefaultNodeName.DECISION });

      await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
      await expect(nodes.get({ name: "Input One" })).toBeAttached();
      await expect(nodes.get({ name: "Input Two" })).toBeAttached();
    });
  });
});
