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

export enum AddRowPosition {
  ABOVE,
  BELOW,
}

export enum AddColumnPosition {
  LEFT,
  RIGHT,
}

export enum Type {
  ARROW,
  KEYBOARD_SHORTCUT,
}

export class Table {
  constructor(public page: Page) {}

  public async addRow(args: { targetCellName: string; position: AddRowPosition }) {
    await this.page.getByRole("cell", { name: args.targetCellName, exact: true }).click({ button: "right" });
    args.position === AddRowPosition.BELOW
      ? await this.page.getByRole("menuitem", { name: "Insert Below" }).click()
      : await this.page.getByRole("menuitem", { name: "Insert Above" }).click();
  }

  public async addPropertyColumn(args: { targetCellName: string; position: AddColumnPosition; nth: number }) {
    await this.page.getByRole("columnheader", { name: args.targetCellName }).nth(args.nth).click({ button: "right" });
    args.position === AddColumnPosition.LEFT
      ? await this.page.getByRole("menuitem", { name: "Insert Field Left" }).click()
      : await this.page.getByRole("menuitem", { name: "Insert Field Right" }).click();
  }

  public async addInstanceColumn(args: { targetCellName: string; position: AddColumnPosition }) {
    await this.page.getByRole("columnheader", { name: args.targetCellName }).click({ button: "right" });
    args.position === AddColumnPosition.LEFT
      ? await this.page.getByRole("menuitem", { name: "Insert Instance Left" }).click()
      : await this.page.getByRole("menuitem", { name: "Insert Instance Right" }).click();
  }
  public async selectCell(args: { rowNumber: string; columnNumber: number }) {
    await this.page
      .getByRole("row", { name: args.rowNumber })
      .getByTestId("monaco-container")
      .nth(args.columnNumber)
      .click();
  }
  public getColumnHeader(args: { name: string }) {
    return this.page.getByRole("columnheader", { name: args.name });
  }

  public getCell(args: { rowNumber: string; columnNumber: number }) {
    return this.page.getByRole("row", { name: args.rowNumber }).nth(args.columnNumber);
  }
  public async deleteCellContent(args: { rowNumber: string; columnNumber: number }) {
    await this.page
      .getByRole("row", { name: args.rowNumber })
      .getByTestId("monaco-container")
      .nth(args.columnNumber)
      .click();
    await this.page.keyboard.press("Delete");
  }
  public async deselectCell(args: { rowNumber: string; columnNumber: number }) {
    await this.page
      .getByRole("row", { name: args.rowNumber, exact: true })
      .getByTestId("monaco-container")
      .nth(args.columnNumber)
      .press("Escape");
  }
  public async navigateLeft(args: { rowNumber: string; columnNumber: number; type: Type }) {
    args.type === Type.ARROW
      ? await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("ArrowLeft")
      : await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("Shift+Tab");
  }
  public async navigateRight(args: { rowNumber: string; columnNumber: number; type: Type }) {
    args.type === Type.ARROW
      ? await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("ArrowRight")
      : await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("Tab");
  }
  public async navigateUp(args: { rowNumber: string; columnNumber: number; type: Type }) {
    args.type === Type.ARROW
      ? await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("ArrowUp")
      : (await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("Shift+Enter"),
        await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("Shift+Enter"));
  }
  public async navigateDown(args: { rowNumber: string; columnNumber: number; type: Type }) {
    args.type === Type.ARROW
      ? await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("ArrowDown")
      : (await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("Enter+Enter"),
        await this.page
          .getByRole("row", { name: args.rowNumber, exact: true })
          .getByTestId("monaco-container")
          .nth(args.columnNumber)
          .press("Enter+Enter"));
  }
}
