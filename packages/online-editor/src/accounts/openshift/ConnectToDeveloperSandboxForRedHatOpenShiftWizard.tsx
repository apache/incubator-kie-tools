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

import React, { useCallback, useMemo, useState } from "react";
import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText, InputGroupItem } from "@patternfly/react-core/dist/js/components/InputGroup";
import { List, ListComponent, ListItem, OrderType } from "@patternfly/react-core/dist/js/components/List";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Wizard, WizardContextConsumer, WizardFooter } from "@patternfly/react-core/deprecated";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useOnlineI18n } from "../../i18n";
import { OpenShiftSettingsTabMode } from "./ConnectToOpenShiftSection";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import { KieSandboxOpenShiftService } from "../../devDeployments/services/openshift/KieSandboxOpenShiftService";
import { v4 as uuid } from "uuid";
import { useAuthSessionsDispatch, useSyncCloudAuthSession } from "../../authSessions/AuthSessionsContext";
import {
  AUTH_SESSION_VERSION_NUMBER,
  AuthSession,
  CloudAuthSessionType,
  OpenShiftAuthSession,
} from "../../authSessions/AuthSessionApi";
import {
  KubernetesConnection,
  isHostValid,
  isKubernetesConnectionValid,
  isNamespaceValid,
  isTokenValid,
  DEVELOPER_SANDBOX_GET_STARTED_URL,
  KubernetesConnectionStatus,
} from "@kie-tools-core/kubernetes-bridge/dist/service";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";

enum WizardStepIds {
  NAMESPACE = "NAMESPACE",
  CREDENTIALS = "CREDENTIALS",
  CONNECT = "CONNECT",
}

