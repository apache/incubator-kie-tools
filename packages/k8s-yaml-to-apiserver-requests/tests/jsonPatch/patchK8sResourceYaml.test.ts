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

import { load as yamlLoad } from "js-yaml";
import { patchK8sResourceYaml } from "../../src/patchK8sResourceYaml";
import {
  ADD_OPERATION_TEST_CASES,
  REPLACE_OPERATION_TEST_CASES,
  REMOVE_OPERATION_TEST_CASES,
  TEST_OPERATION_TEST_CASES,
  MULTIPLE_PATCHES_TEST_CASES,
  EDGE_CASE_TEST_CASES,
  TOKEN_INTERPOLATION_TEST_CASES,
} from "./fixtures";

describe("JSON Patch Operations", () => {
  describe.each([
    ["Add Operations", ADD_OPERATION_TEST_CASES],
    ["Replace Operations", REPLACE_OPERATION_TEST_CASES],
    ["Remove Operations", REMOVE_OPERATION_TEST_CASES],
    ["Test Operations with ResourcePatch", TEST_OPERATION_TEST_CASES],
    ["Multiple ResourcePatches", MULTIPLE_PATCHES_TEST_CASES],
    ["Edge Cases", EDGE_CASE_TEST_CASES],
    ["Token Interpolation in Patches", TOKEN_INTERPOLATION_TEST_CASES],
  ])("%s", (_suiteName, testCases) => {
    testCases.forEach(({ name, given, expected }) => {
      it(name, () => {
        const result = patchK8sResourceYaml(given.yaml, given.patches, given.tokenMap);
        const resultParsed = yamlLoad(result);
        const expectedParsed = yamlLoad(expected);
        expect(resultParsed).toEqual(expectedParsed);
      });
    });
  });
});
