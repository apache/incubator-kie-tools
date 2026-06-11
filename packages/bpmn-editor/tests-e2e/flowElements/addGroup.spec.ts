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

test.describe("Add node - Group", () => {
  test.describe("Add from palette", () => {
    test("should add Group node from palette", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
      await expect(nodes.getByType(NodeType.GROUP)).toBeAttached();

      const groups = await jsonModel.getGroups();
      expect(groups.length).toBe(1);
    });

    test("should add two Group nodes from palette in a row", async ({ palette, diagram, nodes, jsonModel }) => {
      await palette.dragNewNode({
        type: NodeType.GROUP,
        targetPosition: { x: 100, y: 100 },
      });
      await palette.dragNewNode({
        type: NodeType.GROUP,
        targetPosition: { x: 300, y: 300 },
      });

      await diagram.resetFocus();
      await expect(nodes.getByType(NodeType.GROUP).first()).toBeAttached();
      await expect(nodes.getByType(NodeType.GROUP).nth(1)).toBeAttached();

      const groups = await jsonModel.getGroups();
      expect(groups.length).toBe(2);
    });
  });

  test.describe("Group operations", () => {
    test("should delete group", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 300, y: 300 } });
      const groupId = await nodes.getIdByType(NodeType.GROUP);
      await nodes.delete({ name: groupId });
      await expect(nodes.getByType(NodeType.GROUP)).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process?.artifact?.length).toBe(0);
    });

    test("should move group to new position", async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 300, y: 300 } });
      await expect(nodes.getByType(NodeType.GROUP)).toBeAttached();

      await nodes.getByType(NodeType.GROUP).scrollIntoViewIfNeeded();
      const groupId = await nodes.getIdByType(NodeType.GROUP);
      const groupBox = await nodes.getNodeBounds({ id: groupId });
      await nodes.dragNodeToPosition({
        id: groupId,
        fromPosition: NodePosition.LEFT,
        toPosition: { x: 500, y: 400 },
      });

      const boxAfter = await nodes.getNodeBounds({ id: groupId });
      expect(boxAfter.x).not.toBe(groupBox.x);
      expect(boxAfter.y).not.toBe(groupBox.y);
    });
  });
});
