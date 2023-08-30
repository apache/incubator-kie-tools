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

import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/dmn-marshaller";

const files = [
  { path: "../tests-data--manual/other/attachment.dmn", version: "1.2" },
  { path: "../tests-data--manual/other/empty13.dmn", version: "1.3" },
  { path: "../tests-data--manual/other/sample12.dmn", version: "1.2" },
  { path: "../tests-data--manual/other/weird.dmn", version: "1.2" },
  {
    path: "../tests-data--manual/dmn-1_4--examples/Chapter 11 Example 1 Originations/Chapter 11 Example.dmn",
    version: "1.4",
  },
];

describe("versions", () => {
  for (const file of files) {
    test(path.basename(file.path), () => {
      const xml = fs.readFileSync(path.join(__dirname, file.path), "utf-8");
      const { version } = getMarshaller(xml);
      expect(version).toStrictEqual(file.version);
    });
  }
});
