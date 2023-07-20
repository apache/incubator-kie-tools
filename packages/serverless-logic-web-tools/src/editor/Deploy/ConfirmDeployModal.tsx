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

import * as React from "react";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { useCallback, useMemo, useState } from "react";
import { isOfKind } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { useAppI18n } from "../../i18n";
import { CompletedDeployOperation } from "../../openshift/deploy/types";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { SwfDeployOptions } from "./ConfirmOptions/SwfDeployOptions";
import { DashDeployOptions } from "./ConfirmOptions/DashDeployOptions";
import { useGlobalAlert } from "../../alerts/GlobalAlertsContext";

interface ConfirmDeployModalProps {
  workspace: ActiveWorkspace;
  workspaceFile: WorkspaceFile;
}

export interface ConfirmDeployOptionsRef {
  deploy(): Promise<CompletedDeployOperation>;
}

export type ConfirmDeployOptionsProps = Omit<ConfirmDeployModalProps, "alerts">;

export function ConfirmDeployModal(props: ConfirmDeployModalProps) {
  const { i18n } = useAppI18n();
  const openshift = useOpenShift();
  const [isConfirmLoading, setConfirmLoading] = useState(false);
  const [deployOptions, deployOptionsRef] = useController<ConfirmDeployOptionsRef>();

  const optionsComponent = useMemo(() => {
    let DeployOptionsComponent = null;

    if (isOfKind("sw", props.workspaceFile.name)) {
      DeployOptionsComponent = SwfDeployOptions;
    } else if (isOfKind("dash", props.workspaceFile.name)) {
      DeployOptionsComponent = DashDeployOptions;
    }

    return (
      DeployOptionsComponent && (
        <DeployOptionsComponent
          workspace={props.workspace}
          workspaceFile={props.workspaceFile}
          ref={deployOptionsRef}
        />
      )
    );
  }, [deployOptionsRef, props.workspace, props.workspaceFile]);

  const deployStartedErrorAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="danger"
          title={
            "Something went wrong while creating the deployment resources. Check your OpenShift settings and resource limits, and then try again."
          }
          aria-live="polite"
          data-testid="alert-deploy-error"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, []),
    { durationInSeconds: 5 }
  );

  const deployStartedSuccessAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="info"
          title={
            "Your deployment has been successfully started and will be available shortly. Please do not close this browser tab until the operation is completed."
          }
          aria-live="polite"
          data-testid="alert-deploy-success"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, []),
    { durationInSeconds: 5 }
  );

  const onConfirm = useCallback(async () => {
    if (isConfirmLoading || !deployOptions) {
      return;
    }

    setConfirmLoading(true);
    const resourceName = await deployOptions.deploy();
    setConfirmLoading(false);

    openshift.setConfirmDeployModalOpen(false);

    if (!resourceName) {
      deployStartedErrorAlert.show();
      return;
    }

    openshift.setDeploymentsDropdownOpen(true);
    deployStartedSuccessAlert.show();
  }, [isConfirmLoading, deployOptions, openshift, deployStartedSuccessAlert, deployStartedErrorAlert]);

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
          isDisabled={!optionsComponent}
          spinnerAriaValueText={isConfirmLoading ? "Loading" : undefined}
        >
          {isConfirmLoading ? "Creating resources ..." : i18n.terms.confirm}
        </Button>,
        <Button key="cancel" variant="link" onClick={onCancel} isDisabled={isConfirmLoading}>
          {i18n.terms.cancel}
        </Button>,
      ]}
    >
      {optionsComponent ?? "Deployments are not supported for this kind of file."}
    </Modal>
  );
}
