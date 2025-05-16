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
import { CloseOption } from "../../api/nameAndDataTypeCell";

test.describe("Populate Decision table", () => {
  test("should correctly create a routing decision table", async ({ stories, page, bee, resizing }) => {
    await stories.openDecisionTable();

    await bee.expression.asDecisionTable().inputHeaderAt(0).open();
    await bee.expression.asDecisionTable().inputHeaderAt(0).setName({ name: "Post-bureau risk category" });
    await bee.expression
      .asDecisionTable()
      .inputHeaderAt(0)
      .setDataType({ dataType: "string", close: CloseOption.PRESS_ENTER });
    await resizing.reset(page.getByRole("columnheader", { name: "Post-bureau risk category (string)" }));
    await bee.expression.asDecisionTable().addInputAtStart();

    await bee.expression.asDecisionTable().inputHeaderAt(0).open();
    await bee.expression.asDecisionTable().inputHeaderAt(0).setName({ name: "Post-bureau affordability" });
    await bee.expression
      .asDecisionTable()
      .inputHeaderAt(0)
      .setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });
    await resizing.reset(page.getByRole("columnheader", { name: "Post-bureau affordability (boolean)" }));
    await bee.expression.asDecisionTable().addInputAtStart();

    await bee.expression.asDecisionTable().inputHeaderAt(0).open();
    await bee.expression.asDecisionTable().inputHeaderAt(0).setName({ name: "Credit Score" });
    await bee.expression
      .asDecisionTable()
      .inputHeaderAt(0)
      .setDataType({ dataType: "number", close: CloseOption.PRESS_ENTER });
    await resizing.reset(page.getByRole("columnheader", { name: "Credit Score (number)" }));
    await bee.expression.asDecisionTable().addInputAtStart();

    await bee.expression.asDecisionTable().inputHeaderAt(0).open();
    await bee.expression.asDecisionTable().inputHeaderAt(0).setName({ name: "Bankrupt" });
    await bee.expression
      .asDecisionTable()
      .inputHeaderAt(0)
      .setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });
    await resizing.reset(page.getByRole("columnheader", { name: "Bankrupt (boolean)" }));

    await bee.expression.asDecisionTable().outputHeaderAt(0).open();
    await bee.expression.asDecisionTable().outputHeaderAt(0).setName({ name: "Routing" });
    await bee.expression
      .asDecisionTable()
      .outputHeaderAt(0)
      .setDataType({ dataType: "string", close: CloseOption.PRESS_ENTER });
    await resizing.reset(page.getByRole("columnheader", { name: "Routing (string)" }));

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

    await bee.expression.asDecisionTable().addRowAtBottomOfIndex(1);
    await bee.expression.asDecisionTable().addRowAtBottomOfIndex(2);
    await bee.expression.asDecisionTable().addRowAtBottomOfIndex(3);
    await bee.expression.asDecisionTable().addRowAtBottomOfIndex(4);

    await bee.expression.asDecisionTable().fill({
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

    await expect(bee.getContainer()).toHaveScreenshot("decision-table-routing.png");
  });
});
