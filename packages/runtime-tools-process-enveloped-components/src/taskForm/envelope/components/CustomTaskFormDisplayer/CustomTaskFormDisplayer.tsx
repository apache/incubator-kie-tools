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
import React, { useEffect, useRef, useState, useCallback } from "react";
import { v4 as uuidv4 } from "uuid";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { generateFormData } from "../utils/TaskFormDataUtils";
import { TaskFormDriver, User } from "../../../api";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { FormAction } from "@kie-tools/runtime-tools-components/dist/utils";
import { buildTaskFormContext } from "./utils/utils";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { FormFooter } from "@kie-tools/runtime-tools-components/dist/components/FormFooter";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import {
  EmbeddedFormDisplayer,
  FormDisplayerApi,
  FormOpened,
  FormOpenedState,
  FormSubmitResponseType,
} from "@kie-tools/runtime-tools-shared-enveloped-components/dist/formDisplayer";

export interface CustomTaskFormDisplayerProps {
  userTask: UserTaskInstance;
  schema: Record<string, any>;
  customForm: Form;
  user: User;
  driver: TaskFormDriver;
  phases: string[];
  targetOrigin: string;
}

const CustomTaskFormDisplayer: React.FC<CustomTaskFormDisplayerProps & OUIAProps> = ({
  userTask,
  customForm,
  schema,
  user,
  driver,
  phases,
  targetOrigin,
  ouiaId,
  ouiaSafe,
}) => {
  const formDisplayerApiRef = useRef<FormDisplayerApi>(null);
  const [formUUID] = useState<string>(uuidv4());
  const [formData] = useState(generateFormData(userTask));
  const [formActions, setFormActions] = useState<FormAction[]>([]);
  const [formOpened, setFormOpened] = useState<FormOpened>();
  const [submitted, setSubmitted] = useState<boolean>(false);

  const doSubmit = useCallback(
    async (phase: string, payload: any) => {
      const formDisplayerApi = formDisplayerApiRef.current;

      try {
        const response = await driver.doSubmit(phase, payload);
        formDisplayerApi!.notifySubmitResult({
          type: FormSubmitResponseType.SUCCESS,
          info: response,
        });
      } catch (error) {
        formDisplayerApi!.notifySubmitResult({
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
    if (phases) {
      const actions = phases.map((phase) => {
        return {
          name: phase,
          execute: () => {
            const formDisplayerApi = formDisplayerApiRef.current;
            formDisplayerApi!
              .startSubmit({
                params: {
                  phase: phase,
                },
              })
              .then((formOutput) => doSubmit(phase, formOutput))
              .catch((error) => console.log(`Couldn't submit form due to: ${error}`));
          },
        };
      });
      setFormActions(actions);
    }
  }, [doSubmit, phases]);

  useEffect(() => {
    if (formOpened) {
      document.getElementById(`${formUUID}-form`)!.style.visibility = "visible";
    }
  }, [formOpened, formUUID]);

  return (
    <div {...componentOuiaProps(ouiaId, "custom-form-displayer", ouiaSafe)} style={{ height: "100%" }}>
      {!formOpened && (
        <Bullseye
          {...componentOuiaProps((ouiaId ? ouiaId : "task-form-envelope-view") + "-loading-spinner", "task-form", true)}
        >
          <KogitoSpinner spinnerText={`Loading Task form...`} />
        </Bullseye>
      )}
      <Stack hasGutter>
        <StackItem id={`${formUUID}-form`} style={{ visibility: "hidden", height: "inherit" }}>
          <EmbeddedFormDisplayer
            targetOrigin={targetOrigin}
            envelopePath={"resources/form-displayer.html"}
            formContent={customForm}
            data={formData}
            context={buildTaskFormContext(userTask, schema, user)}
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

export default CustomTaskFormDisplayer;
