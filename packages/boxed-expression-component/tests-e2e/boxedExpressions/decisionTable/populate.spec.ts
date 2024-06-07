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

test.describe("Populate Decision table", () => {
  test("should correctly create a routing decision table", async ({
    stories,
    page,
    boxedExpressionEditor,
    resizing,
  }) => {
    await stories.openDecisionTable();

    await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Post-bureau risk category");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "string" }).click();
    await page.keyboard.press("Enter");
    await resizing.reset(page.getByRole("columnheader", { name: "Post-bureau risk category (string)" }));
    await page
      .getByRole("columnheader", { name: "Post-bureau risk category (string)" })
      .hover({ position: { x: 0, y: 0 } });
    await page.getByRole("row", { name: "Post-bureau risk category (string)" }).locator("svg").click();

    await page.getByRole("columnheader", { name: "input-2 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Post-bureau affordability");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");
    await resizing.reset(page.getByRole("columnheader", { name: "Post-bureau affordability (boolean)" }));
    await page
      .getByRole("columnheader", { name: "Post-bureau affordability (boolean)" })
      .hover({ position: { x: 0, y: 0 } });
    await page.getByRole("row", { name: "Post-bureau affordability (boolean)" }).locator("svg").click();

    await page.getByRole("columnheader", { name: "input-3 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Credit Score");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");
    await page.getByRole("columnheader", { name: "Credit Score (number)" }).hover({ position: { x: 0, y: 0 } });
    await page.getByRole("row", { name: "Credit Score (number)" }).locator("svg").click();

    await page.getByRole("columnheader", { name: "input-4 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Bankrupt");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Routing");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "string" }).click();
    await page.keyboard.press("Enter");

    test.info().annotations.push({
      type: TestAnnotations.WORKAROUND_DUE_TO,
      description: "https://github.com/apache/incubator-kie-issues/issues/541",
    });
    // Plus sign triggers flaky tests.
    await page.getByRole("columnheader", { name: "Credit Score (number)" }).hover({ position: { x: 0, y: 0 } });
    await page
      .getByRole("columnheader", { name: "Post-bureau affordability (boolean)" })
      .hover({ position: { x: 0, y: 0 } });
    await page
      .getByRole("columnheader", { name: "Post-bureau risk category (string)" })
      .hover({ position: { x: 0, y: 0 } });
    await page.getByRole("columnheader", { name: "Bankrupt (boolean)" }).hover({ position: { x: 0, y: 0 } });
    await page.getByRole("columnheader", { name: "Routing (string)" }).hover({ position: { x: 0, y: 0 } });

    await page.getByRole("cell", { name: "1" }).hover();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();

    await boxedExpressionEditor.fillDecisionTable({
      startAtCell: 0,
      tableData: [
        ["true", "-", "-", "-", `"Decline"`],
        ["-", "<580", "-", "-", `"Refer"`],
        ["-", "-", "false", "-", `"Decline"`],
        ["-", "-", "-", `"High"`, `"Refer"`],
        ["-", "-", "-", "-", `"Accept"`],
      ],
    });

    await page.getByText("U", { exact: true }).click();
    await page.getByRole("menuitem", { name: "PRIORITY" }).click();
    await page.keyboard.press("Escape");

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("decision-table-routing.png");
  });
});
