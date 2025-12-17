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
import FullScreenPage from "../fullscreen-editor/FullScreenPage";

export default class GitHubEditorPage extends EditorPage {
  private static readonly SEE_AS_SOURCE_BUTTON_LOCATOR = By.xpath("//button[@data-testid='see-as-source-button']");
  private static readonly COPY_LINK_BUTTON_LOCATOR = By.xpath("//button[@data-testid='copy-link-button']");
  private static readonly COPY_LINK_ALERT_LOCATOR = By.xpath("//div[@data-testid='link-copied-alert']");
  private static readonly SEE_AS_DIAGRAM_BUTTON_LOCATOR = By.xpath("//button[@data-testid='see-as-diagram-button']");
  private static readonly FULL_SCREEN_BUTTON_LOCATOR = By.xpath("//button[@data-testid='go-fullscreen-button']");
  private static readonly SOURCE_VIEW_LOCATOR = By.xpath("//textarea[@id='read-only-cursor-text-area']");
  private static readonly KOGITO_CONTAINER_LOCATOR = By.className("kogito-iframe-container");
  private static readonly KOGITO_TOOLBAR_LOCATOR = By.className("kogito-toolbar-container");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(GitHubEditorPage.KOGITO_TOOLBAR_LOCATOR).wait(5000).untilPresent();
  }

  public async copyLinkToOnlineEditor(): Promise<void> {
    const copyLinkButton: Element = await this.tools.by(GitHubEditorPage.COPY_LINK_BUTTON_LOCATOR).getElement();
    await copyLinkButton.click();
    await this.tools.by(GitHubEditorPage.COPY_LINK_ALERT_LOCATOR).wait(1000).untilPresent();
    return await this.tools.by(GitHubEditorPage.COPY_LINK_ALERT_LOCATOR).wait(5000).untilAbsent();
  }

  public async seeAsSource(): Promise<void> {
    const seeAsSourceButton: Element = await this.tools.by(GitHubEditorPage.SEE_AS_SOURCE_BUTTON_LOCATOR).getElement();
    return await seeAsSourceButton.click();
  }

  public async seeAsDiagram(): Promise<void> {
    const seeAsDiagramButton = await this.tools.by(GitHubEditorPage.SEE_AS_DIAGRAM_BUTTON_LOCATOR).getElement();
    return await seeAsDiagramButton.click();
  }

  public async isSourceVisible(): Promise<boolean> {
    return await this.tools.by(GitHubEditorPage.SOURCE_VIEW_LOCATOR).wait(1000).isVisible();
  }

  public async isEditorVisible(): Promise<boolean> {
    return await this.tools.by(GitHubEditorPage.KOGITO_CONTAINER_LOCATOR).wait(1000).isVisible();
  }

  public async fullScreen(): Promise<FullScreenPage> {
    const fullScreenButton: Element = await this.tools.by(GitHubEditorPage.FULL_SCREEN_BUTTON_LOCATOR).getElement();
    // regular click sometimes does not work
    await fullScreenButton.click();
    return await this.tools.createPage(FullScreenPage);
  }
}
