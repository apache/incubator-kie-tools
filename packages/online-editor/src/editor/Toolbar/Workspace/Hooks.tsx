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

import React, { useCallback, useEffect } from "react";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useNavigationBlockersBypass, useNavigationStatus, useNavigationStatusToggle } from "../../../navigation/Hooks";
import { useGlobalAlert } from "../../../alerts";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import {
  WorkspaceKind,
  WorkspaceKindGitBased,
} from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { useOnlineI18n } from "../../../i18n";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { PushToGitAlertActionLinks } from "../GitIntegration/PushToGitAlertActionLinks";
import { useGitIntegration } from "../GitIntegration/GitIntegrationContextProvider";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { GIT_ORIGIN_REMOTE_NAME } from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import { useHistory } from "react-router";
import { useEditorToolbarContext } from "../EditorToolbarContextProvider";

export function useWorkspaceNavigationBlocker(workspace: ActiveWorkspace) {
  const { i18n } = useOnlineI18n();
  const history = useHistory();

  const {
    auth: { changeGitAuthSessionId, authSessionSelectFilter },
    git: { canPushToGitRepository, pushToGitRepository },
    gistOrSnippet: { canUpdateGistOrSnippet, updateGistOrSnippet },
  } = useGitIntegration();

  const { downloadWorkspaceZip } = useEditorToolbarContext();

  const navigationStatus = useNavigationStatus();
  const navigationStatusToggle = useNavigationStatusToggle();
  const navigationBlockersBypass = useNavigationBlockersBypass();

  const confirmNavigationAlert = useGlobalAlert<{
    lastBlockedLocation: Location;
  }>(
    useCallback(
      (_, { lastBlockedLocation }) => (
        <Alert
          data-testid="unsaved-alert"
          variant="warning"
          title={
            workspace.descriptor.origin.kind === WorkspaceKind.LOCAL
              ? i18n.editorPage.alerts.unsaved.titleLocal
              : i18n.editorPage.alerts.unsaved.titleGit
          }
          actionClose={
            <AlertActionCloseButton data-testid="unsaved-alert-close-button" onClose={navigationStatusToggle.unblock} />
          }
          actionLinks={
            <>
              <Divider inset={{ default: "insetMd" }} />
              <br />
              {(workspace.descriptor.origin.kind === WorkspaceKind.LOCAL && (
                <AlertActionLink
                  data-testid="unsaved-alert-save-button"
                  onClick={() => {
                    navigationStatusToggle.unblock();
                    return downloadWorkspaceZip();
                  }}
                  style={{ fontWeight: "bold" }}
                >
                  {`${i18n.terms.download} '${workspace.descriptor.name}'`}
                </AlertActionLink>
              )) || (
                <PushToGitAlertActionLinks
                  changeGitAuthSessionId={changeGitAuthSessionId}
                  workspaceDescriptor={workspace.descriptor}
                  canPush={switchExpression(workspace.descriptor.origin.kind as WorkspaceKindGitBased, {
                    BITBUCKET_SNIPPET: canUpdateGistOrSnippet,
                    GITHUB_GIST: canUpdateGistOrSnippet,
                    GIT: canPushToGitRepository,
                  })}
                  remoteRef={`${GIT_ORIGIN_REMOTE_NAME}/${workspace.descriptor.origin.branch}`}
                  authSessionSelectFilter={authSessionSelectFilter}
                  onPush={() => {
                    navigationStatusToggle.unblock();
                    switchExpression(workspace.descriptor.origin.kind as WorkspaceKindGitBased, {
                      BITBUCKET_SNIPPET: updateGistOrSnippet,
                      GITHUB_GIST: updateGistOrSnippet,
                      GIT: pushToGitRepository,
                    })();
                  }}
                />
              )}
              <br />
              <br />
              <AlertActionLink
                data-testid="unsaved-alert-close-without-save-button"
                onClick={() =>
                  navigationBlockersBypass.execute(() => {
                    history.push(lastBlockedLocation);
                  })
                }
              >
                {i18n.editorPage.alerts.unsaved.proceedAnyway}
              </AlertActionLink>
              <br />
              <br />
            </>
          }
        >
          <br />
          <p>{i18n.editorPage.alerts.unsaved.message}</p>
        </Alert>
      ),
      [
        workspace.descriptor,
        i18n.editorPage.alerts.unsaved.titleLocal,
        i18n.editorPage.alerts.unsaved.titleGit,
        i18n.editorPage.alerts.unsaved.proceedAnyway,
        i18n.editorPage.alerts.unsaved.message,
        i18n.terms.download,
        navigationStatusToggle,
        changeGitAuthSessionId,
        canUpdateGistOrSnippet,
        canPushToGitRepository,
        authSessionSelectFilter,
        downloadWorkspaceZip,
        updateGistOrSnippet,
        pushToGitRepository,
        navigationBlockersBypass,
        history,
      ]
    )
  );

  useEffect(() => {
    if (navigationStatus.lastBlockedLocation) {
      confirmNavigationAlert.show({ lastBlockedLocation: navigationStatus.lastBlockedLocation });
    } else {
      confirmNavigationAlert.close();
    }

    return () => {
      confirmNavigationAlert.close();
    };
  }, [confirmNavigationAlert, navigationStatus]);

  return;
}
