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

test.describe("Add edge waypoint - Knowledge Requirement", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });
    await nodes.dragNewConnectedNode({
      from: DefaultNodeName.BKM,
      type: NodeType.DECISION,
      targetPosition: { x: 100, y: 300 },
    });
  });

  test("should add single waypoint to Knowledge Requirement edge and should not move when the ending node is moved", async ({
    diagram,
    nodes,
    edges,
  }) => {
    await edges.addWaypoint({ from: DefaultNodeName.BKM, to: DefaultNodeName.DECISION });
    await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });

    await expect(diagram.get()).toHaveScreenshot("add-knowledge-requirement-waypoint-and-not-move-it.png");
  });

  test("should add multiple waypoints to Knowledge Requirement edge and should not move when the ending nodes are moved", async ({
    diagram,
    nodes,
    edges,
  }) => {
    await edges.addWaypoint({ from: DefaultNodeName.BKM, to: DefaultNodeName.DECISION });
    await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 200, y: 500 } });

    await edges.addWaypoint({ from: DefaultNodeName.BKM, to: DefaultNodeName.DECISION });
    await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 500, y: 500 } });
    await nodes.move({ name: DefaultNodeName.BKM, targetPosition: { x: 500, y: 100 } });

    await expect(diagram.get()).toHaveScreenshot("add-multiple-knowledge-requirement-waypoints-and-not-move-them.png");
  });
});
