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

import { patchK8sResourceYaml } from "../../src/patchK8sResourceYaml";
import { YAML_FIXTURES, JSON_PATCHES, RESOURCE_PATCHES, EXPECTED_RESULTS, TOKEN_MAPS } from "./fixtures";

describe("JSON Patch Operations", () => {
  describe("Add Operations", () => {
    it("should add a label to existing labels", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.deploymentWithLabels, [{ jsonPatches: JSON_PATCHES.addLabel }]);

      expect(result).toContain(EXPECTED_RESULTS.hasEnvironmentLabel);
    });

    it("should add multiple labels", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.deploymentWithLabels, [
        { jsonPatches: JSON_PATCHES.addMultipleLabels },
      ]);

      expect(result).toContain(EXPECTED_RESULTS.hasEnvironmentLabel);
      expect(result).toContain(EXPECTED_RESULTS.hasTeamLabel);
    });

    it("should add an annotation to existing annotations", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.deploymentWithAnnotations, [
        { jsonPatches: JSON_PATCHES.addAnnotation },
      ]);

      expect(result).toContain(EXPECTED_RESULTS.hasVersionAnnotation);
    });
  });

  describe("Replace Operations", () => {
    it("should replace replicas value", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.basicDeployment, [
        { jsonPatches: JSON_PATCHES.replaceReplicas },
      ]);

      expect(result).toContain(EXPECTED_RESULTS.hasThreeReplicas);
    });

    it("should replace metadata name", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.basicDeployment, [{ jsonPatches: JSON_PATCHES.replaceName }]);

      expect(result).toContain(EXPECTED_RESULTS.hasNewName);
    });
  });

  describe("Remove Operations", () => {
    it("should remove a label", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.deploymentWithLabels, [
        { jsonPatches: JSON_PATCHES.removeLabel },
      ]);

      expect(result).not.toContain(EXPECTED_RESULTS.hasAppLabel);
    });

    it("should remove an annotation", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.deploymentWithAnnotations, [
        { jsonPatches: JSON_PATCHES.removeAnnotation },
      ]);

      expect(result).not.toContain(EXPECTED_RESULTS.hasDescriptionAnnotation);
    });
  });

  describe("Test Operations with ResourcePatch", () => {
    it("should apply patch when labels are undefined", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.basicDeployment, [RESOURCE_PATCHES.addLabelsIfUndefined]);

      expect(result).toContain(EXPECTED_RESULTS.hasEmptyLabels);
    });

    it("should apply patch when labels are null", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.deploymentWithNullLabels, [RESOURCE_PATCHES.addLabelsIfNull]);

      expect(result).toContain(EXPECTED_RESULTS.hasEmptyLabels);
    });

    it("should not apply patch when test filter fails", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.deploymentWithLabels, [RESOURCE_PATCHES.addLabelsIfUndefined]);

      // Should still have the original labels, not replaced with empty object
      expect(result).toContain(EXPECTED_RESULTS.hasAppLabel);
    });

    it("should apply patch without test filters", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.deploymentWithLabels, [RESOURCE_PATCHES.addEnvironmentLabel]);

      expect(result).toContain(EXPECTED_RESULTS.hasEnvironmentLabel);
    });
  });

  describe("Multiple ResourcePatches", () => {
    it("should apply multiple resource patches in order", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.basicDeployment, [
        RESOURCE_PATCHES.addLabelsIfUndefined,
        RESOURCE_PATCHES.addEnvironmentLabel,
        RESOURCE_PATCHES.updateReplicas,
      ]);

      // First patch adds labels: {}, second patch adds environment label to it
      expect(result).toContain(EXPECTED_RESULTS.hasEnvironmentLabel);
      expect(result).toContain(EXPECTED_RESULTS.hasFiveReplicas);
      // Should have labels section (not empty after environment label is added)
      expect(result).toContain(EXPECTED_RESULTS.hasLabelsSection);
    });

    it("should skip patches when test filters fail", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.deploymentWithLabels, [
        RESOURCE_PATCHES.addLabelsIfUndefined, // Should skip - labels exist
        RESOURCE_PATCHES.addEnvironmentLabel, // Should apply
      ]);

      expect(result).toContain(EXPECTED_RESULTS.hasAppLabel); // Original label preserved
      expect(result).toContain(EXPECTED_RESULTS.hasEnvironmentLabel); // New label added
    });
  });

  describe("Edge Cases", () => {
    it("should handle empty patches array", () => {
      const result = patchK8sResourceYaml(YAML_FIXTURES.basicDeployment, []);

      expect(result).toContain(EXPECTED_RESULTS.hasMyAppName);
      expect(result).toContain(EXPECTED_RESULTS.hasOneReplica);
    });

    it("should silently skip invalid patch paths", () => {
      // Should not throw, just skip the invalid patch
      const result = patchK8sResourceYaml(YAML_FIXTURES.basicDeployment, [RESOURCE_PATCHES.invalidPath]);
      expect(result).toContain(EXPECTED_RESULTS.hasMyAppName);
      expect(result).toContain(EXPECTED_RESULTS.hasOneReplica);
    });
  });

  describe("Token Interpolation in Patches", () => {
    it("should interpolate tokens in patch values", () => {
      const result = patchK8sResourceYaml(
        YAML_FIXTURES.deploymentWithLabels,
        [RESOURCE_PATCHES.addLabelWithToken],
        TOKEN_MAPS.devDeploymentUniqueName
      );

      expect(result).toContain(EXPECTED_RESULTS.hasPartOfLabel);
    });
  });
});
