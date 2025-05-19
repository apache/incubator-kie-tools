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
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";

import { expect, Locator, Page } from "@playwright/test";
import test from "node:test";

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

export enum DefaultDataTypeName {
  Undefined = "New Data type - <Undefined>",
  Any = "New Data type - Any",
  Boolean = "New Data type - boolean",
  Context = "New Data type - context",
  Date = "New Data type - date",
  DateTime = "New Data type - date and time",
  DateTimeDuration = "New Data type - days and time duration",
  Number = "New Data type - number",
  String = "New Data type - string",
  Time = "New Data type - time",
  YearsMonthsDuration = "New Data type - years and months duration",
}

export const DMN15_SPEC_TYPE_LANGUAGE = DMN15_SPEC.typeLanguage.default;

export class DataTypes {
  constructor(
    public page: Page,
    public monaco: Monaco
  ) {}

  public get() {
    return this.page.getByTestId("kie-tools--dmn-editor--data-types-container");
  }

  public resetFocus() {
    return this.get().click({ position: { x: 0, y: 0 } });
  }

  public getDataType(args: { name: string }) {
    return this.page.getByTestId("kie-tools--dmn-editor--data-types-list").getByText(args.name, { exact: true });
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

  public enableDataTypeStruct() {
    this.get().locator("span", { hasText: "Is struct?" }).last().click();
  }

  public async addDataTypeStructProperty(args: { name: string }) {
    await this.get().getByTitle("Add item component (at the top)").click();
    await this.changeDataTypePropertiesTable({ name: args.name });
  }

  public changeDataTypePropertiesTable(args: { name: string }) {
    return this.get().getByRole("table").getByPlaceholder("Enter a name...").first().fill(args.name);
  }

  public async createFirstCustonDataType() {
    await this.get().getByRole("button", { name: "Create a custom data type" }).click();
  }

  public async pasteFirstDataType() {
    await this.get().getByRole("button", { name: "Paste data type" }).click();
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

  public async addDataTypeDescription(args: { newDescription: string }) {
    await this.get().getByPlaceholder("Enter a description...").fill(args.newDescription);
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

  // The current method only supports "string", "number"
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

  // The current method only supports "string", "number"
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

  private isValidDate(value: string): boolean {
    return /^\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\d|3[01])$/.test(value);
  }

  private isValidTime(value: string): boolean {
    return /^\d{2}:\d{2}:\d{2}$/.test(value);
  }

  private isValidTimezone(value: string): boolean {
    return /^[+-]\d{2}:\d{2}$/.test(value);
  }

  private isValidNumber(value: string): boolean {
    return /^-?\d+$/.test(value);
  }

  private async fillDateFields(container: Locator, date: string) {
    const [year, month, day] = date.split("-");
    if (!this.isValidDate(date)) throw new Error(`Invalid date format: ${date}`);

    await container.getByLabel("Toggle date picker").click();
    await container.locator('input[type="number"]').fill(year);
    await container.locator('button[aria-expanded="false"]').first().click();
    await container.getByRole("option", { name: this.getMonthName(month) }).click();
    await container.getByRole("button", { name: `${day} ${this.getMonthName(month)} ${year}`, exact: true }).click();
  }

  private async fillTimeField(container: Locator, time: string) {
    if (!this.isValidTime(time)) throw new Error(`Invalid time format: ${time}`);
    await container.getByLabel("Time picker").fill(time);
  }

  private async fillTimezoneField(container: Locator, timezone: string) {
    if (!this.isValidTimezone(timezone)) throw new Error(`Invalid timezone format: ${timezone}`);
    await container.getByRole("button", { name: "Options menu" }).click();
    await this.get().getByRole("option", { name: timezone }).click();
    await this.resetFocus();
  }

  private async addValueIfLastElementFilled(container: Locator, checkSelector: string, label: string) {
    const lastElementValue = await container
      .getByRole("listitem")
      .last()
      .getByLabel(label, { exact: true })
      .getAttribute("value");
    if (lastElementValue !== "" || lastElementValue == null) {
      await this.get().getByRole("button", { name: "Add value" }).click();
    }
  }

  public async addEnumerationConstraintDate(args: { values: string[] }) {
    const enumerationList = this.get().getByTestId("kie-tools--dmn-editor--enumeration-constraint-list");
    const firstElementValue = await enumerationList
      .getByTestId("kie-tools--dmn-editor--draggable-row-0")
      .getByLabel("Date picker", { exact: true })
      .getAttribute("value");

    const append = firstElementValue !== "" && firstElementValue !== null;
    const startIndex = append ? await enumerationList.getByRole("listitem").count() : 0;

    for (let i = 0; i < args.values.length; i++) {
      if (i !== 0 || append) {
        await this.get().getByRole("button", { name: "Add value" }).click();
      }
      const row = enumerationList.getByTestId(`kie-tools--dmn-editor--draggable-row-${startIndex + i}`);
      await this.fillDateFields(row, args.values[i]);
    }
  }

  public async addRangeConstraintDate(args: { values: [string, string]; includeStart: boolean; includeEnd: boolean }) {
    await this.fillDateFields(
      this.get().getByTestId(`kie-tools--dmn-editor--range-constraint-start-value`),
      args.values[0]
    );
    await this.fillDateFields(
      this.get().getByTestId(`kie-tools--dmn-editor--range-constraint-end-value`),
      args.values[1]
    );
    if (!args.includeStart) await this.get().locator('button[id="start"]').click();
    if (args.includeEnd) await this.get().locator('button[id="end"]').click();
  }

  public async addEnumerationConstraintDateTime(args: { date: string; time: string; timezone: string }) {
    const enumList = this.get().getByTestId("kie-tools--dmn-editor--enumeration-constraint-list");
    await this.addValueIfLastElementFilled(enumList, "Date picker", "Date picker");

    const row = enumList.getByRole("listitem").last();
    await this.fillDateFields(row, args.date);
    await this.fillTimeField(row, args.time);
    await this.fillTimezoneField(row, args.timezone);
  }

  public async addRangeConstraintDateTime(args: {
    date: string;
    time: string;
    timezone: string;
    range: string;
    includeDate: boolean;
  }) {
    const rangeContainer = this.get().getByTestId(`kie-tools--dmn-editor--range-constraint-${args.range}-value`);
    await this.fillDateFields(rangeContainer, args.date);
    await this.fillTimeField(rangeContainer, args.time);
    await this.fillTimezoneField(rangeContainer, args.timezone);
    if ((args.includeDate && args.range === "end") || (!args.includeDate && args.range === "start")) {
      await this.get().locator(`button[id="${args.range}"]`).click();
    }
  }

  public async addEnumerationConstraintDateTimeDuration(args: {
    days: string;
    hrs: string;
    mins: string;
    sec: string;
  }) {
    const { days, hrs, mins, sec } = args;
    if (![days, hrs, mins, sec].every(this.isValidNumber)) {
      throw new Error(`Invalid DateTimeDuration format: ${JSON.stringify(args)}`);
    }

    const enumList = this.get().getByTestId("kie-tools--dmn-editor--enumeration-constraint-list");
    const last = enumList.getByRole("listitem").last();
    const lastElementValue = await last.getByPlaceholder("Days").getAttribute("value");

    if (lastElementValue !== "" || lastElementValue == null) {
      await this.get().getByRole("button", { name: "Add value" }).click();
    }

    await last.getByPlaceholder("Days").fill(days);
    await last.getByPlaceholder("Hours").fill(hrs);
    await last.getByPlaceholder("Minutes").fill(mins);
    await last.getByPlaceholder("Seconds").fill(sec);
  }

  public async addRangeConstraintDateTimeDuration(args: {
    days: string;
    hrs: string;
    mins: string;
    sec: string;
    range: string;
    includeDate: boolean;
  }) {
    const { days, hrs, mins, sec, range } = args;
    if (![days, hrs, mins, sec].every(this.isValidNumber)) {
      throw new Error(`Invalid DateTimeDuration format: ${JSON.stringify(args)}`);
    }

    const rangeContainer = this.get().getByTestId(`kie-tools--dmn-editor--range-constraint-${range}-value`);
    await rangeContainer.getByPlaceholder("Days").fill(days);
    await rangeContainer.getByPlaceholder("Hours").fill(hrs);
    await rangeContainer.getByPlaceholder("Minutes").fill(mins);
    await rangeContainer.getByPlaceholder("Seconds").fill(sec);

    if ((args.includeDate && args.range === "end") || (!args.includeDate && args.range === "start")) {
      await this.get().locator(`button[id="${args.range}"]`).click();
    }
  }

  public async addEnumerationConstraintTime(args: { time: string; timezone: string }) {
    const enumList = this.get().getByTestId("kie-tools--dmn-editor--enumeration-constraint-list");
    await this.addValueIfLastElementFilled(enumList, "Time picker", "Time picker");

    const row = enumList.getByRole("listitem").last();
    await this.fillTimeField(row, args.time);
    await this.fillTimezoneField(row, args.timezone);
  }

  public async addRangeConstraintTime(args: { time: string; timezone: string; range: string; includeDate: boolean }) {
    const container = this.get().getByTestId(`kie-tools--dmn-editor--range-constraint-${args.range}-value`);
    await this.fillTimeField(container, args.time);
    await this.fillTimezoneField(container, args.timezone);
    if ((args.includeDate && args.range === "end") || (!args.includeDate && args.range === "start")) {
      await this.get().locator(`button[id="${args.range}"]`).click();
    }
  }

  public async addEnumerationConstraintYearsMonthsDuration(args: { year: string; month: string }) {
    if (![args.year, args.month].every(this.isValidNumber)) {
      throw new Error(`Invalid YearsMonthsDuration format: ${JSON.stringify(args)}`);
    }
    const enumList = this.get().getByTestId("kie-tools--dmn-editor--enumeration-constraint-list");
    const last = enumList.getByRole("listitem").last();
    const lastElementValue = await last.getByPlaceholder("Years").getAttribute("value");

    if (lastElementValue !== "" || lastElementValue == null) {
      await this.get().getByRole("button", { name: "Add value" }).click();
    }

    await last.getByPlaceholder("Years").fill(args.year);
    await last.getByPlaceholder("Months").fill(args.month);
  }

  public async addRangeConstraintYearsMonthsDuration(args: {
    year: string;
    month: string;
    range: string;
    includeDate: boolean;
  }) {
    if (![args.year, args.month].every(this.isValidNumber)) {
      throw new Error(`Invalid YearsMonthsDuration format: ${JSON.stringify(args)}`);
    }
    const rangeContainer = this.get().getByTestId(`kie-tools--dmn-editor--range-constraint-${args.range}-value`);
    await rangeContainer.getByPlaceholder("Years").fill(args.year);
    await rangeContainer.getByPlaceholder("Months").fill(args.month);
    if ((args.includeDate && args.range === "end") || (!args.includeDate && args.range === "start")) {
      await this.get().locator(`button[id="${args.range}"]`).click();
    }
  }

  private getMonthName(month: string): string {
    const months = [
      "January",
      "February",
      "March",
      "April",
      "May",
      "June",
      "July",
      "August",
      "September",
      "October",
      "November",
      "December",
    ];
    const index = Number(month) - 1;
    if (index < 0 || index > 11) {
      throw new Error(`Invalid month: ${month}`);
    }
    return months[index];
  }
}
