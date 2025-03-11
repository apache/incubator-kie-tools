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

test.beforeEach(async ({ overlays, editor }) => {
  await editor.openEvaluationHighlights();
  await overlays.enableEvaluationHighlights();
});

test.describe("Evaluation Highlights on Nodes", () => {
  test("should highlight node evaluation result - skipped", async ({ nodes }) => {
    await expect(nodes.get({ name: "Front End Ratio" })).toHaveScreenshot("evaluation-highlights-skipped.png");
  });

  test("should highlight node evaluation result - failure", async ({ nodes }) => {
    await expect(nodes.get({ name: "Back End Ratio" })).toHaveScreenshot("evaluation-highlights-failure.png");
  });

  test("should highlight node evaluation result - succeeded", async ({ nodes }) => {
    await expect(nodes.get({ name: "Credit Score Rating" })).toHaveScreenshot("evaluation-highlights-succeeded.png");
  });
});
