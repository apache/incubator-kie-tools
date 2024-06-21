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
import { AssetType } from "../../__fixtures__/editor";
import { AddColumnPosition } from "../../__fixtures__/table";

test.describe("Populate decision background table", () => {
  test("should correctly populate a decision-based background table", async ({
    editor,
    resizing,
    table,
    backgroundTable,
  }) => {
    await editor.createTestScenario(AssetType.DECISION);
    await editor.switchToBackgroundTable();
    await backgroundTable.fill({ content: "{foo}", column: 0 });

    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });

    await backgroundTable.fill({ content: "[foo]", column: 1 });
    await backgroundTable.fill({ content: '"foo"', column: 2 });
    await backgroundTable.fill({ content: ",./123981275980172957129517", column: 3 });
    await backgroundTable.fill({ content: "{foo}{foo}f", column: 4 });
    await backgroundTable.fill({ content: "=1-205=-1205=-0125-0215215", column: 5 });

    await resizing.reset(table.getColumnHeader({ name: "INSTANCE-1 (<Undefined>)" }));
    await expect(backgroundTable.get()).toHaveScreenshot("background-table-decision.png");
  });
});

test.describe("Populate rule background table", () => {
  test("should correctly populate a rule-based background table", async ({
    editor,
    resizing,
    table,
    backgroundTable,
  }) => {
    await editor.createTestScenario(AssetType.RULE);
    await editor.switchToBackgroundTable();
    await backgroundTable.fill({ content: "{foo}", column: 0 });

    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });

    await backgroundTable.fill({ content: "[foo]", column: 1 });
    await backgroundTable.fill({ content: '"foo"', column: 2 });
    await backgroundTable.fill({ content: ",./123981275980172957129517", column: 3 });
    await backgroundTable.fill({ content: "{foo}{foo}f", column: 4 });
    await backgroundTable.fill({ content: "=1-205=-1205=-0125-0215215", column: 5 });

    await resizing.reset(table.getColumnHeader({ name: "INSTANCE-1 (<Undefined>)" }));
    await expect(backgroundTable.get()).toHaveScreenshot("background-table-rule.png");
  });
});

