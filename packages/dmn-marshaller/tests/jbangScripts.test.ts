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
import { dmnValidationGeneratedFilesDirectory, executeValidationTests } from "./jbang/dmnValidation";
import {
  dmnSemanticComparisonGeneratedFilesDirectory,
  executeSemanticComparisonTests,
} from "./jbang/dmnSemanticComparison";

/**
 * This tests suite manages all the JBang script-based tests. This is necessary to guarantee the sequential
 * execution of the JBang script. Running JBang scripts in parallel may lead to issues with JBang caching during the
 * dependencies dowmload.
 */

const generatedFilesDirectories = [dmnValidationGeneratedFilesDirectory, dmnSemanticComparisonGeneratedFilesDirectory];

describe("JBang Scripts Test Suite", () => {
  beforeAll(() => {
    generatedFilesDirectories.forEach((directory) => {
      if (fs.existsSync(directory)) {
        fs.rmSync(directory, { recursive: true });
      }
      fs.mkdirSync(directory, { recursive: true });
    });
  });

  executeValidationTests();
  executeSemanticComparisonTests();
});
