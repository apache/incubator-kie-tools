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
import { interpolateK8sResourceYaml } from "../../src/interpolateK8sResourceYaml";
import {
  BASIC_TOKEN_TEST_CASES,
  JSON_PATH_TEST_CASES,
  RECURSIVE_TOKEN_TEST_CASES,
  MAX_DEPTH_TEST_CASE,
  EDGE_CASE_TEST_CASES,
  UNRESOLVABLE_TOKEN_TEST_CASE,
} from "./fixtures";

describe("Token Interpolation", () => {
  describe.each([
    ["Basic Token Interpolation", BASIC_TOKEN_TEST_CASES],
    ["JSON Path Token Interpolation", JSON_PATH_TEST_CASES],
    ["Recursive Token Interpolation", RECURSIVE_TOKEN_TEST_CASES],
    ["Edge Cases", EDGE_CASE_TEST_CASES],
  ])("%s", (_suiteName, testCases) => {
    testCases.forEach(({ name, given, expected }) => {
      it(name, () => {
        const result = interpolateK8sResourceYaml(given.yaml, given.tokenMap);
        const resultParsed = yamlLoad(result);
        const expectedParsed = yamlLoad(expected);
        expect(resultParsed).toEqual(expectedParsed);
      });
    });
  });

  describe("Error Cases", () => {
    it(MAX_DEPTH_TEST_CASE.name, () => {
      expect(() =>
        interpolateK8sResourceYaml(MAX_DEPTH_TEST_CASE.given.yaml, MAX_DEPTH_TEST_CASE.given.tokenMap)
      ).toThrow();
    });

    it(UNRESOLVABLE_TOKEN_TEST_CASE.name, () => {
      expect(() =>
        interpolateK8sResourceYaml(UNRESOLVABLE_TOKEN_TEST_CASE.given.yaml, UNRESOLVABLE_TOKEN_TEST_CASE.given.tokenMap)
      ).toThrow();
    });
  });
});
