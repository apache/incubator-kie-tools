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

import { Octokit } from "@octokit/rest";
import { useMemo } from "react";
import { useAuthProviders } from "../accounts/authProviders/AuthProvidersContext";
import { AuthSession } from "../accounts/authSessions/AuthSessionApi";

export function useOctokit(authSession: AuthSession | undefined): Octokit {
  const authProviders = useAuthProviders();

  return useMemo(() => {
    if (authSession?.type !== "git") {
      return new Octokit();
    }

    const authProvider = authProviders.find((a) => a.id === authSession.authProviderId);
    if (authProvider?.type !== "github") {
      return new Octokit();
    }

    return new Octokit({ baseUrl: getGithubInstanceApiUrl(authProvider.domain), auth: authSession.token });
  }, [authProviders, authSession]);
}

export const getGithubInstanceApiUrl = (domain: string) => {
  if (domain === "github.com") {
    return undefined;
  }
  return `https://${domain}/api/v3`;
};
