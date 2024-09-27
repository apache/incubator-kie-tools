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
import { NameAndDataTypeWithStandalonePopoverCell as ParameterDescriptionCell } from "../nameAndDataTypeWithStandalonePopoverCell";

export class InvocationExpressionElement {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  public parameter(index: number) {
    return new InvocationParameter(this.locator.getByTestId(`kie-tools--bee--expression-row-${index}`), this.monaco);
  }

  get invokedFunctionNameCell() {
    return this.locator.getByTestId(`kie-tools--bee--table-header-invokedFunctionName`);
  }

  get expressionHeaderCell() {
    return new NameAndDataTypeCell(this.locator.getByRole("columnheader"));
  }

  public async parametersCount() {
    return (await this.locator.getByRole("row").count()) - 2; // -2 because of the 2 header rows
  }

  public async addParameterAboveOfEntryAtIndex(index: number) {
    await this.locator
      .getByTestId(`kie-tools--bee--expression-row-${index}`)
      .getByTestId("kie-tools--bee--expression-column-1")
      .nth(0)
      .hover({
        position: {
          x: 0,
          y: 0,
        },
      });

    await this.locator
      .getByTestId(`kie-tools--bee--expression-row-${index}`)
      .getByTestId("kie-tools--bee--expression-column-1")
      .nth(0)
      .locator("svg")
      .click();
  }

  public async addParameterBelowOfEntryAtIndex(index: number) {
    await this.locator
      .getByTestId(`kie-tools--bee--expression-row-${index}`)
      .getByTestId("kie-tools--bee--expression-column-1")
      .nth(0)
      .hover();

    await this.locator
      .getByTestId(`kie-tools--bee--expression-row-${index}`)
      .getByTestId("kie-tools--bee--expression-column-1")
      .nth(0)
      .locator("svg")
      .click();
  }
}

class InvocationParameter {
  private readonly expressionElement: ChildExpression;

  constructor(
    private locator: Locator,
    monaco: Monaco
  ) {
    this.expressionElement = new ChildExpression(
      locator.getByTestId(`kie-tools--bee--expression-column-2`).nth(0),
      monaco
    );
  }

  get expression() {
    return this.expressionElement.expression;
  }

  get selectExpressionMenu() {
    return this.expressionElement.selectExpressionMenu;
  }

  get descriptionCell() {
    return new ParameterDescriptionCell(this.locator.getByTestId(`kie-tools--bee--expression-column-1`).nth(0));
  }

  get elementCell() {
    return this.expressionElement.elementCell;
  }
}
