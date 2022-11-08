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
import { FeatureDependentOnKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/FeatureDependentOnKieSandboxExtendedServices";
import {
  DependentFeature,
  useKieSandboxExtendedServices,
} from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useSettings } from "../../settings/SettingsContext";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useDeployDropdownItems } from "../Deploy/DeployDropdownItems";
import { EditorPageDockDrawerRef } from "../EditorPageDockDrawer";

interface Props {
  editorPageDock: EditorPageDockDrawerRef | undefined;
  workspace: ActiveWorkspace;
}

export function KieSandboxExtendedServicesButtons(props: Props) {
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const deployDropdownItems = useDeployDropdownItems({
    workspace: props.workspace,
  });
  const openshift = useOpenShift();
  const settings = useSettings();

  const toggleDeployDropdown = useCallback(
    (isOpen: boolean) => {
      if (kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING) {
        openshift.setDeployDropdownOpen(isOpen);
        return;
      }
      kieSandboxExtendedServices.setInstallTriggeredBy(DependentFeature.OPENSHIFT);
      kieSandboxExtendedServices.setModalOpen(true);
    },
    [kieSandboxExtendedServices, openshift]
  );

  const isOpenShiftEnabled = useMemo(() => {
    return (
      kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING &&
      settings.openshift.status === OpenShiftInstanceStatus.CONNECTED
    );
  }, [kieSandboxExtendedServices.status, settings.openshift.status]);

  return (
    <>
      <FeatureDependentOnKieSandboxExtendedServices isLight={true} position="top">
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
      </FeatureDependentOnKieSandboxExtendedServices>
    </>
  );
}
