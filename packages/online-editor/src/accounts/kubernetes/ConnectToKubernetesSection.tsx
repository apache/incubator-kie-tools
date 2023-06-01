/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React, { useMemo, useState } from "react";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { KubernetesInstanceStatus } from "./KubernetesInstanceStatus";
import { DependentFeature, useExtendedServices } from "../../extendedServices/ExtendedServicesContext";
import { ConnectToKubernetesSimple } from "./ConnectToKubernetesSimple";
import { ExtendedServicesStatus } from "../../extendedServices/ExtendedServicesStatus";
import { AccountsDispatchActionKind, AccountsSection, useAccounts, useAccountsDispatch } from "../AccountsContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { KubernetesAuthSession } from "../../authSessions/AuthSessionApi";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import { AuthSessionDescriptionList } from "../../authSessions/AuthSessionsList";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import PficonSatelliteIcon from "@patternfly/react-icons/dist/js/icons/pficon-satellite-icon";
import { KieSandboxKubernetesService } from "../../devDeployments/services/KieSandboxKubernetesService";
import { EMPTY_KUBERNETES_CONNECTION } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { ConnectToLocalKubernetesClusterWizard } from "./ConnectToLocalKubernetesClusterWizard";

export enum KubernetesSettingsTabMode {
  SIMPLE,
  WIZARD,
}

export function ConnectToKubernetesSection() {
  const extendedServices = useExtendedServices();
  const accounts = useAccounts();
  const accountsDispatch = useAccountsDispatch();

  const [mode, setMode] = useState(KubernetesSettingsTabMode.SIMPLE);
  const [newAuthSession, setNewAuthSession] = useState<KubernetesAuthSession>();
  const [status, setStatus] = useState(
    extendedServices.status === ExtendedServicesStatus.RUNNING
      ? KubernetesInstanceStatus.DISCONNECTED
      : KubernetesInstanceStatus.UNAVAILABLE
  );
  const [connection, setConnection] = useState(EMPTY_KUBERNETES_CONNECTION);

  const kubernetesService = useMemo(
    () =>
      new KieSandboxKubernetesService({
        connection,
      }),
    [connection]
  );

  const successPrimaryAction = useMemo(() => {
    if (accounts.section !== AccountsSection.CONNECT_TO_KUBERNETES || !newAuthSession) {
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
      {status === KubernetesInstanceStatus.UNAVAILABLE && (
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
                {`Please setup Extended Services to be able to connect to Kubernetes.`}
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

      {status === KubernetesInstanceStatus.CONNECTED && newAuthSession && (
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
      {status === KubernetesInstanceStatus.DISCONNECTED && (
        <>
          {mode === KubernetesSettingsTabMode.SIMPLE && (
            <ConnectToKubernetesSimple
              setMode={setMode}
              connection={connection}
              setConnection={setConnection}
              status={status}
              setStatus={setStatus}
              setNewAuthSession={setNewAuthSession}
              kubernetesService={kubernetesService}
            />
          )}
          {mode === KubernetesSettingsTabMode.WIZARD && (
            <ConnectToLocalKubernetesClusterWizard
              setMode={setMode}
              connection={connection}
              setConnection={setConnection}
              status={status}
              setStatus={setStatus}
              setNewAuthSession={setNewAuthSession}
              kubernetesService={kubernetesService}
            />
          )}
        </>
      )}
    </>
  );
}
