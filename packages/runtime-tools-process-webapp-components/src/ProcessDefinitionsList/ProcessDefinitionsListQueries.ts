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
import { ProcessDefinitionsFilter, ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import {
  getProcessDefinitions,
  getProcessDefinitionByName,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";

export interface ProcessDefinitionsListQueries {
  getProcessDefinitions(filters: ProcessDefinitionsFilter): Promise<ProcessDefinition[]>;
  getProcessDefinitionByName(processName: string): Promise<ProcessDefinition>;
}

export class GraphQLProcessDefinitionsListQueries implements ProcessDefinitionsListQueries {
  constructor(
    private readonly client: ApolloClient<any>,
    private readonly options?: { transformEndpointBaseUrl?: (url?: string) => string | undefined }
  ) {}

  getProcessDefinitions(filters: ProcessDefinitionsFilter): Promise<ProcessDefinition[]> {
    return getProcessDefinitions(this.client).then((processDefinitions) => {
      return processDefinitions
        .map((definition) => ({
          ...definition,
          endpoint: this.options?.transformEndpointBaseUrl?.(definition.endpoint) ?? definition.endpoint,
        }))
        .filter(
          (definition) => filters?.processNames?.length === 0 || filters.processNames.includes(definition.processName)
        );
    });
  }

  async getProcessDefinitionByName(processName: string): Promise<ProcessDefinition> {
    return getProcessDefinitionByName(processName, this.client).then((definition) => {
      return {
        ...definition,
        endpoint: this.options?.transformEndpointBaseUrl?.(definition.endpoint) ?? definition.endpoint,
      };
    });
  }
}
