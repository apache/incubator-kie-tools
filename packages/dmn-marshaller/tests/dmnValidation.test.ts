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
import { fail } from "assert";
import { executeJbangScript } from "./jbang/jbangManager";
import {
  FULL_1_5_DIRECTORY,
  FULL_1_x_DIRECTORY,
  FULL_1_x_MULTIPLE_DIRECTORY,
  JBANG_DMN_VALIDATION_SCRIPT_PATH,
  LOCAL_MODELS_OTHER_DIRECTORY,
  LOCAL_MODELS_OTHER_DIRECTORY_FULL_PATH,
} from "./testConstants";

const dmnTestingModelsPath = require.resolve("@kie-tools/dmn-testing-models");

const dmnTestingModels = [
  FULL_1_5_DIRECTORY + "AllowedValuesChecksInsideCollection.dmn",
  FULL_1_5_DIRECTORY + "DateToDateTimeFunction.dmn",
  FULL_1_5_DIRECTORY + "ForLoopDatesEvaluate.dmn",
  FULL_1_5_DIRECTORY + "ListReplaceEvaluate.dmn",
  FULL_1_5_DIRECTORY + "NegationOfDurationEvaluate.dmn",
  FULL_1_5_DIRECTORY + "TypeConstraintsChecks.dmn",
  FULL_1_x_MULTIPLE_DIRECTORY + "Financial.dmn",
  FULL_1_x_MULTIPLE_DIRECTORY + "stdlib.dmn",
  FULL_1_x_DIRECTORY + "allTypes.dmn",
  FULL_1_x_DIRECTORY + "loan.dmn",
  FULL_1_x_DIRECTORY + "OneOfEachType.dmn",
  FULL_1_x_DIRECTORY + "testWithExtensionElements.dmn",
];

describe("validation", () => {
  for (const file of dmnTestingModels) {
    testFile(path.join(dmnTestingModelsPath, file));
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
        executeJbangScript(JBANG_DMN_VALIDATION_SCRIPT_PATH, xml_marshalled);
      } catch (error) {
        fail("An error occured");
      }
    }
  );
}
