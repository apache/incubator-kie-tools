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

export enum NodeType {
  EVENT_STATE,
  OPERATION_STATE,
  SWITCH_STATE,
  SLEEP_STATE,
  PARALLEL_STATE,
  INJECT_STATE,
  FOR_EACH_STATE,
  CALLBACK_STATE,
}

export enum DefaultNodeName {
  EVENT_STATE = "New Event State",
  OPERATION_STATE = "New Operarion State",
  SWITCH_STATE = "New Switch State",
  SLEEP_STATE = "New Sleep State",
  PARALLEL_STATE = "New Parallel State",
  INJECT_STATE = "New Inject State",
  FOR_EACH_STATE = "New For Each State",
  CALLBACK_STATE = "New Callback State",
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
    public diagram: Diagram,
    public browserName: string
  ) {}

  public get(args: { name: string }) {
    return this.page.locator(`div[data-nodelabel="${args.name}"]`);
  }

  public async getId(args: { name: string }): Promise<string> {
    return (await this.get({ name: args.name }).getAttribute("data-nodehref")) ?? "";
  }

  private getParentElement(args: { nodeName: string }) {
    return this.get({ name: args.nodeName }).locator("..");
  }

  public async getRectAttribute(args: { nodeName: string; attribute: "fill" | "stroke" }) {
    // It's necessary to pick the parent element to have access to the SVG.
    return await this.getParentElement({ nodeName: args.nodeName }).locator("rect").nth(0).getAttribute(args.attribute);
  }

  public async getPathAttribute(args: { nodeName: string; attribute: "fill" | "stroke" }) {
    // It's necessary to pick the parent element to have access to the SVG.
    return await this.getParentElement({ nodeName: args.nodeName }).locator("path").nth(0).getAttribute(args.attribute);
  }

  public async getPolygonAttribute(args: { nodeName: string; attribute: "fill" | "stroke" }) {
    // It's necessary to pick the parent element to have access to the SVG.
    return await this.getParentElement({ nodeName: args.nodeName })
      .locator("polygon")
      .nth(0)
      .getAttribute(args.attribute);
  }

  public async move(args: { name: string; targetPosition: { x: number; y: number } }) {
    await this.get({ name: args.name }).dragTo(this.diagram.get(), {
      targetPosition: args.targetPosition,
    });
  }

  public async hover(args: { name: string; position?: NodePosition }) {
    const node = this.get({ name: args.name });

    const position =
      args.position !== undefined
        ? await this.getPositionalNodeHandleCoordinates({ node, position: args.position })
        : undefined;

    await node.hover({ position });
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
    return this.page.waitForFunction(
      (nodeName) => (document.activeElement as HTMLInputElement)?.value === nodeName,
      args.name
    );
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
