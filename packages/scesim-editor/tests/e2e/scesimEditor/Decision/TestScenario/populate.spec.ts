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

import { test, expect } from "../../../__fixtures__/base";

test.describe("Populate Decision Test Scenario table", () => {
  test("should correctly populate a test scenario table", async ({ stories, page, resizing }) => {
    await stories.openTestScenarioTableDecision();
    await page.getByTestId("monaco-container").first().click();
    await page.locator(".editable-cell").first().press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("Scenario one");
    await page.getByRole("code").locator("div").filter({ hasText: "Scenario one" }).nth(3).click();
    await page.getByTestId("monaco-container").nth(1).click();
    await page.locator("td:nth-child(3) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("date and time(5, 10)");
    await page.getByText("date and time(5, 10)date and time(5, 10)").press("Tab");
    await page.locator("td:nth-child(4) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("100");
    await resizing.reset(page.getByRole("columnheader", { name: "GIVEN" }));
    await page.getByRole("columnheader", { name: "GIVEN" }).hover({ position: { x: 0, y: 0 } });
    await page
      .getByRole("cell", { name: "date and time(5, 10) date and time(5, 10)" })
      .getByTestId("monaco-container")
      .click();
    await page.getByText("date and time(5, 10)date and time(5, 10)").press("ArrowLeft");
    await page.getByLabel("Test Scenario").locator("svg").click();
    await page.getByLabel("Test Scenario").locator("svg").click();
    await page.getByLabel("Test Scenario").locator("svg").click();
    await page.getByLabel("Test Scenario").locator("svg").click();
    await page.getByLabel("Test Scenario").locator("svg").click();
    await page.getByRole("cell", { name: "Scenario one Scenario one" }).getByTestId("monaco-container").click();
    await page.getByText("Scenario oneScenario one").press("ArrowDown");
    await page.locator(".\\31  > td:nth-child(2) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("{foo}");
    await page.getByRole("row", { name: "3" }).getByTestId("monaco-container").first().click();
    await page.locator(".\\32  > td:nth-child(2) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill('"foo"');
    await page.getByRole("row", { name: "4" }).getByTestId("monaco-container").first().click();
    await page.locator(".\\33  > td:nth-child(2) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("[foo]");
    await page.getByRole("row", { name: "5", exact: true }).getByTestId("monaco-container").first().click();
    await page.locator(".\\34  > td:nth-child(2) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill(",./123");
    await page.getByRole("row", { name: "6" }).getByTestId("monaco-container").first().click();
    await page.locator(".\\35  > td:nth-child(2) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill('"6789"');
    await page.getByRole("row", { name: "2 {foo} {foo}" }).getByTestId("monaco-container").nth(1).click();
    await page.locator(".\\31  > td:nth-child(3) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill('"foo"');
    await page.getByRole("row", { name: '3 "foo" "foo"' }).getByTestId("monaco-container").nth(1).click();
    await page.locator(".\\32  > td:nth-child(3) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("[foo]");
    await page.getByRole("row", { name: "4 [foo] [foo]" }).getByTestId("monaco-container").nth(1).click();
    await page.locator(".\\33  > td:nth-child(3) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill(",./123");
    await page.getByRole("row", { name: "5 ,./123 ,./123" }).getByTestId("monaco-container").nth(1).click();
    await page.locator(".\\34  > td:nth-child(3) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("Scenario two");
    await page.getByRole("row", { name: '6 "6789" "6789"' }).getByTestId("monaco-container").nth(1).click();
    await page.locator(".\\35  > td:nth-child(3) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill('"129587289157"');
    await page.getByRole("row", { name: '2 {foo} {foo} "foo" "foo"' }).getByTestId("monaco-container").nth(2).click();
    await page.locator(".\\31  > td:nth-child(4) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("[foo]");
    await page.getByRole("row", { name: '3 "foo" "foo" [foo] [foo]' }).getByTestId("monaco-container").nth(2).click();
    await page.locator(".\\32  > td:nth-child(4) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill(",./123");
    await page.getByRole("row", { name: "4 [foo] [foo] ,./123 ,./123" }).getByTestId("monaco-container").nth(2).click();
    await page.locator(".\\33  > td:nth-child(4) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill('"12859728917589"');
    await page
      .getByRole("row", { name: "5 ,./123 ,./123 Scenario two Scenario two" })
      .getByTestId("monaco-container")
      .nth(2)
      .click();
    await page.locator(".\\34  > td:nth-child(4) > .editable-cell").press("Enter");
    await page
      .getByLabel("Editor content;Press Alt+F1 for Accessibility Options.")
      .fill("Scenario date and time(213, , )");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").press("ArrowRight");
    await page
      .getByLabel("Editor content;Press Alt+F1 for Accessibility Options.")
      .fill("Scenario date and time(213,456 , )");
    await page
      .getByRole("row", { name: '6 "6789" "6789" "129587289157" "129587289157"' })
      .getByTestId("monaco-container")
      .nth(2)
      .click();
    await page.locator(".\\35  > td:nth-child(4) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("{foofoo}{foofoo}");
    await resizing.reset(page.getByRole("columnheader", { name: "EXPECT" }));
    await expect(page.getByLabel("Test Scenario").getByTestId("standalone-bee-table")).toHaveScreenshot(
      "test-scenario-table-decision.png"
    );
  });
});
