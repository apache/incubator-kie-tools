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

import { Octokit } from "@octokit/rest";
import * as React from "react";
import { useContext, useEffect, useMemo, useState } from "react";
import { getCookie, setCookie } from "../cookies";
import { SwfServiceCatalogStore } from "../editor/api/SwfServiceCatalogStore";
import { readDevModeEnabledConfigCookie, readOpenShiftConfigCookie } from "./openshift/OpenShiftSettingsConfig";
import { OpenShiftInstanceStatus } from "../openshift/OpenShiftInstanceStatus";
import { OpenShiftService } from "@kie-tools-core/kubernetes-bridge/dist/service/OpenShiftService";
import { GITHUB_AUTH_TOKEN_COOKIE_NAME } from "./github/GitHubSettings";
import { readServiceAccountConfigCookie, ServiceAccountSettingsConfig } from "./serviceAccount/ServiceAccountConfig";
import {
  readServiceRegistryConfigCookie,
  ServiceRegistrySettingsConfig,
} from "./serviceRegistry/ServiceRegistryConfig";
import { useEnv } from "../env/EnvContext";
import { KubernetesConnection } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { readRuntimeToolsConfigCookie, RuntimeToolsSettingsConfig } from "./runtimeTools/RuntimeToolsConfig";

export enum AuthStatus {
  SIGNED_OUT,
  TOKEN_EXPIRED,
  LOADING,
  SIGNED_IN,
}

export enum GithubScopes {
  GIST = "gist",
  REPO = "repo",
}

interface GithubUser {
  login: string;
  name: string;
  email: string;
}

export interface SettingsContextType {
  openshift: {
    status: OpenShiftInstanceStatus;
    config: KubernetesConnection;
    isDevModeEnabled: boolean;
  };
  github: {
    token?: string;
    user?: GithubUser;
    scopes?: string[];
    authStatus: AuthStatus;
  };
  serviceAccount: {
    config: ServiceAccountSettingsConfig;
  };
  serviceRegistry: {
    config: ServiceRegistrySettingsConfig;
  };
  runtimeTools: {
    config: RuntimeToolsSettingsConfig;
  };
}

export interface SettingsDispatchContextType {
  openshift: {
    service: OpenShiftService;
    setStatus: React.Dispatch<React.SetStateAction<OpenShiftInstanceStatus>>;
    setConfig: React.Dispatch<React.SetStateAction<KubernetesConnection>>;
    setDevModeEnabled: React.Dispatch<React.SetStateAction<boolean>>;
  };
  github: {
    authService: { reset: () => void; authenticate: (token: string) => Promise<void> };
    octokit: Octokit;
  };
  serviceAccount: {
    setConfig: React.Dispatch<React.SetStateAction<ServiceAccountSettingsConfig>>;
  };
  serviceRegistry: {
    setConfig: React.Dispatch<React.SetStateAction<ServiceRegistrySettingsConfig>>;
    catalogStore: SwfServiceCatalogStore;
  };
  runtimeTools: {
    setConfig: React.Dispatch<React.SetStateAction<RuntimeToolsSettingsConfig>>;
  };
}

export const SETTINGS_PAGE_SECTION_TITLE = "Settings";
export const SettingsContext = React.createContext<SettingsContextType>({} as any);
export const SettingsDispatchContext = React.createContext<SettingsDispatchContextType>({} as any);

