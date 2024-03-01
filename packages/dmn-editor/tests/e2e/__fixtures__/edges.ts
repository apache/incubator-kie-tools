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

export enum EdgeType {
  ASSOCIATION = "association",
  AUTHORITY_REQUIREMENT = "authority-requirement",
  INFORMATION_REQUIREMENT = "information-requirement",
  KNOWLEDGE_REQUIREMENT = "knowledge-requirement",
}

export class Edges {
  constructor(public page: Page, public nodes: Nodes) {}

  public async get(args: { from: string; to: string }) {
    const from = await this.nodes.getId({ name: args.from });
    const to = await this.nodes.getId({ name: args.to });

    return this.page.getByRole("button", { name: `Edge from ${from} to ${to}` });
  }

  public async getType(args: { from: string; to: string }) {
    return (await this.get({ from: args.from, to: args.to })).locator("path").nth(0).getAttribute("data-edgetype");
  }

  public async addWaypoint(args: { from: string; to: string }) {
    await (await this.get({ from: args.from, to: args.to })).dblclick();
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
    await (await this.get({ from: args.from, to: args.to })).click();
  }
}
