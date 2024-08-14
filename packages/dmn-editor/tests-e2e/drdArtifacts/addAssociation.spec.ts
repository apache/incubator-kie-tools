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
import { DefaultNodeName, NodePosition, NodeType } from "../__fixtures__/nodes";
import { EdgeType } from "../__fixtures__/edges";
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add edge - Association", () => {
  test.describe("Add Association edge from nodes to Text Annotation node", () => {
    test.beforeEach(async ({ diagram, palette }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      test.info().annotations.push({
        type: TestAnnotations.WORKAROUND_DUE_TO,
        description: "https://github.com/apache/incubator-kie-issues/issues/980",
      });
      await diagram.resetFocus();
    });

    test("should add an Association edge from BKM node to Text Annotation node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 400 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.BKM,
        to: DefaultNodeName.TEXT_ANNOTATION,
      });

      expect(await edges.get({ from: DefaultNodeName.BKM, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.BKM, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot("add-association-edge-from-bkm-node-to-text-annotation-node.png");
    });

    test("should add an association edge from Decision node to Text Annotation node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 400 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.DECISION,
        to: DefaultNodeName.TEXT_ANNOTATION,
      });

      expect(await edges.get({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-association-edge-from-decision-node-to-text-annotation-node.png"
      );
    });

    test("should add an association edge from Knowledge Source node to Text Annotation node", async ({
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

      await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 400 } });
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
      await expect(diagram.get()).toHaveScreenshot(
        "add-association-edge-from-knowledge-source-node-to-text-annotation-node.png"
      );
    });

    test("should add an association edge from Input Data node to Text Annotation node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 400 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.TEXT_ANNOTATION,
      });

      expect(await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-association-edge-from-input-data-node-to-text-annotation-node.png"
      );
    });

    test("should add an association edge from Decision Service node to Text Annotation node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 500, y: 500 } });
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
      await expect(diagram.get()).toHaveScreenshot(
        "add-association-edge-from-decision-service-node-to-text-annotation-node.png"
      );
    });

    test("should add an association edge from Group node to Text Annotation node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 500, y: 500 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.GROUP,
        to: DefaultNodeName.TEXT_ANNOTATION,
      });

      expect(await edges.get({ from: DefaultNodeName.GROUP, to: DefaultNodeName.TEXT_ANNOTATION })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.GROUP, to: DefaultNodeName.TEXT_ANNOTATION })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot("add-association-edge-from-group-node-to-text-annotation-node.png");
    });
  });

  test.describe("Add association edge from Text Annotation to nodes", () => {
    test.beforeEach(async ({ diagram, palette }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });
      test.info().annotations.push({
        type: TestAnnotations.WORKAROUND_DUE_TO,
        description: "https://github.com/apache/incubator-kie-issues/issues/980",
      });
      await diagram.resetFocus();
    });

    test("should add an association edge from Text Annotation node to BKM node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
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
      await expect(diagram.get()).toHaveScreenshot("add-association-edge-from-text-annotation-node-to-bkm-node.png");
    });

    test("should add an association edge from Text Annotation node to Decision node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
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
      await expect(diagram.get()).toHaveScreenshot(
        "add-association-edge-from-text-annotation-node-to-decision-node.png"
      );
    });

    test("should add an association edge from Text Annotation node to Knowledge Source node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
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
      await expect(diagram.get()).toHaveScreenshot(
        "add-association-edge-from-text-annotation-node-to-knowledge-source-node.png"
      );
    });

    test("should add an association edge from Text Annotation node to Input Data node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 400 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.TEXT_ANNOTATION,
        to: DefaultNodeName.INPUT_DATA,
      });

      expect(await edges.get({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.INPUT_DATA })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.INPUT_DATA })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot(
        "add-association-edge-from-text-annotation-node-to-input-data-node.png"
      );
    });

    test("should add an association edge from Text Annotation node to Decision Service node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 500, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.TEXT_ANNOTATION,
        to: DefaultNodeName.DECISION_SERVICE,
        position: NodePosition.TOP,
      });

      expect(
        await edges.get({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.DECISION_SERVICE })
      ).toBeAttached();
      expect(
        await edges.getType({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.DECISION_SERVICE })
      ).toEqual(EdgeType.ASSOCIATION);
      await expect(diagram.get()).toHaveScreenshot(
        "add-association-edge-from-text-annotation-node-to-decision-service-node.png"
      );
    });

    test("should add an association edge from Text Annotation node to Group node", async ({
      diagram,
      palette,
      nodes,
      edges,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/982",
      });
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 400, y: 400 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.TEXT_ANNOTATION,
        to: DefaultNodeName.GROUP,
        position: NodePosition.TOP,
      });

      expect(await edges.get({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.GROUP })).toBeAttached();
      expect(await edges.getType({ from: DefaultNodeName.TEXT_ANNOTATION, to: DefaultNodeName.GROUP })).toEqual(
        EdgeType.ASSOCIATION
      );
      await expect(diagram.get()).toHaveScreenshot("add-association-edge-from-text-annotation-node-to-group-node.png");
    });
  });
});
