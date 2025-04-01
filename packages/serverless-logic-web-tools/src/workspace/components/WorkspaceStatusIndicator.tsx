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

import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useWorkspaceGitStatusPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { OutlinedClockIcon } from "@patternfly/react-icons/dist/js/icons/outlined-clock-icon";
import { SecurityIcon } from "@patternfly/react-icons/dist/js/icons/security-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { useNavigationBlocker, useRoutes } from "../../navigation/Hooks";
import { matchPath } from "react-router-dom";

function Indicator(props: { workspace: ActiveWorkspace; isSynced: boolean; hasLocalChanges: boolean }) {
  return (
    <>
      {(props.workspace.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST ||
        props.workspace.descriptor.origin.kind === WorkspaceKind.GIT) && (
        <>
          {(!props.isSynced && (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={`There are new changes since your last sync.`} position={"right"}>
                <small>
                  <SecurityIcon color={"gray"} />
                </small>
              </Tooltip>
            </Title>
          )) || (
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={`All files are synced.`} position={"right"}>
                <small>
                  <CheckCircleIcon color={"green"} />
                </small>
              </Tooltip>
            </Title>
          )}
        </>
      )}
      {props.hasLocalChanges && (
        <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
          <Tooltip content={"You have local changes."} position={"right"}>
            <small>
              <i>M</i>
            </small>
          </Tooltip>
        </Title>
      )}
    </>
  );
}

export function WorkspaceStatusIndicator(props: { workspace: ActiveWorkspace }) {
  const routes = useRoutes();
  const workspaceGitStatusPromise = useWorkspaceGitStatusPromise(props.workspace.descriptor);

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
        const match = matchPath(
          {
            caseSensitive: false,
            end: true,
            path: routes.workspaceWithFilePath.path({
              workspaceId: ":workspaceId",
              fileRelativePath: "*",
            }),
          },
          location.pathname
        );

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
            <Title headingLevel={"h6"} style={{ display: "inline", padding: "10px", cursor: "default" }}>
              <Tooltip content={"Checking status..."} position={"right"}>
                <small>
                  <OutlinedClockIcon color={"gray"} />
                </small>
              </Tooltip>
            </Title>
          )}
        </>
      }
      resolved={({ hasLocalChanges, isSynced }) => (
        <Indicator workspace={props.workspace} hasLocalChanges={hasLocalChanges} isSynced={isSynced} />
      )}
    />
  );
}
