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

import { test, expect } from "../__fixtures__/base";
import { MenuItem } from "../__fixtures__/contextMenu";
import { AddColumnPosition, AddRowPosition } from "../__fixtures__/table";

test.describe("Check unassigned are they old enough test case", () => {
  test("should render unassigned are they old enough test correctly", async ({
    useCases,
    testScenarioTable,
    contextMenu,
    table,
  }) => {
    await useCases.openAreTheyOldEnoughTest();
    await table.addInstanceColumn({
      targetCellName: "Applicant",
      position: AddColumnPosition.LEFT,
    });
    await table.addInstanceColumn({
      targetCellName: "LoanApplication",
      position: AddColumnPosition.LEFT,
      columnNumber: 1,
    });
    await table.addRow({
      targetCellName: "2",
      position: AddRowPosition.BELOW,
    });

    await contextMenu.openOnColumnHeader({ name: "Applicant" });
    await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_INSTANCE });

    await contextMenu.openOnColumnHeader({ name: "LoanApplication", columnNumber: 0 });
    await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_INSTANCE });

    await contextMenu.openOnColumnHeader({ name: "IncomeSource" });
    await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_INSTANCE });

    await contextMenu.openOnColumnHeader({ name: "LoanApplication" });
    await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_INSTANCE });

    await contextMenu.openOnCell({ rowNumber: "1", columnNumber: 0 });
    await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_SCENARIO });
    await contextMenu.openOnCell({ rowNumber: "1", columnNumber: 0 });
    await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_SCENARIO });

    await expect(testScenarioTable.get()).toHaveScreenshot("unassigned-are-they-old-enough-test.png");
  });
});
