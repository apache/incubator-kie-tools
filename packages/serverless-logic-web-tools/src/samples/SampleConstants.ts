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

import { join } from "path";
import { SamplesRepositoryInfo } from "./types";

export const SAMPLE_COVERS_CACHE_FILE_PATH = "/covers.json";

export const SAMPLES_FS_MOUNT_POINT_PREFIX = "lfs_v1__samples__";

export const SAMPLE_DEFINITIONS_CACHE_FILE_PATH = "/definitions.json";

export const SAMPLE_SEARCH_KEYS = ["definition.category", "definition.title", "definition.description"];

export const resolveSampleFsMountPoint = (appVersion: string) => {
  return `${SAMPLES_FS_MOUNT_POINT_PREFIX}${appVersion}`;
};

export const KIE_SAMPLES_REPOSITORY_INFO: SamplesRepositoryInfo = {
  org: process.env["WEBPACK_REPLACE__samplesRepositoryOrg"]!,
  name: process.env["WEBPACK_REPLACE__samplesRepositoryName"]!,
  ref: process.env["WEBPACK_REPLACE__samplesRepositoryRef"]!,
  paths: {
    samplesFolder: "samples",
    sampleDefinitionsJson: join(".github", "supporting-files", "sample-definitions.json"),
  },
};
