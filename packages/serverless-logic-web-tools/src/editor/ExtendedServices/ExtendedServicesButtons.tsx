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

import { Dropdown, DropdownPosition, DropdownToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { FeatureDependentExtendedServices } from "../../extendedServices/FeatureDependentOnExtendedServices";
import { DependentFeature, useExtendedServices } from "../../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../../extendedServices/ExtendedServicesStatus";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useSettings } from "../../settings/SettingsContext";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useDeployDropdownItems } from "../hooks/useDeployDropdownItems";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";

interface Props {
  workspace: ActiveWorkspace;
  workspaceFile: WorkspaceFile;
}

export function ExtendedServicesButtons(props: Props) {
  const extendedServices = useExtendedServices();
  const deployDropdownItems = useDeployDropdownItems({
    workspace: props.workspace,
    workspaceFile: props.workspaceFile,
  });
  const openshift = useOpenShift();
  const settings = useSettings();

  const toggleDeployDropdown = useCallback(
    (isOpen: boolean) => {
      if (extendedServices.status === ExtendedServicesStatus.RUNNING) {
        openshift.setDeployDropdownOpen(isOpen);
        return;
      }
      extendedServices.setInstallTriggeredBy(DependentFeature.OPENSHIFT);
      extendedServices.setModalOpen(true);
    },
    [extendedServices, openshift]
  );

  const isOpenShiftEnabled = useMemo(() => {
    return (
      extendedServices.status === ExtendedServicesStatus.RUNNING &&
      settings.openshift.status === OpenShiftInstanceStatus.CONNECTED
    );
  }, [extendedServices.status, settings.openshift.status]);

  return (
    <>
      <FeatureDependentExtendedServices isLight={true} position="top">
        <Dropdown
          className={isOpenShiftEnabled ? "pf-m-active" : ""}
          onSelect={() => openshift.setDeployDropdownOpen(false)}
          toggle={
            <DropdownToggle id="deploy-dropdown-button" onToggle={toggleDeployDropdown} data-testid="deploy-button">
              Try on OpenShift
            </DropdownToggle>
          }
          isOpen={openshift.isDeployDropdownOpen}
          position={DropdownPosition.right}
          dropdownItems={deployDropdownItems}
        />
      </FeatureDependentExtendedServices>
    </>
  );
}
