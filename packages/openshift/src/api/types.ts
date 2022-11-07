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

import { ApiVersions } from "./ApiConstants";

export type KubernetesKind =
  | "Build"
  | "BuildConfig"
  | "Deployment"
  | "ImageStream"
  | "ImageStreamTag"
  | "Pod"
  | "Project"
  | "Secret"
  | "Service"
  | "Route";

export type KNativeKind = "KafkaSource" | "Service";

export type ResourceKind = KubernetesKind | KNativeKind;

export type IpProtocol = "TCP" | "UDP" | "SCTP";

export type ResourceRequirements = {
  limits: {
    memory: string;
  };
};

export interface ObjectReference {
  kind: ResourceKind;
  name: string;
}

export interface ObjectMeta {
  name: string;
  namespace: string;
  creationTimestamp?: string;
  labels?: Record<string, string>;
  annotations?: Record<string, string>;
  finalizers?: string[];
}

export interface ResourceDescriptor {
  apiVersion: ApiVersions;
  kind: ResourceKind;
  metadata: ObjectMeta;
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

export interface ContainerPort {
  containerPort: number;
  protocol: IpProtocol;
}

export interface Container {
  name: string;
  image: string;
  env?: EnvVar[];
  ports?: ContainerPort[];
}

export interface Trigger {
  from: {
    kind: ResourceKind;
    name: string;
    namespace: string;
  };
  fieldPath: string;
  pause: boolean;
}

// Build

export type BuildPhase = "New" | "Pending" | "Running" | "Complete" | "Failed" | "Error" | "Cancelled";

export interface BuildDescriptor extends ResourceDescriptor {
  status?: {
    phase: BuildPhase;
  };
}

export type BuildGroupDescriptor = ResourceGroupDescriptor<BuildDescriptor>;

// BuildConfig

export interface DockerBuildStrategy {
  type: "Docker";
}

export interface BinaryBuildSource {
  type: "Binary";
  binary: {};
}

export interface BuildOutput {
  to: ObjectReference;
}
export type BuildSource = BinaryBuildSource;
export type BuildStrategy = DockerBuildStrategy;

export interface BuildConfigSpec {
  output: BuildOutput;
  resources: ResourceRequirements;
  source: BuildSource;
  strategy: BuildStrategy;
}

export interface BuildConfigDescriptor extends ResourceDescriptor {
  spec: BuildConfigSpec;
}

// Pod

export interface PodSpec {
  containers: Container[];
}

export interface PodTemplate {
  metadata?: Partial<ObjectMeta>;
  spec: PodSpec;
}

// Deployment

export interface DeploymentCondition {
  type: string;
  status: "True" | "False" | "Unknown";
}

export interface DeploymentStatus {
  replicas?: number;
  readyReplicas?: number;
  conditions?: DeploymentCondition[];
}

export interface DeploymentSpec {
  replicas: number;
  selector: {
    matchLabels: Record<string, string>;
  };
  template: PodTemplate;
}

export interface DeploymentDescriptor extends ResourceDescriptor {
  spec: DeploymentSpec;
  status?: DeploymentStatus;
}

export type DeploymentGroupDescriptor = ResourceGroupDescriptor<DeploymentDescriptor>;

// ImageStream

export interface ImageStreamSpec {
  lookupPolicy: {
    local: boolean;
  };
}

export interface ImageStreamDescriptor extends ResourceDescriptor {
  spec: ImageStreamSpec;
}

// Project

export interface ProjectDescriptor extends ResourceDescriptor {
  // Empty on purpose
}

// Secret

export interface SecretDescriptor extends ResourceDescriptor {
  data: Record<string, string>;
}

// Service

export interface ServicePort {
  name: string;
  protocol: IpProtocol;
  port: number;
  targetPort: number | string;
}

export interface ServiceSpec {
  ports: ServicePort[];
  selector: {
    app: string;
    deploymentconfig: string;
  };
}

export interface ServiceDescriptor extends ResourceDescriptor {
  spec: ServiceSpec;
}

// Route

export interface TLSConfig {
  insecureEdgeTerminationPolicy?: "None" | "Allow" | "Redirect";
  termination: "edge" | "passthrough" | "reencrypt";
}

export interface RouteSpec {
  host?: string;
  port: {
    targetPort: number | string;
  };
  tls: TLSConfig;
  to: ObjectReference;
}

export interface RouteDescriptor extends ResourceDescriptor {
  spec: RouteSpec;
}

export type RouteGroupDescriptor = ResourceGroupDescriptor<RouteDescriptor>;

// KafkaSource

export interface SecretKeyRef {
  key: string;
  name: string;
}

export interface SinkRef {
  apiVersion: ApiVersions;
  kind: ResourceKind;
  name: string;
}

export interface KafkaSourceSpec {
  bootstrapServers: string[];
  consumerGroup: string;
  net: {
    tls: {
      enable: boolean;
    };
    sasl: {
      enable: boolean;
      type: {
        secretKeyRef: SecretKeyRef;
      };
      password: {
        secretKeyRef: SecretKeyRef;
      };
      user: {
        secretKeyRef: SecretKeyRef;
      };
    };
  };
  sink: {
    ref: SinkRef;
  };
  topics: string[];
}

export interface KafkaSourceDescriptor extends ResourceDescriptor {
  spec: KafkaSourceSpec;
}

// KNativeService

export interface KNativeServiceSpec {
  template: PodTemplate;
}

export interface KNativeServiceDescriptor extends ResourceDescriptor {
  spec: KNativeServiceSpec;
  status?: {
    url: string;
  };
}

export type KNativeServiceGroupDescriptor = ResourceGroupDescriptor<KNativeServiceDescriptor>;
