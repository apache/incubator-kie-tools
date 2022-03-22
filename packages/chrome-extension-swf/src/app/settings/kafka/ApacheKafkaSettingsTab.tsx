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

import * as React from "react";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { useCallback } from "react";
import { useSettings, useSettingsDispatch } from "../SettingsContext";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import {
  saveBootstrapServerCookie,
  saveClientIdCookie,
  saveClientSecretCookie,
  saveTopicCookie,
} from "./KafkaSettingsConfig";

export function ApacheKafkaSettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  const onClearBootstraServer = useCallback(
    () => settingsDispatch.apacheKafka.setConfig({ ...settings.apacheKafka.config, bootstrapServer: "" }),
    [settings.apacheKafka.config, settingsDispatch.apacheKafka]
  );
  const onClearClientId = useCallback(
    () => settingsDispatch.apacheKafka.setConfig({ ...settings.apacheKafka.config, clientId: "" }),
    [settings.apacheKafka.config, settingsDispatch.apacheKafka]
  );
  const onClearClientSecret = useCallback(
    () => settingsDispatch.apacheKafka.setConfig({ ...settings.apacheKafka.config, clientSecret: "" }),
    [settings.apacheKafka.config, settingsDispatch.apacheKafka]
  );
  const onClearTopic = useCallback(
    () => settingsDispatch.apacheKafka.setConfig({ ...settings.apacheKafka.config, topic: "" }),
    [settings.apacheKafka.config, settingsDispatch.apacheKafka]
  );

  const onBootstrapServerChanged = useCallback(
    (newValue: string) => {
      settingsDispatch.apacheKafka.setConfig({ ...settings.apacheKafka.config, bootstrapServer: newValue });
      saveBootstrapServerCookie(newValue);
    },
    [settings.apacheKafka.config, settingsDispatch.apacheKafka]
  );

  const onClientIdChanged = useCallback(
    (newValue: string) => {
      settingsDispatch.apacheKafka.setConfig({ ...settings.apacheKafka.config, clientId: newValue });
      saveClientIdCookie(newValue);
    },
    [settings.apacheKafka.config, settingsDispatch.apacheKafka]
  );

  const onClientSecretChanged = useCallback(
    (newValue: string) => {
      settingsDispatch.apacheKafka.setConfig({ ...settings.apacheKafka.config, clientSecret: newValue });
      saveClientSecretCookie(newValue);
    },
    [settings.apacheKafka.config, settingsDispatch.apacheKafka]
  );

  const onTopicChanged = useCallback(
    (newValue: string) => {
      settingsDispatch.apacheKafka.setConfig({ ...settings.apacheKafka.config, topic: newValue });
      saveTopicCookie(newValue);
    },
    [settings.apacheKafka.config, settingsDispatch.apacheKafka]
  );

  return (
    <Page>
      <PageSection>
        <Form>
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
                value={settings.apacheKafka.config.bootstrapServer}
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
                value={settings.apacheKafka.config.clientId}
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
                value={settings.apacheKafka.config.clientSecret}
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
                value={settings.apacheKafka.config.topic}
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
      </PageSection>
    </Page>
  );
}

export function obfuscate(token?: string) {
  if (!token) {
    return undefined;
  }

  if (token.length <= 8) {
    return token;
  }

  const stars = new Array(token.length - 8).join("*");
  const pieceToObfuscate = token.substring(4, token.length - 4);
  return token.replace(pieceToObfuscate, stars);
}
