/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Dropdown, DropdownPosition, DropdownToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import * as React from "react";
import { useCallback, useMemo } from "react";
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

export function DeployButtons(props: Props) {
  const deployDropdownItems = useDeployDropdownItems({
    workspace: props.workspace,
    workspaceFile: props.workspaceFile,
  });
  const openshift = useOpenShift();
  const settings = useSettings();

  const toggleDeployDropdown = useCallback(
    (isOpen: boolean) => {
      openshift.setDeployDropdownOpen(isOpen);
    },
    [openshift]
  );

  const isOpenShiftEnabled = useMemo(() => {
    return settings.openshift.status === OpenShiftInstanceStatus.CONNECTED;
  }, [settings.openshift.status]);

  return (
    <>
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
    </>
  );
}
