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

import { DropdownItem, DropdownSeparator } from "@patternfly/react-core/dist/js/components/Dropdown";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { useDmnDevSandbox } from "../DmnDevSandbox/DmnDevSandboxContext";
import { DmnDevSandboxInstanceStatus } from "../DmnDevSandbox/DmnDevSandboxInstanceStatus";
import { FeatureDependentOnKieToolingExtendedServices } from "../KieToolingExtendedServices/FeatureDependentOnKieToolingExtendedServices";
import {
  KieToolingExtendedServicesFeature,
  useKieToolingExtendedServices,
} from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "../KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { DmnDevSandboxDeploymentDropdownItem } from "./DmnDevSandboxDeploymentDropdownItem";

export function useDmnDevSandboxDropdownItems() {
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const dmnDevSandbox = useDmnDevSandbox();

  const isKieToolingExtendedServicesRunning = useMemo(
    () => kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING,
    [kieToolingExtendedServices.status]
  );

  const isDmnDevSandboxConnected = useMemo(
    () => dmnDevSandbox.instanceStatus === DmnDevSandboxInstanceStatus.CONNECTED,
    [dmnDevSandbox.instanceStatus]
  );

  const onDevSandboxSetup = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    dmnDevSandbox.setConfigModalOpen(true);
  }, [dmnDevSandbox, kieToolingExtendedServices]);

  const onDevSandboxDeploy = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    if (isKieToolingExtendedServicesRunning) {
      dmnDevSandbox.setConfirmDeployModalOpen(true);
      return;
    }
    kieToolingExtendedServices.setInstallTriggeredBy(KieToolingExtendedServicesFeature.DMN_DEV_SANDBOX);
    kieToolingExtendedServices.setModalOpen(true);
  }, [dmnDevSandbox, isKieToolingExtendedServicesRunning, kieToolingExtendedServices]);

  return useCallback(() => {
    const items = [
      <>
        {!isDmnDevSandboxConnected && isKieToolingExtendedServicesRunning && (
          <DropdownItem
            key={`dropdown-dmn-dev-sandbox-setup`}
            component={"button"}
            onClick={onDevSandboxSetup}
            ouiaId={"setup-dmn-dev-sandbox-dropdown-button"}
          >
            {i18n.terms.setup}
          </DropdownItem>
        )}
        <FeatureDependentOnKieToolingExtendedServices isLight={false} position="left">
          <DropdownItem
            key={`dropdown-dmn-dev-sandbox-deploy`}
            component={"button"}
            onClick={onDevSandboxDeploy}
            isDisabled={isKieToolingExtendedServicesRunning && !isDmnDevSandboxConnected}
            ouiaId={"deploy-to-dmn-dev-sandbox-dropdown-button"}
          >
            {i18n.dmnDevSandbox.common.deployYourModel}
          </DropdownItem>
        </FeatureDependentOnKieToolingExtendedServices>
        {isDmnDevSandboxConnected && (
          <>
            <DropdownSeparator key="dropdown-dmn-dev-sandbox-separator-setup" />
            <DropdownItem
              key={"dropdown-dmn-dev-sandbox-setup-as"}
              component={"button"}
              isDisabled={true}
              ouiaId={"setup-as-dmn-dev-sandbox-dropdown-button"}
            >
              {i18n.dmnDevSandbox.dropdown.setupFor(dmnDevSandbox.currentConfig.username)}
            </DropdownItem>
            <DropdownItem
              key={"dropdown-dmn-dev-sandbox-setup"}
              component={"button"}
              onClick={onDevSandboxSetup}
              isDisabled={!isKieToolingExtendedServicesRunning}
              ouiaId={"setup-dmn-dev-sandbox-dropdown-button"}
            >
              {i18n.terms.change}
            </DropdownItem>
          </>
        )}
      </>,
      <DropdownSeparator key={"dropdown-dmn-dev-sandbox-separator-deployments"} />,
    ];

    if (dmnDevSandbox.deployments.length === 0) {
      items.push(
        <DropdownItem key="disabled link" isDisabled>
          <i>{i18n.dmnDevSandbox.dropdown.noDeployments}</i>
        </DropdownItem>
      );
    } else {
      dmnDevSandbox.deployments
        .sort((a, b) => b.creationTimestamp.getTime() - a.creationTimestamp.getTime())
        .forEach((deployment, i) => {
          items.push(
            <DmnDevSandboxDeploymentDropdownItem id={i} key={`deployment_item_${i}`} deployment={deployment} />
          );
        });
    }

    return items;
  }, [
    dmnDevSandbox.currentConfig.username,
    dmnDevSandbox.deployments,
    i18n,
    isDmnDevSandboxConnected,
    isKieToolingExtendedServicesRunning,
    onDevSandboxDeploy,
    onDevSandboxSetup,
  ]);
}
