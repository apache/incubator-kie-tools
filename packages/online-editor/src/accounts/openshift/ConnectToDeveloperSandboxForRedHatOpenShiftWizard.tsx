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

import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { List, ListComponent, ListItem, OrderType } from "@patternfly/react-core/dist/js/components/List";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Wizard, WizardContextConsumer, WizardFooter } from "@patternfly/react-core/dist/js/components/Wizard";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useOnlineI18n } from "../../i18n";
import {
  isOpenShiftConnectionValid,
  isHostValid,
  isNamespaceValid,
  isTokenValid,
  OpenShiftConnection,
} from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { DEVELOPER_SANDBOX_GET_STARTED_URL } from "@kie-tools-core/openshift/dist/service/OpenShiftConstants";
import { OpenShiftSettingsTabMode } from "./ConnectToOpenShiftSection";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import { KieSandboxOpenShiftService } from "../../openshift/KieSandboxOpenShiftService";
import { v4 as uuid } from "uuid";
import { useAccountsDispatch } from "../AccountsContext";
import { useAuthSessionsDispatch } from "../../authSessions/AuthSessionsContext";
import { OpenShiftAuthSession } from "../../authSessions/AuthSessionApi";

enum WizardStepIds {
  NAMESPACE = "NAMESPACE",
  CREDENTIALS = "CREDENTIALS",
  CONNECT = "CONNECT",
}

