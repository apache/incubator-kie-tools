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
import Element from "../Element";
import GitHubEditorPage from "../github-editor/GitHubEditorPage";
import PageFragment from "../PageFragment";

export default class GitHubListItem extends PageFragment {
  private static readonly LINK_LOCATOR: By = By.xpath(`.//a[@class='Link--primary']`);

  public async waitUntilLoaded(): Promise<void> {
    return await this.tools.by(GitHubListItem.LINK_LOCATOR).wait(5000).untilPresent();
  }

  public async open(): Promise<GitHubEditorPage> {
    const link: Element = await this.root.findElement(GitHubListItem.LINK_LOCATOR);
    await link.click();
    return await this.tools.createPage(GitHubEditorPage);
  }
}
