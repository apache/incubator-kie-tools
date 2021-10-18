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

import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import { useGlobals } from "../../common/GlobalContext";
import { useOnlineI18n } from "../../common/i18n";
import { useKieToolingExtendedServices } from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "../KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { OpenShiftDeployedModel } from "../../settings/OpenShiftDeployedModel";
import { DeploymentFile, DmnDevSandboxContext } from "./DmnDevSandboxContext";
import { OpenShiftInstanceStatus } from "../../settings/OpenShiftInstanceStatus";
import { DmnDevSandboxModalConfirmDeploy } from "./DmnDevSandboxModalConfirmDeploy";
import { useSettings } from "../../settings/SettingsContext";
import { isConfigValid, OpenShiftSettingsConfig } from "../../settings/OpenShiftSettingsConfig";
import { AlertsController, useAlert } from "../Alerts/Alerts";
import { useWorkspaces, WorkspaceFile } from "../../workspace/WorkspacesContext";

interface Props {
  children: React.ReactNode;
  workspaceFile: WorkspaceFile;
  alerts: AlertsController | undefined;
}

const LOAD_DEPLOYMENTS_POLLING_TIME = 2500;

export function DmnDevSandboxContextProvider(props: Props) {
  const settings = useSettings();
  const { i18n } = useOnlineI18n();
  const globals = useGlobals();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const workspaces = useWorkspaces();

  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);
  const [deployments, setDeployments] = useState([] as OpenShiftDeployedModel[]);

  const deployStartedErrorAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => (
        <Alert
          variant="danger"
          title={i18n.dmnDevSandbox.alerts.deployStartedError}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    )
  );

  const deployStartedSuccessAlert = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => (
        <Alert
          className={"kogito--alert"}
          variant="info"
          title={i18n.dmnDevSandbox.alerts.deployStartedSuccess}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    )
  );

  const onDisconnect = useCallback(
    (closeModals: boolean) => {
      settings.openshift.status.set(OpenShiftInstanceStatus.DISCONNECTED);
      setDropdownOpen(false);
      setDeployments([]);

      if (closeModals) {
        setConfirmDeployModalOpen(false);
      }
    },
    [settings.openshift.status]
  );

  const onDeploy = useCallback(
    async (config: OpenShiftSettingsConfig) => {
      if (!(isConfigValid(config) && (await settings.openshift.service.isConnectionEstablished(config)))) {
        deployStartedErrorAlert.show();
        return;
      }

      const prepareFileContents = (getFileContents: () => Promise<string | undefined>) => async () =>
        ((await getFileContents()) ?? "")
          .replace(/(\r\n|\n|\r)/gm, "") // Remove line breaks
          .replace(/("|')/g, '\\"'); // Escape quotes

      const targetFile: DeploymentFile = {
        path: props.workspaceFile.relativePath,
        getFileContents: prepareFileContents(props.workspaceFile.getFileContentsAsString),
      };

      const workspaceFiles = (
        await workspaces.getFiles({
          fs: workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
          workspaceId: props.workspaceFile.workspaceId,
        })
      ).filter((f) => f.extension === "dmn" || f.extension === "pmml");

      const relatedFiles: DeploymentFile[] = workspaceFiles
        .filter((f) => f.relativePath !== targetFile.path)
        .map((f) => ({
          path: f.relativePath,
          getFileContents: prepareFileContents(f.getFileContentsAsString),
        }));

      try {
        await settings.openshift.service.deploy({
          targetFile: targetFile,
          relatedFiles: relatedFiles,
          config: config,
          onlineEditorUrl: (baseUrl) =>
            globals.routes.importModel.url({
              base: process.env.WEBPACK_REPLACE__dmnDevSandbox_onlineEditorUrl,
              pathParams: {},
              queryParams: { url: `${baseUrl}/${targetFile.path}` },
            }),
        });
        deployStartedSuccessAlert.show();
      } catch (error) {
        deployStartedErrorAlert.show();
      }
    },
    [
      settings.openshift.service,
      props.workspaceFile,
      workspaces,
      deployStartedSuccessAlert,
      deployStartedErrorAlert,
      globals,
    ]
  );

  useEffect(() => {
    if (kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.RUNNING) {
      onDisconnect(true);
      return;
    }

    if (!isConfigValid(settings.openshift.config.get)) {
      if (deployments.length > 0) {
        setDeployments([]);
      }
      return;
    }

    if (settings.openshift.status.get === OpenShiftInstanceStatus.DISCONNECTED) {
      settings.openshift.service
        .isConnectionEstablished(settings.openshift.config.get)
        .then((isConfigOk: boolean) => {
          settings.openshift.status.set(
            isConfigOk ? OpenShiftInstanceStatus.CONNECTED : OpenShiftInstanceStatus.EXPIRED
          );
          return isConfigOk ? settings.openshift.service.loadDeployments(settings.openshift.config.get) : [];
        })
        .then((deployments) => setDeployments(deployments))
        .catch((error) => console.error(error));
      return;
    }

    if (settings.openshift.status.get === OpenShiftInstanceStatus.CONNECTED) {
      const loadDeploymentsTask = setInterval(() => {
        settings.openshift.service
          .loadDeployments(settings.openshift.config.get)
          .then((deployments) => setDeployments(deployments))
          .catch((error) => {
            setDeployments([]);
            clearInterval(loadDeploymentsTask);
            console.error(error);
          });
      }, LOAD_DEPLOYMENTS_POLLING_TIME);
      return () => clearInterval(loadDeploymentsTask);
    }
  }, [
    onDisconnect,
    settings.openshift.config,
    settings.openshift.status,
    settings.openshift.service,
    kieToolingExtendedServices.status,
    deployments.length,
  ]);

  return (
    <DmnDevSandboxContext.Provider
      value={{
        deployments,
        isDropdownOpen,
        isConfirmDeployModalOpen,
        setDeployments,
        setDropdownOpen,
        setConfirmDeployModalOpen,
        onDeploy,
      }}
    >
      {props.children}
      <DmnDevSandboxModalConfirmDeploy />
    </DmnDevSandboxContext.Provider>
  );
}
