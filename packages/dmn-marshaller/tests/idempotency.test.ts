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
import {
  FULL_1_5_DIRECTORY,
  FULL_1_x_DIRECTORY,
  LOCAL_MODELS_1_4_DIRECTORY_FULL_PATH,
  LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH,
} from "./testConstants";

const dmnTestingModels = require.resolve("@kie-tools/dmn-testing-models");

const files = [
  LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH + "attachment.dmn",
  LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH + "empty13.dmn",
  LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH + "sample12.dmn",
  LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH + "list.dmn",
  LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH + "list2.dmn",
  LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH + "external.dmn",
  LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH + "weird.dmn",
  LOCAL_MODELS_1_4_DIRECTORY_FULL_PATH + "Chapter 11 Example 1 Originations/Chapter 11 Example.dmn",
];

const testing_models_paths = [FULL_1_5_DIRECTORY, FULL_1_x_DIRECTORY];

describe("idempotency", () => {
  for (const file of files) {
    testFile(path.join(__dirname, file));
  }
  for (const models_paths of testing_models_paths) {
    const parent_path = path.join(dmnTestingModels, models_paths);
    testDirectory(parent_path);
  }
});

function testDirectory(normalizedFsPathRelativeToTheDirectory: string) {
  fs.readdirSync(normalizedFsPathRelativeToTheDirectory).forEach((file) => {
    const child_path = path.join(normalizedFsPathRelativeToTheDirectory, file);
    const stats = fs.statSync(child_path);
    if (stats.isFile()) {
      testFile(child_path);
    } else {
      testDirectory(child_path);
    }
  });
}

function testFile(normalizedFsPathRelativeToTheFile: string) {
  test(normalizedFsPathRelativeToTheFile.substring(normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1), () => {
    const xml_original = fs.readFileSync(normalizedFsPathRelativeToTheFile, "utf-8");

    const { parser, builder } = getMarshaller(xml_original, { upgradeTo: "latest" });
    const json = parser.parse();

    const xml_firstPass = builder.build(json);
    const xml_secondPass = builder.build(getMarshaller(xml_firstPass, { upgradeTo: "latest" }).parser.parse());

    expect(xml_firstPass).toStrictEqual(xml_secondPass);
  });
}
