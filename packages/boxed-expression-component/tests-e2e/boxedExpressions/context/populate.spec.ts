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
  test("should correctly create pre-bureau risk category boxed context", async ({ page, stories, bee, resizing }) => {
    await stories.openBoxedContext();

    await bee.expression.asContext().expressionHeaderCell.open();
    await bee.expression
      .asContext()
      .expressionHeaderCell.setName({ name: "Pre-bureau risk category calculation", close: false });
    await bee.expression.asContext().expressionHeaderCell.setDataType({ dataType: "number", close: true });

    await bee.expression.asContext().entry(0).selectExpressionMenu.selectLiteral();
    await bee.expression.asContext().entry(0).variable.open();
    await bee.expression.asContext().entry(0).variable.setName({ name: "Existing Customer" });
    await bee.expression.asContext().entry(0).variable.setDataType({ dataType: "boolean", commit: true });
    await bee.expression.asContext().entry(0).expression.asLiteral().fill("Applicant data.ExistingCustomer");

    await bee.expression.asContext().result.selectExpressionMenu.selectDecisionTable();
    await bee.expression.asContext().result.expression.asDecisionTable().addInputAtEnd();
    await bee.expression.asContext().result.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asContext().result.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asContext().result.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asContext().result.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asContext().result.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asContext().result.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asContext().result.expression.asDecisionTable().addRowAtTop();

    await bee.expression.asContext().result.expression.asDecisionTable().inputHeaderAt(0).open();
    await bee.expression
      .asContext()
      .result.expression.asDecisionTable()
      .inputHeaderAt(0)
      .setName({ name: "Existing customer", close: false });
    await bee.expression
      .asContext()
      .result.expression.asDecisionTable()
      .inputHeaderAt(0)
      .setDataType({ dataType: "boolean", close: true });

    await bee.expression.asContext().result.expression.asDecisionTable().inputHeaderAt(1).open();
    await bee.expression
      .asContext()
      .result.expression.asDecisionTable()
      .inputHeaderAt(1)
      .setName({ name: "Application risk score", close: false });
    await bee.expression
      .asContext()
      .result.expression.asDecisionTable()
      .inputHeaderAt(1)
      .setDataType({ dataType: "number", close: true });

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

  test("should not revert context header when '<result>' cell is edited", async ({ stories, bee }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1512",
    });

    await stories.openBoxedContext();

    // edit '<result>' cell for the fist time
    await bee.expression.asContext().result.selectExpressionMenu.selectLiteral();
    await bee.expression.asContext().result.expression.asLiteral().fill("1");

    // change the context header
    await bee.expression.asContext().expressionHeaderCell.open();
    await bee.expression.asContext().expressionHeaderCell.setName({ name: "New Expression Name", close: false });
    await bee.expression.asContext().expressionHeaderCell.setDataType({ dataType: "number", close: true });

    // edit '<result>' cell for the second time
    await bee.expression.asContext().result.expression.asLiteral().fill("2");

    // check the context header is not changed since last edit
    expect(await bee.expression.asContext().expressionHeaderCell.getName()).toBe("New Expression Name");
    expect(await bee.expression.asContext().expressionHeaderCell.getDataType()).toBe("(number)");
    await expect(bee.expression.asContext().result.expression.asLiteral().cell.content).toContainText("2");
  });
});
