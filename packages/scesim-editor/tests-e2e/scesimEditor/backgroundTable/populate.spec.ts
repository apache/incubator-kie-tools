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
    await backgroundTable.fill({ content: "{foo}", columnNumber: 0 });

    await table.addPropertyColumn({
      targetCellName: "PROPERTY-1 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-2 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-3 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-4 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-5 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });

    await backgroundTable.fill({ content: "[foo]", columnNumber: 1 });
    await backgroundTable.fill({ content: '"foo"', columnNumber: 2 });
    await backgroundTable.fill({ content: ",./123981275980172957129517", columnNumber: 3 });
    await backgroundTable.fill({ content: "{foo}{foo}f", columnNumber: 4 });
    await backgroundTable.fill({ content: "=1-205=-1205=-0125-0215215", columnNumber: 5 });

    await resizing.reset(table.getColumnHeader({ name: "INSTANCE-1 (<Undefined>)" }));
    await expect(backgroundTable.get()).toHaveScreenshot("background-table-decision.png");
  });
});

test.describe("Populate rule background table", () => {
  test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1847");
  test("should correctly populate a rule-based background table", async ({
    editor,
    resizing,
    table,
    backgroundTable,
  }) => {
    await editor.createTestScenario(AssetType.RULE);
    await editor.switchToBackgroundTable();
    await backgroundTable.fill({ content: "{foo}", columnNumber: 0 });

    await table.addPropertyColumn({
      targetCellName: "PROPERTY-1 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-2 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-3 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-4 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });
    await table.addPropertyColumn({
      targetCellName: "PROPERTY-5 (<Undefined>)",
      position: AddColumnPosition.RIGHT,
      columnNumber: 0,
    });

    await backgroundTable.fill({ content: "[foo]", columnNumber: 1 });
    await backgroundTable.fill({ content: '"foo"', columnNumber: 2 });
    await backgroundTable.fill({ content: ",./123981275980172957129517", columnNumber: 3 });
    await backgroundTable.fill({ content: "{foo}{foo}f", columnNumber: 4 });
    await backgroundTable.fill({ content: "=1-205=-1205=-0125-0215215", columnNumber: 5 });

    await resizing.reset(table.getColumnHeader({ name: "INSTANCE-1 (<Undefined>)" }));
    await expect(backgroundTable.get()).toHaveScreenshot("background-table-rule.png");
  });
});
