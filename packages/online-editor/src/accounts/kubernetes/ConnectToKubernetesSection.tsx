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

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { KubernetesInstanceStatus } from "./KubernetesInstanceStatus";
import { ConnectToKubernetesSimple } from "./ConnectToKubernetesSimple";
import { AccountsDispatchActionKind, AccountsSection, useAccounts, useAccountsDispatch } from "../AccountsContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { KubernetesAuthSession } from "../../authSessions/AuthSessionApi";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import { AuthSessionDescriptionList } from "../../authSessions/AuthSessionsList";
import { KieSandboxKubernetesService } from "../../devDeployments/services/kubernetes/KieSandboxKubernetesService";
import { EMPTY_KUBERNETES_CONNECTION } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { ConnectToLocalKubernetesClusterWizard } from "./ConnectToLocalKubernetesClusterWizard";
import { KubernetesService, isKubernetesConnectionValid } from "../../devDeployments/services/KubernetesService";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";

export enum KubernetesSettingsTabMode {
  SIMPLE,
  WIZARD,
}

export function ConnectToKubernetesSection() {
  const accounts = useAccounts();
  const accountsDispatch = useAccountsDispatch();

  const [mode, setMode] = useState(KubernetesSettingsTabMode.SIMPLE);
  const [newAuthSession, setNewAuthSession] = useState<KubernetesAuthSession>();
  const [status, setStatus] = useState(KubernetesInstanceStatus.DISCONNECTED);
  const [connection, setConnection] = useState(EMPTY_KUBERNETES_CONNECTION);
  const [kieSandboxKubernetesService, setKieSandboxKubernetesService] = useState<KieSandboxKubernetesService>();
  const [isLoadingService, setIsLoadingService] = useState(false);
  const selectedKubernetesSession =
    accounts.section === AccountsSection.CONNECT_TO_KUBERNETES ? accounts.selectedAuthSession : undefined;

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (isKubernetesConnectionValid(connection)) {
          setIsLoadingService(true);
          KubernetesService.getK8sApiServerEndpointsMap({
            connection,
          })
            .then((k8sApiServerEndpointsByResourceKind) => {
              if (canceled.get()) {
                return;
              }
              setKieSandboxKubernetesService(
                new KieSandboxKubernetesService({ connection, k8sApiServerEndpointsByResourceKind })
              );
            })
            .catch((e) => {
              if (canceled.get()) {
                return;
              }
              console.error(e);
              setKieSandboxKubernetesService(undefined);
              setStatus(KubernetesInstanceStatus.DISCONNECTED);
            });
          setIsLoadingService(false);
        }
      },
      [connection]
    )
  );

  useEffect(() => {
    (async () => {
      if (isKubernetesConnectionValid(connection)) {
        setIsLoadingService(true);
        try {
          const k8sApiServerEndpointsByResourceKind = await KubernetesService.getK8sApiServerEndpointsMap({
            connection,
          });
          setKieSandboxKubernetesService(
            new KieSandboxKubernetesService({ connection, k8sApiServerEndpointsByResourceKind })
          );
        } catch (e) {
          console.error(e);
          setKieSandboxKubernetesService(undefined);
          setStatus(KubernetesInstanceStatus.DISCONNECTED);
        }
        setIsLoadingService(false);
      }
    })();
  }, [connection]);

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
              kieSandboxKubernetesService={kieSandboxKubernetesService}
              isLoadingService={isLoadingService}
              selectedAuthSession={selectedKubernetesSession}
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
              kieSandboxKubernetesService={kieSandboxKubernetesService}
              isLoadingService={isLoadingService}
              selectedAuthSession={selectedKubernetesSession}
            />
          )}
        </>
      )}
    </>
  );
}
