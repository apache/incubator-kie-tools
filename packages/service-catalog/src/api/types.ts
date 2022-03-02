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

export enum ServiceType {
  rest = "rest",
  graphql = "graphql",
}

export interface Service {
  name: string;
  id: string;
  type: ServiceType;

  functions: Function[];
  rawContent: string;
}

export enum FunctionType {
  rest = "rest",
  graphql = "graphql",
  async = "async",
}

export enum FunctionArgumentType {
  boolean = "boolean",
  object = "object",
  number = "number",
  string = "string",
  integer = "integer",
  array = "array",
}

export interface Function {
  name: string;
  operation: string;
  arguments: Record<string, FunctionArgumentType>;
  type: FunctionType;
}
