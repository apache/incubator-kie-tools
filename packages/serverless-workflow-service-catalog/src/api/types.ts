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

export interface SwfServiceCatalogUser {
  username: string;
}

export enum SwfServiceCatalogServiceType {
  rest = "rest",
  graphql = "graphql",
}

export interface SwfServiceCatalogService {
  name: string;
  type: SwfServiceCatalogServiceType;
  source: SwfServiceCatalogServiceSource;

  functions: SwfServiceCatalogFunction[];
  rawContent: string;
}

export type SwfServiceCatalogServiceSource =
  | {
      id: string;
      type: SwfServiceCatalogServiceSourceType.RHHCC_SERVICE_REGISTRY;
    }
  | {
      type: SwfServiceCatalogServiceSourceType.LOCAL_FS;
      absoluteFilePath: string;
    };

export enum SwfServiceCatalogFunctionType {
  rest = "rest",
  graphql = "graphql",
  asyncapi = "asyncapi",
}

export enum SwfServiceCatalogFunctionArgumentType {
  boolean = "boolean",
  object = "object",
  number = "number",
  string = "string",
  integer = "integer",
  array = "array",
}

export enum SwfServiceCatalogFunctionSourceType {
  RHHCC_SERVICE_REGISTRY,
  LOCAL_FS,
}

export enum SwfServiceCatalogServiceSourceType {
  RHHCC_SERVICE_REGISTRY,
  LOCAL_FS,
}

export type SwfServiceCatalogFunctionSource =
  | {
      serviceId: string;
      type: SwfServiceCatalogFunctionSourceType.RHHCC_SERVICE_REGISTRY;
    }
  | {
      type: SwfServiceCatalogFunctionSourceType.LOCAL_FS;
      serviceFileAbsolutePath: string;
    };

export interface SwfServiceCatalogFunction {
  source: SwfServiceCatalogFunctionSource;
  name: string;
  operation: string;
  arguments: Record<string, SwfServiceCatalogFunctionArgumentType>;
  type: SwfServiceCatalogFunctionType;
}
