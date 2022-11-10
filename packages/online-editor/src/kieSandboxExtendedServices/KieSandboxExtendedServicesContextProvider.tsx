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
import { KieSandboxExtendedServicesBridge } from "./KieSandboxExtendedServicesBridge";
import { DependentFeature, KieSandboxExtendedServicesContext } from "./KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "./KieSandboxExtendedServicesStatus";
import {
  ExtendedServicesConfig,
  KIE_SANDBOX_EXTENDED_SERVICES_HOST_COOKIE_NAME,
  KIE_SANDBOX_EXTENDED_SERVICES_PORT_COOKIE_NAME,
} from "../settings/SettingsContext";
import { KieSandboxExtendedServicesModal } from "./KieSandboxExtendedServicesModal";
import {
  DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_HOST,
  DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_PORT,
  useEnv,
} from "../env/hooks/EnvContext";

interface Props {
  children: React.ReactNode;
}

const KIE_SANDBOX_EXTENDED_SERVICES_POLLING_TIME = 1000;

export function KieSandboxExtendedServicesContextProvider(props: Props) {
  const env = useEnv();
  const [status, setStatus] = useState(KieSandboxExtendedServicesStatus.AVAILABLE);
  const [isModalOpen, setModalOpen] = useState(false);
  const [installTriggeredBy, setInstallTriggeredBy] = useState<DependentFeature | undefined>(undefined);
  const [outdated, setOutdated] = useState(false);
  const [config, setConfig] = useState(
    new ExtendedServicesConfig(DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_HOST, DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_PORT)
  );
  const bridge = useMemo(() => new KieSandboxExtendedServicesBridge(config.buildUrl()), [config]);
  const version = useMemo(
    () => process.env.WEBPACK_REPLACE__kieSandboxExtendedServicesCompatibleVersion ?? "0.0.0",
    []
  );

  const saveNewConfig = useCallback((newConfig: ExtendedServicesConfig) => {
    setConfig(newConfig);
    setCookie(KIE_SANDBOX_EXTENDED_SERVICES_HOST_COOKIE_NAME, newConfig.host);
    setCookie(KIE_SANDBOX_EXTENDED_SERVICES_PORT_COOKIE_NAME, newConfig.port);
  }, []);

  useEffect(() => {
    let envHost = DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_HOST;
    let envPort = DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_PORT;
    try {
      const envUrl = new URL(env.vars.KIE_SANDBOX_EXTENDED_SERVICES_URL);
      envHost = `${envUrl.protocol}//${envUrl.hostname}`;
      envPort = envUrl.port;
    } catch (e) {
      console.error("Invalid KIE_SANDBOX_EXTENDED_SERVICES_URL", e);
    }

    const host = getCookie(KIE_SANDBOX_EXTENDED_SERVICES_HOST_COOKIE_NAME) ?? envHost;
    const port = getCookie(KIE_SANDBOX_EXTENDED_SERVICES_PORT_COOKIE_NAME) ?? envPort;

    const newConfig = new ExtendedServicesConfig(host, port);
    setConfig(newConfig);

    new KieSandboxExtendedServicesBridge(newConfig.buildUrl())
      .check()
      .then((checked) => {
        if (checked) {
          saveNewConfig(newConfig);
        }
      })
      .catch((error) => {
        setStatus(KieSandboxExtendedServicesStatus.STOPPED);
      });
  }, [env.vars.KIE_SANDBOX_EXTENDED_SERVICES_URL, saveNewConfig]);

  useEffect(() => {
    // Pooling to detect either if KieSandboxExtendedServices is running or has stopped
    if (status === KieSandboxExtendedServicesStatus.RUNNING) {
      const detectCrashesOrStops: number | undefined = window.setInterval(() => {
        bridge.check().catch(() => {
          setStatus(KieSandboxExtendedServicesStatus.STOPPED);
          setModalOpen(true);
          window.clearInterval(detectCrashesOrStops);
        });
      }, KIE_SANDBOX_EXTENDED_SERVICES_POLLING_TIME);

      return () => window.clearInterval(detectCrashesOrStops);
    }

    const detectKieSandboxExtendedServices: number | undefined = window.setInterval(() => {
      // Check the running version of the KieSandboxExtendedServices, cancel polling if up-to-date.
      bridge
        .version()
        .then((receivedVersion) => {
          if (receivedVersion !== version) {
            setOutdated(true);
          } else {
            window.clearInterval(detectKieSandboxExtendedServices);
            setOutdated(false);
            setStatus(KieSandboxExtendedServicesStatus.RUNNING);
          }
        })
        .catch((err) => {
          console.debug(err);
        });
    }, KIE_SANDBOX_EXTENDED_SERVICES_POLLING_TIME);

    return () => window.clearInterval(detectKieSandboxExtendedServices);
  }, [status, bridge, version]);

  const value = useMemo(
    () => ({
      status,
      config,
      version,
      outdated,
      isModalOpen,
      installTriggeredBy,
      setStatus,
      setModalOpen,
      setInstallTriggeredBy,
      saveNewConfig,
    }),
    [config, installTriggeredBy, isModalOpen, outdated, saveNewConfig, status, version]
  );

  return (
    <KieSandboxExtendedServicesContext.Provider value={value}>
      {props.children}
      <KieSandboxExtendedServicesModal />
    </KieSandboxExtendedServicesContext.Provider>
  );
}
