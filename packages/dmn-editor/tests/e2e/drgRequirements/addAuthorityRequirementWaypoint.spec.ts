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

test.describe("Add edge waypoint - Authority Requirement", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
    await nodes.dragNewConnectedNode({
      from: DefaultNodeName.INPUT_DATA,
      type: NodeType.KNOWLEDGE_SOURCE,
      targetPosition: { x: 100, y: 300 },
    });
  });

  test("should attach single Authority Requirement waypoint to the edge", async ({ edges }) => {
    await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.KNOWLEDGE_SOURCE });
    await expect(
      await edges.getWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
        waypointIndex: 1,
      })
    ).toBeAttached();
    await expect(
      await edges.getWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
        waypointIndex: 2,
      })
    ).not.toBeAttached();
  });

  test("should attach multiple Authority Requirement waypoints to the edge", async ({ edges }) => {
    await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.KNOWLEDGE_SOURCE });
    await edges.addWaypoint({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.KNOWLEDGE_SOURCE,
      afterWaypointIndex: 1,
    });

    await expect(
      await edges.getWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
        waypointIndex: 1,
      })
    ).toBeAttached();
    await expect(
      await edges.getWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.KNOWLEDGE_SOURCE,
        waypointIndex: 2,
      })
    ).toBeAttached();
  });
});
