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

import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { getCookie, setCookie } from "../cookies";
import { Octokit } from "@octokit/rest";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { SettingsModalBody, SettingsTabs } from "./SettingsModalBody";
import { readOpenShiftConfigCookie } from "./openshift/OpenShiftSettingsConfig";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { OpenShiftInstanceStatus } from "../openshift/OpenShiftInstanceStatus";
import { OpenShiftService } from "@kie-tools-core/openshift/dist/service/OpenShiftService";
import { useHistory } from "react-router";
import { QueryParams } from "../navigation/Routes";
import { GITHUB_AUTH_TOKEN_COOKIE_NAME } from "./github/GitHubSettingsTab";
import { KafkaSettingsConfig, readKafkaConfigCookie } from "./kafka/KafkaSettingsConfig";
import { readServiceAccountConfigCookie, ServiceAccountSettingsConfig } from "./serviceAccount/ServiceAccountConfig";
import {
  readServiceRegistryConfigCookie,
  ServiceRegistrySettingsConfig,
} from "./serviceRegistry/ServiceRegistryConfig";
import { useKieSandboxExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { SwfServiceCatalogStore } from "../editor/api/SwfServiceCatalogStore";
import { FeaturePreviewSettingsConfig, readFeaturePreviewConfigCookie } from "./featurePreview/FeaturePreviewConfig";

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

export class ExtendedServicesConfig {
  constructor(public readonly host: string, public readonly port: string) {}

  public buildUrl(): string {
    if (this.port.trim().length === 0) {
      return this.host;
    }
    return `${this.host}:${this.port}`;
  }
}

export interface SettingsContextType {
  isOpen: boolean;
  activeTab: SettingsTabs;
  openshift: {
    status: OpenShiftInstanceStatus;
    config: OpenShiftConnection;
  };
  kieSandboxExtendedServices: {
    config: ExtendedServicesConfig;
  };
  github: {
    token?: string;
    user?: GithubUser;
    scopes?: string[];
    authStatus: AuthStatus;
  };
  apacheKafka: {
    config: KafkaSettingsConfig;
  };
  serviceAccount: {
    config: ServiceAccountSettingsConfig;
  };
  serviceRegistry: {
    config: ServiceRegistrySettingsConfig;
  };
  featurePreview: {
    config: FeaturePreviewSettingsConfig;
  };
}

export interface SettingsDispatchContextType {
  open: (activeTab?: SettingsTabs) => void;
  close: () => void;
  openshift: {
    service: OpenShiftService;
    setStatus: React.Dispatch<React.SetStateAction<OpenShiftInstanceStatus>>;
    setConfig: React.Dispatch<React.SetStateAction<OpenShiftConnection>>;
  };
  kieSandboxExtendedServices: {
    setConfig: React.Dispatch<React.SetStateAction<ExtendedServicesConfig>>;
  };
  github: {
    authService: { reset: () => void; authenticate: (token: string) => Promise<void> };
    octokit: Octokit;
  };
  apacheKafka: {
    setConfig: React.Dispatch<React.SetStateAction<KafkaSettingsConfig>>;
  };
  serviceAccount: {
    setConfig: React.Dispatch<React.SetStateAction<ServiceAccountSettingsConfig>>;
  };
  serviceRegistry: {
    setConfig: React.Dispatch<React.SetStateAction<ServiceRegistrySettingsConfig>>;
    catalogStore: SwfServiceCatalogStore;
  };
  featurePreview: {
    setConfig: React.Dispatch<React.SetStateAction<FeaturePreviewSettingsConfig>>;
  };
}

export const SettingsContext = React.createContext<SettingsContextType>({} as any);
export const SettingsDispatchContext = React.createContext<SettingsDispatchContextType>({} as any);

export function SettingsContextProvider(props: any) {
  const queryParams = useQueryParams();
  const history = useHistory();
  const [isOpen, setOpen] = useState(false);
  const [activeTab, setActiveTab] = useState(SettingsTabs.GITHUB);

  useEffect(() => {
    setOpen(queryParams.has(QueryParams.SETTINGS));
    setActiveTab((queryParams.get(QueryParams.SETTINGS) as SettingsTabs) ?? SettingsTabs.GITHUB);
  }, [queryParams]);

  const open = useCallback(
    (activeTab = SettingsTabs.GITHUB) => {
      history.replace({
        search: queryParams.with(QueryParams.SETTINGS, activeTab).toString(),
      });
    },
    [history, queryParams]
  );

  const close = useCallback(() => {
    history.replace({
      search: queryParams.without(QueryParams.SETTINGS).toString(),
    });
  }, [history, queryParams]);

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

  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const [openshiftConfig, setOpenShiftConfig] = useState(readOpenShiftConfigCookie());
  const [kafkaConfig, setKafkaConfig] = useState<KafkaSettingsConfig>(readKafkaConfigCookie());
  const [serviceAccountConfig, setServiceAccountConfig] = useState<ServiceAccountSettingsConfig>(
    readServiceAccountConfigCookie()
  );
  const [serviceRegistryConfig, setServiceRegistryConfig] = useState<ServiceRegistrySettingsConfig>(
    readServiceRegistryConfigCookie()
  );

  const [featurePreviewConfig, setFeaturePreviewConfig] = useState<FeaturePreviewSettingsConfig>(
    readFeaturePreviewConfigCookie()
  );

  const [openshiftStatus, setOpenshiftStatus] = useState(
    kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.AVAILABLE
      ? OpenShiftInstanceStatus.DISCONNECTED
      : OpenShiftInstanceStatus.UNAVAILABLE
  );

  const openshiftService = useMemo(
    () =>
      new OpenShiftService({
        connection: openshiftConfig,
        proxyUrl: `${kieSandboxExtendedServices.config.buildUrl()}/devsandbox`,
      }),
    [openshiftConfig, kieSandboxExtendedServices.config]
  );

  const serviceCatalogStore = useMemo(
    () =>
      new SwfServiceCatalogStore({
        serviceAccount: serviceAccountConfig,
        serviceRegistry: serviceRegistryConfig,
        extendedServicesConfig: kieSandboxExtendedServices.config,
      }),
    [kieSandboxExtendedServices.config, serviceAccountConfig, serviceRegistryConfig]
  );

  const dispatch = useMemo(() => {
    return {
      open,
      close,
      openshift: {
        service: openshiftService,
        setStatus: setOpenshiftStatus,
        setConfig: setOpenShiftConfig,
      },
      github: {
        authService: githubAuthService,
        octokit: githubOctokit,
      },
      kieSandboxExtendedServices: {
        setConfig: kieSandboxExtendedServices.saveNewConfig,
      },
      apacheKafka: {
        setConfig: setKafkaConfig,
      },
      serviceAccount: {
        setConfig: setServiceAccountConfig,
      },
      serviceRegistry: {
        setConfig: setServiceRegistryConfig,
        catalogStore: serviceCatalogStore,
      },
      featurePreview: {
        setConfig: setFeaturePreviewConfig,
      },
    };
  }, [
    close,
    githubAuthService,
    githubOctokit,
    open,
    openshiftService,
    kieSandboxExtendedServices.saveNewConfig,
    serviceCatalogStore,
  ]);

  const value = useMemo(() => {
    return {
      isOpen,
      activeTab,
      openshift: {
        status: openshiftStatus,
        config: openshiftConfig,
      },
      github: {
        authStatus: githubAuthStatus,
        token: githubToken,
        user: githubUser,
        scopes: githubScopes,
      },
      kieSandboxExtendedServices: {
        config: kieSandboxExtendedServices.config,
      },
      apacheKafka: {
        config: kafkaConfig,
      },
      serviceAccount: {
        config: serviceAccountConfig,
      },
      serviceRegistry: {
        config: serviceRegistryConfig,
      },
      featurePreview: {
        config: featurePreviewConfig,
      },
    };
  }, [
    isOpen,
    activeTab,
    openshiftStatus,
    openshiftConfig,
    githubAuthStatus,
    githubToken,
    githubUser,
    githubScopes,
    kafkaConfig,
    serviceAccountConfig,
    serviceRegistryConfig,
    kieSandboxExtendedServices.config,
    featurePreviewConfig,
  ]);

  return (
    <SettingsContext.Provider value={value}>
      <SettingsDispatchContext.Provider value={dispatch}>
        {githubAuthStatus !== AuthStatus.LOADING && <>{props.children}</>}
        <Modal title="Settings" isOpen={isOpen} onClose={close} variant={ModalVariant.large}>
          <div style={{ height: "calc(100vh * 0.5)" }} className={"kie-tools--settings-modal-content"}>
            <SettingsModalBody />
          </div>
        </Modal>
      </SettingsDispatchContext.Provider>
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
