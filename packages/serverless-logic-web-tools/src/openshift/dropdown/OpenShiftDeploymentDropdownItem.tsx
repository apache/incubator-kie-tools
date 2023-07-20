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
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { extractExtension } from "@kie-tools-core/workspaces-git-fs/dist/relativePath/WorkspaceFileRelativePathParser";
import { useMemo, useCallback, useState } from "react";
import { WebToolsOpenShiftDeployedModel } from "../deploy/types";
import { basename } from "path";
import { DEV_MODE_FEATURE_NAME, buildEndpoints } from "../swfDevMode/DevModeConstants";
import { useDevModeDispatch } from "../swfDevMode/DevModeContext";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { HistoryIcon } from "@patternfly/react-icons/dist/js/icons/history-icon";
import { DeploymentState } from "@kie-tools-core/kubernetes-bridge/dist/resources";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";

interface Props {
  id: number;
  deployment: WebToolsOpenShiftDeployedModel;
  refreshDeployments: (canceled: Holder<boolean>) => void;
}

export function OpenShiftDeploymentDropdownItem(props: Props) {
  const { refreshDeployments } = { ...props };
  const devModeDispatch = useDevModeDispatch();
  const [isRestoring, setRestoring] = useState(false);

  const deploymentName = useMemo(() => {
    if (props.deployment.devMode) {
      return DEV_MODE_FEATURE_NAME;
    }

    const maxSize = 35;

    const workspaceName = props.deployment.workspaceName;
    if (workspaceName.length < maxSize) {
      return workspaceName;
    }

    // `workspaceName` here can also be a filename
    const extension = extractExtension(workspaceName);
    const name = extension ? basename(workspaceName, `.${extension}`) : workspaceName;
    return `${name.substring(0, maxSize - extension.length)}...${extension}`;
  }, [props.deployment]);

  const onDeploymentClicked = useCallback(() => {
    const endpoints = buildEndpoints(props.deployment.routeUrl);
    window.open(props.deployment.devMode ? endpoints.swfDevUi : endpoints.base, "_blank");
  }, [props.deployment.devMode, props.deployment.routeUrl]);

  const onRestoreClicked = useCallback(async () => {
    if (isRestoring) {
      return;
    }

    setRestoring(true);
    await devModeDispatch.restart();

    window.setTimeout(() => {
      setRestoring(false);
      refreshDeployments(new Holder(false));
    }, 2000);
  }, [devModeDispatch, isRestoring, refreshDeployments]);

  const stateIcon = useMemo(() => {
    if (props.deployment.state === DeploymentState.UP) {
      return (
        <Tooltip key={`deployment-up-${props.id}`} position="left" content={"This deployment is up and running."}>
          <CheckCircleIcon
            id="openshift-deployment-item-up-icon"
            className="kogito--editor__openshift-dropdown-item-status success-icon"
          />
        </Tooltip>
      );
    }

    if (
      props.deployment.state === DeploymentState.IN_PROGRESS ||
      props.deployment.state === DeploymentState.PREPARING
    ) {
      return (
        <Tooltip
          key={`deployment-in-progress-${props.id}`}
          position="left"
          content={"This deployment is in progress and it will be available shortly."}
        >
          <SyncAltIcon
            id="openshift-deployment-item-in-progress-icon"
            className="kogito--editor__openshift-dropdown-item-status in-progress-icon rotating"
          />
        </Tooltip>
      );
    }

    if (props.deployment.state === DeploymentState.ERROR) {
      return (
        <Tooltip
          key={`deployment-error-${props.id}`}
          position="left"
          content={
            "Some unexpected error happened during the deploy process. Check the logs in your instance for further information."
          }
        >
          <ExclamationCircleIcon
            id="openshift-deployment-item-error-icon"
            className="kogito--editor__openshift-dropdown-item-status error-icon"
          />
        </Tooltip>
      );
    }

    return (
      <Tooltip key={`deployment-down-${props.id}`} position="left" content={"This deployment is not running."}>
        <ExclamationTriangleIcon
          id="openshift-deployment-item-down-icon"
          className="kogito--editor__openshift-dropdown-item-status warning-icon"
        />
      </Tooltip>
    );
  }, [props.deployment.state, props.id]);

  return (
    <Flex>
      <FlexItem grow={{ default: "grow" }} style={{ margin: "0" }}>
        <DropdownItem
          id="openshift-deployment-item-button"
          key={`openshift-dropdown-item-${props.id}`}
          isDisabled={props.deployment.state === DeploymentState.ERROR}
          onClick={onDeploymentClicked}
          description={`Created at ${props.deployment.creationTimestamp.toLocaleString()}`}
          icon={stateIcon}
        >
          {deploymentName}
        </DropdownItem>
      </FlexItem>
      {props.deployment.devMode && (
        <FlexItem alignSelf={{ default: "alignSelfCenter" }}>
          <Tooltip content={"Restore its original state"}>
            <Button
              className="kogito--editor__openshift-deployments-dropdown-item-action"
              style={{ color: "var(--pf-global--palette--black-500)" }}
              variant={ButtonVariant.plain}
              onClick={onRestoreClicked}
              icon={<HistoryIcon />}
              isLoading={isRestoring}
            />
          </Tooltip>
        </FlexItem>
      )}
    </Flex>
  );
}
