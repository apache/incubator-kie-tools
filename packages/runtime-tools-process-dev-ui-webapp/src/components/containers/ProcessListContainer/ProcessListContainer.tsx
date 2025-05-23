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
import {
  EmbeddedProcessList,
  ProcessListState,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/processList";
import { useProcessListChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessList";
import { ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

interface ProcessListContainerProps {
  initialState: ProcessListState;
}

const ProcessListContainer: React.FC<ProcessListContainerProps> = ({ initialState }) => {
  const navigate = useNavigate();
  const channelApi = useProcessListChannelApi();
  const appContext = useDevUIAppContext();

  useEffect(() => {
    const onOpenInstanceUnsubscriber = channelApi.processList__onOpenProcessListen({
      onOpen(process: ProcessInstance) {
        navigate(
          {
            pathname: `../Process/${process.id}`,
          },
          {}
        );
      },
    });
    return () => {
      onOpenInstanceUnsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
    };
  }, [channelApi, history]);

  return (
    <EmbeddedProcessList
      channelApi={channelApi}
      targetOrigin={appContext.getDevUIUrl()}
      initialState={initialState}
      singularProcessLabel={appContext.customLabels.singularProcessLabel}
      pluralProcessLabel={appContext.customLabels.pluralProcessLabel}
    />
  );
};

export default ProcessListContainer;
