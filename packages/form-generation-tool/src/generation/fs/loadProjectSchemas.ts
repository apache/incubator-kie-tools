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
import { FormSchema } from "../types";
import path from "path";
import { checkKogitoProjectStructure } from "./checks";

const JSON_SCHEMA_PATH = "target/classes/META-INF/jsonSchema";
const JSON_SCHEMA_EXTENSION = ".json";

export const ERROR_INVALID_FOLDER = "Path doesn't exist";
export const ERROR_NOT_DIRECTORY = "Path isn't a directory";
export const ERROR_NOT_MVN_PROJECT = "Cannot find 'pom.xml' in source folder, are you sure it is a Kogito Project?";

function isValidFile(schemasPath: string, file: string): boolean {
  if (!file.endsWith(JSON_SCHEMA_EXTENSION)) {
    return false;
  }
  const stat = fs.statSync(`${schemasPath}/${file}`);
  return stat.isFile();
}

export function loadProjectSchemas(projectPath: string, jsonSchemaPath?: string): FormSchema[] {
  checkKogitoProjectStructure(projectPath);

  const schemasPath = `${projectPath}/${jsonSchemaPath || JSON_SCHEMA_PATH}`;

  if (!fs.existsSync(schemasPath)) {
    return [];
  }

  const files = fs.readdirSync(schemasPath);

  return files
    .filter((file) => isValidFile(schemasPath, file))
    .map((file) => {
      try {
        return {
          name: path.parse(file).name,
          schema: JSON.parse(fs.readFileSync(`${schemasPath}/${file}`, "utf8")),
        };
      } catch (err) {
        console.log(`Cannot load form content for "${file}":`, err.message);
      }
    })
    .filter((formSchema) => formSchema !== undefined) as FormSchema[];
}
