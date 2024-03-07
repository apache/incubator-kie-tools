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

import { Locator, Page } from "@playwright/test";
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

export enum NodePosition {
  BOTTOM,
  CENTER,
  LEFT,
  RIGHT,
  TOP,
  TOP_PADDING,
}

export class Nodes {
  constructor(public page: Page, public diagram: Diagram, public browserName: string) {}

  public get(args: { name: string }) {
    return this.page.locator(`div[data-nodelabel="${args.name}"]`);
  }

  public async getId(args: { name: string }): Promise<string> {
    return (await this.get({ name: args.name }).getAttribute("data-nodehref")) ?? "";
  }

  public async delete(args: { name: string }) {
    await this.select({ name: args.name, position: NodePosition.TOP_PADDING });
    await this.diagram.get().press("Delete");
  }

  public async deleteMultiple(args: { names: string[] }) {
    await this.selectMultiple({ names: args.names, position: NodePosition.TOP_PADDING });
    await this.diagram.get().press("Delete");
  }

  public async dragNewConnectedEdge(args: { type: EdgeType; from: string; to: string; position?: NodePosition }) {
    await this.select({ name: args.from, position: NodePosition.TOP });

    const from = this.get({ name: args.from });
    const to = this.get({ name: args.to });

    const targetPosition =
      args.position !== undefined
        ? await this.getPositionalNodeHandleCoordinates({ node: to, position: args.position })
        : undefined;

    switch (args.type) {
      case EdgeType.ASSOCIATION:
        return from.getByTitle("Add Association edge").dragTo(to, { targetPosition });
      case EdgeType.AUTHORITY_REQUIREMENT:
        return from.getByTitle("Add Authority Requirement edge").dragTo(to, { targetPosition });
      case EdgeType.INFORMATION_REQUIREMENT:
        return from.getByTitle("Add Information Requirement edge").dragTo(to, { targetPosition });
      case EdgeType.KNOWLEDGE_REQUIREMENT:
        return from.getByTitle("Add Knowledge Requirement edge").dragTo(to, { targetPosition });
    }
  }

  public async dragNewConnectedNode(args: {
    type: NodeType;
    from: string;
    targetPosition: { x: number; y: number };
    thenRenameTo?: string;
  }) {
    await this.hover({ name: args.from, position: NodePosition.TOP });
    const node = this.get({ name: args.from });
    const { addNodeTitle, nodeName } = this.getNewConnectedNodeProperties(args.type);

    await node.getByTitle(addNodeTitle).dragTo(this.diagram.get(), { targetPosition: args.targetPosition });
    await this.waitForNodeToBeFocused({ name: nodeName });
    if (args.thenRenameTo) {
      await this.rename({ current: nodeName, new: args.thenRenameTo });
    }
  }

  public async hover(args: { name: string; position?: NodePosition }) {
    const node = this.get({ name: args.name });

    const position =
      args.position !== undefined
        ? await this.getPositionalNodeHandleCoordinates({ node, position: args.position })
        : undefined;

    await node.hover({ position });
  }

  public async move(args: { name: string; targetPosition: { x: number; y: number } }) {
    await this.get({ name: args.name }).dragTo(this.diagram.get(), {
      targetPosition: args.targetPosition,
    });
  }

  public async rename(args: { current: string; new: string }) {
    await this.get({ name: args.current }).getByRole("textbox").nth(0).fill(args.new);
    await this.diagram.get().press("Enter");
  }

  public async select(args: { name: string; position?: NodePosition }) {
    const node = this.get({ name: args.name });

    const position =
      args.position !== undefined
        ? await this.getPositionalNodeHandleCoordinates({ node, position: args.position })
        : undefined;

    await node.click({ position });
  }

  public async selectMultiple(args: { names: string[]; position?: NodePosition }) {
    if (this.browserName === "webkit") {
      await this.page.keyboard.down("Meta");
    } else {
      await this.page.keyboard.down("Control");
    }

    for (const name of args.names) {
      const node = this.get({ name });

      const position =
        args.position !== undefined
          ? await this.getPositionalNodeHandleCoordinates({ node, position: args.position })
          : undefined;

      await node.click({ position });
    }

    if (this.browserName === "webkit") {
      await this.page.keyboard.up("Meta");
    } else {
      await this.page.keyboard.up("Control");
    }
  }

  public async selectLabel(args: { name: string }) {
    return this.get({ name: args.name }).locator("span", { hasText: args.name }).dblclick();
  }

  // After creating a node takes a while to get the focus.
  // This methods waits until it gets the focus.
  public async waitForNodeToBeFocused(args: { name: string }) {
    return this.page.waitForFunction(
      (nodeName) => (document.activeElement as HTMLInputElement)?.value === nodeName,
      args.name
    );
  }

  private getNewConnectedNodeProperties(type: NodeType) {
    switch (type) {
      case NodeType.DECISION:
        return { addNodeTitle: "Add Decision node", nodeName: DefaultNodeName.DECISION };
      case NodeType.KNOWLEDGE_SOURCE:
        return { addNodeTitle: "Add Knowledge Source node", nodeName: DefaultNodeName.KNOWLEDGE_SOURCE };
      case NodeType.BKM:
        return { addNodeTitle: "Add BKM node", nodeName: DefaultNodeName.BKM };
      case NodeType.TEXT_ANNOTATION:
        return { addNodeTitle: "Add Text Annotation node", nodeName: DefaultNodeName.TEXT_ANNOTATION };
      default:
        throw new Error("Invalid type");
    }
  }

  private async getPositionalNodeHandleCoordinates(args: { node: Locator; position: NodePosition }) {
    const toBoundingBox = await args.node.boundingBox();

    if (!toBoundingBox) {
      return undefined;
    }

    switch (args.position) {
      case NodePosition.TOP:
        return { x: toBoundingBox.width / 2, y: 0 };
      case NodePosition.BOTTOM:
        return { x: toBoundingBox.width / 2, y: toBoundingBox.height };
      case NodePosition.LEFT:
        return { x: 0, y: toBoundingBox.height / 2 };
      case NodePosition.RIGHT:
        return { x: toBoundingBox.width, y: toBoundingBox.height / 2 };
      case NodePosition.CENTER:
        return { x: toBoundingBox.width / 2, y: toBoundingBox.height / 2 };
      case NodePosition.TOP_PADDING:
        return { x: toBoundingBox.width / 2, y: 10 };
    }
  }
}
