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

import { ActiveWorkspace } from "../model/ActiveWorkspace";
import { useWorkspaceGitStatusPromise } from "../hooks/WorkspaceHooks";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { WorkspaceKind } from "../worker/api/WorkspaceOrigin";
import { PromiseStateWrapper } from "../hooks/PromiseState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { OutlinedClockIcon } from "@patternfly/react-icons/dist/js/icons/outlined-clock-icon";
import { SecurityIcon } from "@patternfly/react-icons/dist/js/icons/security-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { usePrevious } from "../../reactExt/Hooks";
import { useNavigationBlocker, useRoutes } from "../../navigation/Hooks";
import { matchPath } from "react-router";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";

function Indicator(props: { workspace: ActiveWorkspace; isSynced: boolean; hasLocalChanges: boolean }) {
  return (
    <Flex flexWrap={{ default: "nowrap" }} spaceItems={{ default: "spaceItemsMd" }}>
      {(props.workspace.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST ||
        props.workspace.descriptor.origin.kind === WorkspaceKind.GIT) && (
        <>
          {(!props.isSynced && (
            <Title headingLevel={"h6"} style={{ display: "inline", cursor: "default" }}>
              <Tooltip content={`There are new changes since your last sync.`} position={"bottom"}>
                <small>
                  <SecurityIcon color={"gray"} />
                </small>
              </Tooltip>
            </Title>
          )) || (
            <Title headingLevel={"h6"} style={{ display: "inline", cursor: "default" }}>
              <Tooltip content={`All files are synced.`} position={"bottom"}>
                <small>
                  <CheckCircleIcon color={"green"} />
                </small>
              </Tooltip>
            </Title>
          )}
        </>
      )}
      {props.hasLocalChanges && (
        <Title headingLevel={"h6"} style={{ display: "inline", cursor: "default" }}>
          <Tooltip content={"You have local changes."} position={"bottom"}>
            <small>
              <i>M</i>
            </small>
          </Tooltip>
        </Title>
      )}
    </Flex>
  );
}

export function WorkspaceStatusIndicator(props: { workspace: ActiveWorkspace }) {
  const routes = useRoutes();
  const workspaceGitStatusPromise = useWorkspaceGitStatusPromise(props.workspace);

  const isEverythingPersistedByTheUser = useMemo(() => {
    return (
      workspaceGitStatusPromise.data &&
      workspaceGitStatusPromise.data.isSynced &&
      !workspaceGitStatusPromise.data.hasLocalChanges
    );
  }, [workspaceGitStatusPromise]);

  // Prevent from navigating away
  useNavigationBlocker(
    `block-navigation-for-${props.workspace.descriptor.workspaceId}`,
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

        if (match?.params.workspaceId === props.workspace.descriptor.workspaceId) {
          return false;
        }

        return !isEverythingPersistedByTheUser;
      },
      [routes, isEverythingPersistedByTheUser, props.workspace.descriptor.workspaceId]
    )
  );

  // We use this trick to prevent the icon from blinking while updating.
  const prev = usePrevious(workspaceGitStatusPromise);

  return (
    <PromiseStateWrapper
      promise={workspaceGitStatusPromise}
      pending={
        <>
          {(prev?.data && (
            <Indicator
              workspace={props.workspace}
              hasLocalChanges={prev.data.hasLocalChanges}
              isSynced={prev.data.isSynced}
            />
          )) || (
            <Flex flexWrap={{ default: "nowrap" }} spaceItems={{ default: "spaceItemsNone" }}>
              <Title headingLevel={"h6"} style={{ display: "inline", cursor: "default" }}>
                <Tooltip content={"Checking status..."} position={"right"}>
                  <small>
                    <OutlinedClockIcon color={"gray"} />
                  </small>
                </Tooltip>
              </Title>
            </Flex>
          )}
        </>
      }
      resolved={({ hasLocalChanges, isSynced }) => (
        <Indicator workspace={props.workspace} hasLocalChanges={hasLocalChanges} isSynced={isSynced} />
      )}
    />
  );
}
