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
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import * as React from "react";
import { useCallback, useMemo, useState, useEffect } from "react";
import { AlertsController, useAlert } from "../../alerts/Alerts";
import { useAppI18n } from "../../i18n";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { isKafkaConfigValid } from "../../settings/kafka/KafkaSettingsConfig";
import { isServiceAccountConfigValid } from "../../settings/serviceAccount/ServiceAccountConfig";
import { isServiceRegistryConfigValid } from "../../settings/serviceRegistry/ServiceRegistryConfig";
import { useSettings } from "../../settings/SettingsContext";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { isSingleModuleProject } from "../../project";

const FETCH_OPEN_API_POLLING_TIME = 5000;

interface ConfirmDeployModalProps {
  workspace: ActiveWorkspace;
  workspaceFile: WorkspaceFile;
  alerts: AlertsController | undefined;
}

export function ConfirmDeployModal(props: ConfirmDeployModalProps) {
  const openshift = useOpenShift();
  const settings = useSettings();
  const { i18n } = useAppI18n();
  const [isConfirmLoading, setConfirmLoading] = useState(false);
  const [shouldUploadOpenApi, setShouldUploadOpenApi] = useState(false);
  const [shouldAttachKafkaSource, setShouldAttachKafkaSource] = useState(false);
  const [shouldDeployAsProject, setShouldDeployAsProject] = useState(false);

  const canUploadOpenApi = useMemo(
    () =>
      isServiceAccountConfigValid(settings.serviceAccount.config) &&
      isServiceRegistryConfigValid(settings.serviceRegistry.config),
    [settings.serviceAccount.config, settings.serviceRegistry.config]
  );

  const canAttachKafkaSource = useMemo(
    () =>
      isServiceAccountConfigValid(settings.serviceAccount.config) && isKafkaConfigValid(settings.apacheKafka.config),
    [settings.apacheKafka.config, settings.serviceAccount.config]
  );

  const canDeployAsProject = useMemo(() => isSingleModuleProject(props.workspace.files), [props.workspace.files]);

  useEffect(() => {
    setShouldDeployAsProject(canDeployAsProject);
  }, [canDeployAsProject]);

  const setDeployStartedError = useAlert(
    props.alerts,
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="danger"
          title={
            "Something went wrong while creating the deployment resources. Check your OpenShift connection and resource limits, and then try again."
          }
          aria-live="polite"
          data-testid="alert-deploy-error"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const setDeployStartedSuccess = useAlert(
    props.alerts,
    useCallback(
      ({ close }) => {
        return (
          <Alert
            className="pf-u-mb-md"
            variant="info"
            title={
              <>
                <Spinner size={"sm"} />
                &nbsp;&nbsp;
                {shouldUploadOpenApi
                  ? "A new deployment has been started. Its associated OpenAPI spec will be uploaded to Service Registry as soon as the deployment is up and running."
                  : "Your deployment has been successfully started and will be available shortly."}
                &nbsp;Please do not close this browser tab until the operation is completed.
              </>
            }
            aria-live="polite"
            data-testid="alert-deploy-success"
            actionClose={<AlertActionCloseButton onClose={close} />}
          />
        );
      },
      [shouldUploadOpenApi]
    )
  );

  const deployEndSuccess = useAlert(
    props.alerts,
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="success"
          title={"Your deployment is up and running."}
          aria-live="polite"
          data-testid="alert-upload-success"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const deployEndError = useAlert(
    props.alerts,
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="danger"
          title={"Something went wrong while deploying. Check the logs on your OpenShift instance for more details."}
          aria-live="polite"
          data-testid="alert-deploy-end-error"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const openApiUploadSuccess = useAlert(
    props.alerts,
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="success"
          title={"The OpenAPI spec has been uploaded to Service Registry successfully."}
          aria-live="polite"
          data-testid="alert-upload-success"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const fetchOpenApiSpec = useCallback(
    async (deploymentResourceName: string) => {
      const openApiContents = await openshift.fetchOpenApiFile(deploymentResourceName);

      if (!openApiContents) {
        return false;
      }

      if (shouldUploadOpenApi) {
        await openshift.uploadArtifactToServiceRegistry(
          `${props.workspaceFile.nameWithoutExtension} ${deploymentResourceName}`,
          openApiContents
        );
      }

      return true;
    },
    [openshift, props.workspaceFile.nameWithoutExtension, shouldUploadOpenApi]
  );

  const onConfirm = useCallback(async () => {
    if (isConfirmLoading) {
      return;
    }

    setConfirmLoading(true);
    const resourceName = await openshift.deploy({
      workspaceFile: props.workspaceFile,
      shouldAttachKafkaSource,
      shouldDeployAsProject,
    });
    setConfirmLoading(false);

    openshift.setConfirmDeployModalOpen(false);

    if (resourceName) {
      openshift.setDeploymentsDropdownOpen(true);
      setDeployStartedSuccess.show();

      const fetchOpenApiTask = window.setInterval(async () => {
        try {
          const success = await fetchOpenApiSpec(resourceName);
          if (!success) {
            return;
          }

          setDeployStartedSuccess.close();
          if (shouldUploadOpenApi) {
            openApiUploadSuccess.show();
          } else {
            deployEndSuccess.show();
          }
        } catch (e) {
          setDeployStartedSuccess.close();
          deployEndError.show();
        }

        window.clearInterval(fetchOpenApiTask);
      }, FETCH_OPEN_API_POLLING_TIME);
    } else {
      setDeployStartedError.show();
    }
  }, [
    isConfirmLoading,
    openshift,
    props.workspaceFile,
    shouldAttachKafkaSource,
    shouldDeployAsProject,
    setDeployStartedSuccess,
    fetchOpenApiSpec,
    shouldUploadOpenApi,
    openApiUploadSuccess,
    deployEndSuccess,
    setDeployStartedError,
    deployEndError,
  ]);

  const onCancel = useCallback(() => {
    openshift.setConfirmDeployModalOpen(false);
    setConfirmLoading(false);
  }, [openshift]);

  return (
    <Modal
      data-testid={"confirm-deploy-modal"}
      variant={ModalVariant.small}
      title={i18n.openshift.confirmModal.title}
      isOpen={openshift.isConfirmDeployModalOpen}
      aria-label={"Confirm deploy modal"}
      onClose={onCancel}
      actions={[
        <Button
          id="confirm-deploy-button"
          key="confirm"
          variant="primary"
          onClick={onConfirm}
          isLoading={isConfirmLoading}
          spinnerAriaValueText={isConfirmLoading ? "Loading" : undefined}
        >
          {isConfirmLoading ? "Deploying ..." : i18n.terms.confirm}
        </Button>,
        <Button key="cancel" variant="link" onClick={onCancel}>
          {i18n.terms.cancel}
        </Button>,
      ]}
    >
      <>
        {i18n.openshift.confirmModal.body}
        <br />
        <br />
        <Tooltip
          content={
            "Cannot deploy as a project since your workspace does not seem to contain a single module project structure."
          }
          trigger={!canDeployAsProject ? "mouseenter click" : ""}
        >
          <Checkbox
            id="check-deploy-as-project"
            label="Deploy as a project"
            description={"All files in the workspace will be deployed as-is so no pre-built template will be used."}
            isChecked={shouldDeployAsProject}
            onChange={(checked) => setShouldDeployAsProject(checked)}
            isDisabled={!canDeployAsProject}
          />
        </Tooltip>
        <ExpandableSection
          toggleTextCollapsed="Show advanced options"
          toggleTextExpanded="Hide advanced options"
          className={"plain"}
        >
          <Tooltip
            content={"To use this option, you need to configure your Service Account and Service Registry on Settings."}
            trigger={!canUploadOpenApi ? "mouseenter click" : ""}
          >
            <Checkbox
              id="check-use-service-registry"
              label="Upload OpenAPI spec to Service Registry"
              description={"The spec will be available in the Service Registry, thus enabling autocompletion."}
              isChecked={shouldUploadOpenApi}
              onChange={(checked) => setShouldUploadOpenApi(checked)}
              isDisabled={!canUploadOpenApi}
            />
          </Tooltip>
          <Tooltip
            content={
              "To use this option, you need to configure your Service Account and Streams for Apache Kafka on Settings."
            }
            trigger={!canAttachKafkaSource ? "mouseenter click" : ""}
          >
            <Checkbox
              id="check-use-apache-kafka"
              label="Attach KafkaSource to the deployment"
              description={"Your deployment will listen to incoming messages even when scaled down."}
              isChecked={shouldAttachKafkaSource}
              onChange={(checked) => setShouldAttachKafkaSource(checked)}
              isDisabled={!canAttachKafkaSource}
            />
          </Tooltip>
        </ExpandableSection>
      </>
    </Modal>
  );
}
