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
import React, { useCallback, useEffect, useState } from "react";
import { ProcessDefinition, ProcessFormDriver } from "../../../api";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import CustomProcessFormDisplayer from "./CustomProcessFormDisplayer";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { FormRenderer, FormRendererApi } from "@kie-tools/runtime-tools-components/dist/components/FormRenderer";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { FormAction } from "@kie-tools/runtime-tools-components/dist/utils";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";

export interface ProcessFormProps {
  processDefinition: ProcessDefinition;
  driver: ProcessFormDriver;
  isEnvelopeConnectedToChannel: boolean;
  targetOrigin: string;
}

const ProcessForm: React.FC<ProcessFormProps & OUIAProps> = ({
  processDefinition,
  driver,
  isEnvelopeConnectedToChannel,
  targetOrigin,
  ouiaId,
  ouiaSafe,
}) => {
  const formRendererApi = React.useRef<FormRendererApi>(null);
  const [processFormSchema, setProcessFormSchema] = useState<any>({});
  const [processCustomForm, setProcessCustomForm] = useState<Form>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>();

  const formAction: FormAction[] = [
    {
      name: "Start",
    },
  ];

  const init = useCallback(async (): Promise<void> => {
    if (!isEnvelopeConnectedToChannel) {
      return;
    }
    const customFormPromise: Promise<void> = new Promise<void>((resolve) => {
      driver
        .getCustomForm(processDefinition)
        .then((customForm) => {
          setProcessCustomForm(customForm);
          resolve();
        })
        .catch((error) => resolve());
    });

    const schemaPromise: Promise<void> = new Promise<void>((resolve, reject) => {
      driver
        .getProcessFormSchema(processDefinition)
        .then((schema) => {
          setProcessFormSchema(schema);
          resolve();
        })
        .catch((error) => {
          setError(error);
          reject(error);
        });
    });

    Promise.all([customFormPromise, schemaPromise]).finally(() => {
      setIsLoading(false);
    });
  }, [driver, isEnvelopeConnectedToChannel, processDefinition]);

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsLoading(true);
      return;
    }
    init();
  }, [init, isEnvelopeConnectedToChannel]);

  const onSubmit = (value: any): void => {
    driver.startProcess(value).then(() => {
      formRendererApi?.current?.doReset();
    });
  };

  if (isLoading) {
    return (
      <Bullseye>
        <KogitoSpinner spinnerText="Loading process forms..." ouiaId="process-form-loading" />
      </Bullseye>
    );
  }

  if (error) {
    return <ServerErrors error={error} variant={"large"} />;
  }

  if (!processCustomForm) {
    return (
      <div {...componentOuiaProps(ouiaId, "process-form", ouiaSafe ? ouiaSafe : !isLoading)}>
        <FormRenderer
          formSchema={processFormSchema}
          model={{}}
          readOnly={false}
          onSubmit={onSubmit}
          formActions={formAction}
          ref={formRendererApi}
        />
      </div>
    );
  } else {
    return (
      <CustomProcessFormDisplayer
        {...componentOuiaProps(ouiaId, "process-form", ouiaSafe ? ouiaSafe : !isLoading)}
        schema={processFormSchema}
        customForm={processCustomForm}
        driver={driver}
        targetOrigin={targetOrigin}
      />
    );
  }
};

export default ProcessForm;
