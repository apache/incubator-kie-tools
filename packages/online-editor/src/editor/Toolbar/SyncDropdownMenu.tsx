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

import React from "react";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import {
  WorkspaceKind,
  WorkspaceKindGistLike,
  isGistLikeWorkspaceKind,
} from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import {
  Dropdown,
  DropdownGroup,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
} from "@patternfly/react-core/deprecated";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { useOnlineI18n } from "../../i18n";
import BitbucketIcon from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import GithubIcon from "@patternfly/react-icons/dist/js/icons/github-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { AuthSessionSelect } from "../../authSessions/AuthSessionSelect";
import { AuthProviderGroup, GitAuthProviderType } from "../../authProviders/AuthProvidersApi";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../../accounts/AccountsContext";
import SyncAltIcon from "@patternfly/react-icons/dist/js/icons/sync-alt-icon";
import { GIT_ORIGIN_REMOTE_NAME } from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import ArrowCircleUpIcon from "@patternfly/react-icons/dist/js/icons/arrow-circle-up-icon";
import { useEditorToolbarContext, useEditorToolbarDispatchContext } from "./EditorToolbarContextProvider";
import { useGitIntegration } from "./GitIntegration/GitIntegrationContextProvider";
import { useAuthProvider } from "../../authProviders/AuthProvidersContext";
import { useAuthSession } from "../../authSessions/AuthSessionsContext";
import GitlabIcon from "@patternfly/react-icons/dist/js/icons/gitlab-icon";

type Props = {
  workspace: ActiveWorkspace;
};

