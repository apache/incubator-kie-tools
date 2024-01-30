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

import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useDevDeployments as useDevDeployments } from "./DevDeploymentsContext";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { FileLabel } from "../filesList/FileLabel";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../accounts/AccountsContext";
import { AuthProviderGroup } from "../authProviders/AuthProvidersApi";
import { useAuthSessions } from "../authSessions/AuthSessionsContext";
import { AuthSessionSelect } from "../authSessions/AuthSessionSelect";
import { cloudAuthSessionSelectFilter } from "../authSessions/CompatibleAuthSessions";
import { SelectPosition } from "@patternfly/react-core/dist/js/components/Select";

export function useDevDeploymentsDeployDropdownItems(workspace: ActiveWorkspace | undefined) {
  const devDeployments = useDevDeployments();
  const accountsDispatch = useAccountsDispatch();
  const { authSessions } = useAuthSessions();

  const suggestedAuthSessionForDeployment = useMemo(() => {
    return [...authSessions.values()].find(
      (authSession) => authSession.type === "openshift" || authSession.type === "kubernetes"
    );
  }, [authSessions]);

  const [authSessionId, setAuthSessionId] = useState<string | undefined>();

  useEffect(() => {
    setAuthSessionId((currentAuthSessionId) => {
      if (suggestedAuthSessionForDeployment) {
        return suggestedAuthSessionForDeployment.id;
      } else if (currentAuthSessionId && !authSessions.has(currentAuthSessionId)) {
        return undefined;
      }
      return currentAuthSessionId;
    });
  }, [authSessions, suggestedAuthSessionForDeployment]);

  const onDeploy = useCallback(() => {
    if (authSessionId) {
      devDeployments.setConfirmDeployModalState({ isOpen: true, cloudAuthSessionId: authSessionId });
      return;
    }
  }, [authSessionId, devDeployments]);

  return useMemo(() => {
    return [
      <React.Fragment key={"dmn-dev-deployment-dropdown-items"}>
        {workspace && (
          <>
            <div style={{ padding: "0px 8px" }}>
              <AuthSessionSelect
                position={SelectPosition.right}
                authSessionId={authSessionId}
                setAuthSessionId={(newAuthSessionId) => {
                  setAuthSessionId(newAuthSessionId);
                  setTimeout(() => {
                    accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
                    devDeployments.setDeployDropdownOpen(true);
                  }, 0);
                }}
                isPlain={false}
                title={"Select Cloud provider for this Dev deployment..."}
                filter={cloudAuthSessionSelectFilter()}
                showOnlyThisAuthProviderGroupWhenConnectingToNewAccount={AuthProviderGroup.CLOUD}
              />
            </div>
            <Divider />
            <DropdownItem
              id="dmn-dev-deployment-deploy-your-model-button"
              key={`dropdown-dmn-dev-deployment-deploy`}
              component={"button"}
              onClick={onDeploy}
              isDisabled={!authSessionId}
              ouiaId={"deploy-to-dmn-dev-deployment-dropdown-button"}
              description="For development only!"
              style={{ minWidth: "400px" }}
            >
              {workspace.files.length > 1 && (
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>
                    Deploy <b>{`"${workspace.descriptor.name}"`}</b>
                  </FlexItem>
                </Flex>
              )}
              {workspace.files.length === 1 && (
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>
                    Deploy <b>{`"${workspace.files[0].nameWithoutExtension}"`}</b>
                  </FlexItem>
                  <FlexItem>
                    <b>
                      <FileLabel extension={workspace.files[0].extension} />
                    </b>
                  </FlexItem>
                </Flex>
              )}
            </DropdownItem>
          </>
        )}
      </React.Fragment>,
    ];
  }, [accountsDispatch, authSessionId, devDeployments, onDeploy, workspace]);
}
