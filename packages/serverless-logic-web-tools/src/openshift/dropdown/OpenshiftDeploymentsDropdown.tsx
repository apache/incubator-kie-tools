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
import { DropdownItem, DropdownSeparator } from "@patternfly/react-core/dist/js/components/Dropdown";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { OpenshiftIcon } from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import { TopologyIcon } from "@patternfly/react-icons/dist/js/icons/topology-icon";
import { useCallback, useMemo, useEffect, useState } from "react";
import { ResponsiveDropdown } from "../../ResponsiveDropdown/ResponsiveDropdown";
import { ResponsiveDropdownToggle } from "../../ResponsiveDropdown/ResponsiveDropdownToggle";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { useOpenShift } from "../OpenShiftContext";
import { OpenShiftDeploymentDropdownItem } from "./OpenShiftDeploymentDropdownItem";
import { OpenShiftInstanceStatus } from "../OpenShiftInstanceStatus";
import { WebToolsOpenShiftDeployedModel } from "../deploy/types";
import { useEnv } from "../../env/EnvContext";
import { PromiseStateStatus, useLivePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useDevModeDispatch } from "../swfDevMode/DevModeContext";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { routes } from "../../navigation/Routes";
import { useHistory } from "react-router";

const REFRESH_COUNTDOWN_INITIAL_VALUE_IN_SECONDS = 20;