export function SyncDropdownMenu(props: Props) {
  const { i18n } = useOnlineI18n();
  const accountsDispatch = useAccountsDispatch();
  const { authSession } = useAuthSession(props.workspace.descriptor.gitAuthSessionId);
  const authProvider = useAuthProvider(authSession);
  const { isSyncGistOrSnippetDropdownOpen, isSyncGitRepositoryDropdownOpen } = useEditorToolbarContext();

  const { setSyncGistOrSnippetDropdownOpen, setSyncGitRepositoryDropdownOpen } = useEditorToolbarDispatchContext();

  const {
    auth: { changeGitAuthSessionId, authSessionSelectFilter },
    git: { canPushToGitRepository, pushToGitRepository, pullFromGitRepository },
    gistOrSnippet: { canUpdateGistOrSnippet, updateGistOrSnippet, canForkGitHubGist, forkGitHubGist },
  } = useGitIntegration();

  return (
    <>
      {isGistLikeWorkspaceKind(props.workspace.descriptor.origin.kind) && (
        <Dropdown
          onSelect={() => setSyncGistOrSnippetDropdownOpen(false)}
          isOpen={isSyncGistOrSnippetDropdownOpen}
          position={DropdownPosition.right}
          toggle={
            <DropdownToggle
              id={"sync-dropdown"}
              data-testid={"sync-dropdown"}
              onToggle={(_event, isOpen) => setSyncGistOrSnippetDropdownOpen(isOpen)}
            >
              Sync
            </DropdownToggle>
          }
          dropdownItems={[
            <DropdownGroup key={"sync-gist-or-snippet-dropdown-group"}>
              <Tooltip
                data-testid={"gist-or-snippet-it-tooltip"}
                content={
                  <div>
                    {switchExpression(props.workspace.descriptor.origin.kind as WorkspaceKindGistLike, {
                      GITHUB_GIST: i18n.editorToolbar.cantUpdateGistTooltip,
                      BITBUCKET_SNIPPET: i18n.editorToolbar.cantUpdateSnippetTooltip,
                      GITLAB_SNIPPET: i18n.editorToolbar.cantUpdateSnippetTooltip,
                    })}
                  </div>
                }
                trigger={!canUpdateGistOrSnippet ? "mouseenter click" : ""}
                position="left"
              >
                <>
                  <DropdownItem
                    style={{ minWidth: "300px" }}
                    icon={switchExpression(props.workspace.descriptor.origin.kind as WorkspaceKindGistLike, {
                      BITBUCKET_SNIPPET: <BitbucketIcon />,
                      GITHUB_GIST: <GithubIcon />,
                      GITLAB_SNIPPET: <GitlabIcon />,
                    })}
                    onClick={updateGistOrSnippet}
                    isDisabled={!canUpdateGistOrSnippet}
                  >
                    Update{" "}
                    {switchExpression(props.workspace.descriptor.origin.kind as WorkspaceKindGistLike, {
                      BITBUCKET_SNIPPET: "Bitbucket Snippet",
                      GITHUB_GIST: "GitHub Gist",
                      GITLAB_SNIPPET: "GitLab Snippet",
                    })}
                  </DropdownItem>
                  {canForkGitHubGist && (
                    <>
                      <Divider />
                      <li role="menuitem">
                        <Alert
                          isInline={true}
                          variant={"custom"}
                          title={<span style={{ whiteSpace: "nowrap" }}>{"Can't update Gists you don't own"}</span>}
                        >
                          <br />
                          {`You can create a fork of '${props.workspace.descriptor.name}' to save your updates.`}
                          <br />
                          <br />
                          <Button
                            onClick={forkGitHubGist}
                            variant={ButtonVariant.link}
                            size="sm"
                            style={{ paddingLeft: 0 }}
                          >
                            {`Fork Gist`}
                          </Button>
                          <br />
                          <br />
                          {`Or you can change the authentication source for '${props.workspace.descriptor.name}' to be able to Update Gist.`}
                          <br />
                          <br />
                          <AuthSessionSelect
                            title={`Select Git authentication for '${props.workspace.descriptor.name}'...`}
                            isPlain={false}
                            authSessionId={props.workspace.descriptor.gitAuthSessionId}
                            showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.GIT}
                            setAuthSessionId={(newAuthSessionId) => {
                              changeGitAuthSessionId(newAuthSessionId, props.workspace.descriptor.gitAuthSessionId);
                              accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                              setTimeout(() => {
                                setSyncGistOrSnippetDropdownOpen(true);
                              }, 0);
                            }}
                            filter={authSessionSelectFilter}
                          />
                        </Alert>
                      </li>
                    </>
                  )}
                  {!canPushToGitRepository && (
                    <>
                      <Divider />
                      <Alert
                        isInline={true}
                        variant={"custom"}
                        title={`Can't Update ${switchExpression(authProvider?.type as GitAuthProviderType, {
                          github: "GitHub repository",
                          bitbucket: "Bitbucket repository",
                          gitlab: "GitLab repository",
                          default: "Git repository",
                        })} without selecting a matching authentication source`}
                        actionLinks={
                          <AuthSessionSelect
                            title={`Select Git authentication for '${props.workspace.descriptor.name}'...`}
                            isPlain={false}
                            authSessionId={props.workspace.descriptor.gitAuthSessionId}
                            showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.GIT}
                            setAuthSessionId={(newAuthSessionId) => {
                              changeGitAuthSessionId(newAuthSessionId, props.workspace.descriptor.gitAuthSessionId);
                              accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                              setTimeout(() => {
                                setSyncGistOrSnippetDropdownOpen(true);
                              }, 0);
                            }}
                            filter={authSessionSelectFilter}
                          />
                        }
                      >
                        {`Select an authentication source for '${
                          props.workspace.descriptor.name
                        }' to be able to Update ${switchExpression(
                          props.workspace.descriptor.origin.kind as WorkspaceKindGistLike,
                          {
                            GITHUB_GIST: "Github Gist",
                            BITBUCKET_SNIPPET: "Bitbucket Snippet",
                            GITLAB_SNIPPET: "GitLab Snippet",
                          }
                        )}.`}
                      </Alert>
                    </>
                  )}
                </>
              </Tooltip>
            </DropdownGroup>,
          ]}
        />
      )}
      {props.workspace.descriptor.origin.kind === WorkspaceKind.GIT && (
        <Dropdown
          onSelect={() => setSyncGitRepositoryDropdownOpen(false)}
          isOpen={isSyncGitRepositoryDropdownOpen}
          position={DropdownPosition.right}
          toggle={
            <DropdownToggle
              id={"sync-dropdown"}
              data-testid={"sync-dropdown"}
              onToggle={(_event, isOpen) => setSyncGitRepositoryDropdownOpen(isOpen)}
            >
              Sync
            </DropdownToggle>
          }
          dropdownItems={[
            <DropdownGroup key={"sync-git-dropdown-group"}>
              <DropdownItem
                icon={<SyncAltIcon />}
                onClick={() => pullFromGitRepository({ showAlerts: true })}
                description={`Get new changes made upstream at '${GIT_ORIGIN_REMOTE_NAME}/${props.workspace.descriptor.origin.branch}'.`}
              >
                Pull
              </DropdownItem>
              <Tooltip
                data-testid={"git-it-tooltip"}
                content={<div>{`You need to select an authentication source to Push to this repository.`}</div>}
                trigger={!canPushToGitRepository ? "mouseenter click" : ""}
                position="left"
              >
                <>
                  <DropdownItem
                    icon={<ArrowCircleUpIcon />}
                    onClick={pushToGitRepository}
                    isDisabled={!canPushToGitRepository}
                    description={`Send your changes upstream to '${GIT_ORIGIN_REMOTE_NAME}/${props.workspace.descriptor.origin.branch}'.`}
                  >
                    Push
                  </DropdownItem>
                  {!canPushToGitRepository && (
                    <>
                      <Alert
                        isInline={true}
                        variant={"custom"}
                        title={"Can't Push without selecting an authentication source"}
                        actionLinks={
                          <AuthSessionSelect
                            title={`Select Git authentication for '${props.workspace.descriptor.name}'...`}
                            isPlain={false}
                            authSessionId={props.workspace.descriptor.gitAuthSessionId}
                            showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.GIT}
                            setAuthSessionId={(newAuthSessionId) => {
                              changeGitAuthSessionId(newAuthSessionId, props.workspace.descriptor.gitAuthSessionId);
                              accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                              setTimeout(() => {
                                setSyncGitRepositoryDropdownOpen(true);
                              });
                            }}
                            filter={authSessionSelectFilter}
                          />
                        }
                      >
                        {`Select an authentication source for '${props.workspace.descriptor.name}' to be able to Push.`}
                      </Alert>
                    </>
                  )}
                </>
              </Tooltip>
            </DropdownGroup>,
          ]}
        />
      )}
    </>
  );
}
