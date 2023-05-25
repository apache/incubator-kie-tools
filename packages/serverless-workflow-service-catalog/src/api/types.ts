/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export enum AuthProviderType {
  NONE = "none",
  RH_ACCOUNT = "red-hat-account",
}

export interface SwfServiceRegistrySettings {
  name: string;
  url: string;
  authProvider: AuthProviderType;
}

export interface SwfServiceRegistriesSettings {
  registries: SwfServiceRegistrySettings[];
}

export enum SwfServiceCatalogServiceType {
  rest = "rest",
  graphql = "graphql",
  asyncapi = "asyncapi",
  camelroute = "camelroute",
}

export interface SwfServiceCatalogService {
  name: string;
  type: SwfServiceCatalogServiceType;
  source: SwfServiceCatalogServiceSource;
  functions: SwfServiceCatalogFunction[];
  events?: SwfServiceCatalogEvent[];
  rawContent: string;
}

export type SwfServiceCatalogServiceSource =
  | {
      registry: string;
      id: string;
      url: string;
      type: SwfCatalogSourceType.SERVICE_REGISTRY;
    }
  | {
      type: SwfCatalogSourceType.LOCAL_FS;
      absoluteFilePath: string;
    };

export enum SwfServiceCatalogFunctionType {
  rest = "rest",
  graphql = "graphql",
  asyncapi = "asyncapi",
  custom = "custom",
}

export enum SwfServiceCatalogEventType {
  asyncapi = "asyncapi",
}

export enum SupportArtifactTypes {
  Openapi = "OPENAPI",
  Asyncapi = "ASYNCAPI",
  Camel = "CAMEL",
}

export enum SwfServiceCatalogFunctionArgumentType {
  boolean = "boolean",
  object = "object",
  number = "number",
  string = "string",
  integer = "integer",
  array = "array",
}

export enum SwfCatalogSourceType {
  SERVICE_REGISTRY = "SERVICE_REGISTRY",
  LOCAL_FS = "LOCAL_FS",
}

export enum SwfServiceCatalogEventKind {
  CONSUMED = "consumed",
  PRODUCED = "produced",
}

export type SwfServiceCatalogFunctionSource =
  | {
      registry: string;
      serviceId: string;
      type: SwfCatalogSourceType.SERVICE_REGISTRY;
    }
  | {
      type: SwfCatalogSourceType.LOCAL_FS;
      serviceFileAbsolutePath: string;
    };

export type SwfServiceCatalogEventSource =
  | {
      registry: string;
      serviceId: string;
      type: SwfCatalogSourceType.SERVICE_REGISTRY;
    }
  | {
      type: SwfCatalogSourceType.LOCAL_FS;
      serviceFileAbsolutePath: string;
    };

export interface SwfServiceCatalogFunction {
  source: SwfServiceCatalogFunctionSource;
  name: string;
  arguments: Record<string, SwfServiceCatalogFunctionArgumentType>;
  type: SwfServiceCatalogFunctionType;
}

export interface SwfServiceCatalogEvent {
  source: SwfServiceCatalogEventSource;
  name: string;
  kind: SwfServiceCatalogEventKind;
  type: SwfServiceCatalogEventType;
  eventSource: string;
  eventType: string;
}
