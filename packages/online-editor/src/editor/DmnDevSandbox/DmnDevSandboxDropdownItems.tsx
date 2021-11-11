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
import { useOnlineI18n } from "../../common/i18n";
import { useDmnDevSandbox } from "./DmnDevSandboxContext";
import { OpenShiftInstanceStatus } from "../../settings/OpenShiftInstanceStatus";
import { FeatureDependentOnKieToolingExtendedServices } from "../KieToolingExtendedServices/FeatureDependentOnKieToolingExtendedServices";
import {
  DependentFeature,
  useKieToolingExtendedServices,
} from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "../KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { SettingsTabs } from "../../settings/SettingsModalBody";
import { ActiveWorkspace } from "../../workspace/model/ActiveWorkspace";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { FileLabel } from "../../workspace/components/FileLabel";
import { OpenshiftIcon } from "@patternfly/react-icons/dist/js/icons/openshift-icon";

export function useDmnDevSandboxDropdownItems(workspace: ActiveWorkspace | undefined) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const dmnDevSandbox = useDmnDevSandbox();

  const isKieToolingExtendedServicesRunning = useMemo(
    () => kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING,
    [kieToolingExtendedServices.status]
  );

  const isDmnDevSandboxConnected = useMemo(
    () => settings.openshift.status === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status]
  );

  const onDevSandboxSetup = useCallback(() => {
    settingsDispatch.open(SettingsTabs.OPENSHIFT);
  }, [settingsDispatch]);

  const onDevSandboxDeploy = useCallback(() => {
    if (isKieToolingExtendedServicesRunning) {
      dmnDevSandbox.setConfirmDeployModalOpen(true);
      return;
    }
    kieToolingExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_DEV_SANDBOX);
    kieToolingExtendedServices.setModalOpen(true);
  }, [dmnDevSandbox, isKieToolingExtendedServicesRunning, kieToolingExtendedServices]);

  return useMemo(() => {
    return [
      <React.Fragment key={"dmndev-sandbox-dropdown-items"}>
        {!isDmnDevSandboxConnected && isKieToolingExtendedServicesRunning && (
          <DropdownItem
            id="dmn-dev-sandbox-setup-button"
            key={`dropdown-dmn-dev-sandbox-setup`}
            component={"button"}
            onClick={onDevSandboxSetup}
            ouiaId={"setup-dmn-dev-sandbox-dropdown-button"}
          >
            Setup...
          </DropdownItem>
        )}
        {workspace && (
          <FeatureDependentOnKieToolingExtendedServices isLight={false} position="left">
            <DropdownItem
              icon={<OpenshiftIcon />}
              id="dmn-dev-sandbox-deploy-your-model-button"
              key={`dropdown-dmn-dev-sandbox-deploy`}
              component={"button"}
              onClick={onDevSandboxDeploy}
              isDisabled={isKieToolingExtendedServicesRunning && !isDmnDevSandboxConnected}
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
          </FeatureDependentOnKieToolingExtendedServices>
        )}
      </React.Fragment>,
    ];
  }, [isDmnDevSandboxConnected, isKieToolingExtendedServicesRunning, onDevSandboxDeploy, onDevSandboxSetup, workspace]);
}
