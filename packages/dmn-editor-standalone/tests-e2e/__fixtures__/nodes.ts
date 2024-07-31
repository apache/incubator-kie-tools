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
import { Editor } from "./editor";

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
  constructor(
    public page: Page,
    public editor: Editor,
    public diagram: Diagram
  ) {}

  public get(args: { name: string }) {
    return this.editor.get().locator(`div[data-nodelabel="${args.name}"]`);
  }

  public async getId(args: { name: string }): Promise<string> {
    return (await this.get({ name: args.name }).getAttribute("data-nodehref")) ?? "";
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

  public async select(args: { name: string; position?: NodePosition }) {
    const node = this.get({ name: args.name });

    const position =
      args.position !== undefined
        ? await this.getPositionalNodeHandleCoordinates({ node, position: args.position })
        : undefined;

    await node.click({ position });
  }

  public async delete(args: { name: string }) {
    await this.select({ name: args.name, position: NodePosition.TOP_PADDING });
    await this.diagram.get().press("Delete");
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

  public async edit(args: { name: string }) {
    await this.get({ name: args.name }).hover();
    await this.get({ name: args.name }).getByText("Edit").click();
  }

  public async selectLabel(args: { name: string }) {
    return this.get({ name: args.name }).locator("span", { hasText: args.name }).dblclick();
  }

  // After creating a node takes a while to get the focus.
  // This methods waits until it gets the focus.
  public async waitForNodeToBeFocused(args: { name: string }) {
    await expect(async () => {
      await expect(
        await this.editor
          .get()
          .locator(":root")
          .evaluate((_, nodeName) => (document.activeElement as HTMLInputElement)?.value === nodeName, args.name)
      ).toBeTruthy();
    }).toPass();
  }
}
