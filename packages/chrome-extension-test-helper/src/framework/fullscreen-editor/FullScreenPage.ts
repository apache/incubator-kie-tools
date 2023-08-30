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

import { By, Key } from "selenium-webdriver";
import EditorPage from "../editor/EditorPage";
import Element from "../Element";
import GitHubEditorPage from "../github-editor/GitHubEditorPage";

export default class FullScreenPage extends EditorPage {
  private static readonly EXIT_BUTTON_LOCATOR = By.xpath("//a[@data-testid='exit-fullscreen-button']");

  public async exitFullScreen(): Promise<GitHubEditorPage> {
    const exitButton: Element = await this.tools.by(FullScreenPage.EXIT_BUTTON_LOCATOR).getElement();

    // regular click does not work, clickJs() breaks other tests, sendKeys() does not work on Mac
    await exitButton.sendKeys(Key.ENTER);
    return await this.tools.createPage(GitHubEditorPage);
  }

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(FullScreenPage.EXIT_BUTTON_LOCATOR).wait(10000).untilPresent();
  }

  public async getExitFullScreenUrl(): Promise<string> {
    const exitButton: Element = await this.tools.by(FullScreenPage.EXIT_BUTTON_LOCATOR).getElement();
    return await exitButton.getAttribute("href");
  }
}
