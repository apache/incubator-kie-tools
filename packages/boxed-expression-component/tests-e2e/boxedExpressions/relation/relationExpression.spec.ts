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

test.describe("Create Relation", () => {
  test("should render expression correctly", async ({ bee, stories, page }) => {
    await stories.openRelation();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "column-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "#", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(2);
    await expect(bee.getContainer()).toHaveScreenshot("relation.png");
  });
});

test.describe("Hovering - Relation Expression", () => {
  test.beforeEach(async ({ stories }) => {
    await stories.openRelation();
  });

  test.describe("Add rows", () => {
    test("should add row above by positioning mouse on the index cell upper section", async ({ bee }) => {
      await bee.expression.asRelation().cellAt({ row: 1, column: 1 }).fill("test");
      await bee.expression.asRelation().addRowAboveOfRowAtIndex(1);
      await expect(bee.expression.asRelation().cellAt({ row: 2, column: 1 }).content).toContainText("test");
    });

    test("should add row below by positioning mouse on the index cell lower section", async ({ bee }) => {
      await bee.expression.asRelation().cellAt({ row: 1, column: 1 }).fill("test-1");
      await bee.expression.asRelation().addRowAtBellowOfRowAtIndex(1);
      await expect(bee.expression.asRelation().cellAt({ row: 1, column: 1 }).content).toContainText("test-1");
      await expect(bee.expression.asRelation().cellAt({ row: 2, column: 1 }).content).toBeAttached();
    });
  });

  test.describe("Add columns", () => {
    test("should add column left by positioning mouse on the header cell left section", async ({ bee }) => {
      await bee.expression.asRelation().cellAt({ row: 1, column: 1 }).fill("test-1");
      await bee.expression.asRelation().addColumnAtLeftOfIndex(1);
      await bee.expression.asRelation().cellAt({ row: 1, column: 1 }).fill("test-2");
      await expect(bee.expression.asRelation().cellAt({ row: 1, column: 2 }).content).toContainText("test-1");
      await expect(bee.expression.asRelation().cellAt({ row: 1, column: 1 }).content).toContainText("test-2");
    });

    test("should add column right by positioning mouse on the header cell right section", async ({ bee }) => {
      await bee.expression.asRelation().cellAt({ row: 1, column: 1 }).fill("test-1");
      await bee.expression.asRelation().addColumnAtRightOfIndex(1);
      await bee.expression.asRelation().cellAt({ row: 1, column: 2 }).fill("test-2");
      await expect(bee.expression.asRelation().cellAt({ row: 1, column: 1 }).content).toContainText("test-1");
      await expect(bee.expression.asRelation().cellAt({ row: 1, column: 2 }).content).toContainText("test-2");
    });
  });
});
