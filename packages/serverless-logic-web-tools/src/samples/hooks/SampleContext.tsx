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
import { useSettingsDispatch } from "../../settings/SettingsContext";
import {
  fetchSampleCover,
  fetchSampleDefinitions,
  fetchSampleFiles,
  Sample,
  SampleCategory,
  SampleCoversHashtable,
} from "../SampleApi";
import { decoder, encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import Fuse from "fuse.js";
import {
  SAMPLE_DEFINITIONS_CACHE_FILE_PATH,
  SAMPLE_SEARCH_KEYS,
  resolveSampleFsMountPoint,
  SAMPLE_COVERS_CACHE_FILE_PATH,
} from "../SampleConstants";
import { useEnv } from "../../env/EnvContext";

export interface SampleDispatchContextType {
  getSamples(args: { categoryFilter?: SampleCategory; searchFilter?: string }): Promise<Sample[]>;
  getSampleFiles(sampleId: string): Promise<LocalFile[]>;
  /**
   * Gets the cover image for a sample from cache, or loads it from the API and caches it.
   * @param args.sample The sample to get the cover image for.
   * @param args.noCacheWriting A flag indicating whether to write the loaded cover image to cache.
   * @returns The cover image as a base64-encoded string.
   */
  getSampleCover(args: { sample: Sample; noCacheWriting?: boolean }): Promise<string | undefined>;
  /**
   * Gets the cover images for an array of samples from cache, or loads them from the API and caches them.
   * @param args.samples An array of samples to get the cover images for.
   * @param args.prevState The previous state of the entities being loaded
   * @returns An object containing the sample IDs as keys and the cover images as base64-encoded strings.
   */
  getSampleCovers(args: {
    samples: Sample[];
    prevState: { [key: string]: string | undefined };
  }): Promise<SampleCoversHashtable>;
}

export const SampleDispatchContext = React.createContext<SampleDispatchContextType>({} as any);

export function SampleContextProvider(props: React.PropsWithChildren<{}>) {
  const { env } = useEnv();
  const settingsDispatch = useSettingsDispatch();

  const fsCache = useMemo(() => new LfsFsCache(), []);
  const fs = useMemo(
    () => fsCache.getOrCreateFs(resolveSampleFsMountPoint(env.SERVERLESS_LOGIC_WEB_TOOLS_VERSION)),
    [env, fsCache]
  );
  const sampleStorageService = useMemo(() => new LfsStorageService(), []);

  const [allSampleDefinitions, setAllSampleDefinitions] = useState<Sample[]>();
  const [allSampleCovers, setAllSampleCovers] = useState<{ [sampleId: string]: string }>({});

  /**
   * Retrieves the contents of a cache file
   *
   * @param args.path The path of the cache file to retrieve.
   * @returns The JSON-parsed contents of the cache file, or `null` if the file doesn't exist.
   */
  const getCacheContent = useCallback(
    async (args: { path: string }) => {
      const storageFile = await sampleStorageService.getFile(fs, args.path);
      if (storageFile) {
        const cacheContent = decoder.decode(await storageFile.getFileContents());
        return JSON.parse(cacheContent);
      }
      return null;
    },
    [sampleStorageService, fs]
  );

  /**
   * Adds or updates the contents of a cache file
   *
   * @param args.path The path of the cache file to retrieve.
   * @param args.content The content to add in the cache file.
   * @returns The content stored in the cache file.
   */
  const addCacheContent = useCallback(
    async (args: { path: string; content: any }) => {
      const cacheFile = new LfsStorageFile({
        path: args.path,
        getFileContents: async () => encoder.encode(JSON.stringify(args.content)),
      });
      await sampleStorageService.createOrOverwriteFile(fs, cacheFile);
      return args.content;
    },
    [sampleStorageService, fs]
  );

  const loadCache = useCallback(
    async (args: { path: string; loadFn: () => Promise<any> }) => {
      const cacheContent = await getCacheContent(args);

      if (cacheContent) {
        return cacheContent;
      }
      const content = await args.loadFn();

      return await addCacheContent({ ...args, content });
    },
    [getCacheContent, addCacheContent]
  );

  /**
   * Loads an entity from cache if available, otherwise loads it from the provided load function and saves it to cache.
   * @param args.path The path of the cache file.
   * @param args.id The unique identifier for the entity being loaded.
   * @param args.noCacheWriting A flag indicating whether to write the loaded entity to cache.
   * @param args.loadFn The function to use to load the entity if it is not found in cache.
   * @returns The loaded entity.
   */
  const loadCacheEntity = useCallback(
    async (args: { path: string; id: string; noCacheWriting?: boolean; loadFn: () => Promise<any> }) => {
      const cacheContent = (await getCacheContent(args)) || {};
      if (cacheContent[args.id]) {
        return cacheContent[args.id];
      }

      cacheContent[args.id] = await args.loadFn();

      return !args.noCacheWriting
        ? (await addCacheContent({ ...args, content: cacheContent }))[args.id]
        : cacheContent[args.id];
    },
    [getCacheContent, addCacheContent]
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

  const getSampleCover = useCallback(
    async (args: { sample: Sample; noCacheWriting?: boolean }) => {
      const cachedCover = allSampleCovers[args.sample.sampleId];

      if (!cachedCover) {
        const cover = await loadCacheEntity({
          path: SAMPLE_COVERS_CACHE_FILE_PATH,
          id: args.sample.sampleId,
          loadFn: async () => fetchSampleCover({ octokit: settingsDispatch.github.octokit, sample: args.sample }),
          noCacheWriting: args.noCacheWriting,
        });

        allSampleCovers[args.sample.sampleId] = cover;
        setAllSampleCovers(allSampleCovers);
        return cover;
      } else {
        return cachedCover;
      }
    },
    [settingsDispatch.github.octokit, loadCacheEntity, allSampleCovers]
  );

  const getSampleCovers = useCallback(
    async (args: { samples: Sample[]; prevState: { [key: string]: string } }) => {
      if (!args.samples.length) {
        return {};
      }

      const covers = (
        await Promise.all(
          args.samples.map(async (sample) => ({
            [sample.sampleId]: await getSampleCover({ ...args, sample, noCacheWriting: true }),
          }))
        )
      ).reduce((acc, curr) => ({ ...acc, ...curr }), args.prevState || {});

      const cacheContent = await getCacheContent({ path: SAMPLE_COVERS_CACHE_FILE_PATH });
      return await addCacheContent({ path: SAMPLE_COVERS_CACHE_FILE_PATH, content: { ...covers, ...cacheContent } });
    },
    [getSampleCover, addCacheContent, getCacheContent]
  );

  const getSampleFiles = useCallback(
    async (sampleId: string) => fetchSampleFiles({ octokit: settingsDispatch.github.octokit, sampleId }),
    [settingsDispatch.github.octokit]
  );

  const dispatch = useMemo(
    () => ({ getSamples, getSampleFiles, getSampleCover, getSampleCovers }),
    [getSamples, getSampleFiles, getSampleCover, getSampleCovers]
  );
  return <SampleDispatchContext.Provider value={dispatch}>{props.children}</SampleDispatchContext.Provider>;
}

export function useSampleDispatch() {
  return useContext(SampleDispatchContext);
}
