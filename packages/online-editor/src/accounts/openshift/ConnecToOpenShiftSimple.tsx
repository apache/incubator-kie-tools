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

import React, { useCallback, useEffect, useState } from "react";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ActionGroup, Form, FormAlert, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText, InputGroupItem } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ArrowRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-right-icon";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { useOnlineI18n } from "../../i18n";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import { OpenShiftSettingsTabMode } from "./ConnectToOpenShiftSection";
import { KieSandboxOpenShiftService } from "../../devDeployments/services/openshift/KieSandboxOpenShiftService";
import { useAuthSessionsDispatch, useSyncCloudAuthSession } from "../../authSessions/AuthSessionsContext";
import { v4 as uuid } from "uuid";
import {
  AUTH_SESSION_VERSION_NUMBER,
  AuthSession,
  CloudAuthSessionType,
  isCloudAuthSession,
  OpenShiftAuthSession,
} from "../../authSessions/AuthSessionApi";
import {
  KubernetesConnection,
  KubernetesConnectionStatus,
  isKubernetesConnectionValid,
} from "@kie-tools-core/kubernetes-bridge/dist/service/KubernetesConnection";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

enum FormValiationOptions {
  INITIAL = "INITIAL",
  INVALID = "INVALID",
  CONNECTION_ERROR = "CONNECTION_ERROR",
}

