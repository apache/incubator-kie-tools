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

import { By, WebDriver, WebElement } from "selenium-webdriver";
import Element from "./Element";
import ErrorProcessor from "../utils/tools/ErrorProcessor";
import LocatorWaitAction from "./LocatorWaitAction";

export default class Locator {
  constructor(private readonly driver: WebDriver, private readonly by: By) {}

  public wait(timeout?: number): LocatorWaitAction {
    return new LocatorWaitAction(this.driver, this.by, timeout);
  }

  public async getElements(): Promise<Element[]> {
    return await ErrorProcessor.run(async () => {
      const webElements: WebElement[] = await this.driver.findElements(this.by);
      return webElements.map((webElement) => new Element(webElement));
    }, "Error while getting elements: " + this.by);
  }

  public async getElement(): Promise<Element> {
    return await ErrorProcessor.run(async () => {
      const webElement: WebElement = await this.driver.findElement(this.by);
      return new Element(webElement);
    }, "Error while getting element: " + this.by);
  }
}