export function ConnectToDeveloperSandboxForRedHatOpenShiftWizard(props: {
  openshiftService: KieSandboxOpenShiftService;
  setMode: React.Dispatch<React.SetStateAction<OpenShiftSettingsTabMode>>;
  connection: OpenShiftConnection;
  setConnection: React.Dispatch<React.SetStateAction<OpenShiftConnection>>;
  status: OpenShiftInstanceStatus;
  setStatus: React.Dispatch<React.SetStateAction<OpenShiftInstanceStatus>>;
  setNewAuthSession: React.Dispatch<React.SetStateAction<OpenShiftAuthSession>>;
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

  useEffect(() => {
    setConnectionValidated(isOpenShiftConnectionValid(props.connection));
  }, [props.connection]);

  const onCancel = useCallback(() => {
    props.setMode(OpenShiftSettingsTabMode.SIMPLE);
  }, [props]);

  const resetConnection = useCallback(
    (connection: OpenShiftConnection) => {
      setConnectionValidated(false);
      setConnecting(false);
      setConnectLoading(false);
      props.setConnection(connection);
    },
    [props]
  );

  const onNamespaceInputChanged = useCallback(
    (newValue: string) => {
      props.setConnection((c) => ({ ...c, namespace: newValue }));
    },
    [props]
  );

  const onHostInputChanged = useCallback(
    (newValue: string) => {
      props.setConnection((c) => ({ ...c, host: newValue }));
    },
    [props]
  );

  const onTokenInputChanged = useCallback(
    (newValue: string) => {
      props.setConnection((c) => ({ ...c, token: newValue }));
    },
    [props]
  );

  const onStepChanged = useCallback(
    async ({ id }) => {
      if (id === WizardStepIds.CONNECT) {
        setConnectLoading(true);
        setConnectionValidated(await props.openshiftService.isConnectionEstablished());
        setConnectLoading(false);
      }
    },
    [props.openshiftService]
  );

  const onSave = useCallback(async () => {
    if (isConnecting) {
      return;
    }

    if (!isOpenShiftConnectionValid(props.connection)) {
      return;
    }

    setConnecting(true);
    const isConnectionEstablished = await props.openshiftService.isConnectionEstablished();
    setConnecting(false);

    if (isConnectionEstablished) {
      const newAuthSession: OpenShiftAuthSession = {
        type: "openshift",
        id: uuid(),
        ...props.connection,
        authProviderId: "openshift",
        createdAtDateISO: new Date().toISOString(),
      };
      setConnectionValidated(true);
      props.setStatus(OpenShiftInstanceStatus.CONNECTED);
      authSessionsDispatch.add(newAuthSession);
      props.setNewAuthSession(newAuthSession);
    } else {
      setConnectionValidated(false);
      return;
    }
  }, [authSessionsDispatch, isConnecting, props]);

  const wizardSteps = useMemo(
    () => [
      {
        id: WizardStepIds.NAMESPACE,
        name: i18n.devDeployments.configWizard.steps.first.name,
        component: (
          <div>
            <Text component={TextVariants.p}>{i18n.devDeployments.configWizard.steps.first.introduction}</Text>
            <br />
            <List component={ListComponent.ol} type={OrderType.number} className="pf-u-mt-md">
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <a href={DEVELOPER_SANDBOX_GET_STARTED_URL} target={"_blank"}>
                      {i18n.devDeployments.configWizard.steps.first.goToGetStartedPage}
                      &nbsp;
                      <ExternalLinkAltIcon className="pf-u-mx-sm" />
                    </a>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>{i18n.devDeployments.configWizard.steps.first.followSteps}</Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>{i18n.devDeployments.configWizard.steps.first.informNamespace}</Text>
                </TextContent>
              </ListItem>
            </List>
            <br />
            <br />
            <Form className="pf-u-mt-md" onSubmit={(e) => e.preventDefault()}>
              <FormGroup
                fieldId={"dev-deployments-config-namespace"}
                label={i18n.terms.namespace}
                validated={isNamespaceValidated ? "success" : "error"}
                helperTextInvalid={i18n.devDeployments.common.requiredField}
                isRequired={true}
              >
                <InputGroup>
                  <TextInput
                    autoFocus={true}
                    autoComplete={"off"}
                    type="text"
                    id="namespace-field"
                    name="namespace-field"
                    aria-label="namespace field"
                    value={props.connection.namespace}
                    placeholder={i18n.devDeployments.configWizard.steps.first.namespacePlaceholder}
                    onChange={onNamespaceInputChanged}
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear namespace button" onClick={onClearNamespace}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
            </Form>
            <br />
            <br />
            <Text className="pf-u-my-md" component={TextVariants.p}>
              {i18n.devDeployments.configWizard.steps.first.inputReason}
            </Text>
          </div>
        ),
      },
      {
        id: WizardStepIds.CREDENTIALS,
        name: i18n.devDeployments.configWizard.steps.second.name,
        component: (
          <div>
            <Text component={TextVariants.p}>{i18n.devDeployments.configWizard.steps.second.introduction}</Text>
            <br />
            <List className="pf-u-my-md" component={ListComponent.ol} type={OrderType.number}>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.devDeployments.configWizard.steps.second.accessLoginCommand}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.devDeployments.configWizard.steps.second.accessDisplayToken}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.devDeployments.configWizard.steps.second.copyInformation}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
            </List>
            <br />
            <br />
            <Form className="pf-u-mt-md">
              <FormGroup
                fieldId={"dev-deployments-config-host"}
                label={i18n.terms.host}
                validated={isHostValidated ? "success" : "error"}
                helperTextInvalid={i18n.devDeployments.common.requiredField}
                isRequired={true}
              >
                <InputGroup>
                  <TextInput
                    autoFocus={true}
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="host-field"
                    name="host-field"
                    aria-label="Host field"
                    value={props.connection.host}
                    placeholder={i18n.devDeployments.configWizard.steps.second.hostPlaceholder}
                    onChange={onHostInputChanged}
                    tabIndex={1}
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear host button" onClick={onClearHost}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
              <FormGroup
                fieldId={"dev-deployments-config-token"}
                label={i18n.terms.token}
                validated={isTokenValidated ? "success" : "error"}
                helperTextInvalid={i18n.devDeployments.common.requiredField}
                isRequired={true}
              >
                <InputGroup>
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="token-field"
                    name="token-field"
                    aria-label="Token field"
                    value={props.connection.token}
                    placeholder={i18n.devDeployments.configWizard.steps.second.tokenPlaceholder}
                    onChange={onTokenInputChanged}
                    tabIndex={2}
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear host button" onClick={onClearToken}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
            </Form>
            <br />
            <br />
            <Text className="pf-u-my-md" component={TextVariants.p}>
              {i18n.devDeployments.configWizard.steps.second.inputReason}
            </Text>
          </div>
        ),
      },
      {
        id: WizardStepIds.CONNECT,
        name: i18n.devDeployments.configWizard.steps.final.name,
        component: (
          <>
            {isConnectLoading && (
              <div className="kogito--editor__dev-deployments-wizard-loading-spinner">
                <Spinner isSVG size="xl" />
              </div>
            )}
            {!isConnectLoading && isConnectionValidated && (
              <div>
                <Alert
                  variant={"default"}
                  isInline={true}
                  title={i18n.devDeployments.configWizard.steps.final.connectionSuccess}
                />
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.configWizard.steps.final.introduction}
                </Text>
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.configWizard.steps.final.configNote}
                </Text>
              </div>
            )}
            {!isConnectLoading && !isConnectionValidated && (
              <div>
                <Alert
                  variant={"danger"}
                  isInline={true}
                  title={i18n.devDeployments.configWizard.steps.final.connectionError}
                />
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.configWizard.steps.final.connectionErrorLong}
                </Text>
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.configWizard.steps.final.possibleErrorReasons.introduction}
                </Text>
                <br />
                <List className="pf-u-my-md">
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.devDeployments.configWizard.steps.final.possibleErrorReasons.emptyField}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.devDeployments.configWizard.steps.final.possibleErrorReasons.instanceExpired}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.devDeployments.configWizard.steps.final.possibleErrorReasons.tokenExpired}
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
                <br />
                <br />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.devDeployments.configWizard.steps.final.checkInfo}
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
      props.connection.namespace,
      props.connection.host,
      props.connection.token,
      onNamespaceInputChanged,
      onClearNamespace,
      isHostValidated,
      onHostInputChanged,
      onClearHost,
      isTokenValidated,
      onTokenInputChanged,
      onClearToken,
      isConnectLoading,
      isConnectionValidated,
    ]
  );

  const wizardFooter = useMemo(
    () => (
      <WizardFooter>
        <WizardContextConsumer>
          {({ activeStep, goToStepByName, goToStepById, onNext, onBack }) => {
            if (activeStep.name !== i18n.devDeployments.configWizard.steps.final.name) {
              return (
                <>
                  <Button variant="primary" onClick={onNext}>
                    {i18n.terms.next}
                  </Button>
                  <Button
                    variant="secondary"
                    onClick={onBack}
                    isDisabled={activeStep.name === i18n.devDeployments.configWizard.steps.first.name}
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
                  isLoading={isConnecting}
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
    [i18n, isConnectionValidated, isConnecting, onCancel, onSave]
  );

  return <Wizard steps={wizardSteps} footer={wizardFooter} onNext={onStepChanged} onGoToStep={onStepChanged} />;
}
