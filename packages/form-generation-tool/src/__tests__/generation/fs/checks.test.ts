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

import * as fs from "fs";
import { checkKogitoProjectHasForms, checkKogitoProjectStructure } from "../../../generation/fs";
import {
  ERROR_INVALID_FOLDER,
  ERROR_NOT_DIRECTORY,
  ERROR_NOT_MVN_PROJECT,
} from "../../../generation/fs/loadProjectSchemas";

jest.mock("fs");

describe("checks tests", function () {
  const mockFs = fs as jest.Mocked<typeof fs>;

  const fsExistsSyncMock = jest.fn();
  const fsStatSyncMock = jest.fn();

  mockFs.existsSync.mockImplementation(fsExistsSyncMock);
  mockFs.statSync.mockImplementation(fsStatSyncMock);

  const sourcePath = "/a/test/path";

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("Check Project structure with invalid path", () => {
    fsExistsSyncMock.mockReturnValueOnce(false);

    expect(() => checkKogitoProjectStructure(sourcePath)).toThrowError(ERROR_INVALID_FOLDER);
  });

  it("Check Project structure not a directory", () => {
    fsExistsSyncMock.mockReturnValueOnce(true);
    fsStatSyncMock.mockReturnValueOnce({
      isDirectory: () => false,
    });
    expect(() => checkKogitoProjectStructure(sourcePath)).toThrowError(ERROR_NOT_DIRECTORY);
  });

  it("Check Project structure not a mvn project", () => {
    fsExistsSyncMock.mockReturnValueOnce(true).mockReturnValueOnce(false);

    fsStatSyncMock.mockReturnValueOnce({
      isDirectory: () => true,
    });
    expect(() => checkKogitoProjectStructure(sourcePath)).toThrowError(ERROR_NOT_MVN_PROJECT);
  });

  it("Check Project contains forms", () => {
    fsExistsSyncMock.mockReturnValueOnce(true);

    expect(checkKogitoProjectHasForms(sourcePath)).toBeTruthy();
  });

  it("Check Project does not contain forms", () => {
    fsExistsSyncMock.mockReturnValueOnce(false);

    expect(checkKogitoProjectHasForms(sourcePath)).toBeFalsy();
  });
});
