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

import React, { useMemo } from "react";
import { WorkflowDefinitionListContext } from "./WorkflowDefinitionListContext";
import { WorkflowDefinitionListGatewayApiImpl } from "./WorkflowDefinitionListGatewayApi";
import { HttpLink } from "apollo-link-http";
import { InMemoryCache, NormalizedCacheObject } from "apollo-cache-inmemory";
import { ApolloClient } from "apollo-client";
import { GraphQLWorkflowDefinitionListQueries } from "./WorkflowDefinitionListQueries";

export function WorkflowDefinitionListContextProvider(
  props: React.PropsWithChildren<{ proxyEndpoint?: string; dataIndexUrl: string }>
) {
  const { proxyEndpoint, dataIndexUrl } = props;

  const httpLink = useMemo(
    () =>
      new HttpLink({
        uri: proxyEndpoint || dataIndexUrl,
        headers: {
          ...(proxyEndpoint ? { "Target-Url": dataIndexUrl } : {}),
        },
      }),
    [dataIndexUrl, proxyEndpoint]
  );

  const cache = useMemo(() => new InMemoryCache(), []);

  const apolloClient: ApolloClient<NormalizedCacheObject> = useMemo(
    () =>
      new ApolloClient({
        cache,
        link: httpLink,
      }),
    [cache, httpLink]
  );

  return (
    <WorkflowDefinitionListContextProviderWithApolloClient apolloClient={apolloClient}>
      {props.children}
    </WorkflowDefinitionListContextProviderWithApolloClient>
  );
}

export function WorkflowDefinitionListContextProviderWithApolloClient(
  props: React.PropsWithChildren<{ apolloClient: ApolloClient<NormalizedCacheObject> }>
) {
  const { apolloClient } = props;

  const gatewayApiImpl = useMemo(() => {
    return new WorkflowDefinitionListGatewayApiImpl(new GraphQLWorkflowDefinitionListQueries(apolloClient));
  }, [apolloClient]);

  return (
    <WorkflowDefinitionListContext.Provider value={gatewayApiImpl}>
      {props.children}
    </WorkflowDefinitionListContext.Provider>
  );
}
