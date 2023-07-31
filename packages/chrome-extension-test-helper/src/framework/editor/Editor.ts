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
import PageFragment from "../PageFragment";

export default abstract class Editor extends PageFragment {
  private static readonly LOADING_POPUP_LOCATOR: By = By.className("pf-l-bullseye");
  private static readonly EXPLORE_ICON_LOCATOR: By = By.className("fa-eye");

  public async waitUntilLoaded(): Promise<void> {
    await this.enter();
    if (await this.tools.by(Editor.LOADING_POPUP_LOCATOR).wait(10000).isPresent()) {
      await this.tools.by(Editor.LOADING_POPUP_LOCATOR).wait(30000).untilAbsent();
    }
    await this.tools.by(Editor.EXPLORE_ICON_LOCATOR).wait(5000).untilPresent();
    return await this.leave();
  }

  public async enter(): Promise<void> {
    return await this.root.enterFrame();
  }

  public async leave(): Promise<void> {
    return await this.tools.window().leaveFrame();
  }
}
