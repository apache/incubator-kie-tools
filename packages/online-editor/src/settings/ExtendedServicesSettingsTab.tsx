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

import * as React from "react";
import { useCallback, useState } from "react";
import { ActionGroup, Form, FormAlert, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupItem } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useSettings, useSettingsDispatch } from "./SettingsContext";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { useExtendedServices } from "../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../extendedServices/ExtendedServicesStatus";

export function ExtendedServicesSettingsTab() {
  const { settings } = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const extendedServices = useExtendedServices();
  const [host, setHost] = useState(settings.extendedServices.host);
  const [port, setPort] = useState(settings.extendedServices.port);

  const onSubmit = useCallback(
    (e: any) => {
      e.preventDefault();
      settingsDispatch.set((settings) => {
        settings.extendedServices.host = host;
        settings.extendedServices.port = port;
      });
    },
    [host, port, settingsDispatch]
  );

  return (
    <>
      <PageSection>
        <PageSection variant={"light"} isFilled={true}>
          <Form onSubmit={onSubmit}>
            <FormAlert>
              {extendedServices.status === ExtendedServicesStatus.RUNNING && (
                <Alert variant="success" title={"You are connected to Extended Services"} aria-live="polite" isInline />
              )}
              {extendedServices.status !== ExtendedServicesStatus.RUNNING && (
                <Alert
                  variant="danger"
                  title={"You are not connected to Extended Services"}
                  aria-live="polite"
                  isInline
                />
              )}
            </FormAlert>
            <FormGroup isRequired={true} label={"Host"} fieldId={"host-input"}>
              <InputGroup>
                <InputGroupItem isFill>
                  <TextInput
                    id="host-input"
                    name="host"
                    aria-describedby="host-text-input-helper"
                    placeholder={""}
                    validated={"default"}
                    value={host}
                    onChange={(_event, val) => setHost(val)}
                    autoFocus={true}
                  />
                </InputGroupItem>
              </InputGroup>
            </FormGroup>
            <FormGroup isRequired={false} label={"Port"} fieldId={"port-input"}>
              <InputGroup>
                <InputGroupItem isFill>
                  <TextInput
                    id="port-input"
                    name="port"
                    aria-describedby="port-text-input-helper"
                    placeholder={""}
                    validated={"default"}
                    value={port}
                    onChange={(_event, val) => setPort(val)}
                    autoFocus={true}
                  />
                </InputGroupItem>
              </InputGroup>
            </FormGroup>
            <ActionGroup>
              <Button
                id="extended-services-config-save-button"
                key="save"
                variant="primary"
                onClick={onSubmit}
                data-testid="save-config-button"
              >
                Change
              </Button>
            </ActionGroup>
          </Form>
        </PageSection>
      </PageSection>
    </>
  );
}
