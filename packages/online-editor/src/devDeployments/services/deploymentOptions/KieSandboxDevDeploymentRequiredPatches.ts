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

import { ResourcePatch, encodeJsonPatchSubpath } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";

export const K8S_RESOURCE_CREATED_BY = "kie-tools";

export const requiredLabels = {
  createdBy: "tools.kie.org/created-by",
  partOf: "tools.kie.org/part-of",
};

export const requiredAnnotations = {
  workspaceId: "tools.kie.org/workspace-id",
  workspaceName: "tools.kie.org/workspace-name",
} as const;

export function KieSandboxDevDeploymentRequiredPatches(): ResourcePatch[] {
  return [
    {
      jsonPatches: [
        { op: "checkType", path: "/metadata/labels", type: "null" },
        { op: "add", path: "/metadata/labels", value: {} },
      ],
    },
    {
      jsonPatches: [
        {
          op: "add",
          path: `/metadata/labels/${encodeJsonPatchSubpath(requiredLabels.createdBy)}`,
          value: K8S_RESOURCE_CREATED_BY,
        },
        {
          op: "add",
          path: `/metadata/labels/${encodeJsonPatchSubpath(requiredLabels.partOf)}`,
          value: "${{ devDeployment.uniqueName }}",
        },
      ],
    },
    {
      jsonPatches: [
        { op: "checkType", path: "/metadata/annotations", type: "null" },
        { op: "add", path: "/metadata/annotations", value: {} },
      ],
    },
    {
      jsonPatches: [
        {
          op: "add",
          path: `/metadata/annotations/${encodeJsonPatchSubpath(requiredAnnotations.workspaceId)}`,
          value: "${{ devDeployment.workspace.id }}",
        },
        {
          op: "add",
          path: `/metadata/annotations/${encodeJsonPatchSubpath(requiredAnnotations.workspaceName)}`,
          value: "${{ devDeployment.workspace.name }}",
        },
      ],
    },
  ];
}
