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

import React, { useMemo, useState, useCallback } from "react";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import { KieSandboxOpenShiftService } from "../../devDeployments/services/openshift/KieSandboxOpenShiftService";
import { ConnecToOpenShiftSimple } from "./ConnecToOpenShiftSimple";
import { ConnectToDeveloperSandboxForRedHatOpenShiftWizard } from "./ConnectToDeveloperSandboxForRedHatOpenShiftWizard";
import { EMPTY_KUBERNETES_CONNECTION } from "@kie-tools-core/kubernetes-bridge/dist/service/KubernetesConnection";
import { AccountsDispatchActionKind, AccountsSection, useAccounts, useAccountsDispatch } from "../AccountsContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { OpenShiftAuthSession } from "../../authSessions/AuthSessionApi";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import { AuthSessionDescriptionList } from "../../authSessions/AuthSessionsList";
import { useEnv } from "../../env/hooks/EnvContext";
import { KubernetesService, isKubernetesConnectionValid } from "../../devDeployments/services/KubernetesService";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";

export enum OpenShiftSettingsTabMode {
  SIMPLE,
  WIZARD,
}

export function ConnectToOpenShiftSection() {
  const { env } = useEnv();
  const accounts = useAccounts();
  const accountsDispatch = useAccountsDispatch();

  const [mode, setMode] = useState(OpenShiftSettingsTabMode.SIMPLE);
  const [newAuthSession, setNewAuthSession] = useState<OpenShiftAuthSession>();
  const [status, setStatus] = useState(OpenShiftInstanceStatus.DISCONNECTED);
  const [connection, setConnection] = useState(EMPTY_KUBERNETES_CONNECTION);
  const selectedOpenShiftSession =
    accounts.section === AccountsSection.CONNECT_TO_OPENSHIFT ? accounts.selectedAuthSession : undefined;
  const [kieSandboxOpenShiftService, setKieSandboxOpenShiftService] = useState<KieSandboxOpenShiftService>();
  const [isLoadingService, setIsLoadingService] = useState(false);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (isKubernetesConnectionValid(connection)) {
          setIsLoadingService(true);
          KubernetesService.getK8sApiServerEndpointsMap({
            connection,
            proxyUrl: env.KIE_SANDBOX_CORS_PROXY_URL,
          })
            .then((k8sApiServerEndpointsByResourceKind) => {
              if (canceled.get()) {
                return;
              }
              setKieSandboxOpenShiftService(
                new KieSandboxOpenShiftService({
                  connection,
                  k8sApiServerEndpointsByResourceKind,
                  proxyUrl: env.KIE_SANDBOX_CORS_PROXY_URL,
                })
              );
              setIsLoadingService(false);
            })
            .catch((e) => {
              if (canceled.get()) {
                return;
              }
              console.error(e);
              setKieSandboxOpenShiftService(undefined);
              setStatus(OpenShiftInstanceStatus.DISCONNECTED);
              setIsLoadingService(false);
            });
        }
      },
      [connection, env.KIE_SANDBOX_CORS_PROXY_URL]
    )
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
              kieSandboxOpenShiftService={kieSandboxOpenShiftService}
              isLoadingService={isLoadingService}
              selectedAuthSession={selectedOpenShiftSession}
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
              kieSandboxOpenShiftService={kieSandboxOpenShiftService}
              isLoadingService={isLoadingService}
              selectedAuthSession={selectedOpenShiftSession}
            />
          )}
        </>
      )}
    </>
  );
}
