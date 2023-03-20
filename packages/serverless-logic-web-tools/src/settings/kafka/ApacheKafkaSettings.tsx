/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { QuickStartContext, QuickStartContextValues } from "@patternfly/quickstarts";
import {
  ActionGroup,
  Alert,
  Button,
  ButtonVariant,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  Form,
  FormAlert,
  FormGroup,
  InputGroup,
  InputGroupText,
  Modal,
  ModalVariant,
  Page,
  PageSection,
  Popover,
  Text,
  TextContent,
  TextInput,
  TextVariants,
} from "@patternfly/react-core/dist/js/components";
import { AddCircleOIcon, CheckCircleIcon, HelpIcon, TimesIcon } from "@patternfly/react-icons/dist/js/icons";
import { useCallback, useContext, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { routes } from "../../navigation/Routes";
import { QuickStartIds } from "../../quickstarts-data";
import { useSettings, useSettingsDispatch } from "../SettingsContext";
import { SettingsPageProps } from "../types";
import { EMPTY_CONFIG, isKafkaConfigValid, resetConfigCookie, saveConfigCookie } from "./KafkaSettingsConfig";

export function ApacheKafkaSettings(props: SettingsPageProps) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [config, setConfig] = useState(settings.apacheKafka.config);
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const qsContext = useContext<QuickStartContextValues>(QuickStartContext);

  const handleModalToggle = useCallback(() => {
    setIsModalOpen((prevIsModalOpen) => !prevIsModalOpen);
  }, []);

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
      <PageSection variant={"light"} isWidthLimited>
        <TextContent>
          <Text component={TextVariants.h1}>Streams for Apache Kafka</Text>
          <Text component={TextVariants.p}>
            Data you provide here is necessary for connecting serverless deployments with your Streams for Apache Kafka
            instance through a KafkaSource.
            <br /> All information is locally stored in your browser and never shared with anyone.
          </Text>
        </TextContent>
      </PageSection>

      <PageSection>
        {!isExtendedServicesRunning && (
          <>
            <Alert
              variant="danger"
              title={
                <Text>
                  Connect to{" "}
                  <Link to={routes.settings.kie_sandbox_extended_services.path({})}>KIE Sandbox Extended Services</Link>{" "}
                  before configuring your Streams for Apache Kafka instance
                </Text>
              }
              aria-live="polite"
              isInline
            >
              KIE Sandbox Extended Services is necessary for connecting serverless deployments with your Streams for
              Apache Kafka instance through a KafkaSource.
            </Alert>
            <br />
          </>
        )}
        <PageSection variant={"light"}>
          {isStoredConfigValid ? (
            <EmptyState>
              <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
              <TextContent>
                <Text component={"h2"}>{"Your Streams for Apache Kafka information is set."}</Text>
              </TextContent>
              <EmptyStateBody>
                Deploying models with a KafkaSource attached to the service is <b>enabled</b>.
                <br />
                <b>Bootstrap server: </b>
                <i>{config.bootstrapServer}</i>
                <br />
                <b>Topic: </b>
                <i>{config.topic}</i>
                <br />
                <br />
                <Button variant={ButtonVariant.tertiary} onClick={onReset}>
                  Reset
                </Button>
              </EmptyStateBody>
            </EmptyState>
          ) : (
            <EmptyState>
              <EmptyStateIcon icon={AddCircleOIcon} />
              <TextContent>
                <Text component={"h2"}>No Streams for Apache Kafka information yet</Text>
              </TextContent>
              <EmptyStateBody>
                To get started, add a Streams for Apache Kafka information.
                <br />
                <br />
                <Button variant={ButtonVariant.primary} onClick={handleModalToggle} data-testid="add-connection-button">
                  Add Streams for Apache Kafka
                </Button>
              </EmptyStateBody>
            </EmptyState>
          )}
        </PageSection>
      </PageSection>

      {props.pageContainerRef.current && (
        <Modal
          title="Add Streams for Apache Kafka"
          isOpen={
            isModalOpen &&
            kieSandboxExtendedServices.status !== KieSandboxExtendedServicesStatus.STOPPED &&
            !isStoredConfigValid
          }
          onClose={handleModalToggle}
          variant={ModalVariant.large}
          appendTo={props.pageContainerRef.current || document.body}
        >
          <Form>
            {!isExtendedServicesRunning && (
              <FormAlert>
                <Alert
                  variant="danger"
                  title={
                    <Text>
                      Connect to{" "}
                      <Link to={routes.settings.kie_sandbox_extended_services.path({})}>
                        KIE Sandbox Extended Services
                      </Link>{" "}
                      before configuring your Streams for Apache Kafka instance
                    </Text>
                  }
                  aria-live="polite"
                  isInline
                />
              </FormAlert>
            )}
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
                <Link to={routes.settings.service_account.path({})}>Service Account</Link> so the connection with your
                Streams for Apache Kafka instance can be properly established.
              </Text>
              <br />
              <Button
                isInline={true}
                key="quickstart"
                variant="link"
                onClick={() => {
                  qsContext.setActiveQuickStartID?.(QuickStartIds.ApplicationServicesIntegrationQuickStart);
                  setTimeout(
                    () =>
                      qsContext.setQuickStartTaskNumber?.(QuickStartIds.ApplicationServicesIntegrationQuickStart, 2),
                    0
                  );
                }}
              >
                Need help getting started? Follow our quickstart guide.
              </Button>
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
        </Modal>
      )}
    </Page>
  );
}
