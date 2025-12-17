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
import { AssetType } from "../../__fixtures__/editor";

test.describe("Test Scenario table misc", () => {
  test.beforeEach(async ({ editor }) => {
    await editor.createTestScenario(AssetType.DECISION);
  });

  test("should render add column plus symbols on Instance headers", async ({ table, testScenarioTable }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1354");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1354",
    });

    await expect(table.getColumnHeader({ name: "INSTANCE-3 (<Undefined>)" })).not.toBeAttached();
    await table.getColumnHeader({ name: "INSTANCE-1 (<Undefined>)" }).hover();
    await testScenarioTable.clickPlusIcon();
    await expect(table.getColumnHeader({ name: "INSTANCE-3 (<Undefined>)" })).toBeAttached();
  });

  test("should render add column plus symbols on Property headers", async ({ table, testScenarioTable }) => {
    await expect(table.getColumnHeader({ name: "INSTANCE-3 (<Undefined>)" })).not.toBeAttached();
    await table.getColumnHeader({ name: "PROPERTY-1 (<Undefined>)", columnNumber: 0 }).hover();
    await testScenarioTable.clickPlusIcon();
    await expect(table.getColumnHeader({ name: "INSTANCE-3 (<Undefined>)" })).toBeAttached();
  });

  test("should not render add column plus symbol on Given header", async ({ table, testScenarioTable }) => {
    await table.getColumnHeader({ name: "GIVEN" }).hover();
    await expect(testScenarioTable.getPlusIcon()).not.toBeAttached();
  });

  test("should not render add column plus symbol on Expect header", async ({ table, testScenarioTable }) => {
    await table.getColumnHeader({ name: "EXPECT" }).hover();
    await expect(testScenarioTable.getPlusIcon()).not.toBeAttached();
  });

  test("should render add column plus symbol on table cell", async ({ table, testScenarioTable }) => {
    await table.getNumberedCell({ name: "1" }).hover();
    await expect(testScenarioTable.getPlusIcon()).toBeAttached();
  });
});
