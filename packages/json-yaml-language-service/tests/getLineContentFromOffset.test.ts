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

import { getLineContentFromOffset } from "@kie-tools/json-yaml-language-service/dist/channel";

describe("getLineContentFromOffset", () => {
  const lipsum = `Lorem ipsum dolor sit amet,
consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
sed diam voluptua.
At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,
no sea takimata sanctus est Lorem ipsum dolor sit amet.`;

  const windowsLipsum =
    "Lorem ipsum dolor sit amet,\r\nconsetetur sadipscing elitr,\r\nsed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\r\nsed diam voluptua.";

  it("With not valid inputs", () => {
    expect(getLineContentFromOffset("", 10)).toBe("");
    expect(getLineContentFromOffset("Lorem ipsum ", 100)).toBe("");
    expect(getLineContentFromOffset("Lorem ipsum ", 0)).toBe("");
  });

  it.each([
    ["first line", lipsum, 20, "Lorem ipsum dolor sit amet,"],
    ["3rd line", lipsum, 150, "sed diam voluptua."],
    ["last line", lipsum, 295, "no sea takimata sanctus est Lorem ipsum dolor sit amet."],
    [
      "with windows EOL",
      windowsLipsum,
      87,
      "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,",
    ],
  ])("%s", (_description, content, offset, expectation) => {
    expect(getLineContentFromOffset(content, offset)).toBe(expectation);
  });
});
