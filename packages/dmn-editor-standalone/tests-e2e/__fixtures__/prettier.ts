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

import * as prettier from "prettier";
import xmlPlugin from "@prettier/plugin-xml";

// Prettier loads configuration files using dynamic `import()` calls inside `prettier.resolveConfig()`.
// However, when Playwright detects the use of `__dirname`, it opts into its CommonJS-based test runner
// (see https://github.com/microsoft/playwright/issues/37890).
//
// In this CJS-backed environment, Prettier’s internal `import()` never
// resolves nor rejects, causing test execution to hang indefinitely.
//
// To avoid this, we bypass `prettier.resolveConfig()` and inline the Prettier configuration manually.
export async function prettierFormat(content: string) {
  return prettier.format(content, {
    printWidth: 120,
    trailingComma: "es5",
    xmlWhitespaceSensitivity: "preserve",
    plugins: [xmlPlugin],
    parser: "xml",
  });
}
