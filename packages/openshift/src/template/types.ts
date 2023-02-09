/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  DeploymentDescriptor,
  EnvVar,
  KafkaSourceDescriptor,
  KnativeServiceDescriptor,
  SecretDescriptor,
} from "../api/types";

export interface CommonTemplateArgs {
  resourceName: string;
  namespace: string;
  createdBy: string;
}

export interface TemplatedCreateDeploymentArgs {
  kind: "templated";
  uri: string;
  baseUrl: string;
  workspaceName: string;
  containerImageUrl: string;
  envVars: EnvVar[];
}

export interface ProvidedCreateDeploymentArgs {
  kind: "provided";
  descriptor: DeploymentDescriptor;
}

export type CreateDeploymentArgs = TemplatedCreateDeploymentArgs | ProvidedCreateDeploymentArgs;

export interface TemplatedCreateSecretArgs {
  kind: "templated";
  data: Record<string, string>;
}

export interface ProvidedCreateSecretArgs {
  kind: "provided";
  descriptor: SecretDescriptor;
}

export type CreateSecretArgs = TemplatedCreateSecretArgs | ProvidedCreateSecretArgs;

export interface TemplatedCreateKafkaSourceArgs {
  kind: "templated";
  sinkService: string;
  bootstrapServers: string[];
  topics: string[];
  secret: {
    name: string;
    keyId: string;
    keySecret: string;
    keyMechanism: string;
  };
}

export interface ProvidedCreateKafkaSourceArgs {
  kind: "provided";
  descriptor: KafkaSourceDescriptor;
}

export type CreateKafkaSourceArgs = TemplatedCreateKafkaSourceArgs | ProvidedCreateKafkaSourceArgs;

export interface TemplatedCreateKnativeServiceArgs {
  kind: "templated";
  uri: string;
  workspaceName: string;
}

export interface ProvidedCreateKnativeServiceArgs {
  kind: "provided";
  descriptor: KnativeServiceDescriptor;
}

export type CreateKnativeServiceArgs = TemplatedCreateKnativeServiceArgs | ProvidedCreateKnativeServiceArgs;
