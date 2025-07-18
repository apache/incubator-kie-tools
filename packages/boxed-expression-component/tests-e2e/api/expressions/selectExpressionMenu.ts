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

import { Locator, Page } from "@playwright/test";

export class SelectExpressionMenu {
  constructor(public element: Locator) {
    this.element = element;
  }

  private async click() {
    await this.element.getByText("Select expression").click();
  }

  public async selectRelation() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Relation" }).click();
  }

  public async selectLiteral() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Literal" }).click();
  }

  public async paste() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Paste" }).click();
  }

  public async selectContext() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Context" }).click();
  }

  public async selectDecisionTable() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Decision" }).click();
  }

  public async selectInvocation() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Invocation" }).click();
  }

  public async selectList() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "List" }).click();
  }

  public async selectFunction() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Function" }).click();
  }

  public async selectFor() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "For" }).click();
  }

  public async selectEvery() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Every" }).click();
  }

  public async selectFilter() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Filter" }).click();
  }

  public async selectConditional() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "if Conditional" }).click();
  }

  public async selectSome() {
    await this.click();
    await this.element.page().getByRole("menuitem", { name: "Some" }).click();
  }
}
