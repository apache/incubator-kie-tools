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

test.describe("Import from URL", () => {
  test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/548");
  test.describe("Home", () => {
    test.describe("GitHub", () => {
      test.describe("Unauthenticated", () => {
        test("should open a repository from GitHub on default branch", async ({ page, kieSandbox }) => {});

        test("should open a repository from GitHub on different branch", async ({ page, kieSandbox }) => {});

        test("should open a GitHub Gist on default branch", async ({ page, kieSandbox }) => {});

        test("should open a GitHub Gist on different branch", async ({ page, kieSandbox }) => {});
      });

      test.describe("Authenticated", () => {
        test("should open a private repository from GitHub on default branch", async ({ page, kieSandbox }) => {});

        test("should open a private repository from GitHub on different branch", async ({ page, kieSandbox }) => {});

        test("should open a private GitHub Gist on different branch", async ({ page, kieSandbox }) => {});

        test("should open a private GitHub Gist on default branch", async ({ page, kieSandbox }) => {});
      });
    });

    test.describe("BitBucket", () => {
      test.describe("Unauthenticated", () => {
        test("should open a repository from BitBucket on default branch", async ({ page, kieSandbox }) => {});

        test("should open a repository from BitBucket on different branch", async ({ page, kieSandbox }) => {});

        test("should open a BitBucket Snippet on default branch", async ({ page, kieSandbox }) => {});

        test("should open a BitBucket Snippet on different branch", async ({ page, kieSandbox }) => {});
      });

      test.describe("Authenticated", () => {
        test("should open a private repository from BitBucket on default branch", async ({ page, kieSandbox }) => {});

        test("should open a private repository from BitBucket on different branch", async ({ page, kieSandbox }) => {});

        test("should open a private BitBucket Snippet on different branch", async ({ page, kieSandbox }) => {});

        test("should open a private BitBucket Snippet on default branch", async ({ page, kieSandbox }) => {});
      });
    });

    test.describe("File URL", () => {
      test("should open a file by URL", async ({ page, kieSandbox }) => {});
    });
  });
});
