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
import { Diagram } from "./diagram";
import { EdgeType } from "./edges";

export enum NodeType {
  INPUT_DATA,
  DECISION,
  KNOWLEDGE_SOURCE,
  BKM,
  DECISION_SERVICE,
  GROUP,
  TEXT_ANNOTATION,
}

export enum DefaultNodeName {
  INPUT_DATA = "New Input Data",
  DECISION = "New Decision",
  KNOWLEDGE_SOURCE = "New Knowledge Source",
  BKM = "New BKM",
  DECISION_SERVICE = "New Decision Service",
  GROUP = "New group",
  TEXT_ANNOTATION = "New text annotation",
}

export class Nodes {
  constructor(public page: Page, public diagram: Diagram) {}

  public async getId(args: { name: string }): Promise<string> {
    return (await this.page.getByTitle(args.name, { exact: true }).getAttribute("data-nodeid")) ?? "";
  }

  public async rename(args: { current: string; new: string }) {
    await this.diagram.get().press("Escape");
    await this.page.getByTitle(args.current, { exact: true }).click({ position: { x: 0, y: 0 } });
    await this.page.getByTitle(args.current, { exact: true }).press("Enter");
    await this.page.getByTitle(args.current, { exact: true }).getByRole("textbox").nth(0).fill(args.new);
    await this.diagram.get().press("Enter");
  }

  // FIXME: Currently it requires multiple "Escapes" to work on Webkit and Chrome.
  public async renameInputNode(args: { current: string; new: string }) {
    await this.diagram.get().press("Escape");
    await this.diagram.get().press("Escape");
    await this.diagram.get().press("Escape");
    await this.diagram.get().press("Escape");
    await this.page.getByTitle(args.current, { exact: true }).click({ position: { x: 0, y: 0 } });
    await this.page.getByTitle(args.current, { exact: true }).press("Enter");
    await this.page.getByTitle(args.current, { exact: true }).getByRole("textbox").nth(0).fill(args.new);
    await this.diagram.get().press("Enter");
  }

  public async dragNewConnectedNode(args: {
    type: NodeType;
    from: string;
    targetPosition: { x: number; y: number };
    thenRenameTo?: string;
  }) {
    await this.diagram.hoverNode({ name: args.from });
    const node = this.page.getByTitle(args.from, { exact: true });

    switch (args.type) {
      case NodeType.INPUT_DATA:
        await node
          .getByTitle("Add Input Data node")
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.renameInputNode({ current: DefaultNodeName.INPUT_DATA, new: args.thenRenameTo });
        }
        break;
      case NodeType.DECISION:
        await node.getByTitle("Add Decision node").dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.rename({ current: DefaultNodeName.DECISION, new: args.thenRenameTo });
        }
        break;
      case NodeType.KNOWLEDGE_SOURCE:
        await node
          .getByTitle("Add Knowledge Source node")
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.rename({ current: DefaultNodeName.KNOWLEDGE_SOURCE, new: args.thenRenameTo });
        }
        break;
      case NodeType.BKM:
        await node.getByTitle("Add BKM node").dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.rename({ current: DefaultNodeName.BKM, new: args.thenRenameTo });
        }
        break;
      case NodeType.TEXT_ANNOTATION:
        await node
          .getByTitle("Add Text Annotation node")
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
        if (args.thenRenameTo) {
          await this.rename({ current: DefaultNodeName.TEXT_ANNOTATION, new: args.thenRenameTo });
        }
    }
  }

  public async dragNewConnectedEdge(args: { type: EdgeType; from: string; to: string }) {
    await this.diagram.selectNode({ name: args.from });
    const from = this.page.getByTitle(args.from, { exact: true });
    const to = this.page.getByTitle(args.to, { exact: true });

    switch (args.type) {
      case EdgeType.ASSOCIATION:
        return from.getByTitle("Add Association edge").dragTo(to);
      case EdgeType.AUTHORITY_REQUIREMENT:
        return from.getByTitle("Add Authority Requirement edge").dragTo(to);
      case EdgeType.INFORMATION_REQUIREMENT:
        return from.getByTitle("Add Information Requirement edge").dragTo(to);
      case EdgeType.KNOWLEDGE_REQUIREMENT:
        return from.getByTitle("Add Knowledge Requirement edge").dragTo(to);
    }
  }
}
