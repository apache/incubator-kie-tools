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
import { executeJBangScript } from "./jbangManager";

/**
 * This test suite validates the xml produced (parsed and built) by the marshaller relying on KIE DMN Validator
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
export const dmnValidationGeneratedFilesDirectory = path.join(__dirname, "../dist-tests/dmnValidation-generated-files");
const scriptPath = path.join(__dirname, "./DmnValidation.java");

export function executeValidationTests() {
  for (const file of dmnTestingModels) {
    testFile(path.join(dmnTestingModelsPath, file));
  }
  for (const file of dmnTestingImportedModels) {
    file.imported = path.join(dmnTestingModelsPath, file.imported);
    file.importer = path.join(dmnTestingModelsPath, file.importer);
    testImportedFile(file);
  }
}

function testFile(normalizedFsPathRelativeToTheFile: string) {
  test(
    "DMN Validation: " +
      normalizedFsPathRelativeToTheFile.substring(normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1),
    () => {
      const generatedXMLFilePath = parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFile);

      try {
        executeJBangScript(scriptPath, "-d" + generatedXMLFilePath);
      } catch (error) {
        const fileName = normalizedFsPathRelativeToTheFile.substring(
          normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1
        );
        fail("Validation of " + fileName + " failed! Please scroll up the logs to see the reason.");
      }
    }
  );
}

function testImportedFile(normalizedFsPathRelativeToTheFiles: { imported: string; importer: string }) {
  test(
    "DMN Validation: " +
      normalizedFsPathRelativeToTheFiles.importer.substring(
        normalizedFsPathRelativeToTheFiles.importer.lastIndexOf(path.sep) + 1
      ),
    () => {
      const importedGeneratedXMLFilePath = parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFiles.imported);
      const importerGeneratedXMLFilePath = parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFiles.importer);

      try {
        executeJBangScript(scriptPath, "-d" + importedGeneratedXMLFilePath, "-i" + importerGeneratedXMLFilePath);
      } catch (error) {
        const fileName = normalizedFsPathRelativeToTheFiles.importer.substring(
          normalizedFsPathRelativeToTheFiles.importer.lastIndexOf(path.sep) + 1
        );
        fail("Validation of " + fileName + " failed! Please scroll up the logs to see the reason.");
      }
    }
  );
}

function parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFile: string): string {
  const originalXML = fs.readFileSync(normalizedFsPathRelativeToTheFile, "utf-8");
  const { parser, builder } = getMarshaller(originalXML, { upgradeTo: "latest" });
  const generatedXML = builder.build(parser.parse());
  const fileName = normalizedFsPathRelativeToTheFile.substring(
    normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1
  );
  const generatedXMLFilePath = dmnValidationGeneratedFilesDirectory + path.sep + fileName;
  fs.writeFileSync(generatedXMLFilePath, generatedXML, "utf-8");
  return generatedXMLFilePath;
}
