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
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import * as React from "react";
import { useCallback, useState } from "react";
import { useOnlineI18n } from "../../i18n";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import { AlertsController, useAlert } from "../../alerts/Alerts";
import { useDmnDevSandbox } from "./DmnDevSandboxContext";

interface Props {
  workspaceFile: WorkspaceFile;
  alerts: AlertsController | undefined;
}

export function DmnDevSandboxModalConfirmDeploy(props: Props) {
  const dmnDevSandboxContext = useDmnDevSandbox();
  const { i18n } = useOnlineI18n();
  const [isConfirmLoading, setConfirmLoading] = useState(false);

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

  const onConfirm = useCallback(async () => {
    if (isConfirmLoading) {
      return;
    }

    setConfirmLoading(true);
    const deployStarted = await dmnDevSandboxContext.deploy(props.workspaceFile);
    setConfirmLoading(false);

    dmnDevSandboxContext.setConfirmDeployModalOpen(false);

    if (deployStarted) {
      dmnDevSandboxContext.setDeploymentsDropdownOpen(true);
      deployStartedSuccessAlert.show();
    } else {
      deployStartedErrorAlert.show();
    }
  }, [isConfirmLoading, dmnDevSandboxContext, props.workspaceFile, deployStartedSuccessAlert, deployStartedErrorAlert]);

  const onCancel = useCallback(() => {
    dmnDevSandboxContext.setConfirmDeployModalOpen(false);
    setConfirmLoading(false);
  }, [dmnDevSandboxContext]);

  return (
    <Modal
      data-testid={"confirm-deploy-modal"}
      variant={ModalVariant.small}
      title={i18n.dmnDevSandbox.confirmModal.title}
      isOpen={dmnDevSandboxContext.isConfirmDeployModalOpen}
      aria-label={"Confirm deploy modal"}
      onClose={onCancel}
      actions={[
        <Button
          id="dmn-dev-sandbox-confirm-deploy-button"
          key="confirm"
          variant="primary"
          onClick={onConfirm}
          isLoading={isConfirmLoading}
          spinnerAriaValueText={isConfirmLoading ? "Loading" : undefined}
        >
          {isConfirmLoading ? i18n.dmnDevSandbox.common.deploying : i18n.terms.confirm}
        </Button>,
        <Button key="cancel" variant="link" onClick={onCancel}>
          {i18n.terms.cancel}
        </Button>,
      ]}
    >
      {i18n.dmnDevSandbox.confirmModal.body}
    </Modal>
  );
}
