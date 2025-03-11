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
import { MenuItem } from "../../__fixtures__/contextMenu";
import { AddColumnPosition, AddRowPosition } from "../../__fixtures__/table";

test.describe("Use Selector Panel on Test Scenario table based on are they old enough use case", () => {
  test.beforeEach(async ({ useCases, table, contextMenu, selectorPanel }) => {
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
    await selectorPanel.open();
  });

  test("should correctly remove property from selector panel once assigned", async ({ table, selectorPanel }) => {
    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)", columnNumber: 0 });
    await selectorPanel.expandAttribute({ name: "IncomeSource", dataType: "mortgages.mortgages.IncomeSource" });
    await selectorPanel.assign({ name: "amount" });
    await expect(selectorPanel.getAttribute({ name: "amount" })).toBeAttached();
    await table.addPropertyColumn({
      targetCellName: "amount",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.selectColumnHeader({ name: "PROPERTY-3 (<Undefined>)", columnNumber: 0 });
    await expect(selectorPanel.getAttribute({ name: "amount" })).not.toBeAttached();
  });

  test("should correctly populate an instance by assigning a property", async ({ table, selectorPanel }) => {
    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)", columnNumber: 0 });
    await selectorPanel.expandAttribute({ name: "IncomeSource", dataType: "mortgages.mortgages.IncomeSource" });
    await selectorPanel.assign({ name: "amount" });
    await expect(table.getColumnHeader({ name: "IncomeSource" })).toBeAttached();
  });

  test("deleting an instance should correctly delete all its properties", async ({
    table,
    selectorPanel,
    contextMenu,
  }) => {
    await table.addInstanceColumn({
      targetCellName: "INSTANCE-1 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
    });

    await table.selectColumnHeader({ name: "INSTANCE-1 (<Undefined>)" });
    await selectorPanel.assign({ name: "LoanApplication" });

    await table.addPropertyColumn({
      targetCellName: "expression </>",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY-4 (<Undefined>)", columnNumber: 0 });
    await selectorPanel.assign({ name: "approved" });

    await table.selectColumnHeader({ name: "expression </>" });
    await selectorPanel.assign({ name: "explanation" });

    await expect(table.getColumnHeader({ name: "approved (Boolean)" })).toBeAttached();
    await expect(table.getColumnHeader({ name: "explanation (String)" })).toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "approved" })).toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "explanation" })).toBeAttached();

    await contextMenu.openOnColumnHeader({ name: "LoanApplication (LoanApplication)" });
    await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_INSTANCE });

    await expect(table.getColumnHeader({ name: "approved (Boolean)" })).not.toBeAttached();
    await expect(table.getColumnHeader({ name: "explanation (String)" })).not.toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "approved" })).not.toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "explanation" })).not.toBeAttached();
  });
});
