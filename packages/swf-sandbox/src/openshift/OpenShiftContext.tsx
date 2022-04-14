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

import * as React from "react";
import { useContext } from "react";
import { KafkaSettingsConfig } from "../settings/kafka/KafkaSettingsConfig";
import { OpenShiftSettingsConfig } from "../settings/openshift/OpenShiftSettingsConfig";
import { ServiceAccountSettingsConfig } from "../settings/serviceAccount/ServiceAccountConfig";
import { Deployments } from "./resources/Deployment";
import { KNativeServices } from "./resources/KNativeService";

export const SW_JSON_EXTENSION = "sw.json";

export interface DeployArgs {
  workflow: DeploymentWorkflow;
  openShiftConfig: OpenShiftSettingsConfig;
  kafkaConfig: KafkaSettingsConfig;
  serviceAccountConfig: ServiceAccountSettingsConfig;
  resourceName?: string;
}

export interface DeploymentWorkflow {
  name: string;
  content: string;
}

export interface OpenShiftContextType {
  deploy(args: DeployArgs): Promise<string>;
  uploadOpenApiToServiceRegistry(openApiContent: string, artifactId: string): Promise<void>;
  fetchServiceRegistryArtifacts(): Promise<any>;

  fetchOpenApiFile(config: OpenShiftSettingsConfig, resourceName: string): Promise<string | undefined>;
  fetchWorkflowRoute(config: OpenShiftSettingsConfig, resourceName: string): Promise<string | undefined>;
  fetchWorkflowName(config: OpenShiftSettingsConfig, resourceName: string): Promise<string | undefined>;
  fetchWorkflow(config: OpenShiftSettingsConfig, resourceName: string): Promise<DeploymentWorkflow | undefined>;
  fetchWorkflows(config: OpenShiftSettingsConfig): Promise<DeploymentWorkflow[]>;
  listServices(config: OpenShiftSettingsConfig): Promise<KNativeServices | undefined>;
  listDeployments(config: OpenShiftSettingsConfig): Promise<Deployments | undefined>;
}

export const OpenShiftContext = React.createContext<OpenShiftContextType>({} as any);

export function useOpenShift() {
  return useContext(OpenShiftContext);
}
