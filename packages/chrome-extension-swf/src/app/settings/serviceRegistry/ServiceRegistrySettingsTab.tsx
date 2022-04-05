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
import { saveCoreRegistryApiCookie } from "./ServiceRegistryConfig";

export function ServiceRegistrySettingsTab() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  const onClearCoreRegistryApi = useCallback(
    () => settingsDispatch.serviceRegistry.setConfig({ ...settings.serviceRegistry.config, coreRegistryApi: "" }),
    [settings.serviceRegistry.config, settingsDispatch.serviceRegistry]
  );

  const onCoreRegistryApiChanged = useCallback(
    (newValue: string) => {
      settingsDispatch.serviceRegistry.setConfig({ ...settings.serviceRegistry.config, coreRegistryApi: newValue });
      saveCoreRegistryApiCookie(newValue);
    },
    [settings.serviceRegistry.config, settingsDispatch.serviceRegistry]
  );
  return (
    <Page>
      <PageSection>
        <Form>
          <TextContent>
            <Text component={TextVariants.h3}>Service Registry</Text>
          </TextContent>
          <TextContent>
            <Text component={TextVariants.small}>
              The information you provide here is necessary for uploading Open API specs to your Service Registry
              instance.
            </Text>
          </TextContent>
          <FormGroup
            label={"Core Registry Api"}
            labelIcon={
              <Popover bodyContent={"Core Registry Api"}>
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
                value={settings.serviceRegistry.config.coreRegistryApi}
                onChange={onCoreRegistryApiChanged}
                tabIndex={6}
                data-testid="core-registry-api-text-field"
              />
              <InputGroupText>
                <Button isSmall variant="plain" aria-label="Clear client id button" onClick={onClearCoreRegistryApi}>
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
