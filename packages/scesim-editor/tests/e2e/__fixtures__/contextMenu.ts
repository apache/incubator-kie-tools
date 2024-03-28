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

export enum HeadingType {
  SELECTION = "SELECTION",
  SCENARIO = "SCENARIO",
  FIELD = "FIELD",
  INSTANCE = "INSTANCE",
}

export enum MenuItem {
  DELETE_SCENARIO = "Delete",
  DELETE_INSTANCE = "Delete Instance",
  DELETE_FIELD = "Delete Field",
  COPY = "copy",
  CUT = "cut",
  PASTE = "paste",
  RESET = "reset",
}

export class ContextMenu {
  constructor(public page: Page) {}

  public async openOnCell(args: { rowNumber: string; columnNumber: number }) {
    await this.page
      .getByRole("row", { name: args.rowNumber })
      .getByTestId("monaco-container")
      .nth(args.columnNumber)
      .click({ button: "right" });
  }

  public async openOnInstance(args: { name: string }) {
    await this.page.getByRole("columnheader", { name: args.name }).click({ button: "right" });
  }

  public async openOnProperty(args: { name: string; columnNumber: number }) {
    await this.page.getByRole("columnheader", { name: args.name }).nth(args.columnNumber).click({ button: "right" });
  }

  public async clickMenuItem(args: { menuItem: MenuItem }) {
    await this.page.getByRole("menuitem", { name: `${args.menuItem}` }).click();
  }

  public getHeading(args: { heading: HeadingType }) {
    return this.page.getByRole("heading", { name: `${args.heading}`, exact: true });
  }
}
