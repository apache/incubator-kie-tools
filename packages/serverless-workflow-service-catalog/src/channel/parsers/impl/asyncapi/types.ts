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

import { OpenAPIV3 } from "openapi-types";

export type SchemaObject = OpenAPIV3.ArraySchemaObject | OpenAPIV3.NonArraySchemaObject;

export interface ParametersObject {
  description: string;
  schema: OpenAPIV3.SchemaObject;
  location: string;
}

export interface ReferenceObject {
  $ref: string;
}

interface OperationTraitObject {
  operationId: string;
  summary: string;
  description: string;
  tags: TagsObject;
  externalDocs: ExternalDocsObject;
}

interface MessageTraitObject {
  headers: SchemaObject | ReferenceObject;
  correlationId: CorrelationIdObject;
  schemaFormat: string;
  contentType: string;
  name: string;
  title: string;
  summary: string;
  description: string;
  tags: TagsObject;
  externalDocs: ExternalDocsObject;
}

interface ExternalDocsObject {
  description: string;
  url: string;
}

interface TagsObject {
  name: string;
  description: string;
  externalDocs: ExternalDocsObject;
}

interface CorrelationIdObject {
  description: string;
  location: string;
}

export interface MessageObject {
  messageId: string;
  headers: SchemaObject | ReferenceObject;
  payload: any;
  correlationId: CorrelationIdObject;
  schemaFormat: string;
  contentType: string;
  name: string;
  title: string;
  summary: string;
  description: string;
  tags: TagsObject;
  externalDocs: ExternalDocsObject;
  traits: MessageTraitObject | ReferenceObject;
  $ref: string;
}

export interface OperationObject {
  operationId: string;
  summary?: string;
  description?: string;
  tags?: TagsObject;
  externalDocs?: ExternalDocsObject;
  traits?: OperationTraitObject | ReferenceObject;
  message?: [MessageObject | ReferenceObject];
}

export interface ChannelsObject {
  $ref?: string;
  description?: string;
  servers?: string[];
  subscribe?: OperationObject;
  publish?: OperationObject;
  parameters?: ParametersObject;
}

interface ContactObject {
  name?: string;
  url?: string;
  email?: string;
}

interface LicenseObject {
  name?: string;
  url?: string;
}

interface infoObject {
  title?: String;
  version?: string;
  description?: string;
  termsOfService?: string;
  contact?: ContactObject;
  license?: LicenseObject;
}

interface ServerVariablesObject {
  enum: string[];
  default: string;
  description: string;
  examples: string[];
}

interface ComponentsObject {
  schemas: any;
  servers: any;
  serverVariables: ServerVariablesObject[];
  channels: ChannelsObject[];
  messages: MessageObject[];
  securitySchemes: any;
  parameters: ParametersObject[];
  correlationIds: CorrelationIdObject[];
  operationTraits: OperationTraitObject[];
  messageTraits: MessageTraitObject[];
  serverBindings: any;
  channelBindings: any;
  operationBindings: any;
  messageBindings: any;
}

export interface AsyncAPIDocument {
  asyncapi: string;
  id: string;
  info: infoObject;
  servers: any;
  defaultContentType: string;
  channels: ChannelsObject;
  components: ComponentsObject;
  tags: TagsObject;
  externalDocs: ExternalDocsObject;
}
