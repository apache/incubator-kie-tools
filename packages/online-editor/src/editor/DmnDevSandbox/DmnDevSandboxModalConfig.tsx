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

import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ActionGroup, Form, FormAlert, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { BaseSizes } from "@patternfly/react-core/dist/js/styles/sizes";
import { ArrowRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-right-icon";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { DmnDevSandboxConnectionConfig, EMPTY_CONFIG, isConfigValid } from "./DmnDevSandboxConnectionConfig";
import { useDmnDevSandbox } from "./DmnDevSandboxContext";

enum FormValiationOptions {
  INITIAL,
  INVALID,
  CONNECTION_ERROR,
}

export function DmnDevSandboxModalConfig() {
  const dmnDevSandboxContext = useDmnDevSandbox();
  const { i18n } = useOnlineI18n();
  const [config, setConfig] = useState(dmnDevSandboxContext.currentConfig);
  const [isConfigValidated, setConfigValidated] = useState(FormValiationOptions.INITIAL);
  const [isSaveLoading, setSaveLoading] = useState(false);

  useEffect(() => {
    setConfig(dmnDevSandboxContext.currentConfig);
  }, [dmnDevSandboxContext.currentConfig]);

  const resetUI = useCallback((config: DmnDevSandboxConnectionConfig) => {
    setConfigValidated(FormValiationOptions.INITIAL);
    setSaveLoading(false);
    setConfig(config);
  }, []);

  const onResetConfig = useCallback(() => {
    dmnDevSandboxContext.onResetConfig();
    resetUI(EMPTY_CONFIG);
  }, [dmnDevSandboxContext, resetUI]);

  const onClose = useCallback(() => {
    dmnDevSandboxContext.setConfigModalOpen(false);
    resetUI(dmnDevSandboxContext.currentConfig);
  }, [dmnDevSandboxContext, resetUI]);

  const onSave = useCallback(async () => {
    if (isSaveLoading) {
      return;
    }

    if (!isConfigValid(config)) {
      setConfigValidated(FormValiationOptions.INVALID);
      return;
    }

    setSaveLoading(true);
    const isConfigOk = await dmnDevSandboxContext.onCheckConfig(config, true);
    setSaveLoading(false);

    if (!isConfigOk) {
      setConfigValidated(FormValiationOptions.CONNECTION_ERROR);
      return;
    }

    dmnDevSandboxContext.setConfigModalOpen(false);
    resetUI(config);
  }, [config, dmnDevSandboxContext, isSaveLoading, resetUI]);

  const onClearHost = useCallback(() => setConfig({ ...config, host: "" }), [config]);
  const onClearUsername = useCallback(() => setConfig({ ...config, username: "" }), [config]);
  const onClearToken = useCallback(() => setConfig({ ...config, token: "" }), [config]);

  const onHostChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, host: newValue });
    },
    [config]
  );

  const onUsernameChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, username: newValue });
    },
    [config]
  );

  const onTokenChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, token: newValue });
    },
    [config]
  );

  const onGoToWizard = useCallback(() => {
    onClose();
    dmnDevSandboxContext.setConfigWizardOpen(true);
  }, [dmnDevSandboxContext, onClose]);

  return (
    <Modal
      data-testid={"config-dmn-dev-sandbox-modal"}
      variant={ModalVariant.medium}
      isOpen={dmnDevSandboxContext.isConfigModalOpen}
      onClose={onClose}
      aria-label={"Configure DmnDevSandbox modal"}
      header={
        <Title headingLevel="h1" size={BaseSizes["2xl"]}>
          {i18n.dmnDevSandbox.common.deployInstanceInfo}
        </Title>
      }
    >
      <>
        <Text component={TextVariants.p} className="pf-u-mb-md">
          {i18n.dmnDevSandbox.common.disclaimer}
        </Text>
        <div className="pf-u-my-md">
          <Button key="use-wizard" className="pf-u-p-0" variant="link" onClick={onGoToWizard}>
            {i18n.dmnDevSandbox.configModal.useWizard}
            <ArrowRightIcon className="pf-u-ml-sm" />
          </Button>
        </div>
        <Form>
          {isConfigValidated === FormValiationOptions.INVALID && (
            <FormAlert>
              <Alert
                variant="danger"
                title={i18n.dmnDevSandbox.configModal.validationError}
                aria-live="polite"
                isInline
              />
            </FormAlert>
          )}
          {isConfigValidated === FormValiationOptions.CONNECTION_ERROR && (
            <FormAlert>
              <Alert
                variant="danger"
                title={i18n.dmnDevSandbox.configModal.connectionError}
                aria-live="polite"
                isInline
              />
            </FormAlert>
          )}
          <FormGroup
            label={i18n.terms.username}
            labelIcon={
              <Popover bodyContent={i18n.dmnDevSandbox.configModal.usernameInfo}>
                <button
                  type="button"
                  aria-label="More info for username field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="username-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="username-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoComplete={"off"}
                isRequired
                type="text"
                id="username-field"
                name="username-field"
                aria-describedby="username-field-helper"
                value={config.username}
                onChange={onUsernameChanged}
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear username button" onClick={onClearUsername}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <FormGroup
            label={i18n.terms.host}
            labelIcon={
              <Popover bodyContent={i18n.dmnDevSandbox.configModal.hostInfo}>
                <button
                  type="button"
                  aria-label="More info for host field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="host-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="host-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoComplete={"off"}
                isRequired
                type="text"
                id="host-field"
                name="host-field"
                aria-describedby="host-field-helper"
                value={config.host}
                onChange={onHostChanged}
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear host button" onClick={onClearHost}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <FormGroup
            label={i18n.terms.token}
            labelIcon={
              <Popover bodyContent={i18n.dmnDevSandbox.configModal.tokenInfo}>
                <button
                  type="button"
                  aria-label="More info for token field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="token-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="token-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoComplete={"off"}
                isRequired
                type="text"
                id="token-field"
                name="token-field"
                aria-describedby="token-field-helper"
                value={config.token}
                onChange={onTokenChanged}
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear token button" onClick={onClearToken}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <ActionGroup>
            <Button
              key="save"
              variant="primary"
              onClick={onSave}
              isLoading={isSaveLoading}
              spinnerAriaValueText={isSaveLoading ? "Loading" : undefined}
            >
              {isSaveLoading ? i18n.dmnDevSandbox.common.saving : i18n.terms.save}
            </Button>
            <Button key="reset" variant="danger" onClick={onResetConfig}>
              {i18n.terms.reset}
            </Button>
            <Button key="cancel" variant="link" onClick={onClose}>
              {i18n.terms.cancel}
            </Button>
          </ActionGroup>
        </Form>
      </>
    </Modal>
  );
}
