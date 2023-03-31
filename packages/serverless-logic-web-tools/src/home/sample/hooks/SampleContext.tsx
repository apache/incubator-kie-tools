/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { LfsStorageFile, LfsStorageService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsStorageService";
import { LfsFsCache } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsFsCache";
import { LocalFile } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/LocalFile";
import * as React from "react";
import { useContext, useMemo, useCallback } from "react";
import { useSettingsDispatch } from "../../../settings/SettingsContext";
import { fetchSampleDefinitions, fetchSampleFiles, Sample } from "../sampleApi";
import { decoder, encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";

const SAMPLE_DEFINITIONS_CACHE_FILE_PATH = "/definitions.json";
const SAMPLES_FS_MOUNT_POINT = `lfs_v1__samples__${process.env.WEBPACK_REPLACE__version!}`;

export interface SampleDispatchContextType {
  getSamples(): Promise<Sample[]>;
  getSampleFiles(sampleId: string): Promise<LocalFile[]>;
}

export const SampleDispatchContext = React.createContext<SampleDispatchContextType>({} as any);

export function SampleContextProvider(props: React.PropsWithChildren<{}>) {
  const settingsDispatch = useSettingsDispatch();

  const fsCache = useMemo(() => new LfsFsCache(), []);
  const fs = useMemo(() => fsCache.getOrCreateFs(SAMPLES_FS_MOUNT_POINT), [fsCache]);
  const sampleStorageService = useMemo(() => new LfsStorageService(), []);

  const getSamples = useCallback(async () => {
    const cachedSamples = await sampleStorageService.getFile(fs, SAMPLE_DEFINITIONS_CACHE_FILE_PATH);
    if (cachedSamples) {
      const content = decoder.decode(await cachedSamples.getFileContents());
      return JSON.parse(content) as Sample[];
    }
    const definitions = await fetchSampleDefinitions(settingsDispatch.github.octokit);
    const cacheFile = new LfsStorageFile({
      path: SAMPLE_DEFINITIONS_CACHE_FILE_PATH,
      getFileContents: async () => encoder.encode(JSON.stringify(definitions)),
    });
    sampleStorageService.createFiles(fs, [cacheFile]);
    return definitions;
  }, [fs, sampleStorageService, settingsDispatch.github.octokit]);

  const getSampleFiles = useCallback(
    async (sampleId: string) => fetchSampleFiles({ octokit: settingsDispatch.github.octokit, sampleId }),
    [settingsDispatch.github.octokit]
  );

  const dispatch = useMemo(() => ({ getSamples, getSampleFiles }), [getSamples, getSampleFiles]);
  return <SampleDispatchContext.Provider value={dispatch}>{props.children}</SampleDispatchContext.Provider>;
}

export function useSampleDispatch() {
  return useContext(SampleDispatchContext);
}
