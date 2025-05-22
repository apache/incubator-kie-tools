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

import { GIT_DEFAULT_BRANCH } from "../../constants/GitConstants";

export enum WorkspaceKind {
  GITHUB_GIST = "GITHUB_GIST",
  GIT = "GIT",
  LOCAL = "LOCAL",
  BITBUCKET_SNIPPET = "BITBUCKET_SNIPPET",
  GITLAB_SNIPPET = "GITLAB_SNIPPET",
}

const gitBasedTypeKeys = [
  WorkspaceKind.GIT,
  WorkspaceKind.BITBUCKET_SNIPPET,
  WorkspaceKind.GITHUB_GIST,
  WorkspaceKind.GITLAB_SNIPPET,
] as const;
export type WorkspaceKindGitBased = (typeof gitBasedTypeKeys)[number];

export const isGitBasedWorkspaceKind = (
  maybeGitBasedType: WorkspaceKind | undefined
): maybeGitBasedType is WorkspaceKindGitBased => {
  return gitBasedTypeKeys.some((k) => k === maybeGitBasedType);
};

const gistLikeTypeKeys = [
  WorkspaceKind.GITHUB_GIST,
  WorkspaceKind.BITBUCKET_SNIPPET,
  WorkspaceKind.GITLAB_SNIPPET,
] as const;
export type WorkspaceKindGistLike = (typeof gistLikeTypeKeys)[number];

export const isGistLikeWorkspaceKind = (
  maybeGistLikeType: WorkspaceKind | undefined
): maybeGistLikeType is WorkspaceKindGistLike => {
  return gistLikeTypeKeys.some((k) => k === maybeGistLikeType);
};

export type WorkspaceOrigin =
  | LocalOrigin
  | GistOrigin
  | GitHubOrigin
  | BitbucketOrigin
  | BitbucketSnippetOrigin
  | GitlabOrigin
  | GitlabSnippetOrigin;

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

export interface BitbucketSnippetOrigin {
  kind: WorkspaceKind.BITBUCKET_SNIPPET;
  url: string;
  branch: string;
}

export interface GitlabOrigin {
  kind: WorkspaceKind.GIT;
  url: string;
  branch: string;
}

export interface GitlabSnippetOrigin {
  kind: WorkspaceKind.GITLAB_SNIPPET;
  url: string;
  branch: string;
}
