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

test.describe("Empty editor", () => {
  test("should render editor correctly", async ({ editor, testScenarioTable, backgroundTable }) => {
    await editor.openEmpty();
    await expect(editor.get()).toHaveScreenshot("create-a-new-test-scenario.png");

    await editor.createTestScenario(AssetType.DECISION);
    await expect(testScenarioTable.get()).toHaveScreenshot("empty-test-scenario-table.png");

    await editor.switchToBackgroundTable();
    await expect(backgroundTable.get()).toHaveScreenshot("empty-background-table.png");
  });
});
