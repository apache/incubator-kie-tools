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
import EditorPage from "../editor/EditorPage";
import Element from "../Element";

export default class GitHubPrPage extends EditorPage {
  private static readonly SEE_AS_DIAGRAM_BUTTON_LOCATOR = By.xpath("//button[text()='See as diagram']");
  private static readonly CLOSE_DIAGRAM_BUTTON_LOCATOR = By.xpath("//button[text()='Close diagram']");
  private static readonly ORIGINAL_BUTTON_LOCATOR = By.xpath("//button[text()='Original']");
  private static readonly CHANGES_BUTTON_LOCATOR = By.xpath("//button[text()='Changes']");
  private static readonly RAW_CONTENT_LOCATOR = By.className("js-file-content");
  private static readonly PR_HEADER_LOCATOR = By.className("gh-header-meta");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(GitHubPrPage.SEE_AS_DIAGRAM_BUTTON_LOCATOR).wait(1000).untilPresent();
  }

  public async scrollToPrHeader(): Promise<void> {
    // this is workaround if it is scrolled to low on the pr page the side bar icons are hidden
    const panel: Element = await this.tools.by(GitHubPrPage.PR_HEADER_LOCATOR).getElement();
    return await panel.scroll();
  }

  public async isSourceOpened(): Promise<boolean> {
    return await this.tools.by(GitHubPrPage.RAW_CONTENT_LOCATOR).wait(5000).isVisible();
  }

  public async isDiagramOpened(): Promise<boolean> {
    return await this.tools.by(EditorPage.FRAME_LOCATOR).wait(5000).isPresent();
  }

  public async seeAsDiagram(): Promise<void> {
    const seeAsDiagramButton: Element = await this.tools.by(GitHubPrPage.SEE_AS_DIAGRAM_BUTTON_LOCATOR).getElement();
    return await seeAsDiagramButton.click();
  }

  public async closeDiagram(): Promise<void> {
    const closeDiagramButton: Element = await this.tools.by(GitHubPrPage.CLOSE_DIAGRAM_BUTTON_LOCATOR).getElement();
    return await closeDiagramButton.click();
  }

  public async original(): Promise<void> {
    const originalButton: Element = await this.tools.by(GitHubPrPage.ORIGINAL_BUTTON_LOCATOR).getElement();
    await originalButton.click();
    return await this.tools.by(GitHubPrPage.CHANGES_BUTTON_LOCATOR).wait(1000).untilEnabled();
  }

  public async changes(): Promise<void> {
    const changesButton: Element = await this.tools.by(GitHubPrPage.CHANGES_BUTTON_LOCATOR).getElement();
    await changesButton.click();
    return await this.tools.by(GitHubPrPage.ORIGINAL_BUTTON_LOCATOR).wait(1000).untilEnabled();
  }
}
