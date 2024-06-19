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
import { Monaco } from "./monaco";

export class BoxedExpressionEditor {
  constructor(public page: Page, private monaco: Monaco, public baseURL?: string) {
    this.page = page;
  }

  public async select(from: Page | Locator = this.page) {
    await from.getByText("Select expression").click();
  }

  public async selectBoxedLiteral(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Literal" }).click();
  }

  public async selectBoxedContext(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Context" }).click();
  }

  public async selectDecisionTable(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Decision" }).click();
  }

  public async fillDecisionTable(args: { startAtCell: number; tableData: any[][] }) {
    let cellNumber = args.startAtCell;
    for (const row of args.tableData) {
      for (const cellData of row) {
        if (cellData === "-") {
          cellNumber++;
          continue;
        }
        await this.monaco.fill({ monacoParentLocator: this.page, content: cellData, nth: cellNumber });
        cellNumber++;
      }
      cellNumber++;
    }
  }

  public async selectRelation(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Relation" }).click();
  }

  public async fillRelation(args: { startAtCell: number; relationData: any[][] }) {
    let cellNumber = args.startAtCell;
    for (const row of args.relationData) {
      for (const cellData of row) {
        await this.monaco.fill({ monacoParentLocator: this.page, content: cellData, nth: cellNumber });
        cellNumber++;
      }
    }
  }

  public async selectBoxedInvocation(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Invocation" }).click();
  }

  public async selectBoxedList(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "List" }).click();
  }

  public async selectBoxedFunction(from: Page | Locator = this.page) {
    this.select(from);
    await this.page.getByRole("menuitem", { name: "Function" }).click();
  }

  public async goto() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=misc-empty-boxed-expression--base` ?? "");
  }

  public getContainer() {
    return this.page.locator(".boxed-expression-provider");
  }
}
