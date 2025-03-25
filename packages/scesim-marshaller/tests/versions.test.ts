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
import { getMarshaller } from "@kie-tools/scesim-marshaller";

const files = [
  { path: "../tests-data--manual/dmn/collection.scesim", version: "1.8" },
  { path: "../tests-data--manual/dmn/expression.scesim", version: "1.8" },
  { path: "../tests-data--manual/dmn/imported.scesim", version: "1.8" },
  { path: "../tests-data--manual/dmn/simple.scesim", version: "1.8" },
  { path: "../tests-data--manual/dmn/simpleTypes.scesim", version: "1.8" },
  { path: "../tests-data--manual/dmn/TrafficViolationTest.scesim", version: "1.8" },
  { path: "../tests-data--manual/dmn/undefined.scesim", version: "1.8" },
  { path: "../tests-data--manual/rule/OldEnoughTest.scesim", version: "1.8" },
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
