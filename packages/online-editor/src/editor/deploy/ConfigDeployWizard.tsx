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

import { I18nHtml } from "@kogito-tooling/i18n/dist/react-components";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { List, ListComponent, ListItem, OrderType } from "@patternfly/react-core/dist/js/components/List";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Wizard, WizardContextConsumer, WizardFooter } from "@patternfly/react-core/dist/js/components/Wizard";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { ConnectionConfig, isConfigValid, isHostValid, isTokenValid, isUsernameValid } from "./ConnectionConfig";
import { useDeploy } from "./DeployContext";
import { DEVELOPER_SANDBOX_GET_STARTED_URL } from "./devsandbox/DeveloperSandboxService";

enum WizardStepIds {
  USERNAME = "USERNAME",
  CREDENTIALS = "CREDENTIALS",
  CONNECT = "CONNECT",
}

enum FinishOperation {
  DEPLOY_NOW = "DEPLOY_NOW",
  CONTINUE_EDITING = "CONTINUE_EDITING",
}

export function ConfigDeployWizard() {
  const deployContext = useDeploy();
  const { i18n } = useOnlineI18n();
  const [config, setConfig] = useState(deployContext.currentConfig);
  const [isConfigValidated, setConfigValidated] = useState(false);
  const [isDeployLoading, setDeployLoading] = useState(false);
  const [isContinueEditingLoading, setContinueEditingLoading] = useState(false);
  const [isConnectLoading, setConnectLoading] = useState(false);

  const onClearHost = useCallback(() => setConfig({ ...config, host: "" }), [config]);
  const onClearUsername = useCallback(() => setConfig({ ...config, username: "" }), [config]);
  const onClearToken = useCallback(() => setConfig({ ...config, token: "" }), [config]);

  const isUsernameValidated = useMemo(() => {
    return isUsernameValid(config.username);
  }, [config.username]);

  const isHostValidated = useMemo(() => {
    return isHostValid(config.host);
  }, [config.host]);

  const isTokenValidated = useMemo(() => {
    return isTokenValid(config.token);
  }, [config.token]);

  useEffect(() => {
    setConfigValidated(isConfigValid(config));
  }, [config]);

  useEffect(() => {
    setConfig(deployContext.currentConfig);
  }, [deployContext.currentConfig]);

  const resetUI = useCallback((config: ConnectionConfig) => {
    setConfigValidated(false);
    setDeployLoading(false);
    setContinueEditingLoading(false);
    setConnectLoading(false);
    setConfig(config);
  }, []);

  const onWizardClose = useCallback(() => {
    deployContext.setConfigWizardOpen(false);
    resetUI(deployContext.currentConfig);
  }, [deployContext, resetUI]);

  const onUsernameInputChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, username: newValue });
    },
    [config]
  );

  const onHostInputChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, host: newValue });
    },
    [config]
  );

  const onTokenInputChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, token: newValue });
    },
    [config]
  );

  const onStepChanged = useCallback(
    async ({ id, name }, { prevId, prevName }) => {
      if (id === WizardStepIds.CONNECT) {
        setConnectLoading(true);
        setConfigValidated(await deployContext.onCheckConfig(config, false));
        setConnectLoading(false);
      }
    },
    [config, deployContext]
  );

  const onFinish = useCallback(
    async (operation: FinishOperation) => {
      if (isDeployLoading || isContinueEditingLoading) {
        return;
      }

      setDeployLoading(operation === FinishOperation.DEPLOY_NOW);
      setContinueEditingLoading(operation === FinishOperation.CONTINUE_EDITING);

      const isConfigOk = await deployContext.onCheckConfig(config, true);
      setConfigValidated(isConfigOk);

      if (isConfigOk && operation === FinishOperation.DEPLOY_NOW) {
        await deployContext.onDeploy(config);
      }

      setDeployLoading(false);
      setContinueEditingLoading(false);

      if (!isConfigOk) {
        return;
      }

      deployContext.setConfigWizardOpen(false);
      resetUI(config);
    },
    [config, deployContext, isContinueEditingLoading, isDeployLoading, resetUI]
  );

  const wizardSteps = useMemo(
    () => [
      {
        id: WizardStepIds.USERNAME,
        name: i18n.deploy.configWizard.steps.first.name,
        component: (
          <div>
            <Text component={TextVariants.p}>{i18n.deploy.configWizard.steps.first.introduction}</Text>
            <List component={ListComponent.ol} type={OrderType.number} className="pf-u-mt-md">
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <a href={DEVELOPER_SANDBOX_GET_STARTED_URL} target={"_blank"}>
                      {i18n.deploy.configWizard.steps.first.goToGetStartedPage}
                      <ExternalLinkAltIcon className="pf-u-mx-sm" />
                    </a>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>{i18n.deploy.configWizard.steps.first.followSteps}</Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>{i18n.deploy.configWizard.steps.first.informUsername}</Text>
                </TextContent>
              </ListItem>
            </List>
            <Form isHorizontal={true} className="pf-u-mt-md">
              <FormGroup
                fieldId={"deploy-config-username"}
                label={i18n.terms.username}
                validated={isUsernameValidated ? "success" : "error"}
                helperTextInvalid={i18n.deploy.common.requiredField}
              >
                <InputGroup>
                  <TextInput
                    autoFocus={true}
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="username-field"
                    name="username-field"
                    aria-describedby="username-field"
                    value={config.username}
                    placeholder={i18n.deploy.configWizard.steps.first.usernamePlaceholder}
                    onChange={onUsernameInputChanged}
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear username button" onClick={onClearUsername}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
            </Form>
            <Text className="pf-u-my-md" component={TextVariants.p}>
              {i18n.deploy.configWizard.steps.first.inputReason}
            </Text>
          </div>
        ),
      },
      {
        id: WizardStepIds.CREDENTIALS,
        name: i18n.deploy.configWizard.steps.second.name,
        component: (
          <div>
            <Text component={TextVariants.p}>{i18n.deploy.configWizard.steps.second.introduction}</Text>
            <List className="pf-u-my-md" component={ListComponent.ol} type={OrderType.number}>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.deploy.configWizard.steps.second.accessLoginCommand}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.deploy.configWizard.steps.second.accessDisplayToken}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
              <ListItem>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <I18nHtml>{i18n.deploy.configWizard.steps.second.copyInformation}</I18nHtml>
                  </Text>
                </TextContent>
              </ListItem>
            </List>
            <Form isHorizontal={true} className="pf-u-mt-md">
              <FormGroup
                fieldId={"deploy-config-host"}
                label={i18n.terms.host}
                validated={isHostValidated ? "success" : "error"}
                helperTextInvalid={i18n.deploy.common.requiredField}
              >
                <InputGroup>
                  <TextInput
                    autoFocus={true}
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="host-field"
                    name="host-field"
                    value={config.host}
                    placeholder={i18n.deploy.configWizard.steps.second.hostPlaceholder}
                    onChange={onHostInputChanged}
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear host button" onClick={onClearHost}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
              <FormGroup
                fieldId={"deploy-config-token"}
                label={i18n.terms.token}
                validated={isTokenValidated ? "success" : "error"}
                helperTextInvalid={i18n.deploy.common.requiredField}
              >
                <InputGroup>
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="token-field"
                    name="token-field"
                    value={config.token}
                    placeholder={i18n.deploy.configWizard.steps.second.tokenPlaceholder}
                    onChange={onTokenInputChanged}
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
              {i18n.deploy.configWizard.steps.second.inputReason}
            </Text>
          </div>
        ),
      },
      {
        id: WizardStepIds.CONNECT,
        name: i18n.deploy.configWizard.steps.final.name,
        component: (
          <>
            {isConnectLoading && (
              <div className="kogito--editor__deploy-wizard-loading-spinner">
                <Spinner isSVG size="xl" />
              </div>
            )}
            {!isConnectLoading && isConfigValidated && (
              <div>
                <Alert
                  variant={"default"}
                  isInline={true}
                  title={i18n.deploy.configWizard.steps.final.connectionSuccess}
                />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.deploy.configWizard.steps.final.introduction}
                </Text>
                <List className="pf-u-my-md">
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nHtml>{i18n.deploy.configWizard.steps.final.deployNowExplanation}</I18nHtml>
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        <I18nHtml>{i18n.deploy.configWizard.steps.final.continueEditingExplanation}</I18nHtml>
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.deploy.configWizard.steps.final.configNote}
                </Text>
              </div>
            )}
            {!isConnectLoading && !isConfigValidated && (
              <div>
                <Alert
                  variant={"danger"}
                  isInline={true}
                  title={i18n.deploy.configWizard.steps.final.connectionError}
                />
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.deploy.configWizard.steps.final.connectionErrorLong}
                </Text>
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.deploy.configWizard.steps.final.possibleErrorReasons.introduction}
                </Text>
                <List className="pf-u-my-md">
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.deploy.configWizard.steps.final.possibleErrorReasons.emptyField}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.deploy.configWizard.steps.final.possibleErrorReasons.instanceExpired}
                      </Text>
                    </TextContent>
                  </ListItem>
                  <ListItem>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {i18n.deploy.configWizard.steps.final.possibleErrorReasons.tokenExpired}
                      </Text>
                    </TextContent>
                  </ListItem>
                </List>
                <Text className="pf-u-mt-md" component={TextVariants.p}>
                  {i18n.deploy.configWizard.steps.final.checkInfo}
                </Text>
              </div>
            )}
          </>
        ),
      },
    ],
    [
      i18n,
      isUsernameValidated,
      config,
      onUsernameInputChanged,
      onClearUsername,
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
          {({ activeStep, goToStepByName, goToStepById, onNext, onBack, onClose }) => {
            if (activeStep.name !== i18n.deploy.configWizard.steps.final.name) {
              return (
                <>
                  <Button variant="primary" onClick={onNext}>
                    {i18n.terms.next}
                  </Button>
                  <Button
                    variant="secondary"
                    onClick={onBack}
                    isDisabled={activeStep.name === i18n.deploy.configWizard.steps.first.name}
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
                  onClick={() => onFinish(FinishOperation.DEPLOY_NOW)}
                  isDisabled={!isConfigValidated}
                  isLoading={isDeployLoading}
                  spinnerAriaValueText={isDeployLoading ? "Loading" : undefined}
                >
                  {isDeployLoading ? i18n.deploy.common.deploying : i18n.deploy.configWizard.footer.deployNow}
                </Button>
                <Button
                  onClick={() => onFinish(FinishOperation.CONTINUE_EDITING)}
                  isDisabled={!isConfigValidated}
                  variant="secondary"
                  isLoading={isContinueEditingLoading}
                  spinnerAriaValueText={isContinueEditingLoading ? "Loading" : undefined}
                >
                  {isContinueEditingLoading
                    ? i18n.deploy.common.saving
                    : i18n.deploy.configWizard.footer.continueEditing}
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
    [i18n, isConfigValidated, isDeployLoading, isContinueEditingLoading, onFinish]
  );

  return (
    <Modal
      title={i18n.deploy.common.deployInstanceInfo}
      description={i18n.deploy.common.disclaimer}
      isOpen={deployContext.isConfigWizardOpen}
      variant={ModalVariant.large}
      aria-label={"Steps to configure the deploy instance"}
      onClose={onWizardClose}
    >
      <Wizard
        onClose={onWizardClose}
        steps={wizardSteps}
        height={480}
        footer={wizardFooter}
        onNext={onStepChanged}
        onGoToStep={onStepChanged}
      />
    </Modal>
  );
}
