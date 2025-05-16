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
import React, { ReactElement, useEffect } from "react";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { EmbeddedProcessForm } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processForm";
import { useProcessFormChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessForm";

interface ProcessFormContainerProps {
  processDefinitionData: ProcessDefinition;
  onSubmitSuccess: (id: string) => void;
  onSubmitError: (details?: ReactElement | string) => void;
}
const ProcessFormContainer: React.FC<ProcessFormContainerProps> = ({
  processDefinitionData,
  onSubmitSuccess,
  onSubmitError,
}) => {
  const channelApi = useProcessFormChannelApi();
  const appContext = useDevUIAppContext();

  useEffect(() => {
    const unsubscriber = channelApi.processForm__onStartProcessListen({
      onSuccess(processInstanceId) {
        channelApi.processForm__setBusinessKey("");
        onSubmitSuccess(processInstanceId);
      },
      onError(error) {
        const details = error.response ? (
          <>
            <b>
              {error.response.statusText}: {error.message}
            </b>
            {error.response.data?.message && (
              <>
                <br />
                {error.response.data.message}
              </>
            )}
          </>
        ) : (
          error.message
        );
        onSubmitError(details);
      },
    });

    return () => {
      unsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
    };
  }, [channelApi, onSubmitError, onSubmitSuccess]);

  return (
    <EmbeddedProcessForm
      processDefinition={processDefinitionData}
      channelApi={channelApi}
      targetOrigin={appContext.getDevUIUrl()}
      customFormDisplayerEnvelopePath="resources/form-displayer.html"
      shouldLoadCustomForms={true}
    />
  );
};

export default ProcessFormContainer;
