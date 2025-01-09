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

import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { expect, test } from "../../__fixtures__/base";

test.describe("Create Decision table", () => {
  test.beforeEach(async ({ stories }) => {
    await stories.openDecisionTable();
  });

  test("should render expression correctly", async ({ bee, page }) => {
    await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Annotations", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "U", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(4);
    await expect(page.getByRole("cell")).toHaveCount(4);
    await expect(bee.getContainer()).toHaveScreenshot("decision-table.png");
  });

  test.describe("Hit policy", () => {
    const hitPolicies = [
      ["UNIQUE", "U"],
      ["FIRST", "F"],
      ["PRIORITY", "P"],
      ["ANY", "A"],
      ["COLLECT", "C?"],
      ["RULE ORDER", "R"],
      ["OUTPUT ORDER", "O"],
    ];

    for (const [policy, abbreviation] of hitPolicies) {
      test(`should change hit policy to ${policy}`, async ({ bee }) => {
        const decisionTable = bee.expression.asDecisionTable();
        await decisionTable.hitTableSelector.cell.click();
        await decisionTable.hitTableSelector.menu.option(policy).hover();
        await expect(decisionTable.hitTableSelector.menu.button(`${policy}-help`)).toBeAttached();
        await decisionTable.hitTableSelector.menu.option(policy).click();

        expect(await decisionTable.hitTableSelector.selectedHitTable.nth(0).innerText()).toBe(abbreviation);
      });
    }

    test("should change between hit policies", async ({ bee }) => {
      const decisionTable = bee.expression.asDecisionTable();
      await decisionTable.hitTableSelector.cell.click();

      await decisionTable.hitTableSelector.menu.option("FIRST").click();
      expect(await decisionTable.hitTableSelector.selectedHitTable.nth(0).innerText()).toBe("F");

      await decisionTable.hitTableSelector.menu.option("PRIORITY").click();
      expect(await decisionTable.hitTableSelector.selectedHitTable.nth(0).innerText()).toBe("P");

      await decisionTable.hitTableSelector.menu.option("ANY").click();
      expect(await decisionTable.hitTableSelector.selectedHitTable.nth(0).innerText()).toBe("A");

      await decisionTable.hitTableSelector.menu.option("COLLECT").click();
      expect(await decisionTable.hitTableSelector.selectedHitTable.nth(0).innerText()).toBe("C?");

      await decisionTable.hitTableSelector.menu.option("RULE ORDER").click();
      expect(await decisionTable.hitTableSelector.selectedHitTable.nth(0).innerText()).toBe("R");

      await decisionTable.hitTableSelector.menu.option("OUTPUT ORDER").click();
      expect(await decisionTable.hitTableSelector.selectedHitTable.nth(0).innerText()).toBe("O");

      await decisionTable.hitTableSelector.menu.option("UNIQUE").click();
      expect(await decisionTable.hitTableSelector.selectedHitTable.nth(0).innerText()).toBe("U");
    });

    test.describe("should change collect aggregator function", () => {
      const aggregatorFunction = [
        ["<None>", "?"],
        ["SUM", "+"],
        ["COUNT", "#"],
        ["MIN", "<"],
        ["MAX", ">"],
      ];

      for (const [aggregator, abbreviation] of aggregatorFunction) {
        test(`should change aggregator to ${aggregator}`, async ({ page, bee }) => {
          const decisionTable = bee.expression.asDecisionTable();

          await decisionTable.hitTableSelector.cell.click();

          await page.getByRole("menuitem", { name: "COLLECT" }).click();
          await page.getByRole("menuitem", { name: aggregator }).hover();

          await decisionTable.hitTableSelector.menu.option("COLLECT").hover();
          await decisionTable.hitTableSelector.menu.option(aggregator).hover();
          await expect(decisionTable.hitTableSelector.menu.button(`${abbreviation}-help`)).toBeAttached();

          await decisionTable.hitTableSelector.menu.option(aggregator).click();

          expect(await decisionTable.hitTableSelector.selectedHitTable.nth(0).innerText()).toBe(`C${abbreviation}`);
        });
      }
    });
  });

  test("should commit annotation by cell click", async ({ bee }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1158",
    });

    await bee.expression
      .asDecisionTable()
      .annotationHeaderAt(0)
      .setName({ name: "Changed Annotation Name", close: false });

    // commit a change by a click to another cell
    await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).content.click();

    await expect(bee.getContainer()).toHaveScreenshot("boxed-decision-table-commit-annotation-by-cell-click.png");
  });
});
