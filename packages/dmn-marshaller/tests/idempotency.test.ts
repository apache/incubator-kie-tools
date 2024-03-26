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
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_5/AllowedValuesChecksInsideCollection.dmn",
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_5/DateToDateTimeFunction.dmn",
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_5/ForLoopDatesEvaluate.dmn",
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_5/Importing_EmptyNamed_Model.dmn",
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_5/ListReplaceEvaluate.dmn",
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_5/TypeConstraintsChecks.dmn",
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_x/OneOfEachType.dmn",
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_x/allTypes.dmn",
];

const testing_models_paths = [
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_5",
  "../node_modules/@kie-tools/dmn-testing-models/dist/valid_models/DMNv1_x",
];

describe("idempotency", () => {
  for (const file of files) {
    testFile(path.join(__dirname, file));
  }
  for (const models_paths of testing_models_paths) {
    const parent_path = path.join(__dirname, models_paths);
    testDirectory(parent_path);
  }
});

function testDirectory(fullPathOfModels: string) {
  fs.readdirSync(fullPathOfModels).forEach((file) => {
    const child_path = path.join(fullPathOfModels, file);
    const stats = fs.statSync(child_path);
    if (stats.isFile()) {
      testFile(child_path);
    } else {
      testDirectory(child_path);
    }
  });
}

function testFile(fullPathOfFile: string) {
  test(fullPathOfFile.substring(fullPathOfFile.lastIndexOf("/") + 1), () => {
    const xml_original = fs.readFileSync(fullPathOfFile, "utf-8");

    const { parser, builder } = getMarshaller(xml_original, { upgradeTo: "latest" });
    const json = parser.parse();

    const xml_firstPass = builder.build(json);
    const xml_secondPass = builder.build(getMarshaller(xml_firstPass, { upgradeTo: "latest" }).parser.parse());

    expect(xml_firstPass).toStrictEqual(xml_secondPass);
  });
}
