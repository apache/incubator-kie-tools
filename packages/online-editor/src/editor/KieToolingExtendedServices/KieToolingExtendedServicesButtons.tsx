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
import { useCallback } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { useDmnDevSandbox } from "../DmnDevSandbox/DmnDevSandboxContext";
import { useDmnDevSandboxDropdownItems } from "../DmnDevSandbox/DmnDevSandboxDropdownItems";
import { OpenShiftInstanceStatus } from "../DmnDevSandbox/OpenShiftInstanceStatus";
import { useDmnRunner } from "../DmnRunner/DmnRunnerContext";
import { FeatureDependentOnKieToolingExtendedServices } from "./FeatureDependentOnKieToolingExtendedServices";
import { DependentFeature, useKieToolingExtendedServices } from "./KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";
import { useSettings } from "../../settings/SettingsContext";
import { KieToolingExtendedServicesIcon } from "./KieToolingExtendedServicesIcon";

export function KieToolingExtendedServicesButtons() {
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const dmnDevSandbox = useDmnDevSandbox();
  const dmnRunner = useDmnRunner();
  const settings = useSettings();
  const dmnDevSandboxDropdownItems = useDmnDevSandboxDropdownItems();

  const onToggleDmnRunner = useCallback(() => {
    if (kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING) {
      dmnRunner.setDrawerExpanded(!dmnRunner.isDrawerExpanded);
      return;
    }
    kieToolingExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_RUNNER);
    kieToolingExtendedServices.setModalOpen(true);
  }, [dmnRunner, kieToolingExtendedServices]);

  const onToggleDmnDevSandbox = useCallback(
    (isOpen: boolean) => {
      if (kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING) {
        dmnDevSandbox.setDropdownOpen(isOpen);
        return;
      }
      kieToolingExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_DEV_SANDBOX);
      kieToolingExtendedServices.setModalOpen(true);
    },
    [dmnDevSandbox, kieToolingExtendedServices]
  );

  return (
    <>
      <KieToolingExtendedServicesIcon />

      <FeatureDependentOnKieToolingExtendedServices isLight={true} position="bottom">
        <Dropdown
          onSelect={() => dmnDevSandbox.setDropdownOpen(false)}
          toggle={
            <DropdownToggle
              id="dmn-dev-sandbox-dropdown-button"
              onToggle={(isOpen: boolean) => onToggleDmnDevSandbox(isOpen)}
              data-testid="dmn-dev-sandbox-button"
            >
              {i18n.terms.deploy}
            </DropdownToggle>
          }
          isOpen={dmnDevSandbox.isDropdownOpen}
          isPlain={true}
          className={"kogito--editor__toolbar dropdown"}
          position={DropdownPosition.right}
          dropdownItems={dmnDevSandboxDropdownItems()}
          style={
            kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING &&
            settings.openshift.status.get === OpenShiftInstanceStatus.CONNECTED
              ? { marginRight: "2px", borderBottom: "solid var(--pf-global--palette--blue-300) 2px", paddingBottom: 0 }
              : { marginRight: "2px" }
          }
        />
      </FeatureDependentOnKieToolingExtendedServices>
      <FeatureDependentOnKieToolingExtendedServices isLight={true} position="bottom">
        <Dropdown
          toggle={
            <DropdownToggle
              id="dmn-runner-button"
              toggleIndicator={null}
              onToggle={onToggleDmnRunner}
              className="kogito--dmn-runner-button"
              data-testid="dmn-runner-button"
            >
              {i18n.terms.run}
            </DropdownToggle>
          }
          isPlain={true}
          className={"kogito--editor__toolbar dropdown"}
          position={DropdownPosition.right}
          isOpen={false}
          style={
            dmnRunner.isDrawerExpanded
              ? { marginRight: "2px", borderBottom: "solid var(--pf-global--palette--blue-300) 2px", paddingBottom: 0 }
              : { marginRight: "2px" }
          }
        />
      </FeatureDependentOnKieToolingExtendedServices>
    </>
  );
}
