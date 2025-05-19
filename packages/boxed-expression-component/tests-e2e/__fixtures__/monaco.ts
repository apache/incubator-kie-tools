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

import { ProjectName } from "@kie-tools/playwright-base/projectNames";
import { Page, Locator } from "@playwright/test";

export class Monaco {
  constructor(
    public page: Page,
    public projectName: ProjectName
  ) {}

  public async fill(args: { monacoParentLocator: Locator | Page; content: string; nth?: number; submit?: boolean }) {
    if (args.nth !== undefined) {
      await args.monacoParentLocator.getByTestId("monaco-container").nth(args.nth).dblclick();
    } else {
      await args.monacoParentLocator.getByTestId("monaco-container").dblclick();
    }

    if (this.projectName !== ProjectName.WEBKIT) {
      await this.page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").press("Control+A");
    }
    if (this.projectName === ProjectName.WEBKIT) {
      await this.page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").press("Meta+A");
    }
    // FEEL text input selector when the monaco editor is selected.
    await this.page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill(args.content);
    await this.page.keyboard.press("Home");
    if (args.submit === undefined || args.submit) {
      await this.page.keyboard.press("Enter");
    }
  }

  public async canFill(args: { monacoParentLocator: Locator | Page; nth?: number }) {
    if (args.nth !== undefined) {
      await args.monacoParentLocator.getByTestId("monaco-container").nth(args.nth).dblclick();
    } else {
      await args.monacoParentLocator.getByTestId("monaco-container").dblclick();
    }

    if (await this.page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").isVisible()) {
      return true;
    }

    // Try to fill by typing instead of double-click
    if (args.nth !== undefined) {
      await args.monacoParentLocator.getByTestId("monaco-container").nth(args.nth).press("KeyA");
    } else {
      await args.monacoParentLocator.getByTestId("monaco-container").press("KeyA");
    }
    return await this.page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").isVisible();
  }
}
