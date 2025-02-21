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
import { AddColumnPosition } from "../../__fixtures__/table";

test.describe("Use Selector Panel on background table based on traffic violation use case", () => {
  test.beforeEach(async ({ editor, useCases, selectorPanel }) => {
    await useCases.openTrafficViolationTest();
    await editor.switchToBackgroundTable();
    await selectorPanel.open();
  });
  test("should correctly populate column headers of traffic violation use case", async ({
    table,
    selectorPanel,
    backgroundTable,
  }) => {
    await table.selectColumnHeader({ name: "INSTANCE 1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Driver" });
    await table.selectColumnHeader({ name: "expression </>" });
    await selectorPanel.assign({ name: "Points" });

    await table.addInstanceColumn({
      targetCellName: "Driver",
      position: AddColumnPosition.RIGHT,
    });

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.expandAttribute({
      name: "Should the driver be suspended?",
      dataType: "string",
    });
    await selectorPanel.assign({ name: "value" });

    await table.addInstanceColumn({
      targetCellName: "Should the driver be suspended?",
      position: AddColumnPosition.RIGHT,
    });

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.expandAttribute({ name: "Fine", dataType: "tFine" });
    await selectorPanel.assign({ name: "Amount" });

    await table.addPropertyColumn({
      targetCellName: "Amount",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Points" });

    await table.addInstanceColumn({
      targetCellName: "Should the driver be suspended?",
      position: AddColumnPosition.LEFT,
    });

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.expandAttribute({ name: "Violation", dataType: "tViolation" });
    await selectorPanel.assign({ name: "Actual Speed" });

    await table.addPropertyColumn({
      targetCellName: "Actual Speed",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Type" });

    await table.addPropertyColumn({
      targetCellName: "Actual Speed",
      position: AddColumnPosition.LEFT,
      columnNumber: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Speed Limit" });

    await selectorPanel.close();
    await expect(backgroundTable.get()).toHaveScreenshot("background-table-traffic-violation-headers.png");
  });

  test("should correctly remove instance from selector panel once assigned", async ({ table, selectorPanel }) => {
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-1 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-1 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });

    await table.selectColumnHeader({ name: "INSTANCE 1 (<Undefined>)" });
    await expect(selectorPanel.getAttribute({ name: "Driver" })).toBeAttached();
    await selectorPanel.assign({ name: "Driver" });
    await table.selectColumnHeader({ name: "INSTANCE-2 (<Undefined>)" });
    await expect(selectorPanel.getAttribute({ name: "Driver" })).not.toBeAttached();
  });

  test("should correctly remove property from selector panel once assigned", async ({ table, selectorPanel }) => {
    await table.selectColumnHeader({ name: "INSTANCE 1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Violation" });

    await table.addPropertyColumn({
      targetCellName: "expression </>",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });

    await table.selectColumnHeader({ name: "expression </>" });
    await expect(selectorPanel.getAttribute({ name: "Speed Limit" })).toBeAttached();
    await selectorPanel.assign({ name: "Speed Limit" });
    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await expect(selectorPanel.getAttribute({ name: "Speed Limit" })).not.toBeAttached();
  });

  test("should correctly populate an instance by assigning a property", async ({ table, selectorPanel }) => {
    await expect(table.getColumnHeader({ name: "Violation" })).not.toBeAttached();

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.expandAttribute({ name: "Violation", dataType: "tViolation" });
    await selectorPanel.assign({ name: "Speed Limit" });
    await expect(table.getColumnHeader({ name: "Violation" })).toBeAttached();
  });

  test("deleting an instance should correctly delete all its properties", async ({
    table,
    selectorPanel,
    contextMenu,
  }) => {
    await table.selectColumnHeader({ name: "INSTANCE 1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Driver" });

    await table.addInstanceColumn({
      targetCellName: "Driver",
      position: AddColumnPosition.RIGHT,
    });

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.expandAttribute({ name: "Violation", dataType: "tViolation" });
    await selectorPanel.assign({ name: "Actual Speed" });

    await table.addPropertyColumn({
      targetCellName: "Actual Speed",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Type" });

    await table.addPropertyColumn({
      targetCellName: "Actual Speed",
      position: AddColumnPosition.LEFT,
      columnNumber: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY-1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Speed Limit" });

    await expect(selectorPanel.getAttribute({ name: "Speed Limit" })).toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "Actual Speed" })).toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "Type" })).toBeAttached();

    await contextMenu.openOnColumnHeader({ name: "Violation" });
    await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_INSTANCE });

    await expect(selectorPanel.getAttribute({ name: "Speed Limit" })).not.toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "Actual Speed" })).not.toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "Type" })).not.toBeAttached();
  });
});
