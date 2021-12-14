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
import { KieToolingExtendedServicesBridge } from "./KieToolingExtendedServicesBridge";
import { DependentFeature, KieToolingExtendedServicesContext } from "./KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";
import {
  ExtendedServicesConfig,
  KIE_TOOLING_EXTENDED_SERVICES_HOST_COOKIE_NAME,
  KIE_TOOLING_EXTENDED_SERVICES_PORT_COOKIE_NAME,
} from "../settings/SettingsContext";
import { KieToolingExtendedServicesModal } from "./KieToolingExtendedServicesModal";

interface Props {
  children: React.ReactNode;
}

const KIE_TOOLING_EXTENDED_SERVICES_POLLING_TIME = 1000;

export const KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_HOST = "http://localhost";
export const KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_PORT = "21345";

export function KieToolingExtendedServicesContextProvider(props: Props) {
  const [status, setStatus] = useState(KieToolingExtendedServicesStatus.AVAILABLE);
  const [isModalOpen, setModalOpen] = useState(false);
  const [installTriggeredBy, setInstallTriggeredBy] = useState<DependentFeature | undefined>(undefined);
  const [outdated, setOutdated] = useState(false);
  const [config, setConfig] = useState(
    new ExtendedServicesConfig(KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_HOST, KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_PORT)
  );
  const bridge = useMemo(() => new KieToolingExtendedServicesBridge(config.buildUrl()), [config]);
  const version = useMemo(
    () => process.env.WEBPACK_REPLACE__kieToolingExtendedServicesCompatibleVersion ?? "0.0.0",
    []
  );

  const saveNewConfig = useCallback((newConfig: ExtendedServicesConfig) => {
    setConfig(newConfig);
    setCookie(KIE_TOOLING_EXTENDED_SERVICES_HOST_COOKIE_NAME, newConfig.host);
    setCookie(KIE_TOOLING_EXTENDED_SERVICES_PORT_COOKIE_NAME, newConfig.port);
  }, []);

  useEffect(() => {
    const hostCookie =
      getCookie(KIE_TOOLING_EXTENDED_SERVICES_HOST_COOKIE_NAME) ?? KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_HOST;
    const portCookie =
      getCookie(KIE_TOOLING_EXTENDED_SERVICES_PORT_COOKIE_NAME) ?? KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_PORT;

    const newConfig = new ExtendedServicesConfig(hostCookie, portCookie);
    new KieToolingExtendedServicesBridge(newConfig.buildUrl()).check().then((checked) => {
      if (checked) {
        saveNewConfig(newConfig);
      }
    });
  }, [saveNewConfig]);

  useEffect(() => {
    // Pooling to detect either if KieToolingExtendedServices is running or has stopped
    let detectCrashesOrStops: number | undefined;
    if (status === KieToolingExtendedServicesStatus.RUNNING) {
      detectCrashesOrStops = window.setInterval(() => {
        bridge.check().catch(() => {
          setStatus(KieToolingExtendedServicesStatus.STOPPED);
          setModalOpen(true);
          window.clearInterval(detectCrashesOrStops);
        });
      }, KIE_TOOLING_EXTENDED_SERVICES_POLLING_TIME);

      return () => window.clearInterval(detectCrashesOrStops);
    }

    const detectKieToolingExtendedServices: number | undefined = window.setInterval(() => {
      // Check the running version of the KieToolingExtendedServices, cancel polling if up-to-date.
      bridge
        .version()
        .then((receivedVersion) => {
          if (receivedVersion !== version) {
            setOutdated(true);
          } else {
            window.clearInterval(detectKieToolingExtendedServices);
            setOutdated(false);
            setStatus(KieToolingExtendedServicesStatus.RUNNING);
          }
        })
        .catch((err) => {
          console.debug(err);
        });
    }, KIE_TOOLING_EXTENDED_SERVICES_POLLING_TIME);

    return () => window.clearInterval(detectKieToolingExtendedServices);
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
    <KieToolingExtendedServicesContext.Provider value={value}>
      {props.children}
      <KieToolingExtendedServicesModal />
    </KieToolingExtendedServicesContext.Provider>
  );
}
