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
import DecisionNavigator from "./DecisionNavigator";
import Element from "../../Element";
import SideBar from "../SideBar";
import Locator from "../../Locator";

export default class DmnSideBar extends SideBar {
  private static readonly NAVIGATOR_BUTTON_LOCATOR: By = By.xpath("//div[./button[@data-title='Decision Navigator']]");

  public async openDecisionNavigator(): Promise<DecisionNavigator> {
    const navigatorButtonLocator: Locator = this.tools.by(DmnSideBar.NAVIGATOR_BUTTON_LOCATOR);
    await navigatorButtonLocator.wait(2000).untilPresent();
    const navigatorButton: Element = await navigatorButtonLocator.getElement();
    const sideBar: Element = await this.openSideBar(navigatorButton, "Decision Navigator");
    return await this.tools.createPageFragment(DecisionNavigator, sideBar);
  }
}
