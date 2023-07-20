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
import { useHistory } from "react-router";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { QueryParams } from "../navigation/Routes";
import { useExtendedServices } from "../extendedServices/ExtendedServicesContext";

export const EXTENDED_SERVICES_HOST_COOKIE_NAME = "kie-tools-COOKIE__kie-sandbox-extended-services--host";
export const EXTENDED_SERVICES_PORT_COOKIE_NAME = "kie-tools-COOKIE__kie-sandbox-extended-services--port";

export class ExtendedServicesConfig {
  constructor(public readonly host: string, public readonly port: string) {}

  private buildUrl(): string {
    if (this.port.trim().length === 0) {
      return this.host;
    }
    return `${this.host}:${this.port}`;
  }

  public get url() {
    return {
      jitExecutor: `${this.buildUrl()}/`,
      ping: `${this.buildUrl()}/ping`,
      corsProxy: `${this.buildUrl()}/cors-proxy`,
    };
  }
}

export interface SettingsContextType {
  isOpen: boolean;
  activeTab: SettingsTabs;
  extendedServices: {
    config: ExtendedServicesConfig;
  };
}

export interface SettingsDispatchContextType {
  open: (activeTab?: SettingsTabs) => void;
  close: () => void;
  extendedServices: {
    setConfig: React.Dispatch<React.SetStateAction<ExtendedServicesConfig>>;
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

  const extendedServices = useExtendedServices();

  const dispatch = useMemo(() => {
    return {
      open,
      close,
      extendedServices: {
        setConfig: extendedServices.saveNewConfig,
      },
    };
  }, [close, extendedServices.saveNewConfig, open]);

  const value = useMemo(() => {
    return {
      isOpen,
      activeTab,
      extendedServices: {
        config: extendedServices.config,
      },
    };
  }, [activeTab, isOpen, extendedServices.config]);

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
