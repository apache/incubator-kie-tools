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
import Element from "../Element";
import GitHubListItem from "./GitHubListItem";
import Page from "../Page";

export default class GitHubListPage extends Page {
  private static readonly DOUBLE_DOT_LOCATOR: By = By.xpath("//a[@title='Go to parent directory']");

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(GitHubListPage.DOUBLE_DOT_LOCATOR).wait(1000).untilPresent();
  }

  public async getFile(name: string): Promise<GitHubListItem> {
    const file: By = By.xpath(`//div[@role="rowheader"]/span[.//a[text()='${name}']]`);
    await this.tools.by(file).wait(5000).untilPresent();
    return await this.tools.createPageFragment(GitHubListItem, await this.tools.by(file).getElement());
  }
}
