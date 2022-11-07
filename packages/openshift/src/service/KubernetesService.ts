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

import { BuildDescriptor, BuildPhase, DeploymentCondition, DeploymentDescriptor, RouteDescriptor } from "../api/types";
import { ResourceFetcher } from "../fetch/ResourceFetcher";
import { OpenShiftDeploymentState } from "./types";

export class KubernetesService {
  constructor(private readonly args: { fetcher: ResourceFetcher; namespace: string }) {}

  public composeRouteUrl(route: RouteDescriptor): string {
    return `https://${route.spec.host}`;
  }

  public extractDeploymentState(args: {
    deployment?: DeploymentDescriptor;
    build?: BuildDescriptor;
  }): OpenShiftDeploymentState {
    if (args.build) {
      if (!args.build.status || (["Failed", "Error", "Cancelled"] as BuildPhase[]).includes(args.build.status.phase)) {
        return OpenShiftDeploymentState.DOWN;
      }

      if ((["New", "Pending"] as BuildPhase[]).includes(args.build.status.phase)) {
        return OpenShiftDeploymentState.PREPARING;
      }

      if (args.build.status.phase === "Running") {
        return OpenShiftDeploymentState.IN_PROGRESS;
      }

      // At this point, BuildPhase is `Complete`.
    }

    if (!args.deployment || !args.deployment.status) {
      // Deployment still being created
      return OpenShiftDeploymentState.IN_PROGRESS;
    }

    if (!args.deployment.status.replicas) {
      // Deployment with no replicas is down
      return OpenShiftDeploymentState.DOWN;
    }

    const progressingCondition = args.deployment.status.conditions?.find(
      (condition: DeploymentCondition) => condition.type === "Progressing"
    );

    if (!progressingCondition || progressingCondition.status !== "True") {
      // Without `Progressing` condition, the deployment will never be up
      return OpenShiftDeploymentState.DOWN;
    }

    if (!args.deployment.status.readyReplicas) {
      // Deployment is progressing but no replicas are ready yet
      return OpenShiftDeploymentState.IN_PROGRESS;
    }

    return OpenShiftDeploymentState.UP;
  }
}
