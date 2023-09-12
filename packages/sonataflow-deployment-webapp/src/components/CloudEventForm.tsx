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
import { CodeEditor, Language } from "@patternfly/react-code-editor/dist/js/components/CodeEditor";
import { ActionList, ActionListItem } from "@patternfly/react-core/dist/js/components/ActionList";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import ExclamationCircleIcon from "@patternfly/react-icons/dist/esm/icons/exclamation-circle-icon";
import React, { useCallback, useState } from "react";
import { CloudEventFormDriver, CloudEventMethod, CloudEventRequest } from "../api";
import CloudEventFieldLabelIcon from "./CloudEventFieldLabelIcon";
import { FormValidations, validateCloudEventRequest } from "./validateCloudEventRequest";

export type CloudEventFormDefaultValues = {
  cloudEventSource: string;
  instanceId?: string;
};

export interface CloudEventFormProps {
  driver: CloudEventFormDriver;
  defaultValues: CloudEventFormDefaultValues;
}

export function CloudEventForm(props: CloudEventFormProps) {
  const { driver, defaultValues } = props;
  const [validationState, setValidationState] = useState<FormValidations>();

  const [isMethodOpen, setIsMethodOpen] = useState<boolean>(false);
  const [method, setMethod] = useState<CloudEventMethod>(CloudEventMethod.POST);
  const [endpoint, setEndpoint] = useState<string>("/");
  const [eventType, setEventType] = useState<string>("");
  const [eventSource, setEventSource] = useState<string>(defaultValues.cloudEventSource);
  const [eventData, setEventData] = useState<string>("");

  const resetForm = useCallback(() => {
    setMethod(CloudEventMethod.POST);
    setEndpoint("/");
    setEventType("");
    setEventSource(defaultValues.cloudEventSource);
    setEventData("");
  }, [defaultValues]);

  const getValidationMessage = useCallback(
    (fieldId: string): string | undefined => {
      return validationState && validationState.getFieldValidation(fieldId);
    },
    [validationState]
  );

  const getValidatedOption = useCallback(
    (fieldId: string): ValidatedOptions => {
      return getValidationMessage(fieldId) ? ValidatedOptions.error : ValidatedOptions.default;
    },
    [getValidationMessage]
  );

  const doTrigger = useCallback(() => {
    const extensions: { [key: string]: string } = {};

    const eventRequest: CloudEventRequest = {
      endpoint: endpoint,
      method: method,
      data: eventData,
      headers: {
        type: eventType,
        source: eventSource,
        extensions,
      },
    };

    const validations = validateCloudEventRequest(eventRequest);

    setValidationState(validations);

    if (!validations.isValid()) {
      return;
    }

    driver.triggerCloudEvent(eventRequest).then(() => {
      resetForm();
    });
  }, [resetForm, driver, method, endpoint, eventType, eventSource, eventData]);

  return (
    <div>
      <Form isHorizontal>
        <FormGroup
          label={"Event Endpoint"}
          isRequired
          fieldId={"endpoint"}
          helperTextInvalid={getValidationMessage("endpoint")}
          helperTextInvalidIcon={<ExclamationCircleIcon />}
          validated={getValidatedOption("endpoint")}
          labelIcon={
            <CloudEventFieldLabelIcon
              fieldId={"endpoint"}
              helpMessage={"Sets the endpoint and method where the CloudEvent should be triggered."}
            />
          }
        >
          <InputGroup>
            <Select
              id={"method"}
              width={"100px"}
              variant={SelectVariant.single}
              selections={method}
              onSelect={(_event, value: string) => {
                setMethod(CloudEventMethod[value as keyof typeof CloudEventMethod] ?? CloudEventMethod.POST);
                setIsMethodOpen(false);
              }}
              isOpen={isMethodOpen}
              onToggle={() => setIsMethodOpen(!isMethodOpen)}
            >
              <SelectOption value={CloudEventMethod.POST} />
              <SelectOption value={CloudEventMethod.PUT} />
            </Select>
            <TextInput
              id={"endpoint"}
              isRequired
              value={endpoint}
              onChange={setEndpoint}
              validated={getValidatedOption("endpoint")}
            />
          </InputGroup>
        </FormGroup>
        <FormGroup
          label="Event Type"
          isRequired
          fieldId="eventType"
          helperTextInvalid={getValidationMessage("eventType")}
          helperTextInvalidIcon={<ExclamationCircleIcon />}
          validated={getValidatedOption("eventType")}
          labelIcon={
            <CloudEventFieldLabelIcon
              fieldId={"eventType"}
              helpMessage={"Sets the type of the cloud event."}
              cloudEventHeader={"type"}
            />
          }
        >
          <TextInput
            value={eventType}
            isRequired
            type="text"
            id="eventType"
            onChange={setEventType}
            validated={getValidatedOption("eventType")}
          />
        </FormGroup>
        <FormGroup
          label="Event Data"
          fieldId="eventData"
          isHelperTextBeforeField
          helperTextInvalid={getValidationMessage("eventData")}
          helperTextInvalidIcon={<ExclamationCircleIcon />}
          validated={getValidatedOption("eventData")}
          labelIcon={
            <CloudEventFieldLabelIcon
              fieldId={"eventData"}
              helpMessage={"Sets the content of the cloud event in JSON format."}
            />
          }
        >
          <CodeEditor
            isDarkTheme={false}
            isLineNumbersVisible={true}
            isReadOnly={false}
            isCopyEnabled={false}
            isMinimapVisible={true}
            isLanguageLabelVisible={false}
            code={eventData}
            language={Language.json}
            height="300px"
            onChange={setEventData}
          />
        </FormGroup>
        <ActionList>
          <ActionListItem>
            <Button key={"triggerCloudEventButton"} variant="primary" onClick={doTrigger}>
              Trigger
            </Button>
          </ActionListItem>
          <ActionListItem>
            <Button key={"resetCloudEventFormButton"} variant="secondary" onClick={resetForm}>
              Reset
            </Button>
          </ActionListItem>
        </ActionList>
      </Form>
    </div>
  );
}
