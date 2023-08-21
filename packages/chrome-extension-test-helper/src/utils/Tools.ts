/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { By, WebDriver } from "selenium-webdriver";
import Clipboard from "./tools/Clipboard";
import Driver from "./tools/Driver";
import Element from "../framework/Element";
import Locator from "../framework/Locator";
import Page from "../framework/Page";
import PageFragment from "../framework/PageFragment";
import Screenshot from "./tools/ScreenShot";
import Window from "./tools/Window";
import { resolve } from "path";

export default class Tools {
  private static readonly SCREENSHOTS_DIR: string = resolve("dist-it-tests", "screenshots");

  private readonly screenShot: Screenshot;

  private constructor(private readonly driver: WebDriver, private readonly testName: string) {
    this.screenShot = new Screenshot(this.driver, Tools.SCREENSHOTS_DIR);
  }

  public async finishTest(): Promise<void> {
    const screenshotName: string = "screenshot_after_" + this.testName;
    await this.window().leaveFrame();
    await this.makeScreenshots(screenshotName);
    return await Driver.quit(this.driver);
  }

  public async makeScreenshots(screenshotNamePrefix: string): Promise<void> {
    await this.screenShot.takeHtml(screenshotNamePrefix);
    return await this.screenShot.takePng(screenshotNamePrefix);
  }

  public sleep(timeout: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, timeout));
  }

  public by(by: By): Locator {
    return new Locator(this.driver, by);
  }

  public clipboard(): Clipboard {
    return new Clipboard(this.driver);
  }

  public window(): Window {
    return new Window(this.driver);
  }

  public async openPage<T extends Page>(type: new (tools: Tools) => T, url: string): Promise<T> {
    await Driver.openUrl(this.driver, url);
    return await this.createPage(type);
  }

  public async createPage<T extends Page>(type: new (tools: Tools) => T): Promise<T> {
    return await Page.create(type, this);
  }

  public async createPageFragment<T extends PageFragment>(
    type: new (tools: Tools, root: Element) => T,
    root: Element
  ): Promise<T> {
    return await PageFragment.create(type, this, root);
  }

  public static async init(testName: string): Promise<Tools> {
    return new Tools(await Driver.init(), testName);
  }
}
