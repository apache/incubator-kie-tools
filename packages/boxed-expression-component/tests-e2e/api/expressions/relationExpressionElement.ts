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
import { Monaco } from "../../__fixtures__/monaco";
import { ExpressionCell } from "../expressionContainer";
import { NameAndDataTypeCell } from "../nameAndDataTypeCell";

export class RelationExpressionElement {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  public cellAt(param: { column: number; row: number }) {
    return new ExpressionCell(
      this.locator
        .getByTestId(`kie-tools--bee--expression-row-${param.row - 1}`)
        .getByTestId(`kie-tools--bee--expression-column-${param.column}`),
      this.monaco
    );
  }

  public async fill(args: { startAtCell: number; relationData: any[][] }) {
    let cellNumber = args.startAtCell;
    for (const row of args.relationData) {
      for (const cellData of row) {
        await this.monaco.fill({ monacoParentLocator: this.locator, content: cellData, nth: cellNumber });
        cellNumber++;
      }
    }
  }

  get expressionHeaderCell() {
    return new NameAndDataTypeCell(this.locator.getByRole("columnheader").nth(1));
  }

  public columnHeaderAtIndex(number: number) {
    return new NameAndDataTypeCell(this.locator.getByRole("columnheader").nth(1 + number));
  }

  public async addRowAboveOfRowAtIndex(index: number) {
    await this.locator
      .getByRole("cell", { name: `${index}`, exact: true })
      .nth(0)
      .hover({
        position: {
          x: 0,
          y: 0,
        },
      });
    await this.locator
      .getByRole("cell", { name: `${index}`, exact: true })
      .nth(0)
      .locator("svg")
      .click();
  }

  public async addRowAtBellowOfRowAtIndex(index: number) {
    await this.locator
      .getByRole("cell", { name: `${index}`, exact: true })
      .nth(0)
      .hover();
    await this.locator
      .getByRole("cell", { name: `${index}`, exact: true })
      .nth(0)
      .locator("svg")
      .click();
  }

  public async addColumnAtRightOfIndex(index: number) {
    const bb = await this.locator.getByRole("columnheader").nth(index).boundingBox();
    await this.locator
      .getByRole("columnheader")
      .nth(index + 1)
      .hover({
        position: {
          x: (bb?.width ?? 0) / 2,
          y: 0,
        },
      });
    await this.locator
      .getByRole("columnheader")
      .nth(index + 1)
      .locator("svg")
      .click();
  }

  public async addColumnAtLeftOfIndex(index: number) {
    // index+1 because we're ignoring the expression header cell
    await this.locator
      .getByRole("columnheader")
      .nth(index + 1)
      .hover({
        position: {
          x: 0,
          y: 0,
        },
      });
    await this.locator
      .getByRole("columnheader")
      .nth(index + 1)
      .locator("svg")
      .click();
  }
}
