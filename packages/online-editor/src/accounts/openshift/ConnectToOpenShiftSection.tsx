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
import { useMemo, useState } from "react";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { KieSandboxOpenShiftService } from "../../openshift/KieSandboxOpenShiftService";
import { useExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { obfuscate } from "../github/ConnectToGitHubSection";
import { ConnecToOpenShiftSimple } from "./ConnecToOpenShiftSimple";
import { ConnectToDeveloperSandboxForRedHatOpenShiftWizard } from "./ConnectToDeveloperSandboxForRedHatOpenShiftWizard";
import { EMPTY_OPENSHIFT_CONNECTION } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";

export enum OpenShiftSettingsTabMode {
  SIMPLE,
  WIZARD,
}

export function ConnectToOpenShiftSection() {
  const extendedServices = useExtendedServices();

  const [mode, setMode] = useState(OpenShiftSettingsTabMode.SIMPLE);
  const [status, setStatus] = useState(
    extendedServices.status === KieSandboxExtendedServicesStatus.RUNNING
      ? OpenShiftInstanceStatus.DISCONNECTED
      : OpenShiftInstanceStatus.UNAVAILABLE
  );
  const [connection, setConnection] = useState(EMPTY_OPENSHIFT_CONNECTION);

  const openshiftService = useMemo(
    () =>
      new KieSandboxOpenShiftService({
        connection,
        proxyUrl: extendedServices.config.url.corsProxy,
      }),
    [connection, extendedServices.config]
  );

  return (
    <>
      {status === OpenShiftInstanceStatus.UNAVAILABLE && (
        <>Please connect to Extended Services to be able to connect with OpenShift</>
      )}

      {status === OpenShiftInstanceStatus.CONNECTED && (
        <EmptyState>
          <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
          <TextContent>
            <Text component={"h2"}>{"You're connected to OpenShift."}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>
              Dev deployments are <b>enabled</b>.
            </TextContent>
            <br />
            <TextContent>
              <b>Namespace (project): </b>
              <i>{connection.namespace}</i>
            </TextContent>
            <TextContent>
              <b>Token: </b>
              <i>{obfuscate(connection.token)}</i>
            </TextContent>
            <TextContent>
              <b>Host: </b>
              <i>{connection.host}</i>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      )}
      {status === OpenShiftInstanceStatus.DISCONNECTED && (
        <>
          {mode === OpenShiftSettingsTabMode.SIMPLE && (
            <ConnecToOpenShiftSimple
              setMode={setMode}
              connection={connection}
              setConnection={setConnection}
              status={status}
              setStatus={setStatus}
              openshiftService={openshiftService}
            />
          )}
          {mode === OpenShiftSettingsTabMode.WIZARD && (
            <ConnectToDeveloperSandboxForRedHatOpenShiftWizard
              setMode={setMode}
              connection={connection}
              setConnection={setConnection}
              status={status}
              setStatus={setStatus}
              openshiftService={openshiftService}
            />
          )}
        </>
      )}
    </>
  );
}
