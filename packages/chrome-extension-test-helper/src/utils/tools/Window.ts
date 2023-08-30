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

import ErrorProcessor from "./ErrorProcessor";
import { WebDriver } from "selenium-webdriver";

export default class Window {
  public constructor(private readonly driver: WebDriver) {}

  public async leaveFrame(): Promise<void> {
    return await ErrorProcessor.run(
      async () => await this.driver.switchTo().defaultContent(),
      "Error when switching to default content."
    );
  }

  public async scrollToTop(): Promise<void> {
    return await ErrorProcessor.run(
      async () => await this.driver.executeScript("window.scrollTo(0, 0);"),
      "Error when scrolling to top of page."
    );
  }

  public async switchToSecondWindow(): Promise<void> {
    await this.waitForAnotherWindow();
    const windowHandles: string[] = await this.getWindowHandles();
    if (windowHandles.length > 1) {
      return await this.switchToWindow(windowHandles[1]);
    } else {
      throw new Error("Second window was not found.");
    }
  }

  private async switchToWindow(windowHandle: string): Promise<void> {
    return await ErrorProcessor.run(
      async () => await this.driver.switchTo().window(windowHandle),
      "Error while switching to window."
    );
  }

  private async getWindowHandles(): Promise<string[]> {
    return await ErrorProcessor.run(
      async () => await this.driver.getAllWindowHandles(),
      "Error while getting window handles."
    );
  }

  private async waitForAnotherWindow(): Promise<void> {
    return await ErrorProcessor.run(async () => {
      await this.driver.wait(async () => {
        const windowHandles: string[] = await this.driver.getAllWindowHandles();
        return windowHandles.length > 1;
      }, 5000);
    }, "Error when waiting for second window.");
  }
}
