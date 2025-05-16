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

test.describe("Populate Boxed Literal", () => {
  test("should correctly create a can drive boxed literal", async ({ stories, page, bee, resizing, monaco }) => {
    await stories.openBoxedLiteral();
    await bee.expression.asLiteral().expressionHeaderCell.open();
    await bee.expression.asLiteral().expressionHeaderCell.setName({ name: "Can drive?" });
    await bee.expression
      .asLiteral()
      .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

    await monaco.fill({ monacoParentLocator: page, nth: 0, content: "Age >= 18 then true else false" });
    await resizing.resizeCell(
      page.getByRole("columnheader", { name: "Can drive? (boolean)" }),
      { x: 0, y: 0 },
      { x: 80, y: 0 }
    );

    await expect(bee.getContainer()).toHaveScreenshot("boxed-literal-can-drive.png");
  });
});
