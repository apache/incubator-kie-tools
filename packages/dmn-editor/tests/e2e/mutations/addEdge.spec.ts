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
import { EdgeType } from "../__fixtures__/edge";

test.beforeEach(async ({ editor }, testInfo) => {
  await editor.open();
});

test.describe("MUTATIONS - Add edge", () => {
  test.describe("Between Input Data", () => {
    test("And Decision", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
      });
      await pallete.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });

      await node.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: "New Input Data",
        to: "New Decision",
      });

      expect(await edge.get({ from: "New Input Data", to: "New Decision" })).toBeAttached();
      expect(await edge.type({ from: "New Input Data", to: "New Decision" })).toEqual("edge_informationRequirement");
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Knowledge Source", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 200, y: 200 },
      });
      await pallete.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 200, y: 400 },
      });
      await node.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: "New Input Data",
        to: "New Knowledge Source",
      });

      expect(await edge.get({ from: "New Input Data", to: "New Knowledge Source" })).toBeAttached();
      expect(await edge.type({ from: "New Input Data", to: "New Knowledge Source" })).toEqual(
        "edge_authorityRequirement"
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Text Annotation", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
      });
      await pallete.dragNewNode({
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: "New Input Data",
        to: "New Text Annotation",
      });

      expect(await edge.get({ from: "New Input Data", to: "New Text Annotation" })).toBeAttached();
      expect(await edge.type({ from: "New Input Data", to: "New Text Annotation" })).toEqual("edge_association");
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("Between Decision", () => {
    test("And Decision", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
      });
      await node.rename({ current: "New Decision", new: "My Decision" });
      await pallete.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });

      await node.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: "My Decision",
        to: "New Decision",
      });

      expect(await edge.get({ from: "My Decision", to: "New Decision" })).toBeAttached();
      expect(await edge.type({ from: "My Decision", to: "New Decision" })).toEqual("edge_informationRequirement");
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Knoledge Source", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 200, y: 200 },
      });
      await pallete.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 200, y: 400 },
      });
      await node.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: "New Decision",
        to: "New Knowledge Source",
      });

      expect(await edge.get({ from: "New Decision", to: "New Knowledge Source" })).toBeAttached();
      expect(await edge.type({ from: "New Decision", to: "New Knowledge Source" })).toEqual(
        "edge_authorityRequirement"
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Text Annotation", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
      });
      await pallete.dragNewNode({
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: "New Decision",
        to: "New Text Annotation",
      });

      expect(await edge.get({ from: "New Decision", to: "New Text Annotation" })).toBeAttached();
      expect(await edge.type({ from: "New Decision", to: "New Text Annotation" })).toEqual("edge_association");
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("Between BKM", () => {
    test("And Decision", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.BKM,
        targetPosition: { x: 100, y: 100 },
      });
      await pallete.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });

      await node.dragNewConnectedEdge({
        type: EdgeType.KNOWLEDGE_REQUIREMENT,
        from: "New BKM",
        to: "New Decision",
      });

      expect(await edge.get({ from: "New BKM", to: "New Decision" })).toBeAttached();
      expect(await edge.type({ from: "New BKM", to: "New Decision" })).toEqual("edge_knowledgeRequirement");
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And BKM", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.BKM,
        targetPosition: { x: 200, y: 200 },
      });
      await node.rename({ current: "New BKM", new: "My BKM" });
      await pallete.dragNewNode({
        type: NodeType.BKM,
        targetPosition: { x: 200, y: 400 },
      });
      await node.dragNewConnectedEdge({
        type: EdgeType.KNOWLEDGE_REQUIREMENT,
        from: "My BKM",
        to: "New BKM",
      });

      expect(await edge.get({ from: "My BKM", to: "New BKM" })).toBeAttached();
      expect(await edge.type({ from: "My BKM", to: "New BKM" })).toEqual("edge_knowledgeRequirement");
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Text Annotation", async ({ diagram, pallete, node, edge }) => {
      await pallete.dragNewNode({
        type: NodeType.BKM,
        targetPosition: { x: 100, y: 100 },
      });
      await pallete.dragNewNode({
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 300 },
      });
      await node.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: "New BKM",
        to: "New Text Annotation",
      });

      expect(await edge.get({ from: "New BKM", to: "New Text Annotation" })).toBeAttached();
      expect(await edge.type({ from: "New BKM", to: "New Text Annotation" })).toEqual("edge_association");
      await expect(diagram.get()).toHaveScreenshot();
    });
  });
});
