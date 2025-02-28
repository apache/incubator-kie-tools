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

import { expect, Page } from "@playwright/test";

export enum TabName {
  EDITOR = "Editor",
  DATA_TYPES = "Data types",
  INCLUDED_MODELS = "Included models",
}

export const STORYBOOK__DMN_EDITOR_TOGGLE_READ_ONLY = "storybook--dmn-editor-toggle-read-only";

export class Editor {
  constructor(
    public page: Page,
    public baseURL?: string
  ) {}

  public async open() {
    await this.page.goto(`${this.baseURL}/iframe.html?args=&id=misc-empty--empty&viewMode=story`);
  }

  public async openLoanPreQualification() {
    await this.page.goto(
      `${this.baseURL}/iframe.html?args=&id=use-cases-loan-pre-qualification--loan-pre-qualification&viewMode=story`
    );
  }

  public async openEvaluationHighlights() {
    await this.page.goto(`${this.baseURL}/iframe.html?args=&id=misc-evaluationhighlights--evaluation-highlights-story`);
  }

  public async setIsReadOnly(newState: boolean) {
    const currentState = (await this.page.getByTestId(STORYBOOK__DMN_EDITOR_TOGGLE_READ_ONLY).textContent()) === "true";
    if (currentState !== newState) {
      await this.page.evaluate((toogleSelector: string) => {
        document.querySelector<HTMLButtonElement>(`button[data-testid='${toogleSelector}']`)?.click();
      }, STORYBOOK__DMN_EDITOR_TOGGLE_READ_ONLY);
    }
    await expect(await this.page.getByTestId(STORYBOOK__DMN_EDITOR_TOGGLE_READ_ONLY).textContent()).toBe(
      newState.toString()
    );
  }

  public async changeTab(args: { tab: TabName }) {
    await this.page.getByRole("tab", { name: args.tab }).click();
  }
}
