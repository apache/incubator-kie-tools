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

import { AuthProvider } from "../authProviders/AuthProvidersApi";
import { AuthSession, AuthSessionStatus, AUTH_SESSION_NONE } from "./AuthSessionApi";
import { AuthSessionSelectFilter } from "./AuthSessionSelect";

export function getCompatibleAuthSessionWithUrlDomain(args: {
  authProviders: AuthProvider[];
  authSessions: Map<string, AuthSession>;
  authSessionStatus: Map<string, AuthSessionStatus>;
  urlDomain: string | undefined;
}) {
  const compatible: AuthSession[] = [];
  const incompatible: AuthSession[] = [];

  for (const authSession of [...args.authSessions.values()]) {
    if (
      isAuthSessionCompatibleWithUrlDomain({
        authSession,
        authProvider: args.authProviders.find(
          ({ id }) => authSession.type !== "none" && id === authSession.authProviderId
        ),
        status: args.authSessionStatus.get(authSession.id),
        urlDomain: args.urlDomain,
      })
    ) {
      compatible.push(authSession);
    } else {
      incompatible.push(authSession);
    }
  }

  compatible.push(AUTH_SESSION_NONE);

  return {
    compatible,
    incompatible,
  };
}

export function isAuthSessionCompatibleWithUrlDomain(args: {
  authSession: AuthSession;
  authProvider: AuthProvider | undefined;
  status: AuthSessionStatus | undefined;
  urlDomain: string | undefined;
}) {
  if (!args.urlDomain) {
    return args.authSession.type === "none";
  }

  if (args.authSession.type === "none") {
    return true;
  }

  if (
    (args.authProvider?.type === "github" ||
      args.authProvider?.type === "gitlab" ||
      args.authProvider?.type === "bitbucket") &&
    new Set(args.authProvider.supportedGitRemoteDomains).has(args.urlDomain) &&
    args.status === AuthSessionStatus.VALID
  ) {
    return true;
  }

  return false;
}

export function authSessionsSelectFilterCompatibleWithGitUrlDomain(
  gitUrlDomain: string | undefined
): AuthSessionSelectFilter {
  if (!gitUrlDomain) {
    return gitAuthSessionSelectFilter();
  }

  return (items) => {
    const compatibleItemsWithUrl = items
      .filter(({ authSession }) => authSession.type === "git" || authSession.type === "none")
      .map(({ authSession, authProvider, status }) => {
        if (
          isAuthSessionCompatibleWithUrlDomain({
            authSession,
            authProvider,
            status,
            urlDomain: gitUrlDomain,
          })
        ) {
          return { authSession, authProvider, groupLabel: "Compatible" };
        } else {
          return { authSession, authProvider: authProvider!, groupLabel: "Other" };
        }
      });

    return {
      items: compatibleItemsWithUrl,
      groups: [
        { label: "Compatible", hidden: false },
        { label: "Other", hidden: true },
      ],
    };
  };
}

export function authSessionsSelectFilterCompatibleWithGistOrSnippetUrlDomain(
  gitUrlDomain: string | undefined,
  owner: string | undefined
): AuthSessionSelectFilter {
  if (!gitUrlDomain) {
    return gitAuthSessionSelectFilter();
  }

  return (items) => {
    const compatibleItemsWithUrl = items
      .filter(({ authSession }) => authSession.type === "git" || authSession.type === "none")
      .map(({ authSession, authProvider, status }) => {
        if (
          authSession.type === "git" &&
          isAuthSessionCompatibleWithUrlDomain({
            authSession,
            authProvider,
            status,
            urlDomain: gitUrlDomain,
          }) &&
          owner === authSession.login
        ) {
          return { authSession, authProvider, groupLabel: "Compatible" };
        } else {
          return { authSession, authProvider: authProvider!, groupLabel: "Other" };
        }
      });

    return {
      items: compatibleItemsWithUrl,
      groups: [
        { label: "Compatible", hidden: false },
        { label: "Other", hidden: true },
      ],
    };
  };
}

export function noOpAuthSessionSelectFilter(): AuthSessionSelectFilter {
  return (items) => ({
    groups: [{ label: "Same", hidden: false }],
    items: items.map(({ authSession, authProvider }) => ({
      authSession,
      authProvider,
      groupLabel: "Same",
    })),
  });
}

export function gitAuthSessionSelectFilter(): AuthSessionSelectFilter {
  return (items) => ({
    groups: [{ label: "Same", hidden: false }],
    items: items
      .filter(({ authSession }) => authSession.type === "git" || authSession.type === "none")
      .map(({ authSession, authProvider }) => ({
        authSession,
        authProvider,
        groupLabel: "Same",
      })),
  });
}

export function openshiftAuthSessionSelectFilter(): AuthSessionSelectFilter {
  return (items) => ({
    groups: [{ label: "Same", hidden: false }],
    items: items
      .filter(({ authSession }) => authSession.type === "openshift")
      .map(({ authSession, authProvider }) => ({
        authSession,
        authProvider,
        groupLabel: "Same",
      })),
  });
}

export function kubernetesAuthSessionSelectFilter(): AuthSessionSelectFilter {
  return (items) => ({
    groups: [{ label: "Same", hidden: false }],
    items: items
      .filter(({ authSession }) => authSession.type === "kubernetes")
      .map(({ authSession, authProvider }) => ({
        authSession,
        authProvider,
        groupLabel: "Same",
      })),
  });
}

export function cloudAuthSessionSelectFilter(): AuthSessionSelectFilter {
  return (items) => ({
    groups: [{ label: "Same", hidden: false }],
    items: items
      .filter(({ authSession }) => authSession.type === "kubernetes" || authSession.type === "openshift")
      .map(({ authSession, authProvider }) => ({
        authSession,
        authProvider,
        groupLabel: "Same",
      })),
  });
}
