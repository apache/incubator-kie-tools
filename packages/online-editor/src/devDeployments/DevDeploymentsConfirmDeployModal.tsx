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

import React, { useCallback, useState, useMemo, useEffect } from "react";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { useOnlineI18n } from "../i18n";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useDevDeployments } from "./DevDeploymentsContext";
import { useGlobalAlert } from "../alerts";
import { useAuthSession } from "../authSessions/AuthSessionsContext";
import { CloudAuthSessionType, isCloudAuthSession } from "../authSessions/AuthSessionApi";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/deprecated";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { DeploymentOption, DeploymentParameter } from "./services/deploymentOptions/types";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useEnv } from "../env/hooks/EnvContext";
import { DeploymentOptionArgs } from "./services/types";
import { KubernetesDeploymentOptions } from "./services/kubernetes/KubernetesDeploymentOptions";
import { OpenShiftDeploymentOptions } from "./services/openshift/OpenShiftDeploymentOptions";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { DevDeploymentsTokensList } from "./DevDeploymentsTokensList";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

interface Props {
  workspaceFile: WorkspaceFile;
}

export function DevDeploymentsConfirmDeployModal(props: Props) {
  const devDeployments = useDevDeployments();
  const { env } = useEnv();
  const { i18n } = useOnlineI18n();
  const [isConfirmLoading, setConfirmLoading] = useState(false);

  const { authSession } = useAuthSession(
    devDeployments.confirmDeployModalState.isOpen
      ? devDeployments.confirmDeployModalState.cloudAuthSessionId
      : undefined
  );

  const deploymentOptionsArgs: DeploymentOptionArgs = useMemo(
    () => ({
      kogitoQuarkusBlankAppImageUrl: env.KIE_SANDBOX_DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE_URL,
      baseImageUrl: env.KIE_SANDBOX_DEV_DEPLOYMENT_BASE_IMAGE_URL,
      dmnFormWebappImageUrl: env.KIE_SANDBOX_DEV_DEPLOYMENT_DMN_FORM_WEBAPP_IMAGE_URL,
      imagePullPolicy: env.KIE_SANDBOX_DEV_DEPLOYMENT_IMAGE_PULL_POLICY,
    }),
    [env]
  );

  const availableDeploymentOptions = useMemo(
    () =>
      authSession?.type === CloudAuthSessionType.OpenShift
        ? OpenShiftDeploymentOptions(deploymentOptionsArgs)
        : KubernetesDeploymentOptions(deploymentOptionsArgs),
    [authSession, deploymentOptionsArgs]
  );

  const [deploymentOption, setDeploymentOption] = useState<DeploymentOption>(availableDeploymentOptions[0]);
  const [deploymentParameters, setDeploymentParameters] = useState<Record<string, string | number | boolean>>({});
  const [isDeploymentOptionsDropdownOpen, setDeploymentOptionsDropdownOpen] = useState(false);

  useEffect(() => {
    setDeploymentOption(availableDeploymentOptions[0]);
  }, [availableDeploymentOptions]);

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
      !deploymentOption ||
      isConfirmLoading ||
      (authSession?.type !== CloudAuthSessionType.OpenShift && authSession?.type !== CloudAuthSessionType.Kubernetes)
    ) {
      return;
    }

    setConfirmLoading(true);
    const deployStarted = await devDeployments.deploy(
      props.workspaceFile,
      authSession,
      deploymentOption,
      deploymentParameters
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
    deploymentParameters,
    deploymentOption,
    isConfirmLoading,
    authSession,
    devDeployments,
    props.workspaceFile,
    deployStartedSuccessAlert,
    deployStartedErrorAlert,
  ]);

  const onCancel = useCallback(() => {
    devDeployments.setConfirmDeployModalState({ isOpen: false });
    setConfirmLoading(false);
  }, [devDeployments]);

  const onSelectDeploymentOptions = useCallback((_, value: DeploymentOption) => {
    setDeploymentOption(value);
    setDeploymentOptionsDropdownOpen(false);
  }, []);

  useEffect(() => {
    setDeploymentParameters(
      Object.values(deploymentOption.parameters ?? {}).reduce(
        (parametersValues, parameter) => ({
          ...parametersValues,
          [parameter.id]: parameter.defaultValue,
        }),
        {}
      ) ?? {}
    );
  }, [deploymentOption.parameters]);

  const updateParameters = useCallback((parameter: DeploymentParameter, value: string | number | boolean) => {
    setDeploymentParameters((currentParameters) => ({
      ...currentParameters,
      [parameter.id]: value,
    }));
  }, []);

  const parametersInputs = useMemo(() => {
    return (
      (deploymentOption &&
        deploymentOption.parameters &&
        Object.values(deploymentOption.parameters).map((parameter) => {
          if (parameter.type === "boolean") {
            return (
              <FormGroup key={parameter.id}>
                <Checkbox
                  id={parameter.id}
                  name={parameter.name}
                  label={parameter.name}
                  aria-label={parameter.name}
                  isChecked={Boolean(deploymentParameters[parameter.id])}
                  onChange={(_event, checked) => updateParameters(parameter, checked)}
                />
                <FormHelperText>
                  <HelperText>
                    <HelperTextItem>
                      <i>{parameter.description}</i>
                    </HelperTextItem>
                  </HelperText>
                </FormHelperText>
              </FormGroup>
            );
          } else if (parameter.type === "text") {
            return (
              <FormGroup label={<b>{parameter.name}:</b>} key={parameter.id}>
                <TextArea
                  id={parameter.id}
                  value={String(deploymentParameters[parameter.id])}
                  aria-label={parameter.name}
                  onChange={(_event, value) => updateParameters(parameter, value)}
                  autoResize={true}
                />
                <FormHelperText>
                  <HelperText>
                    <HelperTextItem>
                      <i>{parameter.description}</i>
                    </HelperTextItem>
                  </HelperText>
                </FormHelperText>
              </FormGroup>
            );
          } else if (parameter.type === "number") {
            return (
              <FormGroup label={<b>{parameter.name}:</b>} key={parameter.id}>
                <TextInput
                  id={parameter.id}
                  value={Number(deploymentParameters[parameter.id])}
                  aria-label={parameter.name}
                  type="number"
                  onChange={(_event, value) => updateParameters(parameter, Number(value))}
                />
                <FormHelperText>
                  <HelperText>
                    <HelperTextItem>
                      <i>{parameter.description}</i>
                    </HelperTextItem>
                  </HelperText>
                </FormHelperText>
              </FormGroup>
            );
          }
        })) ??
      []
    );
  }, [deploymentOption, deploymentParameters, updateParameters]);

  return (
    <Modal
      data-testid={"confirm-deploy-modal"}
      variant={ModalVariant.large}
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
              onToggle={(_event, val) => setDeploymentOptionsDropdownOpen(val)}
              isOpen={isDeploymentOptionsDropdownOpen}
              onSelect={onSelectDeploymentOptions}
              selections={deploymentOption.name}
            >
              {availableDeploymentOptions.map((option) => (
                <SelectOption key={option.name} value={option}>
                  {option.name}
                </SelectOption>
              ))}
            </Select>
          </FormGroup>
          {parametersInputs ? (
            <Flex>
              {parametersInputs.map((input, index) => (
                <FlexItem key={index} fullWidth={{ default: "fullWidth" }} style={{ marginTop: "1rem" }}>
                  {input}
                </FlexItem>
              ))}
            </Flex>
          ) : (
            <br />
          )}
        </>
      )}
      <br />
      {authSession && isCloudAuthSession(authSession) && (
        <>
          {`This Dev Deployment will be created at the`}
          &nbsp;
          <b>{`'${authSession.namespace}'`}</b>
          &nbsp;
          {`namespace.`}
        </>
      )}
      <br />
      {authSession && (
        <>
          <br />
          <p>
            <i>
              You can use tokens with pre-computed values for your resources and parameters. Check a list of the
              available tokens below:
            </i>
          </p>
          <DevDeploymentsTokensList workspaceFile={props.workspaceFile} authSession={authSession} />
        </>
      )}
    </Modal>
  );
}
