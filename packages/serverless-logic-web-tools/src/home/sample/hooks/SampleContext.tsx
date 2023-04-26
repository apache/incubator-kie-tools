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
import { useContext, useMemo, useCallback, useState } from "react";
import { useSettingsDispatch } from "../../../settings/SettingsContext";
import { fetchSampleDefinitions, fetchSampleFiles, Sample, SampleCategory } from "../SampleApi";
import { decoder, encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import Fuse from "fuse.js";
import { SAMPLES_FS_MOUNT_POINT, SAMPLE_DEFINITIONS_CACHE_FILE_PATH, SAMPLE_SEARCH_KEYS } from "../SampleConstants";

export interface SampleDispatchContextType {
  getSamples(args: { categoryFilter?: SampleCategory; searchFilter?: string }): Promise<Sample[]>;
  getSampleFiles(sampleId: string): Promise<LocalFile[]>;
}

export const SampleDispatchContext = React.createContext<SampleDispatchContextType>({} as any);

export function SampleContextProvider(props: React.PropsWithChildren<{}>) {
  const settingsDispatch = useSettingsDispatch();

  const fsCache = useMemo(() => new LfsFsCache(), []);
  const fs = useMemo(() => fsCache.getOrCreateFs(SAMPLES_FS_MOUNT_POINT), [fsCache]);
  const sampleStorageService = useMemo(() => new LfsStorageService(), []);

  const [allSampleDefinitions, setAllSampleDefinitions] = useState<Sample[]>();

  const loadCache = useCallback(
    async (args: { path: string; loadFn: () => Promise<any> }) => {
      const storageFile = await sampleStorageService.getFile(fs, args.path);
      if (storageFile) {
        const cacheContent = decoder.decode(await storageFile.getFileContents());
        return JSON.parse(cacheContent);
      }
      const content = await args.loadFn();
      const cacheFile = new LfsStorageFile({
        path: args.path,
        getFileContents: async () => encoder.encode(JSON.stringify(content)),
      });
      await sampleStorageService.createFiles(fs, [cacheFile]);
      return content;
    },
    [fs, sampleStorageService]
  );

  const getSamples = useCallback(
    async (args: { categoryFilter?: SampleCategory; searchFilter?: string }) => {
      let filteredSamples: Sample[];
      if (!allSampleDefinitions) {
        filteredSamples = (await loadCache({
          path: SAMPLE_DEFINITIONS_CACHE_FILE_PATH,
          loadFn: async () => fetchSampleDefinitions(settingsDispatch.github.octokit),
        })) as Sample[];

        setAllSampleDefinitions(filteredSamples);
      } else {
        filteredSamples = allSampleDefinitions;
      }

      if (args.categoryFilter) {
        filteredSamples = filteredSamples.filter((s) => s.definition.category === args.categoryFilter);
      }

      if (args.searchFilter && args.searchFilter.trim().length > 0) {
        const fuse = new Fuse(filteredSamples, {
          keys: SAMPLE_SEARCH_KEYS,
          shouldSort: false,
          threshold: 0.3,
        });

        filteredSamples = fuse.search(args.searchFilter).map((r) => r.item);
      }

      return filteredSamples;
    },
    [allSampleDefinitions, loadCache, settingsDispatch.github.octokit]
  );

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
