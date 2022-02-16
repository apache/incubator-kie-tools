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
import { DeploymentWorkflow, useOpenShift } from "../../openshift/OpenShiftContext";
import {
  OpenShiftSettingsConfig,
  readConfigCookie,
  saveHostCookie,
  saveNamespaceCookie,
  saveProxyCookie,
  saveTokenCookie,
} from "../../openshift/OpenShiftSettingsConfig";
import { CreateServerlessWorkflowModal } from "./CreateServerlessWorkflowModal";

export function ServerlessWorkflowManagementPage() {
  const openshift = useOpenShift();
  const [config, setConfig] = useState<OpenShiftSettingsConfig>(readConfigCookie());
  const [configExpanded, setConfigExpanded] = React.useState(false);
  const [workflows, setWorkflows] = useState<DeploymentWorkflow[]>([]);
  const [showModal, setShowModal] = useState(false);

  const onClearProxy = useCallback(() => setConfig({ ...config, proxy: "" }), [config]);
  const onClearHost = useCallback(() => setConfig({ ...config, host: "" }), [config]);
  const onClearNamespace = useCallback(() => setConfig({ ...config, namespace: "" }), [config]);
  const onClearToken = useCallback(() => setConfig({ ...config, token: "" }), [config]);

  const onProxyChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, proxy: newValue });
      saveProxyCookie(newValue);
    },
    [config]
  );

  const onHostChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, host: newValue });
      saveHostCookie(newValue);
    },
    [config]
  );

  const onNamespaceChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, namespace: newValue });
      saveNamespaceCookie(newValue);
    },
    [config]
  );

  const onTokenChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, token: newValue });
      saveTokenCookie(newValue);
    },
    [config]
  );

  useEffect(() => {
    openshift.fetchWorkflows(config).then(setWorkflows);
  }, [config, openshift]);

  const configContent = useMemo(
    () => (
      <ExpandableSection
        toggleText={"Connection Settings"}
        onToggle={() => setConfigExpanded(!configExpanded)}
        isExpanded={configExpanded}
      >
        <Form>
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
                value={config.proxy}
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
                value={config.namespace}
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
                value={config.host}
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
                value={config.token}
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
        </Form>
      </ExpandableSection>
    ),
    [
      config,
      configExpanded,
      onClearHost,
      onClearNamespace,
      onClearProxy,
      onClearToken,
      onHostChanged,
      onNamespaceChanged,
      onProxyChanged,
      onTokenChanged,
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
        <CreateServerlessWorkflowModal isOpen={showModal} onClose={() => setShowModal(false)} config={config} />
      )}
      <Page>
        <PageSection variant={PageSectionVariants.light}>
          <TextContent>
            <Text component={TextVariants.h1}>Serverless Workflow</Text>
          </TextContent>
        </PageSection>
        <PageSection variant={PageSectionVariants.light}>{configContent}</PageSection>
        <PageSection>{workflows.length === 0 ? emptyState : <div>workflows {workflows.length}</div>}</PageSection>
      </Page>
    </>
  );
}
