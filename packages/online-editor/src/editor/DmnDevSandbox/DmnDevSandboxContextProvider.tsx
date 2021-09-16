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

import { EmbeddedEditorRef } from "@kie-tooling-core/editor/dist/embedded";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import { useGlobals } from "../../common/GlobalContext";
import { useOnlineI18n } from "../../common/i18n";
import { useKieToolingExtendedServices } from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesModal } from "../KieToolingExtendedServices/KieToolingExtendedServicesModal";
import { KieToolingExtendedServicesStatus } from "../KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { OpenShiftDeployedModel } from "../../settings/OpenShiftDeployedModel";
import { DmnDevSandboxContext } from "./DmnDevSandboxContext";
import { OpenShiftInstanceStatus } from "../../settings/OpenShiftInstanceStatus";
import { DmnDevSandboxModalConfirmDeploy } from "./DmnDevSandboxModalConfirmDeploy";
import { useSettings } from "../../settings/SettingsContext";
import { isConfigValid, OpenShiftSettingsConfig } from "../../settings/OpenShiftSettingsConfig";

interface Props {
  children: React.ReactNode;
  editor?: EmbeddedEditorRef;
  isEditorReady: boolean;
}

enum AlertTypes {
  NONE,
  DEPLOY_STARTED_ERROR,
  DEPLOY_STARTED_SUCCESS,
}

const LOAD_DEPLOYMENTS_POLLING_TIME = 2500;

export function DmnDevSandboxContextProvider(props: Props) {
  const settings = useSettings();
  const { i18n } = useOnlineI18n();
  const globals = useGlobals();
  const kieToolingExtendedServices = useKieToolingExtendedServices();

  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);
  const [deployments, setDeployments] = useState([] as OpenShiftDeployedModel[]);
  const [openAlert, setOpenAlert] = useState(AlertTypes.NONE);

  const closeAlert = useCallback(() => setOpenAlert(AlertTypes.NONE), []);

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
      if (!((await isConfigValid(config)) && (await settings.openshift.service.isConnectionEstablished(config)))) {
        setOpenAlert(AlertTypes.DEPLOY_STARTED_ERROR);
        return;
      }

      const filename = `${globals.file.fileName}.${globals.file.fileExtension}`;
      const editorContent = ((await props.editor?.getContent()) ?? "")
        .replace(/(\r\n|\n|\r)/gm, "") // Remove line breaks
        .replace(/"/g, '\\"'); // Escape quotes

      try {
        await settings.openshift.service.deploy(filename, editorContent, config);
        setOpenAlert(AlertTypes.DEPLOY_STARTED_SUCCESS);
      } catch (error) {
        setOpenAlert(AlertTypes.DEPLOY_STARTED_ERROR);
      }
    },
    [globals.file.fileName, globals.file.fileExtension, props.editor, settings.openshift.service]
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
    kieToolingExtendedServices.isModalOpen,
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
      {openAlert === AlertTypes.DEPLOY_STARTED_ERROR && (
        <div className={"kogito--alert-container kogito--editor__dmn-dev-sandbox-alert-container"}>
          <Alert
            className={"kogito--alert"}
            variant="danger"
            title={i18n.dmnDevSandbox.alerts.deployStartedError}
            actionClose={<AlertActionCloseButton onClose={closeAlert} />}
          />
        </div>
      )}
      {openAlert === AlertTypes.DEPLOY_STARTED_SUCCESS && (
        <div className={"kogito--alert-container kogito--editor__dmn-dev-sandbox-alert-container"}>
          <Alert
            className={"kogito--alert"}
            variant="info"
            title={i18n.dmnDevSandbox.alerts.deployStartedSuccess}
            actionClose={<AlertActionCloseButton onClose={closeAlert} />}
          />
        </div>
      )}
      {props.children}
      <DmnDevSandboxModalConfirmDeploy />
      <KieToolingExtendedServicesModal />
    </DmnDevSandboxContext.Provider>
  );
}
