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
import * as React from "react";
import { WorkspaceGitStatusType } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { useCallback } from "react";
import {
  isGitBasedWorkspaceKind,
  WorkspaceKind,
} from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { OutlinedClockIcon } from "@patternfly/react-icons/dist/js/icons/outlined-clock-icon";
import { SecurityIcon } from "@patternfly/react-icons/dist/js/icons/security-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { useNavigationBlocker, useRoutes } from "../../navigation/Hooks";
import { matchPath } from "react-router";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { GitStatusIndicatorActions, GitStatusProps } from "./GitStatusIndicatorActions";
import { FileModificationStatus } from "@kie-tools-core/workspaces-git-fs/dist/services/GitService";
/**
 * Indicates current git sync status either for whole Workspace or a particular WorkspaceFile, depending on the provided properties.
 */
export function GitStatusIndicator(
  props: React.PropsWithChildren<{
    gitStatusProps: GitStatusProps;
    workspaceFile?: WorkspaceFile;
    isHoverable?: boolean;
  }>
) {
  // We use this trick to prevent the icon from blinking while updating.
  const prev = usePrevious(props.gitStatusProps.workspaceGitStatusPromise?.data);

  const localChangesStatus = React.useMemo(() => {
    return resolveGitLocalChangesStatus({
      workspaceGitStatus: props.gitStatusProps.workspaceGitStatusPromise?.data ?? prev,
      file: props.workspaceFile,
    });
  }, [prev, props]);

  const indicatorTooltip = React.useMemo(() => {
    if (!props.gitStatusProps.workspaceGitStatusPromise?.data?.hasLocalChanges) {
      return [];
    }

    const tooltipForStageStatus = (stageStatus?: FileModificationStatus) => {
      const modifiedTooltip = (
        <Tooltip content={"Modified"} position={"bottom"}>
          <small>
            <i>M</i>
          </small>
        </Tooltip>
      );
      const deletedTooltip = (
        <Tooltip content={"Deleted"} position={"bottom"}>
          <small>
            <i>D</i>
          </small>
        </Tooltip>
      );
      const addedTooltip = (
        <Tooltip content={"Added"} position={"bottom"}>
          <small>
            <i>A</i>
          </small>
        </Tooltip>
      );
      return switchExpression(stageStatus, {
        added: addedTooltip,
        modified: modifiedTooltip,
        deleted: deletedTooltip,
        default: <></>,
      });
    };
    return props.workspaceFile
      ? tooltipForStageStatus(
          props.gitStatusProps.workspaceGitStatusPromise?.data?.unstagedModifiedFilesStatus.find(
            ({ path }) => path === props.workspaceFile?.relativePath
          )?.status
        )
      : tooltipForStageStatus(FileModificationStatus.modified);
  }, [
    props.gitStatusProps.workspaceGitStatusPromise?.data?.hasLocalChanges,
    props.gitStatusProps.workspaceGitStatusPromise?.data?.unstagedModifiedFilesStatus,
    props.workspaceFile,
  ]);

  return (
    <Flex
      flexWrap={{ default: "nowrap" }}
      spaceItems={{ default: "spaceItemsMd" }}
      className={"kie-tools--git-status-indicator"}
    >
      {(isGitBasedWorkspaceKind(props.gitStatusProps.workspaceDescriptor.origin.kind) ||
        props.gitStatusProps.workspaceDescriptor.origin.kind === WorkspaceKind.LOCAL) &&
        !props.workspaceFile &&
        switchExpression(resolveGitSyncStatus(props.gitStatusProps.workspaceGitStatusPromise?.data ?? prev), {
          pending: (
            <Title headingLevel={"h6"} style={{ display: "inline", cursor: "default" }}>
              <Tooltip content={`There are new changes since your last sync.`} position={"bottom"}>
                <SecurityIcon color={"gray"} />
              </Tooltip>
            </Title>
          ),
          synced: (
            <Title headingLevel={"h6"} style={{ display: "inline", cursor: "default" }}>
              <Tooltip content={`All files are synced.`} position={"bottom"}>
                <CheckCircleIcon color={"green"} />
              </Tooltip>
            </Title>
          ),
          unknown: (
            <Title headingLevel={"h6"} style={{ display: "inline", cursor: "default" }}>
              <Tooltip content={"Checking status..."} position={"right"}>
                <OutlinedClockIcon color={"gray"} />
              </Tooltip>
            </Title>
          ),
        })}
      {switchExpression(localChangesStatus, {
        pending: (
          <Flex
            flexWrap={{ default: "nowrap" }}
            spaceItems={{ default: "spaceItemsSm" }}
            alignItems={{ default: "alignItemsCenter" }}
            onClick={(e) => {
              e.stopPropagation();
              e.preventDefault();
            }}
          >
            <FlexItem>
              <Title headingLevel={"h6"} style={{ display: "inline", cursor: "default" }}>
                <Flex spaceItems={{ default: "spaceItemsXs" }} direction={{ default: "row" }}>
                  {indicatorTooltip}
                </Flex>
              </Title>
            </FlexItem>
            <FlexItem
              alignSelf={{ default: "alignSelfCenter" }}
              className={props.isHoverable ? "kie-tools--git-status-indicator-children-hoverable" : ""}
            >
              {props.children}
            </FlexItem>
          </Flex>
        ),
        default: <></>,
      })}
    </Flex>
  );
}

