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
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.describe("Decision table context menu", () => {
  test.describe("Decision rules control", () => {
    test.beforeEach(async ({ stories, page, monaco }) => {
      await stories.openDecisionTable();
      await monaco.fill({ monacoParentLocator: page, content: '"test"', nth: 0 });
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
    });

    test("shouldn't render columns context menu", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/420",
      });

      await expect(page.getByRole("heading", { name: "DECISION RULES" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "INPUT COLUMNS" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "OUTPUT COLUMNS" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "RULE ANNOTATION" })).not.toBeAttached();
    });

    test("should open decision rules context menu and insert row above", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
    });

    test("should open decision rules context menu and insert row below", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Insert below" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      await expect(page.getByRole("row", { name: "2" })).toBeAttached();
    });

    test("should open decision rules context menu and insert multiples rows above", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "plus" }).click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "4" })).toContainText("test");
    });

    test("should open decision rules context menu and insert multiples rows below", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "minus" }).click();
      await page.getByLabel("Below").click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "2" })).toBeAttached();
    });

    test("should open decision rules context menu and delete row", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
    });

    test("should open decision rules context menu and duplicate row", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Duplicate" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
    });
  });

  test.describe("Columns controls", () => {
    test.describe("Input columns", () => {
      test.beforeEach(async ({ stories, page, monaco }) => {
        await stories.openDecisionTable();
        await monaco.fill({ monacoParentLocator: page, content: '"test"', nth: 0 });
        await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).click({
          button: "right",
        });
      });

      test("shouldn't render decision rules context menu", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/420",
        });

        await expect(page.getByRole("heading", { name: "DECISION RULES" })).not.toBeAttached();
        await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
        await expect(page.getByRole("heading", { name: "INPUT COLUMNS" })).toBeAttached();
        await expect(page.getByRole("heading", { name: "OUTPUT COLUMNS" })).not.toBeAttached();
        await expect(page.getByRole("heading", { name: "RULE ANNOTATION" })).not.toBeAttached();
      });

      test("should open input column context menu and insert column right", async ({ page }) => {
        await page.getByRole("menuitem", { name: "Insert right" }).click();
        await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "input-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell").nth(1)).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open input column context menu and insert column left", async ({ page }) => {
        await page.getByRole("menuitem", { name: "Insert left" }).click();
        await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "input-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell").nth(2)).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open input column context menu and insert multiples columns on right", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

        await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
        await page.getByRole("button", { name: "plus" }).click();
        await page.getByRole("button", { name: "Insert" }).click();
        await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "input-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "input-3 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "input-4 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell")).toHaveCount(7);
      });

      test("should open input column context menu and insert multiples columns on left", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

        await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
        await page.getByRole("button", { name: "minus" }).click();
        await page.getByLabel("Left").click();
        await page.getByRole("button", { name: "Insert" }).click();
        await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "input-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open input column context menu and delete column", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

        await page.getByRole("menuitem", { name: "Insert left" }).click();
        await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "input-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell").nth(2)).toContainText("test");
        await page.getByTestId("expression-container").getByText("input-1").click({
          button: "right",
        });
        await page.getByRole("menuitem", { name: "Delete" }).click();
        await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).not.toBeAttached();
        await expect(page.getByRole("columnheader", { name: "input-2 (<Undefined>)" })).not.toBeAttached();
        await expect(page.getByRole("cell")).toHaveCount(5);
      });
    });

    test.describe("Output columns", () => {
      test.beforeEach(async ({ stories, page, monaco }) => {
        await stories.openDecisionTable();
        await monaco.fill({ monacoParentLocator: page, content: '"test"', nth: 1 });
        await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click({
          button: "right",
        });
      });

      test("shouldn't render decision rules context menu", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/420",
        });

        await expect(page.getByRole("heading", { name: "DECISION RULES" })).not.toBeAttached();
        await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
        await expect(page.getByRole("heading", { name: "INPUT COLUMNS" })).not.toBeAttached();
        await expect(page.getByRole("heading", { name: "OUTPUT COLUMNS" })).toBeAttached();
        await expect(page.getByRole("heading", { name: "RULE ANNOTATION" })).not.toBeAttached();
      });

      test("should open output column context menu and insert column right", async ({ page }) => {
        await page.getByRole("menuitem", { name: "Insert right" }).click();
        await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell").nth(2)).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open output column context menu and insert column left", async ({ page }) => {
        await page.getByRole("menuitem", { name: "Insert left" }).click();
        await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell").nth(3)).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open output column context menu and insert multiples columns on right", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

        await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
        await page.getByRole("button", { name: "plus" }).click();
        await page.getByRole("button", { name: "Insert" }).click();
        await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-3 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-4 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell")).toHaveCount(7);
      });

      test("should open output column context menu and insert multiples columns on left", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

        await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
        await page.getByRole("button", { name: "minus" }).click();
        await page.getByLabel("Left").click();
        await page.getByRole("button", { name: "Insert" }).click();
        await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open output column context menu and delete column", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

        await page.getByRole("menuitem", { name: "Insert left" }).click();
        await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-1 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell").nth(3)).toContainText("test");
        await page.getByTestId("expression-container").getByText("output-1").click({
          button: "right",
        });
        await page.getByRole("menuitem", { name: "Delete" }).click();
        await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-1 (<Undefined>)" })).not.toBeAttached();
        await expect(page.getByRole("columnheader", { name: "output-2 (<Undefined>)" })).not.toBeAttached();
        await expect(page.getByRole("cell")).toHaveCount(5);
      });
    });

    test.describe("Rule annotation columns", () => {
      test.beforeEach(async ({ stories, page, monaco }) => {
        await stories.openDecisionTable();
        await monaco.fill({ monacoParentLocator: page, content: '"test"', nth: 2 });
        await page.getByRole("columnheader", { name: "Annotations", exact: true }).click({
          button: "right",
        });
      });

      test("shouldn't render decision rules context menu", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/420",
        });

        await expect(page.getByRole("heading", { name: "DECISION RULES" })).not.toBeAttached();
        await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
        await expect(page.getByRole("heading", { name: "INPUT COLUMNS" })).not.toBeAttached();
        await expect(page.getByRole("heading", { name: "OUTPUT COLUMNS" })).not.toBeAttached();
        await expect(page.getByRole("heading", { name: "RULE ANNOTATION" })).toBeAttached();
      });

      test("should open annotation column context menu and insert column right", async ({ page }) => {
        await page.getByRole("menuitem", { name: "Insert right" }).click();
        await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "Annotations-2", exact: true })).toBeAttached();
        await expect(page.getByRole("cell").nth(3)).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open annotation column context menu and insert column left", async ({ page }) => {
        await page.getByRole("menuitem", { name: "Insert left" }).click();
        await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "Annotations-2", exact: true })).toBeAttached();
        await expect(page.getByRole("cell").nth(4)).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open annotation column context menu and insert multiples columns on right", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

        await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
        await page.getByRole("button", { name: "plus" }).click();
        await page.getByRole("button", { name: "Insert" }).click();
        await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "Annotations-2", exact: true })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "Annotations-3", exact: true })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "Annotations-4", exact: true })).toBeAttached();
        await expect(page.getByRole("cell")).toHaveCount(7);
      });

      test("should open annotation column context menu and insert multiples columns on left", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

        await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
        await page.getByRole("button", { name: "minus" }).click();
        await page.getByLabel("Left").click();
        await page.getByRole("button", { name: "Insert" }).click();
        await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "Annotations-2", exact: true })).toBeAttached();
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open annotation column context menu and delete column", async ({ page }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");

        await page.getByRole("menuitem", { name: "Insert left" }).click();
        await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).toBeAttached();
        await expect(page.getByRole("columnheader", { name: "Annotations-2", exact: true })).toBeAttached();
        await expect(page.getByRole("cell").nth(4)).toContainText("test");
        await page.getByTestId("expression-container").getByText("Annotations").click({
          button: "right",
        });
        await page.getByRole("menuitem", { name: "Delete" }).click();
        await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).not.toBeAttached();
        await expect(page.getByRole("columnheader", { name: "Annotations-2", exact: true })).not.toBeAttached();
        await expect(page.getByRole("cell")).toHaveCount(5);
      });
    });
  });

  test("should reset insert multiples menu when opening another cell context menu", async ({
    stories,
    page,
    monaco,
  }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/421");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/421",
    });

    await stories.openDecisionTable();
    await monaco.fill({ monacoParentLocator: page, content: '"test"' });
    await page.getByTestId("monaco-container").click({ button: "right" });
    await page.getByRole("menuitem", { name: "Insert", exact: true }).first().click();
    await page.getByRole("cell", { name: "1" }).click({ button: "right" });
    await expect(page.getByRole("heading", { name: "DECISION RULES" })).toBeAttached();
    await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
  });

  test.describe("Hovering", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openDecisionTable();
    });

    test.describe("Add decision rules", () => {
      test.beforeEach(async ({ page, monaco }) => {
        await monaco.fill({ monacoParentLocator: page, content: '"test"', nth: 0 });
      });

      test("should add row above by positioning mouse on the index cell upper section", async ({ page }) => {
        await page.getByRole("cell", { name: "1" }).hover({ position: { x: 0, y: 0 } });
        await page.getByRole("cell", { name: "1" }).locator("svg").click();
        await expect(page.getByRole("row", { name: "2" })).toContainText("test");
      });

      test("should add row below by positioning mouse on the index cell lower section", async ({ page }) => {
        await page.getByRole("cell", { name: "1" }).hover();
        await page.getByRole("cell", { name: "1" }).locator("svg").click();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
        await expect(page.getByRole("row", { name: "2" })).toBeAttached();
      });
    });

    test.describe("Add columns", () => {
      test.describe("Input columns", () => {
        test.beforeEach(async ({ page, monaco }) => {
          await monaco.fill({ monacoParentLocator: page, content: '"test"', nth: 0 });
        });

        test("should add column left by positioning mouse on the header cell left section", async ({ page }) => {
          await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).hover({ position: { x: 0, y: 0 } });
          await page.getByRole("row", { name: "input-1 (<Undefined>)" }).locator("svg").click();
          await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("columnheader", { name: "input-2 (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("cell").nth(2)).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });

        test("should add column right by positioning mouse on the header cell right section", async ({ page }) => {
          await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).hover();
          await page.getByRole("row", { name: "input-1 (<Undefined>)" }).locator("svg").click();
          await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("columnheader", { name: "input-2 (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("cell").nth(1)).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });
      });

      test.describe("Output columns", () => {
        test.beforeEach(async ({ page, monaco }) => {
          await monaco.fill({ monacoParentLocator: page, content: '"test"', nth: 1 });
        });

        test("should add column left by positioning mouse on the header cell left section", async ({ page }) => {
          await page
            .getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
            .hover({ position: { x: 0, y: 0 } });
          await page.getByRole("row", { name: "Expression Name (<Undefined>)" }).locator("svg").click();
          await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("columnheader", { name: "output-1 (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("columnheader", { name: "output-2 (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("cell").nth(3)).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });

        test("should add column right by positioning mouse on the header cell right section", async ({ page }) => {
          await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).hover();
          await page.getByRole("row", { name: "Expression Name (<Undefined>)" }).locator("svg").click();
          await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("columnheader", { name: "output-1 (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("columnheader", { name: "output-2 (<Undefined>)" })).toBeAttached();
          await expect(page.getByRole("cell").nth(2)).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });
      });

      test.describe("Rule annotation columns", () => {
        test.beforeEach(async ({ page, monaco }) => {
          await monaco.fill({ monacoParentLocator: page, content: '"test"', nth: 2 });
        });

        test("should add column left by positioning mouse on the header cell left section", async ({ page }) => {
          await page
            .getByRole("columnheader", { name: "Annotations", exact: true })
            .hover({ position: { x: 0, y: 0 } });
          await page.getByRole("columnheader", { name: "Annotations", exact: true }).locator("svg").click();
          await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).toBeAttached();
          await expect(page.getByRole("columnheader", { name: "Annotations-2", exact: true })).toBeAttached();
          await expect(page.getByRole("cell").nth(4)).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });

        test("should add column right by positioning mouse on the header cell right section", async ({ page }) => {
          await page.getByRole("columnheader", { name: "Annotations", exact: true }).hover();
          await page.getByRole("columnheader", { name: "Annotations", exact: true }).locator("svg").click();
          await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).toBeAttached();
          await expect(page.getByRole("columnheader", { name: "Annotations-2", exact: true })).toBeAttached();
          await expect(page.getByRole("cell").nth(3)).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });
      });
    });
  });
});
