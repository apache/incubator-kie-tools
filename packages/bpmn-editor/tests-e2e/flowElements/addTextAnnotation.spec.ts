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
import { NodeType, NodePosition } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Text Annotation", () => {
  test.describe("Add from palette", () => {
    test("should add Text Annotation node from palette", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 100, y: 100 } });

      await expect(nodes.getByType(NodeType.TEXT_ANNOTATION)).toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.artifact?.length).toBeGreaterThan(0);
    });

    test("should add two Text Annotation nodes from palette in a row", async ({
      palette,
      diagram,
      nodes,
      jsonModel,
    }) => {
      await palette.dragNewNode({
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 100, y: 100 },
      });
      await palette.dragNewNode({
        type: NodeType.TEXT_ANNOTATION,
        targetPosition: { x: 300, y: 300 },
      });

      await diagram.resetFocus();

      await expect(nodes.getByType(NodeType.TEXT_ANNOTATION).first()).toBeAttached();
      await expect(nodes.getByType(NodeType.TEXT_ANNOTATION).nth(1)).toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.artifact?.length).toBe(2);
    });
  });

  test.describe("Text Annotation operations", () => {
    test("should delete text annotation", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 300, y: 300 } });

      await nodes.deleteByType({ type: NodeType.TEXT_ANNOTATION });

      const process = await jsonModel.getProcess();
      expect(process.artifact?.length || 0).toBe(0);
    });

    test("should move text annotation to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 300, y: 300 } });

      await expect(nodes.getByType(NodeType.TEXT_ANNOTATION)).toBeAttached();

      await nodes.getByType(NodeType.TEXT_ANNOTATION).scrollIntoViewIfNeeded();

      const textAnnotationId = await nodes.getIdByType(NodeType.TEXT_ANNOTATION);
      const textAnnotationBox = await nodes.getNodeBounds({ id: textAnnotationId });

      await nodes.dragNodeToPosition({
        id: textAnnotationId,
        fromPosition: NodePosition.LEFT,
        toPosition: { x: 500, y: 100 },
      });

      const boxAfter = await nodes.getNodeBounds({ id: textAnnotationId });
      expect(boxAfter.x).not.toBe(textAnnotationBox.x);
      expect(boxAfter.y).not.toBe(textAnnotationBox.y);
    });
  });
});
