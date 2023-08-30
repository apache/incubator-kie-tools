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
import Explorer from "./Explorer";
import Locator from "../Locator";
import PageFragment from "../PageFragment";
import Properties from "./Properties";

const MAX_ATTEMPTS_TO_OPEN_SIDEBAR = 10;

export default class SideBar extends PageFragment {
  private static readonly PROP_BUTTON_LOCATOR = By.xpath("//div[./button[@data-title='Properties']]");
  private static readonly EXPLORER_BUTTON_LOCATOR = By.xpath(
    "//div[./button[@data-title='Explore Diagram' or @data-title='Explore diagram']]"
  );
  private static readonly EXPANDED_BAR_LOCATOR = By.className("expanded-docks-bar-E");
  private static readonly TITLE_LOCATOR = By.xpath("./div/h3");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(SideBar.EXPLORER_BUTTON_LOCATOR).wait(1000).untilPresent();
  }

  protected async openSideBar(byIcon: Element, sideBarTitle: string): Promise<Element> {
    const expandedBar: Locator = this.tools.by(SideBar.EXPANDED_BAR_LOCATOR);

    // Some events might take a while to be registered in the docks icons, so we have to reattempt if it fails.
    for (let i = 0; !(await this.isSideBarOpen(sideBarTitle)) && i < MAX_ATTEMPTS_TO_OPEN_SIDEBAR; i++) {
      await byIcon.click();
      await this.tools.sleep(1000);
    }

    // confirms that it's open.
    await expandedBar.wait(2000).untilVisible();

    // move to make the tooltip diappear
    await byIcon.offsetMove(-200, 0);

    return await expandedBar.getElement();
  }

  private async isSideBarOpen(title: string): Promise<boolean> {
    const sideBarLocator: Locator = this.tools.by(SideBar.EXPANDED_BAR_LOCATOR);
    const isSideBarOpen: boolean = await sideBarLocator.wait().isVisible();
    if (isSideBarOpen) {
      const sideBar: Element = await sideBarLocator.getElement();
      const sideBarTitle: Element = await sideBar.findElement(SideBar.TITLE_LOCATOR);
      const actualTitle: string = await sideBarTitle.getText();
      if (actualTitle === title) {
        return true;
      }
    }
    return false;
  }

  public async openExplorer(): Promise<Explorer> {
    const explorerButtonLocator: Locator = this.tools.by(SideBar.EXPLORER_BUTTON_LOCATOR);
    await explorerButtonLocator.wait(2000).untilPresent();
    const diagramButton: Element = await explorerButtonLocator.getElement();
    const sideBar = await this.openSideBar(diagramButton, "Explore Diagram");
    return await this.tools.createPageFragment(Explorer, sideBar);
  }

  public async openProperties(): Promise<Properties> {
    const propButton = await this.tools.by(SideBar.PROP_BUTTON_LOCATOR).getElement();
    const sideBar = await this.openSideBar(propButton, "Properties");
    return await this.tools.createPageFragment(Properties, sideBar);
  }
}
