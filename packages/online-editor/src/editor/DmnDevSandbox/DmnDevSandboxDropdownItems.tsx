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

import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useDmnDevSandbox } from "./DmnDevSandboxContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { FeatureDependentOnKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/FeatureDependentOnKieSandboxExtendedServices";
import {
  DependentFeature,
  useKieSandboxExtendedServices,
} from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { SettingsTabs } from "../../settings/SettingsModalBody";
import { ActiveWorkspace } from "../../workspace/model/ActiveWorkspace";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { FileLabel } from "../../filesList/FileLabel";
import { OpenshiftIcon } from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

export function useDmnDevSandboxDropdownItems(workspace: ActiveWorkspace | undefined) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const dmnDevSandbox = useDmnDevSandbox();

  const isKieSandboxExtendedServicesRunning = useMemo(
    () => kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING,
    [kieSandboxExtendedServices.status]
  );

  const isDmnDevSandboxConnected = useMemo(
    () => settings.openshift.status === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status]
  );

  const onDevSandboxSetup = useCallback(() => {
    settingsDispatch.open(SettingsTabs.OPENSHIFT);
  }, [settingsDispatch]);

  const onDevSandboxDeploy = useCallback(() => {
    if (isKieSandboxExtendedServicesRunning) {
      dmnDevSandbox.setConfirmDeployModalOpen(true);
      return;
    }
    kieSandboxExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_DEV_SANDBOX);
    kieSandboxExtendedServices.setModalOpen(true);
  }, [dmnDevSandbox, isKieSandboxExtendedServicesRunning, kieSandboxExtendedServices]);

  return useMemo(() => {
    return [
      <React.Fragment key={"dmndev-sandbox-dropdown-items"}>
        {workspace && (
          <FeatureDependentOnKieSandboxExtendedServices isLight={false} position="left">
            <DropdownItem
              icon={<OpenshiftIcon />}
              id="dmn-dev-sandbox-deploy-your-model-button"
              key={`dropdown-dmn-dev-sandbox-deploy`}
              component={"button"}
              onClick={onDevSandboxDeploy}
              isDisabled={isKieSandboxExtendedServicesRunning && !isDmnDevSandboxConnected}
              ouiaId={"deploy-to-dmn-dev-sandbox-dropdown-button"}
            >
              {workspace.files.length > 1 && (
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>
                    Deploy <b>{`"${workspace.descriptor.name}"`}</b>
                  </FlexItem>
                </Flex>
              )}
              {workspace.files.length === 1 && (
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>
                    Deploy <b>{`"${workspace.files[0].nameWithoutExtension}"`}</b>
                  </FlexItem>
                  <FlexItem>
                    <b>
                      <FileLabel extension={workspace.files[0].extension} />
                    </b>
                  </FlexItem>
                </Flex>
              )}
            </DropdownItem>
          </FeatureDependentOnKieSandboxExtendedServices>
        )}
        {!isDmnDevSandboxConnected && isKieSandboxExtendedServicesRunning && (
          <>
            <Divider />
            <DropdownItem
              id="dmn-dev-sandbox-setup-button"
              key={`dropdown-dmn-dev-sandbox-setup`}
              onClick={onDevSandboxSetup}
              ouiaId={"setup-dmn-dev-sandbox-dropdown-button"}
            >
              <Button isInline={true} variant={ButtonVariant.link}>
                Setup...
              </Button>
            </DropdownItem>
          </>
        )}
      </React.Fragment>,
    ];
  }, [isDmnDevSandboxConnected, isKieSandboxExtendedServicesRunning, onDevSandboxDeploy, onDevSandboxSetup, workspace]);
}