export function ConnecToOpenShiftSimple(props: {
  kieSandboxOpenShiftService: KieSandboxOpenShiftService | undefined;
  setMode: React.Dispatch<React.SetStateAction<OpenShiftSettingsTabMode>>;
  connection: KubernetesConnection;
  setConnection: React.Dispatch<React.SetStateAction<KubernetesConnection>>;
  status: OpenShiftInstanceStatus;
  setStatus: React.Dispatch<React.SetStateAction<OpenShiftInstanceStatus>>;
  setNewAuthSession: React.Dispatch<React.SetStateAction<OpenShiftAuthSession>>;
  isLoadingService: boolean;
  selectedAuthSession?: AuthSession;
}) {
  const { i18n } = useOnlineI18n();
  const [isConnectionValidated, setConnectionValidated] = useState(FormValiationOptions.INITIAL);
  const [isConnecting, setConnecting] = useState(false);
  const authSessionsDispatch = useAuthSessionsDispatch();
  useSyncCloudAuthSession(props.selectedAuthSession, props.setConnection);

  const onConnect = useCallback(async () => {
    if (isConnecting) {
      return;
    }

    if (!isKubernetesConnectionValid(props.connection)) {
      setConnectionValidated(FormValiationOptions.INVALID);
      return;
    }

    setConnecting(true);
    const isConnectionEstablished =
      props.kieSandboxOpenShiftService && (await props.kieSandboxOpenShiftService.isConnectionEstablished());
    setConnecting(false);

    if (isConnectionEstablished === KubernetesConnectionStatus.CONNECTED && props.kieSandboxOpenShiftService) {
      const newAuthSession: OpenShiftAuthSession = {
        type: CloudAuthSessionType.OpenShift,
        version: AUTH_SESSION_VERSION_NUMBER,
        id: props.selectedAuthSession?.id ?? uuid(),
        ...props.connection,
        authProviderId: "openshift",
        createdAtDateISO: new Date().toISOString(),
        k8sApiServerEndpointsByResourceKind: props.kieSandboxOpenShiftService.args.k8sApiServerEndpointsByResourceKind,
      };
      props.setStatus(OpenShiftInstanceStatus.CONNECTED);
      props.setNewAuthSession(newAuthSession);
      if (props.selectedAuthSession) {
        authSessionsDispatch.update(newAuthSession);
      } else {
        authSessionsDispatch.add(newAuthSession);
      }
    } else {
      setConnectionValidated(FormValiationOptions.CONNECTION_ERROR);
      return;
    }
  }, [authSessionsDispatch, isConnecting, props]);

  const onClearHost = useCallback(() => props.setConnection({ ...props.connection, host: "" }), [props]);
  const onClearNamespace = useCallback(() => props.setConnection({ ...props.connection, namespace: "" }), [props]);
  const onClearToken = useCallback(() => props.setConnection({ ...props.connection, token: "" }), [props]);

  const onHostChanged = useCallback(
    (event: React.FormEvent<HTMLInputElement>, newValue: string) => {
      props.setConnection({ ...props.connection, host: newValue });
    },
    [props]
  );

  const onNamespaceChanged = useCallback(
    (event: React.FormEvent<HTMLInputElement>, newValue: string) => {
      props.setConnection({ ...props.connection, namespace: newValue });
    },
    [props]
  );

  const onTokenChanged = useCallback(
    (event: React.FormEvent<HTMLInputElement>, newValue: string) => {
      props.setConnection({ ...props.connection, token: newValue });
    },
    [props]
  );

  const onInsecurelyDisableTlsCertificateValidationChange = useCallback(
    (event: React.FormEvent<HTMLInputElement>, checked: boolean) => {
      props.setConnection({ ...props.connection, insecurelyDisableTlsCertificateValidation: checked });
    },
    [props]
  );

  return (
    <>
      {isConnectionValidated === FormValiationOptions.INVALID && (
        <>
          <FormAlert>
            <Alert
              variant="danger"
              title={i18n.devDeployments.configModal.validationError}
              aria-live="polite"
              isInline
              data-testid="alert-validation-error"
            />
          </FormAlert>
          <br />
        </>
      )}
      {isConnectionValidated === FormValiationOptions.CONNECTION_ERROR && (
        <>
          {" "}
          <FormAlert>
            <Alert
              variant="danger"
              title={i18n.devDeployments.configModal.connectionError}
              aria-live="polite"
              isInline
              data-testid="alert-connection-error"
            />
          </FormAlert>
          <br />
        </>
      )}

      <Button
        style={{ paddingLeft: 0 }}
        id="dev-deployments-config-use-wizard-button"
        key="use-wizard"
        className="pf-v5-u-p-0"
        variant="link"
        onClick={() => props.setMode(OpenShiftSettingsTabMode.WIZARD)}
        data-testid="use-wizard-button"
        isLoading={isConnecting}
      >
        {i18n.devDeployments.configModal.useOpenShiftWizard}
        &nbsp;
        <ArrowRightIcon className="pf-v5-u-ml-sm" />
      </Button>

      <br />
      <br />

      <Form>
        <FormGroup
          label={i18n.terms.namespace}
          labelIcon={
            <Popover bodyContent={i18n.devDeployments.configModal.namespaceInfo}>
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
                autoFocus={true}
                autoComplete={"off"}
                isRequired
                type="text"
                id="namespace-field"
                name="namespace-field"
                aria-label="Namespace field"
                aria-describedby="namespace-field-helper"
                value={props.connection.namespace}
                onChange={onNamespaceChanged}
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
            <Popover bodyContent={i18n.devDeployments.configModal.hostInfo}>
              <button
                type="button"
                aria-label="More info for host field"
                onClick={(e) => e.preventDefault()}
                aria-describedby="host-field"
                className="pf-v5-c-form__group-label-help"
              >
                <Icon isInline>
                  <HelpIcon />
                </Icon>{" "}
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
                value={props.connection.host}
                onChange={onHostChanged}
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
            <Popover bodyContent={i18n.devDeployments.configModal.tokenInfo}>
              <button
                type="button"
                aria-label="More info for token field"
                onClick={(e) => e.preventDefault()}
                aria-describedby="token-field"
                className="pf-v5-c-form__group-label-help"
              >
                <Icon isInline>
                  <HelpIcon />
                </Icon>{" "}
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
                value={props.connection.token}
                onChange={onTokenChanged}
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
            label={i18n.devDeployments.configModal.insecurelyDisableTlsCertificateValidation}
            description={
              <I18nHtml>{i18n.devDeployments.configModal.insecurelyDisableTlsCertificateValidationInfo}</I18nHtml>
            }
            aria-label="Disable TLS Certificate Validation"
            tabIndex={4}
            isChecked={props.connection.insecurelyDisableTlsCertificateValidation}
            onChange={onInsecurelyDisableTlsCertificateValidationChange}
          />
        </FormGroup>
        <ActionGroup>
          <Button
            id="dev-deployments-config-save-button"
            key="save"
            variant="primary"
            onClick={onConnect}
            data-testid="save-config-button"
            isLoading={isConnecting || props.isLoadingService}
            isDisabled={isConnecting || props.isLoadingService}
            spinnerAriaValueText={isConnecting || props.isLoadingService ? "Loading" : undefined}
          >
            {isConnecting || props.isLoadingService ? "Connecting" : "Connect"}
          </Button>
        </ActionGroup>
      </Form>
    </>
  );
}
