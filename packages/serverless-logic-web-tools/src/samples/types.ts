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

import { LocalFile } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/LocalFile";

export type SampleCategory = "serverless-workflow" | "serverless-decision" | "dashbuilder";

export const SampleCategories: SampleCategory[] = ["serverless-workflow", "serverless-decision", "dashbuilder"];

export type SampleStatus = "ok" | "out of date" | "deprecated";

export interface SampleSocial {
  network: string;
  id: string;
}

export interface SampleAuthor {
  name: string;
  email: string;
  github: string;
  social: SampleSocial[];
}

export interface SampleDefinition {
  category: SampleCategory;
  status: SampleStatus;
  title: string;
  description: string;
  cover: string;
  tags: string[];
  type: string;
  dependencies: string[];
  related_to: string[];
  resources: string[];
  authors: SampleAuthor[];
  sample_path: string;
}

export interface Sample {
  sampleId: string;
  definition: SampleDefinition;
}

export type SampleCoversHashtable = {
  [sampleId: string]: string;
};

export interface GitHubContentData {
  type: "dir" | "file";
  name: string;
  path: string;
}

export interface GitHubFileData {
  content: string;
  encoding: BufferEncoding;
}

export interface GitHubRepoInfo {
  owner: string;
  repo: string;
  ref: string;
}

export type GitHubFileInfo = GitHubRepoInfo & { path: string };

export type FetchErrorResponse = { success: false } & (
  | {
      error: "NotFound" | "Unauthenticated";
    }
  | {
      error: "Generic";
      message?: string;
    }
);

export type FetchFileContentSuccess = {
  success: true;
  path: string;
  content: string;
};

export type FetchFileContentResponse = FetchFileContentSuccess | FetchErrorResponse;

export type FetchFolderContentSuccess = {
  success: true;
  contents: GitHubContentData[];
};

export type FetchFolderContentResponse = FetchFolderContentSuccess | FetchErrorResponse;

export type FetchSampleDefinitionsSuccess = {
  success: true;
  samples: Sample[];
};

export type FetchSampleDefinitionsResponse = FetchSampleDefinitionsSuccess | FetchErrorResponse;

export type FetchSampleFilesSuccess = {
  success: true;
  files: LocalFile[];
};

export type FetchSampleFilesResponse = FetchSampleFilesSuccess | FetchErrorResponse;

export interface SamplesRepositoryInfo {
  org: string;
  name: string;
  ref: string;
  paths: {
    samplesFolder: string;
    sampleDefinitionsJson: string;
  };
}
