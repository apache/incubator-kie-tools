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

import { DropdownGroup, DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useOnlineI18n } from "../../i18n";
import { useDevDeploymentsDeployDropdownItems } from "../../devDeployments/DevDeploymentsDeployDropdownItems";
import { useDmnRunnerState, useDmnRunnerDispatch } from "../../dmnRunner/DmnRunnerContext";
import { FeatureDependentOnExtendedServices } from "../../extendedServices/FeatureDependentOnExtendedServices";
import { DependentFeature, useExtendedServices } from "../../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../../extendedServices/ExtendedServicesStatus";
import { ExtendedServicesIcon } from "../../extendedServices/ExtendedServicesIcon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { DmnRunnerProviderActionType } from "../../dmnRunner/DmnRunnerTypes";

export function ExtendedServicesDropdownGroup(props: { workspace: ActiveWorkspace | undefined }) {
  const { i18n } = useOnlineI18n();
  const extendedServices = useExtendedServices();
  const dmnRunnerState = useDmnRunnerState();
  const devDeploymentsDropdownItems = useDevDeploymentsDeployDropdownItems(props.workspace);
  const { setDmnRunnerContextProviderState } = useDmnRunnerDispatch();

  const isExtendedServicesRunning = useMemo(
    () => extendedServices.status === ExtendedServicesStatus.RUNNING,
    [extendedServices.status]
  );

  const onToggleDmnRunner = useCallback(() => {
    if (isExtendedServicesRunning) {
      setDmnRunnerContextProviderState({ type: DmnRunnerProviderActionType.TOGGLE_EXPANDED });
      return;
    }
    extendedServices.setInstallTriggeredBy(DependentFeature.DMN_RUNNER);
    extendedServices.setModalOpen(true);
  }, [setDmnRunnerContextProviderState, isExtendedServicesRunning, extendedServices]);

  return (
    <>
      <DropdownGroup
        key={"dmn-runner-group"}
        label={
          <>
            {"Run"}
            <ExtendedServicesIcon />
          </>
        }
      >
        <FeatureDependentOnExtendedServices isLight={false} position="left">
          <DropdownItem
            id="dmn-runner-kebab-toggle"
            key={"extended-services-dropdown-dmn-runner-toggle"}
            component={"button"}
            onClick={onToggleDmnRunner}
            ouiaId="toggle-dmn-runner-dropdown-button"
          >
            <Text>{dmnRunnerState.isExpanded ? i18n.terms.close : i18n.terms.open}</Text>
          </DropdownItem>
        </FeatureDependentOnExtendedServices>
      </DropdownGroup>
      <Divider key={"divider-kie-extended-service-dropdown-items"} />
      <DropdownGroup
        key={"dmn-dev-deployment-group"}
        label={
          <>
            {"Deploy"}
            <ExtendedServicesIcon />
          </>
        }
      >
        {devDeploymentsDropdownItems}
      </DropdownGroup>
    </>
  );
}
