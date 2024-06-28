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

import { test, expect } from "./__fixtures__/base";
import { DefaultNodeName, NodeType } from "./__fixtures__/nodes";
import { EdgeType } from "./__fixtures__/edges";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Delete node - Connected nodes", () => {
  test.describe("Connected to one node", () => {
    test.beforeEach(async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedNode({
        from: DefaultNodeName.INPUT_DATA,
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
      });
    });

    test('should delete "from" and not affect "to"', async ({ nodes }) => {
      await nodes.delete({ name: DefaultNodeName.INPUT_DATA });

      await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).not.toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
    });

    test('should delete "to" and not affect "from"', async ({ nodes }) => {
      await nodes.delete({ name: DefaultNodeName.DECISION });

      await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
    });

    test('should delete "from" and "to"', async ({ nodes }) => {
      await nodes.delete({ name: DefaultNodeName.INPUT_DATA });
      await nodes.delete({ name: DefaultNodeName.DECISION });

      await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).not.toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
    });
  });

  test.describe("Connected with multiple nodes", () => {
    test.describe('One "from" related to two "to"s', () => {
      test.beforeEach(async ({ diagram, palette, nodes }) => {
        await palette.dragNewNode({
          type: NodeType.INPUT_DATA,
          targetPosition: { x: 100, y: 100 },
        });
        await nodes.dragNewConnectedNode({
          from: DefaultNodeName.INPUT_DATA,
          type: NodeType.DECISION,
          targetPosition: { x: 100, y: 300 },
          thenRenameTo: "Decision One",
        });
        await nodes.dragNewConnectedNode({
          from: DefaultNodeName.INPUT_DATA,
          type: NodeType.DECISION,
          targetPosition: { x: 300, y: 300 },
          thenRenameTo: "Decision Two",
        });
        await diagram.resetFocus();
      });

      test('should delete "from" and not affect "to"s', async ({ nodes }) => {
        await nodes.delete({ name: DefaultNodeName.INPUT_DATA });

        await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).not.toBeAttached();
        await expect(nodes.get({ name: "Decision One" })).toBeAttached();
        await expect(nodes.get({ name: "Decision Two" })).toBeAttached();
      });

      test('should delete "to"s and not affect "from"', async ({ nodes }) => {
        await nodes.deleteMultiple({ names: ["Decision One", "Decision Two"] });

        await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).toBeAttached();
        await expect(nodes.get({ name: "Decision One" })).not.toBeAttached();
        await expect(nodes.get({ name: "Decision Two" })).not.toBeAttached();
      });

      test("should delete all", async ({ nodes }) => {
        await nodes.deleteMultiple({ names: [DefaultNodeName.INPUT_DATA, "Decision One", "Decision Two"] });

        await expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).not.toBeAttached();
        await expect(nodes.get({ name: "Decision One" })).not.toBeAttached();
        await expect(nodes.get({ name: "Decision Two" })).not.toBeAttached();
      });
    });

    test.describe('Two "from"s related to one "to"', () => {
      test.beforeEach(async ({ diagram, palette, nodes }) => {
        await palette.dragNewNode({
          type: NodeType.INPUT_DATA,
          targetPosition: { x: 100, y: 100 },
          thenRenameTo: "Input One",
        });
        await palette.dragNewNode({
          type: NodeType.INPUT_DATA,
          targetPosition: { x: 300, y: 100 },
          thenRenameTo: "Input Two",
        });
        await palette.dragNewNode({
          type: NodeType.DECISION,
          targetPosition: { x: 300, y: 300 },
        });
        await nodes.dragNewConnectedEdge({
          type: EdgeType.INFORMATION_REQUIREMENT,
          from: "Input One",
          to: DefaultNodeName.DECISION,
        });
        await nodes.dragNewConnectedEdge({
          type: EdgeType.INFORMATION_REQUIREMENT,
          from: "Input Two",
          to: DefaultNodeName.DECISION,
        });
        await diagram.resetFocus();
      });

      test('should delete "to" and not affect "from"s', async ({ nodes }) => {
        await nodes.delete({ name: DefaultNodeName.DECISION });

        await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
        await expect(nodes.get({ name: "Input One" })).toBeAttached();
        await expect(nodes.get({ name: "Input Two" })).toBeAttached();
      });

      test('should delete "to"s and not affect "from"', async ({ nodes }) => {
        await nodes.deleteMultiple({ names: ["Input One", "Input Two"] });

        await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
        await expect(nodes.get({ name: "Input One" })).not.toBeAttached();
        await expect(nodes.get({ name: "Input Two" })).not.toBeAttached();
      });

      test("should delete all", async ({ nodes }) => {
        await nodes.deleteMultiple({ names: [DefaultNodeName.DECISION, "Input One", "Input Two"] });

        await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
        await expect(nodes.get({ name: "Input One" })).not.toBeAttached();
        await expect(nodes.get({ name: "Input Two" })).not.toBeAttached();
      });
    });
  });
});
