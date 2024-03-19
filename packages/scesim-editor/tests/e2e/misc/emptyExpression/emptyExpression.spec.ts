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

import { test, expect } from "../../__fixtures__/base";

test.describe("Empty Editor", () => {
  test("should render editor correctly", async ({ editor, page }) => {
    await editor.openSelectionPage();
    await expect(
      page.getByText("Create a new Test ScenarioAsset type * Select a typeDecision (DMN)Rule (DRL)Skip")
    ).toHaveScreenshot("create-a-new-test-scenario.png");
    await page.locator("#asset-type-select").selectOption("DMN");
    await page.getByRole("button", { name: "Create" }).click();
    await page.getByLabel("Close drawer panel").click();
    await expect(page.getByLabel("Test Scenario")).toHaveScreenshot("empty-test-scenario-table.png");
    await page.getByRole("tab", { name: "Background" }).click();
    await expect(page.getByLabel("Background")).toHaveScreenshot("empty-background-table.png");
  });
});
