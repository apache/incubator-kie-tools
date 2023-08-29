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

export interface FromObject {
  id: string;
  parameters: object;
  description: string;
  steps: any;
  uri: string;
}

export interface RouteObject {
  autoStartup: boolean;
  description: string;
  from: FromObject;
  group: string;
  id: string;
  logMask: boolean;
  messageHistory: boolean;
  nodePrefixId: string;
  precondition: string;
  routeConfigurationId: string;
  routePolicy: string;
  startupOrder: number;
  streamCaching: boolean;
  trace: boolean;
}

export interface RoutesDefinition {
  route: RouteObject;
}

export interface FromDefinition {
  from: FromObject;
}

export type CamelRouteDocument = FromDefinition[] | RoutesDefinition[];

export type RouteItemType = RoutesDefinition | FromDefinition;
