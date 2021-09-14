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
import { useCallback, useEffect, useMemo, useState } from "react";
import { useGlobals } from "../../common/GlobalContext";
import { useOnlineI18n } from "../../common/i18n";
import { useKieToolingExtendedServices } from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesModal } from "../KieToolingExtendedServices/KieToolingExtendedServicesModal";
import { KieToolingExtendedServicesStatus } from "../KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { DeployedModel } from "./DeployedModel";
import {
  DmnDevSandboxConnectionConfig,
  EMPTY_CONFIG,
  isConfigValid,
  readConfigCookie,
  resetConfigCookie,
  saveConfigCookie,
} from "./DmnDevSandboxConnectionConfig";
import { DmnDevSandboxContext } from "./DmnDevSandboxContext";
import { DmnDevSandboxInstanceStatus } from "./DmnDevSandboxInstanceStatus";
import { DmnDevSandboxModalConfig } from "./DmnDevSandboxModalConfig";
import { DmnDevSandboxModalConfirmDeploy } from "./DmnDevSandboxModalConfirmDeploy";
import { DmnDevSandboxService } from "./DmnDevSandboxService";
import { DmnDevSandboxWizardConfig } from "./DmnDevSandboxWizardConfig";

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

export function DmnDevSandboxContextProvider(props: Props) {
  const KOGITO_ONLINE_EDITOR = "online-editor";
  const LOAD_DEPLOYMENTS_POLLING_TIME = 2500;

  const { i18n } = useOnlineI18n();
  const globals = useGlobals();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const [instanceStatus, setInstanceStatus] = useState(
    kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.UNAVAILABLE
      ? DmnDevSandboxInstanceStatus.UNAVAILABLE
      : DmnDevSandboxInstanceStatus.DISCONNECTED
  );
  const service = useMemo(
    () => new DmnDevSandboxService(KOGITO_ONLINE_EDITOR, `${kieToolingExtendedServices.baseUrl}/devsandbox`),
    [kieToolingExtendedServices.baseUrl]
  );
  const [currentConfig, setCurrentConfig] = useState(readConfigCookie());
  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [isConfigModalOpen, setConfigModalOpen] = useState(false);
  const [isConfigWizardOpen, setConfigWizardOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);
  const [deployments, setDeployments] = useState([] as DeployedModel[]);
  const [openAlert, setOpenAlert] = useState(AlertTypes.NONE);

  const closeAlert = useCallback(() => setOpenAlert(AlertTypes.NONE), []);

  const onDisconnect = useCallback((closeModals: boolean) => {
    setInstanceStatus(DmnDevSandboxInstanceStatus.DISCONNECTED);
    setDropdownOpen(false);
    setDeployments([]);

    if (closeModals) {
      setConfigModalOpen(false);
      setConfigWizardOpen(false);
      setConfirmDeployModalOpen(false);
    }
  }, []);

  const onCheckConfig = useCallback(
    async (config: DmnDevSandboxConnectionConfig, persist: boolean) => {
      const isConfigOk = isConfigValid(config) && (await service.isConnectionEstablished(config));

      if (persist && isConfigOk) {
        setCurrentConfig(config);
        saveConfigCookie(config);
        setInstanceStatus(DmnDevSandboxInstanceStatus.CONNECTED);
      }

      return isConfigOk;
    },
    [service]
  );

  const onResetConfig = useCallback(() => {
    setCurrentConfig(EMPTY_CONFIG);
    onDisconnect(false);
    resetConfigCookie();
  }, [onDisconnect]);

  const onDeploy = useCallback(
    async (config: DmnDevSandboxConnectionConfig) => {
      if (!(await onCheckConfig(config, false))) {
        setOpenAlert(AlertTypes.DEPLOY_STARTED_ERROR);
        return;
      }

      const filename = `${globals.file.fileName}.${globals.file.fileExtension}`;
      const editorContent = ((await props.editor?.getContent()) ?? "")
        .replace(/(\r\n|\n|\r)/gm, "") // Remove line breaks
        .replace(/"/g, '\\"'); // Escape quotes

      try {
        await service.deploy(filename, editorContent, config);
        setOpenAlert(AlertTypes.DEPLOY_STARTED_SUCCESS);
      } catch (error) {
        setOpenAlert(AlertTypes.DEPLOY_STARTED_ERROR);
      }
    },
    [onCheckConfig, globals.file.fileName, globals.file.fileExtension, props.editor, service]
  );

  useEffect(() => {
    if (kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.RUNNING) {
      onDisconnect(true);
      return;
    }

    if (!isConfigValid(currentConfig)) {
      if (deployments.length > 0) {
        setDeployments([]);
      }
      return;
    }

    if (instanceStatus === DmnDevSandboxInstanceStatus.DISCONNECTED) {
      service
        .isConnectionEstablished(currentConfig)
        .then((isConfigOk: boolean) => {
          setConfigModalOpen(!isConfigOk && !kieToolingExtendedServices.isModalOpen);
          setInstanceStatus(isConfigOk ? DmnDevSandboxInstanceStatus.CONNECTED : DmnDevSandboxInstanceStatus.EXPIRED);
          return isConfigOk ? service.loadDeployments(currentConfig) : [];
        })
        .then((deployments: DeployedModel[]) => setDeployments(deployments))
        .catch((error: any) => console.error(error));
      return;
    }

    if (instanceStatus === DmnDevSandboxInstanceStatus.CONNECTED) {
      const loadDeploymentsTask = setInterval(() => {
        service
          .loadDeployments(currentConfig)
          .then((deployments: DeployedModel[]) => setDeployments(deployments))
          .catch((error: any) => {
            setDeployments([]);
            clearInterval(loadDeploymentsTask);
            console.error(error);
          });
      }, LOAD_DEPLOYMENTS_POLLING_TIME);
      return () => clearInterval(loadDeploymentsTask);
    }
  }, [
    onDisconnect,
    currentConfig,
    instanceStatus,
    kieToolingExtendedServices.isModalOpen,
    kieToolingExtendedServices.status,
    service,
    deployments.length,
  ]);

  return (
    <DmnDevSandboxContext.Provider
      value={{
        deployments,
        currentConfig,
        instanceStatus,
        isDropdownOpen,
        isConfigModalOpen,
        isConfigWizardOpen,
        isConfirmDeployModalOpen,
        setDeployments,
        setInstanceStatus,
        setDropdownOpen,
        setConfigModalOpen,
        setConfigWizardOpen,
        setConfirmDeployModalOpen,
        onDeploy,
        onCheckConfig,
        onResetConfig,
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
      <DmnDevSandboxWizardConfig />
      <DmnDevSandboxModalConfig />
      <DmnDevSandboxModalConfirmDeploy />
      <KieToolingExtendedServicesModal />
    </DmnDevSandboxContext.Provider>
  );
}
