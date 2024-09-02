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

import { expect, test } from "../__fixtures__/base";
import { TabName } from "../__fixtures__/editor";

test.beforeEach(async ({ editor, nodes }) => {
  await editor.openLoanPreQualification();
  await expect(nodes.get({ name: "Loan Pre-Qualification" })).toBeVisible();
  await editor.setIsReadOnly(true);
  await editor.changeTab({ tab: TabName.DATA_TYPES });
});

test.describe("Read Only mode - Data Types", () => {
  test("inputs should be disabled on data types table", async ({ page }) => {
    await page.getByText("Applicant_Data").click();
    await expect(page).toHaveScreenshot("readOnly-dataTypes-inputs-disabled.png");

    await page.getByLabel("Actions").first().click();
    await expect(page.getByRole("menu").getByText("Remove")).not.toBeAttached();
    await expect(page.getByRole("menu").getByText("Copy")).toBeAttached();
    await page.getByLabel("Actions").first().click();

    const dataTypeNameInputs = await page.getByPlaceholder("Enter a name...").all();

    for (let input of dataTypeNameInputs) {
      await expect(input).toBeDisabled();
    }

    await expect(await page.getByPlaceholder("Enter a description...")).toBeDisabled();

    const checkboxInputs = await page.getByRole("checkbox").all();
    for (let input of checkboxInputs) {
      await expect(input).toBeDisabled();
    }

    await page.getByRole("table").getByLabel("Actions").first().click();

    await expect(page.getByRole("menu").getByText("Extract data type")).not.toBeAttached();
    await expect(page.getByRole("menu").getByText("Cut")).not.toBeAttached();
    await expect(page.getByRole("menu").getByText("Remove")).not.toBeAttached();
    await expect(page.getByRole("menu").getByText("View")).toBeAttached();
    await expect(page.getByRole("menu").getByText("Copy")).toBeAttached();

    await expect(page.getByRole("button", { name: "Add property to 'Applicant_Data'" })).not.toBeAttached();
    await expect(page.getByTitle("Add item component").first()).not.toBeAttached();
  });
  test("inputs should be disabled on constraints table and form", async ({ page, dataTypes }) => {
    await page.getByText("Applicant_Data").click();
    await dataTypes.getEnumerationConstraintButton().click();

    const constraintsNameInputs = await page.getByPlaceholder("Enter a name...").all();

    for (let input of constraintsNameInputs) {
      await expect(input).toBeDisabled();
    }

    await expect(await page.getByPlaceholder("Enter a description...")).toBeDisabled();

    const checkboxInputs = await page.getByRole("checkbox").all();
    for (let input of checkboxInputs) {
      await expect(input).toBeDisabled();
    }

    await expect(await dataTypes.getNoneConstraintButton()).toBeDisabled();
    await expect(await dataTypes.getEnumerationConstraintButton()).toBeDisabled();
    await expect(await dataTypes.getExpressionConstraintButton()).toBeDisabled();
    await expect(await dataTypes.getRangeConstraintButton()).toBeDisabled();
  });
});
