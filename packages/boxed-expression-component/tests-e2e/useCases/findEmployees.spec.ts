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

test.describe("Check find employees use case", () => {
  test("should render employees expression correctly", async ({ useCases, bee }) => {
    await useCases.openFindEmployees("employees");
    await expect(bee.getContainer()).toHaveScreenshot("employees-expression.png");
  });

  test("should render find by employees expression correctly", async ({ useCases, bee }) => {
    await useCases.openFindEmployees("find-by-employees");
    await expect(bee.getContainer()).toHaveScreenshot("find-by-employees-expression.png");
  });

  test("should render find employees by knowledge expression correctly", async ({ useCases, bee }) => {
    await useCases.openFindEmployees("find-employees-by-knowledge");
    await expect(bee.getContainer()).toHaveScreenshot("find-employees-by-knowledge-expression.png");
  });
});
