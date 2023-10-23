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

import React, { useCallback, useState, useMemo } from "react";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { useOnlineI18n } from "../i18n";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useDevDeployments } from "./DevDeploymentsContext";
import { useGlobalAlert } from "../alerts";
import { useAuthSession } from "../authSessions/AuthSessionsContext";
import { createOpenShiftDeploymentYamls } from "./services/resources/openshift";
import { createKubernetesDeploymentYamls } from "./services/resources/kubernetes";
import { CloudAuthSessionType, isCloudAuthSession } from "../authSessions/AuthSessionApi";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";

interface Props {
  workspaceFile: WorkspaceFile;
}

export function DevDeploymentsConfirmDeployModal(props: Props) {
  const devDeployments = useDevDeployments();
  const { i18n } = useOnlineI18n();
  const [isConfirmLoading, setConfirmLoading] = useState(false);
  const { authSession } = useAuthSession(
    devDeployments.confirmDeployModalState.isOpen
      ? devDeployments.confirmDeployModalState.cloudAuthSessionId
      : undefined
  );
  const availableDeploymentOptions = useMemo(
    () =>
      authSession?.type === CloudAuthSessionType.OpenShift
        ? createOpenShiftDeploymentYamls
        : createKubernetesDeploymentYamls,
    [authSession?.type]
  );
  const [deploymentOptionName, setDeploymentOptionName] = useState<string>(availableDeploymentOptions[0].name);
  const [isDeploymentOptionsDropdownOpen, setDeploymentOptionsDropdownOpen] = useState(false);

  const deployStartedErrorAlert = useGlobalAlert(
    useCallback(
      ({ close }) => (
        <Alert
          variant="danger"
          title={i18n.devDeployments.alerts.deployStartedError}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    ),
    { durationInSeconds: 5 }
  );

  const deployStartedSuccessAlert = useGlobalAlert(
    useCallback(
      ({ close }) => (
        <Alert
          className={"kogito--alert"}
          variant="info"
          title={i18n.devDeployments.alerts.deployStartedSuccess}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    ),
    { durationInSeconds: 5 }
  );

  const onConfirm = useCallback(async () => {
    if (
      !deploymentOptionName ||
      isConfirmLoading ||
      (authSession?.type !== CloudAuthSessionType.OpenShift && authSession?.type !== CloudAuthSessionType.Kubernetes)
    ) {
      return;
    }

    const selectedDeploymentOptionContent = availableDeploymentOptions.find(
      (option) => option.name === deploymentOptionName
    )!.content;

    setConfirmLoading(true);
    const deployStarted = await devDeployments.deploy(
      props.workspaceFile,
      authSession,
      selectedDeploymentOptionContent
    );
    setConfirmLoading(false);

    devDeployments.setConfirmDeployModalState({ isOpen: false });

    if (deployStarted) {
      devDeployments.setDeploymentsDropdownOpen(true);
      deployStartedSuccessAlert.show();
    } else {
      deployStartedErrorAlert.show();
    }
  }, [
    deploymentOptionName,
    isConfirmLoading,
    authSession,
    availableDeploymentOptions,
    devDeployments,
    props.workspaceFile,
    deployStartedSuccessAlert,
    deployStartedErrorAlert,
  ]);

  const onCancel = useCallback(() => {
    devDeployments.setConfirmDeployModalState({ isOpen: false });
    setConfirmLoading(false);
  }, [devDeployments]);

  const onSelectDeploymentOptions = useCallback((e, value) => {
    setDeploymentOptionName(value);
    setDeploymentOptionsDropdownOpen(false);
  }, []);

  return (
    <Modal
      data-testid={"confirm-deploy-modal"}
      variant={ModalVariant.small}
      title={i18n.devDeployments.deployConfirmModal.title}
      isOpen={devDeployments.confirmDeployModalState.isOpen}
      aria-label={"Confirm deploy modal"}
      onClose={onCancel}
      actions={[
        <Button
          id="dmn-dev-deployment-confirm-deploy-button"
          key="confirm"
          variant="primary"
          onClick={onConfirm}
          isLoading={isConfirmLoading}
          spinnerAriaValueText={isConfirmLoading ? "Loading" : undefined}
        >
          {isConfirmLoading ? i18n.devDeployments.common.deploying : i18n.terms.confirm}
        </Button>,
        <Button key="cancel" variant="link" onClick={onCancel}>
          {i18n.terms.cancel}
        </Button>,
      ]}
    >
      {i18n.devDeployments.deployConfirmModal.body}
      <br />
      <br />
      {availableDeploymentOptions && (
        <>
          <FormGroup label={<b>Choose your deployment option:</b>}>
            <Select
              variant={SelectVariant.single}
              menuAppendTo={"parent"}
              onToggle={setDeploymentOptionsDropdownOpen}
              isOpen={isDeploymentOptionsDropdownOpen}
              onSelect={onSelectDeploymentOptions}
              selections={deploymentOptionName}
            >
              {availableDeploymentOptions.map((option) => (
                <SelectOption key={option.name} value={option.name}>
                  {option.name}
                </SelectOption>
              ))}
            </Select>
          </FormGroup>
        </>
      )}
      <br />
      <br />
      {authSession && isCloudAuthSession(authSession) && (
        <>
          {`This Dev deployment will be created at`}
          &nbsp;
          <b>{`'${authSession.namespace}'`}</b>.
        </>
      )}
    </Modal>
  );
}
