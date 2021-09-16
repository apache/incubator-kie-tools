import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { getCookie, setCookie } from "../common/utils";
import { Octokit } from "@octokit/rest";
import { GithubService } from "./GithubService";
import { AuthStatus } from "../common/GlobalContext";
import { QueryParams, useQueryParams } from "../queryParams/QueryParamsContext";
import { SettingsTabs } from "./SettingsModalBody";
import { OpenShiftSettingsConfig, readConfigCookie } from "./OpenShiftSettingsConfig";
import { KieToolingExtendedServicesStatus } from "../editor/KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import { OpenShiftService } from "./OpenShiftService";
import { useKieToolingExtendedServices } from "../editor/KieToolingExtendedServices/KieToolingExtendedServicesContext";

const GITHUB_AUTH_TOKEN_COOKIE_NAME = "KOGITO-TOOLING-COOKIE__github-oauth-token";
const GUIDED_TOUR_ENABLED_COOKIE_NAME = "KOGITO-TOOLING-COOKIE__is-guided-tour-enabled";
export const OPENSHIFT_NAMESPACE_COOKIE_NAME = "KOGITO-TOOLING-COOKIE__dmn-dev-sandbox--connection-namespace";
export const OPENSHIFT_HOST_COOKIE_NAME = "KOGITO-TOOLING-COOKIE__dmn-dev-sandbox--connection-host";
export const OPENSHIFT_TOKEN_COOKIE_NAME = "KOGITO-TOOLING-COOKIE__dmn-dev-sandbox--connection-token";

export interface SettingsContextType {
  open: (activeTab?: SettingsTabs) => void;
  close: () => void;
  isOpen: boolean;
  activeTab: SettingsTabs;
  openshift: {
    service: OpenShiftService;
    status: {
      get: OpenShiftInstanceStatus;
      set: React.Dispatch<React.SetStateAction<OpenShiftInstanceStatus>>;
    };
    config: {
      get: OpenShiftSettingsConfig;
      set: React.Dispatch<React.SetStateAction<OpenShiftSettingsConfig>>;
    };
  };
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
  //
  const queryParams = useQueryParams();
  const [isOpen, setOpen] = useState(!!queryParams.get(QueryParams.SETTINGS));
  const [activeTab, setActiveTab] = useState(
    (queryParams.get(QueryParams.SETTINGS) as SettingsTabs) ?? SettingsTabs.GENERAL
  );

  const open = useCallback((activeTab = SettingsTabs.GENERAL) => {
    setOpen(true);
    setActiveTab(activeTab);
  }, []);

  const close = useCallback(() => {
    setOpen(false);
  }, []);

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

  //openshift
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const [openshiftConfig, setOpenShiftConfig] = useState(readConfigCookie());
  const [openshiftStatus, setOpenshiftStatus] = useState(
    kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.UNAVAILABLE
      ? OpenShiftInstanceStatus.UNAVAILABLE
      : OpenShiftInstanceStatus.DISCONNECTED
  );
  const openshiftService = useMemo(
    () => new OpenShiftService(`${kieToolingExtendedServices.baseUrl}/devsandbox`),
    [kieToolingExtendedServices.baseUrl]
  );

  return (
    <SettingsContext.Provider
      value={{
        open,
        close,
        isOpen,
        activeTab,
        openshift: {
          service: openshiftService,
          status: {
            get: openshiftStatus,
            set: setOpenshiftStatus,
          },
          config: {
            get: openshiftConfig,
            set: setOpenShiftConfig,
          },
        },
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
