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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ActionGroup, Form, FormGroup, FormAlert } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useSettings, useSettingsDispatch } from "../SettingsContext";
import { SettingsTabs } from "../SettingsModalBody";
import { EMPTY_CONFIG, isKafkaConfigValid, resetConfigCookie, saveConfigCookie } from "./KafkaSettingsConfig";

export function ApacheKafkaSettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [config, setConfig] = useState(settings.apacheKafka.config);
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();

  const isExtendedServicesRunning = useMemo(
    () => kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING,
    [kieSandboxExtendedServices.status]
  );

  const isStoredConfigValid = useMemo(
    () => isExtendedServicesRunning && isKafkaConfigValid(settings.apacheKafka.config),
    [isExtendedServicesRunning, settings.apacheKafka.config]
  );

  const isCurrentConfigValid = useMemo(
    () => isExtendedServicesRunning && isKafkaConfigValid(config),
    [isExtendedServicesRunning, config]
  );

  const onClearBootstraServer = useCallback(() => setConfig({ ...config, bootstrapServer: "" }), [config]);
  const onClearTopic = useCallback(() => setConfig({ ...config, topic: "" }), [config]);

  const onBootstrapServerChanged = useCallback(
    (newValue: string) => setConfig({ ...config, bootstrapServer: newValue }),
    [config]
  );

  const onTopicChanged = useCallback((newValue: string) => setConfig({ ...config, topic: newValue }), [config]);

  const onReset = useCallback(() => {
    setConfig(EMPTY_CONFIG);
    settingsDispatch.apacheKafka.setConfig(EMPTY_CONFIG);
    resetConfigCookie();
  }, [settingsDispatch.apacheKafka]);

  const onApply = useCallback(() => {
    settingsDispatch.apacheKafka.setConfig(config);
    saveConfigCookie(config);
  }, [config, settingsDispatch.apacheKafka]);

  return (
    <Page>
      <PageSection>
        {isStoredConfigValid ? (
          <EmptyState>
            <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
            <TextContent>
              <Text component={"h2"}>{"Your Streams for Apache Kafka information is set."}</Text>
            </TextContent>
            <EmptyStateBody>
              <TextContent>
                Deploying models with a KafkaSource attached to the service is <b>enabled</b>.
              </TextContent>
              <br />
              <TextContent>
                <b>Bootstrap server: </b>
                <i>{config.bootstrapServer}</i>
              </TextContent>
              <TextContent>
                <b>Topic: </b>
                <i>{config.topic}</i>
              </TextContent>
              <br />
              <Button variant={ButtonVariant.tertiary} onClick={onReset}>
                Reset
              </Button>
            </EmptyStateBody>
          </EmptyState>
        ) : (
          <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
            <Form>
              {!isExtendedServicesRunning && (
                <FormAlert>
                  <Alert
                    variant="danger"
                    title={
                      <Text>
                        Connect to{" "}
                        <a onClick={() => settingsDispatch.open(SettingsTabs.KIE_SANDBOX_EXTENDED_SERVICES)}>
                          KIE Sandbox Extended Services
                        </a>{" "}
                        before configuring your Streams for Apache Kafka instance
                      </Text>
                    }
                    aria-live="polite"
                    isInline
                  />
                </FormAlert>
              )}
              <TextContent>
                <Text component={TextVariants.h3}>Streams for Apache Kafka</Text>
              </TextContent>
              <TextContent>
                <Text component={TextVariants.small}>
                  Data you provide here is necessary for connecting serverless deployments with your Streams for Apache
                  Kafka instance through a KafkaSource. All information is locally stored in your browser and never
                  shared with anyone.
                </Text>
              </TextContent>
              <FormGroup
                label={"Bootstrap Server"}
                labelIcon={
                  <Popover bodyContent={"The bootstrap server associated with your Streams for Apache Kafka instance."}>
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
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="bootstrap-server-field"
                    name="bootstrap-server-field"
                    aria-label="Bootstrap server field"
                    aria-describedby="bootstrap-server-field-helper"
                    value={config.bootstrapServer}
                    onChange={onBootstrapServerChanged}
                    tabIndex={1}
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
                label={"Topic"}
                labelIcon={
                  <Popover bodyContent={"The topic that messages will flow in."}>
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
                    value={config.topic}
                    onChange={onTopicChanged}
                    tabIndex={2}
                    data-testid="topic-text-field"
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear topic button" onClick={onClearTopic}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
              <TextContent>
                <Text component={TextVariants.p}>
                  <b>Note</b>: You must also provide{" "}
                  <a onClick={() => settingsDispatch.open(SettingsTabs.SERVICE_ACCOUNT)}>Service Account</a> information
                  so the connection with your Streams for Apache Kafka instance can be properly established.
                </Text>
              </TextContent>
              <ActionGroup>
                <Button
                  isDisabled={!isCurrentConfigValid}
                  id="apache-kafka-config-apply-button"
                  key="save"
                  variant="primary"
                  onClick={onApply}
                  data-testid="apply-config-button"
                >
                  Apply
                </Button>
              </ActionGroup>
            </Form>
          </PageSection>
        )}
      </PageSection>
    </Page>
  );
}
