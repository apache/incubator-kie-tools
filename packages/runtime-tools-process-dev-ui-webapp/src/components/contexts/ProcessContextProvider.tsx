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
import React from "react";
import { ApolloClient } from "apollo-client";
import { useDevUIAppContext } from "../../components/contexts/DevUIAppContext";
import { ProcessListContextProvider } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessList";
import { ProcessDetailsContextProvider } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDetails";
import { ProcessDefinitionListContextProvider } from "../../channel/ProcessDefinitionList";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  children;
}

export const ProcessContextProvider: React.FC<IOwnProps> = ({ apolloClient, children }) => {
  const appContext = useDevUIAppContext();

  return (
    <ProcessListContextProvider
      apolloClient={apolloClient}
      options={{ transformEndpointBaseUrl: (url) => appContext.transformEndpointBaseUrl(url) }}
    >
      <ProcessDetailsContextProvider
        apolloClient={apolloClient}
        options={{ transformEndpointBaseUrl: (url) => appContext.transformEndpointBaseUrl(url) }}
      >
        <ProcessDefinitionListContextProvider
          apolloClient={apolloClient}
          options={{ transformEndpointBaseUrl: (url) => appContext.transformEndpointBaseUrl(url) }}
        >
          {children}
        </ProcessDefinitionListContextProvider>
      </ProcessDetailsContextProvider>
    </ProcessListContextProvider>
  );
};

export default ProcessContextProvider;
