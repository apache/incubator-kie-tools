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

test.describe("Boxed List context menu", () => {
  test.describe("Rows control", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedList();
    });

    test("shouldn't render column context menu", async ({ bee }) => {
      const descriptionCell = bee.expression.asList().row(0).cell;
      await descriptionCell.contextMenu.open();
      await expect(descriptionCell.contextMenu.heading("ROWS")).toBeAttached();
      await expect(descriptionCell.contextMenu.heading("SELECTION")).toBeAttached();
      await expect(descriptionCell.contextMenu.heading("COLUMNS")).not.toBeAttached();
    });

    test("shouldn't render row context menu", async ({ page, bee }) => {
      const nameAndDataTypeCell = bee.expression.asList().expressionHeaderCell;
      await nameAndDataTypeCell.contextMenu.open();
      await expect(nameAndDataTypeCell.contextMenu.heading("ROWS")).not.toBeAttached();
      await expect(nameAndDataTypeCell.contextMenu.heading("SELECTION")).toBeAttached();
      await expect(nameAndDataTypeCell.contextMenu.heading("COLUMNS")).not.toBeAttached();
      await page.keyboard.press("Escape");

      const entryContextMenu = bee.expression.asList().row(0).expression.contextMenu;
      await entryContextMenu.open();
      await expect(entryContextMenu.heading("ROWS")).toBeAttached();
      await expect(entryContextMenu.heading("SELECTION")).toBeAttached();
      await expect(entryContextMenu.heading("COLUMNS")).not.toBeAttached();
    });

    test("should open row context menu and insert row above", async ({ bee }) => {
      const entry0 = bee.expression.asList().row(0);
      await entry0.selectExpressionMenu.selectLiteral();
      await entry0.expression.asLiteral().fill("test");
      await entry0.cell.contextMenu.open();
      await entry0.cell.contextMenu.option("Insert above").click();
      await expect(bee.expression.asList().row(1).expression.asLiteral().content).toContainText("test");
    });

    test("should open row context menu and insert row below", async ({ bee }) => {
      const entry0 = bee.expression.asList().row(0);
      await entry0.selectExpressionMenu.selectLiteral();
      await entry0.expression.asLiteral().fill("test");
      await entry0.cell.contextMenu.open();
      await entry0.cell.contextMenu.option("Insert below").click();
      await expect(entry0.expression.asLiteral().content).toContainText("test");
      await expect(bee.expression.asList().row(1).cell.content).toBeAttached();
    });

    test("should open row context menu and insert multiples rows above", async ({ bee }) => {
      const entry0 = bee.expression.asList().row(0);
      await entry0.selectExpressionMenu.selectLiteral();
      await entry0.expression.asLiteral().fill("test");
      await entry0.cell.contextMenu.open();
      await entry0.cell.contextMenu.option("Insert").click();
      await entry0.cell.contextMenu.button("plus").click();
      await entry0.cell.contextMenu.button("Insert").click();

      await expect(bee.expression.asList().row(0).cell.content).toBeAttached();
      await expect(bee.expression.asList().row(1).cell.content).toBeAttached();
      await expect(bee.expression.asList().row(2).cell.content).toBeAttached();
      await expect(bee.expression.asList().row(3).expression.asLiteral().content).toContainText("test");
    });

    test("should open row context menu and insert multiples rows below", async ({ bee }) => {
      const entry0 = bee.expression.asList().row(0);
      await entry0.selectExpressionMenu.selectLiteral();
      await entry0.expression.asLiteral().fill("test");
      await entry0.cell.contextMenu.open();
      await entry0.cell.contextMenu.option("Insert").click();
      await entry0.cell.contextMenu.button("minus").click();
      await entry0.cell.contextMenu.radio("Below").click();
      await entry0.cell.contextMenu.button("Insert").click();

      await expect(entry0.cell.content).toBeAttached();
      await expect(bee.expression.asList().row(1).cell.content).toBeAttached();
      await expect(entry0.expression.asLiteral().content).toContainText("test");
    });

    test("should open row context menu and delete row", async ({ bee }) => {
      const entry0 = bee.expression.asList().row(0);
      await entry0.selectExpressionMenu.selectLiteral();
      await entry0.expression.asLiteral().fill("test");
      await entry0.cell.contextMenu.open();
      await entry0.cell.contextMenu.option("Insert above").click();

      await expect(bee.expression.asList().row(1).expression.asLiteral().content).toContainText("test");
      await entry0.cell.contextMenu.open();
      await entry0.cell.contextMenu.option("Delete").click();
      await expect(entry0.expression.asLiteral().content).toContainText("test");
    });
  });

  test("should reset insert multiples menu when opening another cell context menu", async ({ stories, bee }) => {
    await stories.openBoxedList();
    const entry0 = bee.expression.asList().row(0);
    await entry0.expression.contextMenu.open();
    await entry0.expression.contextMenu.option("Insert").first().click();
    await entry0.cell.contextMenu.open();

    await expect(entry0.cell.contextMenu.heading("ROWS")).toBeAttached();
    await expect(entry0.cell.contextMenu.heading("SELECTION")).toBeAttached();
  });

  test.describe("Hovering", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedList();
    });

    test.describe("Add rows", () => {
      test("should add row above by positioning mouse on the index cell upper section", async ({ bee }) => {
        const entry0 = bee.expression.asList().row(0);
        await entry0.selectExpressionMenu.selectLiteral();
        await entry0.expression.asLiteral().fill("test");
        await bee.expression.asList().addEntryAtTop();

        await expect(bee.expression.asList().row(1).expression.asLiteral().content).toContainText("test");
      });

      test("should add row below by positioning mouse on the index cell lower section", async ({ bee }) => {
        const entry0 = bee.expression.asList().row(0);
        await entry0.selectExpressionMenu.selectLiteral();
        await entry0.expression.asLiteral().fill("test");
        await bee.expression.asList().addEntryBelowOfEntryAtIndex(0);

        await expect(bee.expression.asList().row(0).expression.asLiteral().content).toContainText("test");
      });
    });
  });
});
