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
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import { KieSandboxOpenShiftService } from "../../openshift/KieSandboxOpenShiftService";
import {
  DependentFeature,
  useExtendedServices,
} from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { obfuscate } from "../git/ConnectToGitSection";
import { ConnecToOpenShiftSimple } from "./ConnecToOpenShiftSimple";
import { ConnectToDeveloperSandboxForRedHatOpenShiftWizard } from "./ConnectToDeveloperSandboxForRedHatOpenShiftWizard";
import { EMPTY_OPENSHIFT_CONNECTION } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { AccountsDispatchActionKind, AccountsSection, useAccounts, useAccountsDispatch } from "../AccountsContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { OpenShiftAuthSession } from "../../authSessions/AuthSessionApi";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import { AuthSessionDescriptionList } from "../../authSessions/AuthSessionsList";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import PficonSatelliteIcon from "@patternfly/react-icons/dist/js/icons/pficon-satellite-icon";

export enum OpenShiftSettingsTabMode {
  SIMPLE,
  WIZARD,
}

export function ConnectToOpenShiftSection() {
  const extendedServices = useExtendedServices();
  const accounts = useAccounts();
  const accountsDispatch = useAccountsDispatch();

  const [mode, setMode] = useState(OpenShiftSettingsTabMode.SIMPLE);
  const [newAuthSession, setNewAuthSession] = useState<OpenShiftAuthSession>();
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

  const successPrimaryAction = useMemo(() => {
    if (accounts.section !== AccountsSection.CONNECT_TO_OPENSHIFT || !newAuthSession) {
      return;
    }

    if (!accounts.onNewAuthSession) {
      return {
        action: () => accountsDispatch({ kind: AccountsDispatchActionKind.GO_HOME }),
        label: "See connected accounts",
      };
    }

    return {
      action: () => accounts.onNewAuthSession?.(newAuthSession),
      label: "Continue",
    };
  }, [accounts, accountsDispatch, newAuthSession]);

  return (
    <>
      {status === OpenShiftInstanceStatus.UNAVAILABLE && (
        <>
          <Bullseye>
            <EmptyState>
              <EmptyStateIcon icon={PficonSatelliteIcon} />
              <Title
                headingLevel="h4"
                size="md"
                style={{
                  width: "300px",
                  textOverflow: "ellipsis",
                  whiteSpace: "break-spaces",
                }}
              >
                {`Please setup KIE Sandbox Extended Services to be able to connect to OpenShift.`}
              </Title>
              <br />
              <Button
                onClick={() => {
                  setTimeout(() => {
                    extendedServices.setInstallTriggeredBy(DependentFeature.DEV_DEPLOYMENTS);
                    extendedServices.setModalOpen(true);
                    accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                  });
                }}
              >
                Setup
              </Button>
            </EmptyState>
          </Bullseye>
        </>
      )}

      {status === OpenShiftInstanceStatus.CONNECTED && newAuthSession && (
        <>
          <Alert isPlain={true} isInline={true} variant={AlertVariant.success} title={`Successfully connected`}></Alert>
          <br />
          <br />
          <AuthSessionDescriptionList authSession={newAuthSession} />
          <br />
          <br />
          <br />
          <Button variant={ButtonVariant.primary} onClick={successPrimaryAction?.action}>
            {successPrimaryAction?.label}
          </Button>
        </>
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
              setNewAuthSession={setNewAuthSession}
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
              setNewAuthSession={setNewAuthSession}
              openshiftService={openshiftService}
            />
          )}
        </>
      )}
    </>
  );
}
