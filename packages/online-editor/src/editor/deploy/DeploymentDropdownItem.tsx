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
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { useDeploy } from "./DeployContext";
import { DeployedModel, DeployedModelState } from "./DeployedModel";

interface Props {
  id: number;
  deployment: DeployedModel;
}

export function DeploymentDropdownItem(props: Props) {
  const deployContext = useDeploy();
  const { i18n } = useOnlineI18n();

  const onConfigure = useCallback(
    (e: React.MouseEvent) => {
      deployContext.setDeployDropdownOpen(false);
      e.stopPropagation();
    },
    [deployContext]
  );

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
    if (props.deployment.state === DeployedModelState.UP) {
      return (
        <Tooltip key={`deployment-up-${props.id}`} position="left" content={i18n.deploy.dropdown.item.upTooltip}>
          <Text
            className="kogito-editor_deploy-dropdown-item-status up"
            component={TextVariants.a}
            href={props.deployment.urls.console}
            target="_blank"
            onClick={onConfigure}
          >
            <CheckCircleIcon />
          </Text>
        </Tooltip>
      );
    }

    if (
      props.deployment.state === DeployedModelState.IN_PROGRESS ||
      props.deployment.state === DeployedModelState.PREPARING
    ) {
      return (
        <Tooltip
          key={`deployment-in-progress-${props.id}`}
          position="left"
          content={i18n.deploy.dropdown.item.inProgressTooltip}
        >
          <Text
            className="kogito-editor_deploy-dropdown-item-status in-progress"
            component={TextVariants.a}
            href={props.deployment.urls.console}
            target="_blank"
            onClick={onConfigure}
          >
            <SyncAltIcon className="rotating" />
          </Text>
        </Tooltip>
      );
    }

    return (
      <Tooltip key={`deployment-down-${props.id}`} position="left" content={i18n.deploy.dropdown.item.downTooltip}>
        <Text
          className="kogito-editor_deploy-dropdown-item-status down"
          component={TextVariants.a}
          href={props.deployment.urls.console}
          target="_blank"
          onClick={onConfigure}
        >
          <ExclamationTriangleIcon />
        </Text>
      </Tooltip>
    );
  }, [i18n, onConfigure, props.deployment.urls.console, props.deployment.state, props.id]);

  const onItemClicked = useCallback(() => {
    window.open(props.deployment.urls.index, "_blank");
  }, [props.deployment.urls.index]);

  return (
    <DropdownItem
      isDisabled={props.deployment.state !== DeployedModelState.UP}
      key={`deploy-dropdown-item-${props.id}`}
      onClick={onItemClicked}
      description={i18n.deploy.dropdown.item.createdAt(props.deployment.creationTimestamp.toLocaleString())}
      icon={stateIcon}
    >
      {filename}
    </DropdownItem>
  );
}
