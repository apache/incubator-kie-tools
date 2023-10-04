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

test.describe("Create Boxed Context", () => {
  test("should render expression correctly", async ({ boxedExpressionEditor, stories, page }) => {
    await stories.openBoxedContext();
    await expect(page.getByText("Expression Name (Context)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "<result>" })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(2);
    await expect(page.getByRole("columnheader")).toHaveCount(1);
    await expect(page.getByRole("cell")).toHaveCount(4);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-context.png");
  });
});