test.describe("Populate background table based on traffic violation use case", () => {
  test("should correctly populate column headers of traffic violation use case", async ({
    editor,
    table,
    useCases,
    selectorPanel,
    backgroundTable,
  }) => {
    await useCases.openTrafficViolationTest();
    await editor.switchToBackgroundTable();
    await selectorPanel.open();

    await table.selectColumnHeader({ name: "INSTANCE 1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Driver" });
    await table.selectColumnHeader({ name: "Expression </>" });
    await selectorPanel.assign({ name: "Points" });

    await table.addInstanceColumn({
      targetCellName: "Driver",
      position: AddColumnPosition.RIGHT,
    });

    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await selectorPanel.expandAttribute({ name: "Should the driver be suspended?" });
    await selectorPanel.assign({ name: "value" });

    await table.addInstanceColumn({
      targetCellName: "Should the driver be suspended?",
      position: AddColumnPosition.RIGHT,
    });

    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await selectorPanel.expandAttribute({ name: "Fine" });
    await selectorPanel.assign({ name: "Amount" });

    await table.addPropertyColumn({
      targetCellName: "Amount",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await selectorPanel.assign({ name: "Points" });

    await table.addInstanceColumn({
      targetCellName: "Should the driver be suspended?",
      position: AddColumnPosition.LEFT,
    });

    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await selectorPanel.expandAttribute({ name: "Violation" });
    await selectorPanel.assign({ name: "Actual Speed" });

    await table.addPropertyColumn({
      targetCellName: "Actual Speed",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await selectorPanel.assign({ name: "Type" });

    await table.addPropertyColumn({
      targetCellName: "Actual Speed",
      position: AddColumnPosition.LEFT,
      nth: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await selectorPanel.assign({ name: "Speed Limit" });

    await selectorPanel.close();
    await expect(backgroundTable.get()).toHaveScreenshot("background-table-traffic-violation-headers.png");
  });

  test("should correctly remove instance from selector panel once assigned", async ({
    editor,
    table,
    useCases,
    selectorPanel,
  }) => {
    await useCases.openTrafficViolationTest();
    await editor.switchToBackgroundTable();

    await table.addPropertyColumn({
      targetCellName: "PROPERTY 1 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY 1 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });

    await selectorPanel.open();
    await table.selectColumnHeader({ name: "INSTANCE 1 (<Undefined>)" });
    await expect(selectorPanel.getAttribute({ name: "Driver" })).toBeAttached();
    await selectorPanel.assign({ name: "Driver" });
    await table.selectColumnHeader({ name: "INSTANCE-2 (<Undefined>)" });
    await expect(selectorPanel.getAttribute({ name: "Driver" })).not.toBeAttached();
  });

  test("should correctly remove property from selector panel once assigned", async ({
    editor,
    table,
    useCases,
    selectorPanel,
  }) => {
    await useCases.openTrafficViolationTest();
    await editor.switchToBackgroundTable();
    await selectorPanel.open();
    await table.selectColumnHeader({ name: "INSTANCE 1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Violation" });

    await table.addPropertyColumn({
      targetCellName: "Expression </>",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });

    await table.selectColumnHeader({ name: "Expression </>" });
    await expect(selectorPanel.getAttribute({ name: "Speed Limit" })).toBeAttached();
    await selectorPanel.assign({ name: "Speed Limit" });
    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await expect(selectorPanel.getAttribute({ name: "Speed Limit" })).not.toBeAttached();
  });

  test("should correctly populate an instance by assigning a property", async ({
    editor,
    table,
    useCases,
    selectorPanel,
  }) => {
    await useCases.openTrafficViolationTest();
    await editor.switchToBackgroundTable();
    await selectorPanel.open();
    await expect(table.getColumnHeader({ name: "Violation" })).not.toBeAttached();

    await table.selectColumnHeader({ name: "PROPERTY 1 (<Undefined>)" });
    await selectorPanel.expandAttribute({ name: "Violation" });
    await selectorPanel.assign({ name: "Speed Limit" });
    await expect(table.getColumnHeader({ name: "Violation" })).toBeAttached();
  });

  test("when deleting an instance should correctly delete all its properties", async ({
    editor,
    table,
    useCases,
    selectorPanel,
    contextMenu,
  }) => {
    await useCases.openTrafficViolationTest();
    await editor.switchToBackgroundTable();
    await selectorPanel.open();

    await table.selectColumnHeader({ name: "INSTANCE 1 (<Undefined>)" });
    await selectorPanel.assign({ name: "Driver" });

    await table.addInstanceColumn({
      targetCellName: "Driver",
      position: AddColumnPosition.RIGHT,
    });

    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await selectorPanel.expandAttribute({ name: "Violation" });
    await selectorPanel.assign({ name: "Actual Speed" });

    await table.addPropertyColumn({
      targetCellName: "Actual Speed",
      position: AddColumnPosition.RIGHT,
      nth: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await selectorPanel.assign({ name: "Type" });

    await table.addPropertyColumn({
      targetCellName: "Actual Speed",
      position: AddColumnPosition.LEFT,
      nth: 0,
    });

    await table.selectColumnHeader({ name: "PROPERTY (<Undefined>)" });
    await selectorPanel.assign({ name: "Speed Limit" });

    await expect(selectorPanel.getAttribute({ name: "Speed Limit" })).toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "Actual Speed" })).toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "Type" })).toBeAttached();

    await contextMenu.openOnInstance({ name: "Violation" });
    await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_INSTANCE });

    await expect(selectorPanel.getAttribute({ name: "Speed Limit" })).not.toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "Actual Speed" })).not.toBeAttached();
    await expect(selectorPanel.getAttribute({ name: "Type" })).not.toBeAttached();
  });
});
