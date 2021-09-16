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
import { useDmnDevSandbox } from "./DmnDevSandboxContext";
import { OpenShiftInstanceStatus } from "../../settings/OpenShiftInstanceStatus";
import { FeatureDependentOnKieToolingExtendedServices } from "../KieToolingExtendedServices/FeatureDependentOnKieToolingExtendedServices";
import {
  DependentFeature,
  useKieToolingExtendedServices,
} from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "../KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { DmnDevSandboxDeploymentDropdownItem } from "./DmnDevSandboxDeploymentDropdownItem";
import { useSettings } from "../../settings/SettingsContext";
import { SettingsTabs } from "../../settings/SettingsModalBody";

export function useDmnDevSandboxDropdownItems() {
  const settings = useSettings();
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const dmnDevSandbox = useDmnDevSandbox();

  const isKieToolingExtendedServicesRunning = useMemo(
    () => kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING,
    [kieToolingExtendedServices.status]
  );

  const isDmnDevSandboxConnected = useMemo(
    () => settings.openshift.status.get === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status.get]
  );

  const onDevSandboxSetup = useCallback(() => {
    settings.open(SettingsTabs.OPENSHIFT);
  }, [settings]);

  const onDevSandboxDeploy = useCallback(() => {
    if (isKieToolingExtendedServicesRunning) {
      dmnDevSandbox.setConfirmDeployModalOpen(true);
      return;
    }
    kieToolingExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_DEV_SANDBOX);
    kieToolingExtendedServices.setModalOpen(true);
  }, [dmnDevSandbox, isKieToolingExtendedServicesRunning, kieToolingExtendedServices]);

  return useCallback(() => {
    const items = [
      <React.Fragment key={"dmndev-sandbox-dropdown-items"}>
        {!isDmnDevSandboxConnected && isKieToolingExtendedServicesRunning && (
          <DropdownItem
            id="dmn-dev-sandbox-setup-button"
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
            id="dmn-dev-sandbox-deploy-your-model-button"
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
              {i18n.dmnDevSandbox.dropdown.setupFor(settings.openshift.config.get.namespace)}
            </DropdownItem>
            <DropdownItem
              id="dmn-dev-sandbox-change-config-button"
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
      </React.Fragment>,
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
    settings.openshift.config.get.namespace,
    dmnDevSandbox.deployments,
    i18n,
    isDmnDevSandboxConnected,
    isKieToolingExtendedServicesRunning,
    onDevSandboxDeploy,
    onDevSandboxSetup,
  ]);
}
