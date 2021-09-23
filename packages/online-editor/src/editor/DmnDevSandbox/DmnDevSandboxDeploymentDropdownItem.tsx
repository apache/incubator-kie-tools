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
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { OpenShiftDeployedModel, OpenShiftDeployedModelState } from "../../settings/OpenShiftDeployedModel";

interface Props {
  id: number;
  deployment: OpenShiftDeployedModel;
}

export function DmnDevSandboxDeploymentDropdownItem(props: Props) {
  const { i18n } = useOnlineI18n();

  const filename = useMemo(() => {
    const maxSize = 25;
    const originalFilename = props.deployment.filename;
    const extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    const name = originalFilename.replace(`.${extension}`, "");

    if (name.length < maxSize) {
      return originalFilename;
    }

    return `${name.substring(0, maxSize)}... .${extension}`;
  }, [props.deployment.filename]);

  const stateIcon = useMemo(() => {
    if (props.deployment.state === OpenShiftDeployedModelState.UP) {
      return (
        <Tooltip key={`deployment-up-${props.id}`} position="left" content={i18n.dmnDevSandbox.dropdown.item.upTooltip}>
          <CheckCircleIcon
            id="dmn-dev-sandbox-deployment-item-up-icon"
            className="kogito--editor__dmn-dev-sandbox-dropdown-item-status success-icon"
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
          content={i18n.dmnDevSandbox.dropdown.item.inProgressTooltip}
        >
          <SyncAltIcon
            id="dmn-dev-sandbox-deployment-item-in-progress-icon"
            className="kogito--editor__dmn-dev-sandbox-dropdown-item-status in-progress-icon rotating"
          />
        </Tooltip>
      );
    }

    return (
      <Tooltip
        key={`deployment-down-${props.id}`}
        position="left"
        content={i18n.dmnDevSandbox.dropdown.item.downTooltip}
      >
        <ExclamationTriangleIcon
          id="dmn-dev-sandbox-deployment-item-down-icon"
          className="kogito--editor__dmn-dev-sandbox-dropdown-item-status warning-icon"
        />
      </Tooltip>
    );
  }, [i18n, props.deployment.state, props.id]);

  const onItemClicked = useCallback(() => {
    window.open(props.deployment.urls.index, "_blank");
  }, [props.deployment.urls.index]);

  return (
    <DropdownItem
      id="dmn-dev-sandbox-deployment-item-button"
      isDisabled={props.deployment.state !== OpenShiftDeployedModelState.UP}
      key={`dmn-dev-sandbox-dropdown-item-${props.id}`}
      onClick={onItemClicked}
      description={i18n.dmnDevSandbox.dropdown.item.createdAt(props.deployment.creationTimestamp.toLocaleString())}
      icon={stateIcon}
    >
      {filename}
    </DropdownItem>
  );
}
