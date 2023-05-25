/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useCallback } from "react";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { DashboardSingleModelDeployment } from "../deploy/strategies/DashboardSingleModelDeployment";
import { DashboardWorkspaceDeployment } from "../deploy/strategies/DashboardWorkspaceDeployment";
import { KogitoProjectDeployment } from "../deploy/strategies/KogitoProjectDeployment";
import { KogitoSwfModelDeployment } from "../deploy/strategies/KogitoSwfModelDeployment";
import { DeploymentStrategyKind, InitDeployArgs } from "../deploy/types";
import { RESOURCE_PREFIX } from "../OpenShiftConstants";

export function useDeploymentStrategy() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const workspaces = useWorkspaces();

  const createDeploymentStrategy = useCallback(
    async (args: InitDeployArgs) => {
      const resourceName = settingsDispatch.openshift.service.newResourceName(RESOURCE_PREFIX);
      const workspace = await workspaces.getWorkspace({ workspaceId: args.targetFile.workspaceId });

      if (args.factoryArgs.kind === DeploymentStrategyKind.KOGITO_SWF_MODEL) {
        return new KogitoSwfModelDeployment({
          resourceName,
          workspace,
          namespace: settings.openshift.config.namespace,
          targetFile: args.targetFile,
          getFiles: workspaces.getFiles,
          openShiftService: settingsDispatch.openshift.service,
        });
      }

      if (args.factoryArgs.kind === DeploymentStrategyKind.KOGITO_PROJECT) {
        return new KogitoProjectDeployment({
          resourceName,
          workspace,
          namespace: settings.openshift.config.namespace,
          openShiftConnection: settings.openshift.config,
          targetFile: args.targetFile,
          getFiles: workspaces.getFiles,
          openShiftService: settingsDispatch.openshift.service,
        });
      }

      if (args.factoryArgs.kind === DeploymentStrategyKind.DASHBOARD_SINGLE_MODEL) {
        return new DashboardSingleModelDeployment({
          resourceName,
          workspace,
          namespace: settings.openshift.config.namespace,
          targetFile: args.targetFile,
          getFiles: workspaces.getFiles,
          openShiftService: settingsDispatch.openshift.service,
        });
      }

      if (args.factoryArgs.kind === DeploymentStrategyKind.DASHBOARD_WORKSPACE) {
        return new DashboardWorkspaceDeployment({
          resourceName,
          workspace,
          namespace: settings.openshift.config.namespace,
          targetFile: args.targetFile,
          getFiles: workspaces.getFiles,
          openShiftService: settingsDispatch.openshift.service,
        });
      }

      throw new Error("Unknown deployment strategy");
    },
    [settings.openshift.config, settingsDispatch.openshift.service, workspaces]
  );

  return { createDeploymentStrategy };
}
