import * as React from "react";
import { useContext, useEffect, useMemo, useState } from "react";
import { getCookie, setCookie } from "../common/utils";
import { Octokit } from "@octokit/rest";
import { GithubService } from "../common/GithubService";
import { AuthStatus } from "../common/GlobalContext";

const GITHUB_AUTH_TOKEN_COOKIE_NAME = "KOGITO-TOOLING-COOKIE__github-oauth-token";
const GUIDED_TOUR_ENABLED_COOKIE_NAME = "KOGITO-TOOLING-COOKIE__is-guided-tour-enabled";

export interface SettingsContextType {
  github: {
    authService: { reset: () => void; authenticate: (token: string) => Promise<void> };
    authStatus: AuthStatus;
    octokit: Octokit;
    token?: string;
    user?: string;
    scopes?: string[];
    service: GithubService;
  };
  general: {
    guidedTourEnabled: {
      get: boolean;
      set: React.Dispatch<React.SetStateAction<boolean>>;
    };
  };
}

export const SettingsContext = React.createContext<SettingsContextType>({} as any);

export function SettingsContextProvider(props: any) {
  //github
  const [githubAuthStatus, setGitHubAuthStatus] = useState(AuthStatus.LOADING);
  const [githubOctokit, setGitHubOctokit] = useState<Octokit>(new Octokit());
  const [githubToken, setGitHubToken] = useState<string | undefined>(undefined);
  const [githubUser, setGitHubUser] = useState<string | undefined>(undefined);
  const [githubScopes, setGitHubScopes] = useState<string[] | undefined>(undefined);

  const githubAuthService = useMemo(() => {
    return {
      reset: () => {
        setGitHubOctokit(new Octokit());
        setGitHubAuthStatus(AuthStatus.SIGNED_OUT);
        setGitHubToken(undefined);
        setGitHubUser(undefined);
        setGitHubScopes(undefined);
        setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, "");
      },
      authenticate: async (token: string) => {
        try {
          setGitHubAuthStatus(AuthStatus.LOADING);
          const octokit = new Octokit({ auth: token });
          const response = await octokit.users.getAuthenticated();
          await delay(1000);
          setGitHubOctokit(octokit);
          setGitHubAuthStatus(AuthStatus.SIGNED_IN);
          setGitHubToken(token);
          setGitHubUser(response.data.login);
          setGitHubScopes(response.headers["x-oauth-scopes"]?.split(", ") ?? []);
          setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, token);
        } catch (e) {
          await delay(1000);
          setGitHubAuthStatus(AuthStatus.SIGNED_OUT);
          throw e;
        }
      },
    };
  }, []);

  useEffect(() => {
    const tokenCookie = getCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME);
    if (!tokenCookie) {
      setGitHubAuthStatus(AuthStatus.SIGNED_OUT);
      return;
    }

    githubAuthService.authenticate(tokenCookie).catch(() => {
      setGitHubAuthStatus(AuthStatus.TOKEN_EXPIRED);
    });
  }, [githubAuthService]);

  const githubService = useMemo(() => new GithubService(), []);

  //guided tour
  const [isGuidedTourEnabled, setGuidedTourEnabled] = useState(
    getBooleanCookieInitialValue(GUIDED_TOUR_ENABLED_COOKIE_NAME, true)
  );

  useEffect(() => {
    setCookie(GUIDED_TOUR_ENABLED_COOKIE_NAME, `${isGuidedTourEnabled}`);
  }, [isGuidedTourEnabled]);

  //

  return (
    <SettingsContext.Provider
      value={{
        github: {
          octokit: githubOctokit,
          authStatus: githubAuthStatus,
          token: githubToken,
          user: githubUser,
          scopes: githubScopes,
          authService: githubAuthService,
          service: githubService,
        },
        general: {
          guidedTourEnabled: {
            get: isGuidedTourEnabled,
            set: setGuidedTourEnabled,
          },
        },
      }}
    >
      {props.children}
    </SettingsContext.Provider>
  );
}

export function useSettings() {
  return useContext(SettingsContext);
}

function getBooleanCookieInitialValue<T>(name: string, defaultValue: boolean) {
  return !getCookie(name) ? defaultValue : getCookie(name) === "true";
}

function delay(ms: number) {
  return new Promise<void>((res) => {
    setTimeout(() => {
      res();
    }, ms);
  });
}
