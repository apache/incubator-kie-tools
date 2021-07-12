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

import { DropdownGroup, DropdownItem, DropdownSeparator } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { DeploymentDropdownItem } from "../DmnDevSandbox/DeploymentDropdownItem";
import { useDmnDevSandbox } from "../DmnDevSandbox/DmnDevSandboxContext";
import { DmnDevSandboxInstanceStatus } from "../DmnDevSandbox/DmnDevSandboxInstanceStatus";
import { useDmnRunner } from "../DmnRunner/DmnRunnerContext";
import { useKieToolingExtendedServices } from "./KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";

export function useDropdownItems(dropdownId: string) {
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const dmnRunner = useDmnRunner();
  const dmnDevSandbox = useDmnDevSandbox();

  const isKieToolingExtendedServicesRunning = useMemo(
    () => kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING,
    [kieToolingExtendedServices.status]
  );

  const onInstallKieToolingExtendedServices = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    kieToolingExtendedServices.setModalOpen(true);
  }, [kieToolingExtendedServices]);

  const onToggleDmnRunner = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    dmnRunner.setDrawerExpanded(!dmnRunner.isDrawerExpanded);
  }, [dmnRunner, kieToolingExtendedServices]);

  const onDevSandboxSetup = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    dmnDevSandbox.setConfigModalOpen(true);
  }, [dmnDevSandbox, kieToolingExtendedServices]);

  const onDevSandboxDeploy = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    dmnDevSandbox.setConfirmDeployModalOpen(true);
  }, [dmnDevSandbox, kieToolingExtendedServices]);

  return useCallback(() => {
    const items = [
      <DropdownItem
        key={`kie-tooling-extended-services-dropdown-${dropdownId}-install`}
        component={"button"}
        onClick={onInstallKieToolingExtendedServices}
        isDisabled={isKieToolingExtendedServicesRunning}
        ouiaId="install-dropdown-button"
      >
        {isKieToolingExtendedServicesRunning
          ? i18n.kieToolingExtendedServices.connectedOnPort(kieToolingExtendedServices.port)
          : i18n.terms.install}
      </DropdownItem>,
      <DropdownGroup key={"dmn-runner-group"} label={i18n.names.dmnRunner}>
        <Tooltip
          data-testid={"dmn-runner-install-tooltip-for-open-toggle"}
          content={i18n.kieToolingExtendedServices.dropdown.tooltip.installForDmnRunner}
          trigger={!isKieToolingExtendedServicesRunning ? "mouseenter click" : ""}
          position="left"
        >
          <DropdownItem
            key={`kie-tooling-extended-services-dropdown-${dropdownId}-dmn-runner-toggle`}
            component={"button"}
            onClick={onToggleDmnRunner}
            isDisabled={!isKieToolingExtendedServicesRunning}
            ouiaId="toggle-dmn-runner-dropdown-button"
            className="kogito--dmn-runner-button"
          >
            <Text>{dmnRunner.isDrawerExpanded ? i18n.terms.close : i18n.terms.open}</Text>
          </DropdownItem>
        </Tooltip>
      </DropdownGroup>,
      <DropdownGroup
        key={"dmn-dev-sandbox-group"}
        label={
          <>
            {i18n.names.dmnDevSandbox}
            {isKieToolingExtendedServicesRunning &&
              dmnDevSandbox.instanceStatus === DmnDevSandboxInstanceStatus.DISCONNECTED && (
                <Tooltip data-testid={"dmn-dev-sandbox-setup-tooltip"} content={i18n.dmnDevSandbox.common.setupFirst}>
                  <ExclamationTriangleIcon className="pf-u-ml-sm" />
                </Tooltip>
              )}
          </>
        }
      >
        <Tooltip
          data-testid={"dmn-dev-sandbox-install-tooltip-for-setup"}
          content={i18n.kieToolingExtendedServices.dropdown.tooltip.installForDmnDevSandbox}
          trigger={!isKieToolingExtendedServicesRunning ? "mouseenter click" : ""}
          position="left"
        >
          <DropdownItem
            key={`kie-tooling-extended-services-dropdown-${dropdownId}-dmn-dev-sandbox-setup`}
            component={"button"}
            onClick={onDevSandboxSetup}
            isDisabled={!isKieToolingExtendedServicesRunning}
            ouiaId="setup-dmn-dev-sandbox-dropdown-button"
          >
            {i18n.terms.setup}
          </DropdownItem>
        </Tooltip>
        <Tooltip
          data-testid={"dmn-dev-sandbox-install-tooltip-for-deploy"}
          content={i18n.kieToolingExtendedServices.dropdown.tooltip.installForDmnDevSandbox}
          trigger={!isKieToolingExtendedServicesRunning ? "mouseenter click" : ""}
          position="left"
        >
          <DropdownItem
            key={`kie-tooling-extended-services-dropdown-${dropdownId}-dmn-dev-sandbox-deploy`}
            component={"button"}
            onClick={onDevSandboxDeploy}
            isDisabled={
              !isKieToolingExtendedServicesRunning ||
              dmnDevSandbox.instanceStatus !== DmnDevSandboxInstanceStatus.CONNECTED
            }
            ouiaId="deploy-to-dmn-dev-sandbox-dropdown-button"
          >
            {i18n.terms.deploy}
          </DropdownItem>
        </Tooltip>
      </DropdownGroup>,
      <DropdownSeparator key={`kie-tooling-extended-services-dropdown-${dropdownId}-separator`} />,
    ];

    if (dmnDevSandbox.deployments.length === 0) {
      items.push(
        <DropdownItem
          key={`kie-tooling-extended-services-dropdown-${dropdownId}-dmn-dev-sandbox-no-deployments`}
          isDisabled
        >
          {i18n.dmnDevSandbox.dropdown.noDeployments}
        </DropdownItem>
      );
    } else {
      dmnDevSandbox.deployments
        .sort((a, b) => b.creationTimestamp.getTime() - a.creationTimestamp.getTime())
        .forEach((deployment, i) => {
          items.push(
            <DeploymentDropdownItem
              id={i}
              key={`kie-tooling-extended-services-dropdown-${dropdownId}-dmn-dev-sandbox-deployment_item_${i}`}
              deployment={deployment}
            />
          );
        });
    }
    return items;
  }, [
    dmnDevSandbox.deployments,
    dmnDevSandbox.instanceStatus,
    dmnRunner.isDrawerExpanded,
    dropdownId,
    i18n,
    isKieToolingExtendedServicesRunning,
    kieToolingExtendedServices.port,
    onDevSandboxDeploy,
    onDevSandboxSetup,
    onInstallKieToolingExtendedServices,
    onToggleDmnRunner,
  ]);
}
