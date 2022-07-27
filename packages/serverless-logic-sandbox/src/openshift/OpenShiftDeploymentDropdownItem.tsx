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

import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { basename } from "path";
import * as React from "react";
import { useMemo } from "react";
import { OpenShiftDeployedModel, OpenShiftDeployedModelState } from "./OpenShiftDeployedModel";

interface Props {
  id: number;
  deployment: OpenShiftDeployedModel;
}

export function OpenShiftDeploymentDropdownItem(props: Props) {
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
    if (props.deployment.state === OpenShiftDeployedModelState.UP) {
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
      props.deployment.state === OpenShiftDeployedModelState.IN_PROGRESS ||
      props.deployment.state === OpenShiftDeployedModelState.PREPARING
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

    if (props.deployment.state === OpenShiftDeployedModelState.ERROR) {
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
    <DropdownItem
      id="openshift-deployment-item-button"
      key={`openshift-dropdown-item-${props.id}`}
      isDisabled={props.deployment.state === OpenShiftDeployedModelState.ERROR}
      onClick={() => window.open(props.deployment.baseUrl, "_blank")}
      description={`Created at ${props.deployment.creationTimestamp.toLocaleString()}`}
      icon={stateIcon}
    >
      {deploymentName}
    </DropdownItem>
  );
}
