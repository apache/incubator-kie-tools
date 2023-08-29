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

import { By, WebElement } from "selenium-webdriver";
import ErrorProcessor from "../utils/tools/ErrorProcessor";

export default class Element {
  constructor(private readonly webElement: WebElement) {}

  public async dragAndDrop(x: number, y: number): Promise<void> {
    return await ErrorProcessor.run(async () => {
      await this.click();

      // no other way of drag and drop works
      const actions = this.webElement.getDriver().actions();
      await actions.move({ origin: this.webElement, x, y }).perform();
      return await actions.click().perform();
    }, "Error while drag and drop element to: x=" + x + " y=" + y);
  }

  public async sendKeys(keys: string): Promise<void> {
    return await ErrorProcessor.run(
      async () => await this.webElement.sendKeys(keys),
      "Error while sending keys " + keys
    );
  }

  public async getText(): Promise<string> {
    return await ErrorProcessor.run(
      async () => await this.webElement.getText(),
      "Error while getting text from element."
    );
  }

  // do not use, it might break tests, regular click sometimes does not work then
  public async clickJs(): Promise<void> {
    return await ErrorProcessor.run(
      // there is an issue that after using js click, sometimes regular click does not work
      async () => await this.webElement.getDriver().executeScript("arguments[0].click();", this.webElement),
      "Error while clicking by JavaScript on element."
    );
  }

  public async click(): Promise<void> {
    return await ErrorProcessor.run(async () => await this.webElement.click(), "Error while clicking on element.");
  }

  public async offsetClick(x: number, y: number): Promise<void> {
    return await ErrorProcessor.run(async () => {
      const actions = this.webElement.getDriver().actions();
      await actions.move({ origin: this.webElement, x, y }).perform();
      return await actions.click().perform();
    }, "Error while clicking on element by offset: x=" + x + " ,y=" + y);
  }

  public async offsetMove(x: number, y: number): Promise<void> {
    return await ErrorProcessor.run(async () => {
      const actions = this.webElement.getDriver().actions();
      return await actions.move({ origin: this.webElement, x, y }).perform();
    }, "Error while moving from element by offset: x=" + x + " ,y=" + y);
  }

  public async scroll(): Promise<void> {
    return await ErrorProcessor.run(
      async () =>
        await this.webElement.getDriver().executeScript("arguments[0].scrollIntoView(true);", this.webElement),
      "Error while scrolling to element."
    );
  }

  public async getAttribute(attributeName: string): Promise<string> {
    return await ErrorProcessor.run(
      async () => await this.webElement.getAttribute(attributeName),
      "Error while getting attribute: " + attributeName
    );
  }

  public async findElement(by: By): Promise<Element> {
    return await ErrorProcessor.run(
      async () => new Element(await this.webElement.findElement(by)),
      "Error while finding element: " + by
    );
  }

  public async findElements(by: By): Promise<Element[]> {
    return await ErrorProcessor.run(async () => {
      const webElements = await this.webElement.findElements(by);
      return webElements.map((webElement) => new Element(webElement));
    }, "Error while finding elements: " + by);
  }

  public async enterFrame(): Promise<void> {
    return await ErrorProcessor.run(
      async () => await this.webElement.getDriver().switchTo().frame(this.webElement),
      "Error while entering element frame."
    );
  }

  public async markWithRedColor(): Promise<void> {
    return await ErrorProcessor.run(
      async () =>
        await this.webElement
          .getDriver()
          .executeScript("arguments[0].style.backgroundColor = '#ff0000';", this.webElement),
      "Error while coloring element."
    );
  }
}
