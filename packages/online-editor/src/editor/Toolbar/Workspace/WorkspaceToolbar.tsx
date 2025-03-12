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

import React, { useCallback, useEffect, useRef } from "react";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useHistory } from "react-router";
import { useRoutes } from "../../../navigation/Hooks";
import AngleLeftIcon from "@patternfly/react-icons/dist/js/icons/angle-left-icon";
import { AuthSessionSelect } from "../../../authSessions/AuthSessionSelect";
import { AuthProviderGroup } from "../../../authProviders/AuthProvidersApi";
import { useGitIntegration } from "../GitIntegration/GitIntegrationContextProvider";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../../../accounts/AccountsContext";
import { WorkspaceLabel } from "../../../workspace/components/WorkspaceLabel";
import FolderIcon from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceStatusIndicator } from "../../../workspace/components/WorkspaceStatusIndicator";
import { AcceleratorIndicator } from "../Accelerators/AcceleratorIndicator";
import { PromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { WorkspaceGitStatusType } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

type Props = {
  workspace: ActiveWorkspace;
  currentWorkspaceFile: WorkspaceFile;
  workspaceGitStatusPromise: PromiseState<WorkspaceGitStatusType>;
  onDeletedWorkspaceFile: () => void;
};

export function WorkspaceToolbar(props: Props) {
  const history = useHistory();
  const routes = useRoutes();
  const workspaces = useWorkspaces();

  const {
    auth: { changeGitAuthSessionId, authSessionSelectFilter },
  } = useGitIntegration();

  const accountsDispatch = useAccountsDispatch();

  const workspaceNameRef = useRef<HTMLInputElement>(null);

  const resetWorkspaceName = useCallback(() => {
    if (workspaceNameRef.current) {
      workspaceNameRef.current.value = props.workspace.descriptor.name;
    }
  }, [props.workspace]);

  useEffect(resetWorkspaceName, [resetWorkspaceName]);

  const onRenameWorkspace = useCallback(
    async (newName: string | undefined) => {
      if (!newName) {
        resetWorkspaceName();
        return;
      }

      if (newName === props.workspace.descriptor.name) {
        return;
      }

      await workspaces.renameWorkspace({
        workspaceId: props.workspace.descriptor.workspaceId,
        newName: newName.trim(),
      });
    },
    [props.workspace.descriptor, workspaces, resetWorkspaceName]
  );

  const onWorkspaceNameKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      e.stopPropagation();
      if (e.keyCode === 13 /* Enter */) {
        e.currentTarget.blur();
      } else if (e.keyCode === 27 /* ESC */) {
        resetWorkspaceName();
        e.currentTarget.blur();
      }
    },
    [resetWorkspaceName]
  );

  return (
    <Flex
      justifyContent={{ default: "justifyContentFlexStart" }}
      flexWrap={{ default: "nowrap" }}
      spaceItems={{ default: "spaceItemsSm" }}
      alignItems={{ default: "alignItemsCenter" }}
    >
      <FlexItem>
        <Button
          className={"kie-tools--masthead-hoverable"}
          variant={ButtonVariant.plain}
          onClick={() => history.push({ pathname: routes.home.path({}) })}
        >
          <AngleLeftIcon />
        </Button>
      </FlexItem>
      <FlexItem>
        <AuthSessionSelect
          title={`Select Git authentication for '${props.workspace.descriptor.name}'...`}
          isPlain={true}
          authSessionId={props.workspace.descriptor.gitAuthSessionId}
          showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.GIT}
          setAuthSessionId={(newAuthSessionId) => {
            changeGitAuthSessionId(newAuthSessionId, props.workspace.descriptor.gitAuthSessionId);
            accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
          }}
          filter={authSessionSelectFilter}
        />
      </FlexItem>
      <FlexItem>
        <WorkspaceLabel descriptor={props.workspace.descriptor} />
      </FlexItem>
      <FlexItem>
        <AcceleratorIndicator workspaceId={props.workspace.descriptor.workspaceId} />
      </FlexItem>
      <FlexItem
        style={{ minWidth: 0, padding: "0 8px 0 8px", flexShrink: 0 }}
        className={"kie-tools--masthead-hoverable"}
      >
        <Icon size="lg" style={{ marginRight: "8px", verticalAlign: "middle" }}>
          <FolderIcon />
        </Icon>

        <div
          data-testid={"toolbar-title-workspace"}
          className={"kogito--editor__toolbar-name-container"}
          style={{ display: "inline-block", verticalAlign: "middle" }}
        >
          <Title
            aria-label={"EmbeddedEditorFile name"}
            headingLevel={"h3"}
            size={"md"}
            style={{
              fontStyle: "italic",
            }}
          >
            {props.workspace.descriptor.name}
          </Title>
          <TextInput
            ref={workspaceNameRef}
            type={"text"}
            aria-label={"Edit workspace name"}
            onKeyDown={onWorkspaceNameKeyDown}
            className={"kogito--editor__toolbar-subtitle"}
            onBlur={(e) => onRenameWorkspace(e.target.value)}
            style={{ fontStyle: "italic", top: "4px", height: "calc(100% - 8px)" }}
          />
        </div>
      </FlexItem>
      <FlexItem>
        <WorkspaceStatusIndicator
          gitStatusProps={{
            workspaceDescriptor: props.workspace.descriptor,
            workspaceGitStatusPromise: props.workspaceGitStatusPromise,
          }}
          currentWorkspaceFile={props.currentWorkspaceFile}
          onDeletedWorkspaceFile={props.onDeletedWorkspaceFile}
          workspaceFiles={props.workspace.files}
        />
      </FlexItem>
    </Flex>
  );
}
