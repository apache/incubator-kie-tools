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

import { test } from "../__fixtures__/base";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Model - DRD", () => {
  test.describe("Create DRD", () => {
    test("Create DRD in empty diagram", async ({ drds }) => {
      await drds.open();
      await drds.create({ name: "second drd" });
    });

    test("Create DRD in diagram containing DRGs", async ({ drds }) => {
      // TODO
    });
  });

  test.describe("Rename DRD", () => {
    test("DRD names should be updated in correct places", async ({ drds }) => {
      // TODO
      // have 2 DRDs, at least one non default name
      // navigate between them and check the name is updated in tvo places
    });

    test("rename using special character", async ({ drds }) => {
      // TODO
    });

    test("remove DRG element - its removed from all DRDs", async ({ drds }) => {
      // TODO
    });
  });

  test.describe("Delete DRD", async () => {});

  test.describe("Navigate DRDs", () => {
    // before create drds that will be switched in the tests

    test("switch to second", async ({ drds }) => {
      // TODO
    });

    test("switch to second and return back", async ({ drds }) => {
      // TODO
    });
  });
});
