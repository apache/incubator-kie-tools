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
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ConnectedIcon } from "@patternfly/react-icons/dist/js/icons/connected-icon";
import { DisconnectedIcon } from "@patternfly/react-icons/dist/js/icons/disconnected-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import * as React from "react";
import { useOnlineI18n } from "../../common/i18n";
import { useKieToolingExtendedServices } from "./KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";

interface Props {
  dropdownItems: (dropdownId: string) => any[];
}

export function KieToolingExtendedServicesDropdown(props: Props) {
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();

  return (
    <Dropdown
      onSelect={() => kieToolingExtendedServices.setDropdownOpen(false)}
      toggle={
        <DropdownToggle
          id={"kie-tooling-extended-services-id-lg"}
          data-testid={"kie-tooling-extended-services-menu"}
          onToggle={(isOpen) => kieToolingExtendedServices.setDropdownOpen(isOpen)}
        >
          {kieToolingExtendedServices.outdated && (
            <Tooltip
              className="kogito--editor__kie-tooling-extended-services-dropdown-tooltip"
              key={"outdated"}
              content={i18n.kieToolingExtendedServices.dropdown.tooltip.outdated}
              flipBehavior={["left"]}
              distance={20}
              trigger="mouseenter focus"
            >
              <ExclamationTriangleIcon className="pf-u-mr-sm" id={"dmn-runner-outdated-icon"} />
            </Tooltip>
          )}
          {!kieToolingExtendedServices.outdated && (
            <>
              {kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING ? (
                <Tooltip
                  className="kogito--editor__kie-tooling-extended-services-dropdown-tooltip"
                  key={"connected"}
                  content={i18n.kieToolingExtendedServices.dropdown.tooltip.connected}
                  flipBehavior={["left"]}
                  distance={20}
                  trigger="mouseenter focus"
                >
                  <ConnectedIcon className="blink-opacity pf-u-mr-sm" />
                </Tooltip>
              ) : (
                <Tooltip
                  className="kogito--editor__kie-tooling-extended-services-dropdown-tooltip"
                  key={"disconnected"}
                  content={i18n.kieToolingExtendedServices.dropdown.tooltip.disconnected}
                  flipBehavior={["left"]}
                  distance={20}
                  trigger="mouseenter focus"
                >
                  <DisconnectedIcon className="pf-u-mr-sm" />
                </Tooltip>
              )}
            </>
          )}
          {i18n.names.kieToolingExtendedServices}
        </DropdownToggle>
      }
      isPlain={true}
      className={"kogito--editor__toolbar dropdown"}
      isOpen={kieToolingExtendedServices.isDropdownOpen}
      dropdownItems={props.dropdownItems("lg")}
      position={DropdownPosition.right}
    />
  );
}
