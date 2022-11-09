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
import { useKieSandboxExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { useOnlineI18n } from "../i18n";
import { useSettings, useSettingsDispatch } from "./SettingsContext";
import {
  isOpenShiftConnectionValid,
  isHostValid,
  isNamespaceValid,
  isTokenValid,
  OpenShiftConnection,
} from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { saveConfigCookie } from "../openshift/OpenShiftSettingsConfig";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { DEVELOPER_SANDBOX_GET_STARTED_URL } from "@kie-tools-core/openshift/dist/service/OpenShiftConstants";
import { OpenShiftSettingsTabMode } from "./OpenShiftSettingsTab";
import { OpenShiftInstanceStatus } from "../openshift/OpenShiftInstanceStatus";

enum WizardStepIds {
  NAMESPACE = "NAMESPACE",
  CREDENTIALS = "CREDENTIALS",
  CONNECT = "CONNECT",
}

export function OpenShiftSettingsTabWizardConfig(props: {
  setMode: React.Dispatch<React.SetStateAction<OpenShiftSettingsTabMode>>;
}) {
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const { i18n } = useOnlineI18n();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [config, setConfig] = useState(settings.openshift.config);
  const [isConfigValidated, setConfigValidated] = useState(false);
  const [isSaveLoading, setSaveLoading] = useState(false);
  const [isConnectLoading, setConnectLoading] = useState(false);

  const onClearHost = useCallback(() => setConfig({ ...config, host: "" }), [config]);
  const onClearNamespace = useCallback(() => setConfig({ ...config, namespace: "" }), [config]);
  const onClearToken = useCallback(() => setConfig({ ...config, token: "" }), [config]);

  const isNamespaceValidated = useMemo(() => {
    return isNamespaceValid(config.namespace);
  }, [config.namespace]);

  const isHostValidated = useMemo(() => {
    return isHostValid(config.host);
  }, [config.host]);

  const isTokenValidated = useMemo(() => {
    return isTokenValid(config.token);
  }, [config.token]);

  useEffect(() => {
    setConfigValidated(isOpenShiftConnectionValid(config));
  }, [config]);

  useEffect(() => {
    if (kieSandboxExtendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
      return;
    }
    setConfig(settings.openshift.config);
  }, [settings.openshift.config, kieSandboxExtendedServices.status]);

  const onClose = useCallback(() => {
    props.setMode(OpenShiftSettingsTabMode.SIMPLE);
  }, []);

  const resetConfig = useCallback((config: OpenShiftConnection) => {
    setConfigValidated(false);
    setSaveLoading(false);
    setConnectLoading(false);
    setConfig(config);
  }, []);

  const onNamespaceInputChanged = useCallback((newValue: string) => {
    setConfig((c) => ({ ...c, namespace: newValue }));
  }, []);

  const onHostInputChanged = useCallback((newValue: string) => {
    setConfig((c) => ({ ...c, host: newValue }));
  }, []);

  const onTokenInputChanged = useCallback((newValue: string) => {
    setConfig((c) => ({ ...c, token: newValue }));
  }, []);

  const onStepChanged = useCallback(
    async ({ id }) => {
      if (id === WizardStepIds.CONNECT) {
        setConnectLoading(true);
        setConfigValidated(await settingsDispatch.openshift.service.isConnectionEstablished(config));
        setConnectLoading(false);
      }
    },
    [config, settingsDispatch.openshift.service]
  );

  const onFinish = useCallback(async () => {
    if (isSaveLoading) {
      return;
    }

    setSaveLoading(true);

    const isConfigOk = await settingsDispatch.openshift.service.isConnectionEstablished(config);
    if (isConfigOk) {
      settingsDispatch.openshift.setConfig(config);
      saveConfigCookie(config);
      settingsDispatch.openshift.setStatus(OpenShiftInstanceStatus.CONNECTED);
    }

    setConfigValidated(isConfigOk);
    setSaveLoading(false);

    if (!isConfigOk) {
      return;
    }

    resetConfig(config);
  }, [config, isSaveLoading, resetConfig, settingsDispatch.openshift]);

  const wizardSteps = useMemo(
    () => [
      {
        id: WizardStepIds.NAMESPACE,
        name: i18n.dmnDevSandbox.configWizard.steps.first.name,
        component: (
          <div>
            <Text component={TextVariants.p}>{i18n.dmnDevSandbox.configWizard.steps.first.introduction}</Text>
            <List component={ListComponent.ol} type={OrderType.number} className="pf-u-mt-md">
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <a href={DEVELOPER_SANDBOX_GET_STARTED_URL} target={"_blank"}>
                      {i18n.dmnDevSandbox.configWizard.steps.first.goToGetStartedPage}
                      &nbsp;
                      <ExternalLinkAltIcon className="pf-u-mx-sm" />
                    </a>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>{i18n.dmnDevSandbox.configWizard.steps.first.followSteps}</Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>{i18n.dmnDevSandbox.configWizard.steps.first.informNamespace}</Text>
                </TextContent>
              </ListItem>
            </List>
            <Form isHorizontal={true} className="pf-u-mt-md" onSubmit={(e) => e.preventDefault()}>
              <FormGroup
                fieldId={"dmn-dev-sandbox-config-namespace"}
                label={i18n.terms.namespace}
                validated={isNamespaceValidated ? "success" : "error"}
                helperTextInvalid={i18n.dmnDevSandbox.common.requiredField}
              >
                <InputGroup>
                  <TextInput
                    autoFocus={true}
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="namespace-field"
                    name="namespace-field"
                    aria-label="namespace field"
                    value={config.namespace}
                    placeholder={i18n.dmnDevSandbox.configWizard.steps.first.namespacePlaceholder}
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
            <Text className="pf-u-my-md" component={TextVariants.p}>
              {i18n.dmnDevSandbox.configWizard.steps.first.inputReason}
            </Text>
          </div>
        ),
      },
      {
        id: WizardStepIds.CREDENTIALS,
        name: i18n.dmnDevSandbox.configWizard.steps.second.name,
        component: (
          <div>
            <Text component={TextVariants.p}>{i18n.dmnDevSandbox.configWizard.steps.second.introduction}</Text>
            <List className="pf-u-my-md" component={ListComponent.ol} type={OrderType.number}>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.dmnDevSandbox.configWizard.steps.second.accessLoginCommand}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.dmnDevSandbox.configWizard.steps.second.accessDisplayToken}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.dmnDevSandbox.configWizard.steps.second.copyInformation}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
            </List>
            <Form isHorizontal={true} className="pf-u-mt-md">
              <FormGroup
                fieldId={"dmn-dev-sandbox-config-host"}
                label={i18n.terms.host}
                validated={isHostValidated ? "success" : "error"}
                helperTextInvalid={i18n.dmnDevSandbox.common.requiredField}
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
                    value={config.host}
                    placeholder={i18n.dmnDevSandbox.configWizard.steps.second.hostPlaceholder}
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
                fieldId={"dmn-dev-sandbox-config-token"}
                label={i18n.terms.token}
                validated={isTokenValidated ? "success" : "error"}
                helperTextInvalid={i18n.dmnDevSandbox.common.requiredField}
              >
                <InputGroup>
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="token-field"
                    name="token-field"
                    aria-label="Token field"
                    value={config.token}
                    placeholder={i18n.dmnDevSandbox.configWizard.steps.second.tokenPlaceholder}
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
            <Text className="pf-u-my-md" component={TextVariants.p}>
              {i18n.dmnDevSandbox.configWizard.steps.second.inputReason}
            </Text>
          </div>
        ),
      },
      {
        id: WizardStepIds.CONNECT,
        name: i18n.dmnDevSandbox.configWizard.steps.final.name,
        component: (
          <>
            {isConnectLoading && (
              <div className="kogito--editor__dmn-dev-sandbox-wizard-loading-spinner">
                <Spinner isSVG size="xl" />
              </div>
            )}
            {!isConnectLoading && isConfigValidated && (
              <div>
                <Alert
                  variant={"default"}
                  isInline={true}
                  title={i18n.dmnDevSandbox.configWizard.steps.final.connectionSuccess}
                />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.dmnDevSandbox.configWizard.steps.final.introduction}
                </Text>
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.dmnDevSandbox.configWizard.steps.final.configNote}
                </Text>
              </div>
            )}
            {!isConnectLoading && !isConfigValidated && (
              <div>
                <Alert
                  variant={"danger"}
                  isInline={true}
                  title={i18n.dmnDevSandbox.configWizard.steps.final.connectionError}
                />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.dmnDevSandbox.configWizard.steps.final.connectionErrorLong}
                </Text>
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.dmnDevSandbox.configWizard.steps.final.possibleErrorReasons.introduction}
                </Text>
                <List className="pf-u-my-md">
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.dmnDevSandbox.configWizard.steps.final.possibleErrorReasons.emptyField}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.dmnDevSandbox.configWizard.steps.final.possibleErrorReasons.instanceExpired}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.dmnDevSandbox.configWizard.steps.final.possibleErrorReasons.tokenExpired}
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.dmnDevSandbox.configWizard.steps.final.checkInfo}
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
      config,
      onNamespaceInputChanged,
      onClearNamespace,
      isHostValidated,
      onHostInputChanged,
      onClearHost,
      isTokenValidated,
      onTokenInputChanged,
      onClearToken,
      isConnectLoading,
      isConfigValidated,
    ]
  );

  const wizardFooter = useMemo(
    () => (
      <WizardFooter>
        <WizardContextConsumer>
          {({ activeStep, goToStepByName, goToStepById, onNext, onBack }) => {
            if (activeStep.name !== i18n.dmnDevSandbox.configWizard.steps.final.name) {
              return (
                <>
                  <Button variant="primary" onClick={onNext}>
                    {i18n.terms.next}
                  </Button>
                  <Button
                    variant="secondary"
                    onClick={onBack}
                    isDisabled={activeStep.name === i18n.dmnDevSandbox.configWizard.steps.first.name}
                  >
                    {i18n.terms.back}
                  </Button>
                  <Button variant="link" onClick={onClose}>
                    {i18n.terms.cancel}
                  </Button>
                </>
              );
            }
            // Final step buttons
            return (
              <>
                <Button
                  id="dmn-dev-sandbox-config-continue-editing-button"
                  onClick={() => onFinish()}
                  isDisabled={!isConfigValidated}
                  variant={ButtonVariant.primary}
                  isLoading={isSaveLoading}
                  spinnerAriaValueText={isSaveLoading ? "Loading" : undefined}
                >
                  {isSaveLoading ? i18n.dmnDevSandbox.common.saving : i18n.terms.save}
                </Button>
                <Button variant="secondary" onClick={onBack}>
                  {i18n.terms.back}
                </Button>
                <Button variant="link" onClick={onClose}>
                  {i18n.terms.cancel}
                </Button>
              </>
            );
          }}
        </WizardContextConsumer>
      </WizardFooter>
    ),
    [i18n, isConfigValidated, isSaveLoading, onClose, onFinish]
  );

  return <Wizard steps={wizardSteps} footer={wizardFooter} onNext={onStepChanged} onGoToStep={onStepChanged} />;
}
