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
import {
  EMPTY_CONFIG,
  isServiceRegistryConfigValid,
  resetConfigCookie,
  saveConfigCookie,
} from "./ServiceRegistryConfig";

export function ServiceRegistrySettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [config, setConfig] = useState(settings.serviceRegistry.config);
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();

  const isExtendedServicesRunning = useMemo(
    () => kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING,
    [kieSandboxExtendedServices.status]
  );

  const isStoredConfigValid = useMemo(
    () => isExtendedServicesRunning && isServiceRegistryConfigValid(settings.serviceRegistry.config),
    [isExtendedServicesRunning, settings.serviceRegistry.config]
  );

  const isCurrentConfigValid = useMemo(
    () => isExtendedServicesRunning && isServiceRegistryConfigValid(config),
    [isExtendedServicesRunning, config]
  );

  const onClearName = useCallback(() => setConfig({ ...config, name: "" }), [config]);

  const onClearCoreRegistryApi = useCallback(() => setConfig({ ...config, coreRegistryApi: "" }), [config]);

  const onNameChanged = useCallback((newValue: string) => setConfig({ ...config, name: newValue }), [config]);

  const onCoreRegistryApiChanged = useCallback(
    (newValue: string) => setConfig({ ...config, coreRegistryApi: newValue }),
    [config]
  );

  const onReset = useCallback(() => {
    setConfig(EMPTY_CONFIG);
    settingsDispatch.serviceRegistry.setConfig(EMPTY_CONFIG);
    resetConfigCookie();
  }, [settingsDispatch.serviceRegistry]);

  const onApply = useCallback(() => {
    settingsDispatch.serviceRegistry.setConfig(config);
    saveConfigCookie(config);
  }, [config, settingsDispatch.serviceRegistry]);

  return (
    <Page>
      <PageSection>
        {isStoredConfigValid ? (
          <EmptyState>
            <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
            <TextContent>
              <Text component={"h2"}>{"Your Service Registry information is set."}</Text>
            </TextContent>
            <EmptyStateBody>
              <TextContent>
                Uploading OpenAPI specs when deploying models is <b>enabled</b>.
              </TextContent>
              <br />
              <TextContent>
                <b>Service Registry Name: </b>
                <i>{config.name}</i>
              </TextContent>
              <br />
              <TextContent>
                <b>Core Registry Api: </b>
                <i>{config.coreRegistryApi}</i>
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
                        before configuring your Service Registry instance
                      </Text>
                    }
                    aria-live="polite"
                    isInline
                  />
                </FormAlert>
              )}
              <TextContent>
                <Text component={TextVariants.h3}>Service Registry</Text>
              </TextContent>
              <TextContent>
                <Text component={TextVariants.small}>
                  Data you provide here is necessary for uploading Open API specs associated with models you design to
                  your Service Registry instance. All information is locally stored in your browser and never shared
                  with anyone.
                </Text>
              </TextContent>
              <FormGroup
                label={"Name"}
                labelIcon={
                  <Popover
                    bodyContent={
                      "Name to identify your Service Registry instance across the Serverless Logic Web Tools."
                    }
                  >
                    <button
                      type="button"
                      aria-label="More info for name field"
                      onClick={(e) => e.preventDefault()}
                      aria-describedby="name-field"
                      className="pf-c-form__group-label-help"
                    >
                      <HelpIcon noVerticalAlign />
                    </button>
                  </Popover>
                }
                isRequired
                fieldId="name-field"
              >
                <InputGroup className="pf-u-mt-sm">
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="name-field"
                    name="name-field"
                    aria-label="Name field"
                    aria-describedby="name-field-helper"
                    value={config.name}
                    onChange={onNameChanged}
                    tabIndex={1}
                    data-testid="name-text-field"
                  />
                  <InputGroupText>
                    <Button isSmall variant="plain" aria-label="Clear name button" onClick={onClearName}>
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
              <FormGroup
                label={"Core Registry API"}
                labelIcon={
                  <Popover bodyContent={"Core Registry API URL associated with your Service Registry instance."}>
                    <button
                      type="button"
                      aria-label="More info for core registry api field"
                      onClick={(e) => e.preventDefault()}
                      aria-describedby="core-registry-api-field"
                      className="pf-c-form__group-label-help"
                    >
                      <HelpIcon noVerticalAlign />
                    </button>
                  </Popover>
                }
                isRequired
                fieldId="core-registry-api-field"
              >
                <InputGroup className="pf-u-mt-sm">
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="core-registry-api-field"
                    name="core-registry-api-field"
                    aria-label="Core Registry API field"
                    aria-describedby="core-registry-api-field-helper"
                    value={config.coreRegistryApi}
                    onChange={onCoreRegistryApiChanged}
                    tabIndex={2}
                    data-testid="core-registry-api-text-field"
                  />
                  <InputGroupText>
                    <Button
                      isSmall
                      variant="plain"
                      aria-label="Clear core registry api button"
                      onClick={onClearCoreRegistryApi}
                    >
                      <TimesIcon />
                    </Button>
                  </InputGroupText>
                </InputGroup>
              </FormGroup>
              <TextContent>
                <Text component={TextVariants.p}>
                  <b>Note</b>: You must also provide{" "}
                  <a onClick={() => settingsDispatch.open(SettingsTabs.SERVICE_ACCOUNT)}>Service Account</a> information
                  so the connection with your Service Registry instance can be properly established.
                </Text>
              </TextContent>
              <ActionGroup>
                <Button
                  isDisabled={!isCurrentConfigValid}
                  id="service-registry-config-apply-button"
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
