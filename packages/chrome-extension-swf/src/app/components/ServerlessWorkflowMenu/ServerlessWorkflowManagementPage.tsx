/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Page, PageSection, PageSectionVariants } from "@patternfly/react-core/dist/js/components/Page";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  isKafkaConfigValid,
  KafkaSettingsConfig,
  readKafkaConfigCookie,
  saveBootstrapServerCookie,
  saveClientIdCookie,
  saveClientSecretCookie,
  saveOAuthEndpointUriCookie,
  saveTopicCookie,
} from "../../kafka/KafkaSettingsConfig";
import { DeploymentWorkflow, useOpenShift } from "../../openshift/OpenShiftContext";
import {
  OpenShiftSettingsConfig,
  readOpenShiftConfigCookie,
  saveHostCookie,
  saveNamespaceCookie,
  saveProxyCookie,
  saveTokenCookie,
} from "../../openshift/OpenShiftSettingsConfig";
import { CreateServerlessWorkflowModal } from "./CreateServerlessWorkflowModal";

export function ServerlessWorkflowManagementPage() {
  const openshift = useOpenShift();
  const [openShiftConfig, setOpenShiftConfig] = useState<OpenShiftSettingsConfig>(readOpenShiftConfigCookie());
  const [kafkaConfig, setKafkaConfig] = useState<KafkaSettingsConfig>(readKafkaConfigCookie());
  const [configExpanded, setConfigExpanded] = React.useState(false);
  const [workflows, setWorkflows] = useState<DeploymentWorkflow[]>([]);
  const [showModal, setShowModal] = useState(false);

  const onClearProxy = useCallback(() => setOpenShiftConfig({ ...openShiftConfig, proxy: "" }), [openShiftConfig]);
  const onClearHost = useCallback(() => setOpenShiftConfig({ ...openShiftConfig, host: "" }), [openShiftConfig]);
  const onClearNamespace = useCallback(
    () => setOpenShiftConfig({ ...openShiftConfig, namespace: "" }),
    [openShiftConfig]
  );
  const onClearToken = useCallback(() => setOpenShiftConfig({ ...openShiftConfig, token: "" }), [openShiftConfig]);

  const onClearBootstraServer = useCallback(
    () => setKafkaConfig({ ...kafkaConfig, bootstrapServer: "" }),
    [kafkaConfig]
  );
  const onClearClientId = useCallback(() => setKafkaConfig({ ...kafkaConfig, clientId: "" }), [kafkaConfig]);
  const onClearClientSecret = useCallback(() => setKafkaConfig({ ...kafkaConfig, clientSecret: "" }), [kafkaConfig]);
  const onClearOAuthEndpointUri = useCallback(
    () => setKafkaConfig({ ...kafkaConfig, oauthEndpointUri: "" }),
    [kafkaConfig]
  );
  const onClearTopic = useCallback(() => setKafkaConfig({ ...kafkaConfig, topic: "" }), [kafkaConfig]);

  const onProxyChanged = useCallback(
    (newValue: string) => {
      setOpenShiftConfig({ ...openShiftConfig, proxy: newValue });
      saveProxyCookie(newValue);
    },
    [openShiftConfig]
  );

  const onHostChanged = useCallback(
    (newValue: string) => {
      setOpenShiftConfig({ ...openShiftConfig, host: newValue });
      saveHostCookie(newValue);
    },
    [openShiftConfig]
  );

  const onNamespaceChanged = useCallback(
    (newValue: string) => {
      setOpenShiftConfig({ ...openShiftConfig, namespace: newValue });
      saveNamespaceCookie(newValue);
    },
    [openShiftConfig]
  );

  const onTokenChanged = useCallback(
    (newValue: string) => {
      setOpenShiftConfig({ ...openShiftConfig, token: newValue });
      saveTokenCookie(newValue);
    },
    [openShiftConfig]
  );

  const onBootstrapServerChanged = useCallback(
    (newValue: string) => {
      setKafkaConfig({ ...kafkaConfig, bootstrapServer: newValue });
      saveBootstrapServerCookie(newValue);
    },
    [kafkaConfig]
  );

  const onClientIdChanged = useCallback(
    (newValue: string) => {
      setKafkaConfig({ ...kafkaConfig, clientId: newValue });
      saveClientIdCookie(newValue);
    },
    [kafkaConfig]
  );

  const onClientSecretChanged = useCallback(
    (newValue: string) => {
      setKafkaConfig({ ...kafkaConfig, clientSecret: newValue });
      saveClientSecretCookie(newValue);
    },
    [kafkaConfig]
  );

  const onOAuthEndpointUriChanged = useCallback(
    (newValue: string) => {
      setKafkaConfig({ ...kafkaConfig, oauthEndpointUri: newValue });
      saveOAuthEndpointUriCookie(newValue);
    },
    [kafkaConfig]
  );

  const onTopicChanged = useCallback(
    (newValue: string) => {
      setKafkaConfig({ ...kafkaConfig, topic: newValue });
      saveTopicCookie(newValue);
    },
    [kafkaConfig]
  );

  useEffect(() => {
    //TODO show a loading spinner instead of empty state while fetching workflows
    // openshift.fetchWorkflows(openShiftConfig).then(setWorkflows);
  }, [openShiftConfig, openshift]);

  const configContent = useMemo(
    () => (
      <ExpandableSection
        toggleText={"Connection Settings"}
        onToggle={() => setConfigExpanded(!configExpanded)}
        isExpanded={configExpanded}
      >
        <Form>
          <TextContent>
            <Text component={TextVariants.h3}>OpenShift Sandbox</Text>
          </TextContent>
          <FormGroup
            label={"Proxy URL"}
            labelIcon={
              <Popover bodyContent={"Proxy URL"}>
                <button
                  type="button"
                  aria-label="More info for proxy field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="proxy-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="proxy-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoFocus={true}
                autoComplete={"off"}
                isRequired
                type="text"
                id="proxy-field"
                name="proxy-field"
                aria-label="Proxy field"
                aria-describedby="proxy-field-helper"
                value={openShiftConfig.proxy}
                onChange={onProxyChanged}
                tabIndex={1}
                data-testid="proxy-text-field"
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear proxy button" onClick={onClearProxy}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <FormGroup
            label={"Namespace"}
            labelIcon={
              <Popover bodyContent={"Namespace"}>
                <button
                  type="button"
                  aria-label="More info for namespace field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="namespace-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="namespace-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoFocus={true}
                autoComplete={"off"}
                isRequired
                type="text"
                id="namespace-field"
                name="namespace-field"
                aria-label="Namespace field"
                aria-describedby="namespace-field-helper"
                value={openShiftConfig.namespace}
                onChange={onNamespaceChanged}
                tabIndex={2}
                data-testid="namespace-text-field"
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear namespace button" onClick={onClearNamespace}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <FormGroup
            label={"Host"}
            labelIcon={
              <Popover bodyContent={"Host"}>
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
                aria-label="Host field"
                aria-describedby="host-field-helper"
                value={openShiftConfig.host}
                onChange={onHostChanged}
                tabIndex={3}
                data-testid="host-text-field"
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear host button" onClick={onClearHost}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <FormGroup
            label={"Token"}
            labelIcon={
              <Popover bodyContent={"Token"}>
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
                aria-label="Token field"
                aria-describedby="token-field-helper"
                value={openShiftConfig.token}
                onChange={onTokenChanged}
                tabIndex={4}
                data-testid="token-text-field"
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear token button" onClick={onClearToken}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <TextContent>
            <Text component={TextVariants.h3}>Apache Kafka</Text>
          </TextContent>
          <FormGroup
            label={"Bootstrap Server"}
            labelIcon={
              <Popover bodyContent={"Bootstrap Server"}>
                <button
                  type="button"
                  aria-label="More info for bootstrap server field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="bootstrap-server-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="bootstrap-server-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoFocus={true}
                autoComplete={"off"}
                isRequired
                type="text"
                id="bootstrap-server-field"
                name="bootstrap-server-field"
                aria-label="Bootstrap server field"
                aria-describedby="bootstrap-server-field-helper"
                value={kafkaConfig.bootstrapServer}
                onChange={onBootstrapServerChanged}
                tabIndex={5}
                data-testid="bootstrap-server-text-field"
              />
              <InputGroupText>
                <Button
                  isSmall
                  variant="plain"
                  aria-label="Clear bootstrap server button"
                  onClick={onClearBootstraServer}
                >
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <FormGroup
            label={"Client ID"}
            labelIcon={
              <Popover bodyContent={"Client ID"}>
                <button
                  type="button"
                  aria-label="More info for client id field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="client-id-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="client-id-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoFocus={true}
                autoComplete={"off"}
                isRequired
                type="text"
                id="client-id-field"
                name="client-id-field"
                aria-label="Client ID field"
                aria-describedby="client-id-field-helper"
                value={kafkaConfig.clientId}
                onChange={onClientIdChanged}
                tabIndex={6}
                data-testid="client-id-text-field"
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear client id button" onClick={onClearClientId}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <FormGroup
            label={"Client Secret"}
            labelIcon={
              <Popover bodyContent={"Client Secret"}>
                <button
                  type="button"
                  aria-label="More info for client secret field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="client-secret-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="client-secret-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoComplete={"off"}
                isRequired
                type="text"
                id="client-secret-field"
                name="client-secret-field"
                aria-label="Client secret field"
                aria-describedby="client-secret-field-helper"
                value={kafkaConfig.clientSecret}
                onChange={onClientSecretChanged}
                tabIndex={7}
                data-testid="client-secret-text-field"
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear client secret button" onClick={onClearClientSecret}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <FormGroup
            label={"OAuth Endpoint URI"}
            labelIcon={
              <Popover bodyContent={"OAuth Endpoint URI"}>
                <button
                  type="button"
                  aria-label="More info for oauth endpoint uri field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="oauth-endpoint-uri-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="oauth-endpoint-uri-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoComplete={"off"}
                isRequired
                type="text"
                id="oauth-endpoint-uri-field"
                name="oauth-endpoint-uri-field"
                aria-label="OAuth endpoint uri field"
                aria-describedby="oauth-endpoint-uri-field-helper"
                value={kafkaConfig.oauthEndpointUri}
                onChange={onOAuthEndpointUriChanged}
                tabIndex={8}
                data-testid="oauth-endpoint-uri-text-field"
              />
              <InputGroupText>
                <Button
                  isSmall
                  variant="plain"
                  aria-label="Clear oauth endpoint uri button"
                  onClick={onClearOAuthEndpointUri}
                >
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
          <FormGroup
            label={"Topic"}
            labelIcon={
              <Popover bodyContent={"Topic"}>
                <button
                  type="button"
                  aria-label="More info for topic field"
                  onClick={(e) => e.preventDefault()}
                  aria-describedby="topic-field"
                  className="pf-c-form__group-label-help"
                >
                  <HelpIcon noVerticalAlign />
                </button>
              </Popover>
            }
            isRequired
            fieldId="topic-field"
          >
            <InputGroup className="pf-u-mt-sm">
              <TextInput
                autoComplete={"off"}
                isRequired
                type="text"
                id="topic-field"
                name="topic-field"
                aria-label="Topic field"
                aria-describedby="topic-field-helper"
                value={kafkaConfig.topic}
                onChange={onTopicChanged}
                tabIndex={8}
                data-testid="topic-text-field"
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear topic button" onClick={onClearTopic}>
                  <TimesIcon />
                </Button>
              </InputGroupText>
            </InputGroup>
          </FormGroup>
        </Form>
      </ExpandableSection>
    ),
    [
      configExpanded,
      openShiftConfig,
      onProxyChanged,
      onClearProxy,
      onNamespaceChanged,
      onClearNamespace,
      onHostChanged,
      onClearHost,
      onTokenChanged,
      onClearToken,
      kafkaConfig,
      onBootstrapServerChanged,
      onClearBootstraServer,
      onClientIdChanged,
      onClearClientId,
      onClientSecretChanged,
      onClearClientSecret,
      onOAuthEndpointUriChanged,
      onClearOAuthEndpointUri,
      onTopicChanged,
      onClearTopic,
    ]
  );

  const emptyState = useMemo(
    () => (
      <EmptyState>
        <EmptyStateIcon icon={PlusCircleIcon} />
        <Title headingLevel="h4" size="lg">
          Your deployed Serverless Workflows are shown here
        </Title>
        <EmptyStateBody>
          For help getting started, access the <a>quick start guide</a>.
        </EmptyStateBody>
        <Button variant="primary" onClick={() => setShowModal(true)}>
          Create Serverless Workflow
        </Button>
      </EmptyState>
    ),
    []
  );

  return (
    <>
      {showModal && (
        <CreateServerlessWorkflowModal
          isOpen={showModal}
          onClose={() => setShowModal(false)}
          openShiftConfig={openShiftConfig}
          kafkaConfig={isKafkaConfigValid(kafkaConfig) ? kafkaConfig : undefined}
        />
      )}
      <Page>
        <PageSection variant={PageSectionVariants.light}>
          <TextContent>
            <Text component={TextVariants.h1}>Serverless Workflow</Text>
          </TextContent>
        </PageSection>
        <PageSection variant={PageSectionVariants.light}>{configContent}</PageSection>
        <PageSection>
          {workflows.length === 0 ? (
            emptyState
          ) : (
            <div>{`# workflows ${workflows.length} - (TODO: workflow list + create workflow button go here)`}</div>
          )}
        </PageSection>
      </Page>
    </>
  );
}
