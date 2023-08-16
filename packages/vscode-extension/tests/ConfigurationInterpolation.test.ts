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

import { doInterpolation } from "@kie-tools-core/vscode-extension/dist/ConfigurationInterpolation";

describe("generateSvg", () => {
  test("Should replace tokens with mapped values", () => {
    const tokens = {
      myToken1: "Token 1 Value",
      "${myToken2}": "Token 2 Value",
      myToken3: "10",
    };

    const originalText = "This text has myToken1, ${myToken2} and myToken3!";

    const resultText = doInterpolation(tokens, originalText);

    expect(resultText).toBe("This text has Token 1 Value, Token 2 Value and 10!");
  });
  test("Should replace repeated tokens with mapped values", () => {
    const tokens = {
      myToken1: "Token 1 Value",
    };

    const originalText = "This text has myToken1myToken1myToken1 myToken1";

    const resultText = doInterpolation(tokens, originalText);

    expect(resultText).toBe("This text has Token 1 ValueToken 1 ValueToken 1 Value Token 1 Value");
  });
});
