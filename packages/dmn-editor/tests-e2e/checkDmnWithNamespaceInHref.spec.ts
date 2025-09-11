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

import { expect, test } from "./__fixtures__/base";

test.describe("Namespace in Href - Diagram", () => {
  test.beforeEach(async ({ editor, nodes }) => {
    await editor.openDecisionWithNamespaceInHref();
    await expect(nodes.get({ name: "Required Decision A" })).toBeVisible();
    await editor.setIsReadOnly(true);
  });
  test("should show edges if namespace present in hrefs", async ({ page }) => {
    await expect(page.getByText("Required Decision A")).toBeAttached();
    await expect(page.getByText("Decision in Need")).toBeAttached();
    await expect(page).toHaveScreenshot("namespace-in-required-decision-href.png");
  });
});
