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
import { useDevDeployments as useDevDeployments } from "./DevDeploymentsContext";
import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { DevDeploymentsDropdownItem } from "./DevDeploymentsDropdownItem";
import { PficonSatelliteIcon } from "@patternfly/react-icons/dist/js/icons/pficon-satellite-icon";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { ResponsiveDropdown } from "../ResponsiveDropdown/ResponsiveDropdown";
import { ResponsiveDropdownToggle } from "../ResponsiveDropdown/ResponsiveDropdownToggle";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import CaretDownIcon from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { AuthSessionSelect } from "../accounts/authSessions/AuthSessionSelect";
import { SelectPosition } from "@patternfly/react-core/dist/js/components/Select";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../accounts/AccountsContext";
import { KieSandboxOpenShiftDeployedModel } from "../openshift/KieSandboxOpenShiftService";
import { useLivePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useAuthSession, useAuthSessions } from "../accounts/authSessions/AuthSessionsContext";
import { openshiftAuthSessionSelectFilter } from "../accounts/authSessions/CompatibleAuthSessions";
import { AuthProviderGroup } from "../accounts/authProviders/AuthProvidersApi";

const DEPLOYMENTS_REFRESH_POLLING_TIME = 2500;

export function DevDeploymentsDropdown() {
  const devDeployments = useDevDeployments();
  const extendedServices = useExtendedServices();
  const accountsDispatch = useAccountsDispatch();
  const [authSessionId, setAuthSessionId] = useState<string | undefined>();
  const { authSessions } = useAuthSessions();
  const { authSession } = useAuthSession(authSessionId);

  const suggestedAuthSessionForDeployment = useMemo(() => {
    return [...authSessions.values()].find((authSession) => authSession.type === "openshift");
  }, [authSessions]);

  useEffect(() => {
    if (suggestedAuthSessionForDeployment) {
      setAuthSessionId(suggestedAuthSessionForDeployment.id);
    }

    if (authSessionId && !authSessions.has(authSessionId)) {
      setAuthSessionId(undefined);
    }
  }, [authSessionId, authSessions, suggestedAuthSessionForDeployment]);

  const deployments = useLivePromiseState<KieSandboxOpenShiftDeployedModel[]>(
    useMemo(() => {
      if (!authSession || authSession.type !== "openshift") {
        return { error: "Can't load dev deployments with this AuthSession." };
      }

      return () => devDeployments.loadDeployments({ connection: authSession });
    }, [authSession, devDeployments])
  );

  const items = useMemo(() => {
    if ((deployments.data?.length ?? 0) === 0) {
      return [
        <DropdownItem key="disabled link" isDisabled>
          <Bullseye>
            <EmptyState>
              <EmptyStateIcon icon={PficonSatelliteIcon} />
              <Title headingLevel="h4" size="md">
                {`No dev deployments found.`}
              </Title>
            </EmptyState>
          </Bullseye>
        </DropdownItem>,
      ];
    } else if (authSession) {
      return [
        deployments.data
          ?.sort((a, b) => b.creationTimestamp.getTime() - a.creationTimestamp.getTime())
          .map((deployment, i) => {
            return (
              <DevDeploymentsDropdownItem
                key={deployment.creationTimestamp.getTime()}
                id={i}
                deployment={deployment}
                cloudAuthSession={authSession}
              />
            );
          }),
      ];
    }
  }, [authSession, deployments.data]);

  return (
    <>
      <ResponsiveDropdown
        position={"right"}
        onSelect={() => devDeployments.setDeploymentsDropdownOpen(false)}
        onClose={() => devDeployments.setDeploymentsDropdownOpen(false)}
        toggle={
          <ResponsiveDropdownToggle
            toggleIndicator={null}
            onToggle={() => devDeployments.setDeploymentsDropdownOpen((dropdownOpen) => !dropdownOpen)}
            className={"kie-tools--masthead-hoverable-dark"}
          >
            <PficonSatelliteIcon
              color={extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING ? "gray" : undefined}
            />
            &nbsp;&nbsp; Dev deployments &nbsp;&nbsp;
            <CaretDownIcon
              color={extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING ? "gray" : undefined}
            />
          </ResponsiveDropdownToggle>
        }
        isOpen={devDeployments.isDeploymentsDropdownOpen}
        isPlain={true}
        className="kogito--editor__dev-deployments-dropdown"
        title="Dev deployments"
        dropdownItems={[
          <div style={{ padding: "8px 16px", minWidth: "400px" }} key={"cloud-auth-session-select"}>
            <AuthSessionSelect
              position={SelectPosition.right}
              authSessionId={authSessionId}
              setAuthSessionId={(newAuthSessionId) => {
                setAuthSessionId(newAuthSessionId);
                setTimeout(() => {
                  accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                  devDeployments.setDeploymentsDropdownOpen(true);
                }, 0);
              }}
              isPlain={false}
              title={"Select cloud provider..."}
              filter={openshiftAuthSessionSelectFilter()}
              showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.CLOUD}
            />
          </div>,
          ...(items ?? []),
        ]}
      />
    </>
  );
}
