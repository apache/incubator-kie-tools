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

import { ResourcePatch } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";

export type DeploymentParameterType = "text" | "number" | "boolean";

export type DeploymentParameter = {
  id: string;
  name: string;
  description?: string;
  resourcePatches?: ResourcePatch[];
  appendYamls?: string[];
} & (
  | {
      defaultValue: string;
      type: "text";
    }
  | {
      defaultValue: number;
      type: "number";
    }
  | {
      defaultValue: boolean;
      type: "boolean";
      skipActionsIfFalse?: boolean;
    }
);

export function shouldSkipAction(parameter: DeploymentParameter, value: any) {
  // Non boolean type or configured to not skip actions if value is false shouldn't skip
  if (parameter.type !== "boolean" || !parameter.skipActionsIfFalse) {
    return false;
  }
  if (!value) {
    return true;
  }
  return false;
}

export type DeploymentOptionOpts = {
  parameters?: Record<string, DeploymentParameter>;
  resourcePatches?: ResourcePatch[];
  appendYamls?: string[];
};

export type DeploymentOption = {
  name: string;
  content: string;
} & DeploymentOptionOpts;
