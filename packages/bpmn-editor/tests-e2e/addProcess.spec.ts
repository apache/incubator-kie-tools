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

import { test, expect } from "./__fixtures__/base";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add Process", () => {
  test("should set initial process id", async ({ page }) => {
    await expect(page.getByRole("heading", { name: "Create Your BPMN Process" })).toBeVisible();
    await page.getByPlaceholder("e.g., hiring").fill("test");
    await page.getByRole("button", { name: "Start Modeling" }).click();
    await expect(page.getByRole("heading", { name: "This BPMN is empty" })).toBeVisible();

    // Check if pallette is visible
    await expect(page.getByRole("button", { name: "Process Variables" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Correlations" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Properties Management" })).toBeVisible();

    await expect(page.getByTitle("Start Events")).toBeVisible();
    await expect(page.getByTitle("Intermediate Catch Events")).toBeVisible();
    await expect(page.getByTitle("Intermediate Throw Events")).toBeVisible();
    await expect(page.getByTitle("End Events")).toBeVisible();
    await expect(page.getByTitle("Tasks")).toBeVisible();
    await expect(page.getByTitle("Call Activity")).toBeVisible();
    await expect(page.getByTitle("Sub-processes")).toBeVisible();
    await expect(page.getByTitle("Gateways")).toBeVisible();
    await expect(page.getByTitle("Lanes")).toBeVisible();

    await expect(page.getByTitle("Data Object")).toBeVisible();
    await expect(page.getByTitle("Group")).toBeVisible();
    await expect(page.getByTitle("Text Annotation")).toBeVisible();
  });

  test("should add process with default properties", async ({ page, jsonModel }) => {
    await expect(page.getByRole("heading", { name: "Create Your BPMN Process" })).toBeVisible();
    await page.getByPlaceholder("e.g., hiring").fill("test");
    await page.getByRole("button", { name: "Start Modeling" }).click();
    await expect(page.getByRole("heading", { name: "This BPMN is empty" })).toBeVisible();

    const bpmnProcess = await jsonModel.getProcess();
    expect(bpmnProcess).toBeDefined();
    expect(bpmnProcess?.["@_id"]).toBeDefined();
    expect(bpmnProcess?.["@_isExecutable"]).toBe(true);
    expect(bpmnProcess?.["@_drools:adHoc"]).toBe(false);
  });
});
