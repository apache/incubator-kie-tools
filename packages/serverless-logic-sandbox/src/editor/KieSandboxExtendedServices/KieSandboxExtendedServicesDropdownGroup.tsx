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

import { DropdownGroup } from "@patternfly/react-core/dist/js/components/Dropdown";
import * as React from "react";
import { KieSandboxExtendedServicesIcon } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesIcon";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useDeployDropdownItems } from "../Deploy/DeployDropdownItems";

export function KieSandboxExtendedServicesDropdownGroup(props: { workspace: ActiveWorkspace }) {
  const deployDropdownItems = useDeployDropdownItems({
    workspace: props.workspace,
  });

  return (
    <>
      <DropdownGroup
        key={"deploy-group"}
        label={
          <>
            {"Try on OpenShift"}
            <KieSandboxExtendedServicesIcon />
          </>
        }
      >
        {deployDropdownItems}
      </DropdownGroup>
    </>
  );
}
