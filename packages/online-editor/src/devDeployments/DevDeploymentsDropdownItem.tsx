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

import * as React from "react";
import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { basename } from "path";
import { useCallback, useMemo } from "react";
import { useOnlineI18n } from "../i18n";
import { OpenShiftDeploymentState } from "@kie-tools-core/openshift/dist/service/types";
import { KieSandboxOpenShiftDeployedModel } from "../openshift/KieSandboxOpenShiftService";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import TrashIcon from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { useDevDeployments } from "./DevDeploymentsContext";
import { AuthSession } from "../authSessions/AuthSessionApi";

interface Props {
  id: number;
  deployment: KieSandboxOpenShiftDeployedModel;
  cloudAuthSession: AuthSession;
}

export function DevDeploymentsDropdownItem(props: Props) {
  const { i18n } = useOnlineI18n();
  const devDeployments = useDevDeployments();

  const deploymentName = useMemo(() => {
    const maxSize = 30;

    let name = props.deployment.workspaceName;
    let extension = "";

    if (!name) {
      const originalFilename = basename(props.deployment.uri);
      extension = ` ${originalFilename.substring(originalFilename.lastIndexOf("."))}`;
      name = originalFilename.replace(extension, "");
    }

    if (name.length < maxSize) {
      return name;
    }

    return `${name.substring(0, maxSize)}...${extension}`;
  }, [props.deployment.uri, props.deployment.workspaceName]);

  const stateIcon = useMemo(() => {
    if (props.deployment.state === OpenShiftDeploymentState.UP) {
      return (
        <Tooltip
          key={`deployment-up-${props.id}`}
          position="left"
          content={i18n.devDeployments.dropdown.item.upTooltip}
        >
          <CheckCircleIcon
            id="dmn-dev-deployment-item-up-icon"
            className="kogito--editor__dmn-dev-deployment-dropdown-item-status success-icon"
          />
        </Tooltip>
      );
    }

    if (
      props.deployment.state === OpenShiftDeploymentState.IN_PROGRESS ||
      props.deployment.state === OpenShiftDeploymentState.PREPARING
    ) {
      return (
        <Tooltip
          key={`deployment-in-progress-${props.id}`}
          position="left"
          content={i18n.devDeployments.dropdown.item.inProgressTooltip}
        >
          <SyncAltIcon
            id="dmn-dev-deployment-item-in-progress-icon"
            className="kogito--editor__dmn-dev-deployment-dropdown-item-status in-progress-icon rotating"
          />
        </Tooltip>
      );
    }

    if (props.deployment.state === OpenShiftDeploymentState.ERROR) {
      return (
        <Tooltip
          key={`deployment-error-${props.id}`}
          position="left"
          content={i18n.devDeployments.dropdown.item.errorTooltip}
        >
          <ExclamationCircleIcon
            id="dmn-dev-deployment-item-error-icon"
            className="kogito--editor__dmn-dev-deployment-dropdown-item-status error-icon"
          />
        </Tooltip>
      );
    }

    return (
      <Tooltip
        key={`deployment-down-${props.id}`}
        position="left"
        content={i18n.devDeployments.dropdown.item.downTooltip}
      >
        <ExclamationTriangleIcon
          id="dmn-dev-deployment-item-down-icon"
          className="kogito--editor__dmn-dev-deployment-dropdown-item-status warning-icon"
        />
      </Tooltip>
    );
  }, [i18n, props.deployment.state, props.id]);

  const onItemClicked = useCallback(() => {
    window.open(`${props.deployment.routeUrl}/#/form/${props.deployment.uri}`, "_blank");
  }, [props.deployment.routeUrl, props.deployment.uri]);

  const onDelete = useCallback(() => {
    devDeployments.setConfirmDeleteModalState({
      isOpen: true,
      cloudAuthSessionId: props.cloudAuthSession.id,
      resourceNames: [props.deployment.resourceName],
    });
  }, [devDeployments, props.cloudAuthSession.id, props.deployment.resourceName]);

  return (
    <Flex>
      <FlexItem grow={{ default: "grow" }} style={{ margin: "0" }}>
        <DropdownItem
          id="dev-deployments-deployment-item-button"
          key={`dev-deployments-dropdown-item-${props.id}`}
          onClick={onItemClicked}
          description={i18n.devDeployments.dropdown.item.createdAt(props.deployment.creationTimestamp.toLocaleString())}
          icon={stateIcon}
        >
          {deploymentName}
        </DropdownItem>
      </FlexItem>
      <FlexItem alignSelf={{ default: "alignSelfCenter" }}>
        <Button
          className="kogito--editor__dev-deployments-dropdown-item-delete"
          style={{ color: "var(--pf-global--palette--black-500)" }}
          variant={ButtonVariant.link}
          isDanger={true}
          onClick={onDelete}
          icon={<TrashIcon />}
        />
      </FlexItem>
    </Flex>
  );
}
