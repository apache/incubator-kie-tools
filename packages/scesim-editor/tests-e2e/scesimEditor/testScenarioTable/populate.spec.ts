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
import { AddRowPosition } from "../../__fixtures__/table";

test.describe("Populate decision test scenario table", () => {
  test("should correctly populate decision-based a test scenario table", async ({
    editor,
    resizing,
    table,
    testScenarioTable,
  }) => {
    await editor.createTestScenario(AssetType.DECISION);
    await testScenarioTable.fill({ content: "Scenario one", rowLocatorInfo: "1", columnNumber: 0 });
    await testScenarioTable.fill({
      content: "date and time(5, 10)",
      rowLocatorInfo: "1 Scenario one Scenario one",
      columnNumber: 1,
    });
    await testScenarioTable.fill({
      content: "100",
      rowLocatorInfo: "1 Scenario one Scenario one date and time(5, 10) date and time(5, 10)",
      columnNumber: 2,
    });

    await resizing.reset(table.getColumnHeader({ name: "GIVEN" }));
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });

    await testScenarioTable.fill({ content: "{foo}", rowLocatorInfo: "2", columnNumber: 0 });
    await testScenarioTable.fill({ content: '"foo"', rowLocatorInfo: "3", columnNumber: 0 });
    await testScenarioTable.fill({ content: "[foo]", rowLocatorInfo: "4", columnNumber: 0 });
    await testScenarioTable.fill({ content: ",./123", rowLocatorInfo: "5", columnNumber: 0 });
    await testScenarioTable.fill({ content: '"6789"', rowLocatorInfo: "6", columnNumber: 0 });

    await testScenarioTable.fill({ content: '"foo"', rowLocatorInfo: "2 {foo} {foo}", columnNumber: 1 });
    await testScenarioTable.fill({ content: "[foo]", rowLocatorInfo: '3 "foo" "foo"', columnNumber: 1 });
    await testScenarioTable.fill({
      content: ",./123",
      rowLocatorInfo: "4 [foo] [foo]",
      columnNumber: 1,
    });
    await testScenarioTable.fill({
      content: "Scenario two",
      rowLocatorInfo: "5 ,./123 ,./123",
      columnNumber: 1,
    });
    await testScenarioTable.fill({
      content: '"129587289157"',
      rowLocatorInfo: '6 "6789" "6789"',
      columnNumber: 1,
    });

    await testScenarioTable.fill({
      content: '"foo"',
      rowLocatorInfo: '2 {foo} {foo} "foo" "foo"',
      columnNumber: 2,
    });
    await testScenarioTable.fill({
      content: ",./123",
      rowLocatorInfo: '3 "foo" "foo" [foo] [foo]',
      columnNumber: 2,
    });
    await testScenarioTable.fill({
      content: '"12859728917589"',
      rowLocatorInfo: "4 [foo] [foo] ,./123 ,./123",
      columnNumber: 2,
    });
    await testScenarioTable.fill({
      content: "Scenario date and time(213,456 , )",
      rowLocatorInfo: "5 ,./123 ,./123 Scenario two Scenario two",
      columnNumber: 2,
    });
    await testScenarioTable.fill({
      content: "{foofoo}{foofoo}",
      rowLocatorInfo: '6 "6789" "6789" "129587289157" "129587289157"',
      columnNumber: 2,
    });

    await resizing.reset(table.getColumnHeader({ name: "EXPECT" }));
    await expect(testScenarioTable.get()).toHaveScreenshot("test-scenario-table-decision.png");
  });
});

test.describe("Populate rule test scenario table", () => {
  test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1847");
  test("should correctly populate a rule-based test scenario table", async ({
    editor,
    resizing,
    table,
    testScenarioTable,
  }) => {
    await editor.createTestScenario(AssetType.RULE);

    await testScenarioTable.fill({ content: "Scenario one", rowLocatorInfo: "1", columnNumber: 0 });
    await testScenarioTable.fill({
      content: "date and time(5, 10)",
      rowLocatorInfo: "1 Scenario one Scenario one",
      columnNumber: 1,
    });
    await testScenarioTable.fill({
      content: "100",
      rowLocatorInfo: "1 Scenario one Scenario one date and time(5, 10) date and time(5, 10)",
      columnNumber: 2,
    });

    await resizing.reset(table.getColumnHeader({ name: "GIVEN" }));
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });
    await table.addRow({ targetCellName: "1", position: AddRowPosition.BELOW });

    await testScenarioTable.fill({ content: "{foo}", rowLocatorInfo: "2", columnNumber: 0 });
    await testScenarioTable.fill({ content: '"foo"', rowLocatorInfo: "3", columnNumber: 0 });
    await testScenarioTable.fill({ content: "[foo]", rowLocatorInfo: "4", columnNumber: 0 });
    await testScenarioTable.fill({ content: ",./123", rowLocatorInfo: "5", columnNumber: 0 });
    await testScenarioTable.fill({ content: '"6789"', rowLocatorInfo: "6", columnNumber: 0 });

    await testScenarioTable.fill({ content: '"foo"', rowLocatorInfo: "2 {foo} {foo}", columnNumber: 1 });
    await testScenarioTable.fill({ content: "[foo]", rowLocatorInfo: '3 "foo" "foo"', columnNumber: 1 });
    await testScenarioTable.fill({
      content: ",./123",
      rowLocatorInfo: "4 [foo] [foo]",
      columnNumber: 1,
    });
    await testScenarioTable.fill({
      content: "Scenario two",
      rowLocatorInfo: "5 ,./123 ,./123",
      columnNumber: 1,
    });
    await testScenarioTable.fill({
      content: '"129587289157"',
      rowLocatorInfo: '6 "6789" "6789"',
      columnNumber: 1,
    });

    await testScenarioTable.fill({
      content: '"foo"',
      rowLocatorInfo: '2 {foo} {foo} "foo" "foo"',
      columnNumber: 2,
    });
    await testScenarioTable.fill({
      content: ",./123",
      rowLocatorInfo: '3 "foo" "foo" [foo] [foo]',
      columnNumber: 2,
    });
    await testScenarioTable.fill({
      content: '"12859728917589"',
      rowLocatorInfo: "4 [foo] [foo] ,./123 ,./123",
      columnNumber: 2,
    });
    await testScenarioTable.fill({
      content: "Scenario date and time(213,456 , )",
      rowLocatorInfo: "5 ,./123 ,./123 Scenario two Scenario two",
      columnNumber: 2,
    });
    await testScenarioTable.fill({
      content: "{foofoo}{foofoo}",
      rowLocatorInfo: '6 "6789" "6789" "129587289157" "129587289157"',
      columnNumber: 2,
    });

    await resizing.reset(table.getColumnHeader({ name: "EXPECT" }));
    await expect(testScenarioTable.get()).toHaveScreenshot("test-scenario-table-rule.png");
  });
});
