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
} from "./testConstants";

/**
 * This test suite validates the xml produced by the marshalle relying on KIE DMN Validator
 * (https://github.com/apache/incubator-kie-drools/tree/main/kie-dmn/kie-dmn-validation).
 * A JBang script is used to actually call the KIE DMN Validator Java code.
 */

const dmnTestingModelsPath = require.resolve("@kie-tools/dmn-testing-models");

const dmnTestingModels = [
  FULL_1_5_DIRECTORY + "AllowedValuesChecksInsideCollection.dmn",
  FULL_1_5_DIRECTORY + "DateToDateTimeFunction.dmn",
  FULL_1_5_DIRECTORY + "ForLoopDatesEvaluate.dmn",
  FULL_1_5_DIRECTORY + "ListReplaceEvaluate.dmn",
  FULL_1_5_DIRECTORY + "Imported_Model_Unamed.dmn",
  FULL_1_5_DIRECTORY + "NegationOfDurationEvaluate.dmn",
  FULL_1_5_DIRECTORY + "TypeConstraintsChecks.dmn",
  FULL_1_x_MULTIPLE_DIRECTORY + "Financial.dmn",
  //FULL_1_x_MULTIPLE_DIRECTORY + "Imported_Traffic_Violation.dmn",
  FULL_1_x_MULTIPLE_DIRECTORY + "stdlib.dmn",
  FULL_1_x_DIRECTORY + "allTypes.dmn",
  //FULL_1_x_DIRECTORY + "dtevent.dmn",
  //FULL_1_x_DIRECTORY + "habitability.dmn",
  FULL_1_x_DIRECTORY + "loan.dmn",
  //FULL_1_x_DIRECTORY + "LoanEligibility.dmn",
  FULL_1_x_DIRECTORY + "OneOfEachType.dmn",
  //FULL_1_x_DIRECTORY + "Prequalification.dmn",
  FULL_1_x_DIRECTORY + "testWithExtensionElements.dmn",
  //FULL_1_x_DIRECTORY + "Traffic Violation Simple.dmn",
];

const dmnTestingImportedModels = [
  {
    imported: FULL_1_5_DIRECTORY + "Imported_Model_Unamed.dmn",
    importer: FULL_1_5_DIRECTORY + "Importing_EmptyNamed_Model.dmn",
  },
  {
    imported: FULL_1_5_DIRECTORY + "Imported_Model_Unamed.dmn",
    importer: FULL_1_5_DIRECTORY + "Importing_Named_Model.dmn",
  },
  {
    imported: FULL_1_5_DIRECTORY + "Imported_Model_Unamed.dmn",
    importer: FULL_1_5_DIRECTORY + "Importing_OverridingEmptyNamed_Model.dmn",
  },
  // {
  //   imported: FULL_1_x_MULTIPLE_DIRECTORY + "Imported_Traffic_Violation.dmn",
  //   importer: FULL_1_x_MULTIPLE_DIRECTORY + "Traffic Violation With Import.dmn",
  // },
];

describe("validation", () => {
  for (const file of dmnTestingModels) {
    testFile(path.join(dmnTestingModelsPath, file));
  }
  for (const file of dmnTestingImportedModels) {
    file.imported = path.join(dmnTestingModelsPath, file.imported);
    file.importer = path.join(dmnTestingModelsPath, file.importer);
    testImportedFile(file);
  }
});

function testFile(normalizedFsPathRelativeToTheFile: string) {
  test(normalizedFsPathRelativeToTheFile.substring(normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1), () => {
    const processedDMN = parseXML(normalizedFsPathRelativeToTheFile);

    try {
      executeJbangScript(JBANG_DMN_VALIDATION_SCRIPT_PATH, processedDMN.marshalledXMLFilePath);
    } catch (error) {
      const fileName = normalizedFsPathRelativeToTheFile.substring(
        normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1
      );
      fail("Validation of " + fileName + " failed! Please scroll up to DMN VALIDATION ERROR message to see the reason");
    }
  });
}

function testImportedFile(normalizedFsPathRelativeToTheFiles: { imported: string; importer: string }) {
  test(
    normalizedFsPathRelativeToTheFiles.importer.substring(
      normalizedFsPathRelativeToTheFiles.importer.lastIndexOf(path.sep) + 1
    ),
    () => {
      const imported = parseXML(normalizedFsPathRelativeToTheFiles.imported);
      const importer = parseXML(normalizedFsPathRelativeToTheFiles.importer);

      try {
        executeJbangScript(
          JBANG_DMN_VALIDATION_SCRIPT_PATH,
          importer.marshalledXMLFilePath,
          imported.marshalledXMLFilePath
        );
      } catch (error) {
        const fileName = normalizedFsPathRelativeToTheFiles.importer.substring(
          normalizedFsPathRelativeToTheFiles.importer.lastIndexOf(path.sep) + 1
        );
        fail(
          "Validation of " + fileName + " failed! Please scroll up to DMN VALIDATION ERROR message to see the reason"
        );
      }
    }
  );
}

function parseXML(normalizedFsPathRelativeToTheFile: string): {
  originalXML: string;
  marshalledXML: string;
  marshalledXMLFilePath: string;
} {
  const originalXML = fs.readFileSync(normalizedFsPathRelativeToTheFile, "utf-8");
  const { parser, builder } = getMarshaller(originalXML, { upgradeTo: "latest" });
  const marshalledXML = builder.build(parser.parse());
  const fileName = normalizedFsPathRelativeToTheFile.substring(
    normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1
  );
  const filetowrite = __dirname + path.sep + ".." + path.sep + "dist-tests" + path.sep + fileName;
  fs.writeFileSync(filetowrite, marshalledXML);
  return { originalXML: originalXML, marshalledXML: marshalledXML, marshalledXMLFilePath: filetowrite };
}
