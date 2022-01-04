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
import { useDmnDevSandboxDropdownItems } from "../DmnDevSandbox/DmnDevSandboxDropdownItems";
import { useDmnRunnerState, useDmnRunnerDispatch } from "../DmnRunner/DmnRunnerContext";
import { FeatureDependentOnKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/FeatureDependentOnKieSandboxExtendedServices";
import {
  DependentFeature,
  useKieSandboxExtendedServices,
} from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { KieSandboxExtendedServicesIcon } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesIcon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { ActiveWorkspace } from "../../workspace/model/ActiveWorkspace";

export function KieSandboxExtendedServicesDropdownGroup(props: { workspace: ActiveWorkspace | undefined }) {
  const { i18n } = useOnlineI18n();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const dmnRunnerState = useDmnRunnerState();
  const dmnDevSandboxDropdownItems = useDmnDevSandboxDropdownItems(props.workspace);
  const dmnRunnerDispatch = useDmnRunnerDispatch();

  const isKieSandboxExtendedServicesRunning = useMemo(
    () => kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING,
    [kieSandboxExtendedServices.status]
  );

  const onToggleDmnRunner = useCallback(() => {
    if (isKieSandboxExtendedServicesRunning) {
      dmnRunnerDispatch.setExpanded((prev) => !prev);
      return;
    }
    kieSandboxExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_RUNNER);
    kieSandboxExtendedServices.setModalOpen(true);
  }, [dmnRunnerDispatch, isKieSandboxExtendedServicesRunning, kieSandboxExtendedServices]);

  return (
    <>
      <DropdownGroup
        key={"dmn-runner-group"}
        label={
          <>
            {"Run"}
            <KieSandboxExtendedServicesIcon />
          </>
        }
      >
        <FeatureDependentOnKieSandboxExtendedServices isLight={false} position="left">
          <DropdownItem
            id="dmn-runner-kebab-toggle"
            key={"kie-sandbox-extended-services-dropdown-dmn-runner-toggle"}
            component={"button"}
            onClick={onToggleDmnRunner}
            ouiaId="toggle-dmn-runner-dropdown-button"
          >
            <Text>{dmnRunnerState.isExpanded ? i18n.terms.close : i18n.terms.open}</Text>
          </DropdownItem>
        </FeatureDependentOnKieSandboxExtendedServices>
      </DropdownGroup>
      <Divider key={"divider-kie-extended-service-dropdown-items"} />
      <DropdownGroup
        key={"dmn-dev-sandbox-group"}
        label={
          <>
            {"Try on OpenShift"}
            <KieSandboxExtendedServicesIcon />
          </>
        }
      >
        {dmnDevSandboxDropdownItems}
      </DropdownGroup>
    </>
  );
}
