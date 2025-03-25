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
import { SelectorPanel } from "./selectorPanel";

export enum AssetType {
  DECISION,
  RULE,
}

export class Editor {
  constructor(
    public page: Page,
    public selectorPanel: SelectorPanel,
    public baseURL?: string
  ) {
    this.page = page;
    this.baseURL = baseURL;
  }

  public getIframeURL(iframeId: string) {
    return `iframe.html?id=${iframeId}&viewMode=story`;
  }

  public async openEmpty() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`misc-empty--empty`)}` ?? "");
  }

  public async createTestScenario(type: AssetType) {
    await this.openEmpty();
    type === AssetType.DECISION
      ? await this.page.locator("#asset-type-select").selectOption("DMN")
      : await this.page.locator("#asset-type-select").selectOption("RULE");
    if (type === AssetType.DECISION) {
      await this.page.getByLabel("Select a model...").click();
      await this.page.getByRole("option", { name: "empty.dmn empty.dmn" }).click();
    }
    await this.page.getByRole("button", { name: "Create" }).click();
    await this.selectorPanel.close();
  }

  public async switchToTestScenarioTable() {
    await this.page.getByRole("tab", { name: "Test Scenario" }).click();
  }

  public async switchToBackgroundTable() {
    await this.page.getByRole("tab", { name: "Background" }).click();
  }

  public get() {
    return this.page.getByTestId("kie-scesim-editor--container");
  }
}
