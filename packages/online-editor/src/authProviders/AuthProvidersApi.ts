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

export enum AuthProviderGroup {
  CLOUD = "cloud",
  GIT = "git",
}

export enum AuthProviderType {
  github = "github",
  bitbucket = "bitbucket",
  gitlab = "gitlab",
  openshift = "openshift",
  kubernetes = "kubernetes",
}

const gitAuthProviderKeys = [AuthProviderType.bitbucket, AuthProviderType.github, AuthProviderType.gitlab] as const;
const supportedGitAuthProvidersKeys = [
  AuthProviderType.bitbucket,
  AuthProviderType.github,
  AuthProviderType.gitlab,
] as const;
export type SupportedGitAuthProviders = (typeof supportedGitAuthProvidersKeys)[number];
export const isSupportedGitAuthProviderType = (
  maybeSupportedTypeKey: AuthProviderType | undefined
): maybeSupportedTypeKey is SupportedGitAuthProviders => {
  return supportedGitAuthProvidersKeys.some((k) => k === maybeSupportedTypeKey);
};
export const isGitAuthProvider = (
  maybeGitAuthProvider: AuthProvider | undefined
): maybeGitAuthProvider is GitAuthProvider => {
  return gitAuthProviderKeys.some((k) => k === maybeGitAuthProvider?.type);
};
export const isOpenShiftAuthProvider = (
  maybeOpenShiftAuthProvider: AuthProvider | undefined
): maybeOpenShiftAuthProvider is OpenShiftAuthProvider => {
  const openShiftAuthProviderKeys = ["openshift"];
  return openShiftAuthProviderKeys.some((k) => k === maybeOpenShiftAuthProvider?.type);
};

export const isKubernetesAuthProvider = (
  maybeKubernetesAuthProvider: AuthProvider | undefined
): maybeKubernetesAuthProvider is KubernetesAuthProvider => {
  const kubernetesAuthProviderKeys = ["kubernetes"];
  return kubernetesAuthProviderKeys.some((k) => k === maybeKubernetesAuthProvider?.type);
};

export type OpenShiftAuthProviderType = AuthProviderType.openshift;
export type KubernetesAuthProviderType = AuthProviderType.kubernetes;
export type GitAuthProviderType = (typeof gitAuthProviderKeys)[number];

export type OpenShiftAuthProvider = {
  id: string;
  type: OpenShiftAuthProviderType;
  name: string;
  domain: undefined;
  iconPath?: string;
  enabled: boolean;
  group: AuthProviderGroup.CLOUD;
};

export type KubernetesAuthProvider = {
  id: string;
  type: KubernetesAuthProviderType;
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
  insecurelyDisableTlsCertificateValidation?: boolean;
  disableEncoding?: boolean;
};

export type AuthProvider = OpenShiftAuthProvider | KubernetesAuthProvider | GitAuthProvider;

const gistEnabledTypeConfigKeys = [
  AuthProviderType.github,
  AuthProviderType.bitbucket,
  AuthProviderType.gitlab,
] as const;
export type GistEnabledAuthProviderType = (typeof gistEnabledTypeConfigKeys)[number];

export const isGistEnabledAuthProviderType = (
  maybeGistEnabledTypeKey: AuthProviderType | undefined
): maybeGistEnabledTypeKey is GistEnabledAuthProviderType => {
  return gistEnabledTypeConfigKeys.some((k) => k === maybeGistEnabledTypeKey);
};
