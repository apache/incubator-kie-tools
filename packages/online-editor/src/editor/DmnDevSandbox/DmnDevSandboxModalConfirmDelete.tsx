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
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { useCallback, useState } from "react";
import { useOnlineI18n } from "../../i18n";
import { useAlert } from "../../alerts/Alerts";
import { useDmnDevSandbox } from "./DmnDevSandboxContext";
import { useAlerts } from "../../alerts/AlertsContext";

export function DmnDevSandboxModalConfirmDelete() {
  const dmnDevSandboxContext = useDmnDevSandbox();
  const { i18n } = useOnlineI18n();
  const { alerts } = useAlerts();
  const [isConfirmLoading, setConfirmLoading] = useState(false);

  const deleteErrorAlert = useAlert(
    alerts,
    useCallback(
      ({ close }) => (
        <Alert
          variant="danger"
          title={i18n.dmnDevSandbox.alerts.deleteError}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    )
  );

  const deleteSuccessAlert = useAlert(
    alerts,
    useCallback(
      ({ close }) => (
        <Alert
          className={"kogito--alert"}
          variant="info"
          title={i18n.dmnDevSandbox.alerts.deleteSuccess}
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
    const deleteStarted = await dmnDevSandboxContext.deleteDeployments();
    await delay(600);
    dmnDevSandboxContext.loadDeployments();
    setConfirmLoading(false);

    dmnDevSandboxContext.setConfirmDeleteModalOpen(false);
    dmnDevSandboxContext.setDeploymentsDropdownOpen(true);

    if (deleteStarted) {
      deleteSuccessAlert.show();
    } else {
      deleteErrorAlert.show();
    }
  }, [isConfirmLoading, dmnDevSandboxContext, deleteSuccessAlert, deleteErrorAlert]);

  const onCancel = useCallback(() => {
    dmnDevSandboxContext.setConfirmDeleteModalOpen(false);
    dmnDevSandboxContext.setDeploymentsToBeDeleted([]);
    setConfirmLoading(false);
  }, [dmnDevSandboxContext]);

  return (
    <Modal
      data-testid={"confirm-delete-modal"}
      variant={ModalVariant.small}
      title={i18n.dmnDevSandbox.deleteConfirmModal.title}
      isOpen={dmnDevSandboxContext.isConfirmDeleteModalOpen}
      aria-label={"Confirm delete modal"}
      onClose={onCancel}
      actions={[
        <Button
          id="dmn-dev-sandbox-confirm-delete-button"
          key="confirm"
          variant="primary"
          onClick={onConfirm}
          isLoading={isConfirmLoading}
          spinnerAriaValueText={isConfirmLoading ? "Loading" : undefined}
        >
          {isConfirmLoading ? i18n.dmnDevSandbox.common.deleting : i18n.terms.confirm}
        </Button>,
        <Button key="cancel" variant="link" onClick={onCancel}>
          {i18n.terms.cancel}
        </Button>,
      ]}
    >
      {i18n.dmnDevSandbox.deleteConfirmModal.body}
    </Modal>
  );
}

function delay(ms: number) {
  return new Promise((res) => setTimeout(res, ms));
}
