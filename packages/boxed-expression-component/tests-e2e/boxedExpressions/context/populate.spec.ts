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

import { expect, test } from "../../__fixtures__/base";
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.describe("Populate Boxed Context", () => {
  test("should correctly create pre-bureau risk category boxed context", async ({
    page,
    stories,
    bee,
    resizing,
    monaco,
  }) => {
    await stories.openBoxedContext();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Pre-bureau risk category calculation");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await bee.expression.asContext().entry(0).selectExpressionMenu.selectLiteral();
    await page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Existing Customer");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");
    await monaco.fill({ monacoParentLocator: page, content: "Applicant data.ExistingCustomer" });

    await bee.expression.asContext().resultEntry.selectExpressionMenu.selectDecisionTable();
    await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).hover({ position: { x: 0, y: 0 } });
    await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).locator("svg").click();
    await page.getByRole("cell", { name: "1", exact: true }).hover();
    await page.getByRole("cell", { name: "1", exact: true }).locator("svg").click();
    await page.getByRole("cell", { name: "1", exact: true }).locator("svg").click();
    await page.getByRole("cell", { name: "1", exact: true }).locator("svg").click();
    await page.getByRole("cell", { name: "1", exact: true }).locator("svg").click();
    await page.getByRole("cell", { name: "1", exact: true }).locator("svg").click();
    await page.getByRole("cell", { name: "1", exact: true }).locator("svg").click();
    await page.getByRole("cell", { name: "1", exact: true }).locator("svg").click();

    await page.getByRole("columnheader", { name: "input-2 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Existing customer");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");
    await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Application risk score");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await bee.expression.asDecisionTable().fill({
      startAtCell: 1,
      tableData: [
        ["false", "<100", `"High"`],
        ["false", "[100..120)", `"Medium"`],
        ["false", "[120..130]", `"Low"`],
        ["false", ">130", `"Very Low"`],
        ["true", "<80", `"Decline"`],
        ["true", "[80..90)", `"High"`],
        ["true", "[90..110]", `"Medium"`],
        ["true", ">110", `"Low"`],
      ],
    });

    await resizing.reset(page.getByRole("columnheader", { name: "Existing customer (boolean)" }));
    await resizing.reset(page.getByRole("columnheader", { name: "Application risk score (number)" }));

    test.info().annotations.push({
      type: TestAnnotations.WORKAROUND_DUE_TO,
      description: "https://github.com/apache/incubator-kie-issues/issues/536",
    });
    // Can't resize this column as it's named as "Expression Name"
    // await resizing.reset(
    //   page.getByRole("columnheader", { name: "Pre-bureau risk category calculation (number)" }).nth(1)
    // );

    await expect(bee.getContainer()).toHaveScreenshot("boxed-context-pre-bureau-risk-category.png");
  });
});
