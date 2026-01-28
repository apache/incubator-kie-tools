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

import { interpolateK8sResourceYaml } from "../../src/interpolateK8sResourceYaml";
import { YAML_FIXTURES, TOKEN_MAPS, EXPECTED_RESULTS } from "./fixtures";

describe("Token Interpolation", () => {
  describe("Basic Token Interpolation", () => {
    it("should replace simple tokens", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.simpleToken, TOKEN_MAPS.simpleService);

      expect(result).toContain(EXPECTED_RESULTS.simpleService);
    });

    it("should replace multiple tokens", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.multipleTokens, TOKEN_MAPS.multipleValues);

      expect(result).toContain(EXPECTED_RESULTS.multipleTokensName);
      expect(result).toContain(EXPECTED_RESULTS.multipleTokensNamespace);
    });
  });

  describe("JSON Path Token Interpolation", () => {
    it("should resolve JSON Path expressions", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.jsonPathSimple, TOKEN_MAPS.deploymentSimple);

      expect(result).toContain(EXPECTED_RESULTS.jsonPathSimple);
    });

    it("should resolve nested JSON Path expressions", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.jsonPathNested, TOKEN_MAPS.deploymentNested);

      expect(result).toContain(EXPECTED_RESULTS.jsonPathNested);
    });

    it("should resolve array access in JSON Path", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.jsonPathArray, TOKEN_MAPS.deploymentsArray as any);

      expect(result).toContain(EXPECTED_RESULTS.jsonPathArray);
    });
  });

  describe("Recursive Token Interpolation", () => {
    it("should resolve nested tokens", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.recursiveSimple, TOKEN_MAPS.routeResources);

      expect(result).toContain(EXPECTED_RESULTS.recursiveSimple);
    });

    it("should resolve deeply nested tokens", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.recursiveDeeplyNested, TOKEN_MAPS.dynamicResources);

      expect(result).toContain(EXPECTED_RESULTS.recursiveDeeplyNested);
    });

    it("should throw error when max depth is exceeded", () => {
      // This should throw an error about max depth
      expect(() => interpolateK8sResourceYaml(YAML_FIXTURES.recursiveMaxDepth, TOKEN_MAPS.circularReference)).toThrow();
    });
  });

  describe("Edge Cases", () => {
    it("should handle empty token map", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.noTokens, TOKEN_MAPS.empty);

      expect(result).toContain(EXPECTED_RESULTS.noTokensName);
    });

    it("should handle YAML without tokens", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.noTokens, TOKEN_MAPS.unused);

      expect(result).toContain(EXPECTED_RESULTS.noTokensName);
      expect(result).toContain(EXPECTED_RESULTS.noTokensNamespace);
    });

    it("should throw error for unresolvable tokens", () => {
      expect(() => interpolateK8sResourceYaml(YAML_FIXTURES.unresolvableToken, TOKEN_MAPS.nonexistent)).toThrow();
    });

    it("should handle numeric values", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.numericValue, TOKEN_MAPS.numeric);

      expect(result).toContain(EXPECTED_RESULTS.numericValue);
    });

    it("should handle boolean values", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.booleanValue, TOKEN_MAPS.boolean);

      expect(result).toContain(EXPECTED_RESULTS.booleanValue);
    });

    it("should handle mixed flat and JSONPath tokens in same YAML", () => {
      const result = interpolateK8sResourceYaml(YAML_FIXTURES.mixedTokens, TOKEN_MAPS.mixed);

      expect(result).toContain(EXPECTED_RESULTS.mixedTokensName);
      expect(result).toContain(EXPECTED_RESULTS.mixedTokensNamespace);
      expect(result).toContain(EXPECTED_RESULTS.mixedTokensApp);
      expect(result).toContain(EXPECTED_RESULTS.mixedTokensVersion);
    });
  });
});
