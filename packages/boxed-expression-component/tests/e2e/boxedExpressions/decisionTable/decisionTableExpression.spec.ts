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

test.describe("Create Decision table", () => {
  test.beforeEach(async ({ stories }) => {
    await stories.openDecisionTable();
  });

  test("should render expression correctly", async ({ boxedExpressionEditor, page }) => {
    await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "U", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(4);
    await expect(page.getByRole("cell")).toHaveCount(4);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("decision-table.png");
  });

  test.describe("Hit policy", () => {
    test.beforeEach(async ({ page }) => {
      await page.getByRole("columnheader", { name: "U", exact: true }).click();
    });

    const hitPolicies = [
      ["UNIQUE", "U"],
      ["FIRST", "F"],
      ["PRIORITY", "P"],
      ["ANY", "A"],
      ["COLLECT", "C?"],
      ["RULE ORDER", "R"],
      ["OUTPUT ORDER", "O"],
    ];

    for (const [policy, abreviation] of hitPolicies) {
      test(`should change hit policy to ${policy}`, async ({ page }) => {
        await page.getByRole("menuitem", { name: policy }).hover();
        await expect(page.getByLabel(`${policy}-help`)).toBeAttached();
        await page.getByRole("menuitem", { name: policy }).click();
        await expect(page.getByRole("columnheader", { name: abreviation, exact: true })).toBeAttached();
      });
    }

    test("should change between hit policies", async ({ page }) => {
      await page.getByRole("menuitem", { name: "FIRST" }).click();
      await expect(page.getByRole("columnheader", { name: "F", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "PRIORITY" }).click();
      await expect(page.getByRole("columnheader", { name: "P", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "ANY" }).click();
      await expect(page.getByRole("columnheader", { name: "A", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "COLLECT" }).click();
      await expect(page.getByRole("columnheader", { name: "C?", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "RULE ORDER" }).click();
      await expect(page.getByRole("columnheader", { name: "R", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "OUTPUT ORDER" }).click();
      await expect(page.getByRole("columnheader", { name: "O", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "UNIQUE" }).click();
      await expect(page.getByRole("columnheader", { name: "U", exact: true })).toBeAttached();
    });

    test.describe("should change collect aggregator function", () => {
      const aggregatorFunction = [
        ["<None>", "?"],
        ["SUM", "+"],
        ["COUNT", "#"],
        ["MIN", "<"],
        ["MAX", ">"],
      ];

      for (const [aggregator, abreviation] of aggregatorFunction) {
        test(`should change aggregator to ${aggregator}`, async ({ page }) => {
          await page.getByRole("menuitem", { name: "COLLECT" }).click();
          await page.getByRole("menuitem", { name: aggregator }).hover();
          await expect(page.getByLabel(`${abreviation}-help`)).toBeAttached();
          await page.getByRole("menuitem", { name: aggregator }).click();
          await expect(page.getByRole("columnheader", { name: `C${abreviation}`, exact: true })).toBeAttached();
        });
      }
    });
  });
});
