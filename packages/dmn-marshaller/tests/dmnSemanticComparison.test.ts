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
import { executeJBangScript } from "./jbang/jbangManager";

/**
 * This test suite validates the xml produced by the marshaller relying on KIE DMN Validator
 * (https://github.com/apache/incubator-kie-drools/tree/main/kie-dmn/kie-dmn-validation).
 * A JBang script is used to actually call the KIE DMN Validator Java code.
 */

const dmnTestingModelsPath = require.resolve("@kie-tools/dmn-testing-models");

const dmnTestingModels = [
  "../valid_models/DMNv1_5/AllowedValuesChecksInsideCollection.dmn",
  "../valid_models/DMNv1_5/DateToDateTimeFunction.dmn",
  "../valid_models/DMNv1_5/ForLoopDatesEvaluate.dmn",
  "../valid_models/DMNv1_5/ListReplaceEvaluate.dmn",
  "../valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
  "../valid_models/DMNv1_5/NegationOfDurationEvaluate.dmn",
  "../valid_models/DMNv1_5/TypeConstraintsChecks.dmn",
  "../valid_models/DMNv1_x/multiple/Financial.dmn",
  //FULL_1_x_MULTIPLE_DIRECTORY + "Imported_Traffic_Violation.dmn",
  "../valid_models/DMNv1_x/multiple/stdlib.dmn",
  "../valid_models/DMNv1_x/allTypes.dmn",
  //FULL_1_x_DIRECTORY + "dtevent.dmn",
  //FULL_1_x_DIRECTORY + "habitability.dmn",
  "../valid_models/DMNv1_x/loan.dmn",
  //FULL_1_x_DIRECTORY + "LoanEligibility.dmn",
  "../valid_models/DMNv1_x/OneOfEachType.dmn",
  //FULL_1_x_DIRECTORY + "Prequalification.dmn",
  "../valid_models/DMNv1_x/testWithExtensionElements.dmn",
  //FULL_1_x_DIRECTORY + "Traffic Violation Simple.dmn",
];

const dmnTestingImportedModels = [
  {
    imported: "../valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
    importer: "../valid_models/DMNv1_5/Importing_EmptyNamed_Model.dmn",
  },
  {
    imported: "../valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
    importer: "../valid_models/DMNv1_5/Importing_Named_Model.dmn",
  },
  {
    imported: "../valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
    importer: "../valid_models/DMNv1_5/Importing_OverridingEmptyNamed_Model.dmn",
  },
  // {
  //   imported: FULL_1_x_MULTIPLE_DIRECTORY + "Imported_Traffic_Violation.dmn",
  //   importer: FULL_1_x_MULTIPLE_DIRECTORY + "Traffic Violation With Import.dmn",
  // },
];

const marshalledXMLDirectory = path.join(__dirname, "../dist-tests/dmnSemanticComparison-test-files");
const jbangDmnSemanticComparisonScriptPath = path.join(__dirname, "./jbang/DmnSemanticComparison.java");

describe("validation", () => {
  beforeAll(() => {
    if (fs.existsSync(marshalledXMLDirectory)) {
      fs.rmSync(marshalledXMLDirectory, { recursive: true });
    }
    fs.mkdirSync(marshalledXMLDirectory, { recursive: true });
  });

  for (const file of dmnTestingModels) {
    testFile(path.join(dmnTestingModelsPath, file));
  } /*
  for (const file of dmnTestingImportedModels) {
    file.imported = path.join(dmnTestingModelsPath, file.imported);
    file.importer = path.join(dmnTestingModelsPath, file.importer);
    testImportedFile(file);
  } */
});

function testFile(normalizedFsPathRelativeToTheFile: string) {
  test(normalizedFsPathRelativeToTheFile.substring(normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1), () => {
    const marshalledXMLFilePath = parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFile);

    try {
      executeJBangScript(
        jbangDmnSemanticComparisonScriptPath,
        "-o" + normalizedFsPathRelativeToTheFile,
        "-g" + marshalledXMLFilePath
      );
    } catch (error) {
      const fileName = normalizedFsPathRelativeToTheFile.substring(
        normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1
      );
      fail("Comparison of " + fileName + " failed! Please scroll up to catch the error root cause");
    }
  });
}

function testImportedFile(normalizedFsPathRelativeToTheFiles: { imported: string; importer: string }) {
  test(
    normalizedFsPathRelativeToTheFiles.importer.substring(
      normalizedFsPathRelativeToTheFiles.importer.lastIndexOf(path.sep) + 1
    ),
    () => {
      const importedMarshalledXMLFilePath = parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFiles.imported);
      const importerMarshalledXMLFilePath = parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFiles.importer);

      try {
        executeJBangScript(
          jbangDmnSemanticComparisonScriptPath,
          importerMarshalledXMLFilePath,
          importedMarshalledXMLFilePath,
          normalizedFsPathRelativeToTheFiles.importer,
          normalizedFsPathRelativeToTheFiles.imported
        );
      } catch (error) {
        const fileName = normalizedFsPathRelativeToTheFiles.importer.substring(
          normalizedFsPathRelativeToTheFiles.importer.lastIndexOf(path.sep) + 1
        );
        fail("Comparison of " + fileName + " failed! Please scroll up to catch the error root cause");
      }
    }
  );
}

function parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFile: string): string {
  const originalXML = fs.readFileSync(normalizedFsPathRelativeToTheFile, "utf-8");
  const { parser, builder } = getMarshaller(originalXML, { upgradeTo: "latest" });
  const marshalledXML = builder.build(parser.parse());
  const fileName = normalizedFsPathRelativeToTheFile.substring(
    normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1
  );
  const marshalledXMLFilePath = marshalledXMLDirectory + path.sep + fileName;
  fs.writeFileSync(marshalledXMLFilePath, marshalledXML, "utf-8");
  return marshalledXMLFilePath;
}
