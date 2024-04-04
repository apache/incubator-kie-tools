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
import * as prettier from "prettier";
import { fail } from "assert";

const xmlPrettierPlugin = require("@prettier/plugin-xml");
const jbang = require("@jbangdev/jbang");

/* dmn-testing-models module Directories */
const VALID_MODELS_DIRECTORY = "valid_models";
const DMN_1_5_DIRECTORY = "DMNv1_5";
const dmnTestingModels = require.resolve("@kie-tools/dmn-testing-models");

const files = [
  ".." +
    path.sep +
    VALID_MODELS_DIRECTORY +
    path.sep +
    DMN_1_5_DIRECTORY +
    path.sep +
    "AllowedValuesChecksInsideCollection.dmn",
  ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY + path.sep + "DateToDateTimeFunction.dmn",
  ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY + path.sep + "ForLoopDatesEvaluate.dmn",
  ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY + path.sep + "ListReplaceEvaluate.dmn",
  ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY + path.sep + "NegationOfDurationEvaluate.dmn",
  ".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY + path.sep + "TypeConstraintsChecks.dmn",
];

const testing_models_paths = [".." + path.sep + VALID_MODELS_DIRECTORY + path.sep + DMN_1_5_DIRECTORY];

describe("validation", () => {
  for (const file of files) {
    testFile(path.join(dmnTestingModels, file));
  }
});

function testFile(normalizedFsPathRelativeToTheFile: string) {
  test(
    normalizedFsPathRelativeToTheFile.substring(normalizedFsPathRelativeToTheFile.lastIndexOf("/") + 1),
    async () => {
      const xml_original = fs.readFileSync(normalizedFsPathRelativeToTheFile, "utf-8");
      const { parser, builder } = getMarshaller(xml_original, { upgradeTo: "latest" });
      const xml_marshalled = builder.build(parser.parse());

      try {
        jbang.exec("--java 17", "properties@jbangdev", "java.version");
        jbang.exec("./tests/jbang_scripts/dmnValidation.java", "'" + xml_marshalled + "'");
      } catch (error) {
        fail("An error occured");
      }
    }
  );
}