export function ConnectToDeveloperSandboxForRedHatOpenShiftWizard(props: {
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
  const [isConnectionValidated, setConnectionValidated] = useState(false);
  const [isConnecting, setConnecting] = useState(false);
  const [isConnectLoading, setConnectLoading] = useState(false);
  const authSessionsDispatch = useAuthSessionsDispatch();

  const onClearHost = useCallback(() => props.setConnection({ ...props.connection, host: "" }), [props]);
  const onClearNamespace = useCallback(() => props.setConnection({ ...props.connection, namespace: "" }), [props]);
  const onClearToken = useCallback(() => props.setConnection({ ...props.connection, token: "" }), [props]);

  const isNamespaceValidated = useMemo(() => {
    return isNamespaceValid(props.connection.namespace);
  }, [props.connection.namespace]);

  const isHostValidated = useMemo(() => {
    return isHostValid(props.connection.host);
  }, [props.connection.host]);

  const isTokenValidated = useMemo(() => {
    return isTokenValid(props.connection.token);
  }, [props.connection.token]);

  useSyncCloudAuthSession(props.selectedAuthSession, props.setConnection);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        setConnectLoading(true);
        if (!props.kieSandboxOpenShiftService) {
          setConnectionValidated(false);
          setConnectLoading(false);
          return;
        }

        props.kieSandboxOpenShiftService.isConnectionEstablished().then((connectionStatus) => {
          if (canceled.get()) {
            setConnectLoading(false);
            return;
          }
          setConnectionValidated(connectionStatus === KubernetesConnectionStatus.CONNECTED);
          setConnectLoading(false);
        });
      },
      [props.kieSandboxOpenShiftService]
    )
  );

  const onCancel = useCallback(() => {
    props.setMode(OpenShiftSettingsTabMode.SIMPLE);
  }, [props]);

  const onNamespaceInputChanged = useCallback(
    (event: React.FormEvent<HTMLInputElement>, newValue: string) => {
      props.setConnection((c) => ({ ...c, namespace: newValue }));
    },
    [props]
  );

  const onHostInputChanged = useCallback(
    (event: React.FormEvent<HTMLInputElement>, newValue: string) => {
      props.setConnection((c) => ({ ...c, host: newValue }));
    },
    [props]
  );

  const onTokenInputChanged = useCallback(
    (event: React.FormEvent<HTMLInputElement>, newValue: string) => {
      props.setConnection((c) => ({ ...c, token: newValue }));
    },
    [props]
  );

  const onInsecurelyDisableTlsCertificateValidationChange = useCallback(
    (event: React.FormEvent<HTMLInputElement>, checked: boolean) => {
      props.setConnection({ ...props.connection, insecurelyDisableTlsCertificateValidation: checked });
    },
    [props]
  );

  const onSave = useCallback(async () => {
    if (isConnecting) {
      return;
    }

    if (!isKubernetesConnectionValid(props.connection)) {
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
      setConnectionValidated(true);
      props.setStatus(OpenShiftInstanceStatus.CONNECTED);
      props.setNewAuthSession(newAuthSession);
      if (props.selectedAuthSession) {
        authSessionsDispatch.update(newAuthSession);
      } else {
        authSessionsDispatch.add(newAuthSession);
      }
    } else {
      setConnectionValidated(false);
      return;
    }
  }, [authSessionsDispatch, isConnecting, props]);

  const wizardSteps = useMemo(
    () => [
      {
        id: WizardStepIds.NAMESPACE,
        name: i18n.devDeployments.openShiftConfigWizard.steps.first.name,
        component: (
          <div>
            <Text component={TextVariants.p}>{i18n.devDeployments.openShiftConfigWizard.steps.first.introduction}</Text>
            <br />
            <List component={ListComponent.ol} type={OrderType.number} className="pf-v5-u-mt-md">
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <a href={DEVELOPER_SANDBOX_GET_STARTED_URL} target={"_blank"}>
                      {i18n.devDeployments.openShiftConfigWizard.steps.first.goToGetStartedPage}
                      &nbsp;
                      <ExternalLinkAltIcon className="pf-v5-u-mx-sm" />
                    </a>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.devDeployments.openShiftConfigWizard.steps.first.followSteps}
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    {i18n.devDeployments.openShiftConfigWizard.steps.first.informNamespace}
                  </Text>
                </TextContent>
              </ListItem>
            </List>
            <br />
            <br />
            <Form className="pf-v5-u-mt-md" onSubmit={(e) => e.preventDefault()}>
              <FormGroup fieldId={"dev-deployments-config-namespace"} label={i18n.terms.namespace} isRequired={true}>
                <InputGroup>
                  <InputGroupItem isFill>
                    <TextInput
                      autoFocus={true}
                      autoComplete={"off"}
                      type="text"
                      id="namespace-field"
                      name="namespace-field"
                      aria-label="namespace field"
                      value={props.connection.namespace}
                      placeholder={i18n.devDeployments.openShiftConfigWizard.steps.first.namespacePlaceholder}
                      onChange={onNamespaceInputChanged}
                    />
                  </InputGroupItem>
                  <InputGroupText>
                    <Button size="sm" variant="plain" aria-label="Clear namespace button" onClick={onClearNamespace}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
                <HelperText>
                  {isNamespaceValidated === false ? (
                    <HelperTextItem variant="error">{i18n.devDeployments.common.requiredField}</HelperTextItem>
                  ) : (
                    <HelperTextItem icon={ValidatedOptions.success}></HelperTextItem>
                  )}
                </HelperText>
              </FormGroup>
            </Form>
            <br />
            <br />
            <Text className="pf-v5-u-my-md" component={TextVariants.p}>
              {i18n.devDeployments.openShiftConfigWizard.steps.first.inputReason}
            </Text>
          </div>
        ),
      },
      {
        id: WizardStepIds.CREDENTIALS,
        name: i18n.devDeployments.openShiftConfigWizard.steps.second.name,
        component: (
          <div>
            <Text component={TextVariants.p}>
              {i18n.devDeployments.openShiftConfigWizard.steps.second.introduction}
            </Text>
            <br />
            <List className="pf-v5-u-my-md" component={ListComponent.ol} type={OrderType.number}>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.devDeployments.openShiftConfigWizard.steps.second.accessLoginCommand}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.devDeployments.openShiftConfigWizard.steps.second.accessDisplayToken}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.devDeployments.openShiftConfigWizard.steps.second.copyInformation}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
            </List>
            <br />
            <br />
            <Form className="pf-v5-u-mt-md">
              <FormGroup fieldId={"dev-deployments-config-host"} label={i18n.terms.host} isRequired={true}>
                <InputGroup>
                  <InputGroupItem isFill>
                    <TextInput
                      autoFocus={true}
                      autoComplete={"off"}
                      isRequired
                      type="text"
                      id="host-field"
                      name="host-field"
                      aria-label="Host field"
                      value={props.connection.host}
                      placeholder={i18n.devDeployments.openShiftConfigWizard.steps.second.hostPlaceholder}
                      onChange={onHostInputChanged}
                      tabIndex={1}
                    />
                  </InputGroupItem>
                  <InputGroupText>
                    <Button size="sm" variant="plain" aria-label="Clear host button" onClick={onClearHost}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
                <HelperText>
                  {isHostValidated === false ? (
                    <HelperTextItem variant="error">{i18n.devDeployments.common.requiredField}</HelperTextItem>
                  ) : (
                    <HelperTextItem icon={ValidatedOptions.success}></HelperTextItem>
                  )}
                </HelperText>
              </FormGroup>
              <FormGroup fieldId={"dev-deployments-config-token"} label={i18n.terms.token} isRequired={true}>
                <InputGroup>
                  <InputGroupItem isFill>
                    <TextInput
                      autoComplete={"off"}
                      isRequired
                      type="text"
                      id="token-field"
                      name="token-field"
                      aria-label="Token field"
                      value={props.connection.token}
                      placeholder={i18n.devDeployments.openShiftConfigWizard.steps.second.tokenPlaceholder}
                      onChange={onTokenInputChanged}
                      tabIndex={2}
                    />
                  </InputGroupItem>
                  <InputGroupText>
                    <Button size="sm" variant="plain" aria-label="Clear host button" onClick={onClearToken}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
                <HelperText>
                  {isTokenValidated === false ? (
                    <HelperTextItem variant="error">{i18n.devDeployments.common.requiredField}</HelperTextItem>
                  ) : (
                    <HelperTextItem icon={ValidatedOptions.success}></HelperTextItem>
                  )}
                </HelperText>
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
                  tabIndex={3}
                  isChecked={props.connection.insecurelyDisableTlsCertificateValidation}
                  onChange={onInsecurelyDisableTlsCertificateValidationChange}
                />
              </FormGroup>
            </Form>
            <br />
            <br />
            <Text className="pf-v5-u-my-md" component={TextVariants.p}>
              {i18n.devDeployments.openShiftConfigWizard.steps.second.inputReason}
            </Text>
          </div>
        ),
      },
      {
        id: WizardStepIds.CONNECT,
        name: i18n.devDeployments.openShiftConfigWizard.steps.final.name,
        component: (
          <>
            {(isConnectLoading || isConnecting || props.isLoadingService) && (
              <div className="kogito--editor__dev-deployments-wizard-loading-spinner">
                <Spinner size="xl" />
              </div>
            )}
            {!isConnectLoading && isConnectionValidated && (
              <div>
                <Alert
                  variant={"custom"}
                  isInline={true}
                  title={i18n.devDeployments.openShiftConfigWizard.steps.final.connectionSuccess}
                />
                <br />
                <Text className="pf-v5-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.openShiftConfigWizard.steps.final.introduction}
                </Text>
                <br />
                <Text className="pf-v5-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.openShiftConfigWizard.steps.final.configNote}
                </Text>
              </div>
            )}
            {!isConnectLoading && !isConnectionValidated && !props.isLoadingService && (
              <div>
                <Alert
                  variant={"danger"}
                  isInline={true}
                  title={i18n.devDeployments.openShiftConfigWizard.steps.final.connectionError}
                />
                <br />
                <Text className="pf-v5-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.openShiftConfigWizard.steps.final.connectionErrorLong}
                </Text>
                <br />
                <Text className="pf-v5-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.openShiftConfigWizard.steps.final.possibleErrorReasons.introduction}
                </Text>
                <br />
                <List className="pf-v5-u-my-md">
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.devDeployments.openShiftConfigWizard.steps.final.possibleErrorReasons.emptyField}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.devDeployments.openShiftConfigWizard.steps.final.possibleErrorReasons.instanceExpired}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.devDeployments.openShiftConfigWizard.steps.final.possibleErrorReasons.tokenExpired}
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
                <br />
                <br />
                <Text className="pf-v5-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.openShiftConfigWizard.steps.final.checkInfo}
                </Text>
              </div>
            )}
          </>
        ),
      },
    ],
    [
      i18n,
      isNamespaceValidated,
      props.connection,
      props.isLoadingService,
      onNamespaceInputChanged,
      onClearNamespace,
      isHostValidated,
      onHostInputChanged,
      onClearHost,
      isTokenValidated,
      onTokenInputChanged,
      onClearToken,
      onInsecurelyDisableTlsCertificateValidationChange,
      isConnecting,
      isConnectLoading,
      isConnectionValidated,
    ]
  );

  const wizardFooter = useMemo(
    () => (
      <WizardFooter>
        <WizardContextConsumer>
          {({ activeStep, onNext, onBack }) => {
            if (activeStep.name !== i18n.devDeployments.openShiftConfigWizard.steps.final.name) {
              return (
                <>
                  <Button variant="primary" onClick={onNext}>
                    {i18n.terms.next}
                  </Button>
                  <Button
                    variant="secondary"
                    onClick={onBack}
                    isDisabled={activeStep.name === i18n.devDeployments.openShiftConfigWizard.steps.first.name}
                  >
                    {i18n.terms.back}
                  </Button>
                  <Button variant="link" onClick={onCancel}>
                    {i18n.terms.cancel}
                  </Button>
                </>
              );
            }
            // Final step buttons
            return (
              <>
                <Button
                  id="dev-deployments-config-continue-editing-button"
                  onClick={onSave}
                  isDisabled={!isConnectionValidated}
                  variant={ButtonVariant.primary}
                  isLoading={isConnecting || props.isLoadingService}
                  spinnerAriaValueText={isConnecting ? "Loading" : undefined}
                >
                  {isConnecting ? i18n.devDeployments.common.saving : i18n.terms.save}
                </Button>
                <Button variant="secondary" onClick={onBack}>
                  {i18n.terms.back}
                </Button>
                <Button variant="link" onClick={onCancel}>
                  {i18n.terms.cancel}
                </Button>
              </>
            );
          }}
        </WizardContextConsumer>
      </WizardFooter>
    ),
    [i18n, onSave, isConnectionValidated, isConnecting, props.isLoadingService, onCancel]
  );

  return <Wizard steps={wizardSteps} footer={wizardFooter} />;
}
