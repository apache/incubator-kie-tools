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
import { useDmnRunner } from "../DmnRunner/DmnRunnerContext";
import { DmnRunnerStatus } from "../DmnRunner/DmnRunnerStatus";
import { ConfigDeployModal } from "./ConfigDeployModal";
import { ConfigDeployWizard } from "./ConfigDeployWizard";
import { ConfirmDeployModal } from "./ConfirmDeployModal";
import {
  ConnectionConfig,
  EMPTY_CONFIG,
  isConfigValid,
  readConfigCookie,
  resetConfigCookie,
  saveConfigCookie,
} from "./ConnectionConfig";
import { DeployContext } from "./DeployContext";
import { DeployedModel } from "./DeployedModel";
import { DeployInstanceStatus } from "./DeployInstanceStatus";
import { DeployIntroductionModal } from "./DeployIntroductionModal";
import { DeveloperSandboxService } from "./devsandbox/DeveloperSandboxService";

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

export function DeployContextProvider(props: Props) {
  const KOGITO_ONLINE_EDITOR = "online-editor";
  const LOAD_DEPLOYMENTS_POLLING_TIME = 1000;

  const globalContext = useContext(GlobalContext);
  const dmnRunner = useDmnRunner();
  const { i18n } = useOnlineI18n();
  const [instanceStatus, setInstanceStatus] = useState(DeployInstanceStatus.UNAVAILABLE);
  const service = useMemo(
    () => new DeveloperSandboxService(KOGITO_ONLINE_EDITOR, `http://localhost:${dmnRunner.port}/devsandbox`),
    [dmnRunner.port]
  );
  const [currentConfig, setCurrentConfig] = useState(readConfigCookie());
  const [isConfigModalOpen, setConfigModalOpen] = useState(false);
  const [isConfigWizardOpen, setConfigWizardOpen] = useState(false);
  const [isDeployDropdownOpen, setDeployDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);
  const [isDeployIntroductionModalOpen, setDeployIntroductionModalOpen] = useState(false);
  const [deployments, setDeployments] = useState([] as DeployedModel[]);
  const [openAlert, setOpenAlert] = useState(AlertTypes.NONE);

  const closeAlert = useCallback(() => setOpenAlert(AlertTypes.NONE), []);

  const disconnect = useCallback(() => {
    setDeployments([]);
    setInstanceStatus(DeployInstanceStatus.DISCONNECTED);
  }, []);

  const onCheckConfig = useCallback(
    async (config: ConnectionConfig, persist: boolean) => {
      const isConfigOk = isConfigValid(config) && (await service.isConnectionEstablished(config));
      if (persist) {
        if (isConfigOk) {
          setCurrentConfig(config);
          saveConfigCookie(config);
          try {
            setDeployments(await service.loadDeployments(config));
          } catch (e) {
            disconnect();
          }
        }
        setInstanceStatus(isConfigOk ? DeployInstanceStatus.CONNECTED : DeployInstanceStatus.DISCONNECTED);
      }

      return isConfigOk;
    },
    [disconnect, service]
  );

  const onDeploy = useCallback(
    async (config: ConnectionConfig) => {
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
    setInstanceStatus(
      dmnRunner.status === DmnRunnerStatus.RUNNING
        ? DeployInstanceStatus.DISCONNECTED
        : DeployInstanceStatus.UNAVAILABLE
    );
  }, [dmnRunner.status]);

  useEffect(() => {
    if (instanceStatus === DeployInstanceStatus.UNAVAILABLE) {
      return;
    }

    if (!isConfigValid(currentConfig)) {
      disconnect();
      return;
    }

    let loadDeploymentsTask: number | undefined;
    if (instanceStatus === DeployInstanceStatus.CONNECTED) {
      loadDeploymentsTask = window.setInterval(() => {
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
    }

    if (instanceStatus === DeployInstanceStatus.DISCONNECTED) {
      service
        .isConnectionEstablished(currentConfig)
        .then((isConfigOk) => {
          if (!isConfigOk) {
            disconnect();
            return;
          }
          setInstanceStatus(DeployInstanceStatus.CONNECTED);
        })
        .catch(() => disconnect());
    }

    return () => window.clearInterval(loadDeploymentsTask);
  }, [service, instanceStatus, currentConfig, deployments.length, disconnect]);

  return (
    <DeployContext.Provider
      value={{
        deployments,
        service,
        currentConfig,
        instanceStatus,
        isConfigModalOpen,
        isConfigWizardOpen,
        isDeployDropdownOpen,
        isConfirmDeployModalOpen,
        isDeployIntroductionModalOpen,
        setDeployments,
        setInstanceStatus,
        setConfigModalOpen,
        setConfigWizardOpen,
        setDeployDropdownOpen,
        setConfirmDeployModalOpen,
        setDeployIntroductionModalOpen,
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
            title={i18n.deploy.alerts.deployStartedError}
            actionClose={<AlertActionCloseButton onClose={closeAlert} />}
          />
        </div>
      )}
      {openAlert === AlertTypes.DEPLOY_STARTED_SUCCESS && (
        <div className={"kogito--alert-container"}>
          <Alert
            className={"kogito--alert"}
            variant="info"
            title={i18n.deploy.alerts.deployStartedSuccess}
            actionClose={<AlertActionCloseButton onClose={closeAlert} />}
          />
        </div>
      )}
      {props.children}
      <ConfigDeployModal />
      <ConfigDeployWizard />
      <ConfirmDeployModal />
      <DeployIntroductionModal />
    </DeployContext.Provider>
  );
}
