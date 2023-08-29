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

import { By } from "selenium-webdriver";
import Element from "../Element";
import PageFragment from "../PageFragment";

export default class Explorer extends PageFragment {
  private static readonly PANEL_LOCATOR = By.xpath("//div[@data-field='explorerPanelBody']");
  private static readonly ITEM_LOCATOR = By.className("gwt-Anchor");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(Explorer.PANEL_LOCATOR).wait(5000).untilPresent();
  }

  private async getItems(): Promise<Element[]> {
    return await this.tools.by(Explorer.ITEM_LOCATOR).getElements();
  }

  private async getNodes(): Promise<Element[]> {
    const items: Element[] = await this.getItems();
    items.shift(); // remove asset name
    return items;
  }

  private async getNode(name: string): Promise<Element> {
    for (const node of await this.getNodes()) {
      if ((await node.getText()) === name) {
        return node;
      }
    }
    throw new Error("Node '" + name + "' was not found.");
  }

  public async getProcessName(): Promise<string> {
    const items: Element[] = await this.getItems();
    return await items[0].getText();
  }

  public async getNodeNames(): Promise<string[]> {
    const nodes: Element[] = await this.getNodes();
    return Promise.all(nodes.map((node) => node.getText()));
  }

  public async selectNode(name: string): Promise<void> {
    const node: Element = await this.getNode(name);
    return await node.click();
  }
}
