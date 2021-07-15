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
import { useCallback, useMemo } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { useDmnDevSandbox } from "../DmnDevSandbox/DmnDevSandboxContext";
import { useDmnDevSandboxDropdownItems } from "../DmnDevSandbox/DmnDevSandboxDropdownItems";
import { DmnDevSandboxInstanceStatus } from "../DmnDevSandbox/DmnDevSandboxInstanceStatus";
import { useDmnRunner } from "../DmnRunner/DmnRunnerContext";
import { FeatureDependentOnKieToolingExtendedServices } from "./FeatureDependentOnKieToolingExtendedServices";
import { useKieToolingExtendedServices } from "./KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";

export function KieToolingExtendedServicesButtons() {
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const dmnDevSandbox = useDmnDevSandbox();
  const dmnRunner = useDmnRunner();
  const dmnDevSandboxDropdownItems = useDmnDevSandboxDropdownItems();

  const isKieToolingExtendedServicesRunning = useMemo(
    () => kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING,
    [kieToolingExtendedServices.status]
  );

  const onInstallKieToolingExtendedServices = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    kieToolingExtendedServices.setModalOpen(true);
  }, [kieToolingExtendedServices]);

  const onToggleDmnRunner = useCallback(() => {
    kieToolingExtendedServices.closeDmnTour();
    dmnRunner.setDrawerExpanded(!dmnRunner.isDrawerExpanded);
  }, [dmnRunner, kieToolingExtendedServices]);

  const dropdownToggleIcon = useMemo(
    () => (
      <>
        {kieToolingExtendedServices.outdated && (
          <Tooltip
            className="kogito--editor__kie-tooling-extended-services-dropdown-tooltip"
            key={"outdated"}
            content={i18n.kieToolingExtendedServices.dropdown.tooltip.outdated}
            flipBehavior={["left"]}
            distance={20}
          >
            <ExclamationTriangleIcon
              className="static-opacity pf-u-mr-sm"
              id={"kie-tooling-extended-services-outdated-icon"}
            />
          </Tooltip>
        )}
        {!kieToolingExtendedServices.outdated && (
          <>
            {kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING ? (
              <Tooltip
                className="kogito--editor__kie-tooling-extended-services-dropdown-tooltip"
                key={"connected"}
                content={i18n.kieToolingExtendedServices.dropdown.tooltip.connected(kieToolingExtendedServices.port)}
                flipBehavior={["left"]}
                distance={20}
              >
                <ConnectedIcon
                  className="kogito--editor__kie-tooling-extended-services-dropdown-icon-connected blink-opacity pf-u-mr-sm"
                  id={"kie-tooling-extended-services-connected-icon"}
                />
              </Tooltip>
            ) : (
              <Tooltip
                className="kogito--editor__kie-tooling-extended-services-dropdown-tooltip"
                key={"disconnected"}
                content={i18n.kieToolingExtendedServices.dropdown.tooltip.disconnected}
                flipBehavior={["left"]}
                distance={20}
              >
                <DisconnectedIcon
                  className="static-opacity pf-u-mr-sm"
                  id={"kie-tooling-extended-services-disconnected-icon"}
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
    <>
      <Dropdown
        toggle={
          <DropdownToggle
            onToggle={onInstallKieToolingExtendedServices}
            toggleIndicator={null}
            className="kogito--kie-tooling-extended-services-button"
          >
            {dropdownToggleIcon}
            {i18n.names.kieToolingExtendedServices}
          </DropdownToggle>
        }
        isPlain={true}
        className={"kogito--editor__toolbar dropdown"}
        position={DropdownPosition.right}
        style={
          isKieToolingExtendedServicesRunning
            ? { marginRight: "2px", borderBottom: "solid transparent 2px", paddingBottom: 0 }
            : { marginRight: "2px", borderBottom: "solid var(--pf-global--palette--red-300) 2px", paddingBottom: 0 }
        }
      />

      <FeatureDependentOnKieToolingExtendedServices isLight={true} position="bottom">
        <Dropdown
          onSelect={() => dmnDevSandbox.setDropdownOpen(false)}
          toggle={
            <DropdownToggle isDisabled={!isKieToolingExtendedServicesRunning} onToggle={dmnDevSandbox.setDropdownOpen}>
              {i18n.names.dmnDevSandbox}
            </DropdownToggle>
          }
          isOpen={dmnDevSandbox.isDropdownOpen}
          isPlain={true}
          className={"kogito--editor__toolbar dropdown"}
          position={DropdownPosition.right}
          dropdownItems={dmnDevSandboxDropdownItems()}
          style={
            dmnDevSandbox.instanceStatus === DmnDevSandboxInstanceStatus.CONNECTED
              ? { marginRight: "2px", borderBottom: "solid var(--pf-global--palette--blue-300) 2px", paddingBottom: 0 }
              : { marginRight: "2px" }
          }
        />
      </FeatureDependentOnKieToolingExtendedServices>
      <FeatureDependentOnKieToolingExtendedServices isLight={true} position="bottom">
        <Dropdown
          toggle={
            <DropdownToggle
              isDisabled={!isKieToolingExtendedServicesRunning}
              toggleIndicator={null}
              onToggle={onToggleDmnRunner}
              className="kogito--dmn-runner-button"
            >
              {i18n.names.dmnRunner}
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
