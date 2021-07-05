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

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import {
  Dropdown,
  DropdownItem,
  DropdownPosition,
  DropdownSeparator,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { CogIcon } from "@patternfly/react-icons/dist/js/icons/cog-icon";
import { ConnectedIcon } from "@patternfly/react-icons/dist/js/icons/connected-icon";
import { DisconnectedIcon } from "@patternfly/react-icons/dist/js/icons/disconnected-icon";
import * as React from "react";
import { useCallback } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { isConfigValid } from "./ConnectionConfig";
import { useDeploy } from "./DeployContext";
import { DeployInstanceStatus } from "./DeployInstanceStatus";
import { DeploymentDropdownItem } from "./DeploymentDropdownItem";

export function DeployDropdown() {
  const deployContext = useDeploy();
  const { i18n } = useOnlineI18n();

  const onDropdownClicked = useCallback(() => deployContext.setConfigModalOpen(true), [deployContext]);

  const onDeploy = useCallback(() => {
    deployContext.setDeployDropdownOpen(false);
    if (deployContext.instanceStatus === DeployInstanceStatus.DISCONNECTED) {
      if (!isConfigValid(deployContext.currentConfig)) {
        deployContext.setDeployIntroductionModalOpen(true);
        return;
      }
      deployContext.setConfigWizardOpen(true);
      return;
    }
    deployContext.setConfirmDeployModalOpen(true);
  }, [deployContext]);

  const dropdownItems = useCallback(
    (dropdownId: string) => {
      const items = [];
      if (deployContext.deployments.length === 0) {
        items.push(
          <DropdownItem key="disabled link" isDisabled>
            {i18n.deploy.dropdown.noDeployments}
          </DropdownItem>
        );
      } else {
        deployContext.deployments
          .sort((a, b) => b.creationTimestamp.getTime() - a.creationTimestamp.getTime())
          .forEach((deployment, i) => {
            items.push(<DeploymentDropdownItem id={i} key={`deployment_item_${i}`} deployment={deployment} />);
          });
      }
      return [
        <DropdownItem
          key={`dropdown-${dropdownId}-setup`}
          icon={<CogIcon />}
          component={"button"}
          onClick={onDropdownClicked}
          ouiaId="deploy-setup-dropdown-button"
        >
          {i18n.terms.configure}
        </DropdownItem>,
        <DropdownSeparator key="separator" />,
        items,
      ];
    },
    [deployContext.deployments, onDropdownClicked, i18n.terms.configure, i18n.deploy.dropdown.noDeployments]
  );

  return (
    <Dropdown
      onSelect={() => deployContext.setDeployDropdownOpen(false)}
      toggle={
        <DropdownToggle
          id={"deploy-id-lg"}
          data-testid={"deploy-menu"}
          onToggle={(isOpen) => deployContext.setDeployDropdownOpen(isOpen)}
          splitButtonVariant="action"
          splitButtonItems={[
            <Button variant="tertiary" className={"pf-u-pr-md pf-u-pl-md"} key="deploy" onClick={onDeploy}>
              {deployContext.instanceStatus === DeployInstanceStatus.DISCONNECTED && (
                <Tooltip
                  className="kogito-editor__deploy-dropdown-tooltip"
                  key={"disconnected"}
                  position="left"
                  distance={20}
                  content={i18n.deploy.dropdown.tooltip.disconnected}
                >
                  <DisconnectedIcon />
                </Tooltip>
              )}
              {deployContext.instanceStatus === DeployInstanceStatus.CONNECTED && (
                <Tooltip
                  className="kogito-editor__deploy-dropdown-tooltip"
                  key={"connected"}
                  position="left"
                  distance={20}
                  content={i18n.deploy.dropdown.tooltip.connected}
                >
                  <ConnectedIcon className="blink-opacity" />
                </Tooltip>
              )}
              <span className={"pf-u-ml-sm"}>{i18n.deploy.dropdown.title}</span>
            </Button>,
          ]}
        ></DropdownToggle>
      }
      isOpen={deployContext.isDeployDropdownOpen}
      dropdownItems={dropdownItems("lg")}
      position={DropdownPosition.right}
    />
  );
}
