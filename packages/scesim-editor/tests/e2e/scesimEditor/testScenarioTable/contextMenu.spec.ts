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

test.describe("Test Scenario Table context menu", () => {
  test.describe("Context menu checks", () => {
    test.beforeEach(async ({ stories, page, monaco, scesimEditor }) => {
      await stories.openTestScenarioTableRule();
      await page.getByRole("cell", { name: "1" }).click();
      await scesimEditor.addRows(1);
      await monaco.fill({ monacoParentLocator: page, content: "test", nth: 1 });
      await page.keyboard.press("Escape");
    });

    test("should render select context menu", async ({ page }) => {
      await page.getByRole("cell", { name: "test test" }).getByTestId("monaco-container").click({ button: "right" });
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "SCENARIO", exact: true })).toBeAttached();
      await expect(page.getByRole("heading", { name: "FIELD" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "INSTANCE" })).not.toBeAttached();
    });

    test("should render field context menu", async ({ page }) => {
      await page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).first().click({ button: "right" });
      await expect(page.getByRole("heading", { name: "SELECTION" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "SCENARIO", exact: true })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "FIELD" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "INSTANCE" })).not.toBeAttached();
    });

    test("should render instance context menu", async ({ page }) => {
      await page.getByRole("columnheader", { name: "INSTANCE-1 (<Undefined>)" }).click({ button: "right" });
      await expect(page.getByRole("heading", { name: "SELECTION" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "SCENARIO", exact: true })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "FIELD" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "INSTANCE" })).toBeAttached();
    });
  });
});
