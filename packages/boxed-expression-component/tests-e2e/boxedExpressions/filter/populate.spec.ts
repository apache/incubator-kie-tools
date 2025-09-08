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
import { CloseOption } from "../../api/nameAndDataTypeCell";

test.describe("Populate Boxed Filter", () => {
  test("should correctly create a Rebooked Flights filter", async ({ stories, bee }) => {
    await stories.openBoxedFilter("base");

    await bee.expression.asFilter().expressionHeaderCell.open();
    await bee.expression.asFilter().expressionHeaderCell.setName({ name: "Rebooked Flights" });
    await bee.expression
      .asFilter()
      .expressionHeaderCell.setDataType({ dataType: "Any", close: CloseOption.PRESS_ENTER });

    // Prepare empty Filter 'in' and 'match' cells
    await bee.expression.asFilter().in.selectExpressionMenu.selectLiteral();
    await bee.expression.asFilter().match.selectExpressionMenu.selectLiteral();

    await bee.expression.asFilter().fill({
      collectionIn: ["Passengers"],
      collectionMatch: "item.Flight Number = Flight.Flight Number",
    });

    await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-rebooked-flights.png");
  });
});
