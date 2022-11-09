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

import * as React from "react";
import { useMemo } from "react";
import { AuthSession } from "../authSessions/AuthSessionApi";

export type OpenShiftAuthProvider = {
  id: string;
  type: "openshift";
  name: string;
  domain: undefined;
  iconPath?: string;
  enabled: true;
};

export type GitAuthProvider = {
  id: string;
  type: "github" | "bitbucket" | "gitlab";
  name: string;
  domain: string;
  iconPath?: string;
  enabled: boolean;
  supportedGitRemoteDomains: string[];
};

export type AuthProvider = OpenShiftAuthProvider | GitAuthProvider;

export const AUTH_PROVIDERS: AuthProvider[] = [
  {
    id: "github_dot_com",
    domain: "github.com",
    supportedGitRemoteDomains: ["github.com", "gist.github.com"],
    type: "github",
    name: "GitHub",
    enabled: true,
    iconPath: "",
  },
  {
    id: "gitlab_dot_com",
    domain: "gitlab.com",
    supportedGitRemoteDomains: ["gitlab.com"],
    type: "gitlab",
    name: "GitLab",
    enabled: false,
    iconPath: "",
  },
  {
    id: "bitbucket_dot_com",
    domain: "bitbucket.com",
    supportedGitRemoteDomains: ["bitbucket.com"],
    type: "bitbucket",
    name: "Bitbucket",
    enabled: false,
    iconPath: "",
  },
  //
  //
  // Templates for next PR
  //
  //
  // {
  //   id: "github_at_ibm",
  //   domain: "github.ibm.com",
  //   supportedGitRemoteDomains: ["github.ibm.com", "gist.github.ibm.com"],
  //   type: "github",
  //   name: "GitHub @ IBM",
  //   enabled: true,
  //   iconPath: "assets/ibm-github-icon.png", // Always relative path
  // },
  //   {
  //     id: "bitbucket_at_my_customer",
  //     domain: "bitbucket.my-customer.com",
  //     type: "bitbucket",
  //     name: "Bitbucket @ My customer",
  //     enabled: false,
  //     // iconPath: "assets/bitbucket-my-customer.png", // Always relative path
  //   },
  //   {
  //     id: "github_at_my_partner",
  //     domain: "my-partner.ibm.com",
  //     type: "github",
  //     name: "GitHub @ My partner",
  //     enabled: false,
  //     // iconPath: "assets/my-partner-github-icon.png", // Always relative path
  //   },
  //   {
  //     id: "openshift",
  //     type: "openshift",
  //     name: "OpenShift cluster",
  //     domain: undefined,
  //     enabled: true,
  //   },
];

export function useAuthProviders() {
  return useMemo<AuthProvider[]>(() => AUTH_PROVIDERS, []);
}

export function useAuthProvider(authSession: AuthSession | undefined) {
  const authProviders = useAuthProviders();
  if (authSession?.type === "none") {
    return undefined;
  }

  return authProviders.find((a) => a.id === authSession?.authProviderId);
}
