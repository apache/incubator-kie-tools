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
import { AddColumnPosition } from "../../__fixtures__/scesimEditor";

test.describe("Background Table context menu", () => {
  test.describe("Context menu checks", () => {
    test.beforeEach(async ({ stories, page, monaco, scesimEditor }) => {
      await stories.openBackgroundTableDecision();
      await scesimEditor.addPropertyColumn({
        targetCell: "PROPERTY (<Undefined>)",
        position: AddColumnPosition.RIGHT,
        nth: 0,
      });
      await monaco.fillBackgroundTableCell({ content: "test", column: 1 });
      await page.keyboard.press("Escape");
    });

    test("should render select context menu", async ({ page }) => {
      await page.getByRole("cell", { name: "test test" }).getByTestId("monaco-container").click({ button: "right" });
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "SCENARIO", exact: true })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "FIELD" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "INSTANCE" })).not.toBeAttached();
    });

    test("should render field context menu", async ({ page }) => {
      await page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).nth(1).click({ button: "right" });
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
    test("should add and delete instance column left", async ({ page, scesimEditor }) => {
      await expect(page.getByRole("columnheader", { name: "INSTANCE-3 (<Undefined>)" })).not.toBeAttached();
      await scesimEditor.addInstanceColumn({
        targetCell: "INSTANCE-1 (<Undefined>)",
        position: AddColumnPosition.LEFT,
      });
      await expect(page.getByRole("columnheader", { name: "INSTANCE-3 (<Undefined>)" })).toBeAttached();
      await expect(page.getByLabel("Background")).toHaveScreenshot("background-table-add-instance-column-left.png");
      await page.getByRole("columnheader", { name: "INSTANCE-3 (<Undefined>)" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete Instance" }).click();
      await expect(page.getByRole("columnheader", { name: "INSTANCE-3 (<Undefined>)" })).not.toBeAttached();
    });
    test("should add and delete instance column right", async ({ page, scesimEditor }) => {
      await expect(page.getByRole("columnheader", { name: "INSTANCE-3 (<Undefined>)" })).not.toBeAttached();
      await scesimEditor.addInstanceColumn({
        targetCell: "INSTANCE-1 (<Undefined>)",
        position: AddColumnPosition.RIGHT,
      });
      await expect(page.getByRole("columnheader", { name: "INSTANCE-3 (<Undefined>)" })).toBeAttached();
      await expect(page.getByLabel("Background")).toHaveScreenshot("background-table-add-instance-column-right.png");
      await page.getByRole("columnheader", { name: "INSTANCE-3 (<Undefined>)" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete Instance" }).click();
      await expect(page.getByRole("columnheader", { name: "INSTANCE-3 (<Undefined>)" })).not.toBeAttached();
    });
    test("should add and delete property column left", async ({ page, scesimEditor }) => {
      await expect(page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).nth(2)).not.toBeAttached();
      await scesimEditor.addPropertyColumn({
        targetCell: "PROPERTY (<Undefined>)",
        position: AddColumnPosition.LEFT,
        nth: 1,
      });
      await expect(page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).nth(2)).toBeAttached();
      await expect(page.getByLabel("Background")).toHaveScreenshot("background-table-add-property-column-left.png");
      await page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).nth(1).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete Field" }).click();
      await expect(page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).nth(2)).not.toBeAttached();
    });
    test("should add and delete property column right", async ({ page, scesimEditor }) => {
      await expect(page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).nth(2)).not.toBeAttached();
      await scesimEditor.addPropertyColumn({
        targetCell: "PROPERTY (<Undefined>)",
        position: AddColumnPosition.RIGHT,
        nth: 1,
      });
      await expect(page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).nth(2)).toBeAttached();
      await expect(page.getByLabel("Background")).toHaveScreenshot("background-table-add-property-column-right.png");
      await page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).nth(1).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete Field" }).click();
      await expect(page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).nth(2)).not.toBeAttached();
    });
  });
});
