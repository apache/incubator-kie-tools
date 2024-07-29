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

import { test, expect } from "../__fixtures__/base";

test.describe("DMN Editor - Standalone - API", () => {
  test.describe("Open editor", () => {
    test.beforeEach(async ({ editor }) => {
      await editor.open();
    });

    test("should open and close the editor with a blank DMN", async ({ page, editor }) => {
      await expect(page).toHaveScreenshot("open-editor.png");

      await editor.close();

      await expect(editor.getEditorIframe().getByText("This DMN's Diagram is empty")).not.toBeAttached();
      await expect(page).toHaveScreenshot("close-editor.png");
    });
  });
});
