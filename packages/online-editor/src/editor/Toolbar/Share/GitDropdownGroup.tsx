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
  isGistLikeWorkspaceKind,
} from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { useOnlineI18n } from "../../../i18n";
import {
  AuthProviderGroup,
  GistEnabledAuthProviderType,
  GitAuthProviderType,
} from "../../../authProviders/AuthProvidersApi";
import { SelectPosition } from "@patternfly/react-core/deprecated";
import { DropdownGroup, DropdownItem } from "@patternfly/react-core/deprecated";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { AuthSessionSelect } from "../../../authSessions/AuthSessionSelect";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../../../accounts/AccountsContext";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import BitbucketIcon from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import GithubIcon from "@patternfly/react-icons/dist/js/icons/github-icon";
import { GitlabIcon } from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import GitIcon from "@patternfly/react-icons/dist/js/icons/git-icon";
import { useAuthSession } from "../../../authSessions/AuthSessionsContext";
import { useAuthProvider } from "../../../authProviders/AuthProvidersContext";
import { useGitIntegration } from "../GitIntegration/GitIntegrationContextProvider";
import { useEditorToolbarDispatchContext } from "../EditorToolbarContextProvider";

type Props = {
  workspace: ActiveWorkspace;
};

export const GitDropdownGroup = (props: Props) => {
  const { i18n } = useOnlineI18n();
  const { authSession } = useAuthSession(props.workspace.descriptor.gitAuthSessionId);
  const authProvider = useAuthProvider(authSession);
  const accountsDispatch = useAccountsDispatch();

  const {
    auth: { authSessionSelectFilter, changeGitAuthSessionId },
    git: { canCreateGitRepository },
    gistOrSnippet: { canCreateGistOrSnippet },
  } = useGitIntegration();
  const { setCreateGitRepositoryModalOpen, setCreateGistOrSnippetModalOpen, setShareDropdownOpen, setSmallKebabOpen } =
    useEditorToolbarDispatchContext();

  if (
    props.workspace.descriptor.origin.kind !== WorkspaceKind.LOCAL &&
    !isGistLikeWorkspaceKind(props.workspace.descriptor.origin.kind)
  ) {
    return null;
  }

  return (
    <>
      <DropdownGroup key={"git-group"} label={i18n.names.git}>
        <Alert
          isInline={true}
          variant={"info"}
          title={"Authentication source"}
          actionLinks={
            <AuthSessionSelect
              title={`Select Git authentication for '${props.workspace.descriptor.name}'...`}
              position={SelectPosition.right}
              isPlain={false}
              authSessionId={props.workspace.descriptor.gitAuthSessionId}
              showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.GIT}
              setAuthSessionId={(newAuthSessionId) => {
                changeGitAuthSessionId(newAuthSessionId, props.workspace.descriptor.gitAuthSessionId);
                setTimeout(() => {
                  accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                  setShareDropdownOpen(true);
                  setSmallKebabOpen(true);
                }, 0);
              }}
              filter={authSessionSelectFilter}
            />
          }
        >
          {`Manage authentication sources for '${props.workspace.descriptor.name}' to be able to create Repository, GitHub Gist, Bitbucket Snippet or GitLab Snippet.`}
        </Alert>
        {authProvider && (
          <Tooltip
            data-testid={"create-git-repository-tooltip"}
            key={`dropdown-create-git-repository`}
            content={<div>{"Click to create a repository."}</div>}
            trigger={!canCreateGitRepository ? "mouseenter click" : ""}
            position="left"
          >
            <DropdownItem
              icon={switchExpression(authProvider?.type as GitAuthProviderType, {
                bitbucket: <BitbucketIcon />,
                github: <GithubIcon />,
                gitlab: <GitlabIcon />,
                default: <GitIcon />,
              })}
              data-testid={"create-git-repository-button"}
              component="button"
              onClick={() => setCreateGitRepositoryModalOpen(true)}
              isDisabled={!canCreateGitRepository}
            >
              Create{" "}
              {switchExpression(authProvider?.type as GitAuthProviderType, {
                github: "GitHub repository",
                bitbucket: "Bitbucket repository",
                gitlab: "GitLab repository",
                default: "Git repository",
              })}
              ...
            </DropdownItem>
          </Tooltip>
        )}
        {authProvider && (
          <Tooltip
            data-testid={"create-gist-or-snippet-tooltip"}
            key={`dropdown-create-gist-or-snippet`}
            content={
              <div>
                {switchExpression(authProvider?.type as GistEnabledAuthProviderType, {
                  github: i18n.editorToolbar.cantCreateGistTooltip,
                  bitbucket: i18n.editorToolbar.cantCreateSnippetTooltip,
                  gitlab: i18n.editorToolbar.cantCreateSnippetTooltip,
                })}
              </div>
            }
            trigger={!canCreateGistOrSnippet ? "mouseenter click" : ""}
            position="left"
          >
            <DropdownItem
              icon={switchExpression(authProvider?.type as GistEnabledAuthProviderType, {
                bitbucket: <BitbucketIcon />,
                github: <GithubIcon />,
                gitlab: <GitlabIcon />,
              })}
              data-testid={"create-gist-or-snippet-button"}
              component="button"
              onClick={() => setCreateGistOrSnippetModalOpen(true)}
              isDisabled={!canCreateGistOrSnippet}
            >
              {switchExpression(authProvider?.type as GistEnabledAuthProviderType, {
                github: i18n.editorToolbar.createGist,
                bitbucket: i18n.editorToolbar.createSnippet,
                gitlab: i18n.editorToolbar.createSnippet,
              })}
            </DropdownItem>
          </Tooltip>
        )}
      </DropdownGroup>
    </>
  );
};
