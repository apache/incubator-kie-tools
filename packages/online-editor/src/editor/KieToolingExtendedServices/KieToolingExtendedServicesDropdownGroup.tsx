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
import { useOnlineI18n } from "../../common/i18n";
import { useDmnDevSandboxDropdownItems } from "../DmnDevSandbox/DmnDevSandboxDropdownItems";
import { useDmnRunner } from "../DmnRunner/DmnRunnerContext";
import { FeatureDependentOnKieToolingExtendedServices } from "./FeatureDependentOnKieToolingExtendedServices";
import { useKieToolingExtendedServices } from "./KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";

export function KieToolingExtendedServicesDropdownGroup() {
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const dmnRunner = useDmnRunner();
  const dmnDevSandboxDropdownItems = useDmnDevSandboxDropdownItems();

  const isKieToolingExtendedServicesRunning = useMemo(
    () => kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING,
    [kieToolingExtendedServices.status]
  );

  const onSetupKieToolingExtendedServices = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    kieToolingExtendedServices.setModalOpen(true);
  }, [kieToolingExtendedServices]);

  const onToggleDmnRunner = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    dmnRunner.setDrawerExpanded(!dmnRunner.isDrawerExpanded);
  }, [dmnRunner, kieToolingExtendedServices]);

  return (
    <DropdownGroup label={i18n.names.kieToolingExtendedServices}>
      <DropdownItem
        key={`kie-tooling-extended-services-dropdown-setup`}
        component={"button"}
        onClick={onSetupKieToolingExtendedServices}
        isDisabled={isKieToolingExtendedServicesRunning}
        ouiaId="setup-dropdown-button"
      >
        {isKieToolingExtendedServicesRunning
          ? i18n.kieToolingExtendedServices.dropdown.shortConnected(kieToolingExtendedServices.port)
          : i18n.terms.setup}
      </DropdownItem>
      <DropdownGroup key={"dmn-runner-group"} label={i18n.names.dmnRunner}>
        <FeatureDependentOnKieToolingExtendedServices isLight={false} position="left">
          <DropdownItem
            key={`kie-tooling-extended-services-dropdown-dmn-runner-toggle`}
            component={"button"}
            onClick={onToggleDmnRunner}
            isDisabled={!isKieToolingExtendedServicesRunning}
            ouiaId="toggle-dmn-runner-dropdown-button"
          >
            <Text>{dmnRunner.isDrawerExpanded ? i18n.terms.close : i18n.terms.open}</Text>
          </DropdownItem>
        </FeatureDependentOnKieToolingExtendedServices>
      </DropdownGroup>
      <DropdownGroup key={"dmn-dev-sandbox-group"} label={i18n.names.dmnDevSandbox}>
        {dmnDevSandboxDropdownItems()}
      </DropdownGroup>
    </DropdownGroup>
  );
}
