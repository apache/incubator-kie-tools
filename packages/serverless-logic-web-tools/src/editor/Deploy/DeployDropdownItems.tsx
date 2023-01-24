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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { RegistryIcon } from "@patternfly/react-icons/dist/js/icons";
import { OpenshiftIcon } from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useAppI18n } from "../../i18n";
import { FeatureDependentOnKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/FeatureDependentOnKieSandboxExtendedServices";
import {
  DependentFeature,
  useKieSandboxExtendedServices,
} from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { SettingsTabs } from "../../settings/SettingsModalBody";
import { useVirtualServiceRegistryDependencies } from "../../virtualServiceRegistry/hooks/useVirtualServiceRegistryDependencies";
import { FileLabel } from "../../workspace/components/FileLabel";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";

export function useDeployDropdownItems(props: { workspace: ActiveWorkspace }) {
  const { i18n } = useAppI18n();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const openshift = useOpenShift();
  const { needsDependencyDeployment } = useVirtualServiceRegistryDependencies({
    workspace: props.workspace,
  });

  const isKieSandboxExtendedServicesRunning = useMemo(
    () => kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING,
    [kieSandboxExtendedServices.status]
  );

  const isOpenShiftConnected = useMemo(
    () => settings.openshift.status === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status]
  );

  const onSetup = useCallback(() => {
    settingsDispatch.open(SettingsTabs.OPENSHIFT);
  }, [settingsDispatch]);

  const onDeploy = useCallback(() => {
    if (isKieSandboxExtendedServicesRunning) {
      openshift.setConfirmDeployModalOpen(true);
      return;
    }
    kieSandboxExtendedServices.setInstallTriggeredBy(DependentFeature.OPENSHIFT);
    kieSandboxExtendedServices.setModalOpen(true);
  }, [isKieSandboxExtendedServicesRunning, kieSandboxExtendedServices, openshift]);

  return useMemo(() => {
    return [
      <React.Fragment key={"deploy-dropdown-items"}>
        {props.workspace && (
          <FeatureDependentOnKieSandboxExtendedServices isLight={false} position="left">
            <DropdownItem
              icon={<OpenshiftIcon />}
              id="deploy-your-model-button"
              key={`dropdown-deploy`}
              component={"button"}
              onClick={onDeploy}
              isDisabled={isKieSandboxExtendedServicesRunning && !isOpenShiftConnected}
              ouiaId={"deploy-to-openshift-dropdown-button"}
            >
              {props.workspace.files.length > 1 && (
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>
                    Deploy models in <b>{`"${props.workspace.descriptor.name}"`}</b>
                  </FlexItem>
                </Flex>
              )}
              {props.workspace.files.length === 1 && (
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>
                    Deploy <b>{`"${props.workspace.files[0].nameWithoutExtension}"`}</b>
                  </FlexItem>
                  <FlexItem>
                    <b>
                      <FileLabel extension={props.workspace.files[0].extension} />
                    </b>
                  </FlexItem>
                </Flex>
              )}
            </DropdownItem>
            {needsDependencyDeployment && (
              <>
                <Divider />
                <Tooltip content={i18n.deployments.virtualServiceRegistry.dependencyWarningTooltip} position="bottom">
                  <DropdownItem icon={<RegistryIcon color="var(--pf-global--warning-color--100)" />} isDisabled>
                    <Flex flexWrap={{ default: "nowrap" }}>
                      <FlexItem>
                        <Text component="small" style={{ color: "var(--pf-global--warning-color--200)" }}>
                          {i18n.deployments.virtualServiceRegistry.dependencyWarning}
                        </Text>
                      </FlexItem>
                    </Flex>
                  </DropdownItem>
                </Tooltip>
              </>
            )}
          </FeatureDependentOnKieSandboxExtendedServices>
        )}
        {!isOpenShiftConnected && isKieSandboxExtendedServicesRunning && (
          <>
            <Divider />
            <DropdownItem
              id="deploy-setup-button"
              key={`dropdown-deploy-setup`}
              onClick={onSetup}
              ouiaId={"setup-deploy-dropdown-button"}
            >
              <Button isInline={true} variant={ButtonVariant.link}>
                Setup...
              </Button>
            </DropdownItem>
          </>
        )}
      </React.Fragment>,
    ];
  }, [
    props.workspace,
    onDeploy,
    isKieSandboxExtendedServicesRunning,
    isOpenShiftConnected,
    needsDependencyDeployment,
    i18n,
    onSetup,
  ]);
}
