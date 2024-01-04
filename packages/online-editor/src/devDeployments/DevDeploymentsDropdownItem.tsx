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

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { SyncAltIcon } from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { useOnlineI18n } from "../i18n";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import TrashIcon from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { useDevDeployments } from "./DevDeploymentsContext";
import { AuthSession } from "../authSessions/AuthSessionApi";
import { DeploymentState } from "@kie-tools-core/kubernetes-bridge/dist/resources/common";
import { KieSandboxDeployment } from "./services/types";
import { NEW_WORKSPACE_DEFAULT_NAME } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";

interface Props {
  id: number;
  deployment: KieSandboxDeployment;
  cloudAuthSession: AuthSession;
}

const MAX_DEPLOYMENT_NAME_LENGTH = 30;

export function DevDeploymentsDropdownItem(props: Props) {
  const { i18n } = useOnlineI18n();
  const devDeployments = useDevDeployments();
  const workspaces = useWorkspaces();
  const [currentWorkspaceName, setCurrentWorkspaceName] = useState<string>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        workspaces
          .getWorkspace({ workspaceId: props.deployment.workspaceId })
          .then((workspace) => {
            if (canceled.get()) {
              return;
            }
            if (workspace.name === NEW_WORKSPACE_DEFAULT_NAME) {
              workspaces.getFiles({ workspaceId: props.deployment.workspaceId }).then((workspaceFiles) => {
                if (canceled.get()) {
                  return;
                }
                setCurrentWorkspaceName(workspaceFiles[0].name);
                return;
              });
            } else {
              setCurrentWorkspaceName(workspace.name);
            }
          })
          .catch(() => setCurrentWorkspaceName(""));
      },
      [props.deployment.workspaceId, workspaces]
    )
  );

  const shouldDisplayNameChange = useMemo(
    () => currentWorkspaceName && currentWorkspaceName !== props.deployment.workspaceName,
    [currentWorkspaceName, props.deployment.workspaceName]
  );

  const stateIcon = useMemo(() => {
    if (props.deployment.state === DeploymentState.UP) {
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
      props.deployment.state === DeploymentState.IN_PROGRESS ||
      props.deployment.state === DeploymentState.PREPARING
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

    if (props.deployment.state === DeploymentState.ERROR) {
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
    window.open(`${props.deployment.routeUrl}`, "_blank");
  }, [props.deployment.routeUrl]);

  const onDelete = useCallback(() => {
    devDeployments.setConfirmDeleteModalState({
      isOpen: true,
      cloudAuthSessionId: props.cloudAuthSession.id,
      resources: props.deployment.resources,
    });
  }, [devDeployments, props.cloudAuthSession.id, props.deployment]);

  return (
    <Flex>
      <FlexItem grow={{ default: "grow" }} style={{ margin: "0" }}>
        <DropdownItem
          id="dev-deployments-deployment-item-button"
          key={`dev-deployments-dropdown-item-${props.id}`}
          onClick={onItemClicked}
          description={
            <>
              {props.deployment.name}
              <br />
              {i18n.devDeployments.dropdown.item.createdAt(props.deployment.creationTimestamp.toLocaleString())}
            </>
          }
          icon={stateIcon}
          tooltip={
            shouldDisplayNameChange ? (
              <>
                Renamed to <b>{currentWorkspaceName}</b>
              </>
            ) : (
              !currentWorkspaceName && <b>Workspace not found</b>
            )
          }
        >
          {(props.deployment.workspaceName ?? currentWorkspaceName ?? props.deployment.name).substring(
            0,
            MAX_DEPLOYMENT_NAME_LENGTH
          )}
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
