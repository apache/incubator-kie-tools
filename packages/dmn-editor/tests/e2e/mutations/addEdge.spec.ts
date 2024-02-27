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

test.describe("MUTATIONS - Add edge", () => {
  test.describe("Between Input Data", () => {
    test("And Decision", async ({ diagram, palette, nodes, edges }) => {
      // FIXME: Input Data node requires to be renamed.
      // As they're created the editor selects it causing a bug on the palette.
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Input",
      });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: "Input",
        to: DefaultNodeName.DECISION,
      });

      expect(await edges.get({ from: "Input", to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: "Input", to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.INFORMATION_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Knowledge Source", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Input",
      });
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: "Input",
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
      });

      expect(await edges.get({ from: "Input", to: DefaultNodeName.KNOWLEDGE_SOURCE })).toBeAttached();
      expect(await edges.getType({ from: "Input", to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Text Annotation", async ({ page, diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Input",
      });
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: "Input",
        to: DefaultNodeName.TEXT_ANNOTATION,
      });

      expect(await edges.get({ from: "Input", to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: "Input", to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(EdgeType.ASSOCIATION);
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("Between Decision", () => {
    test("And Decision", async ({ diagram, palette, nodes, edges }) => {
      // Rename to avoid ambiguity
      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Decision - A",
      });
      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: "Decision - B",
      });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: "Decision - A",
        to: "Decision - B",
      });

      expect(await edges.get({ from: "Decision - A", to: "Decision - B" })).toBeAttached();
      expect(await edges.getType({ from: "Decision - A", to: "Decision - B" })).toEqual(
        EdgeType.INFORMATION_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Knoledge Source", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 300 },
      });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: DefaultNodeName.DECISION,
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.DECISION,
        to: DefaultNodeName.TEXT_ANNOTATION,
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("Between BKM", () => {
    test("And Decision", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.KNOWLEDGE_REQUIREMENT,
        from: DefaultNodeName.BKM,
        to: DefaultNodeName.DECISION,
      });

      expect(await edges.get({ from: DefaultNodeName.BKM, to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.BKM, to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.KNOWLEDGE_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And BKM", async ({ diagram, palette, nodes, edges }) => {
      // Rename to avoid ambiguity
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 }, thenRenameTo: "BKM - A" });
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 300 }, thenRenameTo: "BKM - B" });
      await nodes.dragNewConnectedEdge({ type: EdgeType.KNOWLEDGE_REQUIREMENT, from: "BKM - A", to: "BKM - B" });

      expect(await edges.get({ from: "BKM - A", to: "BKM - B" })).toBeAttached();
      expect(await edges.getType({ from: "BKM - A", to: "BKM - B" })).toEqual(EdgeType.KNOWLEDGE_REQUIREMENT);
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.BKM,
        to: DefaultNodeName.TEXT_ANNOTATION,
      });

      expect(await edges.get({ from: DefaultNodeName.BKM, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.BKM, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });
  });
});
