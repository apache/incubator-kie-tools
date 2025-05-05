/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React from "react";
import { QuickStartContext, QuickStartContextValues } from "@patternfly/quickstarts";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { ActionGroup, Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText, InputGroupItem } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { AddCircleOIcon } from "@patternfly/react-icons/dist/js/icons/add-circle-o-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useCallback, useContext, useMemo, useState } from "react";
import { QuickStartIds } from "../../quickstarts-data";
import { useSettings, useSettingsDispatch } from "../SettingsContext";
import { SettingsPageContainer } from "../SettingsPageContainer";
import { SettingsPageProps } from "../types";
import { EMPTY_CONFIG, isServiceAccountConfigValid, resetConfigCookie, saveConfigCookie } from "./ServiceAccountConfig";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

const PAGE_TITLE = "Service Account";

export function ServiceAccountSettings(props: SettingsPageProps) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [config, setConfig] = useState(settings.serviceAccount.config);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const qsContext = useContext<QuickStartContextValues>(QuickStartContext);

  const handleModalToggle = useCallback(() => {
    setIsModalOpen((prevIsModalOpen) => !prevIsModalOpen);
  }, []);

  const isStoredConfigValid = useMemo(
    () => isServiceAccountConfigValid(settings.serviceAccount.config),
    [settings.serviceAccount.config]
  );

  const isCurrentConfigValid = useMemo(() => isServiceAccountConfigValid(config), [config]);

  const onClearClientId = useCallback(() => setConfig({ ...config, clientId: "" }), [config]);
  const onClearClientSecret = useCallback(() => setConfig({ ...config, clientSecret: "" }), [config]);

  const onClientIdChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, clientId: newValue });
    },
    [config]
  );

  const onClientSecretChanged = useCallback(
    (newValue: string) => {
      setConfig({ ...config, clientSecret: newValue });
    },
    [config]
  );

  const onReset = useCallback(() => {
    setConfig(EMPTY_CONFIG);
    settingsDispatch.serviceAccount.setConfig(EMPTY_CONFIG);
    resetConfigCookie();
  }, [settingsDispatch.serviceAccount]);

  const onApply = useCallback(() => {
    settingsDispatch.serviceAccount.setConfig(config);
    saveConfigCookie(config);
  }, [config, settingsDispatch.serviceAccount]);

  return (
    <SettingsPageContainer
      pageTitle={PAGE_TITLE}
      subtitle={
        <>
          Data you provide here is necessary for uploading Open API specs associated with models you design to your
          Service Registry instance.
          <br />
          All information is locally stored in your browser and never shared with anyone.
        </>
      }
    >
      <PageSection>
        <PageSection variant={"light"}>
          {isStoredConfigValid ? (
            <EmptyState>
              <EmptyStateHeader
                icon={<EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-v5-global--success-color--100)"} />}
              />
              <TextContent>
                <Text component={"h2"}>{"Your Service Account information is set."}</Text>
              </TextContent>
              <EmptyStateBody>
                Accessing your Service Registry is <b>enabled</b>.
                <br />
                <b>Client ID: </b>
                <i>{config.clientId}</i>
                <br />
                <b>Client secret: </b>
                <i>{obfuscate(config.clientSecret)}</i>
                <br />
                <br />
                <Button variant={ButtonVariant.tertiary} onClick={onReset}>
                  Reset
                </Button>
              </EmptyStateBody>
            </EmptyState>
          ) : (
            <EmptyState>
              <EmptyStateHeader icon={<EmptyStateIcon icon={AddCircleOIcon} />} />
              <TextContent>
                <Text component={"h2"}>No Service Accounts yet</Text>
              </TextContent>
              <EmptyStateBody>
                To get started, add a service account.
                <br />
                <br />
                <Button variant={ButtonVariant.primary} onClick={handleModalToggle} data-testid="add-connection-button">
                  Add service account
                </Button>
              </EmptyStateBody>
            </EmptyState>
          )}
        </PageSection>
      </PageSection>

      {props.pageContainerRef.current && (
        <Modal
          title="Add Service Account"
          isOpen={isModalOpen && !isStoredConfigValid}
          onClose={handleModalToggle}
          variant={ModalVariant.large}
          appendTo={props.pageContainerRef.current || document.body}
        >
          <Form>
            <FormGroup
              label={"Client ID"}
              labelIcon={
                <Popover bodyContent={"Client ID"}>
                  <button
                    type="button"
                    aria-label="More info for client id field"
                    onClick={(e) => e.preventDefault()}
                    aria-describedby="client-id-field"
                    className="pf-v5-c-form__group-label-help"
                  >
                    <Icon isInline>
                      <HelpIcon />
                    </Icon>
                  </button>
                </Popover>
              }
              isRequired
              fieldId="client-id-field"
            >
              <InputGroup className="pf-v5-u-mt-sm">
                <InputGroupItem isFill>
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="client-id-field"
                    name="client-id-field"
                    aria-label="Client ID field"
                    aria-describedby="client-id-field-helper"
                    value={config.clientId}
                    onChange={(_event, value) => onClientIdChanged(value)}
                    tabIndex={1}
                    data-testid="client-id-text-field"
                  />
                </InputGroupItem>
                <InputGroupText>
                  <Button size="sm" variant="plain" aria-label="Clear client id button" onClick={onClearClientId}>
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
                    className="pf-v5-c-form__group-label-help"
                  >
                    <Icon isInline>
                      <HelpIcon />
                    </Icon>
                  </button>
                </Popover>
              }
              isRequired
              fieldId="client-secret-field"
            >
              <InputGroup className="pf-v5-u-mt-sm">
                <InputGroupItem isFill>
                  <TextInput
                    autoComplete={"off"}
                    isRequired
                    type="text"
                    id="client-secret-field"
                    name="client-secret-field"
                    aria-label="Client secret field"
                    aria-describedby="client-secret-field-helper"
                    value={config.clientSecret}
                    onChange={(_event, value) => onClientSecretChanged(value)}
                    tabIndex={2}
                    data-testid="client-secret-text-field"
                  />
                </InputGroupItem>
                <InputGroupText>
                  <Button
                    size="sm"
                    variant="plain"
                    aria-label="Clear client secret button"
                    onClick={onClearClientSecret}
                  >
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
                  qsContext.setActiveQuickStartID?.(QuickStartIds.ApplicationServicesIntegrationQuickStart);
                  setTimeout(
                    () =>
                      qsContext.setQuickStartTaskNumber?.(QuickStartIds.ApplicationServicesIntegrationQuickStart, 0),
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
                id="service-account-config-apply-button"
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
    </SettingsPageContainer>
  );
}

export function obfuscate(token?: string) {
  if (!token) {
    return undefined;
  }

  if (token.length <= 10) {
    return new Array(10).join("*");
  }

  const stars = new Array(token.length - 4).join("*");
  const pieceToObfuscate = token.substring(0, token.length - 4);
  return token.replace(pieceToObfuscate, stars);
}
