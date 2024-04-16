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
import {
  DmnBackendCompatibilityScript,
  executeJBangScript,
} from "@kie-tools/dmn-marshaller-backend-compatibility-tester";

/**
 * This test suite compares the xml generated (parsed and built) by the dmn-parser with the original xml.
 * The original xml and the generated one are passed and compered to the KIE DMN Core backend API.
 * A JBang script is used to actually call the KIE DMN Core backend API.
 */

const dmnTestingModelsPath = require.resolve("@kie-tools/dmn-testing-models");

const dmnTestingModels = [
  "../valid_models/DMNv1_5/AllowedValuesChecksInsideCollection.dmn",
  "../valid_models/DMNv1_5/DateToDateTimeFunction.dmn",
  "../valid_models/DMNv1_5/ForLoopDatesEvaluate.dmn",
  "../valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
  "../valid_models/DMNv1_5/ListReplaceEvaluate.dmn",
  "../valid_models/DMNv1_5/NegationOfDurationEvaluate.dmn",
  "../valid_models/DMNv1_5/TypeConstraintsChecks.dmn",
  "../valid_models/DMNv1_x/multiple/Financial.dmn",
  "../valid_models/DMNv1_x/multiple/Imported_Traffic_Violation.dmn",
  "../valid_models/DMNv1_x/multiple/stdlib.dmn",
  "../valid_models/DMNv1_x/allTypes.dmn",
  "../valid_models/DMNv1_x/dtevent.dmn",
  "../valid_models/DMNv1_x/habitability.dmn",
  "../valid_models/DMNv1_x/loan.dmn",
  "../valid_models/DMNv1_x/LoanEligibility.dmn",
  "../valid_models/DMNv1_x/OneOfEachType.dmn",
  "../valid_models/DMNv1_x/Prequalification.dmn",
  "../valid_models/DMNv1_x/testWithExtensionElements.dmn",
  "../valid_models/DMNv1_x/Traffic Violation Simple.dmn",
  "../valid_models/DMNv1_x/Traffic Violation.dmn",
];

const dmnTestingImportedModels = [
  {
    imported: "../valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
    importer: "../valid_models/DMNv1_5/Importing_EmptyNamed_Model_With_Href_Namespace.dmn",
  },
  {
    imported: "../valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
    importer: "../valid_models/DMNv1_5/Importing_EmptyNamed_Model_Without_Href_Namespace.dmn",
  },
  {
    imported: "../valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
    importer: "../valid_models/DMNv1_5/Importing_Named_Model.dmn",
  },
  {
    imported: "../valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
    importer: "../valid_models/DMNv1_5/Importing_OverridingEmptyNamed_Model.dmn",
  },
  {
    imported: "../valid_models/DMNv1_x/multiple/Imported_Traffic_Violation.dmn",
    importer: "../valid_models/DMNv1_x/multiple/Traffic Violation With Import.dmn",
  },
];

export const dmnSemanticComparisonGeneratedFilesDirectory = path.join(
  __dirname,
  "../../dist-tests/dmnSemanticComparison-generated-files"
);

describe("JBang Scripts Test Suite", () => {
  beforeAll(() => {
    if (fs.existsSync(dmnSemanticComparisonGeneratedFilesDirectory)) {
      fs.rmSync(dmnSemanticComparisonGeneratedFilesDirectory, { recursive: true });
    }
    fs.mkdirSync(dmnSemanticComparisonGeneratedFilesDirectory, { recursive: true });
  });

  executeSemanticComparisonTests();
});

export function executeSemanticComparisonTests() {
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
    "DMN Semantic Comparison: " +
      normalizedFsPathRelativeToTheFile.substring(normalizedFsPathRelativeToTheFile.lastIndexOf(path.sep) + 1),
    () => {
      const generatedXMLFilePath = parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFile);

      try {
        executeJBangScript(
          DmnBackendCompatibilityScript.DMN_SEMANTIC_COMPARISON,
          "--command=no_imports",
          "--originalDmnFilePath=" + normalizedFsPathRelativeToTheFile,
          "--generatedDmnFilePath=" + generatedXMLFilePath
        );
      } catch (error) {
        fail(error.cause);
      }
    }
  );
}

function testImportedFile(normalizedFsPathRelativeToTheFiles: { imported: string; importer: string }) {
  test(
    "DMN Semantic Comparison: " +
      normalizedFsPathRelativeToTheFiles.importer.substring(
        normalizedFsPathRelativeToTheFiles.importer.lastIndexOf(path.sep) + 1
      ),
    () => {
      const importedGeneratedXMLFilePath = parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFiles.imported);
      const importerGeneratedXMLFilePath = parseXMLAndWriteInFile(normalizedFsPathRelativeToTheFiles.importer);

      try {
        executeJBangScript(
          DmnBackendCompatibilityScript.DMN_SEMANTIC_COMPARISON,
          "--command=with_imports",
          "--generatedDmnFilePath=" + importerGeneratedXMLFilePath,
          "--importedGeneratedDmnFilesPaths=" + importedGeneratedXMLFilePath,
          "--originalDmnFilePath=" + normalizedFsPathRelativeToTheFiles.importer,
          "--importedOriginalDmnFilesPaths=" + normalizedFsPathRelativeToTheFiles.imported
        );
      } catch (error) {
        fail(error.cause);
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
  const generatedXMLFilePath = dmnSemanticComparisonGeneratedFilesDirectory + path.sep + fileName;
  fs.writeFileSync(generatedXMLFilePath, generatedXML, "utf-8");
  return generatedXMLFilePath;
}
