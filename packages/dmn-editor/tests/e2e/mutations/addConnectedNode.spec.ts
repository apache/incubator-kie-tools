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
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("MUTATION - Add connected node", () => {
  test.describe("From Input Data", () => {
    test("Add Decision", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.INFORMATION_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Knowledge Source", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 300 },
      });

      expect(
        await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.KNOWLEDGE_SOURCE })
      ).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("From Decision", () => {
    test("Add Decision", async ({ diagram, palette, nodes, edges }) => {
      // Renaming to avoid ambiguity
      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Decision - A",
      });
      await nodes.dragNewConnectedNode({
        from: "Decision - A",
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: "Decision - B",
      });

      expect(await edges.get({ from: "Decision - A", to: "Decision - B" })).toBeAttached();
      expect(await edges.getType({ from: "Decision - A", to: "Decision - B" })).toEqual(
        EdgeType.INFORMATION_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Knowledge Source", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.DECISION,
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.DECISION,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("From BKM", () => {
    test("Add Decision", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.BKM,
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.BKM, to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.BKM, to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.KNOWLEDGE_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add BKM", async ({ diagram, palette, nodes, edges }) => {
      // Renaming to avoid ambiguity
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 }, thenRenameTo: "BKM - A" });
      await nodes.dragNewConnectedNode({
        from: "BKM - A",
        type: NodeType.BKM,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: "BKM - B",
      });

      expect(await edges.get({ from: "BKM - A", to: "BKM - B" })).toBeAttached();
      expect(await edges.getType({ from: "BKM - A", to: "BKM - B" })).toEqual(EdgeType.KNOWLEDGE_REQUIREMENT);
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.BKM,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.BKM, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.BKM, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("From Knowledge Source", () => {
    test("Add Decision", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.KNOWLEDGE_SOURCE,
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add BKM", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.KNOWLEDGE_SOURCE,
        type: NodeType.BKM,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.BKM })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.BKM })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Knowledge Source", async ({ diagram, palette, nodes, edges }) => {
      // Renaming to avoid ambiguity
      await palette.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Knowledge Source - A",
      });
      await nodes.dragNewConnectedNode({
        from: "Knowledge Source - A",
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: "Knowledge Source - B",
      });

      expect(await edges.get({ from: "Knowledge Source - A", to: "Knowledge Source - B" })).toBeAttached();
      expect(await edges.getType({ from: "Knowledge Source - A", to: "Knowledge Source - B" })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      test.skip(true, "");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "",
      });

      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.KNOWLEDGE_SOURCE,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 300 },
      });

      expect(
        await edges.get({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.TEXT_ANNOTATION })
      ).toBeAttached();
      expect(
        await edges.getType({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.TEXT_ANNOTATION })
      ).toEqual(EdgeType.ASSOCIATION);
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("From Decision Service", () => {
    test("Add Decision", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.DECISION_SERVICE,
        type: NodeType.DECISION,
        targetPosition: { x: 500, y: 500 },
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.KNOWLEDGE_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add BKM", async ({ diagram, palette, nodes, edges }) => {
      // Renaming to avoid ambiguity
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.DECISION_SERVICE,
        type: NodeType.BKM,
        targetPosition: { x: 500, y: 500 },
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.BKM })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.BKM })).toEqual(
        EdgeType.KNOWLEDGE_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("Add Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.DECISION_SERVICE,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 500, y: 500 },
      });

      expect(
        await edges.get({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.TEXT_ANNOTATION })
      ).toBeAttached();
      expect(
        await edges.getType({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.TEXT_ANNOTATION })
      ).toEqual(EdgeType.ASSOCIATION);
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("From Group", () => {
    test("Add Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.GROUP,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 500, y: 500 },
      });

      expect(await edges.get({ from: DefaultNodeName.GROUP, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.GROUP, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });
  });
});
