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

import { KubernetesLabelNames } from "../api/kubernetes/api";
import { OpenShiftLabelNames } from "../api/openshift/api";

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
  [OpenShiftLabelNames.VERSION]: "openjdk-11-el7",
});
