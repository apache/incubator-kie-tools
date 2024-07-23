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

export enum DataType {
  Undefined = "<Undefined>",
  Any = "Any",
  Boolean = "boolean",
  Context = "context",
  Date = "date",
  DateTime = "date and time",
  DateTimeDuration = "days and time duration",
  Number = "number",
  String = "string",
  Time = "time",
  YearsMonthsDuration = "years and months duration",
}

export enum ConstraintType {
  ENUMERATION = "Enumeration",
  EXPRESSION = "Expression",
  NONE = "None",
  RANGE = "Range",
}

export class DataTypes {
  constructor(public page: Page) {}

  public get() {
    return this.page.getByTestId("kie-dmn-editor--data-types-container");
  }

  public getNoneConstraintButton() {
    return this.get().getByRole("button", { name: ConstraintType.NONE, exact: true });
  }

  public getEnumerationConstraintButton() {
    return this.get().getByRole("button", { name: ConstraintType.ENUMERATION, exact: true });
  }

  public getExpressionConstraintButton() {
    return this.get().getByRole("button", { name: ConstraintType.EXPRESSION, exact: true });
  }

  public getRangeConstraintButton() {
    return this.get().getByRole("button", { name: ConstraintType.RANGE, exact: true });
  }

  public async createFirstCustonDataType() {
    await this.get().getByRole("button", { name: "Create a custom data type" }).click();
  }

  public async createNewDataType() {
    await this.get().getByLabel("Add Data Type").click();
  }

  public async changeDataTypeName(args: { newName: string }) {
    await this.get().getByPlaceholder("Enter a name...").fill(args.newName);
  }

  public async changeDataTypeBaseType(args: { newBaseType: DataType }) {
    await this.get().getByPlaceholder("Select a data type...").click();
    await this.get().getByPlaceholder("Select a data type...").press("ControlOrMeta+a");
    await this.get().getByPlaceholder("Select a data type...").fill(args.newBaseType);
    await this.page.getByRole("option", { name: args.newBaseType, exact: true }).click();
  }

  public async changeDataTypeBaseCustomType(args: { newBaseType: string }) {
    await this.get().getByPlaceholder("Select a data type...").click();
    await this.get().getByPlaceholder("Select a data type...").press("ControlOrMeta+a");
    await this.get().getByPlaceholder("Select a data type...").fill(args.newBaseType);
    await this.page.getByRole("option", { name: `${args.newBaseType} `, exact: false }).click();
  }

  // TODO: Add other types of values, date, date-time, etc
  public async addEnumerationConstraint(args: { values: string[] }) {
    await this.getEnumerationConstraintButton().click();

    for (let index = 0; index < args.values.length; index++) {
      await this.get().locator(`#enum-element-${index}`).fill(args.values[index]);
      if (index !== args.values.length - 1) {
        await this.get().getByRole("button", { name: "Add value" }).click();
      }
    }
  }

  public async addExpressionConstraint(args: { value: string }) {
    await this.getExpressionConstraintButton().click();
    await this.get().getByLabel("Editor content;Press Alt+F1").fill(args.value);
  }

  // TODO: Add other types of values, date, date-time, etc
  public async addRangeConstraint(args: { values: [string, string] }) {
    await this.getRangeConstraintButton().click();
    await this.get().locator("#start-value").fill(args.values[0]);
    await this.get().locator("#end-value").click();
    await this.get().locator("#end-value").fill(args.values[1]);
  }

  public async deleteConstraint() {
    await this.getNoneConstraintButton().click();
  }
}
