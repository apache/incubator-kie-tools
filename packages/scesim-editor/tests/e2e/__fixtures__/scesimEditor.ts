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

import { Page, Locator } from "@playwright/test";

export enum AddRowPosition {
  ABOVE,
  BELOW,
}

export enum AddColumnPosition {
  LEFT,
  RIGHT,
}

export class SceSimEditor {
  constructor(public page: Page) {}

  public async addRow(args: { targetCell: string; position: AddRowPosition }) {
    await this.page.getByRole("cell", { name: args.targetCell, exact: true }).click({ button: "right" });
    args.position === AddRowPosition.BELOW
      ? await this.page.getByRole("menuitem", { name: "Insert Below" }).click()
      : await this.page.getByRole("menuitem", { name: "Insert Above" }).click();
  }

  public async addPropertyColumn(args: { targetCell: string; position: AddColumnPosition; nth: number }) {
    args.nth === 0
      ? await this.page.getByRole("columnheader", { name: args.targetCell }).first().click({ button: "right" })
      : await this.page.getByRole("columnheader", { name: args.targetCell }).nth(args.nth).click({ button: "right" });
    args.position === AddColumnPosition.LEFT
      ? await this.page.getByRole("menuitem", { name: "Insert Field Left" }).click()
      : await this.page.getByRole("menuitem", { name: "Insert Field Right" }).click();
  }

  public async addInstanceColumn(args: { targetCell: string; position: AddColumnPosition }) {
    await this.page.getByRole("columnheader", { name: args.targetCell }).click({ button: "right" });
    args.position === AddColumnPosition.LEFT
      ? await this.page.getByRole("menuitem", { name: "Insert Instance Left" }).click()
      : await this.page.getByRole("menuitem", { name: "Insert Instance Right" }).click();
  }
}
