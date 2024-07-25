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
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Delete edge waypoint - Association", () => {
  test.beforeEach(async ({ palette, nodes, browserName }) => {
    test.skip(browserName === "webkit", "https://github.com/apache/incubator-kie-issues/issues/991");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/991",
    });

    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
    await nodes.dragNewConnectedNode({
      from: DefaultNodeName.INPUT_DATA,
      type: NodeType.TEXT_ANNOTATION,
      targetPosition: { x: 100, y: 300 },
    });
  });

  test("should delete one Association edge waypoint", async ({ edges }) => {
    await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION });

    await edges.deleteWaypoint({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.TEXT_ANNOTATION,
      waypointIndex: 1,
    });

    await expect(
      await edges.getWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.TEXT_ANNOTATION,
        waypointIndex: 1,
      })
    ).not.toBeAttached();
  });

  test("should move and delete one Association edge waypoint", async ({ diagram, edges }) => {
    await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION });
    await edges.moveWaypoint({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.TEXT_ANNOTATION,
      waypointIndex: 1,
      targetPosition: { x: 500, y: 300 },
    });

    await edges.deleteWaypoint({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.TEXT_ANNOTATION,
      waypointIndex: 1,
    });
    await expect(diagram.get()).toHaveScreenshot("delete-association-waypoint-straight-edge.png");
  });

  test("should delete two Association edge waypoints", async ({ edges }) => {
    await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION });
    await edges.addWaypoint({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.TEXT_ANNOTATION,
      afterWaypointIndex: 1,
    });

    await edges.deleteWaypoints({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.TEXT_ANNOTATION,
      waypointsCount: 2,
    });

    await expect(
      await edges.getWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.TEXT_ANNOTATION,
        waypointIndex: 1,
      })
    ).not.toBeAttached();
    await expect(
      await edges.getWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.TEXT_ANNOTATION,
        waypointIndex: 2,
      })
    ).not.toBeAttached();
  });

  test("should move two and delete one Association edge waypoints", async ({ diagram, edges }) => {
    await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.TEXT_ANNOTATION });
    await edges.addWaypoint({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.TEXT_ANNOTATION,
      afterWaypointIndex: 1,
    });
    await edges.moveWaypoint({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.TEXT_ANNOTATION,
      waypointIndex: 1,
      targetPosition: { x: 500, y: 100 },
    });
    await edges.moveWaypoint({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.TEXT_ANNOTATION,
      waypointIndex: 2,
      targetPosition: { x: 500, y: 500 },
    });

    await edges.deleteWaypoint({
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.TEXT_ANNOTATION,
      waypointIndex: 1,
    });

    await expect(diagram.get()).toHaveScreenshot("delete-association-waypoint-edge-with-corner.png");
  });
});
