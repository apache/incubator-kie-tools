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
import Page from "../Page";

export default class GitHubRepoPage extends Page {
  private static readonly TOKEN_ICON: By = By.className("kogito-menu-icon");
  private static readonly TOKEN_INPUT: By = By.className("kogito-github-token-input");
  private static readonly RESET_BUTTON: By = By.xpath("//button[text()='Reset']");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(GitHubRepoPage.TOKEN_ICON).wait(1000).untilPresent();
  }

  public async addToken(token: string): Promise<void> {
    const tokenIcon = await this.tools.by(GitHubRepoPage.TOKEN_ICON).getElement();
    await tokenIcon.click();
    await this.tools.by(GitHubRepoPage.RESET_BUTTON).wait(1000).untilPresent();
    await this.tools.by(GitHubRepoPage.TOKEN_INPUT).wait(1000).untilPresent();
    const resetButton = await this.tools.by(GitHubRepoPage.RESET_BUTTON).getElement();
    await resetButton.click();
    const tokenInput = await this.tools.by(GitHubRepoPage.TOKEN_INPUT).getElement();
    await tokenInput.click();
    await this.tools.clipboard().setContent(token);
    await tokenInput.sendKeys(this.tools.clipboard().getCtrvKeys());
    await this.tools.by(GitHubRepoPage.TOKEN_INPUT).wait(5000).untilAbsent();
  }
}
