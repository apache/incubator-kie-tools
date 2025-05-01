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
import {
  isKubernetesConnectionValid,
  KubernetesConnection,
  KubernetesConnectionStatus,
} from "@kie-tools-core/kubernetes-bridge/dist/service";
import { QuickStartContext, QuickStartContextValues } from "@patternfly/quickstarts";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { ActionGroup, Form, FormAlert, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText, InputGroupItem } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useCallback, useContext, useEffect, useState } from "react";
import { useAppI18n } from "../../i18n";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { DEV_MODE_FEATURE_NAME } from "../../openshift/swfDevMode/DevModeConstants";
import { QuickStartIds } from "../../quickstarts-data";
import { useSettings, useSettingsDispatch } from "../SettingsContext";
import { EMPTY_CONFIG, saveConfigCookie, saveDevModeEnabledConfigCookie } from "./OpenShiftSettingsConfig";
import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { OutlinedQuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

enum FormValiationOptions {
  INITIAL = "INITIAL",
  INVALID = "INVALID",
  CONNECTION_ERROR = "CONNECTION_ERROR",
  CONFIG_EXPIRED = "CONFIG_EXPIRED",
}

export function OpenShiftSettingsSimpleConfig() {
  const { i18n } = useAppI18n();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [config, setConfig] = useState(settings.openshift.config);
  const [isConfigValidated, setConfigValidated] = useState(FormValiationOptions.INITIAL);
  const [isConnecting, setConnecting] = useState(false);
  const [isDevModeConfigEnabled, setDevModeConfigEnabled] = useState(settings.openshift.isDevModeEnabled);
  const qsContext = useContext<QuickStartContextValues>(QuickStartContext);

  useEffect(() => {
    setConfig(settings.openshift.config);
    setConfigValidated(
      settings.openshift.status === OpenShiftInstanceStatus.EXPIRED
        ? FormValiationOptions.CONFIG_EXPIRED
        : FormValiationOptions.INITIAL
    );
  }, [settings.openshift.config, settings.openshift.status]);

  const resetConfig = useCallback(
    (config: KubernetesConnection) => {
      setConfigValidated(
        settings.openshift.status === OpenShiftInstanceStatus.EXPIRED && config !== EMPTY_CONFIG
          ? FormValiationOptions.CONFIG_EXPIRED
          : FormValiationOptions.INITIAL
      );
      setConnecting(false);
      setConfig(config);
    },
    [settings.openshift.status]
  );

  const onConnect = useCallback(async () => {
    if (isConnecting) {
      return;
    }

    if (!isKubernetesConnectionValid(config)) {
      setConfigValidated(FormValiationOptions.INVALID);
      return;
    }

    setConnecting(true);
    const isConfigOk =
      (await settingsDispatch.openshift.service.isConnectionEstablished(config)) ===
      KubernetesConnectionStatus.CONNECTED;

    if (isConfigOk) {
      saveConfigCookie(config);
      settingsDispatch.openshift.setConfig(config);
      settingsDispatch.openshift.setStatus(OpenShiftInstanceStatus.CONNECTED);
    }
    setConnecting(false);

    if (!isConfigOk) {
      setConfigValidated(FormValiationOptions.CONNECTION_ERROR);
      return;
    }

    resetConfig(config);
  }, [config, isConnecting, resetConfig, settingsDispatch.openshift]);

  const onClearHost = useCallback(() => setConfig({ ...config, host: "" }), [config]);
  const onClearNamespace = useCallback(() => setConfig({ ...config, namespace: "" }), [config]);
  const onClearToken = useCallback(() => setConfig({ ...config, token: "" }), [config]);

  const onHostChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, host: newValue });
    },
    [config]
  );

  const onNamespaceChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, namespace: newValue });
    },
    [config]
  );

  const onTokenChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, token: newValue });
    },
    [config]
  );

  const onInsecurelyDisableTlsCertificateValidationChange = useCallback(
    (checked: boolean) => {
      setConfig({ ...config, insecurelyDisableTlsCertificateValidation: checked });
    },
    [config]
  );

  const onEnableDevModeConfigChanged = useCallback(
    (isEnabled: boolean) => {
      setDevModeConfigEnabled(isEnabled);
      saveDevModeEnabledConfigCookie(isEnabled);
      settingsDispatch.openshift.setDevModeEnabled(isEnabled);
    },
    [settingsDispatch.openshift]
  );

  return (
    <>
      <Form>
        {isConfigValidated === FormValiationOptions.INVALID && (
          <FormAlert>
            <Alert
              variant="danger"
              title={i18n.openshift.configModal.validationError}
              aria-live="polite"
              isInline
              data-testid="alert-validation-error"
            />
          </FormAlert>
        )}
        {isConfigValidated === FormValiationOptions.CONNECTION_ERROR && (
          <FormAlert>
            <Alert
              variant="danger"
              title={i18n.openshift.configModal.connectionError}
              aria-live="polite"
              isInline
              data-testid="alert-connection-error"
            />
          </FormAlert>
        )}
        {isConfigValidated === FormValiationOptions.CONFIG_EXPIRED && (
          <FormAlert>
            <Alert
              variant="warning"
              title={i18n.openshift.configModal.configExpiredWarning}
              aria-live="polite"
              isInline
              data-testid="alert-config-expired-warning"
            />
          </FormAlert>
        )}
        <FormGroup
          label={i18n.terms.namespace}
          labelIcon={
            <Popover bodyContent={i18n.openshift.configModal.namespaceInfo}>
              <button
                type="button"
                aria-label="More info for namespace field"
                onClick={(e) => e.preventDefault()}
                aria-describedby="namespace-field"
                className="pf-v5-c-form__group-label-help"
              >
                <Icon isInline>
                  <HelpIcon />
                </Icon>
              </button>
            </Popover>
          }
          isRequired
          fieldId="namespace-field"
        >
          <InputGroup className="pf-v5-u-mt-sm">
            <InputGroupItem isFill>
              <TextInput
                autoComplete={"off"}
                isRequired
                type="text"
                id="namespace-field"
                name="namespace-field"
                aria-label="Namespace field"
                aria-describedby="namespace-field-helper"
                value={config.namespace}
                onChange={(_event, value) => onNamespaceChanged(value)}
                isDisabled={isConnecting}
                tabIndex={1}
                data-testid="namespace-text-field"
              />
            </InputGroupItem>
            <InputGroupText>
              <Button size="sm" variant="plain" aria-label="Clear namespace button" onClick={onClearNamespace}>
                <TimesIcon />
              </Button>
            </InputGroupText>
          </InputGroup>
        </FormGroup>
        <FormGroup
          label={i18n.terms.host}
          labelIcon={
            <Popover bodyContent={i18n.openshift.configModal.hostInfo}>
              <button
                type="button"
                aria-label="More info for host field"
                onClick={(e) => e.preventDefault()}
                aria-describedby="host-field"
                className="pf-v5-c-form__group-label-help"
              >
                <Icon isInline>
                  <HelpIcon />
                </Icon>
              </button>
            </Popover>
          }
          isRequired
          fieldId="host-field"
        >
          <InputGroup className="pf-v5-u-mt-sm">
            <InputGroupItem isFill>
              <TextInput
                autoComplete={"off"}
                isRequired
                type="text"
                id="host-field"
                name="host-field"
                aria-label="Host field"
                aria-describedby="host-field-helper"
                value={config.host}
                onChange={(_event, value) => onHostChanged(value)}
                isDisabled={isConnecting}
                tabIndex={2}
                data-testid="host-text-field"
              />
            </InputGroupItem>
            <InputGroupText>
              <Button size="sm" variant="plain" aria-label="Clear host button" onClick={onClearHost}>
                <TimesIcon />
              </Button>
            </InputGroupText>
          </InputGroup>
        </FormGroup>
        <FormGroup
          label={i18n.terms.token}
          labelIcon={
            <Popover bodyContent={i18n.openshift.configModal.tokenInfo}>
              <button
                type="button"
                aria-label="More info for token field"
                onClick={(e) => e.preventDefault()}
                aria-describedby="token-field"
                className="pf-v5-c-form__group-label-help"
              >
                <Icon isInline>
                  <HelpIcon />
                </Icon>
              </button>
            </Popover>
          }
          isRequired
          fieldId="token-field"
        >
          <InputGroup className="pf-v5-u-mt-sm">
            <InputGroupItem isFill>
              <TextInput
                autoComplete={"off"}
                isRequired
                type="text"
                id="token-field"
                name="token-field"
                aria-label="Token field"
                aria-describedby="token-field-helper"
                value={config.token}
                onChange={(_event, value) => onTokenChanged(value)}
                isDisabled={isConnecting}
                tabIndex={3}
                data-testid="token-text-field"
              />
            </InputGroupItem>
            <InputGroupText>
              <Button size="sm" variant="plain" aria-label="Clear token button" onClick={onClearToken}>
                <TimesIcon />
              </Button>
            </InputGroupText>
          </InputGroup>
        </FormGroup>
        <FormGroup fieldId="disable-tls-validation">
          <Checkbox
            id="disable-tls-validation"
            name="disable-tls-validation"
            label={
              <>
                {i18n.openshift.configModal.insecurelyDisableTlsCertificateValidation}
                <Popover
                  minWidth="500px"
                  bodyContent={
                    <div>
                      <I18nHtml>{i18n.openshift.configModal.insecurelyDisableTlsCertificateValidationInfo}</I18nHtml>
                    </div>
                  }
                >
                  <button
                    type="button"
                    aria-label="Insecurely disable tls certificate validation info"
                    onClick={(e) => e.preventDefault()}
                    aria-describedby="disable-tls-validation"
                    className="pf-v5-c-form__group-label-help"
                  >
                    <OutlinedQuestionCircleIcon />
                  </button>
                </Popover>
              </>
            }
            aria-label="Disable TLS Certificate Validation"
            tabIndex={4}
            isChecked={config.insecurelyDisableTlsCertificateValidation}
            onChange={(_event, value) => onInsecurelyDisableTlsCertificateValidationChange(value)}
          />
          <br />
          <Button
            isInline={true}
            key="quickstart"
            variant="link"
            onClick={() => {
              qsContext.setActiveQuickStartID?.(QuickStartIds.OpenShiftIntegrationQuickStart);
              setTimeout(() => qsContext.setQuickStartTaskNumber?.(QuickStartIds.OpenShiftIntegrationQuickStart, 1), 0);
            }}
          >
            Need help getting started? Follow our quickstart guide.
          </Button>
        </FormGroup>
        <FormGroup
          label={DEV_MODE_FEATURE_NAME}
          labelIcon={
            <Popover
              bodyContent={
                "Automatically spins up a deployment running Quarkus in dev mode, making it quick to try model changes"
              }
            >
              <button
                type="button"
                aria-label="More info for Dev Mode field"
                onClick={(e) => e.preventDefault()}
                aria-describedby="dev-mode-field"
                className="pf-v5-c-form__group-label-help"
              >
                <Icon isInline>
                  <HelpIcon />
                </Icon>
              </button>
            </Popover>
          }
          isRequired
          fieldId="dev-mode-field"
        >
          <Checkbox
            id="enable-dev-mode-checkbox"
            label="Enable Dev Mode"
            description={
              "Be sure to set up at least 4GB of ram for your OpenShift deployments, otherwise, the Dev Mode deployment may run into issues."
            }
            isChecked={isDevModeConfigEnabled}
            onChange={(_event, value) => onEnableDevModeConfigChanged(value)}
          />
        </FormGroup>
        <ActionGroup>
          <Button
            id="openshift-config-save-button"
            key="save"
            variant="primary"
            onClick={onConnect}
            data-testid="save-config-button"
            isLoading={isConnecting}
            spinnerAriaValueText={isConnecting ? "Loading" : undefined}
          >
            {isConnecting ? "Connecting" : "Connect"}
          </Button>
        </ActionGroup>
      </Form>
    </>
  );
}
