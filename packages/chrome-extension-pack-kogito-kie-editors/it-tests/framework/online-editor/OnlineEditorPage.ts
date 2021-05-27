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

import { By } from "selenium-webdriver";
import EditorPage from "../editor/EditorPage";
import Element from "../Element";

export default class OnlineEditorPage extends EditorPage {
  private static readonly TOOLBAR_LOCATOR: By = By.className("kogito--editor__toolbar");
  private static readonly FILE_NAME_LOCATOR: By = By.xpath("//input[@aria-label='Edit file name']");
  private static readonly CLOSE_TOUR_BUTTON_LOCATOR: By = By.css(
    "[data-ouia-component-id='dmn-guided-tour'] button[aria-label='Close']"
  );

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(OnlineEditorPage.TOOLBAR_LOCATOR).wait(2000).untilPresent();
  }

  public async getFileName(): Promise<string> {
    const filename: Element = await this.tools.by(OnlineEditorPage.FILE_NAME_LOCATOR).getElement();
    return await filename.getAttribute("value");
  }

  public async closeTour(): Promise<void> {
    const closeTourButton: Element = await this.tools.by(OnlineEditorPage.CLOSE_TOUR_BUTTON_LOCATOR).getElement();
    await closeTourButton.click();
  }
}
