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

import { Monaco } from "@kie-tools/boxed-expression-component/tests-e2e/__fixtures__/monaco";
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

export enum RangeConstraintPosition {
  START = "start",
  END = "end",
}

export class DataTypes {
  constructor(
    public page: Page,
    public monaco: Monaco
  ) {}

  public get() {
    return this.page.getByTestId("kie-tools--dmn-editor--data-types-container");
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

  public async selectDataType(args: { name: string }) {
    await this.get().getByTestId("kie-tools--dmn-editor--data-types-list").getByText(args.name).click();
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
    const enumerationList = this.get().getByTestId("kie-tools--dmn-editor--enumeration-constraint-list");
    const firstElementValue = await enumerationList
      .getByTestId("kie-tools--dmn-editor--draggable-row-0")
      .locator("input")
      .getAttribute("value");
    let append = false;
    if (firstElementValue !== "" || firstElementValue === null) {
      append = true;
    }

    if (append) {
      // add at the end of a list;
      const listCount = await enumerationList.getByRole("listitem").count();
      await this.get().getByRole("button", { name: "Add value" }).click();
      await this.addEnumerationValue({
        values: args.values,
        initial: listCount,
        total: args.values.length + listCount,
      });
    } else {
      // initialize a new list of values;
      await this.addEnumerationValue({ values: args.values, initial: 0, total: args.values.length });
    }
  }

  private async addEnumerationValue(args: { values: string[]; initial: number; total: number }) {
    let valueIndex = 0;
    for (let index = args.initial; index < args.total; index++) {
      await this.get()
        .getByTestId(`kie-tools--dmn-editor--draggable-row-${index}`)
        .locator("input")
        .fill(args.values[valueIndex]);
      if (index !== args.total - 1) {
        await this.get().getByRole("button", { name: "Add value" }).click();
      }
      valueIndex++;
    }
  }

  public async addExpressionConstraint(args: { value: string }) {
    await this.monaco.fill({ monacoParentLocator: this.get(), content: args.value });
  }

  // TODO: Add other types of values, date, date-time, etc
  public async addRangeConstraint(args: { values: [string, string] }) {
    await this.changeRangeStartConstraint(args.values[0]);
    await this.get().getByTestId("kie-tools--dmn-editor--range-constraint-end-value").click();
    await this.changeRangeEndConstraint(args.values[1]);
  }

  public async changeRangeStartConstraint(value: string) {
    await this.get().getByTestId("kie-tools--dmn-editor--range-constraint-start-value").locator("input").fill(value);
  }

  public async changeRangeEndConstraint(value: string) {
    await this.get().getByTestId("kie-tools--dmn-editor--range-constraint-end-value").locator("input").fill(value);
  }

  public async deleteConstraint() {
    await this.getNoneConstraintButton().click();
  }
}
