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
import { ChildExpression } from "../expressionContainer";
import { NameAndDataTypeCell } from "../nameAndDataTypeCell";

export class ListExpressionElement {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  public async addEntryAtTop() {
    await this.locator
      .getByRole("cell", { name: "1" })
      .nth(0)
      .hover({
        position: {
          x: 0,
          y: 0,
        },
      });
    await this.locator.getByRole("cell", { name: "1" }).nth(0).locator("svg").click();
  }

  public async addEntryBelowOfEntryAtIndex(index: number) {
    await this.locator
      .getByRole("cell", { name: `${index + 1}` })
      .nth(0)
      .hover();
    await this.locator
      .getByRole("cell", { name: `${index + 1}` })
      .nth(0)
      .locator("svg")
      .click();
  }

  public row(index: number) {
    return new Row(this.locator.getByTestId(`kie-tools--bee--expression-row-${index}`).nth(0), this.monaco);
  }

  get expressionHeaderCell() {
    return new NameAndDataTypeCell(this.locator.getByRole("columnheader").nth(1));
  }
}

export class Row {
  private readonly childExpression: ChildExpression;

  constructor(
    private locator: Locator,
    monaco: Monaco
  ) {
    this.childExpression = new ChildExpression(locator, monaco);
  }

  get expression() {
    return this.childExpression.expression;
  }

  get elementCell() {
    return this.childExpression.elementCell;
  }

  get selectExpressionMenu() {
    return this.childExpression.selectExpressionMenu;
  }

  get cell() {
    return new NameAndDataTypeCell(this.locator.getByRole("cell").nth(0));
  }
}
