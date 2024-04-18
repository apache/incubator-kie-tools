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

const dmnTestingModels = path.dirname(require.resolve("@kie-tools/dmn-testing-models/package.json"));

const files = [
  "../tests-data--manual/other/attachment.dmn",
  "../tests-data--manual/other/empty13.dmn",
  "../tests-data--manual/other/sample12.dmn",
  "../tests-data--manual/other/list.dmn",
  "../tests-data--manual/other/list2.dmn",
  "../tests-data--manual/other/external.dmn",
  "../tests-data--manual/other/weird.dmn",
  "../tests-data--manual/dmn-1_4--examples/Chapter 11 Example 1 Originations/Chapter 11 Example.dmn",
];

const testing_models_paths = ["dist/valid_models/DMNv1_5/", "dist/valid_models/DMNv1_x/"];

describe("idempotency", () => {
  for (const file of files) {
    testFile(path.join(__dirname, file));
  }
  for (const models_paths of testing_models_paths) {
    const parentPath = path.join(dmnTestingModels, models_paths);
    testDirectory(parentPath);
  }
});

function testDirectory(normalizedFsPathRelativeToTheDirectory: string) {
  fs.readdirSync(normalizedFsPathRelativeToTheDirectory).forEach((file) => {
    const childPath = path.join(normalizedFsPathRelativeToTheDirectory, file);
    const stats = fs.statSync(childPath);
    if (stats.isFile()) {
      testFile(childPath);
    } else {
      testDirectory(childPath);
    }
  });
}

function testFile(normalizedFsPathRelativeToTheFile: string) {
  test(normalizedFsPathRelativeToTheFile.substring(normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1), () => {
    const xmlOriginal = fs.readFileSync(normalizedFsPathRelativeToTheFile, "utf-8");

    const { parser, builder } = getMarshaller(xmlOriginal, { upgradeTo: "latest" });
    const json = parser.parse();

    const xmlFirstPass = builder.build(json);
    const xmlSecondPass = builder.build(getMarshaller(xmlFirstPass, { upgradeTo: "latest" }).parser.parse());

    expect(xmlFirstPass).toStrictEqual(xmlSecondPass);
  });
}
