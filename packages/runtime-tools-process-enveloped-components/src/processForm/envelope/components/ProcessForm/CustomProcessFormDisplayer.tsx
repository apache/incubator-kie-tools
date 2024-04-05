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
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { Stack, StackItem } from "@patternfly/react-core/layouts/Stack";
import React, { useCallback, useEffect, useRef, useState } from "react";
import { v4 as uuidv4 } from "uuid";
import { ProcessFormDriver } from "../../../api";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import {
  EmbeddedFormDisplayer,
  FormDisplayerApi,
  FormOpened,
  FormOpenedState,
  FormSubmitResponseType,
} from "@kie-tools/runtime-tools-shared-enveloped-components/dist/formDisplayer";
import { FormAction } from "@kie-tools/runtime-tools-components/dist/utils";
import { FormFooter } from "@kie-tools/runtime-tools-components/dist/components/FormFooter";

export interface CustomProcessFormDisplayerProps {
  schema: Record<string, any>;
  customForm: Form;
  driver: ProcessFormDriver;
  targetOrigin: string;
}

const CustomProcessFormDisplayer: React.FC<CustomProcessFormDisplayerProps & OUIAProps> = ({
  customForm,
  driver,
  targetOrigin,
  ouiaId,
  ouiaSafe,
}) => {
  const formDisplayerApiRef = useRef<FormDisplayerApi>(null);
  const [formUUID] = useState<string>(uuidv4());
  const [formData] = useState({});
  const [formActions, setFormActions] = useState<FormAction[]>([]);
  const [formOpened, setFormOpened] = useState<FormOpened>();
  const [submitted, setSubmitted] = useState<boolean>(false);

  const doSubmit = useCallback(
    async (payload: any) => {
      const formDisplayerApi = formDisplayerApiRef.current!;

      try {
        const response = await driver.startProcess(payload);
        formDisplayerApi.notifySubmitResult({
          type: FormSubmitResponseType.SUCCESS,
          info: response,
        });
      } catch (error) {
        formDisplayerApi.notifySubmitResult({
          type: FormSubmitResponseType.FAILURE,
          info: error,
        });
      } finally {
        setSubmitted(true);
      }
    },
    [driver]
  );

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
            .then((formOutput) => doSubmit(formOutput))
            .catch((error) => console.log(`Couldn't submit form due to: ${error}`));
        },
      },
    ]);
  }, [doSubmit]);

  return (
    <div {...componentOuiaProps(ouiaId, "custom-form-displayer", ouiaSafe)} style={{ height: "100%" }}>
      <Stack hasGutter>
        <StackItem id={`${formUUID}-form`} style={{ visibility: "visible", height: "inherit" }}>
          <EmbeddedFormDisplayer
            targetOrigin={targetOrigin}
            envelopePath={"resources/form-displayer.html"}
            formContent={customForm}
            data={formData}
            context={{}}
            onOpenForm={(opened) => setFormOpened(opened)}
            ref={formDisplayerApiRef}
          />
        </StackItem>
        {formOpened && formOpened.state === FormOpenedState.OPENED && (
          <StackItem>
            <FormFooter actions={formActions} enabled={!submitted} />
          </StackItem>
        )}
      </Stack>
    </div>
  );
};

export default CustomProcessFormDisplayer;
