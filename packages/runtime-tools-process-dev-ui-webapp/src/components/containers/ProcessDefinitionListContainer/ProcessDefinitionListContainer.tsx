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
import React, { useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import {
  EmbeddedProcessDefinitionsList,
  ProcessDefinitionsListState,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDefinitionsList";
import { useProcessDefinitionsListChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDefinitionsList";

const defaultFilters = {
  processNames: [],
};

const ProcessDefinitionsListContainer: React.FC = () => {
  const navigate = useNavigate();
  const channelApi = useProcessDefinitionsListChannelApi();
  const appContext = useDevUIAppContext();

  useEffect(() => {
    const onOpenProcess = {
      onOpen(processDefinition: ProcessDefinition) {
        navigate(
          {
            pathname: `../ProcessDefinition/Form/${processDefinition.processName}`,
          },
          {
            state: {
              processDefinition: processDefinition,
            },
          }
        );
      },
    };

    const onOpenInstanceUnsubscriber = channelApi.processDefinitionsList__onOpenProcessDefinitionListen(onOpenProcess);

    return () => {
      onOpenInstanceUnsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
    };
  }, [channelApi, navigate]);

  const initialState: ProcessDefinitionsListState = useMemo(() => {
    return {
      filters: defaultFilters,
    };
  }, []);

  return (
    <EmbeddedProcessDefinitionsList
      initialState={initialState}
      channelApi={channelApi}
      targetOrigin={appContext.getDevUIUrl()}
    />
  );
};

export default ProcessDefinitionsListContainer;
