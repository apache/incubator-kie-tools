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
    const from = await this.nodes.getId({ name: args.from });
    const to = await this.nodes.getId({ name: args.to });

    return this.page.getByRole("button", { name: `Edge from ${from} to ${to}` });
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
