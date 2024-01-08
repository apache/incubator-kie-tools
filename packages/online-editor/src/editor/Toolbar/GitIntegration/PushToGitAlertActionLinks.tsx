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

import React, { useMemo } from "react";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { AuthSessionSelect, AuthSessionSelectFilter } from "../../../authSessions/AuthSessionSelect";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../../../accounts/AccountsContext";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { Alert, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { SelectPosition } from "@patternfly/react-core/dist/js/components/Select";
import { AuthProviderGroup } from "../../../authProviders/AuthProvidersApi";

export function PushToGitAlertActionLinks(props: {
  onPush: () => void;
  canPush?: boolean;
  remoteRef?: string;
  workspaceDescriptor: WorkspaceDescriptor | undefined;
  changeGitAuthSessionId: (a: React.SetStateAction<string | undefined>, b: string | undefined) => void;
  authSessionSelectFilter: AuthSessionSelectFilter;
}) {
  const accountsDispatch = useAccountsDispatch();
  if (props.workspaceDescriptor?.origin.kind === WorkspaceKind.GIT && !props.remoteRef) {
    throw new Error("Should specify remoteRef for GIT workspaces");
  }

  const pushButton = useMemo(
    () => (
      <AlertActionLink onClick={props.onPush} style={{ fontWeight: "bold" }} isDisabled={!props.canPush}>
        {props.workspaceDescriptor
          ? switchExpression(props.workspaceDescriptor.origin.kind, {
              GIT: `Push to '${props.remoteRef}'`,
              GITHUB_GIST: "Update Gist",
              BITBUCKET_SNIPPET: "Update Snippet",
              default: "",
            })
          : ""}
      </AlertActionLink>
    ),
    [props]
  );

  return (
    <>
      {!props.canPush && (
        <Alert
          isInline={true}
          variant={"default"}
          title={"Can't Push without selecting an authentication source"}
          actionLinks={
            <>
              <AuthSessionSelect
                title={`Select Git authentication for '${props.workspaceDescriptor?.name}'...`}
                position={SelectPosition.right}
                isPlain={false}
                authSessionId={props.workspaceDescriptor?.gitAuthSessionId}
                showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.GIT}
                setAuthSessionId={(newAuthSessionId) => {
                  props.changeGitAuthSessionId(newAuthSessionId, props.workspaceDescriptor?.gitAuthSessionId);
                  accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                }}
                filter={props.authSessionSelectFilter}
              />
              <br />
              <br />
              {pushButton}
            </>
          }
        >
          {`Select an authentication source for '${props.workspaceDescriptor?.name}' to be able to Push.`}
          <br />
        </Alert>
      )}
      {props.canPush && pushButton}
    </>
  );
}
