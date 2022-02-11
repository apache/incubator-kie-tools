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

import { lookupJSONLanguage } from "./json";
import { initYAMLLanguage } from "./yaml";
import { MonacoLanguage } from "./MonacoLanguage";

export { MonacoLanguage } from "./MonacoLanguage";

export function lookupLanguage(fileName: string): MonacoLanguage {
  if (fileName) {
    if (fileName.endsWith(".sw.json")) {
      return lookupJSONLanguage();
    }
    if (fileName.endsWith(".sw.yaml") || fileName.endsWith(".sw.yml")) {
      return initYAMLLanguage();
    }
  }
  throw new Error(`Cannot lookup language for path "${fileName}"`);
}
