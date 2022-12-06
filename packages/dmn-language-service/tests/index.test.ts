/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { DmnLanguageService } from "../src";
import { readFileSync } from "fs";
import { resolve } from "path";

const tests = [
  { modelPath: "./fixtures/model.dmn", expected: ["recursive.dmn", "nested.dmn"] },
  { modelPath: "./fixtures/recursive.dmn", expected: ["nested.dmn"] },
  { modelPath: "./fixtures/nested.dmn", expected: [] },
];

describe("DmnLanguageService", () => {
  const service = new DmnLanguageService();

  tests.forEach(({ modelPath, expected }) => {
    it("getImportedModels", () => {
      const path = resolve(__dirname, modelPath);
      const file = readFileSync(path, "utf8");
      expect(service.getImportedModels(file)).toEqual(expected);
    });
  });
});
