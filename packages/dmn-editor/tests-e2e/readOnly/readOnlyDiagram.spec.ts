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

import { expect, test } from "../__fixtures__/base";

test.beforeEach(async ({ editor, nodes }) => {
  await editor.openLoanPreQualification();
  await expect(nodes.get({ name: "Loan Pre-Qualification" })).toBeVisible();
  await editor.setIsReadOnly(true);
});

test.describe("Read Only mode - Diagram", () => {
  test("should hide pallete and most controls", async ({ page }) => {
    await expect(page).toHaveScreenshot("readOnly-enabled.png");
  });
  test("should not be able to rename node", async ({ nodes }) => {
    await nodes.selectLabel({ name: "Loan Pre-Qualification" });
    await expect(nodes.get({ name: "Loan Pre-Qualification" }).getByRole("textbox").nth(0)).toBeDisabled();
  });
  test("should not be able to change node type", async ({ nodes }) => {
    await nodes.hover({ name: "Loan Pre-Qualification" });
    await expect(
      nodes.get({ name: "Loan Pre-Qualification" }).getByPlaceholder("Select a data type...")
    ).toBeDisabled();
  });
  test("should not be able to drag node", async ({ page, nodes }) => {
    await nodes.move({ name: "Loan Pre-Qualification", targetPosition: { x: 0, y: 0 } });
    await expect(page).toHaveScreenshot("readOnly-node-not-moved.png");
  });
  test("should not be able to edit node", async ({ nodes }) => {
    await nodes.get({ name: "Loan Pre-Qualification" }).hover();
    await expect(nodes.get({ name: "Loan Pre-Qualification" }).getByText("Edit")).not.toBeAttached();
    await expect(nodes.get({ name: "Loan Pre-Qualification" }).getByText("View")).toBeAttached();
  });
  test("should not be able to add edges", async ({ page, nodes }) => {
    await nodes.get({ name: "Loan Pre-Qualification" }).hover();
    await expect(page).toHaveScreenshot("readOnly-no-edge-handles.png");
  });
  test("should not be able to delete nodes", async ({ nodes }) => {
    await nodes.delete({ name: "Loan Pre-Qualification" });
    await expect(nodes.get({ name: "Loan Pre-Qualification" })).toBeAttached();
  });
});
