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

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add edge waypoint - Information Requirement", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
    await nodes.dragNewConnectedNode({
      from: DefaultNodeName.INPUT_DATA,
      type: NodeType.DECISION,
      targetPosition: { x: 100, y: 300 },
    });
  });

  test("should add single waypoint to Information Requirement edge and should not move when the ending node is moved", async ({
    diagram,
    nodes,
    edges,
  }) => {
    await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });

    await nodes.get({ name: DefaultNodeName.DECISION }).dragTo(diagram.get(), { targetPosition: { x: 300, y: 300 } });

    await expect(diagram.get()).toHaveScreenshot("add-information-requirement-waypoint-and-not-move-it.png");
  });

  test("should add multiple waypoints to Information Requirement edge and should not move when the ending nodes are moved", async ({
    diagram,
    nodes,
    edges,
  }) => {
    await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });
    const boundingBox = await nodes.get({ name: DefaultNodeName.DECISION }).boundingBox();
    await nodes
      .get({ name: DefaultNodeName.DECISION })
      .dragTo(diagram.get(), { targetPosition: { x: 100 + (boundingBox?.width ?? 0) / 2, y: 500 } });

    await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });
    await nodes.get({ name: DefaultNodeName.DECISION }).dragTo(diagram.get(), { targetPosition: { x: 500, y: 500 } });
    await nodes.get({ name: DefaultNodeName.INPUT_DATA }).dragTo(diagram.get(), { targetPosition: { x: 500, y: 100 } });

    await expect(diagram.get()).toHaveScreenshot("add-information-requirement-waypoint-and-not-move-it.png");
  });
});
