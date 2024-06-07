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

import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { test, expect } from "../__fixtures__/base";
import { EdgeType } from "../__fixtures__/edges";
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Text Annotation", () => {
  test.describe("add from the palette", () => {
    test("should add Text Annotation node from palette", async ({ palette, nodes, diagram }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });

      expect(nodes.get({ name: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      await expect(diagram.get()).toHaveScreenshot("add-text-annotation-node-from-palette.png");
    });
  });

  test.describe("add from nodes", () => {
    test("should add connected Text Annotation node from Input Data node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
      });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot("add-text-annotation-from-input-data.png");
    });

    test("should add connected Text Annotation node from Decision node", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
      });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.DECISION,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 300 },
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot("add-text-annotation-from-decision.png");
    });

    test("should add connected Text Annotation node from BKM node", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({
        type: NodeType.BKM,
        targetPosition: { x: 100, y: 100 },
      });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.BKM,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 500, y: 500 },
      });

      expect(await edges.get({ from: DefaultNodeName.BKM, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.BKM, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot("add-text-annotation-from-bkm.png");
    });

    test("should add connected Text Annotation node from Decision Service node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({
        type: NodeType.DECISION_SERVICE,
        targetPosition: { x: 100, y: 100 },
      });
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
      await expect(diagram.get()).toHaveScreenshot("add-text-annotation-from-decision-service.png");
    });

    test("should add connected Text Annotation node from Knowledge Source node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/981");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/981",
      });

      await palette.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 100 },
      });
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
      await expect(diagram.get()).toHaveScreenshot("add-text-annotation-from-knowledge-source.png");
    });

    test("should add connected Text Annotation node from Group node", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({
        type: NodeType.GROUP,
        targetPosition: { x: 100, y: 100 },
      });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.GROUP,
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 500, y: 500 },
      });

      expect(await edges.get({ from: DefaultNodeName.GROUP, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.GROUP, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot("add-text-annotation-from-group.png");
    });
  });
});
