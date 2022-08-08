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
import { ActionGroup, Form, FormAlert, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
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
import { ExtendedServicesConfig, useSettings, useSettingsDispatch } from "../SettingsContext";

export function KieSandboxExtendedServicesSettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const [config, setConfig] = useState(settings.kieSandboxExtendedServices.config);

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
    settingsDispatch.kieSandboxExtendedServices.setConfig(config);
  }, [settingsDispatch.kieSandboxExtendedServices, config]);

  const onReset = useCallback(() => {
    const emptyConfig = new ExtendedServicesConfig("", "");
    setConfig(emptyConfig);
    settingsDispatch.kieSandboxExtendedServices.setConfig(emptyConfig);
  }, [settingsDispatch.kieSandboxExtendedServices]);

  return (
    <>
      <Page>
        <PageSection>
          {kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING ? (
            <EmptyState>
              <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
              <TextContent>
                <Text component={"h2"}>{"You are connect to the KIE Sandbox Extended Services."}</Text>
              </TextContent>
              <EmptyStateBody>
                <TextContent>
                  Deploying models is <b>enabled</b>.
                </TextContent>
                <br />
                <TextContent>
                  <b>URL: </b>
                  <i>{config.buildUrl()}</i>
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
                <FormAlert>
                  <Alert
                    variant="danger"
                    title={
                      <Text>
                        You are not connected to KIE Sandbox Extended Services.{" "}
                        <a
                          onClick={() => {
                            kieSandboxExtendedServices.setInstallTriggeredBy(undefined);
                            kieSandboxExtendedServices.setModalOpen(true);
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
                <TextContent>
                  <Text component={TextVariants.h3}>KIE Sandbox Extended Services</Text>
                </TextContent>
                <TextContent>
                  <Text component={TextVariants.small}>
                    Data you provide here is necessary for proxying Serverless Logic Web Tools requests to OpenShift,
                    thus making it possible to deploy models. All information is locally stored in your browser and
                    never shared with anyone.
                  </Text>
                </TextContent>
                <FormGroup
                  label={"Host"}
                  labelIcon={
                    <Popover bodyContent={"The host associated with the KIE Sandbox Extended Services URL instance."}>
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
                    <Popover
                      bodyContent={"The port number associated with the KIE Sandbox Extended Services URL instance."}
                    >
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
                </FormGroup>
                <ActionGroup>
                  <Button
                    isDisabled={!isCurrentConfigValid}
                    id="kie-sandbox-extended-services-config-connect-button"
                    key="connect"
                    variant="primary"
                    onClick={onConnect}
                    data-testid="connect-config-button"
                  >
                    Connect
                  </Button>
                </ActionGroup>
              </Form>
            </PageSection>
          )}
        </PageSection>
      </Page>
    </>
  );
}
