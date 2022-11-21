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
import { useMemo } from "react";
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

export function DevDeploymentsDropdown() {
  const devDeployments = useDevDeployments();
  const extendedServices = useExtendedServices();

  const items = useMemo(() => {
    if (devDeployments.deployments.length === 0) {
      return [
        <DropdownItem key="disabled link" isDisabled>
          <Bullseye>
            <EmptyState>
              <EmptyStateIcon icon={PficonSatelliteIcon} />
              <Title headingLevel="h4" size="md">
                {`No deployments yet.`}
              </Title>
            </EmptyState>
          </Bullseye>
        </DropdownItem>,
      ];
    } else {
      return [
        devDeployments.deployments
          .sort((a, b) => b.creationTimestamp.getTime() - a.creationTimestamp.getTime())
          .map((deployment, i) => {
            return (
              <DevDeploymentsDropdownItem key={deployment.creationTimestamp.getTime()} id={i} deployment={deployment} />
            );
          }),
      ];
    }
  }, [devDeployments.deployments]);

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
        dropdownItems={items}
      />
    </>
  );
}
