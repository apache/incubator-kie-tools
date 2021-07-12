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

import { EmbeddedEditorRef } from "@kogito-tooling/editor/dist/embedded";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { GlobalContext } from "../../common/GlobalContext";
import { useOnlineI18n } from "../../common/i18n";
import { DmnRunnerModal } from "../DmnRunner/DmnRunnerModal";
import { useKieToolingExtendedServices } from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
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
  const LOAD_DEPLOYMENTS_POLLING_TIME = 1000;

  const { i18n } = useOnlineI18n();
  const globalContext = useContext(GlobalContext);
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
  const [isConfigModalOpen, setConfigModalOpen] = useState(false);
  const [isConfigWizardOpen, setConfigWizardOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);
  const [deployments, setDeployments] = useState([] as DeployedModel[]);
  const [openAlert, setOpenAlert] = useState(AlertTypes.NONE);

  const closeAlert = useCallback(() => setOpenAlert(AlertTypes.NONE), []);

  const disconnect = useCallback(() => {
    setDeployments([]);
    setInstanceStatus(DmnDevSandboxInstanceStatus.DISCONNECTED);
  }, []);

  const onCheckConfig = useCallback(
    async (config: DmnDevSandboxConnectionConfig, persist: boolean) => {
      const isConfigOk = isConfigValid(config) && (await service.isConnectionEstablished(config));
      if (persist) {
        if (isConfigOk) {
          setCurrentConfig(config);
          saveConfigCookie(config);
        }
        setInstanceStatus(
          isConfigOk ? DmnDevSandboxInstanceStatus.CONNECTED : DmnDevSandboxInstanceStatus.DISCONNECTED
        );
      }

      return isConfigOk;
    },
    [service]
  );

  const onDeploy = useCallback(
    async (config: DmnDevSandboxConnectionConfig) => {
      const isConfigOk = await onCheckConfig(config, false);

      if (!isConfigOk) {
        setOpenAlert(AlertTypes.DEPLOY_STARTED_ERROR);
        return;
      }

      const filename = `${globalContext.file.fileName}.${globalContext.file.fileExtension}`;
      const editorContent = ((await props.editor?.getContent()) ?? "")
        .replace(/(\r\n|\n|\r)/gm, "")
        .replace(/"/g, '\\"');

      try {
        await service.deploy(filename, editorContent, config);
        setOpenAlert(AlertTypes.DEPLOY_STARTED_SUCCESS);
      } catch (e) {
        setOpenAlert(AlertTypes.DEPLOY_STARTED_ERROR);
      }
    },
    [onCheckConfig, globalContext.file.fileName, globalContext.file.fileExtension, props.editor, service]
  );

  const onResetConfig = useCallback(() => {
    disconnect();
    resetConfigCookie();
    setCurrentConfig(EMPTY_CONFIG);
  }, [disconnect]);

  useEffect(() => {
    if (
      kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.UNAVAILABLE ||
      !isConfigValid(currentConfig)
    ) {
      disconnect();
      return;
    }

    if (instanceStatus === DmnDevSandboxInstanceStatus.DISCONNECTED) {
      service
        .isConnectionEstablished(currentConfig)
        .then((isConfigOk) => {
          if (!isConfigOk) {
            disconnect();
            return;
          }
          setInstanceStatus(DmnDevSandboxInstanceStatus.CONNECTED);
        })
        .catch(() => disconnect());
    }

    if (instanceStatus === DmnDevSandboxInstanceStatus.CONNECTED) {
      const loadDeploymentsTask = window.setInterval(() => {
        service
          .loadDeployments(currentConfig)
          .then((deployments: DeployedModel[]) => {
            setDeployments(deployments);
          })
          .catch(() => {
            window.clearInterval(loadDeploymentsTask);
            disconnect();
          });
      }, LOAD_DEPLOYMENTS_POLLING_TIME);
      return () => window.clearInterval(loadDeploymentsTask);
    }
  }, [service, instanceStatus, currentConfig, deployments.length, disconnect, kieToolingExtendedServices.status]);

  return (
    <DmnDevSandboxContext.Provider
      value={{
        deployments,
        currentConfig,
        instanceStatus,
        isConfigModalOpen,
        isConfigWizardOpen,
        isConfirmDeployModalOpen,
        setDeployments,
        setInstanceStatus,
        setConfigModalOpen,
        setConfigWizardOpen,
        setConfirmDeployModalOpen,
        onDeploy,
        onCheckConfig,
        onResetConfig,
      }}
    >
      {openAlert === AlertTypes.DEPLOY_STARTED_ERROR && (
        <div className={"kogito--alert-container"}>
          <Alert
            className={"kogito--alert"}
            variant="danger"
            title={i18n.dmnDevSandbox.alerts.deployStartedError}
            actionClose={<AlertActionCloseButton onClose={closeAlert} />}
          />
        </div>
      )}
      {openAlert === AlertTypes.DEPLOY_STARTED_SUCCESS && (
        <div className={"kogito--alert-container"}>
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
      <DmnRunnerModal />
    </DmnDevSandboxContext.Provider>
  );
}
