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
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { SettingsModalBody, SettingsTabs } from "./SettingsModalBody";
import { readConfigCookie } from "../openshift/OpenShiftSettingsConfig";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { OpenShiftInstanceStatus } from "../openshift/OpenShiftInstanceStatus";
import { KieSandboxOpenShiftService } from "../openshift/KieSandboxOpenShiftService";
import { useKieSandboxExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { useHistory } from "react-router";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { QueryParams } from "../navigation/Routes";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";

export const KIE_SANDBOX_EXTENDED_SERVICES_HOST_COOKIE_NAME = "kie-tools-COOKIE__kie-sandbox-extended-services--host";
export const KIE_SANDBOX_EXTENDED_SERVICES_PORT_COOKIE_NAME = "kie-tools-COOKIE__kie-sandbox-extended-services--port";
const GUIDED_TOUR_ENABLED_COOKIE_NAME = "kie-tools-COOKIE__guided-tour--is-enabled";
export const OPENSHIFT_NAMESPACE_COOKIE_NAME = "kie-tools-COOKIE__dmn-dev-sandbox--connection-namespace";
export const OPENSHIFT_HOST_COOKIE_NAME = "kie-tools-COOKIE__dmn-dev-sandbox--connection-host";
export const OPENSHIFT_TOKEN_COOKIE_NAME = "kie-tools-COOKIE__dmn-dev-sandbox--connection-token";

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
  general: {
    guidedTour: {
      isEnabled: boolean;
    };
  };
}

export interface SettingsDispatchContextType {
  open: (activeTab?: SettingsTabs) => void;
  close: () => void;
  openshift: {
    service: KieSandboxOpenShiftService;
    setStatus: React.Dispatch<React.SetStateAction<OpenShiftInstanceStatus>>;
    setConfig: React.Dispatch<React.SetStateAction<OpenShiftConnection>>;
  };
  kieSandboxExtendedServices: {
    setConfig: React.Dispatch<React.SetStateAction<ExtendedServicesConfig>>;
  };
  general: {
    guidedTour: {
      setEnabled: React.Dispatch<React.SetStateAction<boolean>>;
    };
  };
}

export const SettingsContext = React.createContext<SettingsContextType>({} as any);
export const SettingsDispatchContext = React.createContext<SettingsDispatchContextType>({} as any);

export function SettingsContextProvider(props: any) {
  const queryParams = useQueryParams();
  const history = useHistory();
  const [isOpen, setOpen] = useState(false);
  const [activeTab, setActiveTab] = useState(SettingsTabs.KIE_SANDBOX_EXTENDED_SERVICES);

  useEffect(() => {
    setOpen(queryParams.has(QueryParams.SETTINGS));
    setActiveTab((queryParams.get(QueryParams.SETTINGS) as SettingsTabs) ?? SettingsTabs.KIE_SANDBOX_EXTENDED_SERVICES);
  }, [queryParams]);

  const open = useCallback(
    (activeTab = SettingsTabs.KIE_SANDBOX_EXTENDED_SERVICES) => {
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

  //guided tour
  const [isGuidedTourEnabled, setGuidedTourEnabled] = useState(
    getBooleanCookieInitialValue(GUIDED_TOUR_ENABLED_COOKIE_NAME, true)
  );

  useEffect(() => {
    setCookie(GUIDED_TOUR_ENABLED_COOKIE_NAME, `${isGuidedTourEnabled}`);
  }, [isGuidedTourEnabled]);

  //openshift
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const [openshiftConfig, setOpenShiftConfig] = useState(readConfigCookie());
  const [openshiftStatus, setOpenshiftStatus] = useState(
    kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.AVAILABLE
      ? OpenShiftInstanceStatus.DISCONNECTED
      : OpenShiftInstanceStatus.UNAVAILABLE
  );

  const openshiftService = useMemo(
    () =>
      new KieSandboxOpenShiftService({
        connection: openshiftConfig,
        proxyUrl: `${kieSandboxExtendedServices.config.buildUrl()}/devsandbox`,
      }),
    [openshiftConfig, kieSandboxExtendedServices.config]
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
      kieSandboxExtendedServices: {
        setConfig: kieSandboxExtendedServices.saveNewConfig,
      },
      general: {
        guidedTour: {
          setEnabled: setGuidedTourEnabled,
        },
      },
    };
  }, [close, kieSandboxExtendedServices.saveNewConfig, open, openshiftService]);

  const value = useMemo(() => {
    return {
      isOpen,
      activeTab,
      openshift: {
        status: openshiftStatus,
        config: openshiftConfig,
      },
      kieSandboxExtendedServices: {
        config: kieSandboxExtendedServices.config,
      },
      general: {
        guidedTour: {
          isEnabled: isGuidedTourEnabled,
        },
      },
    };
  }, [activeTab, isGuidedTourEnabled, isOpen, kieSandboxExtendedServices.config, openshiftConfig, openshiftStatus]);

  return (
    <SettingsContext.Provider value={value}>
      <SettingsDispatchContext.Provider value={dispatch}>
        {<>{props.children}</>}
        <Modal title="Settings" isOpen={isOpen} onClose={close} variant={ModalVariant.large}>
          <div style={{ height: "calc(100vh * 0.5)" }} className={"kie-tools--setings-modal-content"}>
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

function getBooleanCookieInitialValue<T>(name: string, defaultValue: boolean) {
  return !getCookie(name) ? defaultValue : getCookie(name) === "true";
}
