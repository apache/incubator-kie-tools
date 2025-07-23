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
import { Monaco } from "./monaco";

import { ExpressionContainer } from "../api/expressionContainer";
import { SelectExpressionMenu } from "../api/expressions/selectExpressionMenu";

export class BoxedExpressionEditor {
  private readonly _selectExpressionMenu: SelectExpressionMenu;

  constructor(
    public page: Page,
    private monaco: Monaco,
    public baseURL?: string
  ) {
    this.page = page;
    this._selectExpressionMenu = new SelectExpressionMenu(page.locator("body"));
  }

  get selectExpressionMenu(): SelectExpressionMenu {
    return this._selectExpressionMenu;
  }

  get expression() {
    return new ExpressionContainer(this.page.getByTestId("kie-tools--bee--expression-container").nth(0), this.monaco);
  }

  public async pasteToSelectExpression(nth?: number) {
    await this.page
      .getByText("Select expression")
      .nth(nth ?? 0)
      .click();
    await this.page.getByRole("menuitem", { name: "Paste" }).click();
  }

  public async goto() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=misc-empty-boxed-expression--base` ?? "");
  }

  public getContainer() {
    return this.page.locator(".boxed-expression-provider");
  }
}
