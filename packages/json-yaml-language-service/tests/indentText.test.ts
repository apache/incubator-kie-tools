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

import { indentText } from "../dist/channel";

describe("indentText", () => {
  describe.each([
    ["using 3 space indentation", " ", 3],
    ["using 3 tabs indentation", "  ", 3],
  ])("%s", (_desc, indentChar, indentNum) => {
    describe.each([
      ["skipping first line", true],
      ["without skipping first line", false],
    ])("%s", (_desc, skipFirstLine) => {
      const indentation = new Array(indentNum).fill(indentChar).join("");

      test.each([
        ["empty string", "", ""],
        ["single line", "test", `${skipFirstLine ? "" : indentation}test`],
        [
          "lorem ipsum",
          `lorem ipsum 
dolor sit amet, 
consectetur adipiscing 
    elit`,
          `${skipFirstLine ? "" : indentation}lorem ipsum 
${indentation}dolor sit amet, 
${indentation}consectetur adipiscing 
${indentation}    elit`,
        ],
      ])("%s", (_desc, text, expectation) => {
        expect(indentText(text, indentNum, indentChar, skipFirstLine)).toBe(expectation);
      });
    });
  });
});
