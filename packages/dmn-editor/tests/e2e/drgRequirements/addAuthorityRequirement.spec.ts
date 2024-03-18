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
import { EdgeType } from "../__fixtures__/edges";
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add edge - Authority Requirement", () => {
  test.describe("Add Authority Requirement edge from Knowledge Source to nodes", () => {
    test.beforeEach(async ({ palette, diagram }) => {
      // Rename to avoid ambuiguity
      await palette.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Knowledge Source - A",
      });
      test.info().annotations.push({
        type: TestAnnotations.WORKAROUND_DUE_TO,
        description: "https://github.com/apache/incubator-kie-issues/issues/980",
      });
      await diagram.resetFocus();
    });

    test("should add an Authority Requirement edge from Knowledge Source node to Decision node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: "Knowledge Source - A",
        to: DefaultNodeName.DECISION,
      });

      expect(await edges.get({ from: "Knowledge Source - A", to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: "Knowledge Source - A", to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-authority-requirement-edge-from-knowledge-source-node-to-decision-node.png"
      );
    });

    test("should add an Authority Requirement edge from Knowledge Source node to BKM node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: "Knowledge Source - A",
        to: DefaultNodeName.BKM,
      });

      expect(await edges.get({ from: "Knowledge Source - A", to: DefaultNodeName.BKM })).toBeAttached();
      expect(await edges.getType({ from: "Knowledge Source - A", to: DefaultNodeName.BKM })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-authority-requirement-edge-from-knowledge-source-node-to-bkm-node.png"
      );
    });

    test("should add an Authority Requirement edge from Knowledge Source node to Knowledge Source node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: DefaultNodeName.KNOWLEDGE_SOURCE,
      });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: "Knowledge Source - A",
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
      });

      expect(await edges.get({ from: "Knowledge Source - A", to: DefaultNodeName.KNOWLEDGE_SOURCE })).toBeAttached();
      expect(await edges.getType({ from: "Knowledge Source - A", to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-authority-requirement-edge-from-knowledge-source-node-to-knowledge-source-node.png"
      );
    });
  });

  test.describe("Add Authority Requirement edge from nodes to Knowledge Source", () => {
    test.beforeEach(async ({ palette, diagram }) => {
      await palette.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 100 },
      });
      test.info().annotations.push({
        type: TestAnnotations.WORKAROUND_DUE_TO,
        description: "https://github.com/apache/incubator-kie-issues/issues/980",
      });
      await diagram.resetFocus();
    });

    test("should add an Authority Requirement edge from Decision node to Knowledge Source node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: DefaultNodeName.DECISION,
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-authority-requirement-edge-from-decision-node-to-knowledge-source-node.png"
      );
    });

    test("should add an Authority Requirement edge from Input Data node to Knowledge Source node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: DefaultNodeName.INPUT_DATA,
      });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
      });

      expect(
        await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.KNOWLEDGE_SOURCE })
      ).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-authority-requirement-edge-from-input-data-node-to-knowledge-source-node.png"
      );
    });
  });
});
