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
import { NodeType } from "../__fixtures__/node";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("MUTATION - Add connected node", () => {
  test.describe("From Input Data", () => {
    test("Add Decision", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedNode({
        from: "New Input Data",
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
      });

      expect(await edge.get({ from: "New Input Data", to: "New Decision" })).toBeAttached();
      expect(await edge.type({ from: "New Input Data", to: "New Decision" })).toEqual("edge_informationRequirement");
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Knowledge Source", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedNode({
        from: "New Input Data",
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 100 },
      });

      expect(await edge.get({ from: "New Input Data", to: "New Knowledge Source" })).toBeAttached();
      expect(await edge.type({ from: "New Input Data", to: "New Knowledge Source" })).toEqual(
        "edge_authorityRequirement"
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Text Annotation", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedNode({
        from: "New Input Data",
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 100 },
      });

      expect(await edge.get({ from: "New Input Data", to: "New Text Annotation" })).toBeAttached();
      expect(await edge.type({ from: "New Input Data", to: "New Text Annotation" })).toEqual("edge_association");
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("From Decision", () => {
    test("Add Decision", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedNode({
        from: "New Decision",
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
      });

      expect(await edge.get({ from: "New Decision", to: "New Decision" })).toBeAttached();
      expect(await edge.type({ from: "New Decision", to: "New Decision" })).toEqual("edge_informationRequirement");
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Knowledge Source", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedNode({
        from: "New Decision",
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 100 },
      });

      expect(await edge.get({ from: "New Decision", to: "New Knowledge Source" })).toBeAttached();
      expect(await edge.type({ from: "New Decision", to: "New Knowledge Source" })).toEqual(
        "edge_authorityRequirement"
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Text Annotation", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedNode({
        from: "New Decision",
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 100 },
      });

      expect(await edge.get({ from: "New Decision", to: "New Text Annotation" })).toBeAttached();
      expect(await edge.type({ from: "New Decision", to: "New Text Annotation" })).toEqual("edge_association");
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("From BKM", () => {
    test("Add Decision", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.BKM,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedNode({
        from: "New BKM",
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
      });

      expect(await edge.get({ from: "New BKM", to: "New Decision" })).toBeAttached();
      expect(await edge.type({ from: "New BKM", to: "New Decision" })).toEqual("edge_knowledgeRequirement");
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add BKM", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.BKM,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedNode({
        from: "New BKM",
        type: NodeType.BKM,
        targetPosition: { x: 100, y: 100 },
      });

      expect(await edge.get({ from: "New BKM", to: "New BKM" })).toBeAttached();
      expect(await edge.type({ from: "New BKM", to: "New BKM" })).toEqual("edge_knowledgeRequirement");
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Text Annotation", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.BKM,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedNode({
        from: "New BKM",
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 100 },
      });

      expect(await edge.get({ from: "New BKM", to: "New Text Annotation" })).toBeAttached();
      expect(await edge.type({ from: "New BKM", to: "New Text Annotation" })).toEqual("edge_association");
      await expect(diagram.get()).toHaveScreenshot();
    });
  });
});
