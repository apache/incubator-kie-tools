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

export enum KubernetesApiVersions {
  BUILD = "build.openshift.io/v1",
  BUILD_CONFIG = "build.openshift.io/v1",
  DEPLOYMENT = "apps/v1",
  IMAGE_STREAM = "image.openshift.io/v1",
  PROJECT = "project.openshift.io/v1",
  ROUTE = "route.openshift.io/v1",
  SERVICE = "v1",
  SECRET = "v1",
}

export enum KNativeApiVersions {
  KAFKA_SOURCE = "sources.knative.dev/v1beta1",
  SERVICE = "serving.knative.dev/v1",
}

export type ApiVersions = `${KubernetesApiVersions}` | `${KNativeApiVersions}`;

export const baseEndpoint = (apiVersion: ApiVersions) => {
  if (apiVersion === KubernetesApiVersions.SECRET) {
    return `api/${apiVersion}`;
  }
  return `apis/${apiVersion}`;
};

export const KubernetesLabelNames = {
  APP: "app",
  COMPONENT: "app.kubernetes.io/component",
  INSTANCE: "app.kubernetes.io/instance",
  PART_OF: "app.kubernetes.io/part-of",
  NAME: "app.kubernetes.io/name",
};

export const OpenShiftLabelNames = {
  RUNTIME: "app.openshift.io/runtime",
  VERSION: "app.openshift.io/runtime-version",
  TRIGGERS: "image.openshift.io/triggers",
};

export const KNativeLabelNames = {
  SERVICE: "serving.knative.dev/service",
};
