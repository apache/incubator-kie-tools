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
import { saveBootstrapServerCookie, saveTopicCookie } from "./KafkaSettingsConfig";
import { SettingsTabs } from "../SettingsDrawerBody";

export function ApacheKafkaSettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  const onClearBootstraServer = useCallback(
    () => settingsDispatch.apacheKafka.setConfig({ ...settings.apacheKafka.config, bootstrapServer: "" }),
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
            <Text component={TextVariants.h3}>Streams for Apache Kafka</Text>
          </TextContent>
          <TextContent>
            <Text component={TextVariants.small}>
              Data you provide here is necessary for connecting Serverless Workflow deployments with your Streams for
              Apache Kafka instance. All information is locally stored in your browser and never shared with anyone.
            </Text>
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
          <TextContent>
            <Text component={TextVariants.p}>
              <b>Note</b>: You must also provide{" "}
              <a onClick={() => settingsDispatch.open(SettingsTabs.SERVICE_ACCOUNT)}>Service Account</a> information so
              the connection with your Streams for Apache Kafka instance can be properly established.
            </Text>
          </TextContent>
        </Form>
      </PageSection>
    </Page>
  );
}
