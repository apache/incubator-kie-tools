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
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";
import { ConnectedIcon } from "@patternfly/react-icons/dist/js/icons/connected-icon";
import { DisconnectedIcon } from "@patternfly/react-icons/dist/js/icons/disconnected-icon";
import { useKieToolingExtendedServices } from "./KieToolingExtendedServicesContext";
import { useOnlineI18n } from "../i18n";
import { useSettingsDispatch } from "../settings/SettingsContext";
import { SettingsTabs } from "../settings/SettingsModalBody";

export function KieToolingExtendedServicesIcon() {
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const { i18n } = useOnlineI18n();
  const settingsDispatch = useSettingsDispatch();

  const toggleKieToolingExtendedServices = useCallback(() => {
    if (kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING) {
      settingsDispatch.open(SettingsTabs.KIE_TOOLING_EXTENDED_SERVICES);
    }

    if (!kieToolingExtendedServices.outdated) {
      return;
    }
    kieToolingExtendedServices.setModalOpen(true);
  }, [settingsDispatch, kieToolingExtendedServices]);

  const dropdownToggleIcon = useMemo(
    () => (
      <>
        {kieToolingExtendedServices.outdated && (
          <Tooltip
            className="kogito--editor__light-tooltip"
            key={"outdated"}
            content={i18n.kieToolingExtendedServices.dropdown.tooltip.outdated}
            flipBehavior={["left"]}
            distance={20}
          >
            <ExclamationTriangleIcon
              data-testid="outdated-icon"
              className="kogito--editor__kie-tooling-extended-services-dropdown-icon-outdated static-opacity"
              id="kie-tooling-extended-services-outdated-icon"
            />
          </Tooltip>
        )}
        {!kieToolingExtendedServices.outdated && (
          <>
            {kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING ? (
              <Tooltip
                className="kogito--editor__light-tooltip"
                key={"connected"}
                content={i18n.kieToolingExtendedServices.dropdown.tooltip.connected(kieToolingExtendedServices.port)}
                flipBehavior={["left"]}
                distance={20}
              >
                <ConnectedIcon
                  data-testid="connected-icon"
                  className="kogito--editor__kie-tooling-extended-services-dropdown-icon-connected blink-opacity"
                  id="kie-tooling-extended-services-connected-icon"
                />
              </Tooltip>
            ) : (
              <Tooltip
                className="kogito--editor__light-tooltip"
                key={"disconnected"}
                content={i18n.kieToolingExtendedServices.dropdown.tooltip.disconnected}
                flipBehavior={["left"]}
                distance={20}
              >
                <DisconnectedIcon
                  data-testid="disconnected-icon"
                  className="static-opacity"
                  id="kie-tooling-extended-services-disconnected-icon"
                />
              </Tooltip>
            )}
          </>
        )}
      </>
    ),
    [i18n, kieToolingExtendedServices.outdated, kieToolingExtendedServices.port, kieToolingExtendedServices.status]
  );

  return (
    <Dropdown
      toggle={
        <DropdownToggle
          id="kie-tooling-extended-services-button"
          toggleIndicator={null}
          onToggle={toggleKieToolingExtendedServices}
          className="kogito-tooling--masthead-hoverable-dark"
          data-testid="kie-tooling-extended-services-button"
        >
          {dropdownToggleIcon}
        </DropdownToggle>
      }
      isPlain={true}
      position={DropdownPosition.right}
    />
  );
}
