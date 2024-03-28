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
import { HttpLink } from "apollo-link-http";
import { onError } from "apollo-link-error";
import { ServerUnavailablePage } from "@kogito-apps/consoles-common/dist/components/pages/ServerUnavailablePage";
import { User } from "@kogito-apps/consoles-common/dist/environment/auth";
import { InMemoryCache, NormalizedCacheObject } from "apollo-cache-inmemory";
import ApolloClient from "apollo-client";
import DevUIRoutes from "../DevUIRoutes/DevUIRoutes";
import DevUILayout from "../DevUILayout/DevUILayout";
import ReactDOM from "react-dom";
import { CustomLabels } from "../../../api/CustomLabels";
import { DiagramPreviewSize } from "@kogito-apps/process-details/dist/api";

interface IOwnProps {
  isProcessEnabled: boolean;
  users: User[];
  dataIndexUrl: string;
  navigate: string;
  devUIUrl: string;
  openApiPath: string;
  availablePages: string[];
  customLabels: CustomLabels;
  omittedProcessTimelineEvents: string[];
  diagramPreviewSize?: DiagramPreviewSize;
}

const RuntimeTools: React.FC<IOwnProps> = ({
  users,
  dataIndexUrl,
  navigate,
  devUIUrl,
  openApiPath,
  isProcessEnabled,
  availablePages,
  customLabels,
  omittedProcessTimelineEvents,
  diagramPreviewSize,
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
          users={users}
          devUIUrl={devUIUrl}
          openApiPath={openApiPath}
          isProcessEnabled={isProcessEnabled}
          availablePages={availablePages}
          customLabels={customLabels}
          omittedProcessTimelineEvents={omittedProcessTimelineEvents}
          diagramPreviewSize={diagramPreviewSize}
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
      users={users}
      devUIUrl={devUIUrl}
      openApiPath={openApiPath}
      isProcessEnabled={isProcessEnabled}
      availablePages={availablePages}
      customLabels={customLabels}
      omittedProcessTimelineEvents={omittedProcessTimelineEvents}
      diagramPreviewSize={diagramPreviewSize}
    >
      <DevUIRoutes navigate={navigate} />
    </DevUILayout>
  );
};

export default RuntimeTools;
