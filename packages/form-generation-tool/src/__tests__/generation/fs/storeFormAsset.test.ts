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
import { FormAsset, FormAssetType } from "../../../generation/types";
import { storeFormAsset } from "../../../generation/fs";
import { getFormAssetPath, getFormAssetStoragePath } from "../../../generation/fs/storeFormAsset";

jest.mock("fs");

describe("storeFormAssets tests", () => {
  const mockFs = fs as jest.Mocked<typeof fs>;

  const fsRmSyncMock = jest.fn();
  const fsMkDirSyncMock = jest.fn();
  const fsWriteFileSyncMock = jest.fn();

  mockFs.rmSync.mockImplementation(fsRmSyncMock);
  mockFs.mkdirSync.mockImplementation(fsMkDirSyncMock);
  mockFs.writeFileSync.mockImplementation(fsWriteFileSyncMock);

  const sourcePath = "/a/test/path";
  const formAsset: FormAsset = {
    id: "test",
    assetName: "test.tsx",
    type: FormAssetType.TSX,
    content: "content",
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("Store existing asset without overwrite", () => {
    mockFs.existsSync.mockImplementation(() => true);
    expect(() => storeFormAsset(formAsset, sourcePath, false)).toThrow(`Form already exists.`);
  });

  it("Store existing asset with overwrite", () => {
    mockFs.existsSync.mockImplementation(() => true);

    expect(() => storeFormAsset(formAsset, sourcePath, true)).not.toThrow();
    expect(fsRmSyncMock).toHaveBeenCalledWith(getFormAssetStoragePath(sourcePath, formAsset), { recursive: true });
    expect(fsMkDirSyncMock).toHaveBeenCalledWith(getFormAssetStoragePath(sourcePath, formAsset), { recursive: true });
    expect(fsWriteFileSyncMock).toHaveBeenCalledWith(getFormAssetPath(sourcePath, formAsset), formAsset.content);
  });

  it("Store asset", () => {
    mockFs.existsSync.mockImplementation(() => false);

    expect(() => storeFormAsset(formAsset, sourcePath, true)).not.toThrow();
    expect(fsRmSyncMock).not.toHaveBeenCalled();
    expect(fsMkDirSyncMock).toHaveBeenCalledWith(getFormAssetStoragePath(sourcePath, formAsset), { recursive: true });
    expect(fsWriteFileSyncMock).toHaveBeenCalledWith(getFormAssetPath(sourcePath, formAsset), formAsset.content);
  });
});
