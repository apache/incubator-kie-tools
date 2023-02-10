/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

export enum AuthProviderGroup {
  CLOUD = "cloud",
  GIT = "git",
}

export enum AuthProviderType {
  github = "github",
  bitbucket = "bitbucket",
  gitlab = "gitlab",
  openshift = "openshift",
}

const supportedGitAuthProvidersKeys = [AuthProviderType.bitbucket, AuthProviderType.github] as const;
export type SupportedGitAuthProviders = typeof supportedGitAuthProvidersKeys[number];
export const isSupportedGitAuthProviderType = (
  maybeSupportedTypeKey: AuthProviderType | undefined
): maybeSupportedTypeKey is SupportedGitAuthProviders => {
  return supportedGitAuthProvidersKeys.some((k) => k === maybeSupportedTypeKey);
};

export type OpenShiftAuthProviderType = AuthProviderType.openshift;
export type GitAuthProviderType = AuthProviderType.bitbucket | AuthProviderType.github | AuthProviderType.gitlab;

export type OpenShiftAuthProvider = {
  id: string;
  type: OpenShiftAuthProviderType;
  name: string;
  domain: undefined;
  iconPath?: string;
  enabled: boolean;
  group: AuthProviderGroup.CLOUD;
};

export type GitAuthProvider = {
  id: string;
  type: GitAuthProviderType;
  name: string;
  domain: string;
  iconPath?: string;
  enabled: boolean;
  supportedGitRemoteDomains: string[];
  group: AuthProviderGroup.GIT;
};

export type AuthProvider = OpenShiftAuthProvider | GitAuthProvider;

const gistEnabledTypeConfigKeys = [AuthProviderType.github, AuthProviderType.bitbucket] as const;
export type GistEnabledAuthProviderType = typeof gistEnabledTypeConfigKeys[number];

export const isGistEnabledAuthProviderType = (
  maybeGistEnabledTypeKey: AuthProviderType | undefined
): maybeGistEnabledTypeKey is GistEnabledAuthProviderType => {
  return gistEnabledTypeConfigKeys.some((k) => k === maybeGistEnabledTypeKey);
};
