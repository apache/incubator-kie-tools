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

export class NameAndDataTypeCell {
  constructor(private locator: Locator) {}

  public async open() {
    await this.locator.nth(0).click();
  }

  public async getPopoverMenu() {
    return this.locator.page().getByTestId("kie-tools--bee--expression-popover-menu");
  }

  public async setName(params: { name: string; close: boolean }) {
    await this.locator.getByRole("textbox").fill(params.name);
    if (params.close) {
      await this.locator.getByRole("textbox").press("Enter");
    }
  }

  public async setDataType(params: { dataType: string; close: boolean }) {
    await this.locator.getByTestId("kie-tools--bee--edit-expression-data-type").click();
    await this.locator.getByPlaceholder("Choose...").nth(0).fill(params.dataType);
    await this.locator.getByRole("group").nth(0).getByRole("option").nth(0).click();
    if (params.close) {
      await this.locator.page().keyboard.press("Enter");
    }
  }

  public async getName() {
    return await this.locator.getByTestId("kie-tools--bee--expression-info-name").nth(0).innerText();
  }

  public async getDataType() {
    return await this.locator.getByTestId("kie-tools--bee--expression-info-data-type").nth(0).innerText();
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
