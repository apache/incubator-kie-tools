import * as React from "react";
import GithubIcon from "@patternfly/react-icons/dist/js/icons/github-icon";
import { AuthStatus, useSettings } from "../settings/SettingsContext";
import BitbucketIcon from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import GitlabIcon from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import { useMemo } from "react";
import { useGitHubAuthInfo } from "../github/Hooks";

// These are temporary placeholders
export type AuthSource = string;
export enum AuthSourceKeys {
  NONE = "none",
  GITHUB = "github.com",
  BITBUCKET = "bitbucket.com",
  GITLAB = "gitlab.com",
}
//

export function useAuthSources() {
  const settings = useSettings();

  // This is a temporary placeholder.
  const authSources = useMemo(() => {
    return new Map([
      [
        AuthSourceKeys.NONE,
        {
          enabled: true,
          label: "None",
          description: "",
        },
      ],
      [
        AuthSourceKeys.GITHUB,
        {
          enabled: settings.github.authStatus === AuthStatus.SIGNED_IN,
          label: (
            <>
              <GithubIcon />
              &nbsp;&nbsp;GitHub{" "}
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
          label: (
            <>
              <BitbucketIcon />
              &nbsp;&nbsp;Bitbucket
            </>
          ),
          description: "Available soon!",
        },
      ],
      [
        AuthSourceKeys.GITLAB,
        {
          enabled: false,
          label: (
            <>
              <GitlabIcon />
              &nbsp;&nbsp;GitLab
            </>
          ),
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
