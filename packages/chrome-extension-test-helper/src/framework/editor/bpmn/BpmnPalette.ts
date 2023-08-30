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

export default class BpmnPalette extends PageFragment {
  private static readonly START_EVENTS_LOCATOR = By.xpath("//button[@title='Start Events']");
  private static readonly START_ITEM_LOCATOR = By.xpath(
    "//a[@class='kie-palette-item-anchor-spacer']" + "[span[text()='Start']]"
  );
  private static readonly CLOSE_LOCATOR = By.className("kie-palette-flyout__btn-link--close");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(BpmnPalette.START_EVENTS_LOCATOR).wait(1000).untilPresent();
  }

  public async dragAndDropStartEventToCanvas(): Promise<void> {
    // open start events
    const startEvents: Element = await this.tools.by(BpmnPalette.START_EVENTS_LOCATOR).getElement();
    await startEvents.click();

    // select start item
    const startItem = await this.tools.by(BpmnPalette.START_ITEM_LOCATOR).getElement();

    // move to canvas
    await startItem.dragAndDrop(200, 0);

    // close start events palette
    const closeButton: Element = await this.tools.by(BpmnPalette.CLOSE_LOCATOR).getElement();
    return await closeButton.click();
  }
}
