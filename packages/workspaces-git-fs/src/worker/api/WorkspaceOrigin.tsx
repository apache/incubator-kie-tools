/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { GIT_DEFAULT_BRANCH } from "../../constants/GitConstants";

export enum WorkspaceKind {
  GITHUB_GIST = "GITHUB_GIST",
  GIT = "GIT",
  LOCAL = "LOCAL",
  BITBUCKET_SNIPPET = "BITBUCKET_SNIPPET",
}

const gitBasedTypeKeys = [WorkspaceKind.GIT, WorkspaceKind.BITBUCKET_SNIPPET, WorkspaceKind.GITHUB_GIST] as const;
export type WorkspaceKindGitBased = (typeof gitBasedTypeKeys)[number];

export const isGitBasedWorkspaceKind = (maybeGitBasedType: WorkspaceKind | undefined): boolean => {
  if (maybeGitBasedType === undefined) {
    return false;
  }
  const gitBasedType = gitBasedTypeKeys.find((validKey) => validKey === maybeGitBasedType);
  return !!gitBasedType;
};

const gistLikeTypeKeys = [WorkspaceKind.GITHUB_GIST, WorkspaceKind.BITBUCKET_SNIPPET] as const;
export type WorkspaceKindGistLike = (typeof gistLikeTypeKeys)[number];

export const isGistLikeWorkspaceKind = (maybeGistLikeType: WorkspaceKind | undefined): boolean => {
  if (maybeGistLikeType === undefined) {
    return false;
  }
  const gistLikeType = gistLikeTypeKeys.find((validKey) => validKey === maybeGistLikeType);
  return !!gistLikeType;
};

export type WorkspaceOrigin = LocalOrigin | GistOrigin | GitHubOrigin | BitbucketOrigin | SnippetOrigin;

export interface LocalOrigin {
  kind: WorkspaceKind.LOCAL;
  branch: typeof GIT_DEFAULT_BRANCH;
  url?: string;
}

export interface GitHubOrigin {
  kind: WorkspaceKind.GIT;
  url: string;
  branch: string;
}

export interface BitbucketOrigin {
  kind: WorkspaceKind.GIT;
  url: string;
  branch: string;
}

export interface GistOrigin {
  kind: WorkspaceKind.GITHUB_GIST;
  url: string;
  branch: string;
}

export interface SnippetOrigin {
  kind: WorkspaceKind.BITBUCKET_SNIPPET;
  url: string;
  branch: string;
}
