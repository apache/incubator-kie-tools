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
import Element from "../../Element";
import PageFragment from "../../PageFragment";

export default class DecisionNavigator extends PageFragment {
  private static readonly DECISION_GRAPH_LOCATOR: By = By.xpath(
    "//div[@data-i18n-prefix='DecisionNavigatorTreeView.']"
  );
  private static readonly ITEM_LOCATOR: By = By.xpath("//li[@data-field='item']");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(DecisionNavigator.DECISION_GRAPH_LOCATOR).wait(5000).untilPresent();
  }

  private async getItems(): Promise<Element[]> {
    return await this.tools.by(DecisionNavigator.ITEM_LOCATOR).getElements();
  }

  private async getNodes(): Promise<Element[]> {
    const items: Element[] = await this.getItems();
    items.shift(); // remove DMN name
    return items;
  }

  public async getDmnName(): Promise<string> {
    const items: Element[] = await this.getItems();
    return await items[0].getAttribute("title");
  }

  public async getNodeNames(): Promise<string[]> {
    const nodes: Element[] = await this.getNodes();
    return Promise.all(nodes.map((node) => node.getAttribute("title")));
  }

  public async selectNode(name: string): Promise<void> {
    const node: Element = await this.tools.by(By.xpath(`//li[@data-field='item'][@title='${name}']/div`)).getElement();
    return await node.click();
  }

  public async selectNodeExpression(name: string): Promise<void> {
    const node: Element = await this.tools
      .by(By.xpath(`//li[@data-field='item'][@title='${name}']/div/following-sibling::ul/li/div`))
      .getElement();
    return await node.click();
  }
}
