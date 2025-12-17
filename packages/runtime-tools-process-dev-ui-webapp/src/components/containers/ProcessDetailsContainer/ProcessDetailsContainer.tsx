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
import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { useProcessDetailsChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDetails";
import { EmbeddedProcessDetails } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDetails";

interface ProcessDetailsContainerProps {
  processInstance: ProcessInstance;
}

const ProcessDetailsContainer: React.FC<ProcessDetailsContainerProps> = ({ processInstance }) => {
  const navigate = useNavigate();
  const appContext = useDevUIAppContext();
  const channelApi = useProcessDetailsChannelApi();

  useEffect(() => {
    const unsubscriber = channelApi.processDetails__onOpenProcessInstanceDetailsListener({
      onOpen(id: string) {
        navigate({ pathname: `/` });
        navigate({ pathname: `/Process/${id}` });
      },
    });

    return () => {
      unsubscriber.then((unSubscribeHandler) => unSubscribeHandler.unSubscribe());
    };
  }, [channelApi, navigate, processInstance]);

  return (
    <EmbeddedProcessDetails
      channelApi={channelApi}
      targetOrigin={appContext.getDevUIUrl()}
      processInstance={processInstance}
      omittedProcessTimelineEvents={appContext.omittedProcessTimelineEvents}
      diagramPreviewSize={appContext.diagramPreviewSize}
      singularProcessLabel={appContext.customLabels.singularProcessLabel}
      pluralProcessLabel={appContext.customLabels.pluralProcessLabel}
    />
  );
};

export default ProcessDetailsContainer;
