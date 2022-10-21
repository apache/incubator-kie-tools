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
import { useGitHubAuthInfo } from "../github/Hooks";
import { AuthStatus, useSettings } from "../settings/SettingsContext";

//
//
//
//  This file contains temporary modeling for auth sources.
//
//

export type AuthSource = string;

export enum AuthSourceKeys {
  NONE = "none",
  GITHUB = "github.com",
  BITBUCKET = "bitbucket.com",
  GITLAB = "gitlab.com",
}

export function useAuthSources() {
  const settings = useSettings();

  // This is a temporary placeholder.
  const authSources = useMemo(() => {
    return new Map([
      [
        AuthSourceKeys.NONE,
        {
          enabled: true,
          label: "Anonymous",
          description: "",
        },
      ],
      [
        AuthSourceKeys.GITHUB,
        {
          enabled: settings.github.authStatus === AuthStatus.SIGNED_IN,
          label: (
            <>
              {`GitHub`}
              &nbsp;
              <i>{settings.github.authStatus === AuthStatus.SIGNED_IN ? `(${settings.github.user?.login})` : ""}</i>
            </>
          ),

          description:
            settings.github.authStatus === AuthStatus.SIGNED_IN
              ? `${settings.github.user?.name}${settings.github.user?.email ? `(${settings.github.user?.email})` : ``}`
              : "Not logged in",
        },
      ],
      [
        AuthSourceKeys.BITBUCKET,
        {
          enabled: false,
          label: <>{`Bitbucket`}</>,
          description: "Available soon!",
        },
      ],
      [
        AuthSourceKeys.GITLAB,
        {
          enabled: false,
          label: <>{`GitLab`}</>,
          description: "Available soon!",
        },
      ],
    ]);
  }, [settings.github.authStatus, settings.github.user]);

  return authSources;
}

export function useSelectedAuthInfo(authSource: AuthSource | undefined) {
  const githubAuthInfo = useGitHubAuthInfo();

  // Change this when more auth sources are available.
  const { selectedAuthInfo, selectedAuthSource } = useMemo(() => {
    // By default, use GitHub if present.
    if (!authSource && githubAuthInfo) {
      return { selectedAuthInfo: githubAuthInfo, selectedAuthSource: AuthSourceKeys.GITHUB };
    }

    // If GitHub is selected, use it, independent if present or not.
    if (authSource === AuthSourceKeys.GITHUB) {
      return { selectedAuthInfo: githubAuthInfo, selectedAuthSource: AuthSourceKeys.GITHUB };
    }

    // Don't use anything
    return { selectedAuthInfo: undefined, selectedAuthSource: AuthSourceKeys.NONE };
  }, [githubAuthInfo, authSource]);

  return { authInfo: selectedAuthInfo, authSource: selectedAuthSource };
}
