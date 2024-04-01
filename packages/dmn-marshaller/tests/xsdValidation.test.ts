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
import * as validator from "xsd-schema-validator";

/* Local Directories */
const LOCAL_MODELS_DIRECTORY = "tests-data--manual";
const LOCAL_MODELS_1_4_DIRECTORY = "dmn-1_4--examples";
const LOCAL_MODELS_OTHER_DIRECTORY = "other";

/* dmn-testing-models module Directories */
const VALID_MODELS_DIRECTORY = "valid_models";
const DMN_1_5_DIRECTORY = "DMNv1_5";
const DMN_1_x_DIRECTORY = "DMNv1_X";
const dmnTestingModels = require.resolve("@kie-tools/dmn-testing-models");

/* XSD DMN Schema */
const DMN_1_5_XSD = "../src/schemas/dmn-1_5/DMN15.xsd";

const files = [
  ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY + path.sep + "DateToDateTimeFunction.dmn",
];

const testing_models_paths = [
  ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY,
  ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_x_DIRECTORY,
];

describe("validation", () => {
  for (const file of files) {
    testFile(path.join(dmnTestingModels, file));
  }
  // for (const models_paths of testing_models_paths) {
  //   const parent_path = path.join(dmnTestingModels, models_paths);
  //   testDirectory(parent_path);
  // }
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
  test(
    normalizedFsPathRelativeToTheFile.substring(normalizedFsPathRelativeToTheFile.lastIndexOf("/") + 1),
    async () => {
      const xml_original = fs.readFileSync(normalizedFsPathRelativeToTheFile, "utf-8");
      const { parser, builder } = getMarshaller(xml_original, { upgradeTo: "latest" });
      const xml_marshalled = builder.build(parser.parse());

      expect(xml_marshalled).toStrictEqual(xml_original);

      await expect((await validator.validateXML(xml_marshalled, path.join(__dirname, DMN_1_5_XSD))).valid).toBe(true);
    }
  );
}
