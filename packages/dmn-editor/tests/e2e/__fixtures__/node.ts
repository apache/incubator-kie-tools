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
import { EdgeType } from "./edge";

export enum NodeType {
  INPUT_DATA,
  DECISION,
  KNOWLEDGE_SOURCE,
  BKM,
  DECISION_SERVICE,
  GROUP,
  TEXT_ANNOTATION,
}

export class Node {
  constructor(public page: Page, public diagram: Diagram) {
    this.page = page;
  }

  public async getId(args: { name: string }): Promise<string> {
    return (await this.page.getByTitle(args.name).getAttribute("data-nodeid")) ?? "";
  }

  public async rename(args: { current: string; new: string }) {
    await this.page.getByTitle(args.current).click();
    await this.page.getByTitle(args.current).press("Enter");
    await this.page.getByTitle(args.current).getByRole("textbox").nth(0).fill(args.new);
    await this.page.getByTitle(args.current).press("Enter");
  }

  public async dragNewConnectedNode(args: { type: NodeType; from: string; targetPosition: { x: number; y: number } }) {
    await this.diagram.hoverNode({ name: args.from });
    const node = this.page.getByTitle(args.from);

    switch (args.type) {
      case NodeType.INPUT_DATA:
        return node
          .getByTitle("Add Input Data node")
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
      case NodeType.DECISION:
        return node.getByTitle("Add Decision node").dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
      case NodeType.KNOWLEDGE_SOURCE:
        return node
          .getByTitle("Add Knowledge Source node")
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
      case NodeType.BKM:
        return node.getByTitle("Add BKM node").dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
      case NodeType.TEXT_ANNOTATION:
        return node
          .getByTitle("Add Text Annotation node")
          .dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
    }
  }

  public async dragNewConnectedEdge(args: { type: EdgeType; from: string; to: string }) {
    await this.diagram.hoverNode({ name: args.from });
    const from = this.page.getByTitle(args.from);
    const to = this.page.getByTitle(args.to);

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
