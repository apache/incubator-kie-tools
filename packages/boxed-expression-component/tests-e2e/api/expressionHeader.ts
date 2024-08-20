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

export class ExpressionHeader {
  constructor(public page: Locator | Page) {}

  public get() {
    return this.page.getByTestId("kie-tools--bee--expression-header-dropdown").nth(0);
  }

  public async copy() {
    await this.get().click();
    await this.page.getByRole("menuitem", { name: "copy" }).click();
  }

  public async cut() {
    await this.get().click();
    await this.page.getByRole("menuitem", { name: "cut" }).click();
  }

  public async paste() {
    await this.get().click();
    await this.page.getByRole("menuitem", { name: "paste" }).click();
  }

  public async reset() {
    await this.get().click();
    await this.page.getByRole("menuitem", { name: "Reset" }).click();
  }

  public async availableOptions() {
    return this.page.getByRole("menuitem");
  }
}
