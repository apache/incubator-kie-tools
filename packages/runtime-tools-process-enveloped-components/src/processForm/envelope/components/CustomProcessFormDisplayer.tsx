/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useEffect, useRef, useState } from "react";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { v4 as uuidv4 } from "uuid";
import { ProcessFormChannelApi } from "../../api";
import { FormAction } from "@kie-tools/runtime-tools-components/dist/utils";
import { FormFooter } from "@kie-tools/runtime-tools-components/dist/components/FormFooter";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { Card, CardBody, CardFooter } from "@patternfly/react-core/dist/js/components/Card";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import {
  EmbeddedFormDisplayer,
  FormDisplayerApi,
  FormOpened,
  FormOpenedState,
  FormSubmitResponseType,
} from "../../../formDisplayer";

export interface CustomProcessFormDisplayerProps {
  schema: Record<string, any>;
  customForm: Form;
  channelApi: MessageBusClientApi<ProcessFormChannelApi>;
  targetOrigin: string;
  processDefinition: ProcessDefinition;
  envelopePath?: string;
}

const CustomProcessFormDisplayer: React.FC<CustomProcessFormDisplayerProps> = ({
  customForm,
  channelApi,
  targetOrigin,
  processDefinition,
  envelopePath = "resources/forms-displayer.html",
}) => {
  const formDisplayerApiRef = useRef<FormDisplayerApi>(null);
  const [formUUID] = useState<string>(uuidv4());
  const [formData] = useState({});
  const [formActions, setFormActions] = useState<FormAction[]>([]);
  const [formOpened, setFormOpened] = useState<FormOpened>();

  useEffect(() => {
    setFormActions([
      {
        name: "Start",
        execute: () => {
          const formDisplayerApi = formDisplayerApiRef.current!;
          formDisplayerApi
            .startSubmit({
              params: {},
            })
            .then((formOutput) => channelApi.requests.processForm__startProcess(processDefinition, formOutput))
            .then((response) => {
              formDisplayerApi.notifySubmitResult({
                type: FormSubmitResponseType.SUCCESS,
                info: response,
              });
            })
            .catch((error) => {
              formDisplayerApi.notifySubmitResult({
                type: FormSubmitResponseType.FAILURE,
                info: error,
              });
            });
        },
      },
    ]);
  }, [channelApi.requests, processDefinition]);

  return (
    <Card>
      <CardBody id={`${formUUID}-form`} style={{ visibility: "visible" }}>
        <EmbeddedFormDisplayer
          targetOrigin={targetOrigin}
          envelopePath={envelopePath}
          formContent={customForm}
          data={formData}
          context={{}}
          onOpenForm={(opened) => setFormOpened(opened)}
          ref={formDisplayerApiRef}
        />
      </CardBody>
      {formOpened && formOpened.state === FormOpenedState.OPENED && (
        <CardFooter>
          <FormFooter actions={formActions} />
        </CardFooter>
      )}
    </Card>
  );
};

export default CustomProcessFormDisplayer;
