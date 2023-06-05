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

import React from "react";
import { QuickStartContext, QuickStartContextValues } from "@patternfly/quickstarts";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ActionGroup, Form, FormAlert, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { AddCircleOIcon } from "@patternfly/react-icons/dist/js/icons/add-circle-o-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { SETTINGS_PAGE_SECTION_TITLE } from "../SettingsContext";
import { useExtendedServices } from "../../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../../extendedServices/ExtendedServicesStatus";
import { setPageTitle } from "../../PageTitle";
import { QuickStartIds } from "../../quickstarts-data";
import { ExtendedServicesConfig, useSettings, useSettingsDispatch } from "../SettingsContext";
import { SettingsPageProps } from "../types";

const PAGE_TITLE = "Extended Services";

export function ExtendedServicesSettings(props: SettingsPageProps) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const extendedServices = useExtendedServices();
  const [config, setConfig] = useState(settings.extendedServices.config);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const qsContext = useContext<QuickStartContextValues>(QuickStartContext);

  const isCurrentConfigValid = useMemo(
    () => config.host.trim().length > 0 && config.buildUrl().trim().length > 0,
    [config]
  );

  const onClearHost = useCallback(() => setConfig(new ExtendedServicesConfig("", config.port)), [config]);
  const onClearPort = useCallback(() => setConfig(new ExtendedServicesConfig(config.host, "")), [config]);

  const onHostChanged = useCallback(
    (newValue: string) => setConfig(new ExtendedServicesConfig(newValue, config.port)),
    [config]
  );
  const onPortChanged = useCallback(
    (newValue: string) => setConfig(new ExtendedServicesConfig(config.host, newValue)),
    [config]
  );

  const onConnect = useCallback(() => {
    settingsDispatch.extendedServices.setConfig(config);
  }, [settingsDispatch.extendedServices, config]);

  const onReset = useCallback(() => {
    const emptyConfig = new ExtendedServicesConfig("", "");
    setConfig(emptyConfig);
    settingsDispatch.extendedServices.setConfig(emptyConfig);
  }, [settingsDispatch.extendedServices]);

  const handleModalToggle = useCallback(() => {
    setIsModalOpen((prevIsModalOpen) => !prevIsModalOpen);
  }, []);

  useEffect(() => {
    setPageTitle([SETTINGS_PAGE_SECTION_TITLE, PAGE_TITLE]);
  }, []);

  return (
    <Page>
      <PageSection variant={"light"} isWidthLimited>
        <TextContent>
          <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
          <Text component={TextVariants.p}>
            Data you provide here is necessary for proxying Serverless Logic Web Tools requests to OpenShift, thus
            making it possible to deploy models.
            <br /> All information is locally stored in your browser and never shared with anyone.
          </Text>
        </TextContent>
      </PageSection>

      <PageSection isFilled>
        <PageSection variant={"light"}>
          {extendedServices.status === ExtendedServicesStatus.RUNNING ? (
            <EmptyState>
              <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
              <TextContent>
                <Text component={"h2"}>You are connect to Extended Services.</Text>
              </TextContent>
              <EmptyStateBody>
                Deploying models is <b>enabled</b>.
                <br />
                <b>URL: </b>
                <i>{config.buildUrl()}</i>
                <br />
                <br />
                <Button variant={ButtonVariant.secondary} onClick={onReset}>
                  Reset
                </Button>
              </EmptyStateBody>
            </EmptyState>
          ) : (
            <>
              <EmptyState>
                <EmptyStateIcon icon={AddCircleOIcon} />
                <TextContent>
                  <Text component={"h2"}>You are not connected to Extended Services.</Text>
                </TextContent>
                <EmptyStateBody>
                  You currently have no Extended Services connections.{" "}
                  <a
                    onClick={() => {
                      extendedServices.setInstallTriggeredBy(undefined);
                      extendedServices.setModalOpen(true);
                    }}
                  >
                    Click to setup
                  </a>
                  <br />
                  <br />
                  <Button
                    variant={ButtonVariant.primary}
                    onClick={handleModalToggle}
                    data-testid="add-connection-button"
                  >
                    Add connection
                  </Button>
                </EmptyStateBody>
              </EmptyState>
            </>
          )}
        </PageSection>
      </PageSection>

      {props.pageContainerRef.current && (
        <Modal
          title="Add connection"
          isOpen={
            isModalOpen && extendedServices.status !== ExtendedServicesStatus.RUNNING && !extendedServices.isModalOpen
          }
          onClose={handleModalToggle}
          variant={ModalVariant.large}
          appendTo={props.pageContainerRef.current || document.body}
        >
          <Form>
            <FormAlert>
              <Alert
                variant="danger"
                title={
                  <Text>
                    You are not connected to Extended Services.{" "}
                    <a
                      onClick={() => {
                        extendedServices.setInstallTriggeredBy(undefined);
                        extendedServices.setModalOpen(true);
                      }}
                    >
                      Click to setup
                    </a>
                  </Text>
                }
                aria-live="polite"
                isInline
              />
            </FormAlert>
            <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
              <FormGroup
                label={"Host"}
                labelIcon={
                  <Popover bodyContent={"The host associated with the Extended Services URL instance."}>
                    <button
                      type="button"
                      aria-label="More info for host field"
                      onClick={(e) => e.preventDefault()}
                      aria-describedby="host-server-field"
                      className="pf-c-form__group-label-help"
                    >
                      <HelpIcon noVerticalAlign />
                    </button>
                  </Popover>
                }
                isRequired
                fieldId="host-server-field"
              >
                <InputGroup className="pf-u-mt-sm">
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="host-server-field"
                    name="host-server-field"
                    aria-label="Host field"
                    aria-describedby="host-server-field-helper"
                    value={config.host}
                    onChange={onHostChanged}
                    tabIndex={1}
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
                label={"Port"}
                labelIcon={
                  <Popover bodyContent={"The port number associated with the Extended Services URL instance."}>
                    <button
                      type="button"
                      aria-label="More info for port field"
                      onClick={(e) => e.preventDefault()}
                      aria-describedby="port-field"
                      className="pf-c-form__group-label-help"
                    >
                      <HelpIcon noVerticalAlign />
                    </button>
                  </Popover>
                }
                isRequired
                fieldId="port-field"
              >
                <InputGroup className="pf-u-mt-sm">
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="port-field"
                    name="port-field"
                    aria-label="Port field"
                    aria-describedby="port-field-helper"
                    value={config.port}
                    onChange={onPortChanged}
                    tabIndex={2}
                    data-testid="port-text-field"
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear port button" onClick={onClearPort}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
                <br />
                <Button
                  isInline={true}
                  key="quickstart"
                  variant="link"
                  onClick={() => {
                    qsContext.setActiveQuickStartID?.(QuickStartIds.OpenShiftIntegrationQuickStart);
                    setTimeout(
                      () => qsContext.setQuickStartTaskNumber?.(QuickStartIds.OpenShiftIntegrationQuickStart, 0),
                      0
                    );
                  }}
                >
                  Need help getting started? Follow our quickstart guide.
                </Button>
              </FormGroup>
              <ActionGroup>
                <Button
                  isDisabled={!isCurrentConfigValid}
                  id="extended-services-config-connect-button"
                  key="connect"
                  variant="primary"
                  onClick={onConnect}
                  data-testid="connect-config-button"
                >
                  Connect
                </Button>
              </ActionGroup>
            </PageSection>
          </Form>
        </Modal>
      )}
    </Page>
  );
}
