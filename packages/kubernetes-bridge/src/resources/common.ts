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

import { IObjectMeta } from "@kubernetes-models/apimachinery/apis/meta/v1/ObjectMeta";
import { KubernetesLabelNames } from "./kubernetes/api";
import { OpenShiftLabelNames } from "./openshift/api";

export type IpProtocol = "TCP" | "UDP" | "SCTP";

export type ResourceRequirements = {
  limits: {
    memory: string;
  };
};

export type ResourceMetadata = IObjectMeta & { name: string; namespace: string };

export interface ResourceDescriptor {
  apiVersion: string;
  kind: string;
  metadata: ResourceMetadata;
}

export interface ResourceMetadataEnforcer {
  metadata: ResourceMetadata;
}

export interface ResourceGroupDescriptor<T extends ResourceDescriptor> {
  items: T[];
}

export type Resource<T extends ResourceDescriptor = ResourceDescriptor> =
  | ResourceDescriptor
  | ResourceGroupDescriptor<T>;

export interface EnvVar {
  name: string;
  value: string;
}

export const BUILD_IMAGE_TAG_VERSION = "1.0";

export const ResourceLabelNames = {
  URI: "kogito.kie.org/uri",
  CREATED_BY: "kogito.kie.org/created-by",
  WORKSPACE_NAME: "kogito.kie.org/workspace-name",
};

export const commonLabels = (args: { resourceName: string; createdBy: string }) => ({
  [KubernetesLabelNames.APP]: args.resourceName,
  [KubernetesLabelNames.COMPONENT]: args.resourceName,
  [KubernetesLabelNames.INSTANCE]: args.resourceName,
  [KubernetesLabelNames.PART_OF]: args.resourceName,
  [KubernetesLabelNames.NAME]: args.resourceName,
  [ResourceLabelNames.CREATED_BY]: args.createdBy,
});

export const runtimeLabels = () => ({
  [OpenShiftLabelNames.RUNTIME]: "quarkus",
  [OpenShiftLabelNames.VERSION]: "openjdk-17-el7",
});

export enum DeploymentState {
  UP = "UP",
  DOWN = "DOWN",
  IN_PROGRESS = "IN_PROGRESS",
  PREPARING = "PREPARING",
  ERROR = "ERROR",
}

export interface DeployedModel {
  resourceName: string;
  routeUrl: string;
  creationTimestamp: Date;
  state: DeploymentState;
}

export enum ResourceDataSource {
  PROVIDED = "PROVIDED",
  TEMPLATE = "TEMPLATE",
}
