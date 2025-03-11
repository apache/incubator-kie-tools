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
import { ApolloClient } from "apollo-client";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { getProcessDefinitions } from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";

export interface ProcessDefinitionListQueries {
  getProcessDefinitions(): Promise<ProcessDefinition[]>;
}

export class GraphQLProcessDefinitionListQueries implements ProcessDefinitionListQueries {
  constructor(
    private readonly client: ApolloClient<any>,
    private readonly options?: { transformEndpointBaseUrl?: (url?: string) => string }
  ) {
    this.client = client;
  }

  getProcessDefinitions(): Promise<ProcessDefinition[]> {
    return getProcessDefinitions(this.client).then((processDefinitions) =>
      processDefinitions.map((definition) => ({
        ...definition,
        endpoint: this.options?.transformEndpointBaseUrl?.(definition.endpoint) ?? definition.endpoint,
      }))
    );
  }
}
