/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React from "react";
import { HttpLink } from "apollo-link-http";
import { onError } from "apollo-link-error";
import { InMemoryCache, NormalizedCacheObject } from "apollo-cache-inmemory";
import ApolloClient from "apollo-client";
import DevUIRoutes from "../DevUIRoutes/DevUIRoutes";
import DevUILayout from "../DevUILayout/DevUILayout";
import ReactDOM from "react-dom";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/api";
import { ServerUnavailablePage } from "@kie-tools/runtime-tools-shared-webapp-components/dist/ServerUnavailablePage";

interface IOwnProps {
  isWorkflowEnabled: boolean;
  dataIndexUrl: string;
  navigate: string;
  devUIUrl: string;
  openApiBaseUrl: string;
  openApiPath: string;
  availablePages: string[];
  omittedWorkflowTimelineEvents: string[];
  diagramPreviewSize?: DiagramPreviewSize;
  isStunnerEnabled: boolean;
}

const RuntimeTools: React.FC<IOwnProps> = ({
  dataIndexUrl,
  navigate,
  devUIUrl,
  openApiBaseUrl,
  openApiPath,
  isWorkflowEnabled,
  availablePages,
  omittedWorkflowTimelineEvents,
  diagramPreviewSize,
  isStunnerEnabled,
}) => {
  const httpLink = new HttpLink({
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    uri: dataIndexUrl,
  });

  const fallbackUI = onError(({ networkError }: any) => {
    if (networkError && networkError.stack === "TypeError: Failed to fetch") {
      // eslint-disable-next-line react/no-render-return-value
      return ReactDOM.render(
        <DevUILayout
          apolloClient={client}
          devUIUrl={devUIUrl}
          openApiBaseUrl={openApiBaseUrl}
          openApiPath={openApiPath}
          isWorkflowEnabled={isWorkflowEnabled}
          availablePages={availablePages}
          omittedWorkflowTimelineEvents={omittedWorkflowTimelineEvents}
          diagramPreviewSize={diagramPreviewSize}
          isStunnerEnabled={isStunnerEnabled}
        >
          <ServerUnavailablePage displayName={"Runtime Dev UI"} reload={() => window.location.reload()} />
        </DevUILayout>,
        document.getElementById("envelope-app")
      );
    }
  });

  const cache = new InMemoryCache();
  const client: ApolloClient<NormalizedCacheObject> = new ApolloClient({
    cache,
    link: fallbackUI.concat(httpLink),
  });

  return (
    <DevUILayout
      apolloClient={client}
      devUIUrl={devUIUrl}
      openApiBaseUrl={openApiBaseUrl}
      openApiPath={openApiPath}
      isWorkflowEnabled={isWorkflowEnabled}
      availablePages={availablePages}
      omittedWorkflowTimelineEvents={omittedWorkflowTimelineEvents}
      diagramPreviewSize={diagramPreviewSize}
      isStunnerEnabled={isStunnerEnabled}
    >
      <DevUIRoutes navigate={navigate} dataIndexUrl={dataIndexUrl} />
    </DevUILayout>
  );
};

export default RuntimeTools;
