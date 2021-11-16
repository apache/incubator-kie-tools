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
import { useCallback, useMemo } from "react";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { useOnlineI18n } from "../../i18n";
import { useDmnDevSandbox } from "./DmnDevSandboxContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { SettingsTabs } from "../../settings/SettingsModalBody";
import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownSeparator,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { DmnDevSandboxDeploymentDropdownItem } from "./DmnDevSandboxDeploymentDropdownItem";
import { OpenshiftIcon } from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { TopologyIcon } from "@patternfly/react-icons/dist/js/icons/topology-icon";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";

export function OpenshiftDeploymentsDropdown() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const { i18n } = useOnlineI18n();
  const dmnDevSandbox = useDmnDevSandbox();

  const isDmnDevSandboxConnected = useMemo(
    () => settings.openshift.status === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status]
  );

  const openOpenShiftSettings = useCallback(() => {
    settingsDispatch.open(SettingsTabs.OPENSHIFT);
  }, [settingsDispatch]);

  const items = useMemo(() => {
    const common = isDmnDevSandboxConnected
      ? [
          <DropdownSeparator key={"dropdown-dmn-dev-sandbox-separator-deployments-1"} />,
          <DropdownItem
            style={{ minWidth: "400px" }}
            key={"dropdown-dmn-dev-sandbox-setup-as"}
            component={"button"}
            onClick={openOpenShiftSettings}
            ouiaId={"setup-as-dmn-dev-sandbox-dropdown-button"}
            description={"Change..."}
          >
            {i18n.dmnDevSandbox.dropdown.connectedTo(settings.openshift.config.namespace)}
          </DropdownItem>,
          <DropdownSeparator key={"dropdown-dmn-dev-sandbox-separator-deployments-2"} />,
        ]
      : [];

    if (dmnDevSandbox.deployments.length === 0) {
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
        dmnDevSandbox.deployments
          .sort((a, b) => b.creationTimestamp.getTime() - a.creationTimestamp.getTime())
          .map((deployment, i) => {
            return (
              <DmnDevSandboxDeploymentDropdownItem
                key={deployment.creationTimestamp.getTime()}
                id={i}
                deployment={deployment}
              />
            );
          }),
      ];
    }
  }, [
    dmnDevSandbox.deployments,
    i18n,
    isDmnDevSandboxConnected,
    openOpenShiftSettings,
    settings.openshift.config.namespace,
  ]);

  return (
    <>
      <Tooltip
        className="kogito--editor__light-tooltip"
        content={<div>{`You're not connected to any OpenShift instance.`}</div>}
        trigger={!isDmnDevSandboxConnected ? "mouseenter" : ""}
        position="left"
      >
        <Dropdown
          position={"right"}
          onSelect={() => dmnDevSandbox.setDeploymentsDropdownOpen(false)}
          toggle={
            <DropdownToggle
              toggleIndicator={null}
              onToggle={(isOpen) => dmnDevSandbox.setDeploymentsDropdownOpen(isDmnDevSandboxConnected && isOpen)}
            >
              <OpenshiftIcon color={!isDmnDevSandboxConnected ? "gray" : undefined} />
            </DropdownToggle>
          }
          isOpen={dmnDevSandbox.isDeploymentsDropdownOpen}
          isPlain={true}
          dropdownItems={[
            <DropdownGroup key={"openshift-deployments-group"} label={"OpenShift deployments"}>
              {items}
            </DropdownGroup>,
          ]}
        />
      </Tooltip>
    </>
  );
}
