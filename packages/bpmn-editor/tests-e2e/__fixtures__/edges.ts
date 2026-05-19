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
    const fromId = args.from.startsWith("_") ? args.from : await this.nodes.getId({ name: args.from });
    const toId = args.to.startsWith("_") ? args.to : await this.nodes.getId({ name: args.to });

    const allEdges = this.page.getByTestId(/kie-tools--bpmn-editor--edge-[^p]/);
    const edgeCount = await allEdges.count();

    if (edgeCount === 1) {
      return allEdges.first();
    }

    const fromNode = this.nodes.getById({ id: fromId });
    const toNode = this.nodes.getById({ id: toId });

    const fromBox = await fromNode.boundingBox();
    const toBox = await toNode.boundingBox();
    expect(fromBox).not.toBeNull();
    expect(toBox).not.toBeNull();

    const toCenter = { x: toBox!.x + toBox!.width / 2, y: toBox!.y + toBox!.height / 2 };

    let bestMatch: { edge: Locator; distance: number } | null = null;

    for (let i = 0; i < edgeCount; i++) {
      const edge = allEdges.nth(i);
      const edgeId = await edge.getAttribute("data-testid");

      if (!edgeId) continue;

      const actualEdgeId = edgeId.replace("kie-tools--bpmn-editor--edge-", "");

      const pathElement = this.page.getByTestId(`kie-tools--bpmn-editor--edge-path-${actualEdgeId}`);
      const pathBox = await pathElement.boundingBox();

      if (!pathBox) continue;

      const edgeEndX = pathBox.x + pathBox.width;
      const edgeEndY = pathBox.y + pathBox.height / 2;

      const distanceToTarget = Math.sqrt(Math.pow(edgeEndX - toCenter.x, 2) + Math.pow(edgeEndY - toCenter.y, 2));

      if (!bestMatch || distanceToTarget < bestMatch.distance) {
        bestMatch = { edge, distance: distanceToTarget };
      }
    }

    expect(bestMatch).toBeDefined();
    return bestMatch!.edge;
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
}
