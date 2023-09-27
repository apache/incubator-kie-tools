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

import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { CloudEventFormDefaultValues, CloudEventFormDriver } from "../../../api";
import { ActionListGroup } from "@patternfly/react-core/dist/js/components/ActionList";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import ExclamationCircleIcon from "@patternfly/react-icons/dist/esm/icons/exclamation-circle-icon";
import { FormValidations, validateCloudEventRequest } from "./validateCloudEventRequest";
import CloudEventCustomHeadersEditor, {
  CloudEventCustomHeadersEditorApi,
} from "../CloudEventCustomHeadersEditor/CloudEventCustomHeadersEditor";
import CloudEventFieldLabelIcon from "../CloudEventFieldLabelIcon/CloudEventFieldLabelIcon";
import {
  RequestDataEditor,
  RequestDataEditorApi,
} from "@kie-tools/runtime-tools-components/dist/components/RequestDataEditor";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import {
  CloudEventMethod,
  KOGITO_BUSINESS_KEY,
  KOGITO_PROCESS_REFERENCE_ID,
} from "@kie-tools/runtime-tools-gateway-api/dist/types";

export interface CloudEventFormProps {
  driver: CloudEventFormDriver;
  isNewInstanceEvent?: boolean;
  defaultValues?: CloudEventFormDefaultValues;
}

export const CloudEventForm: React.FC<CloudEventFormProps & OUIAProps> = ({
  driver,
  isNewInstanceEvent,
  defaultValues,
  ouiaId,
  ouiaSafe,
}) => {
  const [validationState, setValidationState] = useState<FormValidations>();

  const customHeadersEditorApi = useRef<CloudEventCustomHeadersEditorApi>(null);
  const requestDataEditorRef = useRef<RequestDataEditorApi>(null);

  const [isMethodOpen, setIsMethodOpen] = useState<boolean>(false);
  const [method, setMethod] = useState<CloudEventMethod>(CloudEventMethod.POST);
  const [endpoint, setEndpoint] = useState<string>("/");
  const [instanceId, setInstanceId] = useState<string>("");
  const [businessKey, setBusinessKey] = useState<string>("");
  const [eventType, setEventType] = useState<string>("");
  const [eventSource, setEventSource] = useState<string>("/from/form");
  const [eventData, setEventData] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const resetForm = useCallback(() => {
    setMethod(CloudEventMethod.POST);
    setEndpoint("/");
    setEventType("");
    setEventSource(defaultValues?.cloudEventSource ?? "/from/form");
    setEventData("");
    setInstanceId(defaultValues?.instanceId ?? "");
    setBusinessKey("");
    customHeadersEditorApi?.current?.reset();
    requestDataEditorRef.current?.setContent("");
  }, [defaultValues]);

  useEffect(() => {
    setEventSource(defaultValues?.cloudEventSource ?? "/from/form");
    setInstanceId(defaultValues?.instanceId ?? "");
  }, [defaultValues]);

  const getValidatedOption = useCallback(
    (fieldId: string): ValidatedOptions => {
      return getValidationMessage(fieldId) ? ValidatedOptions.error : ValidatedOptions.default;
    },
    [validationState]
  );

  const getValidationMessage = useCallback(
    (fieldId: string): string | undefined => {
      return validationState && validationState.getFieldValidation(fieldId);
    },
    [validationState]
  );

  const doTrigger = useCallback(() => {
    const extensions = {
      ...customHeadersEditorApi?.current?.getCustomHeaders(),
    };

    if (isNewInstanceEvent) {
      businessKey && (extensions[KOGITO_BUSINESS_KEY] = businessKey);
    } else {
      instanceId && (extensions[KOGITO_PROCESS_REFERENCE_ID] = instanceId);
    }

    const eventRequest = {
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

    setIsLoading(true);
    driver
      .triggerCloudEvent(eventRequest)
      .then((response: any) => {
        resetForm();
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, [method, endpoint, eventType, eventSource, eventData, instanceId, businessKey]);

  const requestDataEditor = useMemo(() => {
    return (
      <RequestDataEditor
        ref={requestDataEditorRef}
        content={""}
        onContentChange={(args) => setEventData(args.content)}
        isReadOnly={false}
      />
    );
  }, [setEventData]);

  if (isLoading) {
    return (
      <Bullseye>
        <KogitoSpinner spinnerText="Triggering cloud event..." ouiaId="cloud-event-form-loading" />
      </Bullseye>
    );
  }

  return (
    <div {...componentOuiaProps(ouiaId, "workflow-form", ouiaSafe)}>
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
              onSelect={(event, value: "POST" | "PUT") => {
                setMethod(CloudEventMethod[value] ?? CloudEventMethod.POST);
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
          label="Event Source"
          labelIcon={
            <CloudEventFieldLabelIcon
              fieldId={"eventSource"}
              helpMessage={"Sets the source of the cloud event."}
              cloudEventHeader={"source"}
            />
          }
          fieldId="eventSource"
        >
          <TextInput value={eventSource} isRequired type="text" id="eventSource" onChange={setEventSource} />
        </FormGroup>
        {!isNewInstanceEvent && (
          <FormGroup
            label="Instance Id"
            fieldId="instanceId"
            labelIcon={
              <CloudEventFieldLabelIcon
                fieldId={"instanceId"}
                helpMessage={"Sets the Service Workflow instance Id the cloud event will interact with."}
                cloudEventHeader={"kogitoprocrefid"}
              />
            }
          >
            <TextInput value={instanceId} isRequired type="text" id="instanceId" onChange={setInstanceId} />
          </FormGroup>
        )}
        {isNewInstanceEvent && (
          <FormGroup
            label="Business Key"
            fieldId="businessKey"
            labelIcon={
              <CloudEventFieldLabelIcon
                fieldId={"businessKey"}
                helpMessage={"Sets the Business Key for the Serverless Workflow instance started by the cloud event."}
                cloudEventHeader={"kogitobusinesskey"}
              />
            }
          >
            <TextInput value={businessKey} isRequired type="text" id="businessKey" onChange={setBusinessKey} />
          </FormGroup>
        )}
        <FormGroup
          label="Event Custom Headers"
          fieldId="customHeaders"
          labelIcon={
            <CloudEventFieldLabelIcon
              fieldId={"customHeaders"}
              helpMessage={
                <div>
                  <p>Sets the custom headers that will be added into the Cloud Event.</p>
                  <p>
                    Press the <span className="pf-u-link-color">Add Header</span> button to start adding new headers.
                  </p>
                  <p>Headers with empty Name won&apos;t be added into the Cloud Event.</p>
                </div>
              }
            />
          }
        >
          <CloudEventCustomHeadersEditor ref={customHeadersEditorApi} />
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
          {requestDataEditor}
        </FormGroup>
        <ActionListGroup>
          <Button key={"triggerCloudEventButton"} variant="primary" onClick={doTrigger}>
            Trigger
          </Button>
          <Button key={"resetCloudEventFormButton"} variant="secondary" onClick={resetForm}>
            Reset
          </Button>
        </ActionListGroup>
      </Form>
    </div>
  );
};

export default CloudEventForm;
