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
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { ProcessFormChannelApi } from "../../api";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import CustomProcessFormDisplayer from "./CustomProcessFormDisplayer";
import { FormRenderer, FormRendererApi } from "@kie-tools/runtime-tools-components/dist/components/FormRenderer";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { FormAction } from "@kie-tools/runtime-tools-components/dist/utils";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

export interface ProcessFormProps {
  processDefinition: ProcessDefinition;
  channelApi: MessageBusClientApi<ProcessFormChannelApi>;
  isEnvelopeConnectedToChannel: boolean;
  targetOrigin: string;
  customFormDisplayerEnvelopePath?: string;
  shouldLoadCustomForms?: boolean;
}

const formAction: FormAction[] = [
  {
    name: "Start",
  },
];

const ProcessForm: React.FC<ProcessFormProps> = ({
  processDefinition,
  channelApi,
  isEnvelopeConnectedToChannel,
  targetOrigin,
  customFormDisplayerEnvelopePath,
  shouldLoadCustomForms = false,
}) => {
  const formRendererApi = React.useRef<FormRendererApi>(null);
  const [processFormSchema, setProcessFormSchema] = useState<any>({});
  const [processCustomForm, setProcessCustomForm] = useState<Form>();
  const [svg, setSvg] = useState<JSX.Element>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>();

  const init = useCallback(async (): Promise<void> => {
    if (!isEnvelopeConnectedToChannel) {
      return;
    }
    const customFormPromise: Promise<void> = new Promise<void>((resolve) => {
      if (!shouldLoadCustomForms) {
        resolve();
        return;
      }
      channelApi.requests
        .processForm__getCustomForm(processDefinition)
        .then((customForm) => {
          setProcessCustomForm(customForm);
          resolve();
        })
        .catch((error) => resolve());
    });

    const schemaPromise: Promise<void> = new Promise<void>((resolve, reject) => {
      channelApi.requests
        .processForm__getProcessFormSchema(processDefinition)
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
  }, [channelApi.requests, isEnvelopeConnectedToChannel, processDefinition, shouldLoadCustomForms]);

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsLoading(true);
      return;
    }
    init();
  }, [init, isEnvelopeConnectedToChannel]);

  const onSubmit = useCallback(
    (value: any): void => {
      channelApi.requests.processForm__startProcess(processDefinition, value).then(() => {
        formRendererApi?.current?.doReset();
      });
    },
    [channelApi.requests, processDefinition]
  );

  useEffect(() => {
    const handleSvgApi = async (): Promise<void> => {
      try {
        const response = await channelApi.requests.processForm__getProcessDefinitionSvg(processDefinition);
        if (response) {
          const temp = <img src={`data:image/svg+xml;utf8,${encodeURIComponent(response)}`} />;
          setSvg(temp);
        } else {
          setSvg(undefined);
        }
      } catch (error) {
        console.log(error);
      }
    };
    if (isEnvelopeConnectedToChannel) {
      handleSvgApi();
    }
  }, [channelApi, isEnvelopeConnectedToChannel, processDefinition]);

  const processDiagramBlock = useMemo(
    () =>
      svg && (
        <Card>
          <CardBody>{svg}</CardBody>
        </Card>
      ),
    [svg]
  );

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
      <Flex direction={{ default: "column" }}>
        <FlexItem>{processDiagramBlock}</FlexItem>
        <FlexItem>
          <Card>
            <CardBody>
              <FormRenderer
                formSchema={processFormSchema}
                model={{}}
                readOnly={false}
                onSubmit={onSubmit}
                formActions={formAction}
                ref={formRendererApi}
              />
            </CardBody>
          </Card>
        </FlexItem>
      </Flex>
    );
  } else {
    return (
      <Flex direction={{ default: "column" }}>
        <FlexItem>{processDiagramBlock}</FlexItem>
        <FlexItem grow={{ default: "grow" }}>
          <CustomProcessFormDisplayer
            processDefinition={processDefinition}
            schema={processFormSchema}
            customForm={processCustomForm}
            channelApi={channelApi}
            targetOrigin={targetOrigin}
            envelopePath={customFormDisplayerEnvelopePath}
          />
        </FlexItem>
      </Flex>
    );
  }
};

export default ProcessForm;
