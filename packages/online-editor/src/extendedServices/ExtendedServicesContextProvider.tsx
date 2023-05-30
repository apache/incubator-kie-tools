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
import { useCallback, useEffect, useMemo, useState } from "react";
import { getCookie, setCookie } from "../cookies";
import { ExtendedServicesBridge } from "./ExtendedServicesBridge";
import { DependentFeature, ExtendedServicesContext } from "./ExtendedServicesContext";
import { ExtendedServicesStatus } from "./ExtendedServicesStatus";
import {
  ExtendedServicesConfig,
  EXTENDED_SERVICES_HOST_COOKIE_NAME,
  EXTENDED_SERVICES_PORT_COOKIE_NAME,
} from "../settings/SettingsContext";
import { ExtendedServicesModal } from "./ExtendedServicesModal";
import { useEnv } from "../env/hooks/EnvContext";
import { ExtendedServicesClient } from "./ExtendedServicesClient";

interface Props {
  children: React.ReactNode;
}

const KIE_SANDBOX_EXTENDED_SERVICES_POLLING_TIME = 1000;

export function ExtendedServicesContextProvider(props: Props) {
  const env = useEnv();
  const [status, setStatus] = useState(ExtendedServicesStatus.AVAILABLE);
  const [isModalOpen, setModalOpen] = useState(false);
  const [installTriggeredBy, setInstallTriggeredBy] = useState<DependentFeature | undefined>(undefined);
  const [outdated, setOutdated] = useState(false);

  const { port, host } = useMemo(() => {
    const url = new URL(env.env.KIE_SANDBOX_EXTENDED_SERVICES_URL);
    const port = url.port;
    url.port = "";

    return {
      port,
      host: url.href,
    };
  }, [env.env.KIE_SANDBOX_EXTENDED_SERVICES_URL]);

  const [config, setConfig] = useState(new ExtendedServicesConfig(host, port));
  const bridge = useMemo(() => new ExtendedServicesBridge(config.url.ping), [config]);
  const version = useMemo(() => process.env.WEBPACK_REPLACE__extendedServicesCompatibleVersion ?? "0.0.0", []);

  const saveNewConfig = useCallback((newConfig: ExtendedServicesConfig) => {
    setConfig(newConfig);
    setCookie(EXTENDED_SERVICES_HOST_COOKIE_NAME, newConfig.host);
    setCookie(EXTENDED_SERVICES_PORT_COOKIE_NAME, newConfig.port);
  }, []);

  useEffect(() => {
    try {
      const envUrl = new URL(env.env.KIE_SANDBOX_EXTENDED_SERVICES_URL);
      const envHost = `${envUrl.protocol}//${envUrl.hostname}`;
      const envPort = envUrl.port;

      const host = getCookie(EXTENDED_SERVICES_HOST_COOKIE_NAME) ?? envHost;
      const port = getCookie(EXTENDED_SERVICES_PORT_COOKIE_NAME) ?? envPort;

      const newConfig = new ExtendedServicesConfig(host, port);
      setConfig(newConfig);

      new ExtendedServicesBridge(newConfig.url.ping).check().then((checked) => {
        if (checked) {
          saveNewConfig(newConfig);
        }
      });
    } catch (e) {
      console.error("Invalid KIE_SANDBOX_EXTENDED_SERVICES_URL", e);
    }
  }, [env.env.KIE_SANDBOX_EXTENDED_SERVICES_URL, saveNewConfig]);

  useEffect(() => {
    // Pooling to detect either if ExtendedServices is running or has stopped
    if (status === ExtendedServicesStatus.RUNNING) {
      const detectCrashesOrStops: number | undefined = window.setInterval(() => {
        bridge.check().catch(() => {
          setStatus(ExtendedServicesStatus.STOPPED);
          setModalOpen(true);
          window.clearInterval(detectCrashesOrStops);
        });
      }, KIE_SANDBOX_EXTENDED_SERVICES_POLLING_TIME);

      return () => window.clearInterval(detectCrashesOrStops);
    }

    const detectExtendedServices: number | undefined = window.setInterval(() => {
      // Check the running version of the ExtendedServices, cancel polling if up-to-date.
      bridge
        .ping()
        .then((response) => {
          if (response.version !== version) {
            setOutdated(true);
          } else if (response.started) {
            window.clearInterval(detectExtendedServices);
            setOutdated(false);
            setStatus(ExtendedServicesStatus.RUNNING);
          }
        })
        .catch((err) => {
          console.debug(err);
        });
    }, KIE_SANDBOX_EXTENDED_SERVICES_POLLING_TIME);

    return () => window.clearInterval(detectExtendedServices);
  }, [status, bridge, version]);

  const client = useMemo(() => new ExtendedServicesClient(config.url.jitExecutor), [config]);

  const value = useMemo(
    () => ({
      status,
      config,
      client,
      version,
      outdated,
      isModalOpen,
      installTriggeredBy,
      setStatus,
      setModalOpen,
      setInstallTriggeredBy,
      saveNewConfig,
    }),
    [client, config, installTriggeredBy, isModalOpen, outdated, saveNewConfig, status, version]
  );

  return (
    <ExtendedServicesContext.Provider value={value}>
      {props.children}
      <ExtendedServicesModal />
    </ExtendedServicesContext.Provider>
  );
}
