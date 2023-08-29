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

import { By, WebDriver, error, until } from "selenium-webdriver";
import Element from "./Element";
import ErrorProcessor from "../utils/tools/ErrorProcessor";

export default class LocatorWaitAction {
  private static readonly DEFAULT_TIMEOUT: number = 100;

  public constructor(
    private readonly driver: WebDriver,
    private readonly by: By,
    private readonly timeout: number = timeout ?? LocatorWaitAction.DEFAULT_TIMEOUT
  ) {}

  private async absent(): Promise<void> {
    await this.driver.wait(async () => (await this.driver.findElements(this.by)).length === 0, this.timeout);
    return Promise.resolve();
  }

  public async untilAbsent(): Promise<void> {
    return await ErrorProcessor.run(async () => await this.absent(), "Error while waiting until absent " + this.by);
  }

  public async isAbsent(): Promise<boolean> {
    try {
      await this.absent();
      return true;
    } catch (err) {
      if (err instanceof error.TimeoutError) {
        return false;
      } else {
        throw err;
      }
    }
  }

  private async present(): Promise<Element> {
    return new Element(await this.driver.wait(until.elementLocated(this.by), this.timeout));
  }

  public async untilPresent(): Promise<void> {
    await ErrorProcessor.run(async () => await this.present(), "Error while waiting until present: " + this.by);
    return Promise.resolve();
  }

  public async isPresent(): Promise<boolean> {
    try {
      await this.present();
      return true;
    } catch (err) {
      if (err instanceof error.TimeoutError) {
        return false;
      } else {
        throw err;
      }
    }
  }

  private async visible(): Promise<void> {
    const webElement = await this.driver.findElement(this.by);
    await this.driver.wait(until.elementIsVisible(webElement), this.timeout);
    return Promise.resolve();
  }

  public async untilVisible(): Promise<void> {
    return await ErrorProcessor.run(async () => await this.visible(), "Error while waiting until visible: " + this.by);
  }

  public async isVisible(): Promise<boolean> {
    try {
      await this.visible();
      return true;
    } catch (err) {
      if (err instanceof error.TimeoutError) {
        return false;
      } else {
        throw err;
      }
    }
  }

  private async value(): Promise<string> {
    const webElement = await this.driver.findElement(this.by);
    await this.driver.wait(async () => (await webElement.getAttribute("value")) !== "", this.timeout);
    return await webElement.getAttribute("value");
  }

  public async untilHasValue(): Promise<string> {
    return await ErrorProcessor.run(async () => await this.value(), "Error while waiting until has value: " + this.by);
  }

  public async hasValue(): Promise<boolean> {
    try {
      await this.value();
      return true;
    } catch (err) {
      if (err instanceof error.TimeoutError) {
        return false;
      } else {
        throw err;
      }
    }
  }

  private async enabled(): Promise<Element> {
    const webElement = await this.driver.findElement(this.by);
    return new Element(await this.driver.wait(until.elementIsEnabled(webElement), this.timeout));
  }

  public async untilEnabled(): Promise<void> {
    await ErrorProcessor.run(async () => await this.enabled(), "Error while waiting until enabled: " + this.by);
    return Promise.resolve();
  }
}
