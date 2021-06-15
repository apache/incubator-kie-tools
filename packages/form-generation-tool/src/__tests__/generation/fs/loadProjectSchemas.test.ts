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

import { loadProjectSchemas } from "../../../generation/fs";
import {
  ERROR_INVALID_FOLDER,
  ERROR_NOT_DIRECTORY,
  ERROR_NOT_MVN_PROJECT,
} from "../../../generation/fs/loadProjectSchemas";
import { FormSchema } from "../../../generation/types";

describe("loadProjectSchemas tests", () => {
  it("Load with invalid project path", () => {
    expect(() => loadProjectSchemas(`${__dirname}/resources/invalid`)).toThrowError(ERROR_INVALID_FOLDER);
  });

  it("Load with non mvn project path", () => {
    expect(() => loadProjectSchemas(`${__dirname}/resources`)).toThrowError(ERROR_NOT_MVN_PROJECT);
  });

  it("Load with file path", () => {
    expect(() => loadProjectSchemas(`${__dirname}/resources/file.txt`)).toThrowError(ERROR_NOT_DIRECTORY);
  });

  it("Load project without schemas", () => {
    expect(() => loadProjectSchemas(`${__dirname}/resources/empty`)).toHaveLength(0);
  });

  it("Load project with schemas", () => {
    const schemas: FormSchema[] = loadProjectSchemas(`${__dirname}/resources/full`);

    expect(schemas).toHaveLength(2);

    expect(schemas[0].name).toBe("travels_ApplyForVisa");
    expect(schemas[1].name).toBe("travels_ConfirmTravel");
  });
});
