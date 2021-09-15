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

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import * as React from "react";
import { useCallback, useState } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { useDmnDevSandbox } from "./DmnDevSandboxContext";
import { useSettings } from "../../settings/SettingsContext";

export function DmnDevSandboxModalConfirmDeploy() {
  const dmnDevSandboxContext = useDmnDevSandbox();
  const { i18n } = useOnlineI18n();
  const settings = useSettings();
  const [isConfirmLoading, setConfirmLoading] = useState(false);

  const onConfirm = useCallback(async () => {
    if (isConfirmLoading) {
      return;
    }

    setConfirmLoading(true);
    await dmnDevSandboxContext.onDeploy(settings.openshift.config.get);
    setConfirmLoading(false);

    dmnDevSandboxContext.setConfirmDeployModalOpen(false);
  }, [dmnDevSandboxContext, isConfirmLoading]);

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
