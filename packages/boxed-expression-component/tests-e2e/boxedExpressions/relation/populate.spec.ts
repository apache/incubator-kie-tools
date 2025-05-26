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

test.describe("Populate Relation", () => {
  test("should correctly create a people relation", async ({ stories, page, bee, resizing }) => {
    await stories.openRelation();

    await bee.expression.asRelation().columnHeaderAtIndex(1).open();
    await bee.expression.asRelation().columnHeaderAtIndex(1).setName({ name: "Name" });
    await bee.expression
      .asRelation()
      .columnHeaderAtIndex(1)
      .setDataType({ dataType: "string", close: CloseOption.PRESS_ENTER });
    await resizing.resizeCell(
      page.getByRole("columnheader", { name: "Name (string)" }),
      { x: 0, y: 0 },
      { x: 40, y: 0 }
    );
    await bee.expression.asRelation().addColumnAtRightOfIndex(1);

    await bee.expression.asRelation().columnHeaderAtIndex(2).open();
    await bee.expression.asRelation().columnHeaderAtIndex(2).setName({ name: "Age" });
    await bee.expression
      .asRelation()
      .columnHeaderAtIndex(2)
      .setDataType({ dataType: "number", close: CloseOption.PRESS_ENTER });
    await bee.expression.asRelation().addColumnAtRightOfIndex(2);

    await bee.expression.asRelation().columnHeaderAtIndex(3).open();
    await bee.expression.asRelation().columnHeaderAtIndex(3).setName({ name: "Country" });
    await bee.expression
      .asRelation()
      .columnHeaderAtIndex(3)
      .setDataType({ dataType: "string", close: CloseOption.PRESS_ENTER });
    await bee.expression.asRelation().addColumnAtRightOfIndex(3);

    await bee.expression.asRelation().columnHeaderAtIndex(4).open();
    await bee.expression.asRelation().columnHeaderAtIndex(4).setName({ name: "Married" });
    await bee.expression
      .asRelation()
      .columnHeaderAtIndex(4)
      .setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

    test.info().annotations.push({
      type: TestAnnotations.WORKAROUND_DUE_TO,
      description: "https://github.com/apache/incubator-kie-issues/issues/541",
    });
    // Plus sign triggers flaky tests.
    await page.getByRole("columnheader", { name: "Name (string)" }).hover();
    await page.getByRole("columnheader", { name: "Age (number)" }).hover();
    await page.getByRole("columnheader", { name: "Country (string)" }).hover();
    await page.getByRole("columnheader", { name: "Married (boolean)" }).hover();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page
      .getByRole("row", { name: "# Expression Name (<Undefined" })
      .getByPlaceholder("Expression Name")
      .fill("People");
    await page.keyboard.press("Enter");

    await bee.expression.asRelation().addRowAtBellowOfRowAtIndex(1);
    await bee.expression.asRelation().addRowAtBellowOfRowAtIndex(1);

    await bee.expression.asRelation().fill({
      startAtCell: 0,
      relationData: [
        [`"John Doe"`, "30", `"US"`, `"S"`],
        [`"Richard roe"`, "54", `"Canada"`, `"M"`],
        [`"Jane Doe"`, "23", `"England"`, `"M"`],
      ],
    });

    await expect(bee.getContainer()).toHaveScreenshot("relation-people.png");
  });
});