export function SettingsContextProvider(props: any) {
  const { env } = useEnv();
  //github
  const [githubAuthStatus, setGitHubAuthStatus] = useState(AuthStatus.LOADING);
  const [githubOctokit, setGitHubOctokit] = useState<Octokit>(new Octokit());
  const [githubToken, setGitHubToken] = useState<string | undefined>(undefined);
  const [githubUser, setGitHubUser] = useState<GithubUser | undefined>(undefined);
  const [githubScopes, setGitHubScopes] = useState<string[] | undefined>(undefined);

  const githubAuthService = useMemo(() => {
    return {
      reset: () => {
        setGitHubOctokit(new Octokit());
        setGitHubToken(undefined);
        setGitHubUser(undefined);
        setGitHubScopes(undefined);
        setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, "");
        setGitHubAuthStatus(AuthStatus.SIGNED_OUT);
      },
      authenticate: async (token: string) => {
        try {
          setGitHubAuthStatus(AuthStatus.LOADING);
          const octokit = new Octokit({ auth: token });
          const response = await octokit.users.getAuthenticated();
          await delay(1000);
          const scopes = response.headers["x-oauth-scopes"]?.split(", ") ?? [];
          if (!scopes.includes("repo")) {
            throw new Error("Token doesn't have 'repo' scope.");
          }

          setGitHubOctokit(octokit);
          setGitHubToken(token);
          setGitHubUser({
            login: response.data.login,
            name: response.data.name ?? "",
            email: response.data.email ?? "",
          });
          setGitHubScopes(scopes);
          setCookie(GITHUB_AUTH_TOKEN_COOKIE_NAME, token);
          setGitHubAuthStatus(AuthStatus.SIGNED_IN);
        } catch (e) {
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

  const [openshiftConfig, setOpenShiftConfig] = useState(readOpenShiftConfigCookie());
  const [serviceAccountConfig, setServiceAccountConfig] = useState(readServiceAccountConfigCookie());
  const [serviceRegistryConfig, setServiceRegistryConfig] = useState(readServiceRegistryConfigCookie());
  const [runtimeToolsConfig, setRuntimeToolsConfig] = useState(readRuntimeToolsConfigCookie());

  const [openshiftStatus, setOpenshiftStatus] = useState(OpenShiftInstanceStatus.DISCONNECTED);

  const openshiftService = useMemo(
    () =>
      new OpenShiftService({
        connection: openshiftConfig,
        proxyUrl: env.SERVERLESS_LOGIC_WEB_TOOLS_CORS_PROXY_URL,
      }),
    [openshiftConfig]
  );

  const [isOpenShiftDevModeEnabled, setOpenShiftDevModeEnabled] = useState(readDevModeEnabledConfigCookie());

  const serviceCatalogStore = useMemo(
    () =>
      new SwfServiceCatalogStore({
        serviceAccount: serviceAccountConfig,
        serviceRegistry: serviceRegistryConfig,
        proxyUrl: env.SERVERLESS_LOGIC_WEB_TOOLS_CORS_PROXY_URL,
      }),
    [serviceAccountConfig, serviceRegistryConfig]
  );

  const dispatch = useMemo(() => {
    return {
      openshift: {
        service: openshiftService,
        setStatus: setOpenshiftStatus,
        setConfig: setOpenShiftConfig,
        setDevModeEnabled: setOpenShiftDevModeEnabled,
      },
      github: {
        authService: githubAuthService,
        octokit: githubOctokit,
      },
      serviceAccount: {
        setConfig: setServiceAccountConfig,
      },
      serviceRegistry: {
        setConfig: setServiceRegistryConfig,
        catalogStore: serviceCatalogStore,
      },
      runtimeTools: {
        setConfig: setRuntimeToolsConfig,
      },
    };
  }, [githubAuthService, githubOctokit, openshiftService, serviceCatalogStore]);

  const value = useMemo(() => {
    return {
      openshift: {
        status: openshiftStatus,
        config: openshiftConfig,
        isDevModeEnabled: isOpenShiftDevModeEnabled,
      },
      github: {
        authStatus: githubAuthStatus,
        token: githubToken,
        user: githubUser,
        scopes: githubScopes,
      },
      serviceAccount: {
        config: serviceAccountConfig,
      },
      serviceRegistry: {
        config: serviceRegistryConfig,
      },
      runtimeTools: {
        config: runtimeToolsConfig,
      },
    };
  }, [
    openshiftStatus,
    openshiftConfig,
    isOpenShiftDevModeEnabled,
    githubAuthStatus,
    githubToken,
    githubUser,
    githubScopes,
    serviceAccountConfig,
    serviceRegistryConfig,
    runtimeToolsConfig,
  ]);

  return (
    <SettingsContext.Provider value={value}>
      <SettingsDispatchContext.Provider value={dispatch}>{props.children}</SettingsDispatchContext.Provider>
    </SettingsContext.Provider>
  );
}

export function useSettings() {
  return useContext(SettingsContext);
}

export function useSettingsDispatch() {
  return useContext(SettingsDispatchContext);
}

function delay(ms: number) {
  return new Promise<void>((res) => {
    setTimeout(() => {
      res();
    }, ms);
  });
}
