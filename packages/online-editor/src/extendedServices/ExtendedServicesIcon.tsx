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
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { ExtendedServicesStatus } from "./ExtendedServicesStatus";
import { ConnectedIcon } from "@patternfly/react-icons/dist/js/icons/connected-icon";
import { DisconnectedIcon } from "@patternfly/react-icons/dist/js/icons/disconnected-icon";
import { useExtendedServices } from "./ExtendedServicesContext";
import { useOnlineI18n } from "../i18n";
import { useSettingsDispatch } from "../settings/SettingsContext";
import { SettingsTabs } from "../settings/SettingsModalBody";

export function ExtendedServicesIcon() {
  const extendedServices = useExtendedServices();
  const { i18n } = useOnlineI18n();
  const settingsDispatch = useSettingsDispatch();

  const toggleExtendedServices = useCallback(() => {
    settingsDispatch.open(SettingsTabs.KIE_SANDBOX_EXTENDED_SERVICES);

    if (!extendedServices.outdated) {
      return;
    }
    extendedServices.setModalOpen(true);
  }, [settingsDispatch, extendedServices]);

  const dropdownToggleIcon = useMemo(
    () => (
      <>
        {extendedServices.outdated && (
          <Tooltip
            className="kogito--editor__light-tooltip"
            key={"outdated"}
            content={i18n.extendedServices.dropdown.tooltip.outdated}
            flipBehavior={["left"]}
            distance={20}
          >
            <ExclamationTriangleIcon
              data-testid="outdated-icon"
              className="kogito--editor__extended-services-dropdown-icon-outdated static-opacity"
              id="extended-services-outdated-icon"
            />
          </Tooltip>
        )}
        {!extendedServices.outdated && (
          <>
            {extendedServices.status === ExtendedServicesStatus.RUNNING ? (
              <Tooltip
                className="kogito--editor__light-tooltip"
                key={"connected"}
                content={i18n.extendedServices.dropdown.tooltip.connected}
                flipBehavior={["left"]}
                distance={20}
              >
                <ConnectedIcon
                  data-testid="connected-icon"
                  className="kogito--editor__extended-services-dropdown-icon-connected blink-opacity"
                  id="extended-services-connected-icon"
                />
              </Tooltip>
            ) : (
              <Tooltip
                className="kogito--editor__light-tooltip"
                key={"disconnected"}
                content={i18n.extendedServices.dropdown.tooltip.disconnected}
                flipBehavior={["left"]}
                distance={20}
              >
                <DisconnectedIcon
                  data-testid="disconnected-icon"
                  className="static-opacity"
                  id="extended-services-disconnected-icon"
                />
              </Tooltip>
            )}
          </>
        )}
      </>
    ),
    [i18n, extendedServices.outdated, extendedServices.status]
  );

  return (
    <Dropdown
      toggle={
        <DropdownToggle
          id="extended-services-button"
          toggleIndicator={null}
          onToggle={toggleExtendedServices}
          className="kie-tools--masthead-hoverable-dark"
          data-testid="extended-services-button"
        >
          {dropdownToggleIcon}
        </DropdownToggle>
      }
      isPlain={true}
      position={DropdownPosition.right}
    />
  );
}
