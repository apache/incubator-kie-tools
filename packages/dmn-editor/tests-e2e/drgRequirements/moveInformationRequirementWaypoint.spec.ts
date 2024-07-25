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

test.describe("Move edge waypoint - Information Requirement", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
    await nodes.dragNewConnectedNode({
      from: DefaultNodeName.INPUT_DATA,
      type: NodeType.DECISION,
      targetPosition: { x: 100, y: 300 },
    });
  });

  test.describe("Add Single Waypoint", () => {
    test("Information Requirement edge waypoint should not move when the ending node is moved", async ({
      diagram,
      nodes,
      edges,
    }) => {
      await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });
      await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });

      await expect(diagram.get()).toHaveScreenshot("move-information-requirement-ending-waypoint.png");
    });

    test("Information Requirement edge ending nodes should not move when the waypoint is moved", async ({
      diagram,
      edges,
      browserName,
    }) => {
      test.skip(browserName === "webkit", "https://github.com/apache/incubator-kie-issues/issues/991");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/991",
      });

      await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });
      await edges.moveWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.DECISION,
        waypointIndex: 1,
        targetPosition: { x: 300, y: 300 },
      });

      await expect(diagram.get()).toHaveScreenshot("move-information-requirement-middle-waypoint.png");
    });
  });

  test.describe("Add Multiple Waypoints", () => {
    test("Information Requirement edge waypoints should not move when the ending nodes are moved", async ({
      diagram,
      nodes,
      edges,
    }) => {
      await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });
      await edges.addWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.DECISION,
        afterWaypointIndex: 1,
      });
      await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 500, y: 500 } });
      await nodes.move({ name: DefaultNodeName.INPUT_DATA, targetPosition: { x: 500, y: 100 } });

      await expect(diagram.get()).toHaveScreenshot("move-information-requirement-starting-and-ending-waypoint.png");
    });

    test("Information Requirement edge ending nodes should not move when the waypoints are moved", async ({
      diagram,
      edges,
      browserName,
    }) => {
      test.skip(browserName === "webkit", "https://github.com/apache/incubator-kie-issues/issues/991");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/991",
      });

      await edges.addWaypoint({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });
      await edges.addWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.DECISION,
        afterWaypointIndex: 1,
      });

      await edges.moveWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.DECISION,
        waypointIndex: 1,
        targetPosition: { x: 500, y: 100 },
      });
      await edges.moveWaypoint({
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.DECISION,
        waypointIndex: 2,
        targetPosition: { x: 500, y: 500 },
      });

      await expect(diagram.get()).toHaveScreenshot("move-information-requirement-middle-waypoints.png");
    });
  });
});
