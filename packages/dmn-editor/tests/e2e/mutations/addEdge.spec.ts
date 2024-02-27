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
import { EdgePosition, EdgeType } from "../__fixtures__/edges";
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("MUTATIONS - Add edge", () => {
  test.describe("Between Input Data", () => {
    test.beforeEach(({ page }, testInfo) => {
      // FIXME: Input Data node requires to be renamed. Add new issue
      if (testInfo.project.name === "Google Chrome" || testInfo.project.name === "webkit") {
        test.skip(true, "");
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "",
        });
      }
    });

    test("And Decision", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
      });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.DECISION,
      });

      expect(await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.INFORMATION_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Knowledge Source", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
      });
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 300 } });
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
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
      });
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.TEXT_ANNOTATION,
      });

      expect(await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
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

  test.describe("Between Knowledge Source", () => {
    test("And Decision", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: DefaultNodeName.KNOWLEDGE_SOURCE,
        to: DefaultNodeName.DECISION,
      });

      expect(await edges.get({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And BKM", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: DefaultNodeName.KNOWLEDGE_SOURCE,
        to: DefaultNodeName.BKM,
      });

      expect(await edges.get({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.BKM })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.BKM })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Knoledge Source", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Knowledge Source - A",
      });
      await palette.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: "Knowledge Source - B",
      });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: "Knowledge Source - A",
        to: "Knowledge Source - B",
      });

      expect(await edges.get({ from: "Knowledge Source - A", to: "Knowledge Source - B" })).toBeAttached();
      expect(await edges.getType({ from: "Knowledge Source - A", to: "Knowledge Source - B" })).toEqual(
        EdgeType.AUTHORITY_REQUIREMENT
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      // FIXME: Not implemented yet.
      test.skip(true, "");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "",
      });

      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.KNOWLEDGE_SOURCE,
        to: DefaultNodeName.TEXT_ANNOTATION,
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

  test.describe("Between Decision Service", () => {
    test("And Decision", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
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
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And BKM", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
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
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 500, y: 500 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.DECISION_SERVICE,
        to: DefaultNodeName.TEXT_ANNOTATION,
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

  test.describe("Between Group", () => {
    test("And Text Annotation", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 500, y: 500 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.GROUP,
        to: DefaultNodeName.TEXT_ANNOTATION,
      });

      expect(await edges.get({ from: DefaultNodeName.GROUP, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.GROUP, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });
  });

  test.describe("Between Text Annotation", () => {
    test("And Input Node", async ({ diagram, palette, nodes, edges }, testInfo) => {
      // FIXME: Input Data node requires to be renamed. Add new issue
      if (testInfo.project.name === "webkit") {
        test.skip(true, "");
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "",
        });
      }

      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 400 },
      });
      await diagram.get().press("Escape");

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.TEXT_ANNOTATION,
        to: DefaultNodeName.INPUT_DATA,
      });

      expect(await edges.get({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.INPUT_DATA })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.INPUT_DATA })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Decision", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 400 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.TEXT_ANNOTATION,
        to: DefaultNodeName.DECISION,
      });

      expect(await edges.get({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.DECISION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.DECISION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And BKM", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 400 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.TEXT_ANNOTATION,
        to: DefaultNodeName.BKM,
      });

      expect(await edges.get({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.BKM })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.BKM })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Knowledge Source", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 400 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.TEXT_ANNOTATION,
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
      });

      expect(
        await edges.get({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.KNOWLEDGE_SOURCE })
      ).toBeAttached();
      expect(
        await edges.getType({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.KNOWLEDGE_SOURCE })
      ).toEqual(EdgeType.ASSOCIATION);
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Decision Service", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 300, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.TEXT_ANNOTATION,
        to: DefaultNodeName.DECISION_SERVICE,
        position: EdgePosition.TOP,
      });

      expect(
        await edges.get({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.DECISION_SERVICE })
      ).toBeAttached();
      expect(
        await edges.getType({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.DECISION_SERVICE })
      ).toEqual(EdgeType.ASSOCIATION);
      await expect(diagram.get()).toHaveScreenshot();
    });

    test("And Group", async ({ diagram, palette, nodes, edges }) => {
      // FIXME: Not implemented yet. DMN SPEC 1.5 pag 34
      test.skip(true, "");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "",
      });

      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 300, y: 300 } });

      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.TEXT_ANNOTATION,
        to: DefaultNodeName.GROUP,
        position: EdgePosition.TOP,
      });

      expect(await edges.get({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.GROUP })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.GROUP })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot();
    });
  });
});
