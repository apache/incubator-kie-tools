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

import { expect, Locator, Page } from "@playwright/test";
import { BPMNDI__BPMNEdge } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Diagram } from "./diagram";
import { Nodes } from "./nodes";

export enum EdgeType {
  SEQUENCE_FLOW = "edge_sequenceFlow",
  ASSOCIATION = "edge_association",
}

export class Edges {
  constructor(
    public page: Page,
    public nodes: Nodes,
    public diagram: Diagram
  ) {}

  public async get(args: { from: string; to: string }): Promise<Locator> {
    const from = await this.nodes.getId({ name: args.from });
    const to = await this.nodes.getId({ name: args.to });

    return this.getByIds({ from, to });
  }

  public getByIds(args: { from: string; to: string }): Locator {
    return this.page.getByRole("button", { name: `Edge from ${args.from} to ${args.to}` });
  }

  public async getType(args: { from: string; to: string }): Promise<EdgeType> {
    const edge = await this.get(args);
    const type = await edge.getAttribute("data-edgetype");
    return type as EdgeType;
  }

  public async delete(args: { from: string; to: string }) {
    const edge = await this.get(args);
    await edge.click();
    await this.diagram.get().press("Delete");
  }

  public getLabel(args: { name: string }): Locator {
    return this.diagram.get().getByText(args.name, { exact: true });
  }

  public async getLabelBounds(args: { name: string }) {
    const box = await this.getLabel(args).boundingBox();
    expect(box).not.toBeNull();
    return box!;
  }

  public async getLabelCenterPosition(args: { name: string }): Promise<{ x: number; y: number }> {
    const box = await this.getLabelBounds(args);
    return {
      x: box.x + box.width / 2,
      y: box.y + box.height / 2,
    };
  }

  public async moveLabel(args: { name: string; offset: { x: number; y: number } }) {
    const { x, y } = await this.getLabelCenterPosition({ name: args.name });
    await this.page.mouse.move(x, y);
    await this.page.mouse.down();
    await this.page.mouse.move(x + args.offset.x, y + args.offset.y, { steps: 10 });
    await this.page.mouse.up();
  }

  public getLabelDistanceToEdge(bpmnEdge: BPMNDI__BPMNEdge) {
    const bounds = bpmnEdge["bpmndi:BPMNLabel"]!["dc:Bounds"]!;
    const x = bounds["@_x"] + bounds["@_width"] / 2;
    const y = bounds["@_y"] + bounds["@_height"] / 2;
    const waypoints = bpmnEdge["di:waypoint"];

    let distance = Infinity;
    for (let i = 0; i < waypoints.length - 1; i++) {
      const a = waypoints[i];
      const dx = waypoints[i + 1]["@_x"] - a["@_x"];
      const dy = waypoints[i + 1]["@_y"] - a["@_y"];
      const squaredLength = dx * dx + dy * dy;
      const t =
        squaredLength === 0 ? 0 : Math.min(1, Math.max(0, ((x - a["@_x"]) * dx + (y - a["@_y"]) * dy) / squaredLength));
      distance = Math.min(distance, Math.hypot(x - a["@_x"] - t * dx, y - a["@_y"] - t * dy));
    }
    return distance;
  }
}
