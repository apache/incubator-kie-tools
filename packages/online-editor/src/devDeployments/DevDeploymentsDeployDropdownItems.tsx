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

import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useDevDeployments as useDevDeployments } from "./DevDeploymentsContext";
import { FeatureDependentOnExtendedServices } from "../extendedServices/FeatureDependentOnExtendedServices";
import { DependentFeature, useExtendedServices } from "../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../extendedServices/ExtendedServicesStatus";
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
  const extendedServices = useExtendedServices();
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

  const isExtendedServicesRunning = useMemo(
    () => extendedServices.status === ExtendedServicesStatus.RUNNING,
    [extendedServices.status]
  );

  const onDeploy = useCallback(() => {
    if (isExtendedServicesRunning && authSessionId) {
      devDeployments.setConfirmDeployModalState({ isOpen: true, cloudAuthSessionId: authSessionId });
      return;
    }
    extendedServices.setInstallTriggeredBy(DependentFeature.DEV_DEPLOYMENTS);
    extendedServices.setModalOpen(true);
  }, [isExtendedServicesRunning, authSessionId, extendedServices, devDeployments]);

  return useMemo(() => {
    return [
      <React.Fragment key={"dmn-dev-deployment-dropdown-items"}>
        {workspace && (
          <FeatureDependentOnExtendedServices isLight={false} position="left">
            <div style={{ padding: "8px 16px" }}>
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
              isDisabled={!isExtendedServicesRunning || !authSessionId}
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
          </FeatureDependentOnExtendedServices>
        )}
      </React.Fragment>,
    ];
  }, [accountsDispatch, authSessionId, devDeployments, isExtendedServicesRunning, onDeploy, workspace]);
}
