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

import { Locator } from "@playwright/test";
import { ContextMenu } from "./expressionContainer";

export class NameAndDataTypeWithStandalonePopoverCell {
  constructor(private locator: Locator) {}

  public async open() {
    await this.locator.nth(0).click();
  }

  public async getPopoverMenu() {
    return this.locator.page().getByTestId("kie-tools--bee--expression-popover-menu");
  }

  public async setName({ name, commit = false }: { name: string; commit?: boolean }) {
    await (await this.getPopoverMenu()).getByRole("textbox").fill(name);
    if (commit) {
      await (await this.getPopoverMenu()).getByRole("textbox").press("Enter");
    }
  }

  public async setDataType({ dataType, commit = false }: { dataType: string; commit?: boolean }) {
    const popoverMenu = await this.getPopoverMenu();
    await popoverMenu.getByTestId("kie-tools--bee--edit-expression-data-type").click();
    await popoverMenu.getByPlaceholder("Choose...").nth(0).fill(dataType);
    await popoverMenu.getByRole("group").nth(0).getByRole("option").nth(0).click();
    if (commit) {
      await popoverMenu.page().keyboard.press("Enter");
    }
  }

  public async getName() {
    return await (await this.getPopoverMenu()).getByTestId("kie-tools--bee--expression-info-name").nth(0).innerText();
  }

  public async getDataType() {
    return await (await this.getPopoverMenu())
      .getByTestId("kie-tools--bee--expression-info-data-type")
      .nth(0)
      .innerText();
  }

  public async select() {
    // Uses the 1,1 to avoid problems by clicking on the element corner
    await this.content.click({ position: { x: 1, y: 1 } });
  }

  get content() {
    return this.locator.nth(0);
  }

  get contextMenu() {
    return new ContextMenu(this.locator);
  }
}
