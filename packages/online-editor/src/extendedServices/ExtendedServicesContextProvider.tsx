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

import * as React from "react";
import { useEffect, useMemo, useState } from "react";
import { useSettings } from "../settings/SettingsContext";
import { ExtendedServicesBridge } from "./ExtendedServicesBridge";
import { ExtendedServicesClient } from "./ExtendedServicesClient";
import { DependentFeature, ExtendedServicesContext } from "./ExtendedServicesContext";
import { ExtendedServicesModal } from "./ExtendedServicesModal";
import { ExtendedServicesStatus } from "./ExtendedServicesStatus";

interface Props {
  children: React.ReactNode;
}

const KIE_SANDBOX_EXTENDED_SERVICES_POLLING_TIME = 1000;

export function ExtendedServicesContextProvider(props: Props) {
  const { settings } = useSettings();
  const [status, setStatus] = useState(ExtendedServicesStatus.AVAILABLE);
  const [isModalOpen, setModalOpen] = useState(false);
  const [installTriggeredBy, setInstallTriggeredBy] = useState<DependentFeature | undefined>(undefined);
  const [outdated, setOutdated] = useState(false);

  const config = useMemo(() => {
    try {
      const url = new URL(settings.extendedServices.host);
      const port = settings.extendedServices.port;
      if (port) {
        url.port = port;
      }

      return new ExtendedServicesConfig(url.href, url.port);
    } catch (error) {
      return new ExtendedServicesConfig(settings.extendedServices.host ?? "", settings.extendedServices.port ?? "");
    }
  }, [settings.extendedServices.port, settings.extendedServices.host]);

  const bridge = useMemo(() => new ExtendedServicesBridge(config.url.ping), [config]);
  const version = useMemo(() => process.env.WEBPACK_REPLACE__extendedServicesCompatibleVersion ?? "0.0.0", []);

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
    }),
    [client, config, installTriggeredBy, isModalOpen, outdated, status, version]
  );

  return (
    <ExtendedServicesContext.Provider value={value}>
      {props.children}
      <ExtendedServicesModal />
    </ExtendedServicesContext.Provider>
  );
}
export class ExtendedServicesConfig {
  constructor(public readonly href: string, public readonly port: string) {}

  public get url() {
    return {
      jitExecutor: `${this.href}`,
      ping: `${this.href.endsWith("/") ? this.href : this.href + ":" + this.port + "/"}ping`,
    };
  }
}
