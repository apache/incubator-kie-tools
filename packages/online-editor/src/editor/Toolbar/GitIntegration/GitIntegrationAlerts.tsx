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

import React, { useCallback } from "react";
import { useGlobalAlert } from "../../../alerts";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import {
  WorkspaceKind,
  WorkspaceKindGistLike,
  isGistLikeWorkspaceKind,
} from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { useOnlineI18n } from "../../../i18n";

export function useGitIntegrationAlerts(workspace: ActiveWorkspace) {
  const { i18n } = useOnlineI18n();

  const createRepositorySuccessAlert = useGlobalAlert<{ url: string }>(
    useCallback(({ close }, { url }) => {
      return (
        <Alert
          variant="success"
          title={`GitHub repository created.`}
          actionClose={<AlertActionCloseButton onClose={close} />}
          actionLinks={<AlertActionLink onClick={() => window.open(url, "_blank")}>{url}</AlertActionLink>}
        />
      );
    }, [])
  );

  const pushingAlert = useGlobalAlert(
    useCallback(() => {
      if (workspace.descriptor.origin.kind !== WorkspaceKind.GIT) {
        return <></>;
      }

      return (
        <Alert
          variant="info"
          title={
            <>
              <Spinner size={"sm"} />
              &nbsp;&nbsp; {`Pushing to '${workspace.descriptor.origin.url}'...`}
            </>
          }
        />
      );
    }, [workspace.descriptor])
  );

  const comittingAlert = useGlobalAlert(
    useCallback(() => {
      return (
        <Alert
          variant="info"
          title={
            <>
              <Spinner size={"sm"} />
              &nbsp;&nbsp; {`Creating commit...`}
            </>
          }
        />
      );
    }, [])
  );

  const commitSuccessAlert = useGlobalAlert(
    useCallback(() => {
      return <Alert variant="success" title={`Commit created.`} />;
    }, []),
    { durationInSeconds: 2 }
  );

  const commitFailAlert = useGlobalAlert(
    useCallback(({ close }, staticArgs: { reason: string }) => {
      return (
        <Alert
          variant="danger"
          title={`Failed to commit: ${staticArgs.reason}`}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const pushSuccessAlert = useGlobalAlert(
    useCallback(() => {
      if (workspace.descriptor.origin.kind !== WorkspaceKind.GIT) {
        return <></>;
      }

      return <Alert variant="success" title={`Pushed to '${workspace.descriptor.origin.url}'`} />;
    }, [workspace.descriptor]),
    { durationInSeconds: 4 }
  );

  const pushErrorAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (workspace.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return (
          <Alert
            variant="danger"
            title={`Error Pushing to '${workspace.descriptor.origin.url}'`}
            actionClose={<AlertActionCloseButton onClose={close} />}
          />
        );
      },
      [workspace.descriptor]
    )
  );

  const pullingAlert = useGlobalAlert(
    useCallback(() => {
      if (workspace.descriptor.origin.kind !== WorkspaceKind.GIT) {
        return <></>;
      }

      return (
        <Alert
          variant="info"
          title={
            <>
              <Spinner size={"sm"} />
              &nbsp;&nbsp; {`Pulling from '${workspace.descriptor.origin.url}'...`}
            </>
          }
        />
      );
    }, [workspace.descriptor])
  );

  const pullSuccessAlert = useGlobalAlert(
    useCallback(() => {
      if (workspace.descriptor.origin.kind !== WorkspaceKind.GIT) {
        return <></>;
      }

      return <Alert variant="success" title={`Pulled from '${workspace.descriptor.origin.url}'`} />;
    }, [workspace.descriptor]),
    { durationInSeconds: 4 }
  );

  const pullErrorAlert = useGlobalAlert<{
    newBranchName: string;
    canPushToGitRepository: boolean;
    pushNewBranch: (branchName: string) => Promise<void>;
  }>(
    useCallback(
      ({ close }, { newBranchName, canPushToGitRepository, pushNewBranch }) => {
        if (workspace.descriptor.origin.kind !== WorkspaceKind.GIT) {
          return <></>;
        }

        return (
          <Alert
            variant="danger"
            title={`Error Pulling from '${workspace.descriptor.origin.url}'`}
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={<></>}
          >
            {`This usually happens when your branch has conflicts with the upstream branch or you don't have permission to Pull.`}
            <br />
            <br />
            {`You can save your work to a new branch.`}
            <br />
            <br />
            <Tooltip
              data-testid={"gist-it-tooltip"}
              content={<div>{`You need select an authentication source to be able to Push to a new branch.`}</div>}
              trigger={!canPushToGitRepository ? "mouseenter click" : ""}
              position="left"
            >
              <Button
                onClick={() => pushNewBranch(newBranchName)}
                variant={ButtonVariant.link}
                style={{ paddingLeft: 0 }}
                size="sm"
                isDisabled={!canPushToGitRepository}
              >
                {`Switch to '${newBranchName}'`}
              </Button>
            </Tooltip>
            <br />
            <br />

            {`Or change the authentication source for '${workspace.descriptor.name}' and try again`}
            <br />
          </Alert>
        );
      },
      [workspace.descriptor]
    )
  );

  const nothingToCommitAlert = useGlobalAlert(
    useCallback(() => {
      return <Alert variant="info" title={"Nothing to commit."} />;
    }, []),
    { durationInSeconds: 2 }
  );

  const successfullyCreatedGistOrSnippetAlert = useGlobalAlert(
    useCallback(
      ({ close }, staticArgs?: { url: string }) => {
        if (!isGistLikeWorkspaceKind(workspace.descriptor.origin.kind)) {
          return <></>;
        }

        const gistOrSnippetUrl = staticArgs?.url || workspace.descriptor.origin.url;
        return (
          <Alert
            variant="success"
            title={switchExpression(workspace.descriptor.origin.kind, {
              GITHUB_GIST: i18n.editorPage.alerts.createGist,
              BITBUCKET_SNIPPET: i18n.editorPage.alerts.createSnippet,
              GITLAB_SNIPPET: i18n.editorPage.alerts.createSnippet,
            })}
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={
              <AlertActionLink onClick={() => window.open(gistOrSnippetUrl, "_blank")}>
                {gistOrSnippetUrl}
              </AlertActionLink>
            }
          />
        );
      },
      [
        i18n.editorPage.alerts.createGist,
        i18n.editorPage.alerts.createSnippet,
        workspace.descriptor.origin.kind,
        workspace.descriptor.origin.url,
      ]
    ),
    { durationInSeconds: 4 }
  );

  const loadingGistOrSnippetAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (!isGistLikeWorkspaceKind(workspace.descriptor.origin.kind)) {
          return <></>;
        }

        const gistOrSnippetUrl = switchExpression(workspace.descriptor.origin.kind, {
          // Remove the `.git` extension from GitLab snippet URLs.
          // If a snippet URL ends with `.git`, GitLab may return a 404 error when accessed.
          // This ensures only the extension is removed, not other parts of the URL.
          GITLAB_SNIPPET: workspace.descriptor.origin.url?.replace?.(/\.git$/, ""),
          default: workspace.descriptor.origin.url,
        });
        return (
          <Alert
            variant="info"
            title={
              <>
                <Spinner size={"sm"} />
                &nbsp;&nbsp; Updating{" "}
                {switchExpression(workspace.descriptor.origin.kind, {
                  BITBUCKET_SNIPPET: "Snippet",
                  GITHUB_GIST: "Gist",
                  GITLAB_SNIPPET: "Snippet",
                })}
                ...
              </>
            }
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={
              <AlertActionLink onClick={() => window.open(gistOrSnippetUrl, "_blank")}>
                {gistOrSnippetUrl}
              </AlertActionLink>
            }
          />
        );
      },
      [workspace.descriptor.origin.kind, workspace.descriptor.origin.url]
    )
  );

  const successfullyUpdatedGistOrSnippetAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        if (!isGistLikeWorkspaceKind(workspace.descriptor.origin.kind)) {
          return <></>;
        }

        const gistOrSnippetUrl = switchExpression(workspace.descriptor.origin.kind, {
          // Remove the `.git` extension from GitLab snippet URLs.
          // If a snippet URL ends with `.git`, GitLab may return a 404 error when accessed.
          // This ensures only the extension is removed, not other parts of the URL.
          GITLAB_SNIPPET: workspace.descriptor.origin.url?.replace?.(/\.git$/, ""),
          default: workspace.descriptor.origin.url,
        });
        return (
          <Alert
            variant="success"
            title={switchExpression(workspace.descriptor.origin.kind, {
              GITHUB_GIST: i18n.editorPage.alerts.updateGist,
              BITBUCKET_SNIPPET: i18n.editorPage.alerts.updateSnippet,
              GITLAB_SNIPPET: i18n.editorPage.alerts.updateSnippet,
            })}
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={
              <AlertActionLink onClick={() => window.open(gistOrSnippetUrl, "_blank")}>
                {gistOrSnippetUrl}
              </AlertActionLink>
            }
          />
        );
      },
      [
        i18n.editorPage.alerts.updateGist,
        i18n.editorPage.alerts.updateSnippet,
        workspace.descriptor.origin.kind,
        workspace.descriptor.origin.url,
      ]
    ),
    { durationInSeconds: 4 }
  );

  const errorPushingGistOrSnippet = useGlobalAlert<{ forceUpdateGistOrSnippet: () => Promise<void> }>(
    useCallback(
      ({ close }, { forceUpdateGistOrSnippet }) => {
        return (
          <Alert
            variant="danger"
            title={switchExpression(workspace.descriptor.origin.kind as WorkspaceKindGistLike, {
              GITHUB_GIST: i18n.editorPage.alerts.errorPushingGist,
              BITBUCKET_SNIPPET: i18n.editorPage.alerts.errorPushingSnippet,
              GITLAB_SNIPPET: i18n.editorPage.alerts.errorPushingSnippet,
            })}
            actionLinks={[
              <AlertActionLink
                key="force"
                onClick={() => {
                  close();
                  forceUpdateGistOrSnippet();
                }}
              >
                Push forcefully
              </AlertActionLink>,
              <AlertActionLink key="dismiss" onClick={close}>
                Dismiss
              </AlertActionLink>,
            ]}
            actionClose={<AlertActionCloseButton onClose={close} />}
          >
            <b>{i18n.editorPage.alerts.forcePushWarning}</b>
          </Alert>
        );
      },
      [
        workspace.descriptor.origin.kind,
        i18n.editorPage.alerts.errorPushingGist,
        i18n.editorPage.alerts.errorPushingSnippet,
        i18n.editorPage.alerts.forcePushWarning,
      ]
    )
  );

  const errorAlert = useGlobalAlert(
    useCallback(
      ({ close }) => (
        <Alert
          variant="danger"
          title={i18n.editorPage.alerts.error}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    )
  );

  return {
    createRepositorySuccessAlert,
    comittingAlert,
    commitSuccessAlert,
    commitFailAlert,
    pushingAlert,
    pushSuccessAlert,
    pushErrorAlert,
    pullingAlert,
    pullSuccessAlert,
    pullErrorAlert,
    nothingToCommitAlert,
    successfullyCreatedGistOrSnippetAlert,
    loadingGistOrSnippetAlert,
    successfullyUpdatedGistOrSnippetAlert,
    errorPushingGistOrSnippet,
    errorAlert,
  };
}
