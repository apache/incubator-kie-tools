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

/**
 * Returns `xml` with the attributes of every start tag sorted by name.
 *
 * Attribute order carries no meaning in XML, and the marshaller does not preserve it: it builds its JSON by iterating
 * `Element.attributes`, and `build()` then serializes them in that same order. Engines disagree on what that order is.
 * Parsing `<e id="1" xmlns:zz="urn:zz" name="n" xmlns:aa="urn:aa"/>` gives:
 *
 *   - Firefox and jsdom      -> `id xmlns:zz name xmlns:aa`  (the source order)
 *   - WebKit and Chromium    -> `xmlns:zz xmlns:aa id name`  (namespace declarations hoisted to the front)
 *   - Chrome 153+            -> `xmlns:aa xmlns:zz id name`  (hoisted AND sorted alphabetically)
 *
 * Chrome 153 changed the last of these, which is what made this test start failing. Normalizing both sides keeps the
 * comparison on what the test is actually about — that every element, attribute, value and text node survives the
 * round-trip — while ignoring an ordering the library never promised to preserve. A missing or altered attribute
 * still fails, because sorting changes the order of the attributes and nothing else.
 */
export function withSortedAttributes(xml: string) {
  return xml.replace(
    /<([A-Za-z_][\w.:-]*)((?:\s+[A-Za-z_][\w.:-]*\s*=\s*"[^"]*")+)(\s*\/)?\s*>/g,
    (_match, tagName: string, rawAttributes: string, selfClosing: string | undefined) => {
      const attributes = [...rawAttributes.matchAll(/([A-Za-z_][\w.:-]*)\s*=\s*"([^"]*)"/g)]
        .map(([, name, value]) => `${name}="${value}"`)
        .sort();
      return `<${tagName} ${attributes.join(" ")}${selfClosing ? " /" : ""}>`;
    }
  );
}