export function OpenshiftDeploymentsDropdown() {
  const { env } = useEnv();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const openshift = useOpenShift();
  const devModeDispatch = useDevModeDispatch();
  const [refreshCountdownInSeconds, setRefreshCountdownInSeconds] = useState(
    REFRESH_COUNTDOWN_INITIAL_VALUE_IN_SECONDS
  );
  const history = useHistory();

  const isConnected = useMemo(
    () => settings.openshift.status === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status]
  );

  const openOpenShiftSettings = useCallback(() => {
    history.push(routes.settings.openshift.path({}));
  }, [history]);

  const [deployments, refresh] = useLivePromiseState<WebToolsOpenShiftDeployedModel[]>(
    useMemo(() => {
      if (settings.openshift.status !== OpenShiftInstanceStatus.CONNECTED) {
        return { error: "Can't load deployments." };
      }
      return async () => {
        setRefreshCountdownInSeconds(REFRESH_COUNTDOWN_INITIAL_VALUE_IN_SECONDS);
        const res = await Promise.all([openshift.loadDeployments(), devModeDispatch.loadDeployments()]);
        return res.flat();
      };
    }, [devModeDispatch, openshift, settings.openshift.status])
  );

  useEffect(() => {
    if (deployments.status === PromiseStateStatus.PENDING) {
      return;
    }

    const interval = window.setInterval(() => {
      setRefreshCountdownInSeconds((prev) => prev - 1);
    }, 1000);

    return () => {
      window.clearInterval(interval);
    };
  }, [deployments.status]);

  useEffect(() => {
    if (refreshCountdownInSeconds > 0) {
      return;
    }
    refresh(new Holder(false));
  }, [refresh, refreshCountdownInSeconds]);

  const connectionItem = useMemo(
    () => [
      <DropdownItem
        key={"dropdown-openshift-setup-as"}
        component={"button"}
        onClick={openOpenShiftSettings}
        ouiaId={"setup-as-openshift-dropdown-button"}
        description={"Change..."}
      >
        {`Connected to ${settings.openshift.config.namespace}`}
      </DropdownItem>,
      <DropdownSeparator key={"dropdown-openshift-separator-deployments-2"} />,
    ],
    [openOpenShiftSettings, settings.openshift.config.namespace]
  );

  const deploymentItems = useMemo(() => {
    if (deployments.status === PromiseStateStatus.PENDING) {
      return [
        Array.from({ length: 3 }, (_, idx) => (
          <DropdownItem key={`deployment-skeleton-${idx}`} isDisabled={true}>
            <Skeleton width={"80%"} style={{ marginBottom: "4px" }} />
            <Skeleton width={"50%"} />
          </DropdownItem>
        )),
      ];
    } else if (deployments.status === PromiseStateStatus.REJECTED) {
      return [
        <DropdownItem key="disabled link" isDisabled>
          <Bullseye>
            <EmptyState>
              <EmptyStateIcon icon={TopologyIcon} />
              <Title headingLevel="h4" size="md">
                {"Error fetching deployments"}
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
              <EmptyStateIcon icon={TopologyIcon} />
              <Title headingLevel="h4" size="md">
                {"No deployments found"}
              </Title>
            </EmptyState>
          </Bullseye>
        </DropdownItem>,
      ];
    } else {
      const dropdownItems = [];

      const [devModeDeployments, userDeployments] = deployments.data
        .sort((a, b) => b.creationTimestamp.getTime() - a.creationTimestamp.getTime())
        .reduce(
          ([devModeDeployments, userDeployments], d: WebToolsOpenShiftDeployedModel) =>
            d.devMode ? [[...devModeDeployments, d], userDeployments] : [devModeDeployments, [...userDeployments, d]],
          [[] as WebToolsOpenShiftDeployedModel[], [] as WebToolsOpenShiftDeployedModel[]]
        );

      if (devModeDeployments.length > 0) {
        dropdownItems.push(
          <OpenShiftDeploymentDropdownItem
            key={devModeDeployments[0].creationTimestamp.getTime()}
            id={0}
            deployment={devModeDeployments[0]}
            refreshDeployments={refresh}
          />
        );

        if (userDeployments.length > 0) {
          dropdownItems.push(<DropdownSeparator key={"dropdown-openshift-separator-deployments-2"} />);
        }
      }

      return [
        ...dropdownItems,
        userDeployments.map((deployment, i) => {
          return (
            <OpenShiftDeploymentDropdownItem
              key={deployment.creationTimestamp.getTime()}
              id={i + 1}
              deployment={deployment}
              refreshDeployments={refresh}
            />
          );
        }),
      ];
    }
  }, [deployments.data, deployments.status, refresh]);

  const onDeploymensDropdownToggle = useCallback(() => {
    if (!isConnected) {
      history.push(routes.settings.openshift.path({}));
    }
    openshift.setDeploymentsDropdownOpen((dropdownOpen) => isConnected && !dropdownOpen);
  }, [history, isConnected, openshift]);

  return (
    <>
      <Tooltip
        className="kogito--editor__light-tooltip"
        content={<div>{`You're not connected to any OpenShift instance.`}</div>}
        trigger={!isConnected ? "mouseenter" : ""}
        position="auto"
      >
        <ResponsiveDropdown
          position={"right"}
          onSelect={() => openshift.setDeploymentsDropdownOpen(false)}
          onClose={() => openshift.setDeploymentsDropdownOpen(false)}
          toggle={
            <ResponsiveDropdownToggle
              toggleIndicator={null}
              onToggle={onDeploymensDropdownToggle}
              className={"kie-tools--masthead-hoverable-dark"}
            >
              <OpenshiftIcon color={!isConnected ? "gray" : undefined} />
            </ResponsiveDropdownToggle>
          }
          isOpen={openshift.isDeploymentsDropdownOpen}
          isPlain={true}
          className="kogito--editor__openshift-deployments-dropdown"
          title="OpenShift deployments"
          dropdownItems={[
            ...connectionItem,
            <>
              <Flex
                style={{ padding: "8px 16px", minWidth: "400px" }}
                justifyContent={{ default: "justifyContentSpaceBetween" }}
              >
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
                  <small>{deployments.status === PromiseStateStatus.PENDING ? "Refreshing..." : "Refresh"}</small>
                </Button>
              </Flex>
              <Divider />
            </>,
            ...deploymentItems,
          ]}
        />
      </Tooltip>
    </>
  );
}
