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
import { NodeType, DefaultNodeName, NodePosition } from "../__fixtures__/nodes";

test.describe("Add Lane", () => {
  test.beforeEach(async ({ editor }) => {
    await editor.open();
  });

  test("should add lane from palette", async ({ palette, nodes, jsonModel }) => {
    await palette.dragNewNode({ type: NodeType.LANE, targetPosition: { x: 300, y: 300 } });

    await expect(nodes.get({ name: DefaultNodeName.LANE })).toBeAttached();

    const process = await jsonModel.getProcess();
    const laneSet = Array.isArray(process.laneSet) ? process.laneSet[0] : process.laneSet;
    expect(laneSet?.lane?.length).toBeGreaterThan(0);
  });

  test.describe("Lane operations", () => {
    test("should delete lane", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.LANE, targetPosition: { x: 300, y: 300 } });
      await nodes.delete({ name: DefaultNodeName.LANE });

      await expect(nodes.get({ name: DefaultNodeName.LANE })).not.toBeAttached();

      const process = await jsonModel.getProcess();
      const laneSet = Array.isArray(process.laneSet) ? process.laneSet[0] : process.laneSet;
      expect(laneSet?.lane?.length || 0).toBe(0);
    });

    test("should move lane to new position", async ({ palette, nodes, diagram, page }) => {
      await palette.dragNewNode({ type: NodeType.LANE, targetPosition: { x: 300, y: 300 } });

      const lane = nodes.get({ name: DefaultNodeName.LANE });
      await expect(lane).toBeAttached();

      await lane.scrollIntoViewIfNeeded();

      const laneBox = await lane.boundingBox();
      expect(laneBox).not.toBeNull();

      await lane.dragTo(diagram.get(), {
        sourcePosition: { x: 20, y: laneBox!.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await lane.boundingBox();
      expect(boxAfter).not.toBeNull();
      expect(boxAfter!.x).not.toBe(laneBox?.x);
      expect(boxAfter!.y).not.toBe(laneBox?.y);
    });

    test("should rename lane", async ({ palette, nodes, jsonModel, page }) => {
      await palette.dragNewNode({ type: NodeType.LANE, targetPosition: { x: 300, y: 300 } });

      await nodes.select({ name: DefaultNodeName.LANE, position: NodePosition.LEFT });

      await nodes.rename({ current: DefaultNodeName.LANE, new: "Customer Service Lane" });

      await expect(nodes.get({ name: "Customer Service Lane" })).toBeAttached();

      const process = await jsonModel.getProcess();
      const lane = process.laneSet?.[0]?.lane?.[0];
      expect(lane?.["@_name"]).toBe("Customer Service Lane");
    });
  });
});