export function WorkspaceStatusIndicator(props: {
  gitStatusProps: GitStatusProps;
  currentWorkspaceFile: WorkspaceFile;
  onDeletedWorkspaceFile: () => void;
  workspaceFiles: WorkspaceFile[];
}) {
  const routes = useRoutes();
  const [isActionsDropdownOpen, setActionsDropdownOpen] = React.useState(false);

  const isEverythingPersistedByTheUser =
    props.gitStatusProps.workspaceGitStatusPromise &&
    props.gitStatusProps.workspaceGitStatusPromise.data?.isSynced &&
    !props.gitStatusProps.workspaceGitStatusPromise.data?.hasLocalChanges;

  // Prevent from navigating away
  useNavigationBlocker(
    `block-navigation-for-${props.gitStatusProps.workspaceDescriptor.workspaceId}`,
    useCallback(
      ({ location }) => {
        const match = matchPath<{ workspaceId: string }>(location.pathname, {
          strict: true,
          exact: true,
          sensitive: false,
          path: routes.workspaceWithFilePath.path({
            workspaceId: ":workspaceId",
            fileRelativePath: ":fileRelativePath*",
            extension: ":extension",
          }),
        });

        if (match?.params.workspaceId === props.gitStatusProps.workspaceDescriptor.workspaceId) {
          return false;
        }

        return !isEverythingPersistedByTheUser;
      },
      [
        routes.workspaceWithFilePath,
        props.gitStatusProps.workspaceDescriptor.workspaceId,
        isEverythingPersistedByTheUser,
      ]
    )
  );
  return (
    <GitStatusIndicator gitStatusProps={props.gitStatusProps} isHoverable={!isActionsDropdownOpen}>
      <GitStatusIndicatorActions
        isOpen={isActionsDropdownOpen}
        setOpen={setActionsDropdownOpen}
        currentWorkspaceFile={props.currentWorkspaceFile}
        onDeletedWorkspaceFile={props.onDeletedWorkspaceFile}
        gitStatusProps={props.gitStatusProps}
        workspaceFiles={props.workspaceFiles}
      />
    </GitStatusIndicator>
  );
}

export enum WorkspaceGitSyncStatus {
  synced = "synced",
  unknown = "unknown",
  pending = "pending",
}

export enum WorkspaceGitLocalChangesStatus {
  synced = "synced",
  unknown = "unknown",
  pending = "pending",
}

export const resolveGitLocalChangesStatus = (args: {
  workspaceGitStatus?: WorkspaceGitStatusType;
  file?: WorkspaceFile;
}) => {
  if (args.workspaceGitStatus === undefined) {
    return WorkspaceGitLocalChangesStatus.unknown;
  }

  if (args.file) {
    return args.workspaceGitStatus.unstagedModifiedFilesStatus.some(
      (stageStatus) => stageStatus.path === args.file?.relativePath
    )
      ? WorkspaceGitLocalChangesStatus.pending
      : WorkspaceGitLocalChangesStatus.synced;
  }

  return args.workspaceGitStatus.hasLocalChanges
    ? WorkspaceGitLocalChangesStatus.pending
    : WorkspaceGitLocalChangesStatus.synced;
};

export const resolveGitSyncStatus = (workspaceGitStatus?: WorkspaceGitStatusType) => {
  if (workspaceGitStatus === undefined) {
    return WorkspaceGitSyncStatus.unknown;
  }

  return workspaceGitStatus.isSynced ? WorkspaceGitSyncStatus.synced : WorkspaceGitSyncStatus.pending;
};

/**
 * Based on Git status, returns files that were removed from repository, but changes not yet commited.
 * @returns array of WorkspaceFile instances with empty file contents
 */
export const listDeletedFiles = (args: GitStatusProps) => {
  if (!args.workspaceGitStatusPromise.data) {
    return [];
  }

  return args.workspaceGitStatusPromise.data.unstagedModifiedFilesStatus
    .filter((stageStatusEntry) => stageStatusEntry.status === FileModificationStatus.deleted)
    .map((status) => {
      return new WorkspaceFile({
        workspaceId: args.workspaceDescriptor.workspaceId,
        relativePath: status.path,
        getFileContents: () => Promise.resolve(new Uint8Array()),
      });
    });
};
