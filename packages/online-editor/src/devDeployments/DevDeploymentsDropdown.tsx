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
import { DependentFeature, useExtendedServices } from "../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../extendedServices/ExtendedServicesStatus";
import CaretDownIcon from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { AuthSessionSelect } from "../authSessions/AuthSessionSelect";
import { SelectPosition } from "@patternfly/react-core/dist/js/components/Select";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../accounts/AccountsContext";
import { PromiseStateStatus, useLivePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useAuthSession, useAuthSessions } from "../authSessions/AuthSessionsContext";
import { cloudAuthSessionSelectFilter } from "../authSessions/CompatibleAuthSessions";
import { AuthProviderGroup } from "../authProviders/AuthProvidersApi";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useOnlineI18n } from "../i18n";
import TrashIcon from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { KieSandboxDeployedModel } from "./services/types";

const REFRESH_COUNTDOWN_INITIAL_VALUE_IN_SECONDS = 30;

export function DevDeploymentsDropdown() {
  const { i18n } = useOnlineI18n();
  const devDeployments = useDevDeployments();
  const extendedServices = useExtendedServices();
  const accountsDispatch = useAccountsDispatch();
  const [authSessionId, setAuthSessionId] = useState<string | undefined>();
  const { authSessions } = useAuthSessions();
  const { authSession } = useAuthSession(authSessionId);
  const [refreshCountdownInSeconds, setRefreshCountdownInSeconds] = useState(
    REFRESH_COUNTDOWN_INITIAL_VALUE_IN_SECONDS
  );

  const suggestedAuthSessionForDeployment = useMemo(() => {
    return [...authSessions.values()].find(
      (authSession) => authSession.type === "openshift" || authSession.type === "kubernetes"
    );
  }, [authSessions]);

  useEffect(() => {
    setAuthSessionId((currentAuthSessionId) => {
      if (suggestedAuthSessionForDeployment) {
        return suggestedAuthSessionForDeployment.id;
      } else if (currentAuthSessionId && !authSessions.has(currentAuthSessionId)) {
        return undefined;
      }
      return currentAuthSessionId;
    });
  }, [authSessions, suggestedAuthSessionForDeployment]);

  const [deployments, refresh] = useLivePromiseState<KieSandboxDeployedModel[]>(
    useMemo(() => {
      if (!authSession || (authSession.type !== "openshift" && authSession.type !== "kubernetes")) {
        return { error: "Can't load Dev deployments with this AuthSession." };
      }

      return () => {
        setRefreshCountdownInSeconds(REFRESH_COUNTDOWN_INITIAL_VALUE_IN_SECONDS);
        return devDeployments.loadDeployments({ authSession });
      };
    }, [authSession, devDeployments])
  );

  const deleteAllDeployments = useCallback(() => {
    if (!authSessionId) {
      return;
    }
    devDeployments.setConfirmDeleteModalState({
      isOpen: true,
      resourceNames: (deployments.data ?? []).map((s) => s.resourceName),
      cloudAuthSessionId: authSessionId,
    });
  }, [authSessionId, deployments.data, devDeployments]);

  useEffect(() => {
    if (deployments.status === PromiseStateStatus.PENDING) {
      return;
    }

    const interval = setInterval(() => {
      setRefreshCountdownInSeconds((prev) => prev - 1);
    }, 1000);

    return () => {
      clearInterval(interval);
    };
  }, [deployments.status]);

  useEffect(() => {
    if (refreshCountdownInSeconds > 0) {
      return;
    }
    refresh(new Holder(false));
  }, [refresh, refreshCountdownInSeconds]);

  const items = useMemo(() => {
    if (authSession) {
      if (deployments.status === PromiseStateStatus.PENDING) {
        return [
          <DropdownItem key={"sk1"} onClick={(e) => e.stopPropagation()}>
            <Skeleton width={"80%"} style={{ marginBottom: "4px" }} />
            <Skeleton width={"50%"} />
          </DropdownItem>,
          <DropdownItem key={"sk2"} onClick={(e) => e.stopPropagation()}>
            <Skeleton width={"80%"} style={{ marginBottom: "4px" }} />
            <Skeleton width={"50%"} />
          </DropdownItem>,
          <DropdownItem key={"sk3"} onClick={(e) => e.stopPropagation()}>
            <Skeleton width={"80%"} style={{ marginBottom: "4px" }} />
            <Skeleton width={"50%"} />
          </DropdownItem>,
        ];
      } else if (deployments.status === PromiseStateStatus.REJECTED) {
        return [
          <DropdownItem key="disabled link" isDisabled>
            <Bullseye>
              <EmptyState>
                <EmptyStateIcon icon={PficonSatelliteIcon} />
                <Title headingLevel="h4" size="md">
                  {`Error fetching Dev deployments.`}
                </Title>
              </EmptyState>
            </Bullseye>
          </DropdownItem>,
        ];
      } else if (deployments.status === PromiseStateStatus.RESOLVED && deployments.data.length === 0) {
        return [
          <DropdownItem key="disabled link" isDisabled>
            <Bullseye>
              <EmptyState>
                <EmptyStateIcon icon={PficonSatelliteIcon} />
                <Title headingLevel="h4" size="md">
                  {`No Dev deployments found`}
                </Title>
              </EmptyState>
            </Bullseye>
          </DropdownItem>,
        ];
      } else {
        return [
          deployments.data
            .sort((a, b) => b.creationTimestamp.getTime() - a.creationTimestamp.getTime())
            .map((deployment, i) => (
              <DevDeploymentsDropdownItem
                key={deployment.creationTimestamp.getTime()}
                id={i}
                deployment={deployment}
                cloudAuthSession={authSession}
              />
            )),
          <Divider key={"delete-all-separator"} inset={{ default: "insetMd" }} />,
          <DropdownItem
            key={"delete-all-deployments-dropdown-button"}
            component={"button"}
            onClick={deleteAllDeployments}
            ouiaId={"delete-all-deployments-dropdown-button"}
            style={{ color: "var(--pf-global--danger-color--100)" }}
            icon={
              <small>
                <TrashIcon size={"sm"} />
              </small>
            }
          >
            <small>{i18n.devDeployments.dropdown.deleteDeployments}</small>
          </DropdownItem>,
        ];
      }
    } else {
      return [
        <div key={"empty-deployments"}>
          <EmptyState>
            <EmptyStateIcon icon={PficonSatelliteIcon} />
            <Title headingLevel="h4" size="md" style={{ color: "darkgray" }}>
              {`Choose a Cloud provider to see your Dev deployments.`}
            </Title>
          </EmptyState>
        </div>,
      ];
    }
  }, [authSession, deleteAllDeployments, deployments.data, deployments.status, i18n]);

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
              color={extendedServices.status !== ExtendedServicesStatus.RUNNING ? "gray" : undefined}
            />
            &nbsp;&nbsp; Dev deployments &nbsp;&nbsp;
            <CaretDownIcon color={extendedServices.status !== ExtendedServicesStatus.RUNNING ? "gray" : undefined} />
          </ResponsiveDropdownToggle>
        }
        isOpen={devDeployments.isDeploymentsDropdownOpen}
        isPlain={true}
        className="kogito--editor__dev-deployments-dropdown"
        title="Dev deployments"
        dropdownItems={
          extendedServices.status !== ExtendedServicesStatus.RUNNING
            ? [
                <DropdownItem
                  key="setup-extended-services"
                  style={{ maxWidth: "400px", minWidth: "400px" }}
                  onClick={() => {
                    setTimeout(() => {
                      extendedServices.setInstallTriggeredBy(DependentFeature.DEV_DEPLOYMENTS);
                      extendedServices.setModalOpen(true);
                    });
                  }}
                >
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
                        {`Please setup Extended Services to be able to see your Dev deployments`}
                      </Title>
                      <br />
                      <Button variant={ButtonVariant.link}>Setup...</Button>
                    </EmptyState>
                  </Bullseye>
                </DropdownItem>,
              ]
            : [
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
                    title={"Select Cloud provider..."}
                    filter={cloudAuthSessionSelectFilter()}
                    showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.CLOUD}
                  />
                  {authSessionId && (
                    <>
                      <br />
                      <br />
                      <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
                        <small style={{ color: "darkgray" }}>
                          {deployments.status !== PromiseStateStatus.PENDING && (
                            <i>{`Refreshing in ${refreshCountdownInSeconds} seconds...`}</i>
                          )}
                        </small>
                        <Button
                          variant={ButtonVariant.link}
                          onClick={() => refresh(new Holder(false))}
                          style={{ padding: 0 }}
                          isDisabled={deployments.status === PromiseStateStatus.PENDING}
                        >
                          <small>
                            {deployments.status === PromiseStateStatus.PENDING ? "Refreshing..." : "Refresh"}
                          </small>
                        </Button>
                      </Flex>
                      <Divider />
                    </>
                  )}
                </div>,
                ...(items ?? []),
              ]
        }
      />
    </>
  );
}
