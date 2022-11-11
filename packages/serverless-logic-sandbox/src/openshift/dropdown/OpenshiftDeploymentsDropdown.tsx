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
import { useCallback, useMemo } from "react";
import { ResponsiveDropdown } from "../../ResponsiveDropdown/ResponsiveDropdown";
import { ResponsiveDropdownToggle } from "../../ResponsiveDropdown/ResponsiveDropdownToggle";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { SettingsTabs } from "../../settings/SettingsModalBody";
import { useOpenShift } from "../OpenShiftContext";
import { OpenShiftDeploymentDropdownItem } from "./OpenShiftDeploymentDropdownItem";
import { OpenShiftInstanceStatus } from "../OpenShiftInstanceStatus";

export function OpenshiftDeploymentsDropdown() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const openshift = useOpenShift();

  const isConnected = useMemo(
    () => settings.openshift.status === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status]
  );

  const openOpenShiftSettings = useCallback(() => {
    settingsDispatch.open(SettingsTabs.OPENSHIFT);
  }, [settingsDispatch]);

  const items = useMemo(() => {
    const common = isConnected
      ? [
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
        ]
      : [];

    if (openshift.deployments.length === 0) {
      return [
        ...common,
        <DropdownItem key="disabled link" isDisabled>
          <Bullseye>
            <EmptyState>
              <EmptyStateIcon icon={TopologyIcon} />
              <Title headingLevel="h4" size="md">
                {`No deployments yet.`}
              </Title>
            </EmptyState>
          </Bullseye>
        </DropdownItem>,
      ];
    } else {
      return [
        ...common,
        openshift.deployments
          .sort((a, b) => b.creationTimestamp.getTime() - a.creationTimestamp.getTime())
          .map((deployment, i) => {
            return (
              <OpenShiftDeploymentDropdownItem
                key={deployment.creationTimestamp.getTime()}
                id={i}
                deployment={deployment}
              />
            );
          }),
      ];
    }
  }, [openshift.deployments, isConnected, openOpenShiftSettings, settings.openshift.config.namespace]);

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
              onToggle={() => openshift.setDeploymentsDropdownOpen((dropdownOpen) => isConnected && !dropdownOpen)}
              className={"kie-tools--masthead-hoverable-dark"}
            >
              <OpenshiftIcon color={!isConnected ? "gray" : undefined} />
            </ResponsiveDropdownToggle>
          }
          isOpen={openshift.isDeploymentsDropdownOpen}
          isPlain={true}
          className="kogito--editor__openshift-deployments-dropdown"
          title="OpenShift deployments"
          dropdownItems={items}
        />
      </Tooltip>
    </>
  );
}
