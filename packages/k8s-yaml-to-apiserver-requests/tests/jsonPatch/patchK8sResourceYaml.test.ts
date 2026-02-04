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
  describe("Add Operations", () => {
    ADD_OPERATION_TEST_CASES.forEach(({ name, given, expected }) => {
      it(name, () => {
        const result = patchK8sResourceYaml(given.yaml, given.patches, given.tokenMap);

        if (Array.isArray(expected)) {
          expected.forEach((exp) => expect(result).toContain(exp));
        } else {
          expect(result).toContain(expected);
        }
      });
    });
  });

  describe("Replace Operations", () => {
    REPLACE_OPERATION_TEST_CASES.forEach(({ name, given, expected }) => {
      it(name, () => {
        const result = patchK8sResourceYaml(given.yaml, given.patches, given.tokenMap);

        if (Array.isArray(expected)) {
          expected.forEach((exp) => expect(result).toContain(exp));
        } else {
          expect(result).toContain(expected);
        }
      });
    });
  });

  describe("Remove Operations", () => {
    REMOVE_OPERATION_TEST_CASES.forEach(({ name, given, notExpected }) => {
      it(name, () => {
        const result = patchK8sResourceYaml(given.yaml, given.patches, given.tokenMap);

        if (Array.isArray(notExpected)) {
          notExpected.forEach((exp) => expect(result).not.toContain(exp));
        } else {
          expect(result).not.toContain(notExpected);
        }
      });
    });
  });

  describe("Test Operations with ResourcePatch", () => {
    TEST_OPERATION_TEST_CASES.forEach(({ name, given, expected }) => {
      it(name, () => {
        const result = patchK8sResourceYaml(given.yaml, given.patches, given.tokenMap);

        if (Array.isArray(expected)) {
          expected.forEach((exp) => expect(result).toContain(exp));
        } else {
          expect(result).toContain(expected);
        }
      });
    });
  });

  describe("Multiple ResourcePatches", () => {
    MULTIPLE_PATCHES_TEST_CASES.forEach(({ name, given, expected }) => {
      it(name, () => {
        const result = patchK8sResourceYaml(given.yaml, given.patches, given.tokenMap);

        if (Array.isArray(expected)) {
          expected.forEach((exp) => expect(result).toContain(exp));
        } else {
          expect(result).toContain(expected);
        }
      });
    });
  });

  describe("Edge Cases", () => {
    EDGE_CASE_TEST_CASES.forEach(({ name, given, expected }) => {
      it(name, () => {
        const result = patchK8sResourceYaml(given.yaml, given.patches, given.tokenMap);

        if (Array.isArray(expected)) {
          expected.forEach((exp) => expect(result).toContain(exp));
        } else {
          expect(result).toContain(expected);
        }
      });
    });
  });

  describe("Token Interpolation in Patches", () => {
    TOKEN_INTERPOLATION_TEST_CASES.forEach(({ name, given, expected }) => {
      it(name, () => {
        const result = patchK8sResourceYaml(given.yaml, given.patches, given.tokenMap);

        if (Array.isArray(expected)) {
          expected.forEach((exp) => expect(result).toContain(exp));
        } else {
          expect(result).toContain(expected);
        }
      });
    });
  });
});
