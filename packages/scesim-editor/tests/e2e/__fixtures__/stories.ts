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

import { Page } from "@playwright/test";

export class Stories {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
    this.baseURL = baseURL;
  }

  public getIframeURL(iframeId: string) {
    return `iframe.html?id=${iframeId}&viewMode=story`;
  }

  public async openSelectionPage() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`misc-empty-scesim-editor--base`)}` ?? "");
  }

  public async openTestScenarioTableDecision() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`misc-empty-scesim-editor--base`)}` ?? "");
    await this.page.locator("#asset-type-select").selectOption("DMN");
    await this.page.getByRole("button", { name: "Create" }).click();
    await this.page.getByLabel("Close drawer panel").click();
  }

  public async openTestScenarioTableRule() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`misc-empty-scesim-editor--base`)}` ?? "");
    await this.page.locator("#asset-type-select").selectOption("RULE");
    await this.page.getByRole("button", { name: "Create" }).click();
    await this.page.getByLabel("Close drawer panel").click();
  }

  public async openBackgroundTableDecision() {
    this.openTestScenarioTableDecision();
    await this.page.getByRole("tab", { name: "Background" }).click();
  }

  public async openBackgroundTableRule() {
    this.openTestScenarioTableRule();
    await this.page.getByRole("tab", { name: "Background" }).click();
  }
}
