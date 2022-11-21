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
import { useDevDeployments as useDevDeployments } from "./DevDeploymentsContext";
import { FeatureDependentOnKieSandboxExtendedServices } from "../kieSandboxExtendedServices/FeatureDependentOnKieSandboxExtendedServices";
import { DependentFeature, useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { FileLabel } from "../filesList/FileLabel";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

export function useDevDeploymentsDropdownItems(workspace: ActiveWorkspace | undefined) {
  const extendedServices = useExtendedServices();
  const devDeployments = useDevDeployments();

  const isKieSandboxExtendedServicesRunning = useMemo(
    () => extendedServices.status === KieSandboxExtendedServicesStatus.RUNNING,
    [extendedServices.status]
  );

  const onDeploy = useCallback(() => {
    if (isKieSandboxExtendedServicesRunning) {
      devDeployments.setConfirmDeployModalOpen(true);
      return;
    }
    extendedServices.setInstallTriggeredBy(DependentFeature.DEV_DEPLOYMENTS);
    extendedServices.setModalOpen(true);
  }, [devDeployments, isKieSandboxExtendedServicesRunning, extendedServices]);

  return useMemo(() => {
    return [
      <React.Fragment key={"dmndev-sandbox-dropdown-items"}>
        {workspace && (
          <FeatureDependentOnKieSandboxExtendedServices isLight={false} position="left">
            <DropdownItem
              id="dmn-dev-deployment-deploy-your-model-button"
              key={`dropdown-dmn-dev-deployment-deploy`}
              component={"button"}
              onClick={onDeploy}
              isDisabled={!isKieSandboxExtendedServicesRunning}
              ouiaId={"deploy-to-dmn-dev-deployment-dropdown-button"}
              description="For development only!"
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
        {isKieSandboxExtendedServicesRunning && (
          <>
            <Divider />
            <DropdownItem
              id="dmn-dev-deployment-setup-button"
              key={`dropdown-dmn-dev-deployment-setup`}
              onClick={() => {
                // FIXME: Tiago
                return alert("Open accounts modal");
              }}
              ouiaId={"setup-dmn-dev-deployment-dropdown-button"}
            >
              <Button isInline={true} variant={ButtonVariant.link}>
                Setup...
              </Button>
            </DropdownItem>
          </>
        )}
      </React.Fragment>,
    ];
  }, [isKieSandboxExtendedServicesRunning, onDeploy, workspace]);
}
