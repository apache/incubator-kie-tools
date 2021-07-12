/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import fs from "fs";
import path from "path";
import { ERROR_INVALID_FOLDER, ERROR_NOT_DIRECTORY, ERROR_NOT_MVN_PROJECT } from "./loadProjectSchemas";
import { FORM_STORAGE_FOLDER } from "./storeFormAsset";

export function checkKogitoProjectStructure(projectPath: string) {
  if (!fs.existsSync(projectPath)) {
    throw new Error(ERROR_INVALID_FOLDER);
  }

  const sourceStat = fs.statSync(projectPath);

  if (!sourceStat.isDirectory()) {
    throw new Error(ERROR_NOT_DIRECTORY);
  }

  if (!fs.existsSync(`${projectPath}/pom.xml`)) {
    throw new Error(ERROR_NOT_MVN_PROJECT);
  }
}

export function checkKogitoProjectHasForms(projectPath: string) {
  const formsPath = path.join(projectPath, FORM_STORAGE_FOLDER);

  return fs.existsSync(formsPath);
}
