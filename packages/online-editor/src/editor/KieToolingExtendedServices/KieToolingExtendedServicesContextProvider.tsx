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

import { EmbeddedEditorRef } from "@kie-tooling-core/editor/dist/embedded";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useGlobals } from "../../common/GlobalContext";
import { getCookie, setCookie } from "../../common/utils";
import { KieToolingExtendedServicesBridge } from "./KieToolingExtendedServicesBridge";
import { DependentFeature, KieToolingExtendedServicesContext } from "./KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";

interface Props {
  children: React.ReactNode;
  editor?: EmbeddedEditorRef;
  isEditorReady: boolean;
  closeDmnTour: () => void;
}

const KIE_TOOLING_EXTENDED_SERVICES_POLLING_TIME = 1000;
const KIE_TOOLING_EXTENDED_SERVICES_PORT_COOKIE_NAME = "kie-tooling-extended-services-port";
export const KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_PORT = "21345";

export function KieToolingExtendedServicesContextProvider(props: Props) {
  const globals = useGlobals();

  const [status, setStatus] = useState(() =>
    globals.file.fileExtension === "dmn"
      ? KieToolingExtendedServicesStatus.AVAILABLE
      : KieToolingExtendedServicesStatus.UNAVAILABLE
  );

  const [isModalOpen, setModalOpen] = useState(false);
  const [installTriggeredBy, setInstallTriggeredBy] = useState(DependentFeature.DMN_RUNNER);
  const [outdated, setOutdated] = useState(false);
  const [port, setPort] = useState(
    () => getCookie(KIE_TOOLING_EXTENDED_SERVICES_PORT_COOKIE_NAME) ?? KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_PORT
  );

  const baseUrl = useMemo(() => `http://localhost:${port}`, [port]);
  const bridge = useMemo(() => new KieToolingExtendedServicesBridge(port), [port]);
  const version = useMemo(
    () => process.env.WEBPACK_REPLACE__kieToolingExtendedServicesCompatibleVersion ?? "0.0.0",
    []
  );

  const saveNewPort = useCallback((newPort: string) => {
    setPort(newPort);
    setCookie(KIE_TOOLING_EXTENDED_SERVICES_PORT_COOKIE_NAME, newPort);
  }, []);

  useEffect(() => {
    if (status === KieToolingExtendedServicesStatus.UNAVAILABLE) {
      return;
    }

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
      bridge
        .check()
        .then(() => {
          // Check the running version of the KieToolingExtendedServices, cancel polling if up-to-date.
          bridge.version().then((kieToolingExtendedServicesVersion) => {
            if (kieToolingExtendedServicesVersion !== version) {
              setOutdated(true);
            } else {
              window.clearInterval(detectKieToolingExtendedServices);
              setOutdated(false);
              setStatus(KieToolingExtendedServicesStatus.RUNNING);
            }
          });
        })
        .catch((err) => {
          console.debug(err);
        });
    }, KIE_TOOLING_EXTENDED_SERVICES_POLLING_TIME);

    return () => window.clearInterval(detectKieToolingExtendedServices);
  }, [props.editor, props.isEditorReady, status, bridge, version]);

  return (
    <KieToolingExtendedServicesContext.Provider
      value={{
        status,
        port,
        baseUrl,
        version,
        outdated,
        isModalOpen,
        installTriggeredBy,
        setStatus,
        setModalOpen,
        setInstallTriggeredBy,
        saveNewPort,
        closeDmnTour: props.closeDmnTour,
      }}
    >
      {props.children}
    </KieToolingExtendedServicesContext.Provider>
  );
}
