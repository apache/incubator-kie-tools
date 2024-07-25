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

test.describe("Add edge - Knowledge Requirement", () => {
  test.describe("Add Knowledge Requirement edge from BKM to nodes", () => {
    test.beforeEach(async ({ palette, diagram }) => {
      // Rename to avoid ambuiguity
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 }, thenRenameTo: "BKM - A" });
      test.info().annotations.push({
        type: TestAnnotations.WORKAROUND_DUE_TO,
        description: "https://github.com/apache/incubator-kie-issues/issues/980",
      });
      await diagram.resetFocus();
    });

    test("should add a Knowledge Requirement edge from BKM node to Decision node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.KNOWLEDGE_REQUIREMENT,
        from: "BKM - A",
        to: DefaultNodeName.DECISION,
      });

      expect(await edges.get({ from: "BKM - A", to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: "BKM - A", to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.KNOWLEDGE_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot("add-knowledge-requirement-edge-from-bkm-node-to-decision-node.png");
    });

    test("should add a Knowledge Requirement edge from BKM node to BKM node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.KNOWLEDGE_REQUIREMENT,
        from: "BKM - A",
        to: DefaultNodeName.BKM,
      });

      expect(await edges.get({ from: "BKM - A", to: DefaultNodeName.BKM })).toBeAttached();
      expect(await edges.getType({ from: "BKM - A", to: DefaultNodeName.BKM })).toEqual(EdgeType.KNOWLEDGE_REQUIREMENT);
      await expect(diagram.get()).toHaveScreenshot("add-knowledge-requirement-edge-from-bkm-node-to-bkm-node.png");
    });
  });

  test.describe("Add Knowledge Requirement edge from Decision Service to nodes", () => {
    test.beforeEach(async ({ palette, diagram }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
      test.info().annotations.push({
        type: TestAnnotations.WORKAROUND_DUE_TO,
        description: "https://github.com/apache/incubator-kie-issues/issues/980",
      });
      await diagram.resetFocus();
    });

    test("should add a Knowledge Requirement edge from Decision Service node to Decision node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 500, y: 500 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.KNOWLEDGE_REQUIREMENT,
        from: DefaultNodeName.DECISION_SERVICE,
        to: DefaultNodeName.DECISION,
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.KNOWLEDGE_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-knowledge-requirement-edge-from-decision-service-node-to-decision-node.png"
      );
    });

    test("should add a Knowledge Requirement edge from Decision Service node to BKM node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 500, y: 500 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.KNOWLEDGE_REQUIREMENT,
        from: DefaultNodeName.DECISION_SERVICE,
        to: DefaultNodeName.BKM,
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.BKM })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.BKM })).toEqual(
        EdgeType.KNOWLEDGE_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-knowledge-requirement-edge-from-decision-service-node-to-bkm-node.png"
      );
    });
  });
});
