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
import { HeadingType, MenuItem } from "../../__fixtures__/contextMenu";
import { AssetType } from "../../__fixtures__/editor";
import { AddColumnPosition } from "../../__fixtures__/table";

test.describe("Background table context menu", () => {
  test.describe("Context menu checks", () => {
    test.beforeEach(async ({ editor, backgroundTable, table }) => {
      await editor.createTestScenario(AssetType.DECISION);
      await editor.switchToBackgroundTable();
      await table.addPropertyColumn({
        targetCellName: "PROPERTY (<Undefined>)",
        position: AddColumnPosition.RIGHT,
        nth: 0,
      });
      await backgroundTable.fill({ content: "test", column: 1 });
    });

    test("should render select context menu", async ({ contextMenu }) => {
      await contextMenu.openOnCell({ rowNumber: "test test", columnNumber: 0 });
      await expect(contextMenu.getHeading({ heading: HeadingType.SELECTION })).toBeAttached();
      await expect(contextMenu.getHeading({ heading: HeadingType.SCENARIO })).not.toBeAttached();
      await expect(contextMenu.getHeading({ heading: HeadingType.FIELD })).not.toBeAttached();
      await expect(contextMenu.getHeading({ heading: HeadingType.INSTANCE })).not.toBeAttached();
    });

    test("should render field context menu", async ({ contextMenu }) => {
      await contextMenu.openOnProperty({ name: "PROPERTY (<Undefined>)", columnNumber: 1 });
      await expect(contextMenu.getHeading({ heading: HeadingType.SELECTION })).not.toBeAttached();
      await expect(contextMenu.getHeading({ heading: HeadingType.SCENARIO })).not.toBeAttached();
      await expect(contextMenu.getHeading({ heading: HeadingType.FIELD })).toBeAttached();
      await expect(contextMenu.getHeading({ heading: HeadingType.INSTANCE })).not.toBeAttached();
    });

    test("should render instance context menu", async ({ contextMenu }) => {
      await contextMenu.openOnInstance({ name: "INSTANCE-1 (<Undefined>)" });
      await expect(contextMenu.getHeading({ heading: HeadingType.SELECTION })).not.toBeAttached();
      await expect(contextMenu.getHeading({ heading: HeadingType.SCENARIO })).not.toBeAttached();
      await expect(contextMenu.getHeading({ heading: HeadingType.FIELD })).not.toBeAttached();
      await expect(contextMenu.getHeading({ heading: HeadingType.INSTANCE })).toBeAttached();
    });

    test("should add and delete instance column left", async ({ table, backgroundTable, contextMenu }) => {
      await table.addInstanceColumn({
        targetCellName: "INSTANCE-1 (<Undefined>)",
        position: AddColumnPosition.LEFT,
      });
      await expect(table.getColumnHeader({ name: "INSTANCE-3 (<Undefined>)" })).toBeAttached();
      await expect(backgroundTable.get()).toHaveScreenshot("background-table-add-instance-column-left.png");

      await contextMenu.openOnInstance({ name: "INSTANCE-3 (<Undefined>)" });
      await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_INSTANCE });
      await expect(table.getColumnHeader({ name: "INSTANCE-3 (<Undefined>)" })).not.toBeAttached();
    });

    test("should add and delete instance column right", async ({ table, backgroundTable, contextMenu }) => {
      await table.addInstanceColumn({
        targetCellName: "INSTANCE-1 (<Undefined>)",
        position: AddColumnPosition.RIGHT,
      });
      await expect(table.getColumnHeader({ name: "INSTANCE-3 (<Undefined>)" })).toBeAttached();
      await expect(backgroundTable.get()).toHaveScreenshot("background-table-add-instance-column-right.png");

      await contextMenu.openOnInstance({ name: "INSTANCE-3 (<Undefined>)" });
      await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_INSTANCE });
      await expect(table.getColumnHeader({ name: "INSTANCE-3 (<Undefined>)" })).not.toBeAttached();
    });

    test("should add and delete property column left", async ({ table, backgroundTable, contextMenu }) => {
      await table.addPropertyColumn({
        targetCellName: "PROPERTY (<Undefined>)",
        position: AddColumnPosition.LEFT,
        nth: 0,
      });
      await expect(table.getColumnHeader({ name: "PROPERTY (<Undefined>)" }).nth(2)).toBeAttached();
      await expect(backgroundTable.get()).toHaveScreenshot("background-table-add-property-column-left.png");

      await contextMenu.openOnProperty({ name: "PROPERTY (<Undefined>)", columnNumber: 1 });
      await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_FIELD });
      await expect(table.getColumnHeader({ name: "PROPERTY (<Undefined>)" }).nth(2)).not.toBeAttached();
    });

    test("should add and delete property column right", async ({ table, backgroundTable, contextMenu }) => {
      await table.addPropertyColumn({
        targetCellName: "PROPERTY (<Undefined>)",
        position: AddColumnPosition.RIGHT,
        nth: 0,
      });
      await expect(table.getColumnHeader({ name: "PROPERTY (<Undefined>)" }).nth(2)).toBeAttached();
      await expect(backgroundTable.get()).toHaveScreenshot("background-table-add-property-column-right.png");

      await contextMenu.openOnProperty({ name: "PROPERTY (<Undefined>)", columnNumber: 1 });
      await contextMenu.clickMenuItem({ menuItem: MenuItem.DELETE_FIELD });
      await expect(table.getColumnHeader({ name: "PROPERTY (<Undefined>)" }).nth(2)).not.toBeAttached();
    });
  });
});
