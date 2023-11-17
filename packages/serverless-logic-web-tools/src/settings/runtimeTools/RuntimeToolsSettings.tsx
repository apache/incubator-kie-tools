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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ActionGroup, Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { AddCircleOIcon } from "@patternfly/react-icons/dist/js/icons/add-circle-o-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useCallback, useMemo, useState } from "react";
import { useSettings, useSettingsDispatch } from "../SettingsContext";
import { SettingsPageContainer } from "../SettingsPageContainer";
import { SettingsPageProps } from "../types";
import {
  EMPTY_CONFIG,
  RuntimeToolsSettingsConfig,
  isRuntimeToolsConfigValid,
  resetConfigCookie,
  saveConfigCookie,
} from "./RuntimeToolsConfig";
import { removeTrailingSlashFromUrl } from "../../url";

const PAGE_TITLE = "Runtime Tools";

export function RuntimeToolsSettings(props: SettingsPageProps) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [config, setConfig] = useState(settings.runtimeTools.config);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleModalToggle = useCallback(() => {
    setIsModalOpen((prevIsModalOpen) => !prevIsModalOpen);
  }, []);

  const isStoredConfigValid = useMemo(
    () => isRuntimeToolsConfigValid(settings.runtimeTools.config),
    [settings.runtimeTools.config]
  );

  const isCurrentConfigValid = useMemo(() => isRuntimeToolsConfigValid(config), [config]);

  const onClearDataIndexUrl = useCallback(() => setConfig({ ...config, dataIndexUrl: "" }), [config]);

  const onClearKogitoServiceUrl = useCallback(() => setConfig({ ...config, kogitoServiceUrl: "" }), [config]);

  const onDataIndexURLChanged = useCallback(
    (newValue: string) => setConfig({ ...config, dataIndexUrl: newValue }),
    [config]
  );

  const onKogitoServiceUrlChanged = useCallback(
    (newValue: string) => setConfig({ ...config, kogitoServiceUrl: newValue }),
    [config]
  );

  const onReset = useCallback(() => {
    setConfig(EMPTY_CONFIG);
    settingsDispatch.runtimeTools.setConfig(EMPTY_CONFIG);
    resetConfigCookie();
  }, [settingsDispatch.runtimeTools]);

  const onApply = useCallback(() => {
    const newConfig: RuntimeToolsSettingsConfig = {
      dataIndexUrl: removeTrailingSlashFromUrl(config.dataIndexUrl),
      kogitoServiceUrl: removeTrailingSlashFromUrl(config.kogitoServiceUrl),
    };
    setConfig(newConfig);
    settingsDispatch.runtimeTools.setConfig(newConfig);
    saveConfigCookie(newConfig);
  }, [config, settingsDispatch.runtimeTools]);

  return (
    <SettingsPageContainer
      pageTitle={PAGE_TITLE}
      subtitle={
        <>
          Data you provide here is necessary for starting new workflows and fetching their data.
          <br />
          All information is locally stored in your browser and never shared with anyone.
        </>
      }
    >
      <PageSection>
        <PageSection variant={"light"}>
          {isStoredConfigValid ? (
            <EmptyState>
              <EmptyStateIcon icon={CheckCircleIcon} color={"var(--pf-global--success-color--100)"} />
              <TextContent>
                <Text component={"h2"}>{"Your Runtime Tools connection information is set."}</Text>
              </TextContent>
              <EmptyStateBody>
                Runtime Tools are <b>enabled</b>.
                <br />
                <b>Data Index URL: </b>
                <i>{config.dataIndexUrl}</i>
                <br />
                <b>SonataFlow Service URL: </b>
                <i>{config.kogitoServiceUrl}</i>
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
                <Text component={"h2"}>No Runtime Tools connection yet</Text>
              </TextContent>
              <EmptyStateBody>
                To get started, add the Runtime Tools connection information.
                <br />
                <br />
                <Button variant={ButtonVariant.primary} onClick={handleModalToggle} data-testid="add-connection-button">
                  Add runtime tools information
                </Button>
              </EmptyStateBody>
            </EmptyState>
          )}
        </PageSection>
      </PageSection>

      {props.pageContainerRef.current && (
        <Modal
          title="Add Runtime Tools information"
          isOpen={isModalOpen && !isStoredConfigValid}
          onClose={handleModalToggle}
          variant={ModalVariant.large}
          appendTo={props.pageContainerRef.current || document.body}
        >
          <Form>
            <FormGroup
              label={"Data Index URL"}
              labelIcon={
                <Popover bodyContent={"Data Index URL associated with your running Kogito runtime service."}>
                  <button
                    type="button"
                    aria-label="More info for Data Index URL field"
                    onClick={(e) => e.preventDefault()}
                    aria-describedby="data-index-url-field"
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
                  id="data-index-url-field"
                  name="data-index-url-field"
                  aria-label="Data Index URL field"
                  aria-describedby="data-index-url-field-helper"
                  value={config.dataIndexUrl}
                  onChange={onDataIndexURLChanged}
                  tabIndex={1}
                  data-testid="data-index-url-text-field"
                />
                <InputGroupText>
                  <Button
                    isSmall
                    variant="plain"
                    aria-label="Clear Data Index URL button"
                    onClick={onClearDataIndexUrl}
                  >
                    <TimesIcon />
                  </Button>
                </InputGroupText>
              </InputGroup>
            </FormGroup>
            <FormGroup
              label={"SonataFlow Service URL"}
              labelIcon={
                <Popover bodyContent={"URL associated with your running SonataFlow runtime service."}>
                  <button
                    type="button"
                    aria-label="More info for SonataFlow Service URL field"
                    onClick={(e) => e.preventDefault()}
                    aria-describedby="kogito-service-url-field"
                    className="pf-c-form__group-label-help"
                  >
                    <HelpIcon noVerticalAlign />
                  </button>
                </Popover>
              }
              isRequired
              fieldId="kogito-service-url-field"
            >
              <InputGroup className="pf-u-mt-sm">
                <TextInput
                  autoComplete={"off"}
                  isRequired
                  type="text"
                  id="kogito-service-url-field"
                  name="kogito-service-url-field"
                  aria-label="SonataFlow Service URL field"
                  aria-describedby="kogito-service-url-field-helper"
                  value={config.kogitoServiceUrl}
                  onChange={onKogitoServiceUrlChanged}
                  tabIndex={2}
                  data-testid="kogito-service-url-text-field"
                />
                <InputGroupText>
                  <Button
                    isSmall
                    variant="plain"
                    aria-label="Clear SonataFlow Service URL button"
                    onClick={onClearKogitoServiceUrl}
                  >
                    <TimesIcon />
                  </Button>
                </InputGroupText>
              </InputGroup>
            </FormGroup>
            <ActionGroup>
              <Button
                isDisabled={!isCurrentConfigValid}
                id="runtime-tools-config-apply-button"
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
