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

import * as fs from "fs";
import { FormAsset, FormAssetType } from "../../../generation/types";
import { storeFormAsset } from "../../../generation/fs";
import { getFormAssetPath, getFormConfigAssetPath } from "../../../generation/fs/storeFormAsset";
import { PatternflyFormConfig } from "../../../generation/tools/uniforms/patternfly/PatternflyFormGenerationTool";

jest.mock("fs");

describe("storeFormAssets tests", () => {
  const mockFs = fs as jest.Mocked<typeof fs>;

  const fsRmSyncMock = jest.fn();
  const fsMkDirSyncMock = jest.fn();
  const fsWriteFileSyncMock = jest.fn();
  const fsReaddirSyncMock = jest.fn();

  mockFs.readdirSync.mockImplementation(fsReaddirSyncMock);
  mockFs.rmSync.mockImplementation(fsRmSyncMock);
  mockFs.mkdirSync.mockImplementation(fsMkDirSyncMock);
  mockFs.writeFileSync.mockImplementation(fsWriteFileSyncMock);

  const sourcePath = "/a/test/path";
  const formAsset: FormAsset = {
    id: "test",
    sanitizedId: "test",
    assetName: "test.tsx",
    sanitizedAssetName: "test.tsx",
    type: FormAssetType.TSX,
    content: "content",
    config: new PatternflyFormConfig({}),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("Store existing asset without overwrite", () => {
    mockFs.existsSync.mockReturnValue(true);
    fsReaddirSyncMock.mockReturnValue(["test.tsx", "test.config"]);

    expect(() => storeFormAsset(formAsset, sourcePath, false)).toThrow(`Form already exists.`);
  });

  it("Store existing asset with overwrite", () => {
    mockFs.existsSync.mockReturnValue(true);
    fsReaddirSyncMock.mockReturnValue(["test.tsx", "test.config"]);

    expect(() => storeFormAsset(formAsset, sourcePath, true)).not.toThrow();
    expect(fsRmSyncMock).toHaveBeenCalledTimes(2);
    expect(fsMkDirSyncMock).not.toHaveBeenCalled();
    expect(fsWriteFileSyncMock).toHaveBeenCalledTimes(2);

    expect(fsWriteFileSyncMock.mock.calls[0][0]).toStrictEqual(getFormAssetPath(sourcePath, formAsset.assetName));
    expect(fsWriteFileSyncMock.mock.calls[0][1]).toStrictEqual(formAsset.content);

    expect(fsWriteFileSyncMock.mock.calls[1][0]).toStrictEqual(getFormConfigAssetPath(sourcePath, formAsset));
    expect(fsWriteFileSyncMock.mock.calls[1][1]).toStrictEqual(JSON.stringify(new PatternflyFormConfig({}), null, 4));
  });

  it("Store asset", () => {
    mockFs.existsSync.mockImplementation(() => false);
    fsReaddirSyncMock.mockReturnValue([]);

    expect(() => storeFormAsset(formAsset, sourcePath, true)).not.toThrow();
    expect(fsMkDirSyncMock).toHaveBeenCalled();
    expect(fsRmSyncMock).not.toHaveBeenCalled();

    expect(fsWriteFileSyncMock).toHaveBeenCalledTimes(2);

    expect(fsWriteFileSyncMock.mock.calls[0][0]).toStrictEqual(getFormAssetPath(sourcePath, formAsset.assetName));
    expect(fsWriteFileSyncMock.mock.calls[0][1]).toStrictEqual(formAsset.content);

    expect(fsWriteFileSyncMock.mock.calls[1][0]).toStrictEqual(getFormConfigAssetPath(sourcePath, formAsset));
    expect(fsWriteFileSyncMock.mock.calls[1][1]).toStrictEqual(JSON.stringify(new PatternflyFormConfig({}), null, 4));
  });
});
