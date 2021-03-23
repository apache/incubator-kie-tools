/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

// TODO CAPONETTO: Improve this whole part

export enum WorkspaceKind {
  GITHUB_REPOSITORY = "GITHUB_REPOSITORY",
  LOCAL = "LOCAL",
}

export type WorkspaceOrigin = LocalOrigin | GitHubRepositoryOrigin;

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface LocalOrigin {}

export interface GitHubRepositoryOrigin {
  url: URL;
  branch: string;
}

export function resolveKind(origin: WorkspaceOrigin): WorkspaceKind {
  if ((origin as GitHubRepositoryOrigin).url) {
    return WorkspaceKind.GITHUB_REPOSITORY;
  }

  return WorkspaceKind.LOCAL;
}
