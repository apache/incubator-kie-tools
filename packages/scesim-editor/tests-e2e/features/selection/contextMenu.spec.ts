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

import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { test, expect } from "../../__fixtures__/base";
import { MenuItem } from "../../__fixtures__/contextMenu";
import { AssetType } from "../../__fixtures__/editor";

test.describe("Selection", () => {
  test.describe("Context menu", () => {
    test.beforeEach(async ({ editor, testScenarioTable }) => {
      await editor.createTestScenario(AssetType.DECISION);
      await testScenarioTable.fill({ content: '"test"', rowLocatorInfo: "1", columnNumber: 1 });
    });

    test.describe(() => {
      test.beforeEach(async ({ clipboard, context, browserName }) => {
        test.skip(
          browserName !== "chromium",
          "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
        );
        clipboard.setup(context, browserName);
      });

      test("should use copy from selection context menu", async ({ clipboard, contextMenu, table }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1861");
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/1861",
        });

        await contextMenu.openOnCell({ rowNumber: "1", columnNumber: 1 });
        await contextMenu.clickMenuItem({ menuItem: MenuItem.COPY });
        await expect(table.getCell({ rowNumber: "1", columnNumber: 1 })).toContainText("test");

        await table.deleteCellContent({ rowNumber: "1", columnNumber: 1 });
        await expect(table.getCell({ rowNumber: "1", columnNumber: 1 })).not.toContainText("test");

        await clipboard.paste();
        await expect(table.getCell({ rowNumber: "1", columnNumber: 1 })).toContainText("test");
      });

      test("should use cut from selection context menu", async ({ clipboard, contextMenu, table }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1861");
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/1861",
        });

        await expect(table.getCell({ rowNumber: "1", columnNumber: 1 })).toContainText("test");
        await contextMenu.openOnCell({ rowNumber: "1", columnNumber: 1 });
        await contextMenu.clickMenuItem({ menuItem: MenuItem.CUT });

        await expect(table.getCell({ rowNumber: "1", columnNumber: 1 })).not.toContainText("test");
        await table.selectCell({ rowNumber: "1", columnNumber: 1 });

        await clipboard.paste();
        await expect(table.getCell({ rowNumber: "1", columnNumber: 1 })).toContainText("test");
      });

      test("should use copy and paste from selection context menu", async ({ contextMenu, table }) => {
        await contextMenu.openOnCell({ rowNumber: "1", columnNumber: 1 });
        await contextMenu.clickMenuItem({ menuItem: MenuItem.COPY });

        await expect(table.getCell({ rowNumber: "1", columnNumber: 2 })).toContainText("test");

        await table.deleteCellContent({ rowNumber: "1", columnNumber: 1 });
        await expect(table.getCell({ rowNumber: "1", columnNumber: 1 })).not.toContainText("test");

        await contextMenu.openOnCell({ rowNumber: "1", columnNumber: 1 });
        await contextMenu.clickMenuItem({ menuItem: MenuItem.PASTE });

        await expect(table.getCell({ rowNumber: "1", columnNumber: 2 })).toContainText("test");
      });

      test("should use cut and paste from selection context menu", async ({ contextMenu, table }) => {
        await contextMenu.openOnCell({ rowNumber: "1", columnNumber: 1 });
        await contextMenu.clickMenuItem({ menuItem: MenuItem.CUT });

        await expect(table.getCell({ rowNumber: "1", columnNumber: 2 })).not.toContainText("test");

        await contextMenu.openOnCell({ rowNumber: "1", columnNumber: 1 });
        await contextMenu.clickMenuItem({ menuItem: MenuItem.PASTE });

        await expect(table.getCell({ rowNumber: "1", columnNumber: 2 })).toContainText("test");
      });
    });

    test("should use reset from selection context menu", async ({ contextMenu, table }) => {
      await expect(table.getCell({ rowNumber: "1", columnNumber: 2 })).toContainText("test");
      await contextMenu.openOnCell({ rowNumber: "1", columnNumber: 1 });
      await contextMenu.clickMenuItem({ menuItem: MenuItem.RESET });
      await expect(table.getCell({ rowNumber: "1", columnNumber: 2 })).not.toContainText("test");
    });
  });
});
