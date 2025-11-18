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

import * as React from "react";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { useCallback, useState } from "react";
import { useOnlineI18n } from "../i18n";
import { useDevDeployments } from "./DevDeploymentsContext";
import { useGlobalAlert } from "../alerts";
import { useAuthSession } from "../authSessions/AuthSessionsContext";

export function DevDeploymentsConfirmDeleteModal() {
  const devDeployments = useDevDeployments();
  const { i18n } = useOnlineI18n();
  const [isLoading, setLoading] = useState(false);

  const { authSession } = useAuthSession(
    devDeployments.confirmDeleteModalState.isOpen
      ? devDeployments.confirmDeleteModalState.cloudAuthSessionId
      : undefined
  );

  const deleteErrorAlert = useGlobalAlert(
    useCallback(
      ({ close }) => (
        <Alert
          variant="danger"
          title={i18n.devDeployments.alerts.deleteError}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    ),
    { durationInSeconds: 5 }
  );

  const deleteSuccessAlert = useGlobalAlert(
    useCallback(
      ({ close }) => (
        <Alert
          className={"kogito--alert"}
          variant="info"
          title={i18n.devDeployments.alerts.deleteSuccess}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      ),
      [i18n]
    ),
    { durationInSeconds: 5 }
  );

  const onConfirm = useCallback(async () => {
    if (
      isLoading ||
      (authSession?.type !== "openshift" && authSession?.type !== "kubernetes") ||
      !devDeployments.confirmDeleteModalState.isOpen
    ) {
      return;
    }

    setLoading(true);
    const deleteStarted = await devDeployments.deleteDeployments({
      authSession,
      resources: devDeployments.confirmDeleteModalState.resources,
    });
    await delay(600);
    setLoading(false);

    devDeployments.setConfirmDeleteModalState({ isOpen: false });
    devDeployments.setDeploymentsDropdownOpen(true);

    if (deleteStarted) {
      deleteSuccessAlert.show();
    } else {
      deleteErrorAlert.show();
    }
  }, [isLoading, authSession, devDeployments, deleteSuccessAlert, deleteErrorAlert]);

  const onCancel = useCallback(() => {
    devDeployments.setConfirmDeleteModalState({ isOpen: false });
  }, [devDeployments]);

  return (
    <Modal
      data-testid={"confirm-delete-modal"}
      variant={ModalVariant.small}
      title={i18n.devDeployments.deleteConfirmModal.title}
      isOpen={devDeployments.confirmDeleteModalState.isOpen}
      aria-label={"Confirm delete modal"}
      onClose={onCancel}
      actions={[
        <Button
          id="dev-deployments-confirm-delete-button"
          key="confirm"
          variant="primary"
          onClick={onConfirm}
          isLoading={isLoading}
          spinnerAriaValueText={isLoading ? "Loading" : undefined}
        >
          {isLoading ? i18n.devDeployments.common.deleting : i18n.terms.confirm}
        </Button>,
        <Button key="cancel" variant="link" onClick={onCancel}>
          {i18n.terms.cancel}
        </Button>,
      ]}
    >
      {i18n.devDeployments.deleteConfirmModal.body}
      <br />
      <br />
      {`This action is not reversible and links you shared with other people will not be available anymore.`}
      <br />
      <br />
      {`The following resources will be deleted:`}
      <br />
      <br />
      {devDeployments.confirmDeleteModalState.isOpen && (
        <ul>
          {devDeployments.confirmDeleteModalState.resources.map((resource) => {
            return (
              <li key={`${resource.kind}: ${resource.metadata?.name}`}>
                <b>
                  - {resource.kind}: {resource.metadata?.name}
                </b>
              </li>
            );
          })}
        </ul>
      )}
      <br />
      <br />
    </Modal>
  );
}

function delay(ms: number) {
  return new Promise((res) => setTimeout(res, ms));
}
