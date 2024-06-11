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

import { Page } from "@playwright/test";
import { Nodes } from "./nodes";
import { Diagram } from "./diagram";

export enum EdgeType {
  ASSOCIATION = "association",
  AUTHORITY_REQUIREMENT = "authority-requirement",
  INFORMATION_REQUIREMENT = "information-requirement",
  KNOWLEDGE_REQUIREMENT = "knowledge-requirement",
}

export class Edges {
  constructor(public page: Page, public nodes: Nodes, public diagram: Diagram) {}

  public async get(args: { from: string; to: string }) {
    const from = await this.nodes.getId({ name: args.from });
    const to = await this.nodes.getId({ name: args.to });

    return this.page.getByRole("button", { name: `Edge from ${from} to ${to}` });
  }

  public async getWaypoint(args: { from: string; to: string; waypointIndex: number }) {
    return (await this.get({ from: args.from, to: args.to })).locator(`[data-waypointindex="${args.waypointIndex}"]`);
  }

  public async getType(args: { from: string; to: string }) {
    return (await this.get({ from: args.from, to: args.to })).locator("path").nth(0).getAttribute("data-edgetype");
  }

  public async addWaypoint(args: { from: string; to: string; afterWaypointIndex?: number }) {
    const dAttribute = await (await this.get({ from: args.from, to: args.to }))
      .locator("path")
      .first()
      .getAttribute("d");

    const edgeSegments = dAttribute?.match(/M [0-9]*,[0-9]* L [0-9]*,[0-9]*/);

    if (edgeSegments) {
      const edgeSegment = args.afterWaypointIndex
        ? edgeSegments[Math.min(edgeSegments.length - 1, args.afterWaypointIndex)]
        : edgeSegments[0];

      const [fromX, fromY] = edgeSegment
        .split(/ L [0-9]*,[0-9]*/)[0]
        .slice(2)
        .split(",");
      const [toX, toY] = edgeSegment
        .split(/M [0-9]*,[0-9]* /)[1]
        .slice(2)
        .split(",");

      await this.diagram.dblclick({
        x: (parseInt(fromX) + parseInt(toX)) / 2,
        y: (parseInt(fromY) + parseInt(toY)) / 2,
      });
    }
  }

  public async moveWaypoint(args: {
    from: string;
    to: string;
    waypointIndex: number;
    targetPosition: { x: number; y: number };
  }) {
    await this.select({ from: args.from, to: args.to });

    await (
      await this.getWaypoint({ from: args.from, to: args.to, waypointIndex: args.waypointIndex })
    ).dragTo(this.diagram.get(), {
      targetPosition: args.targetPosition,
    });
  }

  public async deleteWaypoint(args: { from: string; to: string; waypointIndex: number }) {
    await this.select({ from: args.from, to: args.to });
    await (await this.getWaypoint({ from: args.from, to: args.to, waypointIndex: args.waypointIndex })).dblclick();
  }

  public async deleteWaypoints(args: { from: string; to: string; waypointsCount: number }) {
    for (let i = 0; i < args.waypointsCount; i++) {
      await this.deleteWaypoint({ from: args.from, to: args.to, waypointIndex: 1 });
    }
  }

  public async delete(args: { from: string; to: string; isBackspace?: boolean }) {
    await this.select({ from: args.from, to: args.to });
    if (args.isBackspace) {
      await (await this.get({ from: args.from, to: args.to })).press("Backspace");
    } else {
      await (await this.get({ from: args.from, to: args.to })).press("Delete");
    }
  }

  public async select(args: { from: string; to: string }) {
    // because of the waypoints on the edge, we can not click into the edge bounding box middle
    await (await this.get({ from: args.from, to: args.to })).locator("circle").nth(1).click();
  }
}
