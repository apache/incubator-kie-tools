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

export const SAMPLE_COVERS_CACHE_FILE_PATH = "/covers.json";

export const SAMPLES_FS_MOUNT_POINT_PREFIX = "lfs_v1__samples__";

export const SAMPLE_DEFINITIONS_CACHE_FILE_PATH = "/definitions.json";

export const SAMPLE_SEARCH_KEYS = ["definition.category", "definition.title", "definition.description"];

export const resolveSampleFsMountPoint = (appVersion: string) => {
  return `${SAMPLES_FS_MOUNT_POINT_PREFIX}${appVersion}`;
};
