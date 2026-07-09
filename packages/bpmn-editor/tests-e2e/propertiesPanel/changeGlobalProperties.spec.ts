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

import { test, expect } from "../__fixtures__/base";

test.beforeEach(async ({ editor, page }) => {
  await page.setViewportSize({ width: 1920, height: 1080 });
  await editor.open();
  await editor.setInitialProcessId();
});

test.describe("Change Global Properties - Process Duration", () => {
  test.beforeEach(async ({ page }) => {
    await page.getByRole("button", { name: "Properties Management" }).click();
    await expect(page.getByText("Process", { exact: true })).toBeVisible();
  });

  test("should set processDuration value", async ({ page }) => {
    const processDurationInput = page.getByPlaceholder(/E.g., PT1M \(1 minute\), PT1H \(1 hour\), P1D \(1 day\)/i);
    await processDurationInput.fill("PT30M");

    await expect(processDurationInput).toHaveValue("PT30M");
  });

  test("should update processDuration value", async ({ page }) => {
    const processDurationInput = page.getByPlaceholder(/E.g., PT1M \(1 minute\), PT1H \(1 hour\), P1D \(1 day\)/i);

    await processDurationInput.fill("PT1H");
    await expect(processDurationInput).toHaveValue("PT1H");

    await processDurationInput.fill("P1D");
    await expect(processDurationInput).toHaveValue("P1D");
  });

  test("should clear processDuration value", async ({ page }) => {
    const processDurationInput = page.getByPlaceholder(/E.g., PT1M \(1 minute\), PT1H \(1 hour\), P1D \(1 day\)/i);

    await processDurationInput.fill("PT2H");
    await expect(processDurationInput).toHaveValue("PT2H");

    await processDurationInput.clear();
    await expect(processDurationInput).toHaveValue("");
  });

  test("should accept various ISO 8601 duration formats", async ({ page }) => {
    const processDurationInput = page.getByPlaceholder(/E.g., PT1M \(1 minute\), PT1H \(1 hour\), P1D \(1 day\)/i);

    const testValues = ["PT1M", "PT30M", "PT1H", "PT2H30M", "P1D", "P1DT12H", "P7D"];

    for (const duration of testValues) {
      await processDurationInput.fill(duration);
      await expect(processDurationInput).toHaveValue(duration);
    }
  });

  test("should persist processDuration with other process properties", async ({ page }) => {
    const nameInput = page.getByPlaceholder(/Enter a name.../i);
    await nameInput.fill("Order Processing");

    const processDurationInput = page.getByPlaceholder(/E.g., PT1M \(1 minute\), PT1H \(1 hour\), P1D \(1 day\)/i);
    await processDurationInput.fill("PT45M");

    await expect(nameInput).toHaveValue("Order Processing");
    await expect(processDurationInput).toHaveValue("PT45M");
  });
});
